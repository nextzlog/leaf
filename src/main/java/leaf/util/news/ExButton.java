/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.news;

import javax.swing.JButton;
import leaf.manager.LeafLocalizeManager;

/**
*ボーダーを描画しないボタンです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.3 作成：2011年5月4日
*/
final class ExButton extends JButton{
	private static final LeafLocalizeManager localize
	= LeafLocalizeManager.getInstance(ExButton.class);
	public ExButton(String key){
		super();
		setText(key);
		setBorderPainted(false);
		setFocusPainted(false);
		setFocusable(false);
		setRequestFocusEnabled(false);
	}
	public void setText(String key){
		super.setText(localize.translate(key));
	}
}