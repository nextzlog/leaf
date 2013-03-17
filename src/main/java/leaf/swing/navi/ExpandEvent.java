/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.navi;

import java.util.EventObject;

/**
 * {@link LeafExpandPane}の展開状態の変更を表現するイベントクラスです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年7月10日
 *
 */
public class ExpandEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	/**
	 * イベントの発生元のオブジェクトを指定してイベントを構築します。
	 * 
	 * @param source
	 */
	public ExpandEvent(Object source) {
		super(source);
	}

}