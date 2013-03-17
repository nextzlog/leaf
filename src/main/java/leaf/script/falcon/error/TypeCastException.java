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
 * 代入式や型変換の型が不正な場合に通知される例外です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2013/02/15
 *
 */
public class TypeCastException extends OperationException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 指定されたメッセージで例外を構築します。
	 * 
	 * @param msg 通知するメッセージ
	 * @param line 行番号
	 */
	public TypeCastException(String msg, int line) {
		super(msg, line);
	}
	
	/**
	 * 指定された式木に対応する例外を構築します。
	 * 
	 * @param e 例外の原因となる式
	 */
	public TypeCastException(Expr e) {
		super(e.toString(), e.getLine());
	}

}
