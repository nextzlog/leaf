/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.net.protocol;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 *LeafNetAPIのプロトコル制御装置の基底実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年3月7日
 */
public abstract class LeafNetAgent{
	/**
	 *エージェントを生成します。
	 */
	public LeafNetAgent(){}
	/**
	 *エージェントの識別名を返します。
	 *@return 制御装置の固有識別名
	 */
	public final String getName(){
		return getClass().getCanonicalName();
	}
	/**
	 *新規に接続を要求したクライアントを処理します。
	 *@param ch クライアントとのチャネル
	 *@return クライアントに返す値
	 *@throws IOException 例外を発生するべき場合
	 */
	public abstract Object clientAccessed
	(SocketChannel ch) throws IOException;
	/**
	 *クライアントから受信したデータを処理します。
	 *@param ch クライアントとのチャネル
	 *@param data 受信データ
	 *@return クライアントに返す値
	 *@throws IOException 例外を発生するべき場合
	 */
	public abstract  Object messageReceived
	(SocketChannel ch, LeafNetData data) throws IOException;
}