/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.doc;

import java.util.HashSet;
import java.util.List;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.*;
import javax.swing.undo.CompoundEdit;

/**
 * <!----------------------------------------------------------------------------------
 * From: http://www.koders.com/java/fid57F634657A3C7907377C708E73FB557983401673.aspx
 * License: GNU General Public License
 * ------------------------------------------------------------------------------------>
 * 
 * キーワード強調エンジンを搭載した書式付きドキュメントです。
 * キーワード・コメント・引用符色分けのほか、オートインデント機能を持ちます。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.0 作成：2010年6月22日 文字列リテラル・コメント対応：2010年9月9日
 * @see TextEditorKit
 * @see SyntaxManager
 */
@SuppressWarnings("serial")
public class SyntaxDocument extends DefaultStyledDocument {
	private final Element root;
	
	private final MutableAttributeSet normal;
	private final MutableAttributeSet keyword;
	private final MutableAttributeSet quote;
	private final MutableAttributeSet comment;
	
	private final HashSet<String> keywords;
	
	private static final String OPERANDS  = ";:{}()[]+-/%<=>!?&|^~*.,";
	private static final String QUOTATION = "\"'";
	
	private String commentStart = null, commentEnd = null, commentSingle = null;
	
	private boolean isMultiLineComment = false;
	private boolean indentEnabled = false;
	
	private boolean multiEnabled = false, singleEnabled = false;
	
	private boolean isCompoundingUndoableEdit = false;
	private CompoundEdit compoundEdit;
	
	/**
	 * ドキュメントを生成します。
	 */
	public SyntaxDocument() {
		root = getDefaultRootElement();
		putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
		
		normal = new SimpleAttributeSet();
		keyword= new SimpleAttributeSet();
		quote  = new SimpleAttributeSet();
		comment= new SimpleAttributeSet();
		
		update();
		
		keywords = new HashSet<String>();
	}
	
	/**
	 * キーワードセットを指定してドキュメントを生成します。
	 * 
	 * @param set 強調設定のセット
	 * @since 2011年2月22日
	 */
	public SyntaxDocument(KeywordSet set) {
		this();
		setKeywordSet(set);
	}
	
	/**
	 * 設定を{@link SyntaxManager}から読み込んで表示を更新します。
	 */
	public void update() {
		StyleConstants.setForeground(normal,  SyntaxManager.getColor("normal"));
		StyleConstants.setForeground(keyword, SyntaxManager.getColor("keyword"));
		StyleConstants.setForeground(quote,   SyntaxManager.getColor("quote"));
		StyleConstants.setForeground(comment, SyntaxManager.getColor("comment"));
		try{
			processChangedLines(0,getLength());
		}catch(Exception ex) {}
	}
	/**
	 * キーワードセットを設定します。
	 * このメソッドの実行時に設定されているキーワードのみ有効です。
	 * 
	 * @param set キーワードセット
	 * @since 2011年2月22日
	 */
	public void setKeywordSet(KeywordSet set) {
		if(set != null) {
			setSingleLineCommentStartDelimiter(set.getCommentLineStart());
			setMultiLineCommentStartDelimiter(set.getCommentBlockStart());
			setMultiLineCommentEndDelimiter(set.getCommentBlockEnd());
			
			setKeywords(set.getKeywords());
		}else setKeywords(null);
	}
	
	/**
	 * 強調するキーワードを設定します。
	 * このメソッドの実行時にリスト内に列挙されているキーワードのみ有効です。
	 * 
	 * @param list キーワードを列挙したリスト
	 */
	public void setKeywords(List<String> list) {
		keywords.clear();
		if(list!=null) for(String word: list) {
			keywords.add(word);
		}
	}
	
	/**
	 * 複数行コメントの開始符号を設定します。
	 * 
	 * @param commentStart コメントの開始符号
	 */
	private void setMultiLineCommentStartDelimiter(String commentStart) {
		if(commentStart == null || commentStart.isEmpty())
			 this.commentStart = null;
		else this.commentStart = commentStart;
		
		multiEnabled = (this.commentStart!=null&&this.commentEnd!=null);
	}
	
	/**
	 * 複数行コメントの終了符号を設定します。
	 * 
	 * @param commentEnd コメントの終了符号
	 */
	private void setMultiLineCommentEndDelimiter(String commentEnd) {
		if(commentEnd == null || commentEnd.isEmpty())
			 this.commentEnd = null;
		else this.commentEnd = commentEnd;
		
		multiEnabled = (this.commentStart!=null&&this.commentEnd!=null);
	}
	
	/**
	 * １行コメントの開始符号を設定します。
	 * 
	 * @param commentSingle コメントの開始符号
	 */
	private void setSingleLineCommentStartDelimiter(String commentSingle) {
		if(commentSingle == null || commentSingle.isEmpty())
			 this.commentSingle = null;
		else this.commentSingle = commentSingle;
		
		singleEnabled = (this.commentSingle!=null);
	}
	
	/**
	 * このドキュメントのインスタンスを生成するEditorKitを返します。
	 */
	public static TextEditorKit getEditorKit() {
		return new TextEditorKit() {
			@Override
			public Document createDefaultDocument() {
				return new SyntaxDocument();
			}
		};
	}
	
	/**
	 * このドキュメントが発行するUndoableEditEventを畳みこみます。
	 * 
	 * @param e イベント
	 */
	@Override
	protected void fireUndoableEditUpdate(UndoableEditEvent e) {
		if(isCompoundingUndoableEdit) compoundEdit.addEdit(e.getEdit());
		else super.fireUndoableEditUpdate(e);
	}
	
	/**
	 * ドキュメントの指定位置にコンテンツとなる文字列を挿入します。
	 * 
	 * @param offset 挿入位置
	 * @param str 挿入する文字列
	 * @param attr 挿入される文字列の属性
	 * @throws BadLocationException オフセットが無効な位置を示している場合
	 */
	@Override
	public void insertString(int offset,String str,AttributeSet attr)
	throws BadLocationException {
		this.compoundEdit = new CompoundEdit();
		this.isCompoundingUndoableEdit = true;
		
		if(str.equals("\n")) str = indent(offset);
		super.insertString(offset,str,attr);
		
		processChangedLines(offset,str.length());
		
		this.isCompoundingUndoableEdit = false;
		this.compoundEdit.end();
		fireUndoableEditUpdate(new UndoableEditEvent(this, this.compoundEdit));
		this.compoundEdit = null;
	}
	
	/**
	 * ドキュメントのコンテンツの一部を削除します。
	 * 
	 * @param offset 削除開始位置
	 * @param length 削除する文字列の長さ
	 * @throws BadLocationException オフセットが無効な位置を示している場合
	 */
	@Override
	public void remove(int offset,int length) throws BadLocationException {
		this.compoundEdit = new CompoundEdit();
		this.isCompoundingUndoableEdit = true;
		
		super.remove(offset,length);
		processChangedLines(offset, 0);
		
		this.isCompoundingUndoableEdit = false;
		this.compoundEdit.end();
		fireUndoableEditUpdate(new UndoableEditEvent(this, this.compoundEdit));
		this.compoundEdit = null;
	}
	
	/**
	 * ドキュメントの変更部分に新しい属性を割り当てます。
	 * 
	 * @param offset 変更開始位置
	 * @param length 変更文字列の長さ
	 * @throws BadLocationException オフセットが無効な位置を示している場合
	 */
	private void processChangedLines(int offset, int length) throws BadLocationException {
		final int startLine  = root.getElementIndex(offset);
		final int endLine    = root.getElementIndex(offset+length);
		
		final String content = getText(0, getLength());
		isMultiLineComment = checkMultiLineCommentBefore(content, offset);
		
		for(int i=startLine;i<=endLine;i++) {
			applyHighlightingAtLine(content,i);
		}
		if(isMultiLineComment) checkMultiLineCommentAfter(content, offset + length);
		else applyHighlightingAfter(content, offset + length);
	}
	
	/**
	 * 指定行に属性を適用します。
	 * 
	 * @param content ドキュメント内の文字列を直接指定してください
	 * @param line 適用する行
	 * @throws BadLocationException オフセットが無効な位置を示している場合
	 */
	private void applyHighlightingAtLine(String content,int line)
	throws BadLocationException {
		int startOffset = root.getElement(line).getStartOffset();
		int endOffset   = root.getElement(line).getEndOffset();
		
		final int length = endOffset - startOffset;
		
		if(endOffset >= content.length())
			endOffset = content.length() -1;
		
		if(existsMultiLineCommentEndAt  (content, startOffset, endOffset)
		|| isMultiLineComment
		|| existsMultiLineCommentStartAt(content, startOffset, endOffset)) {
			setCharacterAttributes(startOffset,endOffset-startOffset,comment,false);
			return;
		}
		
		setCharacterAttributes(startOffset,length,normal,false);
		
		if(singleEnabled) {
			final int index = content.indexOf(commentSingle,startOffset);
			if((index>=0)&&(index<endOffset)) {
				setCharacterAttributes(index,endOffset-index+1,comment,false);
				endOffset = index - 1;
			}
		}
		while(startOffset <= endOffset) {
			while(isDelimiter(content.substring(startOffset,startOffset+1))) {
				if(startOffset < endOffset) startOffset++;
				else return;
			}
			if(isQuoteDelimiter(content.substring(startOffset,startOffset+1))) {
				startOffset = checkQuoteToken(content,startOffset,endOffset);
			}else{
				startOffset = checkKeywordToken(content,startOffset,endOffset);
			}
		}
	}
	
	/**
	 * 指定位置以降を走査し、次のコメント開始符号もしくは終了符号まで属性を適用します。
	 * 
	 * @param content ドキュメント内の文字列を直接指定してください。
	 * @param offset 開始位置
	 * @since 2010年9月9日
	 */
	private void applyHighlightingAfter(String content, int offset)
	throws BadLocationException {
		int start = -1, end = -1;
		
		if(multiEnabled) {
			start  = indexOf(content, commentStart, offset);
			end    = indexOf(content, commentEnd,   offset);
		}
		
		if(start < 0) start = content.length();
		if(end   < 0) end   = content.length();
		
		int min = Math.min(start, end);
		if(min < offset) return;
		
		final int line = root.getElementIndex(offset);
		final int endLine = root.getElementIndex(min);
		for(int i=line+1; i<=endLine; i++) {
			applyHighlightingAtLine(content, i);
		}
	}
	
	/**
	 * 指定位置内の文字列を走査し、キーワードを検索します。
	 * 見つかった場合、キーワードに強調属性を適用したうえで、その位置で走査を終了します。
	 * 
	 * @param content ドキュメント内の文字列を直接指定してください
	 * @param startOffset 開始位置
	 * @param endOffset 終了位置
	 * @return 走査の終了位置
	 */
	private int checkKeywordToken(String content,int startOffset,int endOffset) {
		int endOfToken = startOffset+1;
		
		while(endOfToken<=endOffset) {
			if(isDelimiter(content.substring(endOfToken,endOfToken+1))) break;
			endOfToken++;
		}
		
		String token = content.substring(startOffset,endOfToken);
		
		if(isKeyword(token))
			setCharacterAttributes(startOffset,endOfToken-startOffset,keyword,false);
		
		return endOfToken+1;
	}
	
	/**
	 * 指定位置内の文字列を走査し、文字列リテラル属性の終了位置を検出します。
	 * 見つかった場合、リテラルに強調属性を適用し、その位置で走査を終了します。
	 * 
	 * @param content ドキュメント内の文字列を直接指定してください
	 * @param startOffset 開始位置
	 * @param endOffset 終了位置
	 * @return 走査の終了位置
	 * @since 2010年9月9日
	 */
	private int checkQuoteToken(String content, int startOffset, int endOffset) {
		final char delimiter = content.charAt(startOffset);
		final String escaped = toEscapedString(delimiter);
		
		int end = startOffset,index = content.indexOf(escaped,end+1);
		
		//エスケープ文字を飛ばす
		while((index>=0)&&(index<endOffset)) {
			end   = index + 1;
			index = content.indexOf(escaped, end);
		}
		//最終的な終了位置を検索
		index = content.indexOf(delimiter, end+1);
		if((index >= 0)&&(index < endOffset)) end = index+1; // 2011年12月26日解決
		else end = endOffset + ((endOffset == content.length()-1)? 1 : 0);
		
		setCharacterAttributes(startOffset,end-startOffset,quote,false);
		
		return end + 1;
	}
	
	/**
	 * 指定位置より前の文字列を走査し、複数行コメントの開始位置を検出します。
	 * 見つかった場合、コメントに強調属性を適用し、コメント属性であることを返します。
	 * 
	 * @param content ドキュメント内の文字列を直接指定してください
	 * @param line 開始行番号
	 * @return コメント属性に含まれる場合true
	 * @since 2010年9月9日
	 */
	private boolean checkMultiLineCommentBefore(String content, int offset) {
		if(!multiEnabled) return false;
		
		final int start  = lastIndexOf(content,commentStart,offset-1);
		if(start < 0) return false;
		
		final int end = indexOf(content,commentEnd,start);
		if((end<offset)&&(end>0)) return false;
		
		setCharacterAttributes(start, offset-start, comment,false);
		return true;
	}
	
	/**
	 * 指定位置より後の文字列を走査し、複数行コメントの終了位置を検出します。
	 * 見つかった場合、走査開始位置から後のコメントに強調属性を適用します。
	 * 
	 * @param content ドキュメント内の文字列を直接指定してください
	 * @param line 開始行番号
	 * @since 2010年9月9日
	 */
	private void checkMultiLineCommentAfter(String content, int offset) {
		if(!multiEnabled) return;
		
		final int end    = indexOf(content, commentEnd, offset);
		if(end < 0) return;
		
		final int start  = lastIndexOf(content,commentStart,end);
		if((start < 0)||(start <= offset)) {
			setCharacterAttributes(offset, end-offset, comment, false);
		}
	}
	
	/**
	 * 指定位置内の文字列を走査し、複数行コメントの開始を検出します。
	 * 
	 * @param content ドキュメント内の文字列を直接指定してください
	 * @param startOffset 開始位置
	 * @param endOffset 終了位置
	 * @return コメントの開始を検出した場合true
	 * @since 2010年9月9日
	 */
	private boolean existsMultiLineCommentStartAt
	(String content,int startOffset,int endOffset) throws BadLocationException {
		if(!multiEnabled) return false;
		
		final int index = indexOf(content, commentStart, startOffset);
		if((index >= 0)&&(index < endOffset)) return isMultiLineComment = true;
		else return false;
	}
	
	/**
	 * 指定位置内の文字列を走査し、複数行コメントの終了を検出します。
	 * 
	 * @param content ドキュメント内の文字列を直接指定してください
	 * @param startOffset 開始位置
	 * @param endOffset 終了位置
	 * @return コメントの終了を検出した場合true
	 * @since 2010年9月9日
	 */
	private boolean existsMultiLineCommentEndAt
	(String content,int startOffset,int endOffset) throws BadLocationException {
		if(!multiEnabled) return false;
		
		final int index = indexOf(content,commentEnd,startOffset);
		if((index >= 0)&&(index < endOffset)) return !(isMultiLineComment = false);
		else return false;
	}
	
	/**
	 * 指定位置内の文字列を走査し、指定された文字列を文字列リテラル外で含む
	 * 行を検出します。見つかった場合、その文字列の位置を返します。
	 * 
	 * @param content ドキュメント内の文字列を直接指定してください
	 * @param target  検出する文字列
	 * @param offset 開始位置
	 * @since 2010年9月9日
	 */
	private int indexOf(String content, String target, int offset) {
		int index;
		while((index = content.indexOf(target,offset)) >= 0) {
			Element elem = getCharacterElement(index);
			if(elem.getAttributes().isEqual(quote)) offset = index+1;
			else return index;
		}
		return index;
	}
	
	/**
	 * 指定位置内の文字列を走査し、指定された文字列を開始位置または終了位置に含む
	 * 行を検出します。見つかった場合、その文字列の位置を返します。
	 * 
	 * @param content ドキュメント内の文字列を直接指定してください
	 * @param target  検出する文字列
	 * @param offset 開始位置
	 * @since 2010年9月9日
	 */
	private int lastIndexOf(String content, String target, int offset) {
		int index;
		while((index = content.lastIndexOf(target,offset)) >= 0) {
			Element elem = getCharacterElement(index);
			if(elem.getAttributes().isEqual(quote)) offset = index-1;
			else return index;
		}
		return index;
	}
	
	/**
	 * 指定された文字列が区切り文字かどうか返します。
	 * 
	 * @param str 調べる文字列
	 * @return 区切り文字の場合true
	 */
	protected boolean isDelimiter(String str) {
		return (Character.isWhitespace(str.charAt(0)) || OPERANDS.indexOf(str) >= 0);
	}
	
	/**
	 * 指定された文字列が文字列リテラルの区切り文字かどうか返します。
	 * 
	 * @param str 調べる文字列
	 * @return 区切り文字の場合true
	 * @since 2010年9月9日
	 */
	protected boolean isQuoteDelimiter(String str) {
		return QUOTATION.indexOf(str) >= 0;
	}
	
	/**
	 * 指定された特殊文字のエスケープ表現を返します。
	 * 
	 * @param ch 特殊文字
	 * @return エスケープされた文字列
	 * @since 2010年9月9日
	 */
	protected String toEscapedString(char ch) {
		char[] value = {'\\', ch};
		return new String(value);
	}
	
	/**
	 * 指定された文字列がキーワードかどうか返します。
	 * 
	 * @param token 調べる文字列
	 * @return キーワードの場合true
	 */
	public boolean isKeyword(String token) {
		return keywords.contains(token);
	}
	
	/**
	 * ドキュメントへのコンテンツの挿入に際して自動でインデントを補完します。
	 * 
	 * @param offset 挿入位置
	 * @return インデント補完された文字列
	 * @throws BadLocationException オフセットが無効な位置を示している場合
	 */
	protected String indent(int offset) throws BadLocationException {
		if(!indentEnabled) return "\n";
		
		StringBuilder sb = new StringBuilder("\n");
		int index = root.getElement(root.getElementIndex(offset)).getStartOffset();
		
		while(index < offset) {
			char ch = getText(index++, 1).charAt(0);
			if(ch == ' ' || ch == '\t' || ch == '　') sb.append(ch);
			else break;
		}
		return sb.toString();
	}
	
	/**
	 * オートインデントを行うか設定します。
	 * このパラメータはデフォルトでfalseに設定されています。
	 * 
	 * @param enabled オートインデントを行う時はtrue
	 */
	public void setAutoIndentEnabled(boolean enabled) {
		indentEnabled = enabled;
	}
	
	/**
	 * オートインデントが設定されているか返します。
	 * このパラメータはデフォルトでfalseに設定されています。
	 * 
	 * @return オートインデント設定時はtrue
	 */
	public boolean isAutoIndentEnabled() {
		return indentEnabled;
	}

}