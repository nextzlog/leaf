/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.net.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

import leaf.net.protocol.LeafNetData;

/**
*LeafNetAPIで提供される全てのクライアントの基底クラスです。
*
*@author 東大アマチュア無線クラブ
*@since  Leaf 1.3 作成：2011年3月7日
*/
public class LeafNetClient{
	
	/*秘匿フィールド*/
	private SocketChannel channel;
	private InetSocketAddress address;
	private final ByteBuffer buffer;
	private final Charset  chset;
	private final ArrayList<ClientListener> listeners;
	
	/**
	*IPアドレスとポート番号、文字セットを指定してクライアントを構築します。
	*@param inet  サーバーのIPアドレス
	*@param port  ポート番号
	*@param chset 文字セット
	*/
	public LeafNetClient(InetAddress inet, int port, Charset chset){
		this.chset = chset;
		listeners = new ArrayList<ClientListener>();
		address = new InetSocketAddress(inet, port);
		buffer  = ByteBuffer.allocate(4096);
	}
	/**
	*ホスト名とポート番号、文字セットを指定してクライアントを構築します。
	*@param host  サーバー名
	*@param port  ポート番号
	*@param chset 文字セット
	*@throws UnknownHostException ホストが見つからない場合
	*/
	public LeafNetClient(String host, int port, Charset chset)
	throws UnknownHostException{
		
		this(InetAddress.getByName(host), port, chset);
	}
	/**
	*ホスト名を指定して51000番ポートでクライアントを構築します。
	*@param host ホスト名
	*/
	public LeafNetClient(String host) throws UnknownHostException{
		this(host, 51000, Charset.forName("UTF8"));
	}
	/**
	*サーバーとの通信を構築します。
	*@throws IOException 通信失敗時
	*/
	public void connect() throws IOException{
		channel = SocketChannel.open();
		channel.connect(address);
	}
	/**
	*サーバーとの通信を切断します。
	*@throws IOException 切断失敗時
	*/
	public void disconnect() throws IOException{
		if(channel==null)throw new IOException();
		channel.close();
	}
	/**
	*サーバーにデータを送信します。
	*@param data 送信データ
	*@throws IOException 送信失敗時
	*/
	public void transmit(LeafNetData data) throws IOException{
		if(channel==null)throw new IOException();
		channel.write(chset.encode(
			CharBuffer.wrap(LeafNetData.encode(data))
		));
	}
	/**
	*エージェントとコマンドを指定してサーバーにデータを送信します。
	*データは自動で{@link LeafNetData}にラップされます。
	*@param agent エージェント識別名
	*@param cmd   コマンド
	*@param data  添付データ
	*/
	public void transmit(String agent, String cmd, Object data)
	throws IOException{
		transmit(new LeafNetData(agent, cmd, data));
	}
	/**
	*サーバーからの返信データを受信します。
	*データは自動でオブジェクトにデコードされます。
	*@return 受信データ
	*@throws IOException 受信失敗時
	*/
	public Object receive() throws IOException{
		if(channel==null)throw new IOException();
		try{
			channel.read(buffer);
			buffer.flip();
			String xml = chset.decode(buffer).toString();
			return LeafNetData.decode(xml).getData();
		}finally{
			buffer.clear();
		}
	}
	/**
	*受信メッセージを受け取るリスナーを登録します。
	*@param listener リスナー
	*/
	public void addClientListener(ClientListener listener){
		listeners.add(listener);
	}
	/**
	*指定されたリスナーを削除してメッセージを受け取らないようにします。
	*@param listener リスナー
	*/
	public void removeClientListener(ClientListener listener){
		listeners.remove(listener);
	}
}