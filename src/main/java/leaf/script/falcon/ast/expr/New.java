/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;

import leaf.script.falcon.ast.stmt.Scope;
import leaf.script.falcon.error.APIException;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * コンストラクタ呼び出しの式木の実装です。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/02/19
 *
 */
public final class New extends App {
	private final Type type;
	private Constructor<?> cons;
	
	/**
	 * オブジェクト型を指定して木を構築します。
	 * 
	 * @param line 行番号
	 * @param type オブジェクト型
	 */
	public New(int line, Type type, ExprList args) {
		super(line);
		this.type = type;
	}
	
	@Override
	public void print(PrintWriter pw) {
		pw.print("new ");
		pw.print(type);
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
			throws APIException {
		Class<?>[] acs = args.getTypes();
		try {
			Class<?> c = type.getType();
			cons = c.getConstructor(acs);
		} catch (NoSuchMethodException ex) {
			throw new APIException(this);
		}
		return setType(type);
	}
	
	@Override
	public void gencode(CodeList list) {
		list.add(InstructionSet.JNEW);
		list.add(cons);
		list.add(args.size());
	}

}
