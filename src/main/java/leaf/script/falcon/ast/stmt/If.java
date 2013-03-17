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
 * if文の構文木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public final class If extends Stmt {
	private Expr cond;
	private Stmt thenStmt;
	private Stmt elseStmt;
	private Label eslabel;
	private Label eelabel;
	
	/**
	 * 条件式とthen・else双方の木を指定して木を構築します。
	 * 
	 * @param line 行番号
	 * @param cond 条件式
	 * @param t 条件式が成立する時に実行される文
	 * @param e 条件式が成立しない時実行される文
	 */
	public If(int line, Expr cond, Stmt t, Stmt e) {
		super(line);
		this.cond = cond;
		this.thenStmt = t;
		this.elseStmt = e;
		eslabel = new ElseStartLabel();
		eelabel = new ElseEndLabel();
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
	 * @return thenの文
	 */
	public Stmt getThenStmt() {
		return thenStmt;
	}
	
	/**
	 * 条件式が成立しない時に実行される文を返します。
	 * 
	 * @return elseの式
	 */
	public Stmt getElseStmt() {
		return elseStmt;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(TokenType.IF);
		pw.print(" (");
		cond.print(pw);
		pw.print(") ");
		thenStmt.print(pw);
		if(elseStmt != null) {
			pw.print(TokenType.ELSE);
			pw.print(" ");
			elseStmt.print(pw);
		}
		pw.flush();
	}

	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		scope.addLabel(eslabel);
		scope.addLabel(eelabel);
		cond.resolve(scope);
		thenStmt.resolve(scope);
		if(elseStmt != null) {
			elseStmt.resolve(scope);
		}
		if(cond.isType(BooleanType.TYPE)) {
			return null;
		}
		throw new TypeCastException(cond);
	}
	
	@Override
	public void gencode(CodeList list) {
		cond.gencode(list);
		list.add(InstructionSet.JUMPF);
		list.add(eslabel);
		thenStmt.gencode(list);
		if(elseStmt != null) {
			list.add(InstructionSet.JUMP);
			list.add(eelabel);
			eslabel.setJump(list.size());
			elseStmt.gencode(list);
			eelabel.setJump(list.size());
		} else {
			eslabel.setJump(list.size());
			eelabel.setJump(list.size());
		}
	}

}
