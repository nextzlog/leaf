/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.net.server;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.EventObject;

/**
 * サーバーが受信したデータをラップするイベントです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年3月3日
 */
public class ServerEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private final SocketChannel channel;
	private final String message;
	
	/**
	 * イベントを生成します。
	 * 
	 * @param source  サーバー
	 * @param channel チャネル
	 * @param message データの内容
	 */
	public ServerEvent(LeafNetServer source,
		SocketChannel channel, Object message) {
		super(source);
		this.channel = channel;
		this.message = message.toString();
	}
	
	/**
	 * 送信元クライアントとのチャネルを返します。
	 * 
	 * @return チャネル
	 */
	public SocketChannel getSocketChannel() {
		return channel;
	}
	
	/**
	 * 送信元クライアントとのソケットを返します。
	 * 
	 * @return ソケット
	 */
	public Socket getSocket() {
		return channel.socket();
	}
	
	/**
	 * 送信元クライアントのIPアドレスを返します。
	 * 
	 * @return IPアドレス
	 */
	public InetAddress getInetAddress() {
		return channel.socket().getInetAddress();
	}
	
	/**
	 * 受信したデータの内容を返します。
	 * 
	 * @return データ
	 */
	public String getMessage() {
		return message;
	}

}