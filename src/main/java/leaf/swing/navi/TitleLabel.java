/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.navi;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * {@link LeafExpandPane}のタイトルラベルです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/02/17
 *
 */
final class TitleLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public TitleLabel(String text) {
		super(text);
	}
	
	public TitleLabel(Icon icon) {
		super(icon);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Color bgc = getBackground();
		g2.setPaint(new GradientPaint(
			0, 0, bgc.brighter(),
			0, getHeight(), bgc.darker(), true));
		g2.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

}