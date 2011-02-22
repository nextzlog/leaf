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
package leaf.util.type;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import leaf.media.LeafAudioPlayer;

/**
*タイピング音を実現するマネージャです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月30日
*/

public class LeafTypingSoundManager extends KeyAdapter implements Runnable{
	
	private final LeafAudioPlayer player;
	private boolean isTyping;
	/**
	*マネージャを生成します。
	*/
	public LeafTypingSoundManager(){
		player = new LeafAudioPlayer();
		new Thread(this).start();
	}
	/**
	*タイピング音をファイルから読み込みます。
	*@param file 読み込むファイル
	*@return 読み込みに失敗するとfalse
	*/
	public boolean load(File file){
		try{
			player.load(file);
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	/**
	*キープレス時にタイピング音を再生します。
	*@param e キーイベント
	*/
	public void keyPressed(KeyEvent e){
		setTyping(true);
		if(!player.isPlaying()) player.start();
	}
	/**
	*タイピング中かどうか返します。
	*@return タイピング時はtrue
	*/
	private synchronized boolean isTyping(){
		return isTyping;
	}
	/**
	*タイピング中に設定します。
	*@param typing タイピング時はtrue
	*/
	private synchronized void setTyping(boolean typing){
		isTyping = typing;
	}
	/**
	*スレッドを利用してタイピング音を制御します。
	*/
	public void run(){
		try{
			while(true){
				if(player.getFramePosition()>=player.getFrameLength()) player.stop();
				Thread.sleep(100);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}