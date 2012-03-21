/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.text;

import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.text.AttributedCharacterIterator;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

/**
 * 行カーソルの表示機能を持つテキストエリアです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年7月28日
 */
public class LeafTextArea extends JTextArea {
	private LeafCaret caret;
	private int maxRows = 0;
	private Color color = Color.BLUE;
	private boolean isLineCursorVisible = true;
	
	private FontMetrics met;
	/**
	 * テキストエリアを生成します。
	 */
	public LeafTextArea() {
		super();
		initialize(getDocument());
	}
	/**
	 * 初期テキストを指定してテキストエリアを生成します。
	 * @param text 初期表示文字列
	 */
	public LeafTextArea(String text) {
		super(text);
		initialize(getDocument());
	}
	/**
	 * ドキュメントを指定してテキストエリアを生成します。
	 * @param doc ドキュメント
	 */
	public LeafTextArea(Document doc) {
		super(doc);
		initialize(doc);
	}
	/**
	 * 最大行数と初期桁数を指定してテキストエリアを生成します。
	 * @param rows 最大行数
	 * @param cols 初期桁数
	 */
	public LeafTextArea(int rows, int cols) {
		super(rows, cols);
		this.maxRows = rows;
		initialize(getDocument());
	}
	/**
	 * 初期テキストと最大行数、
	 * 初期桁数を指定してテキストエリアを生成します。
	 * @param text 初期表示文字列
	 * @param rows 最大行数
	 * @param cols 初期桁数
	 */
	public LeafTextArea(String text, int rows, int cols) {
		super(text, rows, cols);
		this.maxRows = rows;
		initialize(getDocument());
	}
	/**
	 * ドキュメントと初期テキスト、最大行数、
	 * 初期桁数を指定してテキストエリアを生成します。
	 * @param doc ドキュメント
	 * @param text 初期表示文字列
	 * @param rows 最大行数
	 * @param cols 初期桁数
	 */
	public LeafTextArea(Document doc, String text, int rows, int cols) {
		super(doc, text, rows, cols);
		this.maxRows = rows;
		initialize(doc);
	}
	/**
	 * 指定されたドキュメントモデルでテキストエリアを初期化します。
	 * @param doc ドキュメント
	 */
	private void initialize(Document doc){
		setSelectionColor(Color.BLACK);
		setSelectedTextColor(Color.WHITE);
		int blink = getCaret().getBlinkRate();
		setCaret(caret = new LeafCaret(this));
		caret.setBlinkRate(blink);
		doc.addDocumentListener(new ExDocumentListener(doc));
	}
	/**
	 * 最大表示行数を設定します。
	 * この行数を超えた行は先頭から順に削除されます。
	 * @param rows 最大行数 制限しない場合は0
	 */
	public void setMaxRowCount(int rows){
		this.maxRows = rows;
	}
	/**
	 * 最大表示行数を返します。
	 * @return 最大行数
	 */
	public int getMaxRowCount(){
		return maxRows;
	}
	/**
	 * コンポーネントを描画します。
	 * @param g グラフィックス
	 */
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		paintLineCursor(g);
	}
	/**
	 * 行カーソルを描画します。
	 * @param g グラフィックス
	 */
	protected void paintLineCursor(Graphics g){
		if(!isLineCursorVisible) return;
		g.setColor(color);
		Insets insets = getInsets();
		int cy = caret.y+caret.height;
		int width = getSize().width-insets.left-insets.right;
		g.drawLine(insets.left, cy, width, cy);
	}
	/**
	 * 行カーソルの可視を設定します。
	 * @param visible 行カーソル表示の場合true
	 */
	public void setLineCursorVisible(boolean visible){
		isLineCursorVisible = visible;
	}
	/**
	 * 行カーソルが可視かどうか返します。
	 * @return 行カーソル表示の場合true
	 */
	public boolean isLineCursorVisible(){
		return isLineCursorVisible;
	}
	/**
	 * 改行コードをLFに統一してテキストを返します。
	 * @return テキスト
	 */
	public String getText(){
		return super.getText().replaceAll("(\r\n|\r)","\n");
	}
	/**
	 * 改行コードをLFに統一して選択文字列を返します。
	 * @return 選択されたテキスト
	 */
	public String getSelectedText(){
		try{
			return super.getSelectedText().replaceAll("(\r\n|\r)","\n");
		}catch(NullPointerException ex){
			return null;
		}
	}
	/**
	 * 表示行数を制限します。
	 */
	private final class ExDocumentListener
	implements DocumentListener{
		private final Document doc;
		public ExDocumentListener(Document doc){
			this.doc = doc;
		}
		@Override
		public void changedUpdate(DocumentEvent e){}
		@Override
		public void insertUpdate(DocumentEvent e) {
			if(maxRows <= 0) return;
			final Element root = doc.getDefaultRootElement();
			if(root.getElementCount() > maxRows){
				EventQueue.invokeLater(new Runnable(){
					@Override public void run(){
						removeLines(root);
					}
				});
			}
		}
		private void removeLines(Element root){
			Element first = root.getElement(0);
			try{
				doc.remove(0, first.getEndOffset());
			}catch(BadLocationException ex){}
		}
		@Override
		public void removeUpdate(DocumentEvent e){}	
	}
}
