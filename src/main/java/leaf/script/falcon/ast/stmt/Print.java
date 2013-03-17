/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import java.io.PrintWriter;

import leaf.script.falcon.ast.expr.Expr;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.lex.TokenType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * print文の構文木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
public class Print extends Stmt {
private Expr expr;
	
	/**
	 * 式を指定して木を構築します。
	 * 
	 * @param line 行番号
	 * @param expr 式
	 */
	public Print(int line, Expr expr) {
		super(line);
		this.expr = expr;
	}
	
	/**
	 * 式を返します。
	 * 
	 * @return 式
	 */
	public Expr getExpr() {
		return expr;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(TokenType.PRINT);
		pw.print(" ");
		expr.print(pw);
		pw.println(";");
		pw.flush();
	}

	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		expr.resolve(scope);
		return null;
	}
	
	@Override
	public void gencode(CodeList list) {
		expr.gencode(list);
		list.add(InstructionSet.PRINT);
	}

}
