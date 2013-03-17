/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import java.io.PrintWriter;

import leaf.script.falcon.lex.TokenType;

/**
 * 前置単項演算子の式木の抽象クラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
public abstract class Prefix extends Unary {
	private final TokenType op;
	
	/**
	 * 引数の式と演算子の字句を指定して式木を構築します。
	 * 
	 * @param line 行番号
	 * @param e 引数の式
	 * @param op 演算子の字句
	 */
	Prefix(int line, Expr e, TokenType op) {
		super(line, e);
		this.op = op;
	}

	@Override
	public final void print(PrintWriter pw) {
		pw.print(op);
		pw.print(' ');
		getArg(0).print(pw);
		pw.flush();
	}

}
