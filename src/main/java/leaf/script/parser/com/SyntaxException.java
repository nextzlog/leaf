/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.com;

import javax.script.ScriptException;

import leaf.script.parser.lex.Token;

/**
 * 構文解析器において検出された構文エラーを通知する例外です。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public class SyntaxException extends ScriptException {

	private static final long serialVersionUID = 1L;
	private final Token token;

	/**
	 * 構文エラーの場所を示す字句を指定して例外を構築します。
	 * 
	 * @param token 例外の原因となった字句
	 */
	public SyntaxException(Token token) {
		super("syntax error : " + token);
		this.token = token;
	}
	
	/**
	 * 指定されたメッセージで、構文エラーを示す例外を構築します。
	 * 
	 * @param token 例外の原因となった字句
	 * @param msg   例外のメッセージ
	 */
	public SyntaxException(Token token, String msg) {
		super(msg);
		this.token = token;
	}
	
	/**
	 * 検出された構文エラーの場所を示す字句を返します。
	 * 
	 * @return 例外の原因となった字句
	 */
	public Token getErrorToken() {
		return token;
	}
}
