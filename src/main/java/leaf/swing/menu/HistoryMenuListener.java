/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.menu;

import java.util.EventListener;

/**
 *履歴が選択された時に呼び出される専用のリスナーです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年5月6日
 *@see LeafHistoryMenu
 */
public interface HistoryMenuListener extends EventListener{
	/**
	 *履歴が選択されたときに呼び出されます。
	 *@param e 通知内容を表すイベント
	 */
	public void historyClicked(HistoryMenuEvent e);
}