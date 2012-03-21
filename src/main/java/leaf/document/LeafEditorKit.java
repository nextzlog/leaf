/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.document;

import java.awt.*;
import javax.swing.text.*;

/**
*水平タブや改行の可視化機能を実装するキットです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2011年6月15日
*@see LeafSyntaxDocument
*@see leaf.swing.text.LeafTextPane
*/
public class LeafEditorKit extends StyledEditorKit{
	/**
	*キットを構築します。
	*/
	public LeafEditorKit(){
		super();
	}
	/**
	*ビューを作成するファクトリを返します。
	*@return ビューファクトリ
	*/
	@Override
	public ViewFactory getViewFactory(){
		return new LeafViewFactory();
	}
	private final class LeafViewFactory implements ViewFactory{
		public View create(Element elem){
			String kind = elem.getName();
			if(kind != null){
				if(kind.equals(AbstractDocument.ContentElementName)){
					return new LeafLabelView(elem);
				}else if(kind.equals(AbstractDocument.ParagraphElementName)){
					return new LeafParagraphView(elem);
				}else if(kind.equals(AbstractDocument.SectionElementName)){
					return new BoxView(elem,View.Y_AXIS);
				}else if(kind.equals(StyleConstants.ComponentElementName)){
					return new ComponentView(elem);
				}else if(kind.equals(StyleConstants.IconElementName)){
					return new IconView(elem);
				}
			}
			return new LeafLabelView(elem);
		}
	}
	/**行折り返しの禁止 : 2011年6月15日廃止:LeafTextPane内で十分*/
//	private final class LeafNoWrapBoxView extends BoxView{
//		public LeafNoWrapBoxView(Element elm,int axis){
//			super(elm,axis);
//		}
//		public void layout(int width,int height){
//			try{
//				super.layout(8192,height);
//			}catch(Exception ex){
//				super.layout(width,height);//2011年3月29日追加
//			}
//		}
//	}
	/**改行の可視化*/
	private final class LeafParagraphView extends ParagraphView{
		private final Color col = new Color(60,70,200);
		public LeafParagraphView(Element elm){
			super(elm);
		}
		public void paint(Graphics g,Shape allocation){
			super.paint(g,allocation);
			paintCustomParagraph(g,allocation);
		}
		private void paintCustomParagraph(Graphics g,Shape a){
			try{
				Shape p = modelToView(getEndOffset(),a,Position.Bias.Backward);
				Rectangle r = (p==null)?a.getBounds():p.getBounds();
				int x = r.x;
				int y = r.y;
				int h = r.height;
				int w = g.getFontMetrics().stringWidth("m");
				Color old = g.getColor();
				g.setColor(col);
				/*改行記号*/
				g.drawLine(x+3,   y+h/2, x+5,   y+h/2-2);
				g.drawLine(x+3,   y+h/2, x+5,   y+h/2+2);
				g.drawLine(x+3,   y+h/2, x+w+3, y+h/2);
				g.drawLine(x+w+3, y+h/4, x+w+3, y+h/2);
				g.setColor(old);
			}catch(Exception ex){ex.printStackTrace();}
		}
	}
	/**タブの可視化*/
	private final class LeafLabelView extends LabelView{
		private final Color col = new Color(160,160,160);
		public LeafLabelView(Element elm){
			super(elm);
		}
		public void paint(Graphics g,Shape a){
			super.paint(g,a);
			Rectangle alloc = a.getBounds();
			FontMetrics met = g.getFontMetrics();
			String text = getText(getStartOffset(),getEndOffset()).toString();
			int sumOfTabs = 0, length = text.length();
			for(int i=0;i<length;i++){
				char ch = text.charAt(i);
				int previousWidth = met.stringWidth(text.substring(0,i))+sumOfTabs;
				int sx = alloc.x + previousWidth, sy = alloc.y;
				if(ch == '\t'){
					int tabWidth = (int)getTabExpander().nextTabStop((float)sx,i)-sx;
					g.setColor(col);
					g.drawLine(sx, sy+2, sx+2, sy);
					g.drawLine(sx+2, sy, sx+4, sy+2);
					sumOfTabs += tabWidth;
				}
			}
		}
	}
}
