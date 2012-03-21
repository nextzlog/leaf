/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sound.sampled.*;

/**
*文字列をモールス符号に変換して音響再生します。
*
*@author 東大アマチュア無線クラブ
*@since  Leaf 1.2 作成：2011年2月12日
*/
public class LeafMorseGenerator{
	private static final double WAVE_ATTACK  = 0.005;
	private static final double WAVE_RELEASE =  0.01;
	private static final int BUFFER_SIZE  =   180;//seconds
	private static final int SAMPLE_RATE  = 8000;
	private static final int AMP_BIT = 8;
	
	private static HashMap<Character, boolean[]> map;
	
	static{
		map = new HashMap<Character, boolean[]>();
		map.put('A' ,  new boolean[]{false, true });
		map.put('B' ,  new boolean[]{true,  false, false, false});
		map.put('C' ,  new boolean[]{true,  false, true,  false});
		map.put('D' ,  new boolean[]{true,  false, false});
		map.put('E' ,  new boolean[]{false});
		map.put('F' ,  new boolean[]{false, false, true,  false});
		map.put('G' ,  new boolean[]{true,  true,  false});
		map.put('H' ,  new boolean[]{false, false, false, false});
		map.put('I' ,  new boolean[]{false, false});
		map.put('J' ,  new boolean[]{false, true,  true,  true });
		map.put('K' ,  new boolean[]{true,  false, true  });
		map.put('L' ,  new boolean[]{false, true,  false, false});
		map.put('M' ,  new boolean[]{true,  true });
		map.put('N' ,  new boolean[]{true,  false});
		map.put('O' ,  new boolean[]{true,  true,  true });
		map.put('P' ,  new boolean[]{false, true,  true, false});
		map.put('Q' ,  new boolean[]{true,  true,  false, true});
		map.put('R' ,  new boolean[]{false, true,  false});
		map.put('S' ,  new boolean[]{false, false, false});
		map.put('T' ,  new boolean[]{true });
		map.put('U' ,  new boolean[]{false, false, true });
		map.put('V' ,  new boolean[]{false, false, false, true });
		map.put('W' ,  new boolean[]{false, true,  true });
		map.put('X' ,  new boolean[]{true,  false, false, true });
		map.put('Y' ,  new boolean[]{true,  false, true,  true });
		map.put('Z' ,  new boolean[]{true,  true,  false, false});
		map.put('1' ,  new boolean[]{false, true,  true,  true,  true });
		map.put('2' ,  new boolean[]{false, false, true,  true,  true });
		map.put('3' ,  new boolean[]{false, false, false, true,  true });
		map.put('4' ,  new boolean[]{false, false, false, false, true });
		map.put('5' ,  new boolean[]{false, false, false, false, false});
		map.put('6' ,  new boolean[]{true,  false, false, false, false});
		map.put('7' ,  new boolean[]{true,  true,  false, false, false});
		map.put('8' ,  new boolean[]{true,  true,  true,  false, false});
		map.put('9' ,  new boolean[]{true,  true,  true,  true,  false});
		map.put('0' ,  new boolean[]{true,  true,  true,  true,  true });
		map.put('.' ,  new boolean[]{false, true,  false, true,  false, true });
		map.put(',' ,  new boolean[]{true,  true,  false, false, true,  true });
		map.put(':' ,  new boolean[]{true,  true,  true,  false, false, false});
		map.put('?' ,  new boolean[]{false, false, true,  true,  false, false});
		map.put('\'',  new boolean[]{false, true,  true,  true,  true,  false});
		map.put('-' ,  new boolean[]{true,  false, false, false, false, true });
		map.put('(' ,  new boolean[]{true,  false, true,  true,  false});
		map.put(')' ,  new boolean[]{true,  false, true,  true,  false, true });
		map.put('/' ,  new boolean[]{true,  false, false, true,  false});
		map.put('=' ,  new boolean[]{true,  false, false, false, true });
		map.put('+' ,  new boolean[]{false, true,  false, true,  false});
		map.put('\"',  new boolean[]{false, true,  false, false, true,  false});
		map.put('*' ,  new boolean[]{true,  false, false, true });
		map.put('@' ,  new boolean[]{false, true,  true,  false, true,  false});
	}
	
	private Clip clip;
	private final byte[] buffer;
	private final AudioFormat format;
	private int freq = 1000, wpm =20;
	private final LineListener listener;
	
	/**
	 *モールスジェネレータを生成します。
	 *@param freq トーン周波数
	 *@param wpm PARIS速度
	 *@throws IOException ラインがサポートされていない場合
	 */
	public LeafMorseGenerator(int freq, int wpm) throws IOException{
		format = new AudioFormat(SAMPLE_RATE, AMP_BIT, 1, true, true);
		this.freq = freq;
		this.wpm = wpm;
		listener = new LineHandler();
		buffer = new byte[BUFFER_SIZE * SAMPLE_RATE * (AMP_BIT/8)];
		Line.Info info = new DataLine.Info(Clip.class, format, buffer.length);
		if(!AudioSystem.isLineSupported(info)){
			throw new IOException(info + " is not supported");
		}
	}
	/**
	 *ラインの再生状態を監視し、再生終了時に待機を解除します。
	 */
	private class LineHandler implements LineListener{
		@Override public void update(LineEvent e){
			if(e.getType() == LineEvent.Type.STOP){
				synchronized(buffer){
					buffer.notify();
				}
			}
		}
	}
	/**
	 *ASCII文字列を読み込んで音響再生し、再生終了まで待機します。
	 *@param text 再生する文字列
	 *@throws LineUnavailableException ラインが使用不能な場合
	 */
	public void play(String text) throws LineUnavailableException{
		synchronized(buffer){
			int length = encode(text, buffer, wpm, freq);
			clip = AudioSystem.getClip();
			clip.addLineListener(listener);
			clip.open(format, buffer, 0, length);
			clip.start();
			try{
				buffer.wait();
			}catch(Exception ex){}
			clip.close();
		}
	}
	/**
	 *モールス符号の音響再生を停止します。
	 */
	public synchronized void stop(){
		if(clip != null){
			clip.stop();
			clip.close();
			clip = null;
		}
	}
	/**
	 *ASCII文字列をモールス符号の波形に変換します。
	 *@param text 再生する文字列
	 *@param buffer バッファー
	 *@param wpm PARIS速度
	 *@param freq 正弦波の周波数
	 *@return 波のサンプル総数
	 */
	private static int encode(String text, byte[] buffer, int wpm, int freq){
		final int length = text.length();
		int offset = 0;
		for(int i=0; i<length; i++){
			offset = encode(text.charAt(i), buffer, offset, wpm, freq);
		}
		return offset;
	}
	/**
	 *ASCII文字をモールス符号の波形に変換します。
	 *@param ch 再生する文字
	 *@param buffer バッファー
	 *@param offset 現在の位置
	 *@param wpm PARIS速度
	 *@param freq 正弦波の周波数
	 *@return 現在位置
	 */
	private static int encode(char ch, byte[] buffer, int offset, int wpm, int freq){
		final double dot = 60.0 / (wpm * 50.0);
		boolean[] code = LeafMorseGenerator.map.get(Character.toUpperCase(ch));
		if(code == null){
			return offset += mute(dot * 6.0, buffer, offset);
		}
		for(int i=0; i<code.length; i++){
			double dot_time = (code[i])? (3.0 * dot) : dot;
			offset += wave(dot_time, buffer, offset, freq);
			offset += (int)(SAMPLE_RATE * WAVE_RELEASE + 0.5);
			offset += mute(dot - WAVE_RELEASE, buffer, offset);
		}
		return offset += mute(dot * 2.0, buffer, offset);
	}
	/**
	 *無音の波形をバッファーに追加します。
	 *@param time 波形の長さ
	 *@param buffer バッファー
	 *@param offset 現在の位置
	 *@return 追加した長さ
	 */
	private static int mute(double time, byte[] buffer, int offset){
		int size = (int)(SAMPLE_RATE * time + 0.5);
		size = Math.min (buffer.length - offset, size);
		for(int i=0; i<size; i++) buffer[i+offset] = 0;
		return size;
	}
	/**
	 *正弦波の波形をバッファーに追加します。
	 *@param time 波形の長さ
	 *@param buffer バッファー
	 *@param offset 現在の位置
	 *@param freq 正弦波の周波数
	 *@return 追加した長さ
	 */
	private static int wave(double time, byte[] buffer, int offset, int freq){
		final double radian = Math.PI * 2 * freq / SAMPLE_RATE;
		final double amp_max = (1 << (AMP_BIT - 1)) - 1;
		
		final int sound = (int)(SAMPLE_RATE * time + 0.5);
		final int attack = (int)(SAMPLE_RATE * WAVE_ATTACK + 0.5);
		final int release = (int)(SAMPLE_RATE * WAVE_RELEASE + 0.5);
		final int total = sound + release;
		
		for(int i=0; i<total; i++){
			if(offset + i >= buffer.length){
				return (sound > i)? sound : i;
			}
			double ratio = 1.0;
			if(i < attack) ratio = ((double) i) / attack;
			if(i > sound ) ratio = ((double)(total - i)) / release;
			buffer[offset + i] = (byte)(ratio * Math.sin(radian * i) * amp_max);
		}
		return sound;
	}
}
