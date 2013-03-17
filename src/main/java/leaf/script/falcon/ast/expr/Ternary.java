/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import java.io.PrintWriter;

import leaf.script.falcon.ast.stmt.Label;
import leaf.script.falcon.ast.stmt.LabelName;
import leaf.script.falcon.ast.stmt.Scope;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.error.TypeCastException;
import leaf.script.falcon.type.BooleanType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * 三項演算子の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/26
 *
 */
public class Ternary extends App {
	/**
	 * 条件式と左右の式を指定して式木を構築します。
	 * 
	 * @param c 条件式
	 * @param l 左の式
	 * @param r 右の式
	 */
	public Ternary(Expr c, Expr l, Expr r) {
		super(c.getLine());
		addArg(c);
		addArg(l);
		addArg(r);
	}

	@Override
	public void print(PrintWriter pw) {
		getArg(0).print(pw);
		pw.print(" ? ");
		getArg(1).print(pw);
		pw.print(" : ");
		getArg(2).print(pw);
	}

	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		Type c = getArg(0).resolve(scope);
		Type a = getArg(1).resolve(scope);
		Type b = getArg(2).resolve(scope);
		
		if(c == BooleanType.TYPE) {
			return a.getCommonType(b);
		}
		throw new TypeCastException(getArg(0));
	}

	@Override
	public void gencode(CodeList list) {
		Label l1 = new Label(LabelName.EXPR);
		Label l2 = new Label(LabelName.EXPR);
		getArg(0).gencode(list);
		list.add(InstructionSet.JUMPF);
		list.add(l1);
		getArg(1).gencode(list);
		list.add(InstructionSet.JUMP);
		list.add(l2);
		l1.setJump(list.size());
		getArg(2).gencode(list);
		l2.setJump(list.size());
	}

}
