/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.document;

import java.awt.*;
import javax.swing.text.*;

/**
水平タブや改行の表示機能を実装するエディタキットです。<br>
Java Swing Tips てんぷらメモさんの公開するサンプルをベースにしています。
@author 東大アマチュア無線クラブ
@since Leaf 1.0 作成：2010年5月22日
*/
public class LeafEditorKit extends StyledEditorKit{
	
	public ViewFactory getViewFactory(){
		return new StyledViewFactory();
	}
	private class StyledViewFactory implements ViewFactory{
		
		public View create(Element elm){
			String kind = elm.getName();
			if(kind != null){
				if(kind.equals(AbstractDocument.ContentElementName)){
					return new LeafLabelView(elm);
				}else if(kind.equals(AbstractDocument.ParagraphElementName)){
					return new LeafParagraphView(elm);
				}else if(kind.equals(AbstractDocument.SectionElementName)){
					return new LeafNoWrapBoxView(elm,View.Y_AXIS);
				}else if(kind.equals(StyleConstants.ComponentElementName)){
					return new ComponentView(elm);
				}else if(kind.equals(StyleConstants.IconElementName)){
					return new IconView(elm);
				}
			}return new LeafLabelView(elm);
		}
	}
	/**改行の防止*/
	private  class LeafNoWrapBoxView extends BoxView{

		public LeafNoWrapBoxView(Element elm,int axis){
			super(elm,axis);
		}
		public void layout(int width,int height){
			super.layout(Integer.MAX_VALUE-64,height);
		}
	}
	/**改行の可視化*/
	private  class LeafParagraphView extends ParagraphView{
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
	private  class LeafLabelView extends LabelView{
		
		private final Color col = new Color(160,160,160);
		
		public LeafLabelView(Element elm){
			super(elm);
		}
		
		public void paint(Graphics g,Shape a){
			super.paint(g,a);
			Graphics2D g2 = (Graphics2D)g;
			Rectangle allocation = (a instanceof Rectangle)?(Rectangle)a:a.getBounds();
			FontMetrics metrics = g.getFontMetrics();
			int sumOfTabs = 0;
			String text = getText(getStartOffset(),getEndOffset()).toString();
			for(int i=0;i<text.length();i++){
				String s = text.substring(i,i+1);
				int previousStringWidth = metrics.stringWidth(text.substring(0,i))+sumOfTabs;
				int sx= allocation.x+previousStringWidth;
				int sy = allocation.y;
				if(s.equals("\t")){
					int tabWidth = (int)getTabExpander().nextTabStop((float)sx,i)-sx;
					g2.setColor(col);
					g2.drawLine(sx,sy+2,sx+2,sy);
					g2.drawLine(sx+2,sy,sx+4,sy+2);
					sumOfTabs+=tabWidth;
				}
			}
		}
	}
}
