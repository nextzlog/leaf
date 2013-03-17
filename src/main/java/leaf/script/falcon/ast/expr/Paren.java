/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import java.io.PrintWriter;

import leaf.script.falcon.ast.stmt.Scope;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;

/**
 * 括弧を表す式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Paren extends Expr {
	private Expr expr;
	
	/**
	 * 括弧の中の式を指定して式木を構築します。
	 * 
	 * @param line 行番号
	 * @param expr 式
	 */
	public Paren(int line, Expr expr) {
		super(line);
		this.expr = expr;
	}
	
	/**
	 * 括弧の中の式を返します。
	 * 
	 * @return 式
	 */
	public Expr getExpr() {
		return expr;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print('(');
		expr.print(pw);
		pw.print(')');
		pw.flush();
	}
	
	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		return setType(expr.resolve(scope));
	}
	
	@Override
	public void gencode(CodeList list) {
		expr.gencode(list);
	}

}
