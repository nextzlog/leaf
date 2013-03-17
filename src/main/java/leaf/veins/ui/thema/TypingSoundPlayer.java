/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.thema;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import leaf.media.AudioPlayer;

/**
 *タイピング音を再生するプレーヤです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 */
public class TypingSoundPlayer extends KeyAdapter {
	private final AudioPlayer player;
	private boolean isEnabled = false;
	
	/**
	 *プレーヤを構築します。
	 *
	 */
	public TypingSoundPlayer(){
		player = new AudioPlayer();
	}
	
	/**
	 *音声データをファイルから読み込みます。
	 *
	 *
	 *@param file 音声ファイル
	 *@throws IOException 読み込みに失敗した場合
	 */
	public void load(File file) throws IOException {
		if(isEnabled = (file != null)){
			player.load(file);
			Thread thread = new Thread(new ControleThread());
			thread.setDaemon(true);
			thread.start();
		}
	}
	
	@Override public void keyTyped(KeyEvent e){
		if(isEnabled && !player.isPlaying()) player.start();
	}
	
	private class ControleThread implements Runnable{
		final long length = player.getFrameLength();
		@Override public void run() {
			while(isEnabled){
				if(player.getFramePosition() >= length) player.stop();
				try{
					Thread.sleep(100);
				}catch(InterruptedException ex){}
			}
		}
	}
}
