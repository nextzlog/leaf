/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.net.server;

import java.util.*;

import leaf.net.server.*;

/**
*Leafのネットワーク通信機能において、メンバーから送信されたメッセージを受信するリスナーです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月23日
*@see LeafNetMessageEvent
*/
public interface LeafNetMessageListener extends EventListener{

	/**
	*このメンバーが受信した{@link LeafNetMessageEvent}を処理します。<br>
	*このメソッドは、他のLeafNetMemberによってインタフェース{@link LeafNetMessageListener}を媒介して実行されます。
	*@param e 受信したコマンド付きメッセージのイベント
	*/
	public void processMessage(LeafNetMessageEvent e);
}
