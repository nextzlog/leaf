/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.error;

import leaf.script.falcon.ast.expr.Expr;
import leaf.script.falcon.ast.stmt.Stmt;

/**
 * 関数や変数の解決エラーが発生した場合に通知される例外です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/24
 *
 */
public class ResolutionException extends SyntaxException {
	private static final long serialVersionUID = 1L;

	/**
	 * 指定されたメッセージで例外を構築します。
	 * 
	 * @param msg 通知するメッセージ
	 * @param line 行番号
	 */
	public ResolutionException(String msg, int line) {
		super(msg, line);
	}
	
	/**
	 * 指定された式の解決エラーを示す例外を構築します。
	 * 
	 * @param e 例外のもととなる式
	 */
	public ResolutionException(Expr e) {
		super(e.toString(), e.getLine());
	}
	
	/**
	 * 指定された式の解決エラーを示す例外を構築します。
	 * 
	 * @param s 例外のもととなる文
	 */
	public ResolutionException(Stmt s) {
		super(s.toString(), s.getLine());
	}

}
