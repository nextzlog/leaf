/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.components.text;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

/**
*行番号と桁ルーラを表示するテキストコンポーネント用スクロール領域です。
*
*このコンポーネントはJTextComponentの全ての継承クラスに対応していますが、
*設計上全ての文字が同一のフォントで表示されることを想定しているため、
*マルチフォントのテキストには対応していません。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*/
public class LeafTextScrollPane extends JScrollPane {
	
	private JTextComponent comp;
	private LeafLineNumberPane lineNumber;
	private LeafColumnRulerPane ruler;
	
	private boolean isLineNumberVisible = true, isColumnRulerVisible = true;
	
	/**
	*テキスト領域を指定してスクロール領域を生成します。
	*@param comp テキスト領域
	*/
	public LeafTextScrollPane(JTextComponent comp){
		this(comp, true, true);
	}
	/**
	*テキスト領域と、行番号・桁ルーラの可視を指定してスクロール領域を生成します。
	*@param comp テキスト領域
	*@param ln 行番号を表示する場合true
	*@param cr 桁ルーラを表示する場合true
	*/
	public LeafTextScrollPane(JTextComponent comp, boolean ln, boolean cr){
		super(comp);
		
		isLineNumberVisible  = ln;
		isColumnRulerVisible = cr;
		
		init(this.comp = comp);
		
		getViewport().setOpaque(false);
		
		setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
		
		setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER,  new JPanel());
		setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, new JPanel());
		setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,  new JPanel());
		setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new JPanel());
	}
	/**
	*このスクロール領域を初期化します。
	*フォント変更時には必ず実行してください。
	*/
	public void init(){
		
		comp.addCaretListener(new ExCaretListener());
		comp.getDocument().addDocumentListener(new ExDocumentListener());
		
		setRowHeaderView(
			lineNumber = (isLineNumberVisible)? new LeafLineNumberPane() : null
		);
		setColumnHeaderView(
			ruler = (isColumnRulerVisible)? new LeafColumnRulerPane() : null
		);
	}
	/**
	*ビューポートに表示するテキスト領域を指定して初期化します。
	*@param comp テキスト領域
	*/
	public void init(JTextComponent comp){
		this.comp = comp;
		init();
	}
	/**
	*このスクロール領域に、行番号の速やかな再描画を要求します。
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
	/**
	*CaretListener
	*/
	private class ExCaretListener implements CaretListener{
		public void caretUpdate(CaretEvent e){
			if(ruler!=null)ruler.repaint();
		}
	}
	/**
	*DocumentListener
	*/
	private class ExDocumentListener implements DocumentListener{
		public void changedUpdate(DocumentEvent e){}
		public void insertUpdate(DocumentEvent e){
			repaintLineNumber();
		}
		public void removeUpdate(DocumentEvent e){
			repaintLineNumber();
		}
	}
	/**
	*カーソル行の行番号を返します。
	*@return 1以上の行番号
	*/
	public int getLineNumber(){
		return lineNumber.getLineNumber();
	}
	/**
	*カーソル位置の桁番号を返します。
	*@return 1以上の桁番号
	*/
	public int getColumnNumber(){
		return ruler.getColumnNumber();
	}
	/**
	*行番号の可視を設定します。
	*直後に{@link #init()}を実行すると表示上反映されます。
	*/
	public void setLineNumberVisible(boolean visible){
		this.isLineNumberVisible = visible;
	}
	/**
	*行番号の可視を返します。
	*@return 行番号が表示される場合true
	*/
	public boolean isLineNumberVisible(){
		return isLineNumberVisible;
	}
	/**
	*桁ルーラの可視を設定します。
	*直後に{@link #init()}を実行すると表示上反映されます。
	*/
	public void setColumnRulerVisible(boolean visible){
		this.isColumnRulerVisible = visible;
	}
	/**
	*桁ルーラの可視を返します。
	*@return 桁ルーラが表示される場合true
	*/
	public boolean isColumnRulerVisible(){
		return isColumnRulerVisible;
	}
	/**
	*指定行にキャレットを移動します。行番号は1から始まります。
	*@param line 移動先の行
	*/
	public void scrollToLine(int line){
		
		final Element root = comp.getDocument().getDefaultRootElement();
		line = Math.max(0, Math.min(root.getElementCount()-1, line-1));
		
		final Element elem = root.getElement(line);
		
		try{
			final Rectangle dest = comp.modelToView(elem.getStartOffset());
			final Rectangle cur  = getViewport().getViewRect();
			new Timer(15, new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Timer timer = (Timer)e.getSource();
					if(dest.y < cur.y && timer.isRunning()){
						cur.y-= Math.max(1, (cur.y-dest.y)/2);
						comp.scrollRectToVisible(cur);
					}else if(dest.y > cur.y && timer.isRunning()){
						cur.y += Math.max(1, (dest.y-cur.y)/2);
						comp.scrollRectToVisible(cur);
					}else{
						comp.setCaretPosition(elem.getStartOffset());
						timer.stop();
					}
				}
			}).start();
		}catch(Exception ex){
			Toolkit.getDefaultToolkit().beep();
		}
	}
	/**
	*行番号を表示するコンポーネントです。
	*/
	private class LeafLineNumberPane extends JComponent{
		
		private static final int MARGIN = 8;
		private final FontMetrics fontMetrics;
		private final int topInset;
		private final int fontAscent;
		private final int fontHeight;
		
		private int start = 0; //選択開始行
		
		/**
		*コンポーネントを生成します。
		*/
		public LeafLineNumberPane(){
			setFont(comp.getFont());
			fontMetrics = getFontMetrics(comp.getFont());
			fontHeight = fontMetrics.getHeight();
			fontAscent = fontMetrics.getAscent();
			topInset = comp.getInsets().top;
			setBorder( BorderFactory.createMatteBorder(
					0, 0, 0, 1, Color.BLACK
			));
			addMouseListener(new ExMouseAdapter());
			addMouseMotionListener(new ExMouseMotionAdapter());
		}
		/**
		*このコンポーネントの取るべき幅を返します。
		*/
		private int getComponentWidth(){
			Document doc   = comp.getDocument();
			Element root  = doc.getDefaultRootElement();
			int lineCount  = root.getElementIndex(doc.getLength());
			int maxDigits  = Math.max(4, String.valueOf(lineCount).length());
			return maxDigits * fontMetrics.stringWidth("0") + MARGIN * 2;
		}
		/**
		*指定された垂直座標に対応する行番号を返します。
		*/
		private int getLineAtPoint(int y){
			Element root = comp.getDocument().getDefaultRootElement();
			int pos = comp.viewToModel(new Point(0,y));
			return root.getElementIndex(pos);
		}
		/**
		*このコンポーネントの推奨されたサイズを返します。
		*/
		public Dimension getPreferredSize(){
			return new Dimension(getComponentWidth(), comp.getHeight());
		}
		/**
		*このコンポーネントを描画します。
		*/
		public void paintComponent(Graphics g){
			Rectangle clip = g.getClipBounds();
			g.setColor(getBackground());
			g.fillRect(clip.x, clip.y, clip.width, clip.height);
			g.setColor(getForeground());
			int base = clip.y - topInset;
			int start = getLineAtPoint(base);
			int end = getLineAtPoint(base + clip.height);
			int y = topInset - fontHeight + fontAscent + start * fontHeight;
			for(int i = start; i <= end; i++){
				String text = String.valueOf(i + 1);
				int x = getComponentWidth() - MARGIN - fontMetrics.stringWidth(text);
				y += fontHeight;
				g.drawString(text,x,y);
			}
		}
		/**
		*カーソル行の行番号を返します。
		*/
		public int getLineNumber(){
			Element root = comp.getDocument().getDefaultRootElement();
			return root.getElementIndex(comp.getCaretPosition()) + 1;
		}
		/**
		*コンポーネントの上でクリックすると行選択します。
		*/
		private class ExMouseAdapter extends MouseAdapter{
			public void mousePressed(MouseEvent e){
				start = (e.getY() - topInset) / fontHeight;
				try{
					Element element = comp.getDocument().
					getDefaultRootElement().getElement(start);
					comp.select(
						element.getStartOffset(),
						element.getEndOffset() - 1
					);
					comp.requestFocusInWindow();
				}catch(Exception ex){}
			}
		}
		/**
		*コンポーネントの上でドラッグすると複数行選択します。
		*/
		private class ExMouseMotionAdapter extends MouseMotionAdapter{
			public void mouseDragged(MouseEvent e){
				int end = (e.getY() - topInset) / fontHeight;
				try{
					Element root = comp.getDocument().getDefaultRootElement();
					if(start <= end){
						comp.select(
							root.getElement(start).getStartOffset(),
							root.getElement(end).getEndOffset() - 1
						);
					}else{
						comp.select(
							root.getElement(end).getStartOffset(),
							root.getElement(start).getEndOffset() - 1
						);
					}
					comp.requestFocusInWindow();
				}catch(Exception ex){}
			}
		}
	}
	/**
	*桁ルーラを表示するコンポーネントです。
	*/
	private class LeafColumnRulerPane extends JComponent{
		
		private final FontMetrics fontMetrics;
		private final int leftInset;
		private final int fontWidth;
		
		private int start = 0; // 選択開始桁
		
		/**
		*このコンポーネントを生成します。
		*/
		public LeafColumnRulerPane(){
			setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
			fontMetrics = getFontMetrics(comp.getFont());
			fontWidth   = fontMetrics.stringWidth("m");
			leftInset   = comp.getInsets().left;
			setBorder(BorderFactory.createMatteBorder(
				0, 0, 1, 0, Color.BLACK
			));
			addMouseListener(new ExMouseAdapter());
			addMouseMotionListener(new ExMouseMotionAdapter());
		}
		/**
		*このコンポーネントの推奨されたサイズを返します。
		*/
		public Dimension getPreferredSize(){
			return new Dimension(
				comp.getWidth(),
				getFontMetrics(getFont()).getHeight() - 2
			);
		}
		/**
		*このコンポーネントの最大サイズを返します。
		*/
		public Dimension getMaximumSize(){
			return getPreferredSize();
		}
		/**
		*このコンポーネントを描画します。
		*/
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
		/**
		*カーソル行の位置を返します。
		*/
		private int getLineStartOffset(){
			Element elem = comp.getDocument().getDefaultRootElement();
			return elem.getElement(
				elem.getElementIndex(comp.getCaretPosition())
			).getStartOffset();
		}
		/**
		*カーソル位置の桁番号を返します。
		*/
		public int getColumnNumber(){
			try{
				Rectangle rect = comp.modelToView(comp.getCaretPosition());
				if(rect != null) return (int)rect.x/fontWidth + 1;
				else return 1;
			}catch(Exception ex){
				ex.printStackTrace();
				return 1;
			}
		}
		/**
		*コンポーネントの上でクリックするとキャレットを移動します。
		*/
		private class ExMouseAdapter extends MouseAdapter {
			public void mousePressed(MouseEvent e){
				try{
					Point point = new Point(e.getPoint());
					point.translate(-leftInset,
						comp.modelToView(comp.getCaretPosition()).y-point.y);
					start = comp.viewToModel(point);
					comp.setCaretPosition(start);
					comp.requestFocusInWindow();
				}catch(Exception ex){}
			}
		}
		/**
		*コンポーネントの上でドラッグすると複数行選択します。
		*/
		private class ExMouseMotionAdapter extends MouseMotionAdapter {
			public void mouseDragged(MouseEvent e){
				try{
					Point point = new Point(e.getPoint());
					point.translate(-leftInset,
						comp.modelToView(comp.getCaretPosition()).y-point.y);
					int end = comp.viewToModel(point);
					comp.select(
						Math.min(start,end),
						Math.max(start,end)
					);
					comp.requestFocusInWindow();
				}catch(Exception ex){}
			}
		}
	}
}
