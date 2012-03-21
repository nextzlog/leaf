/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.shell;

import javax.swing.JButton;

/**
 *{@link Command}継承クラスが{@link JButton}を提供する場合に実装されます。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
public interface ButtonProvider {
	/**
	 *コマンドのボタンを構築します。
	 *
	 *@param button 構築するボタン
	 *@return ボタン
	 */
	public abstract JButton createButton(JButton button);
}
