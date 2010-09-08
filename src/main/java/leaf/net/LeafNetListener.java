/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.net.client;

import java.util.*;

import leaf.net.client.*;

/**
*Leafのネットワーク通信機能のクライアント側のメッセージ受信用リスナーです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年6月5日
*/
public interface LeafNetListener extends EventListener{
	/**
	*{@link LeafNetClient}がメッセージを受信したときに呼び出されます。
	*@param e 引き渡されるメッセージイベント
	*/
	public void messageReceived(LeafNetEvent e);
	/**
	*{@link LeafNetClient}内で例外が発生した際に呼び出されます。
	*@param e 引き渡されるエラーメッセージ
	*/
	public void errorOccurred(LeafNetEvent e);
}
