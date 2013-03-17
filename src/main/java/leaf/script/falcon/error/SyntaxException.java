/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.error;

import javax.script.ScriptException;

import leaf.script.falcon.lex.Token;

/**
 * コンパイル時に見つかった構文エラーを通知する例外クラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class SyntaxException extends ScriptException {
	private static final long serialVersionUID = 1L;

	/**
	 * 指定されたメッセージで例外を構築します。
	 * 
	 * @param msg 通知するメッセージ
	 * @param line 行番号
	 */
	public SyntaxException(String msg, int line) {
		super(msg, null, line, 0);
	}
	
	/**
	 * 指定されたメッセージと字句で例外を構築します。
	 * 
	 * @param msg 通知するメッセージ
	 * @param token 例外
	 */
	public SyntaxException(String msg, Token token) {
		super(msg + ": " + token, null, token.getLine(), 0);
	}

}
