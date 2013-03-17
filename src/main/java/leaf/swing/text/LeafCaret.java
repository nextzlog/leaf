/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.text;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

/**
 * {@link LeafTextPane}及び{@link LeafTextArea}のキャレットを描画します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2011年6月12日
 *
 */
final class LeafCaret extends DefaultCaret implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private JTextComponent comp;
	private LeafTextPane pane = null;
	private FontMetrics met;
	private Color caretColor, background;
	
	/**
	 * キャレットを生成します。
	 * 
	 * @param comp キャレットを表示するテキストコンポーネント
	 */
	public LeafCaret(final JTextComponent comp) {
		super();
		this.comp = comp;
		met = comp.getFontMetrics(comp.getFont());
		caretColor = getOpaqueColor(comp.getCaretColor());
		background = getOpaqueColor(comp.getBackground());
		
		if(comp instanceof LeafTextPane) pane = (LeafTextPane)comp;
		
		comp.addPropertyChangeListener("font", this);
		comp.addPropertyChangeListener("caretColor", this);
		comp.addPropertyChangeListener("background", this);
	}
	
	/**
	 * 表示フォントサイズの更新に追随します。
	 * 
	 * @param e 受信するイベント
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		met = comp.getFontMetrics(comp.getFont());
		caretColor = getOpaqueColor(comp.getCaretColor());
		background = getOpaqueColor(comp.getBackground());
	}
	
	/**
	 * キャレットを描画します。置換動作時には続く文字に合わせて描画します。
	 * 
	 * @param g キャレットを描画するグラフィックス
	 */
	@Override
	public void paint(Graphics g) {
		if(isVisible()) try {
			Rectangle rect = comp.modelToView(getDot());
			g.setColor(caretColor);
			g.setXORMode(background);
			if(pane != null) paintLeafTextPaneCaret(g, rect);
			else g.fillRect(rect.x, rect.y, 2, rect.height);
		} catch(BadLocationException ex) {}
	}
	
	/**
	 * {@link LeafTextPane}のキャレットを描画します。
	 * 
	 * @param g キャレットを描画するグラフィックス
	 * @param rect キャレットを描画する位置
	 */
	private void paintLeafTextPaneCaret(Graphics g, Rectangle rect) 
	throws BadLocationException {
		if(pane.getCaretMode() == LeafTextPane.CARET_REPLACE_MODE) {
			char ch = pane.getText(getDot(), 1).charAt(0);
			int width = met.charWidth(ch) - 1;
			if(width > 0) g.fillRect(rect.x, rect.y, width, rect.height);
			else g.fillRect(rect.x, rect.y, 2, rect.height);
		} else g.fillRect(rect.x, rect.y, 2, rect.height);
	}
	
	/**
	 * 不透明色に変換した色を返します。
	 * 
	 * @param color 色
	 * @return 変換した色
	 */
	private Color getOpaqueColor(Color color) {
		if(color == null) return color;
		final int r = color.getRed();
		final int g = color.getGreen();
		final int b = color.getBlue();
		return new Color(r, g, b);
	}
	
	/**
	 * キャレットの描画領域を壊します。
	 * 
	 * @param rect キャレットの描画領域
	 */
	@Override
	protected synchronized void damage(Rectangle rect) {
		if(rect != null) {
			super.x = 0;
			super.y = rect.y;
			super.width = comp.getSize().width;
			super.height= rect.height;
			comp.repaint();
		}
	}

}
