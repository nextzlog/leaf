/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.lex;

import leaf.script.falcon.ast.expr.DoubleLiteral;
import leaf.script.falcon.ast.expr.FalseLiteral;
import leaf.script.falcon.ast.expr.IntLiteral;
import leaf.script.falcon.ast.expr.Literal;
import leaf.script.falcon.ast.expr.TrueLiteral;
import leaf.script.falcon.type.BooleanType;
import leaf.script.falcon.type.DoubleType;
import leaf.script.falcon.type.IntType;
import leaf.script.falcon.type.Type;

/**
 * 字句を表すクラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public final class Token {
	private TokenType type;
	private String token;
	private int line;
	
	/**
	 * 行を指定してトークンを構築します。
	 * 
	 * @param line 行番号
	 */
	public Token(int line) {
		this.line = line;
	}
	
	/**
	 * この字句が出現する行番号を返します。
	 * 
	 * @return 行番号
	 */
	public int getLine() {
		return line;
	}
	
	/**
	 * この字句の種類を設定します。
	 * 
	 * @param type 字句の種類
	 */
	public void setType(TokenType type) {
		this.type = type;
	}
	
	/**
	 * この字句の種類を返します。
	 * 
	 * @return 字句の種類
	 */
	public TokenType getType() {
		return type;
	}
	
	/**
	 * この字句が指定された種類であるか確認します。
	 * 
	 * @param type 確認する種類
	 * @return 一致する場合true
	 */
	public boolean isType(TokenType type) {
		return this.type == type;
	}
	
	/**
	 * この字句の文字列を設定します。
	 * 
	 * @param token 字句の文字列
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * この字句の文字列を返します。
	 * 
	 * @return 字句の文字列
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * この字句を表現する文字列を返します。
	 * 
	 * @return 文字列
	 */
	@Override
	public String toString() {
		return token != null? token : type.token;
	}
	
	/**
	 * この字句をリテラルの式木に変換します。
	 * 
	 * @return 式木 リテラルでない場合null
	 */
	public Literal toLiteral() {
		switch(type) {
		case TRUE:
			return new TrueLiteral(this);
		case FALSE:
			return new FalseLiteral(this);
		case DOUBLE_LITERAL:
			return new DoubleLiteral(this);
		case INT_LITERAL:
			return new IntLiteral(this);
		default:
			return null;
		}
	}
	
	/**
	 * この字句をプリミティブ型に変換します。
	 * 
	 * @return 型 見つからない場合null
	 */
	public Type toPrimitiveType() {
		switch(type) {
		case BOOLEAN:
			return BooleanType.TYPE;
		case DOUBLE:
			return DoubleType.TYPE;
		case INT:
			return IntType.TYPE;
		}
		return null;
	}

}
