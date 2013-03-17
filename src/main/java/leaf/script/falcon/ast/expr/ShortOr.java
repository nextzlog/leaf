/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import leaf.script.falcon.ast.stmt.Label;
import leaf.script.falcon.ast.stmt.LabelName;
import leaf.script.falcon.ast.stmt.Scope;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.lex.Token;
import leaf.script.falcon.type.BooleanType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * 短絡論理和演算子の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/26
 *
 */
public class ShortOr extends Binary {
	/**
	 * 左右の式を指定して式木を構築します。
	 * 
	 * @param op 演算子
	 * @param l 左の式
	 * @param r 右の式
	 */
	public ShortOr(Token op, Expr l, Expr r) {
		super(op, l, r);
	}
	
	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		getArg(0).resolve(scope);
		getArg(1).resolve(scope);
		cast(0, BooleanType.TYPE);
		cast(1, BooleanType.TYPE);
		return BooleanType.TYPE;
	}

	@Override
	public void gencode(CodeList list) {
		Label l1 = new Label(LabelName.EXPR);
		Label l2 = new Label(LabelName.EXPR);
		getArg(0).gencode(list);
		list.add(InstructionSet.JUMPT);
		list.add(l1);
		getArg(1).gencode(list);
		list.add(InstructionSet.JUMP);
		list.add(l2);
		l1.setJump(list.size());
		list.add(InstructionSet.PUSH);
		list.add(Boolean.TRUE);
		l2.setJump(list.size());
	}

}
