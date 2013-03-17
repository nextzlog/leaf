/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.shell;

import javax.swing.JMenuItem;

/**
 * {@link Command}継承クラスが{@link JMenuItem}を提供する場合に実装されます。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年12月11日
 */
public interface MenuItemProvider {
	/**
	 * コマンドのメニューアイテムを構築します。
	 * 
	 * @param item 構築するメニューアイテム
	 * @return メニューアイテム
	 */
	public abstract JMenuItem createMenuItem(JMenuItem item);
}
