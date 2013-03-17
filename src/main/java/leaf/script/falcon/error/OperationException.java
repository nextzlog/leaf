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
 * 演算が構文規則に違反している場合に通知される例外です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
public class OperationException extends ResolutionException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 指定されたメッセージで例外を構築します。
	 * 
	 * @param msg 通知するメッセージ
	 * @param line 行番号
	 */
	public OperationException(String msg, int line) {
		super(msg, line);
	}
	
	/**
	 * 指定された式木に対応する例外を構築します。
	 * 
	 * @param e 例外の原因となる式
	 */
	public OperationException(Expr e) {
		super(e.toString(), e.getLine());
	}

}
