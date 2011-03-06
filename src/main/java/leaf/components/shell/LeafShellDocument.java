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
package leaf.components.shell;

import java.awt.Color;
import javax.swing.text.*;

/**
*シェル用のドキュメントモデルです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年2月7日
*/
public class LeafShellDocument extends DefaultStyledDocument{
	
	private final Style def, reg, err;
	private int finalized = 0;
	
	/**通常のメッセージの属性のキーです。*/
	public static final String REGULAR_MESSAGE = "regular";
	
	/**エラーメッセージの属性のキーです。*/
	public static final String ERROR_MESSAGE   = "error";
	
	/**
	*ドキュメントを生成します。
	*/
	public LeafShellDocument(){
		def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		reg = addStyle(REGULAR_MESSAGE, def);
		err = addStyle(ERROR_MESSAGE,   reg);
		StyleConstants.setForeground(err, Color.RED);
	}
	
	/**
	*最後尾にメッセージを追加し、テキストを確定します。
	*@param msg 追加するメッセージ
	*/
	public void append(String msg){
		append(msg, REGULAR_MESSAGE);
	}
	
	/**
	*メッセージの種類を指定して最後尾にメッセージを追加し、テキストを確定します。
	*@param msg 追加するメッセージ
	*@param key メッセージの種類
	*/
	public void append(String msg, String key){
		try{
			int length = getLength();
			insertString(length, msg, getStyle(key));
			finalized = length + msg.length();
		}catch(BadLocationException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	*最後尾にメッセージの候補を確定することなく追加します。
	*@param msg 追加するメッセージ
	*/
	public void exhibit(String msg){
		try{
			remove(finalized, getLength() - finalized);
			insertString(finalized, msg, getStyle(REGULAR_MESSAGE));
		}catch(BadLocationException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	*最後尾にメッセージと改行を追加し、テキストを確定します。
	*@param msg 追加するメッセージ
	*/
	public void appendln(String msg){
		append(msg + "\n", REGULAR_MESSAGE);
	}
	
	/**
	*メッセージの種類を指定して最後尾にメッセージと改行を追加し、テキストを確定します。
	*@param msg 追加するメッセージ
	*@param key メッセージの種類
	*/
	public void appendln(String msg, String key){
		append(msg + "\n", key);
	}
	
	/**
	*最後尾にエラーメッセージを追加し、テキストを確定します。
	*@param msg 追加するメッセージ
	*/
	public void error(String msg){
		append(msg, ERROR_MESSAGE);
	}
	
	/**
	*最後尾にエラーメッセージと改行を追加し、テキストを確定します。
	*@param msg 追加するメッセージ
	*/
	public void errorln(String msg){
		append(msg + "\n", ERROR_MESSAGE);
	}
	
	/**
	*最後尾の確定済みでないテキストを返します。
	*@return 確定済みでないテキスト
	*/
	public String read(){
		try{
			return getText(finalized, getLength() - finalized);
		}catch(BadLocationException ex){
			return null;
		}
	}
	
	/**
	*指定位置にテキストを挿入するときに呼び出されます。
	*@throws BadLocationException 位置が無効な場合
	*/
	public void insertString(int off, String text, AttributeSet attr)
	throws BadLocationException{
		if(off >= finalized)super.insertString(off, text, attr);
	}
	
	/**
	*指定範囲のテキストを削除するときに呼び出されます。
	*@throws BadLocationException 位置が無効な場合
	*/
	public void remove(int off, int len)
	throws BadLocationException{
		if(off >= finalized || (off == 0 && len == getLength())){
			super.remove(off, len);
			finalized = off;
		}
	}
	
	/**
	*テキストを全消去します。
	*/
	public void clear(){
		try{
			remove(0, getLength());
		}catch(BadLocationException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	*指定範囲のテキストが確定済みでないか返します。
	*@param off 範囲開始位置
	*return 確定済みの場合true
	*/
	public boolean isFinalizedAt(int off){
		return (off >= finalized);
	}
	
	/**
	*確定済みのテキストの長さを返します。
	*@return 確定済みの長さ
	*/
	public int getFinalizedLength(){
		return finalized;
	}
}
