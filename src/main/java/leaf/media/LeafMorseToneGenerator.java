/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.media;

import javax.sound.sampled.*;
import java.io.IOException;

/**
*モールス符号のトーンを再生するプレーヤです。
*モールス符号のデータにはバイト列を利用します。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年2月12日
*/
public class LeafMorseToneGenerator{
	
	/**バイト列で長点を指定します。*/
	public static final byte LONG_DASH = 1;
	
	/**バイト列で短点を指定します。*/
	public static final byte SHORT_DOT = -1;
	
	/**バイト列で空白を指定します。*/
	public static final byte WHITESPACE = 0;
	
	private final AudioFormat format;
	private final SourceDataLine line;
	
	private final int SAMPLING_RATE = 11025;
	private final int BUFFER_SIZE = SAMPLING_RATE / 10;
	
	private byte[] buffer = new byte[BUFFER_SIZE];
	private int frequency = 1000;
	private int period;
	
	private int speed = 250, index;
	
	/**
	*プレーヤを生成します。
	*@throws IOException ラインがサポートされていない場合
	*@throws LineUnavailableException ラインが使用不可能の場合
	*/
	public LeafMorseToneGenerator() throws IOException, LineUnavailableException{
		format = new AudioFormat(SAMPLING_RATE, 8, 1, true, true);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, BUFFER_SIZE);
		if(!AudioSystem.isLineSupported(info)){
			throw new IOException("DataLine " + info + "is not supported");
		}
		setToneFrequency(frequency);
		(line = (SourceDataLine)AudioSystem.getLine(info)).open();
	}
	/**
	*波形データを生成して格納します。
	*@return データの有効長
	*/
	private int createToneData(){
		int length = (buffer.length > period)? period : buffer.length;
		for(int i=0;i<length;i++){
			if(((float)i / period) > (i / period) + 0.5){
				buffer[i] = (byte)(120);
			}else{
				buffer[i] = (byte)(-120);
			}
		}
		return length;
	}
	/**
	*空の波形データを生成して格納します。
	*@return データの有効長
	*/
	private int createNullToneData(){
		int length = (buffer.length > period)? period : buffer.length;
		for(int i=0;i<length;i++){
			buffer[i] = (byte)0;
		}
		return length;
	}
	/**
	*トーンを再生するスレッドです。
	*/
	private class ExToneRunnable implements Runnable{
		private final byte[] codes;
		/**
		*スレッドを生成します。
		*/
		public ExToneRunnable(byte[] codes){
			this.codes = codes;
		}
		public void run(){
			while(index < codes.length){
				switch(codes[index]){
				case WHITESPACE:
					line.write(buffer, 0, createNullToneData());
					break;
				default:
					line.write(buffer, 0, createToneData());
				}
			}
		}
	}
	/**
	*トーンの周波数を設定します。
	*@param freq 周波数
	*/
	public void setToneFrequency(int freq){
		this.frequency = freq;
		period = SAMPLING_RATE / freq;
	}
	/**
	*トーンの周波数を返します。
	*@return 周波数
	*/
	public int getToneFrequency(){
		return frequency;
	}
	/**
	*PARIS速度を設定します。
	*@param wpm ワード(短点50個)/分
	*/
	public void setSpeed(int wpm){
		this.speed = wpm * 50;
	}
	/**
	*PARIS速度を返します。
	*@return ワード(短点50個)/分
	*/
	public int getSpeed(){
		return speed / 50;
	}
	/**
	*指定されたモールスバイト列をこのスレッド上で再生します。
	*@param codes モールス符号列
	*/
	public void play(byte[] codes){
		line.start();
		new Thread(new ExToneRunnable(codes)).start();
		for(index=0;index<codes.length;index++){
			int length = (codes[index] == LONG_DASH)? 3 : 1;
			for(int i=0;i<length;i++){
				try{
					Thread.sleep(60000/speed);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		line.stop();
	}
	/**
	*プレーヤを破棄してリソースを解放します。
	*/
	public void close(){
		line.close();
	}
}
