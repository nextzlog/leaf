/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.net.server;

import java.io.IOException;
import java.util.EventListener;

/**
 * サーバーが受信したメッセージを受け取るリスナーです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年3月3日
 */
public interface ServerListener extends EventListener {
	/**
	 * サーバーにクライアントが接続した時に実行されます。
	 * 
	 * @param e 接続イベント
	 * @throws Exception リスナーが例外を投げる場合
	 */
	public void clientAccessed(ServerEvent e) throws IOException;
	
	/**
	 * サーバが受信したメッセージを受け取ります。
	 * 
	 * @param e 受信イベント
	 * @throws Exception リスナーが例外を投げる場合
	 */
	public void messageReceived(ServerEvent e)throws IOException;
}