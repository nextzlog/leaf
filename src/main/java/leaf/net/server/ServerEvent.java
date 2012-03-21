/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.net.server;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.EventObject;

/**
*サーバーが受信したデータをラップするイベントです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.3 作成：2011年3月3日
*/
public class ServerEvent extends EventObject{
	
	private final SocketChannel channel;
	private final String message;
	
	/**
	*イベントを生成します。
	*@param source  サーバー
	*@param channel チャネル
	*@param message データの内容
	*/
	public ServerEvent(LeafNetServer source,
		SocketChannel channel, Object message){
			
		super(source);
		this.channel = channel;
		this.message = message.toString();
	}
	/**
	*送信元クライアントとのチャネルを返します。
	*@return チャネル
	*/
	public SocketChannel getSocketChannel(){
		return channel;
	}
	/**
	*送信元クライアントとのソケットを返します。
	*@return ソケット
	*/
	public Socket getSocket(){
		return channel.socket();
	}
	/**
	*送信元クライアントのIPアドレスを返します。
	*@return IPアドレス
	*/
	public InetAddress getInetAddress(){
		return channel.socket().getInetAddress();
	}
	/**
	*受信したデータの内容を返します。
	*@return データ
	*/
	public String getMessage(){
		return message;
	}
}