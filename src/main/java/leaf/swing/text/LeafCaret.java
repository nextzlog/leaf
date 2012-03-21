/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.text;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

/**
 *{@link LeafTextPane}及び{@link LeafTextArea}のキャレットを描画します。
 *
 *
 *@since Leaf 1.0 分離：2011年6月12日
 *@author 東大アマチュア無線クラブ
 */
final class LeafCaret extends DefaultCaret implements PropertyChangeListener{
	private JTextComponent comp;
	private LeafTextPane pane = null;
	private FontMetrics met;
	
	/**
	 *キャレットを生成します。
	 *
	 *@param comp キャレットを表示するテキストコンポーネント
	 */
	public LeafCaret(final JTextComponent comp){
		super();
		this.comp = comp;
		met = comp.getFontMetrics(comp.getFont());
		if(comp instanceof LeafTextPane) pane = (LeafTextPane)comp;
		
		comp.addPropertyChangeListener("font", this);
	}
	
	/**
	 *表示フォントサイズの更新に追随します。
	 *
	 *@param e 受信するイベント
	 */
	@Override public void propertyChange(PropertyChangeEvent e){
		met = comp.getFontMetrics(comp.getFont());
	}
	
	/**
	 *キャレットを描画します。置換動作時には続く文字に合わせて描画します。
	 *
	 *@param g キャレットを描画するのに用いられるグラフィックス
	 */
	@Override public void paint(Graphics g){
		if(isVisible()) try{
			Rectangle rect = comp.modelToView(getDot());
			g.setColor(comp.getCaretColor());
			g.setXORMode(comp.getBackground());
			
			if(pane != null && pane.getCaretMode()
			== LeafTextPane.CARET_REPLACE_MODE){
				char ch = pane.getText(getDot(), 1).charAt(0);
				int width = met.charWidth(ch) - 1;
				if(width > 0) g.fillRect(rect.x, rect.y, width, rect.height);
				else g.fillRect(rect.x, rect.y, 2, rect.height);
			}else g.fillRect(rect.x, rect.y, 2, rect.height);
		}catch(BadLocationException ex){}
	}
	
	/**
	*キャレットの描画領域を壊します。
	*@param rect キャレットの描画領域
	*/
	@Override protected synchronized void damage(Rectangle rect){
		if(rect!=null){
			super.x = 0;
			super.y = rect.y;
			super.width = comp.getSize().width;
			super.height= rect.height;
			comp.repaint();
		}
	}
}
