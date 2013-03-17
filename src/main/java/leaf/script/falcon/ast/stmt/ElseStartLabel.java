/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

/**
 * 「ELSE_START」に対応する匿名ラベルです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/24
 *
 */
public final class ElseStartLabel extends Label {
	/**
	 * ラベルを構築します。
	 */
	public ElseStartLabel() {
		super(LabelName.ELSE_START);
	}

}
