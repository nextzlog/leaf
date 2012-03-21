/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.taskpane;

import java.util.EventListener;

/**
 *{@link LeafExpandPane}の展開/折りたたみのイベントを受信するリスナーです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年7月10日
 *@see LeafTaskPane
 *@see LeafExpandPane
 */
public interface ExpandListener extends EventListener{
	/**
	 *展開状態の変更時に呼び出されます。
	 *@param e 変更内容を表すExpandEvent
	 */
	public void stateChanged(ExpandEvent e);
}