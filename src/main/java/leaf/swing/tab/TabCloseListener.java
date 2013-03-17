/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.tab;

import java.util.EventListener;

/**
 * {@link LeafTabbedPane}のタブが閉じられる時に呼び出されます。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年3月12日
 *
 */
public interface TabCloseListener extends EventListener {
	/**
	 * タブ項目の「閉じる」ボタンが押されたことを通知します。
	 * 
	 * @param e イベント
	 * @return 閉じても良い場合はtrue
	 */
	public boolean tabClosing(TabCloseEvent e);

}