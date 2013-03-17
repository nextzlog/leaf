/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.com;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;

/**
 * 文字列が折り返して表示されるラベルです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.4 作成：2012年3月30日
 *
 */
public class LeafWrappedLabel extends JComponent {
	private static final long serialVersionUID = 1L;
	private GlyphVector glyph = null;
	private int prevwidth = -1;
	private String text;
	
	/**
	 * 何も表示されていないラベルを構築します。
	 */
	public LeafWrappedLabel(){
		this("");
	}
	
	/**
	 * デフォルトの表示文字列を指定してラベルを構築します。
	 *
	 * @param text 表示する文字列
	 */
	public LeafWrappedLabel(String text) {
		super();
		setUI(new WrappedLabelUI());
		setText(text);
	}
	
	/**
	 * ラベルに表示する文字列を設定します。
	 *
	 * @param text 表示する文字列
	 */
	public void setText(String text) {
		final String old = this.text;
		this.text = text;
		setupWrappedGlyphVector(getWidth());
		firePropertyChange("text", old, text);
	}
	
	/**
	 * ラベルに表示する文字列を返します。
	 *
	 * @return 表示する文字列
	 */
	public String getText() {
		return text;
	}
	
	private final class WrappedLabelUI extends ComponentUI{
		@Override
		public void installUI(JComponent c) {
			LookAndFeel.installColorsAndFont(c,
				"Label.background", "label.foreground", "Label.font");
			LookAndFeel.installProperty(c, "opaque", Boolean.FALSE);
		}
		
		@Override
		public void paint(Graphics g, JComponent comp) {
			int w = getWidth() - getInsets().left - getInsets().right;
			if(w != prevwidth) setupWrappedGlyphVector(w);
			prevwidth = w;
			
			Graphics2D g2 = (Graphics2D) g;
			if(glyph != null) {
				Insets i = getInsets();
				g2.drawGlyphVector(glyph, i.left, getFont().getSize() + i.top);
			}
			else super.paint(g, comp);
		}
		
		@Override
		public Dimension getPreferredSize(JComponent comp) {
			if(glyph == null) return null;
			Insets insets = getInsets();
			float x = (float) insets.left;
			float y = (float) (getFont().getSize() + insets.top);
			Dimension gsize = glyph.getPixelBounds(null, x, y).getSize();
			gsize.width  += insets.left + insets.right;
			gsize.height += insets.top + insets.bottom + getFont().getSize();
			return gsize;
		}
	}
	
	private void setupWrappedGlyphVector(int width) {
		if(width > 0) glyph = createWrappedGlyphVector(width, getFont());
		revalidate();
	}
	
	private GlyphVector createWrappedGlyphVector(float wrap, Font font) {
		FontRenderContext frc = getFontMetrics(font).getFontRenderContext();
		
		Point2D position = new Point2D.Double(0d, 0d);
		GlyphVector glyph = font.createGlyphVector(frc, text);
		
		float lineHeight = (float) glyph.getLogicalBounds().getHeight();
		float xpos = 0f, advance = 0f;
		
		int lineCount = 0;
		
		for(int i = 0; i < glyph.getNumGlyphs(); i++) {
			advance = glyph.getGlyphMetrics(i).getAdvance();
			char ch = text.charAt(glyph.getGlyphCharIndex(i));
			if(xpos < wrap && wrap <= xpos + advance || ch == '\n') {
				lineCount++;
				xpos = 0f;
			}
			
			position.setLocation(xpos, lineHeight * lineCount);
			glyph.setGlyphPosition(i, position);
			xpos += advance;
		}
		return glyph;
	}
}
