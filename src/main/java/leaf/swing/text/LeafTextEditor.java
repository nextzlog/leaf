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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import leaf.document.LeafSyntaxDocument;
import leaf.manager.LeafCharsetManager;
import leaf.swing.LeafMultiSplitPane;

/**
*高度なテキストエディタ領域の実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年11月26日
*/
public class LeafTextEditor extends LeafMultiSplitPane{
	private LinkedList<LeafTextPane> textpanes;
	private LinkedList<LeafTextScrollPane> scrollpanes;
	
	private LeafTextPane textpane;
	private LeafTextScrollPane scrollpane;
	private Border border;
	
	private EditorKit editorkit;
	private UndoManager undoManager;
	private boolean isEditable;
	private boolean isEdited  = false;
	private boolean isAutoIndentEnabled;
	/**
	*エディタを生成します。
	*/
	public LeafTextEditor(){
		super(true);
		textpane   = textpanes.get(0);
		scrollpane = scrollpanes.get(0);
	}
	/**
	*最初のテキスト領域を生成して返します。
	*@return 最初に配置されるテキスト領域
	*/
	@Override
	protected Component createFirstComponent(){
		editorkit = LeafSyntaxDocument.getEditorKit();
		undoManager = new UndoManager();
		undoManager.setLimit(2000);
		textpanes   = new LinkedList<LeafTextPane>();
		scrollpanes = new LinkedList<LeafTextScrollPane>();
		isEditable  = true;
		scrollpane  = createScrollPane();
		LeafSyntaxDocument doc = getDocument();
		doc.addUndoableEditListener(
			new ExUndoableEditListener());
		doc.setAutoIndentEnabled(isAutoIndentEnabled = true);
		return scrollpane;
	}
	/**
	*画面分割用にテキスト領域を生成して返します。
	*@return 新たに配置されるテキスト領域
	*/
	@Override
	protected Component createComponent(){
		Cursor cursor = textpane.getCursor();
		int tabsize = textpane.getTabSize();
		Font font = textpane.getFont();
		LeafSyntaxDocument doc = getDocument();
		scrollpane = createScrollPane();
		textpane.setDocument(doc);
		textpane.setComponentPopupMenu(
			getComponentPopupMenu());
		textpane.setCursor(cursor);
		textpane.setFont(font);
		textpane.setTabSize(tabsize);
		return scrollpane;
	}
	/**
	*画面分割用にスクロール領域を生成します。
	*/
	private LeafTextScrollPane createScrollPane(){
		final LeafTextPane pane = new LeafTextPane();
		final LeafTextScrollPane scroll
		= new LeafTextScrollPane(pane);
		pane.setOpaque(false);
		pane.setEditorKit(editorkit);
		pane.setEditable(isEditable);
		pane.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e){
				textpane   = pane;
				scrollpane = scroll;
			}
		});
		for(CaretListener listener :
		listenerList.getListeners(CaretListener.class)){
			pane.addCaretListener(listener);
		}
		for(KeyListener listener :
		listenerList.getListeners(KeyListener.class)){
			pane.addKeyListener(listener);
		}
		textpanes.add(textpane = pane);
		scrollpanes.add(scrollpane = scroll);
		setViewportBorder(border);
		return scroll;
	}
	/**
	*エディタの画面分割を1段階解除します。
	*@return 削除されたコンポーネント
	*/
	@Override
	public Component[] merge(){
		Component[] comps = super.merge();
		for(Component comp : comps){
			LeafTextScrollPane scroll = (LeafTextScrollPane)comp;
			Component view = scroll.getViewport().getView();
			textpanes.remove((LeafTextPane)view);
			scrollpanes.remove(scroll);
		}
		return comps;
	}
	/**
	*UndoableEditListener
	*/
	private class ExUndoableEditListener
	implements UndoableEditListener{
		public void undoableEditHappened(UndoableEditEvent e){
			UndoableEdit edit = e.getEdit();
			if(!(edit instanceof DocumentEvent)
			|| ((DocumentEvent)edit).getType() 
			!= DocumentEvent.EventType.CHANGE){
				isEdited = true;
				undoManager.addEdit(edit);
			}
		}
	}
	/**
	*指定されたファイルからテキストを読み込みます。
	*@param file 読み込み元ファイル
	*@param chset 文字セット
	*@throws IOException 読み込み例外があった場合
	*/
	public void read(File file, Charset chset)
	throws IOException{
		FileInputStream stream = null;
		try{
			stream  = new FileInputStream(file);
			read(new InputStreamReader(stream, chset));
		}finally{
			if(stream  != null) stream.close();
		}
	}
	/**
	*リーダーからテキストを読み込みます。
	*@param reader 読み込み元リーダー
	*@throws IOException 読み込み例外があった場合
	*@since 2011年3月28日
	*/
	public void read(Reader reader) throws IOException{
		getDocument().setAutoIndentEnabled(false);
		try{
			textpane.read(reader, null);
			LeafSyntaxDocument doc = getDocument();
			for(LeafTextPane textpane : textpanes){
				textpane.setDocument(doc);
			}
			undoManager.discardAllEdits();
			doc.addUndoableEditListener(
				new ExUndoableEditListener());
			doc.setAutoIndentEnabled(isAutoIndentEnabled);
			for(LeafTextScrollPane scroll : scrollpanes){
				scroll.init();
			}
			isEdited = false;
		}finally{
			if(reader != null) reader.close();
		}
	}
	/**
	*文字列からテキストを読み込みます。
	*@param text 読み込み元文字列
	*@since 2011年3月28日
	*/
	public void read(String text){
		try{
			read(new StringReader(text));
		}catch(IOException ex){}
	}
	/**
	*リーダーから選択文字列にテキストを読み込みます。
	*@param reader 読み込み元リーダー
	*@throws IOException 読み込み例外があった場合
	*/
	public void readIntoSelection(Reader reader)
	throws IOException{
		BufferedReader breader  = null;
		try{
			breader = new BufferedReader(reader);
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = breader.readLine()) != null){
				sb.append(line);
				sb.append("\n");
			}
			textpane.replaceSelection(sb.substring(0, sb.length()-1));
		}finally{
			if(breader != null) breader.close();
			if( reader != null) reader.close();
		}
	}
	/**
	*指定されたファイルから選択部分にテキストを読み込みます。
	*@param file 読み込み元ファイル
	*@param chset 文字セット
	*@throws IOException 読み込み例外があった場合
	*/
	public void readIntoSelection(File file, Charset chset)
	throws IOException{
		FileInputStream stream = null;
		try{
			stream  = new FileInputStream(file);
			readIntoSelection(new InputStreamReader(stream, chset));
		}finally{
			if(stream  != null) stream.close();
		}
	}
	/**
	*テキストをライターに書き込みます。
	*@param writer 書き込み先ライター
	*@throws IOException 書き込み例外があった場合
	*/
	public void write(Writer writer) throws IOException{
		BufferedWriter bwriter = null;
		try{
			bwriter  = new BufferedWriter(writer);
			int start = 0, end = 0;
			String text = getText();
			String ls   = LeafCharsetManager.getLineSeparator();
			while((end = text.indexOf("\n", start)) >= 0){
				bwriter.write(text.substring(start, end) + ls);
				bwriter.flush();
				start = end + 1;
			}
			if(start < text.length())
				bwriter.write(text.substring(start, text.length()));
			isEdited = false;
		}finally{
			if(bwriter != null) bwriter.close();
			if( writer != null) writer.close();
		}
	}
	/**
	*テキストをファイルに書き込みます。
	*@param file 書き込み先ファイル
	*@param chset 文字セット
	*@throws IOException 書き込み例外があった場合
	*/
	public void write(File file, Charset chset)
	throws IOException{
		FileOutputStream stream = null;
		try{
			stream = new FileOutputStream(file);
			write(new OutputStreamWriter(stream, chset));
		}finally{
			if(stream  != null) stream.close();
		}
	}
	/**
	*選択文字列をライターに書き込みます。
	*@param writer 書き込み先ライター
	*@throws IOException 書き込み例外があった場合
	*/
	public void writeFromSelection(Writer writer)
	throws IOException{
		BufferedWriter bwriter = null;
		try{
			bwriter  = new BufferedWriter(writer);
			int start = 0, end = 0;
			String text = getSelectedText();
			String ls   = LeafCharsetManager.getLineSeparator();
			while((end = text.indexOf("\n", start)) >= 0){
				bwriter.write(text.substring(start, end) + ls);
				bwriter.flush();
				start = end + 1;
			}
			if(start < text.length())
				bwriter.write(text.substring(start, text.length()));
		}finally{
			if(bwriter != null) bwriter.close();
			if( writer != null) writer.close();
		}
	}
	/**
	*選択文字列をファイルに書き込みます。
	*@param file 書き込み先ファイル
	*@param chset 文字セット
	*@throws IOException 書き込み例外があった場合
	*/
	public void writeFromSelection(File file, Charset chset)
	throws IOException{
		FileOutputStream stream = null;
		try{
			stream = new FileOutputStream(file);
			writeFromSelection(new OutputStreamWriter(stream, chset));
		}finally{
			if(stream  != null) stream.close();
		}
	}
	/**
	*ドキュメントを返します。
	*@return ドキュメント
	*/
	public LeafSyntaxDocument getDocument(){
		return (LeafSyntaxDocument)textpane.getDocument();
	}
	/**
	*編集中のテキスト領域を返します。
	*@return フォーカスのあるテキスト領域
	*/
	public LeafTextPane getTextPane(){
		return textpane;
	}
	/**
	*編集中のスクロール領域を返します。
	*@return フォーカスのあるスクロール領域
	*/
	public LeafTextScrollPane getScrollPane(){
		return scrollpane;
	}
	/**
	*オートインデントの有効/無効を設定します。
	*@param b 有効な場合true
	*/
	public void setAutoIndentEnabled(boolean b){
		isAutoIndentEnabled = b;
	}
	/**
	*オートインデント機能が有効かどうか返します。
	*@return 有効な場合true
	*/
	public boolean isAutoIndentEnabled(){
		return isAutoIndentEnabled;
	}
	/**
	*ポップアップメニューを登録します。
	*@param popup 登録するポップアップ
	*/
	@Override
	public void setComponentPopupMenu(JPopupMenu popup){
		super.setComponentPopupMenu(popup);
		if(textpanes != null){
			for(LeafTextPane textpane : textpanes){
				textpane.setComponentPopupMenu(popup);
			}
		}
	}
	/**
	*テキスト領域にカーソルを設定します。
	*@param cursor カーソル
	*/
	public void setTextCursor(Cursor cursor){
		for(LeafTextPane textpane : textpanes){
			textpane.setCursor(cursor);
		}
	}
	/**
	*ボーダーをスクロール領域に適用します。
	*@param border 設定するボーダー
	*/
	public void setViewportBorder(Border border){
		this.border = border;
		for(LeafTextScrollPane scroll : scrollpanes){
			scroll.setViewportBorder(border);
			if(border==null){
				scroll.getViewport().setOpaque(true);
				scroll.getViewport().setBackground(Color.WHITE);
			}else{
				scroll.getViewport().setOpaque(false);
			}
		}
	}
	/**
	*エディタにCatretListenerを追加します。
	*@param listener 追加するリスナー
	*/
	public void addCaretListener(CaretListener listener){
		listenerList.add(CaretListener.class, listener);
		for(LeafTextPane textpane : textpanes){
			textpane.addCaretListener(listener);
		}
	}
	/**
	*エディタからCaretListenerを削除します。
	*@param listener 削除するリスナー
	*/
	public void removeCaretListener(CaretListener listener){
		listenerList.remove(CaretListener.class, listener);
		for(LeafTextPane textpane : textpanes){
			textpane.removeCaretListener(listener);
		}
	}
	/**
	*エディタにKeyListenerを追加します。
	*@param listener 追加するリスナー
	*/
	public void addKeyListener(KeyListener listener){
		listenerList.add(KeyListener.class, listener);
		for(LeafTextPane textpane : textpanes){
			textpane.addKeyListener(listener);
		}
	}
	/**
	*エディタからKeyListenerを削除します。
	*@param listener 削除するリスナー
	*/
	public void removeKeyListener(KeyListener listener){
		listenerList.remove(KeyListener.class, listener);
		for(LeafTextPane textpane : textpanes){
			textpane.removeKeyListener(listener);
		}
	}
	/**
	*タブの最大サイズを返します。
	*@return タブサイズ
	*/
	public int getTabSize(){
		return textpane.getTabSize();
	}
	/**
	*タブの最大サイズを設定します。
	*@param size タブサイズ
	*/
	public void setTabSize(int size){
		for(LeafTextPane textpane : textpanes){
			textpane.setTabSize(size);
		}
	}
	/**
	*フォントを設定します。
	*@param font フォント
	*/
	public void setFont(Font font){
		super.setFont(font);
		for(LeafTextPane textpane : textpanes){
			textpane.setFont(font);
			textpane.setTabSize(textpane.getTabSize());
		}
	}
	/**
	*フォントを返します。
	*@return フォント
	*/
	public Font getFont(){
		if(textpane != null)return textpane.getFont();
		return super.getFont();
	}
	/**
	*ドキュメントが編集可能かどうか設定します。
	*@param b 編集可能な場合true
	*/
	public void setEditable(boolean b){
		if(isEditable == b) return;
		for(LeafTextPane textpane : textpanes){
			textpane.setEditable(b);
		}
		isEditable = b;
	}
	/**
	*ドキュメントが編集可能かどうか返します。
	*@return 編集可能な場合true
	*/
	public boolean isEditable(){
		return isEditable;
	}
	/**
	*ドキュメントが編集されているか返します。
	*@return 編集中の場合true
	*/
	public boolean isEdited(){
		return isEdited;
	}
	/**
	*フォーカスをテキスト領域に要求します。
	*/
	public boolean requestFocusInWindow(){
		return textpane.requestFocusInWindow();
	}
	/**
	*関連付けられているUndoManagerを返します。
	*@return UndoManager
	*/
	public UndoManager getUndoManager(){
		return undoManager;
	}
	/**
	*選択部分を切り取ってクリップボードにコピーします。
	*/
	public void cut(){
		textpane.cut();
	}
	/**
	*選択部分をクリップボードにコピーします。
	*/
	public void copy(){
		textpane.copy();
	}
	/**
	*選択部分にクリップボードから貼り付けます。
	*/
	public void paste(){
		textpane.paste();
	}
	/**
	*このテキスト領域の末尾に文字列を追加します。
	*@param str 追加する文字列
	*/
	public void append(String str){
		textpane.append(str);
	}
	/**
	*カーソル位置の行番号を返します。
	*@return カーソルのある行
	*/
	public int getLineNumber(){
		return scrollpane.getLineNumber();
	}
	/**
	*カーソル位置の桁番号を返します。
	*@return カーソルのある桁
	*/
	public int getColumnNumber(){
		return scrollpane.getColumnNumber();
	}
	/**
	*キャレットの位置を返します。
	*@return キャレットのある位置
	*/
	public int getCaretPosition(){
		return textpane.getCaretPosition();
	}
	/**
	*選択開始位置を返します。
	*@return 選択開始位置
	*/
	public int getSelectionStart(){
		return textpane.getSelectionStart();
	}
	/**
	*選択終了位置を返します。
	*@return 選択終了位置
	*/
	public int getSelectionEnd(){
		return textpane.getSelectionEnd();
	}
	/**
	*始点と終点を指定してテキストを選択します。
	*@param start 始点
	*@param end 終点
	*/
	public void select(int start, int end){
		textpane.select(start, end);
	}
	/**
	*テキスト全体を選択します。
	*/
	public void selectAll(){
		textpane.selectAll();
	}
	/**
	*改行コードをLFに統一して選択文字列を返します。
	*@return 選択部分の文字列
	*/
	public String getSelectedText(){
		return textpane.getSelectedText();
	}
	/**
	*選択部分を置換します。
	*@param to 置換後の文字列
	*/
	public void replaceSelection(String to){
		int start = textpane.getSelectionStart();
		textpane.replaceSelection(to);
		textpane.select(start,start+to.length());
	}
	/**
	*カーソルを指定行まで移動します。
	*@param line 1以上の行番号
	*/
	public void moveToLine(int line){
		scrollpane.scrollToLine(line);
	}
	/**
	*カーソル行の行頭の位置を返します。
	*@return 行頭までの文字数
	*/
	public int getLineStartOffset(){
		Element root = getDocument().getDefaultRootElement();
		return root.getElement(
			root.getElementIndex(textpane.getCaretPosition())
		).getStartOffset();
	}
	/**
	*指定された行の行頭の位置を返します。
	*@param line 1以上の行番号
	*@return 行頭までの文字数
	*/
	public int getLineStartOffset(int line){
		Element root = getDocument().getDefaultRootElement();
		return root.getElement(line - 1).getStartOffset();
	}
	/**
	*カーソル行の行末の位置を返します。
	*@return 行末までの文字数
	*/
	public int getLineEndOffset(){
		Element root = getDocument().getDefaultRootElement();
		return root.getElement(
			root.getElementIndex(textpane.getCaretPosition())
		).getEndOffset()-1;
	}
	/**
	*指定された行の行末の位置を返します。
	*@param line 1以上の行番号
	*@return 行末までの文字数
	*/
	public int getLineEndOffset(int line){
		Element root = getDocument().getDefaultRootElement();
		return root.getElement(line - 1).getEndOffset()-1;
	}
	/**
	*指定した行を選択します。
	*@param line 1以上の行番号
	*/
	public void selectLine(int line){
		Element root = getDocument().getDefaultRootElement();
		Element elem = root.getElement(line - 1);
		textpane.select(elem.getStartOffset(), elem.getEndOffset());
	}
	/**
	*改行コードをLFに統一してテキストを返します。
	*@return テキスト
	*/
	public String getText(){
		return textpane.getText();
	}
	/**
	*先頭行と終端行を指定して複数行をソートした配列を返します。
	*@param start 先頭行
	*@param end 終端行
	*@return 配列
	*/
	private String[] sort(int start, int end){
		if(start>end) return null;
		Document doc = getDocument();
		Element root = doc.getDefaultRootElement();
		String[] lines = new String[end-start+1];
		try{
			for(int i=start;i<=end;i++){
				Element elem = root.getElement(i);
				int s = elem.getStartOffset();
				int e = elem.getEndOffset()-1;
				lines[i-start] = doc.getText(s, e-s);
			}
			Arrays.sort(lines);
			return lines;
		}catch(BadLocationException ex){
			return null;
		}
	}
	/**
	*先頭行と終端行を指定して複数行を昇順ソートします。
	*@param start 先頭行
	*@param end 終端行(>=start)
	*/
	public void sortInAscending(int start, int end){
		String[] lines = sort(start, end);
		if(lines != null){
			StringBuilder sb = new StringBuilder(lines[0]);
			for(int i=1;i<lines.length;i++){
				sb.append("\n").append(lines[i]);
			}
			Element root = getDocument().getDefaultRootElement();
			textpane.select(
				root.getElement(start).getStartOffset(),
				root.getElement(end).getEndOffset()-1
			);
			textpane.replaceSelection(sb.toString());
		}
	}
	/**
	*先頭行と終端行を指定して複数行を降順ソートします。
	*@param start 先頭行
	*@param end 終端行(>=start)
	*/
	public void sortInDescending(int start, int end){
		String[] lines = sort(start, end);
		if(lines != null){
			StringBuilder sb = new StringBuilder(lines[lines.length-1]);
			for(int i=lines.length-2;i>=0;i--){
				sb.append("\n").append(lines[i]);
			}
			Element root = getDocument().getDefaultRootElement();
			textpane.select(
				root.getElement(start).getStartOffset(),
				root.getElement(end).getEndOffset()-1
			);
			textpane.replaceSelection(sb.toString());
		}
	}
}
