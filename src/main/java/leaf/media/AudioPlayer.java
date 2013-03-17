/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.media;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 音声ファイルの簡易再生機能をアプリケーション向けに提供します。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.0 作成：2009年3月12日
 */
public class AudioPlayer{
	private AudioInputStream stream;
	private Clip clip;
	private boolean isLoopMode = false;
	private int LOOP_COUNT = 100;
	
	/**
	 * プレーヤを生成します。
	 */
	public AudioPlayer(){}
	
	/**
	 * 指定された音声ファイルでプレーヤを初期化します。
	 * 
	 * @param file 音声ファイル
	 * @throws IOException 読み込みに失敗した場合
	 */
	public synchronized void load(File file) throws IOException{
		close();
		if(file == null) return;
		try{
			stream = AudioSystem.getAudioInputStream(file);
			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(stream);
		}catch(LineUnavailableException ex){
			throw new IOException(ex);
		}catch(UnsupportedAudioFileException ex){
			throw new IOException(ex);
		}
	}
	
	/**
	 * ストリームを閉じリソースを解放します。
	 */
	public synchronized void close() throws IOException{
		if(clip != null){
			clip.close();
			clip = null;
		}if(stream != null){
			stream.close();
			stream = null;
		}
	}
	
	/**
	 * 音声の再生を開始します。
	 * 
	 * @throws IllegalStateException 初期化されていない場合
	 */
	public synchronized void start()
	throws IllegalStateException{
		if(clip != null){
			if(isLoopMode) clip.loop(LOOP_COUNT);
			else clip.start();
		}else throw new IllegalStateException();
	}
	
	/**
	 * 音声の再生を一時停止します。
	 * 
	 * @throws IllegalStateException 初期化されていない場合
	 */
	public synchronized void pause()
	throws IllegalStateException{
		if(clip!=null) clip.stop();
		else throw new IllegalStateException();
	}
	
	/**
	 * 音声の再生を停止し、フレーム位置をリセットします。
	 * 
	 * @throws IllegalStateException 初期化されていない場合
	 */
	public synchronized void stop()
	throws IllegalStateException{
		if(clip!=null){
			clip.stop();
			clip.setFramePosition(0);
		}else throw new IllegalStateException();
	}
	
	/**
	 * 音声ファイルのフレーム数を返します。
	 * 
	 * @throws IllegalStateException 初期化されていない場合
	 */
	public synchronized int getFrameLength()
	throws IllegalStateException{
		if(clip != null) return clip.getFrameLength();
		throw new IllegalStateException();
	}
	
	/**
	 * 現在の再生位置をフレーム数で返します。
	 * 
	 * @throws IllegalStateException 初期化されていない場合
	 */
	public synchronized int getFramePosition()
	throws IllegalStateException{
		if(clip != null) return (int)clip.getLongFramePosition();
		throw new IllegalStateException();
	}
	
	/**
	 * 音声の再生位置をフレーム数で設定します。
	 * 
	 * @param frames 再生位置
	 * @throws IllegalStateException 初期化されていない場合
	 */
	public synchronized void setFramePosition(int frames)
	throws IllegalStateException{
		if(clip != null) clip.setFramePosition(frames);
		throw new IllegalStateException();
	}
	
	/**
	 * ループ再生モードを設定します。
	 * 
	 * @param mode ループする場合true
	 */
	public synchronized void setLoopMode(boolean mode){
		this.isLoopMode = mode;
		if(clip!=null&&clip.isRunning()){
			clip.loop(LOOP_COUNT);
		}
	}
	
	/**
	 * このプレーヤがループ再生モードになっているか返します。
	 * 
	 * @return ループモード時はtrue
	 */
	public synchronized boolean isLoopMode(){
		return isLoopMode;
	}
	
	/**
	 * このプレーヤが再生中かどうか返します。
	 * 
	 * @return 再生中の場合true
	 */
	public synchronized boolean isPlaying(){
		if(clip!=null)return clip.isRunning();
		return false;
	}
	
	/**
	 * プレーヤの再生音量を設定します。
	 * 
	 * @param vol 100段階での音量
	 * @throws IllegalArgumentException 入力された値が不正の場合
	 * @throws IllegalStateException 初期化されていない場合
	 */
	public synchronized void setVolume(int vol)
	throws IllegalArgumentException, IllegalStateException{
		if(clip != null){
			FloatControl cont = (FloatControl)
			clip.getControl(FloatControl.Type.MASTER_GAIN);
			cont.setValue((float)Math.log10(vol)/2);
		}else throw new IllegalStateException();
	}
	
	/**
	 * プレーヤの再生音量を返します。
	 * 
	 * @return 100段階での音量
	 * @throws IllegalStateException 初期化されていない場合
	 */
	public synchronized int getVolume()
	throws IllegalStateException{
		if(clip != null){
			FloatControl cont = (FloatControl)
			clip.getControl(FloatControl.Type.MASTER_GAIN);
			return (int)Math.pow(10,(cont.getValue()*2));
		}else throw new IllegalStateException();
	}
}