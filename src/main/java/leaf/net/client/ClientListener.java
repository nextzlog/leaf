/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.net.client;

import java.util.EventListener;

/**
*クライアントが受信したメッセージを受け取るリスナーです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.3 作成：2011年3月7日
*/
public interface ClientListener extends EventListener{
	
	/**
	*クライアントが受信したメッセージを受け取ります。
	*@param e 受信イベント
	*@throws Exception リスナーが例外を投げる場合
	*/
	public void messageReceived(ClientEvent e)throws Exception;
}