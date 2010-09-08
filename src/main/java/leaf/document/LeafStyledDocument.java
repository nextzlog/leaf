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

import java.awt.Color;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

import leaf.manager.*;

/**
*このクラスは、シンプルかつ強力なキーワード強調の機能を提供します。<br>
*このクラスの実装は、「SyntaxDocument.java」を参考に改良されています。
*キーワード強調のほか、簡易的なオートインデント機能を持ちます。<br>
*以前のバージョンではIMEの表示やファイル読み込み時の動作に支障がありましたが
*2010年7月22日現在このクラスは正常に動作することが確認されています。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年6月22日
*/
public class LeafStyledDocument extends DefaultStyledDocument{
	
	private final Element root;
	
	private final MutableAttributeSet normal;
	private final MutableAttributeSet keyword;
	
	private final HashSet<String> keywords;
	
	private final String OPERANDS = ";:{}()[]+-/%<=>!&|^~*";
	
	private boolean indentEnabled = false;
	
	/**
	*ドキュメントを生成します。
	*/
	public LeafStyledDocument(){
		this(null);
	}
	/**
	*強調するキーワードを指定してドキュメントを生成します。
	*@param list キーワードを列挙したArrayList
	*/
	public LeafStyledDocument(ArrayList<String> list){
		
		root = getDefaultRootElement();
		putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
		
		normal = new SimpleAttributeSet();
		StyleConstants.setForeground(normal,Color.BLACK);
		
		keyword= new SimpleAttributeSet();
		StyleConstants.setForeground(keyword,Color.BLUE);
		
		keywords = new HashSet<String>();
		setKeywords(list);
	}
	/**
	*強調するキーワードを設定します。
	*@param list キーワードを列挙したArrayList
	*/
	public void setKeywords(ArrayList<String> list){
		keywords.clear();
		if(list!=null){
			for(String word: list){
				keywords.add(word);
			}
		}
		try{
			processChangedLines(0,getLength());
		}catch(Exception ex){}
	}
	/**
	*このドキュメントクラスを実装したEditorKitを返します。
	*/
	public static LeafEditorKit getEditorKit(){
		return new LeafEditorKit(){
			public Document createDefaultDocument(){
				return new LeafStyledDocument();
			}
		};
	}
	/**
	*ドキュメントの指定位置にコンテンツとなる文字列を挿入します。
	*@param offset 挿入位置
	*@param str 挿入する文字列
	*@param attr 挿入される文字列の属性
	*@throws BadLocationException オフセットがドキュメント内の有効な位置を示していない場合
	*/
	public void insertString(int offset,String str,AttributeSet attr) throws BadLocationException{
		
		if(str.equals("\n"))
			str = indent(offset);
		
		super.insertString(offset,str,attr);
		processChangedLines(offset,str.length());
	}
	/**
	*ドキュメントのコンテンツの一部を削除します。
	*@param offset 削除開始位置
	*@param length 削除する文字列の長さ
	*@throws BadLocationException オフセットがドキュメント内の有効な位置を示していない場合
	*/
	public void remove(int offset,int length) throws BadLocationException{
		
		super.remove(offset,length);
		processChangedLines(offset,0);
	}
	/**
	*ドキュメントの変更部分に新しい属性を割り当てます。
	*@param offset 変更開始位置
	*@param length 変更文字列の長さ
	*@throws BadLocationException オフセットがドキュメント内の有効な位置を示していない場合
	*/
	protected void processChangedLines(int offset,int length) throws BadLocationException{
		
		int startLine  = root.getElementIndex(offset);
		int endLine    = root.getElementIndex(offset+length);
		
		String content = getText(0,getLength());
		
		for(int i=startLine;i<=endLine;i++){
			applyHighlightingAtLine(content,i);
		}
	}
	/**
	*指定行に属性を適用します。
	*@param content ドキュメント内の文字列を直接指定してください
	*@param line 適用する行
	*@throws BadLocationException オフセットがドキュメント内の有効な位置を示していない場合
	*/
	protected void applyHighlightingAtLine(String content,int line) throws BadLocationException{
		
		int startOffset = root.getElement(line).getStartOffset();
		int endOffset   = root.getElement(line).getEndOffset();
		
		int length = endOffset-startOffset;
		
		if(endOffset >= content.length())
			endOffset = content.length()-1;
		
		setCharacterAttributes(startOffset,length,normal,false);
		
		while(startOffset<=endOffset){
			
			while(isDelimiter(content.substring(startOffset,startOffset+1))){
				if(startOffset<endOffset)
					startOffset++;
				else
					return;
			}
			startOffset = checkKeywordToken(content,startOffset,endOffset);
		}
	}
	/**
	*指定位置内のトークンを走査し、キーワードを検索します。<br>
	*見つかった場合、キーワードに強調属性を適用したうえで、その位置で走査を終了します。
	*@param content ドキュメント内の文字列を直接指定してください
	*@param startOffset 開始位置
	*@param endOffset 終了位置
	*@return 走査の終了位置
	*/
	protected int checkKeywordToken(String content,int startOffset,int endOffset){
		
		int endOfToken = startOffset+1;
		
		while(endOfToken<=endOffset){
			
			if(isDelimiter(content.substring(endOfToken,endOfToken+1)))
				break;
			endOfToken++;
		}
		
		String token = content.substring(startOffset,endOfToken);
		
		if(isKeyword(token))
			setCharacterAttributes(startOffset,endOfToken-startOffset,keyword,false);
		
		return endOfToken+1;
	}
	/**
	指定された文字列が区切り文字かどうか返します。
	@param str 調べる文字列
	@return 区切り文字の場合true
	*/
	protected boolean isDelimiter(String str){
		
		if(Character.isWhitespace(str.charAt(0))||
			OPERANDS.indexOf(str)!=-1)
			return true;
		else
			return false;
	}
	/**
	*指定された文字列がキーワードかどうか返します。
	*@param token 調べる文字列
	*@return キーワードの場合true
	*/
	public boolean isKeyword(String token){
		return keywords.contains(token);
	}
	private final String lineseparator = System.getProperty("line.separator");
	
	/**
	*ドキュメントへのコンテンツの挿入に際して自動でインデントを補完します。
	*@param offset 挿入位置
	*@return インデント補完された文字列
	*@throws BadLocationException オフセットがドキュメント内の有効な位置を示していない場合
	*/
	protected String indent(int offset) throws BadLocationException{
		
		if(!indentEnabled) return "\n";
		
		StringBuffer buffer = new StringBuffer();
		int index = root.getElement(root.getElementIndex(offset)).getStartOffset();
		String current = "";
		
		for(;index<offset;index++){
			current = getText(index,1);
			if(!current.equals(" ")&&
				!current.equals("　")&&
					!current.equals("\t"))
						break;
			buffer.append(current);
		}
		return "\n" + buffer.toString();
	}
	
	/**
	*オートインデントを行うか設定します。<br>
	*このパラメータはデフォルトでfalseに設定されています。
	*@param enabled オートインデントを行う時はtrue
	*/
	public void setAutoIndentEnabled(boolean enabled){
		indentEnabled = enabled;
	}
	/**
	*オートインデントが設定されているか返します。<br>
	*このパラメータはデフォルトでfalseに設定されています。
	*@return オートインデント設定時はtrue
	*/
	public boolean isAutoIndentEnabled(){
		return indentEnabled;
	}
}