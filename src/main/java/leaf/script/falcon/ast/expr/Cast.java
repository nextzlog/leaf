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
import leaf.script.falcon.vm.InstructionSet;

/**
 * 型変換の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Cast extends Expr {
	private Type type;
	private Expr expr;
	
	/**
	 * 型変換後の型と式を指定して式木を構築します。
	 * 
	 * @param line 行番号
	 * @param t 型
	 * @param e 式
	 */
	public Cast(int line, Type t, Expr e) {
		super(line);
		this.type = t;
		this.expr = e;
		setType(t);
	}
	
	/**
	 * 型変換後の型と式を指定して式木を構築します。
	 * 
	 * @param t 型
	 * @param e 式
	 */
	public Cast(Type t, Expr e) {
		super(e.getLine());
		this.type = t;
		this.expr = e;
		setType(t);
	}
	
	/**
	 * 隷下の式を返します。
	 * 
	 * @return 型変換する式
	 */
	public Expr getExpr() {
		return expr;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print("(");
		pw.print(type);
		pw.print(") (");
		expr.print(pw);
		pw.print(")");
		pw.flush();
	}
	
	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		expr.resolve(scope);
		return type;
	}

	@Override
	public void gencode(CodeList list) {
		expr.gencode(list);
		Type et = expr.getType();
		InstructionSet cmd = et.cast(type);
		if(cmd != null) list.add(cmd);
	}

}
