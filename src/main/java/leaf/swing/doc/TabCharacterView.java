/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.doc;

import java.awt.*;
import javax.swing.text.*;

/**
 * 水平タブを可視化する{@link LabelView}の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf1.4 2012年12月28日
 */
public class TabCharacterView extends LabelView {
	private final Color color;
	
	/**
	 * {@link Element}を指定してビューを構築します。
	 * 
	 * @param elm 要素
	 */
	public TabCharacterView(Element elm) {
		super(elm);
		color = getForeground().brighter();
	}
	
	@Override
	public void paint(Graphics g,Shape a) {
		super.paint(g, a);
		Rectangle alloc = a.getBounds();
		FontMetrics met = g.getFontMetrics();
		String text = getText(getStartOffset(), getEndOffset()).toString();
		TabExpander expander = getTabExpander();
		int sumOfTabs = 0, length = text.length();
		for(int i = 0; i < length; i++) {
			if(text.charAt(i) != '\t') continue;
			int prevWidth = met.stringWidth(text.substring(0, i)) + sumOfTabs;
			int sx = alloc.x + prevWidth, sy = alloc.y;
			int tabWidth = (int) expander.nextTabStop((float)sx, i) - sx;
			g.setColor(color);
			g.drawLine(sx, sy+2, sx+2, sy);
			g.drawLine(sx+2, sy, sx+4, sy+2);
			sumOfTabs += tabWidth;
		}
	}
}