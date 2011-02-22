/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.net.server;

import java.util.*;

import leaf.net.server.*;

/**
*Leafのネットワーク通信機能でメンバーから送信されたメッセージを受信するリスナーです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月23日
*@see LeafNetMessageEvent
*/
public interface LeafNetMessageListener extends EventListener{

	/**
	*このメンバーが受信した{@link LeafNetMessageEvent}を処理します。
	*@param e 受信したコマンド付きメッセージのイベント
	*/
	public void processMessage(LeafNetMessageEvent e);
}
