/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import leaf.script.falcon.ast.stmt.Decl;
import leaf.script.falcon.ast.stmt.Local;
import leaf.script.falcon.ast.stmt.Param;
import leaf.script.falcon.ast.stmt.Scope;
import leaf.script.falcon.error.OperationException;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.lex.Token;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * 代入演算子の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Assign extends Binary {
	private Decl decl;
	
	/**
	 * 左右の式を指定して式木を構築します。
	 * 
	 * @param op 演算子
	 * @param l 左の式
	 * @param r 右の式
	 */
	public Assign(Token op, Expr l, Expr r) {
		super(op, l, r);
	}
	
	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		Expr l = getArg(0);
		Expr r = getArg(1);
		l.resolve(scope);
		r.resolve(scope);
		if(l instanceof Id) {
			Id id = (Id) l;
			cast(1, l.getType());
			decl = scope.searchDecl(id.getId());
			return l.getType();
		}
		throw new OperationException(this);
	}
	
	@Override
	public void gencode(CodeList list) {
		getArg(1).gencode(list);
		if(decl instanceof Param) {
			list.add(InstructionSet.AASSN);
			list.add(decl.getIndex());
		}
		else if(decl instanceof Local) {
			list.add(InstructionSet.LASSN);
			list.add(decl.getIndex());
		}
	}

}
