/**********************************************************************************
 * leaf - Java Library for pafelog
 * Copyright(c) 2010 - 2013 by University of Tokyo Amateur Radio Club
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL) as published by the
 * Free Software Foundation, either version 3 of the License, or(at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package leaf.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

/**
 * 行番号と桁ルーラを表示する{@link JTextComponent}用スクロールコンテナです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2010年5月22日
 * 
 */
public class JTextScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private JLineNumberPane lineNumber;
	private JColumnRulerPane columnRuler;
	private ArrayList<BookMark>marks;
	private JTextComponent comp;
	private Element root;
	private boolean isLineNumberVisible = true;
	private boolean isColumnRulerVisible = true;
	private FontMetrics fontMetrics;
	private int fontWidth;
	private final FontHandler fontHandler;
	
	/**
	 * テキストコンポーネントを指定して{@link JTextScrollPane}を生成します。
	 * 
	 * @param comp テキストコンポーネント
	 */
	public JTextScrollPane(JTextComponent comp) {
		super(comp);
		this.comp = comp;
		fontHandler = new FontHandler();
		
		update();
		
		setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER,  new JPanel());
		setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, new JPanel());
		setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,  new JPanel());
		setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new JPanel());
		
		marks = new ArrayList<BookMark>();
	}
	
	/**
	 * {@link JTextScrollPane}の表示を最新の状態に更新します。
	 */
	public final void update() {
		fontMetrics = getFontMetrics(comp.getFont());
		fontWidth   = fontMetrics.charWidth('m');
		
		comp.addCaretListener(new ExCaretListener());
		root = comp.getDocument().getDefaultRootElement();
		comp.getDocument().addDocumentListener(new ExDocumentListener());
		
		lineNumber  = isLineNumberVisible ? new JLineNumberPane() : null;
		columnRuler = isColumnRulerVisible? new JColumnRulerPane(): null;
		setRowHeaderView(lineNumber);
		setColumnHeaderView(columnRuler);
		
		comp.removePropertyChangeListener("font", fontHandler);
		comp.   addPropertyChangeListener("font", fontHandler);
	}
	
	private class FontHandler implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			update();
		}
	}
	
	/**
	 * このスクロールコンポーネントに、行番号の速やかな再描画を要求します。
	 */
	public void repaintLineNumber() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(lineNumber !=null) lineNumber.repaint();
				if(columnRuler!=null) columnRuler.repaint();
			}
		});
	}
	
	private class ExCaretListener implements CaretListener{
		@Override
		public void caretUpdate(CaretEvent e) {
			if(columnRuler!=null) columnRuler.repaint();
		}
	}
	
	private class ExDocumentListener implements DocumentListener{
		@Override
		public void changedUpdate(DocumentEvent e) {}
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			this.update();
			repaintLineNumber();
		}
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			this.update();
			repaintLineNumber();
		}
		
		private void update() {
			for(int i=marks.size()-1;i>=0;i--) {
				marks.get(i).update();
			}
		}
	}
	
	/**
	 * 現在カーソルが表示されている行の行番号を返します。
	 * 
	 * @return 1以上の行番号
	 */
	public int getLineNumber() {
		return root.getElementIndex(comp.getCaretPosition()) + 1;
	}
	
	/**
	 * 現在カーソルが表示されている桁の行番号を返します。
	 * 
	 * @return 1以上の桁番号
	 */
	public int getColumnNumber() {
		int caret = comp.getCaretPosition();
		Element elem = root.getElement(root.getElementIndex(caret));
		return caret - elem.getStartOffset() + 1;
	}
	
	/**
	 * 行番号を表示するか表示しないか設定します。
	 * 直後に{@link #update()}を実行すると表示上反映されます。
	 * 
	 * @param visible 行番号を表示する場合true
	 */
	public void setLineNumberVisible(boolean visible) {
		final boolean old = isLineNumberVisible;
		isLineNumberVisible = visible;
		update();
		firePropertyChange("lineNumberVisible", old, visible);
	}
	
	/**
	 * 行番号を表示するか表示しないか返します。
	 * 
	 * @return 行番号を表示する場合true
	 */
	public boolean isLineNumberVisible() {
		return isLineNumberVisible;
	}
	
	/**
	 * 桁ルーラーを表示するか表示しないか設定します。
	 * 直後に{@link #update()}を実行すると表示上反映されます。
	 * 
	 * @param visible 桁ルーラ―を表示する場合true
	 */
	public void setColumnRulerVisible(boolean visible) {
		final boolean old = isColumnRulerVisible;
		isColumnRulerVisible = visible;
		update();
		firePropertyChange("columnRulerVisible", old, visible);
	}
	
	/**
	 * 桁ルーラ―を表示するか表示しないか返します。
	 * 
	 * @return 桁ルーラ―を表示する場合true
	 */
	public boolean isColumnRulerVisible() {
		return isColumnRulerVisible;
	}
	
	/**
	 * 指定行にキャレットを移動します。行番号は1から始まります。
	 * 
	 * @param line 移動先の行
	 */
	public void scrollToLine(int line) {
		line = Math.max(0, Math.min(root.getElementCount()-1, line-1));
		try {
			Element l = root.getElement(line);
			Rectangle d = comp.modelToView(l.getStartOffset());
			Rectangle c = getViewport().getViewRect();
			new Timer(15, new ScrollToLineTask(l, d, c)).start();
		} catch(BadLocationException | NullPointerException ex) {
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	private class ScrollToLineTask implements ActionListener {
		private final Rectangle dest, cur;
		private final Element line;
		
		public ScrollToLineTask(Element l, Rectangle d, Rectangle c) {
			this.line = l;
			this.dest = d;
			this.cur  = c;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Timer timer = (Timer) e.getSource();
			if(dest.y < cur.y && timer.isRunning()) {
				cur.y-= Math.max(1, (cur.y-dest.y)/2);
				comp.scrollRectToVisible(cur);
			} else if(dest.y > cur.y && timer.isRunning()) {
				cur.y += Math.max(1, (dest.y-cur.y)/2);
				comp.scrollRectToVisible(cur);
			} else {
				comp.setCaretPosition(line.getStartOffset());
				timer.stop();
			}
		}
	}
	
	private class BookMark {
		private Element elem;
		public BookMark(Element elem) {
			this.elem = elem;
		}
		public int getLine() {
			return root.getElementIndex(elem.getStartOffset());
		}
		public void update() {
			if(root.getElement(getLine()) != elem) {
				marks.remove(this);
			}
		}
	}
	
	private BookMark getBookMark(int line) {
		for(BookMark mark : marks) {
			if(mark.getLine() == line) {
				return mark;
			}
		}
		return null;
	}
	
	// 行番号を表示するコンポーネント
	private class JLineNumberPane extends JComponent{
		private static final long serialVersionUID = 1L;
		private static final int MARGIN = 8;
		private final int topInset;
		private final int fontAscent;
		private final int fontHeight;
		private int start = 0; //選択開始行
		
		public JLineNumberPane() {
			setFont(comp.getFont());
			fontHeight = fontMetrics.getHeight();
			fontAscent = fontMetrics.getAscent();
			topInset = comp.getInsets().top;
			setBorder( BorderFactory.createMatteBorder(
					0, 0, 0, 1, Color.BLACK
			));
			addMouseListener(new MouseClickListener());
			addMouseMotionListener(new MouseDragListener());
		}
		
		private int getComponentWidth() {
			Document doc   = comp.getDocument();
			int lineCount  = root.getElementIndex(doc.getLength());
			int maxDigits  = Math.max(4, String.valueOf(lineCount).length());
			return maxDigits * fontMetrics.stringWidth("0") + MARGIN * 2;
		}
		
		private int getLineAtPoint(int y) {
			int pos = comp.viewToModel(new Point(0,y));
			return root.getElementIndex(pos);
		}
		
		private int getHeightAtPoint(int y) {
			try {
				int pos = comp.viewToModel(new Point(0,y));
				return comp.modelToView(pos).y;
			} catch(BadLocationException | NullPointerException ex) {
				return 0;
			}
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(getComponentWidth(), comp.getHeight());
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			Rectangle clip = g.getClipBounds();
			g.setColor(getBackground());
			g.fillRect(clip.x, clip.y, clip.width, clip.height);
			g.setColor(getForeground());
			
			final int width = getComponentWidth();
			final int base  = clip.y - topInset;
			final int start = getLineAtPoint(base);
			final int end = getHeightAtPoint(base + clip.height);
			int y = topInset + fontAscent + (start-1) * fontHeight;
			
			for(int i = start + 1; y <= end; i++) {
				String text = String.valueOf(i);
				int x = width - MARGIN - fontMetrics.stringWidth(text);
				y += fontHeight;
				g.drawString(text, x, y);
			}
			g.setXORMode(Color.WHITE);
			for(BookMark mark : marks) {
				y = topInset + mark.getLine() * fontHeight;
				g.fillRect(0, y, width-1, fontHeight);
			}
		}
		
		private class MouseClickListener extends MouseAdapter{
			@Override
			public void mousePressed(MouseEvent e) {
				start = (e.getY() - topInset) / fontHeight;
				try {
					Element elem = comp.getDocument().
					getDefaultRootElement().getElement(start);
					if(e.getClickCount()==1) {
						comp.select(
							elem.getStartOffset(),
							elem.getEndOffset() - 1
						);
						comp.requestFocusInWindow();
					}else if(elem != null) {
						BookMark mark = getBookMark(start);
						if(mark != null) marks.remove(mark);
						else marks.add(new BookMark(elem));
						repaint();
					}
				} catch(Exception ex) {}
			}
		}
		
		private class MouseDragListener extends MouseMotionAdapter{
			@Override
			public void mouseDragged(MouseEvent e) {
				int end = (e.getY() - topInset) / fontHeight;
				if(start <= end) {
					selectStartToEnd(end);
				} else {
					selectEndToStart(end);
				}
				comp.requestFocusInWindow();
			}
			
			private void selectStartToEnd(int end) {
				int s = root.getElement(start).getStartOffset();
				int e = root.getElement(end).getEndOffset();
				comp.select(s,  e - 1);
			}
			
			private void selectEndToStart(int end) {
				int s = root.getElement(end).getStartOffset();
				int e = root.getElement(start).getEndOffset();
				comp.select(s, e - 1);
			}
		}
	}
	
	// 桁ルーラを表示するコンポーネント
	private class JColumnRulerPane extends JComponent{
		private static final long serialVersionUID = 1L;
		private final int leftInset;
		private int start = 0; // 選択開始桁
		
		public JColumnRulerPane() {
			setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
			leftInset   = comp.getInsets().left;
			setBorder(BorderFactory.createMatteBorder(
				0, 0, 1, 0, Color.BLACK
			));
			addMouseListener(new MouseClickListener());
			addMouseMotionListener(new MouseDragListener());
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(
				comp.getWidth(),
				getFontMetrics(getFont()).getHeight() - 2
			);
		}
		
		@Override
		public Dimension getMaximumSize() {
			return getPreferredSize();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			Rectangle clip = g.getClipBounds();
			g.setColor(getBackground());
			g.fillRect(clip.x,clip.y,clip.width,clip.height);
			g.setColor(getForeground());
			
			final int end = getWidth() / fontWidth;
			final int y1_5 = getHeight() / 3;
			final int y1_e = (int)(getHeight() * 0.75f);
			final int y2 = getHeight() - 2;
			int x = leftInset;
			
			for(int col = 0; col <= end; col++, x += fontWidth) {
				if(col % 5 == 0) g.drawLine(x, y1_5, x, y2);
				else g.drawLine(x, y1_e, x, y2);
				
				if(col % 10 == 0) g.drawString(String.valueOf(col/10), x + 2, y2);
			}
			g.setXORMode(Color.WHITE);
			try {
				Rectangle rect = comp.modelToView(comp.getCaretPosition());
				g.fillRect(rect.x + 1, 0, fontWidth -1, getHeight() + 1);
			} catch(BadLocationException | NullPointerException ex) {}
		}
		
		private class MouseClickListener extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					Point point = new Point(e.getPoint());
					point.translate(-leftInset,
						comp.modelToView(comp.getCaretPosition()).y - point.y);
					start = comp.viewToModel(point);
					comp.setCaretPosition(start);
					comp.requestFocusInWindow();
				} catch(BadLocationException | NullPointerException ex) {}
			}
		}
		
		private class MouseDragListener extends MouseMotionAdapter {
			@Override
			public void mouseDragged(MouseEvent e) {
				try {
					Point point = new Point(e.getPoint());
					point.translate(-leftInset,
						comp.modelToView(comp.getCaretPosition()).y - point.y);
					int end = comp.viewToModel(point);
					comp.select(Math.min(start,end), Math.max(start,end));
					comp.requestFocusInWindow();
				} catch(BadLocationException | NullPointerException ex) {}
			}
		}
	}
}