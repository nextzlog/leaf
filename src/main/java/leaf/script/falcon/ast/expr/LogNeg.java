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
import leaf.script.falcon.type.BooleanType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * 否定演算子の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class LogNeg extends Prefix {
	/**
	 * 引数の式を指定して式木を構築します。
	 * 
	 * @param line 行番号
	 * @param e 引数の式
	 */
	public LogNeg(int line, Expr e) {
		super(line, e, TokenType.BANG);
	}
	
	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		Type t = getArg(0).resolve(scope);
		if(t == BooleanType.TYPE) {
			return setType(t);
		}
		throw new OperationException(this);
	}
	
	@Override
	public void gencode(CodeList list) {
		getArg(0).gencode(list);
		list.add(InstructionSet.NEG);
	}

}
