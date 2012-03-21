/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing;

import java.awt.*;
import javax.swing.*;
import leaf.icon.LeafCornerIcon;

/**
*ステータスバーの実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年4月10日 改良：2011年5月5日
*/
public class LeafStatusBar extends JComponent{
	private final JLabel icon;
	private Component glue;
	/**
	*ステータスバーを生成します。
	*/
	public LeafStatusBar(){
		super();
		setPreferredSize(new Dimension(640, 24));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(glue = Box.createGlue());
		add(icon = new JLabel(new LeafCornerIcon()));
	}
	/**
	*ステータスバーの右端にコンポーネントを追加します。
	*@param comp 追加するコンポーネント
	*/
	public void addComp(Component comp){
		remove(icon);
		add(new JSeparator(JSeparator.VERTICAL));
		add(comp);
		add(icon);
	}
	/**
	*ステータスバーの左端にグル—を追加します。
	*@param comp 追加するグル—
	*/
	public void addGlue(Component comp){
		remove(glue);
		add(comp, 0);
		glue = comp;
	}
}