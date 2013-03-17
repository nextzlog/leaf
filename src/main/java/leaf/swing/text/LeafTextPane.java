/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.text;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;

import leaf.swing.doc.TextEditorKit;

/**
 * 独自の拡張機能を実装したテキストコンポーネントです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年5月22日
 *
 */
public class LeafTextPane extends JTextPane {
	private static final long serialVersionUID = 1L;
	private Color cursorColor = Color.BLUE;
	private LeafCaret caret;
	private int tabSize = 8;
	private boolean isLineWrap = false;
	private boolean isEOFVisible = true;
	private boolean isLineCursorVisible = true;
	
	private boolean isOnIME = false;
	private int caretMode = CARET_INSERT_MODE;
	private FontMetrics met;
	
	/**
	 * 空のテキストコンポーネントを構築します。
	 */
	public LeafTextPane() {
		initialize();
	}
	
	/**
	 * ドキュメントを指定してテキストコンポーネントを構築します。
	 * 
	 * @param doc ドキュメント
	 */
	public LeafTextPane(StyledDocument doc) {
		super(doc);
		initialize();
		setDocument(doc);
	}
	
	private void initialize() {
		met = getFontMetrics(getFont());
		setEditorKit(createDefaultEditorKit());
		
		int blink = getCaret().getBlinkRate();
		setCaret(caret = new LeafCaret(this));
		caret.setBlinkRate(blink);
		
		setSelectedTextColor(Color.WHITE);
		setSelectionColor(Color.BLACK);
		setTabSize(tabSize);
		setDragEnabled(true);
		
		//switch isOnIME
		addInputMethodListener(new InputMethodListener() {
			@Override
			public void inputMethodTextChanged(InputMethodEvent e) {
				AttributedCharacterIterator aci = e.getText();
				try {
					int count = e.getCommittedCharacterCount();
					int length = aci.getEndIndex() - aci.getBeginIndex();
					LeafTextPane.this.isOnIME = (count < length);
				} catch(NullPointerException ex) {
					LeafTextPane.this.isOnIME = false;
				}
			}
			@Override
			public void caretPositionChanged(InputMethodEvent e) {}
		});
		
		addPropertyChangeListener("font", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				LeafTextPane.this.met = getFontMetrics(getFont());
			}
		});
		
		addPropertyChangeListener("document", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if(met != null) setTabSize(getTabSize());
			}
		});
	}
	
	/**
	 * {@link TextEditorKit}を生成して返します。
	 * 
	 * @return TextEditorKit
	 */
	@Override
	protected EditorKit createDefaultEditorKit() {
		return new TextEditorKit();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		try {
			super.paintComponent(g);
			paintMatchingBrace(g);
			paintLineCursor(g);
			paintEOF(g);
		} catch(RuntimeException ex) {}
	}
	
	/**
	 * キャレットの表示されている行を強調表示します。
	 * 
	 * @param g コンポーネントのグラフィックス
	 */
	protected void paintLineCursor(Graphics g) {
		if(isLineCursorVisible) {
			g.setColor(cursorColor);
			Insets insets = getInsets();
			int cy = caret.y + caret.height;
			int width = getSize().width - insets.left - insets.right;
			g.drawLine(insets.left, cy, width, cy);
		}
	}
	
	/**
	 * テキスト終端表示符号を表示します。
	 * 
	 * @param g コンポーネントのグラフィックス
	 */
	protected void paintEOF(Graphics g) {
		if(isEOFVisible) try {
			Graphics2D g2 = (Graphics2D)g;
			g2.setPaint(getSelectionColor());
			int width  = met.stringWidth("[EOF]");
			int height = met.getAscent() + met.getLeading();
			Rectangle rect = modelToView(getDocument().getLength());
			g2.fillRect(rect.x + 2, rect.y, width, met.getHeight());
			g2.setPaint(getSelectedTextColor());
			g2.drawString("[EOF]", rect.x + 3, rect.y + height);
		} catch(BadLocationException ex) {}
	}
	
	/**
	 * キャレット近傍にある括弧と、対応する括弧を強調表示します。
	 * 
	 * @param g コンポーネントのグラフィックス
	 */
	protected void paintMatchingBrace(Graphics g) {
		int index1 = getCaretPosition(), index2;
		final Document doc = getDocument();
		try {
			if(index1 > 0) index1--;
			char brace = doc.getText(index1,  1).charAt(0);
			if(Character.isWhitespace(brace)) {
				brace = doc.getText(++index1, 1).charAt(0);
			}
			index2 = searchMatchingBrace(index1);
			if(index2 >= 0) {
				int width  = met.charWidth(brace);
				g.setColor(cursorColor);
				Rectangle rect1 = modelToView(index1);
				Rectangle rect2 = modelToView(index2);
				g.drawRect(rect1.x, rect1.y, width, met.getHeight());
				g.drawRect(rect2.x, rect2.y, width, met.getHeight());
			}
		} catch(BadLocationException ex) {}
	}
	
	/**
	 * 指定した位置にある括弧に対応する括弧の位置を検索して返します。
	 * 
	 * @param index 括弧の位置
	 * @return 対応する括弧の位置 括弧が存在しない場合-1
	 */
	protected int searchMatchingBrace(int index) {
		final Document doc = getDocument();
		try {
			char brace = doc.getText(index, 1).charAt(0);
			switch(brace) {
				case '<': return searchBraceAfter(index, '>', '<');
				case '(': return searchBraceAfter(index, ')', '(');
				case '{': return searchBraceAfter(index, '}', '{');
				case '[': return searchBraceAfter(index, ']', '[');
				case '>': return searchBraceBefore(index,'<', '>');
				case ')': return searchBraceBefore(index,'(', ')');
				case '}': return searchBraceBefore(index,'{', '}');
				case ']': return searchBraceBefore(index,'[', ']');
			}
		} catch(BadLocationException ex) {}
		return -1;
	}
	
	private int searchBraceBefore(int index, char brace, char pair) {
		final String text = getText();
		int nest = 1;
		for(int i=index-1; i>= 0; i--) {
			char ch = text.charAt(i);
			if(ch == pair ) nest++;
			if(ch == brace) nest--;
			if(nest == 0) return i;
		}
		return -1;
	}
	
	private int searchBraceAfter(int index, char brace, char pair) {
		final String text = getText();
		int length = text.length(), nest = 1;
		for(int i=index +1; i < length; i++) {
			char ch = text.charAt(i);
			if(ch == pair ) nest++;
			if(ch == brace) nest--;
			if(nest == 0) return i;
		}
		return -1;
	}
	
	/**
	 * 行の折り返しポリシーを設定します。
	 * 
	 * @param wrap 行を折り返す場合true
	 */
	public void setLineWrap(boolean wrap) {
		final boolean old = isLineWrap;
		firePropertyChange("lineWrap", old, isLineWrap = wrap);
	}
	
	/**
	 * 行の折り返しポリシーを返します。
	 * 
	 * @return 行を折り返す場合true
	 */
	public boolean getLineWrap() {
		return isLineWrap;
	}
	
	/**
	 * 行の折り返しを制御するためにオーバーライドされます。
	 * 
	 * @return 行を折り返す場合true
	 */
	@Override
	public boolean getScrollableTracksViewportWidth() {
		if(!isLineWrap) {
			Component p = getParent();
			if(!(p instanceof JViewport))return false;
			int width = getUI().getPreferredSize(this).width;
			return width <= p.getSize().width;
		}else return super.getScrollableTracksViewportWidth();
	}
	
	/**
	 * タブの表示サイズを返します。
	 * 
	 * @return タブの展開文字数
	 */
	public int getTabSize() {
		return tabSize;
	}
	
	/**
	 * タブの表示サイズを設定します。
	 * 
	 * @param size タブの展開文字数
	 */
	public void setTabSize(int size) {
		int width = met.charWidth('m') * size;
		TabStop[] tabs = new TabStop[100];
		for(int i=0; i <tabs.length; i++) {
			tabs[i] = new TabStop((i + 1) * width);
		}
		TabSet tabset = new TabSet(tabs);
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setTabSet(set, tabset);
		getStyledDocument().setParagraphAttributes(
			0, getDocument().getLength(), set, false);
		
		final int old = tabSize;
		firePropertyChange("tabSize", old, tabSize = size);
	}
	
	/**
	 * テキスト終端表示符号の表示を設定します。
	 * 
	 * @param visible 表示する場合true
	 */
	public void setEOFVisible(boolean visible) {
		final boolean old = isEOFVisible;
		firePropertyChange("EOFVisible", old, isEOFVisible = visible);
	}
	
	/**
	 * テキスト終端表示符号が表示されているか返します。
	 * 
	 * @return 表示する場合true
	 */
	public boolean isEOFVisible() {
		return isEOFVisible;
	}
	
	/**
	 * キャレットの存在する行を強調表示するか設定します。
	 * 
	 * @param visible 強調表示する場合true
	 */
	public void setLineCursorVisible(boolean visible) {
		final boolean old = isLineCursorVisible;
		isLineCursorVisible = visible;
		firePropertyChange("lineCursorVisible", old, visible);
	}
	
	/**
	 * キャレットの存在する行を強調表示するか返します。
	 * 
	 * @return 強調表示する場合true
	 */
	public boolean isLineCursorVisible() {
		return isLineCursorVisible;
	}
	
	/**
	 * キャレットの存在する行を強調表示するのに用いる色を設定します。
	 * 
	 * @param color 強調表示に用いる色
	 */
	public void setCursorColor(Color color) {
		final Color old = cursorColor;
		cursorColor = color != null? color : Color.BLACK;
		firePropertyChange("cursorColor", old, cursorColor);
	}
	
	/**
	 * キャレットの存在する行を強調表示するのに用いる色を返します。
	 * 
	 * @return 強調表示に用いる色
	 */
	public Color getCursorColor() {
		return cursorColor;
	}
	
	/**
	 * このテキストコンポーネントの内容の末尾に文字列を追加します。
	 * 
	 * @param text 追加する文字列
	 */
	public void append(String text) {
		final Document doc = getDocument();
		try {
			if(doc != null) doc.insertString(doc.getLength(), text, null);
		} catch(BadLocationException ex) {
			UIManager.getLookAndFeel().provideErrorFeedback(this);
		}
	}
	
	/**
	 * このテキストコンポーネントの内容の任意の場所に文字列を挿入します。
	 * 
	 * @param text 挿入する文字列
	 * @param pos  挿入する位置
	 * 
	 * @throws IllegalArgumentException モデル内の無効な位置を指定した場合
	 */
	public void insert(String text, int pos) throws IllegalArgumentException{
		final Document doc = getDocument();
		try {
			doc.insertString(pos, text, null);
		} catch(BadLocationException ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
	}
	
	/**
	 * 選択文字列を指定した文字列で置換します。
	 * 
	 * キャレットが置換動作に設定されている場合、
	 * 選択文字列が空であればキャレット位置の次の文字を削除します。
	 * 
	 * @param text 置換後の文字列
	 */
	@Override
	public void replaceSelection(String text) {
		if(caretMode == CARET_REPLACE_MODE && !isOnIME) {
			int pos = getCaretPosition();
			try {
				if(getSelectedText()==null) {
					char ch = getText(pos, 1).charAt(0);
					if(ch != '\n' && ch != '\r')
					moveCaretPosition(pos+text.length());
				}
			} catch(BadLocationException ex) {}
		}
		super.replaceSelection(text);
	}
	
	/**
	 * キャレットの動作を置換動作に指定するための定数です。
	 */
	public static final int CARET_INSERT_MODE  = 1;
	
	/**
	 * キャレットの動作を挿入動作に指定するための定数です。
	 */
	public static final int CARET_REPLACE_MODE = 0;
	
	/**
	 * キャレットの動作を置換動作または挿入動作に設定します。
	 * 
	 * @param mode {@link #CARET_INSERT_MODE} {@link #CARET_REPLACE_MODE}
	 */
	public void setCaretMode(int mode) {
		final int old = caretMode;
		firePropertyChange("caretMode", old, caretMode = mode);
	}
	
	/**
	 * キャレットの動作を置換動作または挿入動作で返します。
	 * 
	 * @return {@link #CARET_INSERT_MODE} {@link #CARET_REPLACE_MODE}
	 */
	public int getCaretMode() {
		return caretMode;
	}
	
	/**
	 * INSERTキーでキャレット動作を切り替えます。
	 * 
	 * @param e キーイベント
	 */
	@Override
	protected void processKeyEvent(KeyEvent e) {
		super.processKeyEvent(e);
		if(e.getKeyCode() == KeyEvent.VK_INSERT
		&& e.getID() == KeyEvent.KEY_RELEASED) {
			if (caretMode == CARET_INSERT_MODE)
				caretMode = CARET_REPLACE_MODE;
			else caretMode = CARET_INSERT_MODE;
		}
	}
	
	/**
	 * このテキストコンポーネントの内容を改行コードをLFに変換して返します。
	 * 
	 * @return このコンポーネントの内容
	 */
	@Override
	public String getText() {
		return super.getText().replaceAll("(\r\n|\r)","\n");
	}
	
	/**
	 * 選択文字列を改行コードをLFに変換して返します。
	 * 
	 * @return 選択部分の文字列
	 */
	public String getSelectedText() {
		try {
			return super.getSelectedText().replaceAll("(\r\n|\r)","\n");
		} catch(NullPointerException ex) {
			return null;
		}
	}

}
