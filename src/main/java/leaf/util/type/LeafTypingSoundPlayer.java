/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.type;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import leaf.media.LeafAudioPlayer;

/**
 *タイプ音を再生するプレーヤです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年9月30日
 */
public class LeafTypingSoundPlayer extends KeyAdapter{
	private final LeafAudioPlayer player;
	private boolean isEnabled = false;
	/**
	 *プレーヤを生成します。
	 */
	public LeafTypingSoundPlayer(){
		player = new LeafAudioPlayer();
	}
	/**
	 *タイプ音をファイルから読み込みます。
	 *@param file 音声ファイル
	 *@return 読み込みに失敗した場合false
	 */
	public boolean load(File file){
		if(!(isEnabled=(file!=null))) return true;
		try{
			player.load(file);
			Thread th = new Thread(new ControleThread());
			th.setDaemon(true);
			th.start();
			return true;
		}catch(IOException ex){
			return false;
		}
	}
	/**
	 *キーを入力している時にタイプ音を再生します。
	 *@param e キーイベント
	 */
	public void keyTyped(KeyEvent e){
		if(isEnabled && !player.isPlaying()) player.start();
	}
	/**
	 *スレッドを利用してタイプ音を制御します。
	 */
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