/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.net.protocol;

import java.io.IOException;

/**
 * クライアント側からサーバー側に送信されたコマンドの処理を定義します。
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年3月7日
 */
public abstract class LeafNetAgent {
	
	/**
	 * エージェントを生成します。
	 */
	public LeafNetAgent(){}
	
	/**
	 * エージェントの識別名を返します。
	 * 
	 * @param type エージェントのクラス
	 * @return 制御装置の固有識別名
	 */
	public static final String getName(Class<? extends LeafNetAgent> type){
		return type.getCanonicalName();
	}
	
	/**
	 * クライアントから受信したコマンドを処理します。
	 *
	 * @param e イベント
	 * @throws IOException 例外を発生するべき場合
	 */
	public abstract void messageReceived(LeafNetEvent e) throws IOException;
}