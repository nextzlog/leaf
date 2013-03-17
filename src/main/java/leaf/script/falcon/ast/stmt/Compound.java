/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import java.io.PrintWriter;

import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;

/**
 * 複合文の構文木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Compound extends Stmt {
	private StmtList body;
	private CompoundScope scope;
	
	/**
	 * 複合文の木を構築します。
	 * 
	 * @param line 行番号
	 */
	public Compound(int line) {
		super(line);
		body = new StmtList();
		scope = new CompoundScope();
	}
	
	/**
	 * 複合文の文のリストを返します。
	 * 
	 * @return 隷下の文
	 */
	public StmtList getBody() {
		return body;
	}
	
	/**
	 * 複合文に文を追加します。
	 * 
	 * @param stmt 追加する文
	 */
	public void addStmt(Stmt stmt) {
		body.add(stmt);
	}
	
	/**
	 * 複合文のスコープを返します。
	 * 
	 * @return スコープ
	 */
	public CompoundScope getScope() {
		return scope;
	}
	
	/**
	 * 複合文に変数宣言を追加します。
	 * 
	 * @param local
	 */
	public void addLocal(Local local) {
		scope.addLocal(local);
	}
	
	@Override
	public void print(PrintWriter pw) {
		pw.println("{");
		for(Decl d : scope.decls) {
			pw.print(d.getType());
			pw.print(' ');
			pw.print(d.getName());
			pw.println(';');
		}
		for(Stmt s : body) s.print(pw);
		pw.println("}");
		pw.flush();
	}

	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		for(Stmt s : body) {
			s.resolve(this.scope);
		}
		return null;
	}
	
	@Override
	public void gencode(CodeList list) {
		for(Stmt s : body) {
			s.gencode(list);
		}
	}

}
