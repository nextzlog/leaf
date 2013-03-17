/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import java.io.PrintWriter;

import leaf.script.falcon.ast.expr.Call;
import leaf.script.falcon.ast.expr.ExprList;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.lex.Token;
import leaf.script.falcon.lex.TokenType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * 仮想マシンの起動時に実行される最初の文です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/25
 *
 */
public final class Premain extends Stmt {
	private Call call;
	
	/**
	 * 文を構築します。
	 */
	public Premain() {
		super(0);
		ExprList args = new ExprList();
		Token token = new Token(0);
		token.setType(TokenType.ID);
		token.setToken("main");
		call = new Call(token, args);
	}

	@Override
	public void print(PrintWriter pw) {}
	
	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		call.resolve(scope);
		return null;
	}
	
	@Override
	public void gencode(CodeList list) {
		call.gencode(list);
		list.add(InstructionSet.EXIT);
	}

}
