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
import leaf.script.falcon.type.NumberType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * 演算子「<」の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Lt extends Binary {
	/**
	 * 左右の式を指定して式木を構築します。
	 * 
	 * @param op 演算子
	 * @param l 左の式
	 * @param r 右の式
	 */
	public Lt(Token op, Expr l, Expr r) {
		super(op, l, r);
	}
	
	public Type resolve(Scope scope)
			throws ResolutionException {
		Type a = getArg(0).resolve(scope);
		Type b = getArg(1).resolve(scope);
		Type c = a.getCommonType(b);
		if(c instanceof NumberType) {
			cast(0, c);
			cast(1, c);
			return setType(BooleanType.TYPE);
		}
		throw new OperationException(this);
	}
	
	@Override
	public void gencode(CodeList list) {
		getArg(0).gencode(list);
		getArg(1).gencode(list);
		if(getArg(0).isType(IntType.TYPE)) {
			list.add(InstructionSet.ILT);
		} else {
			list.add(InstructionSet.DLT);
		}
	}

}
