/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components.text;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

/**
*行番号と桁ルーラを表示するテキストコンポーネント用スクロール領域です。
*<br>行番号は、マウスでドラッグすることで複数行選択できます。
*<br>桁ルーラはキャレットの移動に合わせてポインタが水平方向に移動します。
*<br>このコンポーネントはJTextComponentの全ての継承クラスに対応していますが、
*<br>設計上全ての文字が同一のフォントで表示されることを想定しているため、
*<br>マルチフォントのテキストには対応していません。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*/
public class LeafTextScrollPane extends JScrollPane
	implements CaretListener,DocumentListener{
	/**フィールド*/
	private LeafLineNumberPane lineNumber;
	private LeafColumnRulerPane ruler;
	private boolean lnvisible = true,crvisible = true;
	/**
	*テキストコンポーネントを指定してスクロール領域を生成します。
	*@param comp ビューポートに表示するJTextComponent
	*/
	public LeafTextScrollPane(JTextComponent comp){
		this(comp,true,true);
	}
	/**
	*テキストコンポーネントと行番号・桁ルーラの可視を指定してスクロール領域を生成します。
	*@param comp ビューポートに表示するJTextComponent
	*@param lnvisible 行番号を表示する場合true
	*@param crvisible 桁ルーラを表示する場合true
	*/
	public LeafTextScrollPane(JTextComponent comp,boolean lnvisible,boolean crvisible){
		super(comp);
		this.lnvisible = lnvisible;
		this.crvisible = crvisible;
		init(comp);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		/**四隅の不透明化*/
		setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER,new JPanel());
		setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER,new JPanel());
		setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,new JPanel());
		setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,new JPanel());
	}
	/**
	*このスクロール領域を初期化します。<br>
	*テキストコンポーネントのフォントサイズ変更時には、必ず実行してください。
	*@param comp ビューポートに表示するJTextComponent
	*/
	public void init(JTextComponent comp){
		comp.addCaretListener(this);
		comp.getDocument().addDocumentListener(this);
		lineNumber = (isLineNumberVisible())? new LeafLineNumberPane(comp):null;
		setRowHeaderView(lineNumber);
		ruler = (isColumnRulerVisible())? new LeafColumnRulerPane(comp):null;
		setColumnHeaderView(ruler);
	}
	/**
	*このスクロール領域に、行番号の速やかな再描画を要求します。<br>
	*ただし再描画の優先順位は仮想マシンによって決められるため、
	*迅速に再描画が行われるかどうかは保証されません。
	*/
	public void repaintLineNumber(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if(lineNumber!=null)lineNumber.repaint();
				if(ruler!=null)ruler.repaint();
			}
		});
	}
	public void caretUpdate(CaretEvent e){
		if(ruler!=null)ruler.repaint();
	}
	public void changedUpdate(DocumentEvent e){}
	public void insertUpdate(DocumentEvent e){
		repaintLineNumber();
	}
	public void removeUpdate(DocumentEvent e){
		repaintLineNumber();
	}
	/**
	*現在キャレットのある行の行番号を返します。
	*行番号は１以上の整数値として得られます。
	*@return 現在の行番号
	*/
	public int getCurrentLineNumber(){
		return lineNumber.getCurrentLineNumber();
	}
	/**
	*現在キャレットのある桁の桁番号を返します。
	*桁番号は１以上の整数値として得られます。
	*@return 現在の桁番号
	*/
	public int getCurrentColumnNumber(){
		return ruler.getCurrentColumnNumber();
	}
	/**
	*行番号の可視を設定します。
	*直後に{@link #init(JTextComponent)}を実行すると表示上反映されます。
	*/
	public void setLineNumberVisible(boolean visible){
		this.lnvisible = visible;
	}
	/**
	*行番号の可視を返します。
	*@return 行番号が表示される場合true
	*/
	public boolean isLineNumberVisible(){
		return this.lnvisible;
	}
	/**
	*桁ルーラの可視を設定します。
	*直後に{@link #init(JTextComponent)}を実行すると表示上反映されます。
	*/
	public void setColumnRulerVisible(boolean visible){
		this.crvisible = visible;
	}
	/**
	*桁ルーラの可視を返します。
	*@return 桁ルーラが表示される場合true
	*/
	public boolean isColumnRulerVisible(){
		return this.crvisible;
	}
	/**行番号部分のコンポーネント*/
	private class LeafLineNumberPane extends JComponent{
		/**フィールド*/
		public static final int MARGIN = 5;
		private final JTextComponent comp;
		private final FontMetrics fontMetrics;
		private final int topInset;
		private final int fontAscent;
		private final int fontHeight;
		private int start=0;//選択開始行
		/**コンストラクタ*/
		LeafLineNumberPane(JTextComponent comp){
			this.comp = comp;
			this.setFont(comp.getFont());
			fontMetrics = getFontMetrics(comp.getFont());
			fontHeight = fontMetrics.getHeight();
			fontAscent = fontMetrics.getAscent();
			topInset = comp.getInsets().top;
			setBorder(BorderFactory.createMatteBorder(0,0,0,1,Color.BLACK));
			this.addMouseListener(new LeafMouseAdapter());
			this.addMouseMotionListener(new LeafMouseMotionAdapter());
		}
		/**LeafLineNumberPaneの幅を得る*/
		private int getComponentWidth(){
			Document doc = comp.getDocument();
			Element root = doc.getDefaultRootElement();
			int lineCount = root.getElementIndex(doc.getLength());
			int maxDigits = Math.max(4,String.valueOf(lineCount).length());
			return maxDigits * fontMetrics.stringWidth("0")+MARGIN*2;
		}
		/**座標から行番号を得る*/
		private int getLineAtPoint(int y){
			Element root = comp.getDocument().getDefaultRootElement();
			int pos = comp.viewToModel(new Point(0,y));
			return root.getElementIndex(pos);
		}
		/**オーバーライド*/
		public Dimension getPreferredSize(){
			return new Dimension(getComponentWidth(),comp.getHeight());
		}
		/**オーバーライド*/
		public void paintComponent(Graphics g){
			Rectangle clip = g.getClipBounds();
			g.setColor(getBackground());
			g.fillRect(clip.x,clip.y,clip.width,clip.height);
			g.setColor(getForeground());
			int base = clip.y - topInset;
			int start = getLineAtPoint(base);
			int end = getLineAtPoint(base+clip.height);
			int y = topInset - fontHeight + fontAscent + start * fontHeight;
			for(int i=start;i<=end;i++){
				String text = String.valueOf(i+1);
				int x = getComponentWidth() - MARGIN - fontMetrics.stringWidth(text);
				y += fontHeight;
				g.drawString(text,x,y);
			}
		}
		/**現在の行番号を返す*/
		public int getCurrentLineNumber(){
			return comp.getDocument().getDefaultRootElement().
			getElementIndex(comp.getCaretPosition())+1;
		}
		/**クリックすると一行選択*/
		private class LeafMouseAdapter extends MouseAdapter{
			public void mousePressed(MouseEvent e){
				int line = (e.getY())/fontHeight;
				try{
					Element element = comp.getDocument().
					getDefaultRootElement().getElement(line);
					comp.select(
						start = element.getStartOffset(),
						element.getEndOffset()-1
					);
					comp.requestFocusInWindow();
				}catch(Exception ex){}
			}
		}
		/**ドラッグすると複数行選択*/
		private class LeafMouseMotionAdapter extends MouseMotionAdapter{
			public void mouseDragged(MouseEvent e){
				int line = (e.getY())/fontHeight;
				try{
					Element element = comp.getDocument().
					getDefaultRootElement().getElement(line);
					comp.select(
						Math.min(start,element.getEndOffset()),
						Math.max(start,element.getEndOffset())
					);
					comp.requestFocusInWindow();
				}catch(Exception ex){}
			}
		}
	}
	/**桁ルーラ部分のコンポーネント*/
	private class LeafColumnRulerPane extends JComponent{
		/**フィールド*/
		public static final int MARGIN = 5;
		private final JTextComponent comp;
		private final FontMetrics fontMetrics;
		private final int leftInset;
		private final int fontWidth;
		/**コンストラクタ*/
		LeafColumnRulerPane(JTextComponent comp){
			this.comp = comp;
			this.setFont(new Font(Font.MONOSPACED,Font.PLAIN,13));
			fontMetrics = getFontMetrics(comp.getFont());
			fontWidth = fontMetrics.stringWidth("m");
			leftInset = comp.getInsets().left;
			setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.BLACK));
		}
		/**オーバーライド*/
		public Dimension getPreferredSize(){
			return new Dimension(
				comp.getWidth(),
				getFontMetrics(getFont()).getHeight()-5
			);
		}
		/**オーバーライド*/
		public Dimension getMaximumSize(){
			return new Dimension(
				comp.getWidth(),
				getFontMetrics(getFont()).getHeight()-5
			);
		}
		/**オーバーライド*/
		public void paintComponent(Graphics g){
			Rectangle clip = g.getClipBounds();
			g.setColor(getBackground());
			g.fillRect(clip.x,clip.y,clip.width,clip.height);
			g.setColor(getForeground());
			int end = getWidth()/fontWidth;
			int currentx = leftInset;
			int y1_5 = getHeight() / 3;
			int y1_e = (int)(getHeight() * 0.75f);
			int y2 = getHeight() - 2;
			
			for(int c=0;c<=end;c++){
				if(c%5==0)g.drawLine(currentx,y1_5,currentx,y2);
				else g.drawLine(currentx,y1_e,currentx,y2);
				if(c%10==0)g.drawString(""+(int)(c/10),currentx+2,y2);
				currentx += fontWidth;
			}
			g.setXORMode(Color.WHITE);
			try{
				Rectangle rect = comp.modelToView(comp.getCaretPosition());
				g.fillRect(rect.x+1,0,fontWidth-1,getHeight()+1);
			}catch(Exception ex){ex.printStackTrace();}
		}
		/**行頭のインデックスを返す*/
		private int getLineStartIndex(){
			Element elem = comp.getDocument().getDefaultRootElement();
			return elem.getElement(
				elem.getElementIndex(comp.getCaretPosition())
			).getStartOffset();
		}
		/**現在の桁数を返す*/
		public int getCurrentColumnNumber(){
			try{
				Rectangle rect = comp.modelToView(comp.getCaretPosition());
				if(rect!=null)return (int)rect.x/fontWidth + 1;
				else return 1;
			}catch(Exception ex){
				ex.printStackTrace();
				return 1;
			}
		}
	}
}
