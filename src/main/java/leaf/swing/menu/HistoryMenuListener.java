/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.menu;

import java.util.EventListener;

/**
 * 履歴が選択された時に呼び出される専用のリスナーです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.0 作成：2010年5月6日
 * @see LeafHistoryMenu
 */
public interface HistoryMenuListener extends EventListener{
	/**
	 * 履歴が選択されたときに呼び出されます。
	 * 
	 * @param e 通知内容を表すイベント
	 */
	public void historyClicked(HistoryMenuEvent e);
}