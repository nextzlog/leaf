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
import leaf.script.falcon.lex.TokenType;
import leaf.script.falcon.type.IntType;
import leaf.script.falcon.type.NumberType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * 単項演算子「-」の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Minus extends Prefix {
	/**
	 * 引数の式を指定して式木を構築します。
	 * 
	 * @param line 行番号
	 * @param e 引数の式
	 */
	public Minus(int line, Expr e) {
		super(line, e, TokenType.MINUS);
	}

	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		Type a = getArg(0).resolve(scope);
		if(a instanceof NumberType) {
			return setType(a);
		}
		throw new OperationException(this);
	}
	
	@Override
	public void gencode(CodeList list) {
		getArg(0).gencode(list);
		if(isType(IntType.TYPE)) {
			list.add(InstructionSet.INEG);
		} else {
			list.add(InstructionSet.DNEG);
		}
	}

}
