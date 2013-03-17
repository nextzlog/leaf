/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.main;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.*;
import leaf.icon.CornerIcon;

/**
 * ステータスバーの実装です。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/02/09 
 *
 */
@SuppressWarnings("serial")
public class StatusBar extends JComponent {
	private final JLabel icon;
	private Component glue;
	
	/**
	 * ステータスバーを構築します。
	 */
	public StatusBar() {
		setPreferredSize(new Dimension(640, 24));
		setLayout(new StatusBarLayout());
		add(glue = (JComponent) Box.createGlue());
		add(icon = new JLabel(new CornerIcon()));
	}
	
	/**
	 * ステータスバーの右端にコンポーネントを追加します。
	 * 
	 * @param comp 追加するコンポーネント
	 */
	public void addComp(Component comp) {
		remove(icon);
		add(new JSeparator(JSeparator.VERTICAL));
		add(comp);
		add(icon);
	}
	
	/**
	 * ステータスバーの左端にグル—を追加します。
	 * 
	 * @param comp 追加するグル—
	 */
	public void setGlue(Component comp) {
		remove(glue);
		add(comp, 0);
		glue = comp;
	}
	
	private class StatusBarLayout extends BoxLayout {
		public StatusBarLayout() {
			super(StatusBar.this, BoxLayout.X_AXIS);
		}
		
		@Override
		public Dimension maximumLayoutSize(Container target) {
			return target.getMaximumSize();
		}
	}

}