/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import java.io.PrintWriter;

import leaf.script.falcon.lex.Token;
import leaf.script.falcon.lex.TokenType;

/**
 * 二項演算子の適用の式木の抽象クラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public abstract class Binary extends App {
	private final TokenType op;
	
	/**
	 * 演算子と左右の式を指定して式木を構築します。
	 * 
	 * @param op 演算子の字句
	 * @param l 左の式
	 * @param r 右の式
	 */
	public Binary(Token op, Expr l, Expr r) {
		super(op.getLine());
		addArg(l);
		addArg(r);
		this.op = op.getType();
	}
	
	@Override
	public final void print(PrintWriter pw) {
		getArg(0).print(pw);
		pw.printf(" %s ", op);
		getArg(1).print(pw);
		pw.flush();
	}

}
