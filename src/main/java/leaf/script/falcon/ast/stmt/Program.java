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
 * プログラム全体の構文木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
public class Program extends Stmt {
	private ProgramScope scope;
	private Premain premain;
	
	/**
	 * プログラムを構築します。
	 */
	public Program() {
		super(0);
		scope = new ProgramScope();
		premain = new Premain();
	}
	
	/**
	 * プログラム全体のスコープを返します。
	 * 
	 * @return スコープ
	 */
	public ProgramScope getScope() {
		return scope;
	}
	
	/**
	 * プログラムにimport文を追加します。
	 * 
	 * @param imp 追加するimport文
	 */
	public void addImport(Import imp) {
		scope.addImport(imp);
	}
	
	/**
	 * プログラムに関数定義を追加します。
	 * 
	 * @param func 追加する関数定義
	 */
	public void addFunction(Function func) {
		scope.addFunction(func);
	}
	
	@Override
	public void print(PrintWriter pw) {
		for(Import i : scope.imports) {
			i.print(pw);
		}
		for(Function f : scope.funcs) {
			f.print(pw);
		}
		pw.flush();
	}
	
	@Override
	public Type resolve(Scope scope)
		throws ResolutionException {
		premain.resolve(this.scope);
		for(Import i : this.scope.imports) {
			i.resolve(scope);
		}
		for(Function f : this.scope.funcs) {
			f.resolve(this.scope);
		}
		return null;
	}
	
	@Override
	public void gencode(CodeList list) {
		premain.gencode(list);
		for(Function f : scope.funcs) {
			f.gencode(list);
		}
	}

}
