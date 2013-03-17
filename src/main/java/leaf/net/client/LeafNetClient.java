/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.net.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import leaf.net.protocol.LeafNetData;

/**
 * TCP/IPベースのクライアントの実装です。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年3月7日
 */
public class LeafNetClient{
	
	private SocketChannel channel;
	private InetSocketAddress address;
	private final ByteBuffer buffer;
	private final Charset  chset;
	
	/**
	 * IPアドレスとポート番号、文字セットを指定してクライアントを構築します。
	 * 
	 * @param inet  サーバーのIPアドレス
	 * @param port  ポート番号
	 * @param chset 文字セット
	 */
	public LeafNetClient(InetAddress inet, int port, Charset chset) {
		this.chset = chset;
		address = new InetSocketAddress(inet, port);
		buffer  = ByteBuffer.allocate(4096);
	}
	
	/**
	 * ホスト名とポート番号、文字セットを指定してクライアントを構築します。
	 *
	 * @param host  サーバー名
	 * @param port  ポート番号
	 * @param chset 文字セット
	 * @throws UnknownHostException ホストが見つからない場合
	 */
	public LeafNetClient(String host, int port, Charset chset)
	throws UnknownHostException {
		this(InetAddress.getByName(host), port, chset);
	}
	
	/**
	 * ホスト名を指定して51000番ポートでクライアントを構築します。
	 * 
	 * @param host ホスト名
	 */
	public LeafNetClient(String host) throws UnknownHostException {
		this(host, 51000, Charset.forName("UTF8"));
	}
	
	/**
	 * サーバーとの通信を構築します。
	 *
	 * @throws IOException 通信失敗時
	 */
	public void connect() throws IOException {
		channel = SocketChannel.open();
		channel.connect(address);
	}
	
	/**
	 * サーバーとの通信を切断します。
	 * 
	 * @throws IOException 切断失敗時
	 */
	public void disconnect() throws IOException {
		if(channel == null) throw new IOException();
		channel.close();
	}
	
	/**
	 * サーバーにデータを送信します。
	 * 
	 * @param data 送信データ
	 * @throws IOException 送信失敗時
	 */
	public void transmit(LeafNetData data) throws IOException {
		if(channel == null) throw new IOException();
		channel.write(chset.encode(CharBuffer.wrap(LeafNetData.encode(data))));
	}
	
	/**
	 * エージェントを指定してサーバーにオブジェクトを送信します。
	 * オブジェクトは内部で{@link LeafNetData} にラップされます。
	 * 
	 * @param agent エージェント識別名
	 * @param object 送信するオブジェクト
	 */
	public void transmit(String agent, Object object)
	throws IOException {
		transmit(new LeafNetData(agent, object));
	}
	
	/**
	 * サーバーからの返信データを受信します。
	 * データは自動でオブジェクトにデコードされます。
	 * 
	 * @return 受信データ
	 * @throws IOException 受信失敗時
	 */
	public Object receive() throws IOException {
		if(channel == null) throw new IOException();
		try {
			channel.read(buffer);
			buffer.flip();
			String xml = chset.decode(buffer).toString();
			return LeafNetData.decode(xml).getData();
		} finally {
			buffer.clear();
		}
	}
}