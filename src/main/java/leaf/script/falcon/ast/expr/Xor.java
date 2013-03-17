/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import leaf.script.falcon.ast.stmt.Scope;
import leaf.script.falcon.error.OperationException;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.lex.Token;
import leaf.script.falcon.type.BooleanType;
import leaf.script.falcon.type.IntType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

public class Xor extends Binary {
	/**
	 * 左右の式を指定して式木を構築します。
	 * 
	 * @param op 演算子
	 * @param l 左の式
	 * @param r 右の式
	 */
	public Xor(Token op, Expr l, Expr r) {
		super(op, l, r);
	}
	
	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		Type a = getArg(0).resolve(scope);
		Type b = getArg(1).resolve(scope);
		if(a == BooleanType.TYPE
		&& b == BooleanType.TYPE) {
			return setType(BooleanType.TYPE);
		}
		if(a == IntType.TYPE && b == IntType.TYPE) {
			return setType(IntType.TYPE);
		}
		throw new OperationException(this);
	}

	@Override
	public void gencode(CodeList list) {
		if(isType(BooleanType.TYPE)) {
			getArg(0).gencode(list);
			getArg(1).gencode(list);
			list.add(InstructionSet.XOR);
		} else {
			getArg(0).gencode(list);
			getArg(1).gencode(list);
			list.add(InstructionSet.IXOR);
		}
	}

}
