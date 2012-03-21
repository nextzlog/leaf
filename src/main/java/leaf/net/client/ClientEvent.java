/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.net.client;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.EventObject;

/**
*クライアントが受信したデータをラップするイベントです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.3 作成：2011年3月7日
*/
public class ClientEvent extends EventObject{
	
	private final String message;
	
	/**
	*イベントを生成します。
	*@param source  サーバー
	*@param message データの内容
	*/
	public ClientEvent(LeafNetClient source, String message){
		super(source);
		this.message = message;
	}
	/**
	*受信したデータの内容を返します。
	*@return データ
	*/
	public String getMessage(){
		return message;
	}
}