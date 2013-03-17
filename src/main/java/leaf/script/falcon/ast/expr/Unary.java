/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

/**
 * 単項演算子の適用の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public abstract class Unary extends App {
	/**
	 * 子の式と演算子を指定して式木を構築します。
	 * 
	 * @param line 行番号
	 * @param e 子となる式
	 */
	Unary(int line, Expr e) {
		super(line);
		addArg(e);
	}

}
