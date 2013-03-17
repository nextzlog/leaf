/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import java.io.PrintWriter;

import leaf.script.falcon.ast.expr.Expr;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.error.TypeCastException;
import leaf.script.falcon.lex.TokenType;
import leaf.script.falcon.type.BooleanType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * while文の構文木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class While extends Stmt {
	private Expr cond;
	private Stmt body;
	private Label lslabel;
	private Label lelabel;
	
	/**
	 * 条件式と本体の木を指定して木を構築します。
	 * 
	 * @param line 行番号
	 * @param cond 条件式
	 * @param body 条件式が成立する時繰り返す文
	 */
	public While(int line, Expr cond, Stmt body) {
		super(line);
		this.cond = cond;
		this.body = body;
		lslabel = new LoopStartLabel();
		lelabel = new LoopEndLabel();
	}
	
	/**
	 * 条件式を返します。
	 * 
	 * @return 条件式
	 */
	public Expr getCond() {
		return cond;
	}
	
	/**
	 * 条件式が成立する時に実行される文を返します。
	 * 
	 * @return 繰り返される文
	 */
	public Stmt getBody() {
		return body;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(TokenType.WHILE);
		pw.print(" (");
		cond.print(pw);
		pw.print(") ");
		body.print(pw);
		pw.flush();
	}

	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		scope.addLabel(lslabel);
		scope.addLabel(lelabel);
		cond.resolve(scope);
		body.resolve(scope);
		if(cond.isType(BooleanType.TYPE)) {
			return null;
		}
		throw new TypeCastException(cond);
	}

	@Override
	public void gencode(CodeList list) {
		lslabel.setJump(list.size());
		cond.gencode(list);
		list.add(InstructionSet.JUMPF);
		list.add(lelabel);
		body.gencode(list);
		list.add(InstructionSet.JUMP);
		list.add(lslabel);
		lelabel.setJump(list.size());
	}

}
