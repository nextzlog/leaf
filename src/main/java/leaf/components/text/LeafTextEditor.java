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
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;

import leaf.components.LeafSplitPane;
import leaf.document.LeafStyledDocument;
import leaf.manager.LeafCharsetManager;

/**
*高度なテキストエディタ領域の実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年11月26日
*/
public class LeafTextEditor extends LeafSplitPane {
	
	private LeafTextPane textPane;
	private LeafTextScrollPane scroll;
	
	private final UndoManager undo;
	private final LeafTextPane textPane1, textPane2;
	private final LeafTextScrollPane scroll1, scroll2;
	
	private File file;
	private String encode;
	private boolean isEdited = false;
	
	/**
	*デフォルトの文字セットを選択してエディタを生成します。
	*/
	public LeafTextEditor(){
		this(LeafCharsetManager.getCharsetNames()[0]);
	}
	/**
	*文字セット名を指定してエディタを生成します。
	*@param charset 文字セット名
	*/
	public LeafTextEditor(String charset){
		super(FIRST_IS_MAIN_COMPONENT, VERTICAL_SPLIT);
		this.encode = charset;
		
		textPane1 = textPane = new LeafTextPane();
		textPane2 = new LeafTextPane();
		
		EditorKit kit = LeafStyledDocument.getEditorKit();
		textPane1.setEditorKit(kit);
		textPane2.setEditorKit(kit);
		
		textPane1.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e){
				textPane = textPane1;
				scroll    = scroll1;
			}
		});
		textPane2.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e){
				textPane = textPane2;
				scroll   = scroll2;
			}
		});
		
		scroll1 = scroll = new LeafTextScrollPane(textPane1);
		scroll2 = new LeafTextScrollPane(textPane2);
		add(scroll1, scroll2);
		
		undo = new UndoManager();
		undo.setLimit(2000);
		
		LeafStyledDocument doc = (LeafStyledDocument)textPane1.getDocument();
		doc.addUndoableEditListener(
			new ExUndoableEditListener()
		);
		textPane2.setDocument(doc);
		doc.setAutoIndentEnabled(true);
	}
	/**
	*UndoableEditListener
	*/
	private class ExUndoableEditListener implements UndoableEditListener{
		public void undoableEditHappened(UndoableEditEvent e){
			UndoableEdit edit = e.getEdit();
			if(edit instanceof DocumentEvent
				&& ((DocumentEvent)edit).getType()
				== DocumentEvent.EventType.CHANGE) return;
			isEdited = true;
			undo.addEdit(edit);
		}
	}
	/**
	*ファイルを読み込みます。
	*/
	public void readFile() throws IOException{
		readFile(file);
	}
	/**
	*ファイルと文字セット名を指定してファイルを読み込みます。
	*@param file テキストファイル
	*@param charset 文字セット名
	*@throws IOException 読み込みエラーがあった場合
	*/
	public void readFile(File file, String charset) throws IOException{
		setEncoding(charset);
		readFile(file);
	}
	/**
	*ファイルを指定してファイルを読み込みます。
	*@param file テキストファイル
	*@throws IOException 読み込みエラーがあった場合
	*/
	public void readFile(File file) throws IOException{
		
		FileInputStream stream   = null;
		InputStreamReader reader = null;
		
		try{
			stream = new FileInputStream(this.file = file);
			reader = new InputStreamReader(stream, encode);
		
			Document doc = textPane.getDocument();
			((LeafStyledDocument)doc).setAutoIndentEnabled(false);
			
			textPane1.read(reader, null);
			textPane2.setDocument(doc = textPane1.getDocument());
			
			undo.discardAllEdits();
			doc.addUndoableEditListener(new ExUndoableEditListener());
			((LeafStyledDocument)doc).setAutoIndentEnabled(true);
			
			scroll1.init();
			scroll2.init();
			
			isEdited = false;
		}finally{
			stream.close();
			reader.close();
		}
	}
	/**
	*ファイルと文字セット名を指定して選択部分にファイルを読み込みます。
	*@param file テキストファイル
	*@param charset 文字セット名
	*@throws IOException 読み込みエラーがあった場合
	*/
	public void readFileIntoSelection(File file, String charset) throws IOException{
		
		FileInputStream stream     = null;
		InputStreamReader isreader = null;
		BufferedReader    breader  = null;
		
		try{
			stream   = new FileInputStream(file);
			isreader = new InputStreamReader(stream, charset);
			breader  = new BufferedReader(isreader);
			
			StringBuffer buf = new StringBuffer(1000);
			String line;
			while((line = breader.readLine()) != null){
				buf.append(line + "\n");
			}
			textPane.replaceSelection(buf.toString());
		}finally{
			breader.close();
			isreader.close();
			stream.close();
		}
	}
	/**
	*ファイルを書き込みます。
	*/
	public void writeFile() throws IOException{
		writeFile(file);
	}
	/**
	*ファイルと文字セット名を指定してテキストファイルに書き込みます。
	*@param file テキストファイル
	*@param charset 文字セット名
	*@throws IOException 書き込みエラーがあった場合
	*/
	public void writeFile(File file, String charset) throws IOException{
		setEncoding(charset);
		writeFile(file);
	}
	/**
	*ファイルを指定してテキストファイルに書き込みます。
	*@param file テキストファイル
	*@throws IOException 書き込みエラーがあった場合
	*/
	public void writeFile(File file) throws IOException{
		
		FileOutputStream stream     = null;
		OutputStreamWriter oswriter = null;
		BufferedWriter bwriter      = null;
		
		try{
			stream   = new FileOutputStream(this.file = file);
			oswriter = new OutputStreamWriter(stream, encode);
			bwriter  = new BufferedWriter(oswriter);
			
			int start = 0, end = 0;
			final String text = textPane.getText().replaceAll("(\r\n|\r)","\n");
			final String ls   = LeafCharsetManager.getLineSeparator();
			
			while((end = text.indexOf("\n", start)) >= 0){
				bwriter.write(text.substring(start, end) + ls);
				bwriter.flush();
				start = end + 1;
			}
			if(start < text.length())
				bwriter.write(text.substring(start, text.length()));
			isEdited = false;
		}finally{
			bwriter.close();
			oswriter.close();
			stream.close();
		}
	}
	/**
	*ファイルと文字セット名を指定してテキストファイルに選択部分を書き込みます。
	*@param file テキストファイル
	*@param charset 文字セット名
	*@throws IOException 書き込みエラーがあった場合
	*/
	public void writeFileFromSelection(File file, String charset) throws IOException {
		
		FileOutputStream stream     = null;
		OutputStreamWriter oswriter = null;
		BufferedWriter bwriter      = null;
		
		try{
			stream   = new FileOutputStream(file);
			oswriter = new OutputStreamWriter(stream, charset);
			bwriter  = new BufferedWriter(oswriter);
			
			int start = 0, end = 0;
			final String text = textPane.getSelectedText().replaceAll("(\r\n|\r)","\n");
			final String ls   = LeafCharsetManager.getLineSeparator();
			
			while((end = text.indexOf("\n", start)) >= 0){
				bwriter.write(text.substring(start, end) + ls);
				bwriter.flush();
				start = end + 1;
			}
			if(start < text.length())
				bwriter.write(text.substring(start, text.length()));
		}finally{
			bwriter.close();
			oswriter.close();
			stream.close();
		}
	}
	/**
	*編集中のファイルを返します。
	*@return ファイル
	*/
	public File getFile(){
		return (file!=null)? file.getAbsoluteFile() : null;
	}
	/**
	*ファイルを設定します。
	*@param file ファイル
	*/
	public void setFile(File file){
		this.file = file;
	}
	/**
	*ファイルパスを返します。
	*@return ファイルの絶対パス
	*/
	public String getFilePath(){
		return (file!=null)? file.getAbsolutePath() : null;
	}
	/**
	*文字セットを設定します。
	*@param charset 文字セット名
	*/
	public void setEncoding(String charset){
		this.encode = charset;
	}
	/**
	*文字セット名を返します。
	*@return 文字セット名
	*/
	public String getEncoding(){
		return encode;
	}
	/**
	*上側のテキスト領域を返します。
	*@return 画面分割時上側のテキスト領域
	*/
	protected LeafTextPane getTopTextPane(){
		return textPane1;
	}
	/**
	*下側のテキスト領域を返します。
	*@return 画面分割時下側のテキスト領域
	*/
	protected LeafTextPane getBottomTextPane(){
		return textPane2;
	}
	/**
	*編集中のテキスト領域を返します。
	*@return フォーカスのあるテキスト領域
	*/
	public LeafTextPane getTextPane(){
		return textPane;
	}
	/**
	*上側のスクロール領域を返します。
	*@return 画面分割時上側のスクロール領域
	*/
	protected LeafTextScrollPane getTopScrollPane(){
		return scroll1;
	}
	/**
	*下側のスクロール領域を返します。
	*@return 画面分割時下側のスクロール領域
	*/
	protected LeafTextScrollPane getBottomScrollPane(){
		return scroll2;
	}
	/**
	*編集中のスクロール領域を返します。
	*@return フォーカスのあるスクロール領域
	*/
	public LeafTextScrollPane getScrollPane(){
		return scroll;
	}
	/**
	*タブの最大サイズを返します。
	*@return タブサイズ
	*/
	public int getTabSize(){
		return textPane.getTabSize();
	}
	/**
	*タブの最大サイズを設定します。
	*@param size タブサイズ
	*/
	public void setTabSize(int size){
		textPane1.setTabSize(size);
		textPane2.setTabSize(size);
	}
	/**
	*フォントを設定します。
	*@param font フォント
	*/
	public void setTextFont(Font font){
		textPane1.setFont(font);
		textPane2.setFont(font);
		setTabSize(textPane1.getTabSize());
		scroll1.init();
		scroll2.init();
	}
	/**
	*フォントを返します。
	*@return フォント
	*/
	public Font getTextFont(){
		return textPane.getFont();
	}
	/**
	*ファイルが編集されているか返します。
	*@return 編集中の場合true
	*/
	public boolean isEdited(){
		return isEdited;
	}
	/**
	*フォーカスをテキスト領域に要求します。
	*/
	public boolean requestFocusInWindow(){
		return textPane.requestFocusInWindow();
	}
	/**
	*関連付けられているUndoManagerを返します。
	*@return UndoManager
	*/
	public UndoManager getUndoManager(){
		return undo;
	}
	/**
	*テキストの編集操作を元に戻します。
	*/
	public void undo(){
		if(undo.canUndo()) undo.undo();
	}
	/**
	*テキストで元に戻した内容をやり直します。
	*/
	public void redo(){
		if(undo.canRedo()) undo.redo();
	}
	/**
	*テキストが元に戻せるか返します。
	*@return 元に戻す内容がある場合true
	*/
	public boolean canUndo(){
		return undo.canUndo();
	}
	/**
	*テキストで元に戻した内容をやり直せるか返します。
	*@return やり直す内容がある場合true
	*/
	public boolean canRedo(){
		return undo.canRedo();
	}
	/**
	*テキストを返します。
	*@return テキスト全体
	*/
	public String getText(){
		return textPane.getText();
	}
	
	/**
	*選択されている文字列を返します。
	*@return 選択部分の文字列
	*/
	public String getSelectedText(){
		return textPane.getSelectedText();
	}
	/**
	*選択部分を置換します。
	*@param to 置換後の文字列
	*/
	public void replaceSelection(String to){
		int start = textPane.getSelectionStart();
		textPane.replaceSelection(to);
		textPane.select(start,start+to.length());
	}
	/**
	*選択部分を切り取ってクリップボードにコピーします。
	*/
	public void cut(){
		textPane.cut();
	}
	/**
	*選択部分をクリップボードにコピーします。
	*/
	public void copy(){
		textPane.copy();
	}
	/**
	*選択部分にクリップボードから貼り付けます。
	*/
	public void paste(){
		textPane.paste();
	}
	/**
	*選択部分を削除します。
	*/
	public void removeSelection(){
		textPane.replaceSelection("");
	}
	/**
	*カーソル行を削除します。
	*/
	public void removeLine(){
		textPane.removeLine();
	}
	/**
	*カーソル前の文字列を削除します。
	*/
	public void backspace(){
		int pos = textPane.getCaretPosition();
		if(textPane.getSelectedText()==null)textPane.select(pos-1,pos);
		replaceSelection("");
	}
	/**
	*テキスト全体を選択します。
	*/
	public void selectAll(){
		textPane.selectAll();
	}
	/**
	*カーソル行を選択します。
	*/
	public void selectLine(){
		textPane.selectLine();
	}
	/**
	*行頭まで選択します。
	*/
	public void selectToLineStart(){
		textPane.setSelectionStart(textPane.getLineStartOffset());
	}
	/**
	*行末まで選択します。
	*/
	public void selectToLineEnd(){
		textPane.setSelectionEnd(textPane.getLineEndOffset());
	}
	/**
	*テキスト先頭まで選択します。
	*/
	public void selectToTop(){
		textPane.setSelectionStart(0);
	}
	/**
	*テキスト末尾まで選択します。
	*/
	public void selectToEnd(){
		textPane.setSelectionEnd(textPane.getText().length());
	}
	/**
	*カーソルをテキスト先頭まで移動します。
	*/
	public void moveToTop(){
		textPane.setCaretPosition(0);
	}
	/**
	*カーソルをテキスト末尾まで移動します。
	*/
	public void moveToEnd(){
		textPane.setCaretPosition(textPane.getText().length());
	}
	/**
	*カーソルを行頭まで移動します。
	*/
	public void moveToLineTop(){
		textPane.setCaretPosition(textPane.getLineStartOffset());
	}
	/**
	*カーソルを行末まで移動します。
	*/
	public void moveToLineEnd(){
		textPane.setCaretPosition(textPane.getLineEndOffset());
	}
	/**
	*カーソルを指定行まで移動します。
	*@param line 1以上の行番号
	*/
	public void moveToLine(int line){
		scroll.scrollToLine(line);
	}
	/**
	*カーソルを前のページまで移動します。
	*/
	public void moveToPreviousPage(){
		Element root = textPane.getDocument().getDefaultRootElement();
		scroll.scrollToLine(1 + root.getElementIndex(textPane.viewToModel(
			new Point(0, scroll.getVerticalScrollBar().getValue()
				- scroll.getViewport().getHeight()
			)
		)));
	}
	/**
	*カーソルを次のページまで移動します。
	*/
	public void moveToNextPage(){
		Element root = textPane.getDocument().getDefaultRootElement();
		scroll.scrollToLine(1 + root.getElementIndex(textPane.viewToModel(
			new Point(0, scroll.getVerticalScrollBar().getValue()
				+ scroll.getViewport().getHeight()
			)
		)));
	}
}
