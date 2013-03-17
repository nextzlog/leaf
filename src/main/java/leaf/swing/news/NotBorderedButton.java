/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.news;

import javax.swing.JButton;
import leaf.util.lang.LocalizeManager;

/**
 * ボーダーを描画しないボタンです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年5月4日
 */
@SuppressWarnings("serial")
final class NotBorderedButton extends JButton {
	private LocalizeManager localize =
	LocalizeManager.get(getClass());
	
	public NotBorderedButton(String key) {
		super();
		setText(key);
		setBorderPainted(false);
		setFocusPainted(false);
		setFocusable(false);
		setRequestFocusEnabled(false);
	}
	
	@Override
	public void setText(String key) {
		super.setText(localize.translate(key));
	}

}