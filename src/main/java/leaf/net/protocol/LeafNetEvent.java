/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.net.protocol;

import java.nio.channels.SocketChannel;
import java.util.EventObject;

import leaf.net.server.LeafNetServer;

/**
 * {@link LeafNetAgent} がクライアントからデータを受け取る時に渡されるイベントです。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since 2012年9月18日
 */
public final class LeafNetEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private final LeafNetServer server;
	private final LeafNetData data;
	private final SocketChannel socketChannel;
	
	/**
	 * イベントを構築します。
	 *
	 * @param source イベントが発生したサーバー
	 * @param channel クライアントとの接続を示すチャンネル
	 * @param data クライアントから送付されたデータ
	 */
	public LeafNetEvent
	(LeafNetServer source, SocketChannel channel, LeafNetData data) {
		super(source);
		this.server = source;
		this.data = data;
		this.socketChannel = channel;
	}
	
	/**
	 * このイベントが発生したサーバーを返します。
	 *
	 * @return イベントが発生したサーバー
	 */
	public LeafNetServer getServer() {
		return server;
	}
	
	/**
	 * クライアントとの接続を示すチャンネルを返します。
	 *
	 * @return クライアントとの接続を示すチャンネル
	 */
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
	
	/**
	 * クライアントから送付されたデータを返します。
	 *
	 * @return クライアントから送付されたデータ
	 */
	public LeafNetData getData() {
		return data;
	}

}