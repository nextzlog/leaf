/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.net.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import leaf.net.protocol.LeafNetAgent;
import leaf.net.protocol.LeafNetData;
import leaf.net.protocol.LeafNetEvent;

/**
 * TCP/IPベースの常時待機型サーバーの実装クラスです。
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年3月3日
 */
public class LeafNetServer{
	
	private Selector selector;
	private ServerSocketChannel serverch;
	private final InetSocketAddress address;
	private final Charset  chset;
	private final int portNumber;
	private final ByteBuffer buffer;
	private final ArrayList<ServerListener> listeners;
	private final HashMap<String, LeafNetAgent>  agents;
	private final HashMap<String, SocketChannel> channels;
	
	/**
	 * ポート番号と文字セットを指定してサーバを構築します。
	 *
	 * @param port  ポート番号
	 * @param chset 文字セット
	 */
	public LeafNetServer(int port, Charset chset) {
		portNumber  = port ;
		this.chset  = chset;
		channels  = new HashMap<String, SocketChannel>();
		agents    = new HashMap<String, LeafNetAgent>();
		listeners = new ArrayList<ServerListener>();
		address = new InetSocketAddress(portNumber);
		buffer  = ByteBuffer.allocate(4096);
		addServerListener(new MessageProcessor());
	}
	
	/**
	 * 51000番ポートを指定してサーバーを構築します。
	 */
	public LeafNetServer() {
		this(51000, Charset.forName("UTF-8"));
	}
	
	private void connect() throws IOException {
		selector = Selector.open();
		serverch = ServerSocketChannel.open();
		serverch.configureBlocking(false);
		serverch.socket().bind(address);
		serverch.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	/**
	 * クライアントとの通信を例外が発生するまで常時待機します。
	 * 
	 * @throws IOException 通信時の例外(通信の強制切断等)
	 */
	public void accept() throws IOException {
		connect();
		try {
			while(true) communicate();
		}finally{
			serverch.close();
			selector.close();
		}
	}
	
	private void communicate() throws IOException {
		selector.select();//待機
		for(SelectionKey key : selector.selectedKeys()) {
			//受付処理
			if(key.isAcceptable()) {
				SocketChannel socketch = serverch.accept();
				if(socketch == null) continue;
				socketch.configureBlocking(false);
				socketch.register(selector, SelectionKey.OP_READ);
				processAccess(socketch);
				socketch = null;
			}
			//読み込み処理
			else if(key.isReadable()) {
				SocketChannel socketch = (SocketChannel)key.channel();
				try {
					switch(socketch.read(buffer)) {
					case 0 : continue;
					case -1: socketch.close();break;
					default:
						buffer.flip();
						processMessage(socketch, chset.decode(buffer));
					}
				} catch(IOException ex) {
					socketch.finishConnect();
					socketch.close();
				}finally{
					buffer.clear();
				}
			}
		}
	}
	
	private void processAccess(SocketChannel socketch) throws IOException {
		InetAddress inet = socketch.socket().getInetAddress();
		ServerEvent e  = new ServerEvent(this, socketch, inet);
		for(ServerListener listener : listeners) {
			try {
				listener.clientAccessed(e);
			} catch(Exception ex) {
				throw new IOException(ex);
			}
		}
	}
	
	private void processMessage(SocketChannel socketch, Object msg) throws IOException {
		ServerEvent e = new ServerEvent(this, socketch, msg);
		for(ServerListener listener : listeners) {
			try {
				listener.messageReceived(e);
			} catch(Exception ex) {
				throw new IOException(ex);
			}
		}
	}
	
	/**
	 * 指定されたチャネルにデータを送信します。
	 * データは自動で{@link LeafNetData}にラップされます。
	 *
	 * @param channel 送信先チャネル
	 * @param data 送信データ
	 * @throws IOException 送信に失敗した場合
	 */
	public void transmit(SocketChannel channel, Object data) throws IOException {
		String xml = LeafNetData.encode(new LeafNetData(data));
		channel.write(chset.encode(xml));
	}
	
	/**
	 * サーバーに接続する全てのクライアントにデータを送信します。
	 * データは自動で{@link LeafNetData}にラップされます。
	 *
	 * @param data 送信データ
	 * @return 送信の結果発生した例外 正常であればnullを返す
	 */
	public IOException[] transmit(Object data) {
		ArrayList<IOException> exs = new ArrayList<IOException>();
		for(SocketChannel channel : channels.values()) {
			try {
				transmit(channel, data);
			} catch(IOException ex) {
				exs.add(ex);
			}
		}
		IOException[] ret = exs.toArray(new IOException[0]);
		return (ret.length>0)? ret : null;
	}
	
	/**
	 * 受信メッセージを受け取るリスナーを登録します。
	 *
	 * @param listener リスナー
	 */
	public void addServerListener(ServerListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * 指定されたリスナーを削除してメッセージを受け取らないようにします。
	 *
	 * @param listener リスナー
	 */
	public void removeServerListener(ServerListener listener) {
		listeners.remove(listener);
	}
	
	private class MessageProcessor implements ServerListener {
		public void clientAccessed(ServerEvent e) throws IOException {
			addClient(e);
		}
		public void messageReceived(ServerEvent e)throws IOException {
			processCommand(e);
		}
	}
	
	private void addClient(ServerEvent e) throws IOException {
		String name = e.getInetAddress().toString();
		SocketChannel sch = e.getSocketChannel();
		channels.put(name, sch);
	}
	
	private void processCommand(ServerEvent e) throws IOException {
		LeafNetData   d = LeafNetData.decode(e.getMessage());
		LeafNetAgent  a = agents.get(d.getAgentName());
		SocketChannel c = e.getSocketChannel();
		if(a != null) a.messageReceived(new LeafNetEvent(this, c, d));
	}
	
	/**
	 * {@link LeafNetAgent}を追加します。
	 *
	 * @param agent 追加するエージェント
	 */
	public void addAgent(LeafNetAgent agent) {
		agents.put(LeafNetAgent.getName(agent.getClass()), agent);
	}
	/**
	 * {@link LeafNetAgent}を削除します。
	 *
	 * @param agent 削除するエージェント
	 */
	public void removeAgent(LeafNetAgent agent) {
		agents.remove(LeafNetAgent.getName(agent.getClass()));
	}
}