/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import java.io.PrintWriter;

import leaf.script.falcon.ast.stmt.Function;
import leaf.script.falcon.ast.stmt.Scope;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.lex.Token;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * 関数呼び出しの式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Call extends App {
	private final String funcName;
	private Function function;
	
	/**
	 * 関数名の字句と引数を指定して式木を構築します。
	 * 
	 * @param name 関数名の字句
	 * @param args 関数の引数
	 */
	public Call(Token name, ExprList args) {
		super(name.getLine(), args);
		this.funcName = name.getToken();
	}
	
	/**
	 * 関数名を返します。
	 * 
	 * @return 関数名
	 */
	public String getFuncName() {
		return funcName;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(funcName);
		pw.print(" (");
		int cnt = 0;
		for(Expr e : args) {
			if(cnt++ != 0) pw.print(", ");
			e.print(pw);
		}
		pw.print(")");
	}
	
	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		for(Expr e : args) e.resolve(scope);
		function = scope.searchFunction(this);
		if(function == null) {
			throw new ResolutionException(this);
		}
		return setType(function.getReturnType());
	}

	@Override
	public void gencode(CodeList list) {
		for(Expr e : args) e.gencode(list);
		list.add(InstructionSet.CALL);
		list.add(function.getLabel());
		list.add(InstructionSet.ADEL);
		list.add(args.size());
	}

}
