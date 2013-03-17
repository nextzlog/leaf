/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.error;

import leaf.script.falcon.ast.expr.Expr;

/**
 * 式や文が途中で終わっている場合に通知される例外です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/27
 *
 */
public class ProgramNotEnded extends SyntaxException {
	private static final long serialVersionUID = 1L;

	/**
	 * メッセージと行番号を指定して例外を構築します。
	 * 
	 * @param msg  通知されるメッセージ
	 * @param line 行番号
	 */
	public ProgramNotEnded(String msg, int line) {
		super(msg, line);
	}
	
	/**
	 * 式を指定してデフォルトメッセージの例外を構築します。
	 * 
	 * @param e 途中で終わっている式
	 */
	public ProgramNotEnded(Expr e) {
		this("expression not ended: " + e, e.getLine());
	}

}
