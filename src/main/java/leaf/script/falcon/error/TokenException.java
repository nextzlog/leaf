/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.error;

import leaf.script.falcon.lex.Token;
import leaf.script.falcon.lex.TokenType;

/**
 * 構文規則で予期されない字句が出現した時に通知される例外です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/27
 *
 */
public class TokenException extends SyntaxException {
	private static final long serialVersionUID = 1L;

	/**
	 * メッセージと検出された字句を指定して例外を構築します。
	 * 
	 * @param msg 通知されるメッセージ
	 * @param err 検出された字句
	 */
	public TokenException(String msg, Token err) {
		super(msg, err.getLine());
	}
	
	/**
	 * 予期された字句の種類と検出された字句を指定して例外を構築します。
	 * 
	 * @param exp 期待されていた字句の種類
	 * @param err 検出された字句
	 */
	public TokenException(TokenType exp, Token err) {
		this("expected is '" + exp + "', but found '" + err + "'", err);
	}

}
