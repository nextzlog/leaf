/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/

package leaf.media;

import java.io.*;
import javax.sound.sampled.*;
import java.util.*;

/**
*Java Sound API を用いて簡単に音声ファイルを再生するクラスです。<br>
*「再生」「一時停止」「停止」ができ、ループのON/OFFの設定と再生を<br>
*別のメソッドに分けるなど、プレーヤとしてより自然な実装になっています。<br>
*対応するファイル形式は、WAVE、AU、AIFF、SNDです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2009年3月12日
*/
public class LeafAudioPlayer{
	
	/**秘匿フィールド*/
	private File file;
	private AudioInputStream stream;
	private AudioFormat format;
	private DataLine.Info datainfo;
	private Clip clip;
	private boolean looping = false;
	private int loopcount = 10;
	
	/**プレーヤを生成します。*/
	public LeafAudioPlayer(){}
	/**
	*指定された音声ファイルを再生するようにプレーヤを初期化します。
	*@param file 再生するファイル
	*/
	public void init(File file) throws LineUnavailableException,IllegalArgumentException,IllegalStateException,SecurityException,IOException,UnsupportedAudioFileException{
		try{
			if(file!=null){
				this.file = file;
				stream = AudioSystem.getAudioInputStream(file);
				format = stream.getFormat();
				
				datainfo = new DataLine.Info(Clip.class,format);
				clip = (Clip)AudioSystem.getLine(datainfo);
				clip.open(stream);
			}
		}catch(LineUnavailableException ex){
			throw ex;
		}catch(IllegalArgumentException ex){
			throw ex;
		}catch(IllegalStateException ex){
			throw ex;
		}catch(SecurityException ex){
			throw ex;
		}catch(IOException ex){
			throw ex;
		}catch(UnsupportedAudioFileException ex){
			throw ex;
		}
	}
	/**
	*プレーヤに読み込まれたファイルを返します。
	*@return ファイル
	*/
	public File getFile(){
		return file;
	}
	/**
	*音声の再生を開始します。
	*/
	public void start(){
		if(clip!=null){
			if(looping)clip.loop(loopcount);
			else clip.start();
		}
	}
	/**
	*音声の再生を一時停止します。
	*/
	public void pause(){
		if(clip!=null){
			clip.stop();
		}
	}
	/**
	*音声の再生を停止し、フレーム位置をリセットします。
	*/
	public void stop(){
		if(clip!=null){
			clip.stop();
			clip.setFramePosition(0);
		}
	}
	/**
	*音声ファイルの長さをマイクロ秒で返します。<br>
	*このメソッドの返り値は、クリップがnullの場合、-1になります。<br>
	*@return 音声の長さ
	*/
	public long getMicrosecondLength(){
		if(clip!=null)return clip.getMicrosecondLength();
		return -1;
	}
	/**
	*現在の再生位置をマイクロ秒で返します。<br>
	*クリップがnullの場合０を返します。
	*@return 現在の再生位置
	*/
	public long getMicrodecondPosition(){
		if(clip!=null)return clip.getMicrosecondPosition();
		return 0;
	}
	/**
	*音声ファイルのフレーム数を返します。<br>
	*このメソッドの返り値は、クリップがnullの場合、-1になります。<br>
	*@return フレーム数
	*/
	public int getFrameLength(){
		if(clip!=null)return clip.getFrameLength();
		return -1;
	}
	/**
	*現在の再生位置をフレーム数で返します。<br>
	*クリップがnullの場合、０を返します。
	*@return 現在の再生位置
	*/
	public int getFramePosition(){
		if(clip!=null)return (int)clip.getLongFramePosition();
		return 0;
	}
	/**
	*音声の再生位置をマイクロ秒で設定します。
	*@param microseconds 再生位置
	*/
	public void setMicrosecondPosition(long microseconds){
		if(clip!=null)clip.setMicrosecondPosition(microseconds);
	}
	/**
	*音声の再生位置をフレーム数で設定します。
	*@param frames 再生位置
	*/
	public void setFramePosition(int frames){
		if(clip!=null)clip.setFramePosition(frames);
	}
	/**
	*ループ再生時のループ開始位置と終了位置をフレーム数で設定します。
	*@param start ループ開始位置
	*@param end ループ終了位置
	*/
	public void setLoopPoints(int start,int end){
		if(clip!=null)clip.setLoopPoints(start,end);
	}
	/**
	*ループ再生モードを設定します。<br>
	*次回以降の再生でループ再生が適用されます。
	*また音声再生中にこのメソッドを実行した場合でも、自動でループされます。
	*@param mode ループする場合true
	*/
	public void setLoopMode(boolean mode){
		this.looping = mode;
		if(clip!=null&&clip.isRunning()){
			clip.loop(loopcount);
		}
	}
	/**
	*このプレーヤがループ再生モードになっているか返します。
	*@return ループモード時はtrue
	*/
	public boolean isLoopMode(){
		return looping;
	}
	/**
	*ループ再生の繰り返し回数を設定します。<br>
	*次回以降のループ再生で適用されます。
	*@param count ループする回数
	*/
	public void setLoopCount(int count){
		this.loopcount = count;
	}
	/**
	*このプレーヤが再生中かどうか返します。
	*@return 再生中の場合true
	*/
	public boolean isPlaying(){
		if(clip!=null)return clip.isRunning();
		return false;
	}
}
