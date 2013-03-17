/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.navi;

import java.util.EventListener;

/**
 * {@link LeafExpandPane}の展開/折りたたみのイベントを受信するリスナーです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.0 作成：2010年7月10日
 * @see LeafTaskPane
 * @see LeafExpandPane
 */
public interface ExpandListener extends EventListener{
	/**
	 * 展開状態の変更時に呼び出されます。
	 * 
	 * @param e 変更内容を表すExpandEvent
	 */
	public void stateChanged(ExpandEvent e);
}