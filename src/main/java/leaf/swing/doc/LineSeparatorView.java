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
 * 改行文字を可視化する{@link ParagraphView}の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf1.4 2012年12月28日
 */
public class LineSeparatorView extends ParagraphView {
	private Color color;
	
	/**
	 * {@link Element}を指定してビューを構築します。
	 * 
	 * @param elm 要素
	 */
	public LineSeparatorView(Element elm) {
		super(elm);
		color = Color.GRAY;
	}
	
	@Override
	public void paint(Graphics g, Shape a) {
		super.paint(g, a);
		try {
			paintLineFeedAndCarriageReturn(g, a);
		} catch(BadLocationException ex) {
			ex.printStackTrace();
		}
	}
	
	private void paintLineFeedAndCarriageReturn(Graphics g, Shape a)
	throws BadLocationException {
		final int offset = getEndOffset();
		Shape p = modelToView(offset, a, Position.Bias.Backward);
		Rectangle r = p == null ? a.getBounds() : p.getBounds();
		int h = r.height;
		int w = g.getFontMetrics().stringWidth("m");
		Color old = g.getColor();
		g.setColor(color);
		g.drawLine(r.x+3,   r.y+h/2, r.x+5,   r.y+h/2-2);
		g.drawLine(r.x+3,   r.y+h/2, r.x+5,   r.y+h/2+2);
		g.drawLine(r.x+3,   r.y+h/2, r.x+w+3, r.y+h/2  );
		g.drawLine(r.x+w+3, r.y+h/4, r.x+w+3, r.y+h/2  );
		g.setColor(old);
	}
}