/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import leaf.script.falcon.ast.expr.Call;
import leaf.script.falcon.type.Type;

/**
 * 静的スコープを実装します。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public abstract class Scope {
	protected Scope parent;
	protected LabelList labels;
	protected DeclList decls;
	protected FunctionList funcs;
	
	/**
	 * 親を持たないスコープを構築します。
	 */
	public Scope() {
		labels = new LabelList();
		decls  = new DeclList();
		funcs  = new FunctionList();
	}
	
	/**
	 * このスコープの親スコープを返します。
	 * 
	 * @return 親となるスコープ
	 */
	public Scope getParent() {
		return parent;
	}
	
	/**
	 * このスコープの親スコープを設定します。
	 * 
	 * @param scope 親となるスコープ
	 * @return このスコープ
	 */
	public Scope setParent(Scope scope) {
		this.parent = scope;
		return this;
	}
	
	/**
	 * このスコープにラベルを追加します。
	 * 
	 * @param label 追加するラベル
	 */
	public void addLabel(Label label) {
		this.labels.add(label);
	}
	
	/**
	 * このスコープに関数定義を追加します。
	 * 
	 * @param func 追加する関数定義
	 */
	public void addFunction(Function func) {
		this.funcs.add(func);
	}
	
	/**
	 * 指定した列挙子に対応するラベルを返します。
	 * 
	 * @param ln ラベルを指定する列挙子
	 * @return lnを名前に持つラベル
	 */
	public Label searchLabel(LabelName ln) {
		return searchLabel(ln.name());
	}
	
	/**
	 * 指定した識別子に対応するラベルを返します。
	 * 
	 * @param id 識別子
	 * @return idを名前に持つラベル
	 */
	public Label searchLabel(String id) {
		for(Label l : labels) {
			if(l.getName().equals(id)) {
				return l;
			}
		}
		if(parent == null) return null;
		return parent.searchLabel(id);
	}
	
	/**
	 * 指定した識別子に対応する宣言を返します。
	 * 
	 * @param id 識別子
	 * @return idを名前に持つ変数宣言
	 */
	public Decl searchDecl(String id) {
		for(Decl d : decls) {
			if(d.getName().equals(id)) {
				return d;
			}
		}
		if(parent == null) return null;
		return parent.searchDecl(id);
	}
	
	/**
	 * 指定された関数呼び出しに適合する関数を返します。
	 * 
	 * @param call 関数呼び出し
	 * @return callに適合する関数
	 */
	public Function searchFunction(Call call) {
		for(Function f : funcs) {
			if(matches(f, call)) return f;
		}
		if(parent == null) return null;
		return parent.searchFunction(call);
	}
	
	/**
	 * 関数呼び出しと関数の組み合わせが正しいか確認します。
	 * 
	 * @param f 関数
	 * @param c 関数呼び出し
	 * @return 関数名と引数の型が適合する場合true
	 */
	private boolean matches(Function f, Call c) {
		if(f.getName().equals(c.getFuncName())) {
			return checkType(f, c);
		}
		return false;
	}
	
	/**
	 * 関数呼び出しの実引数の型が正しいか確認します。
	 * 
	 * @param f 関数
	 * @param c 関数呼び出し
	 * @return 実引数の型が適合する場合true
	 */
	private boolean checkType(Function f, Call c) {
		DeclList pars = f.getScope().decls;
		int argc = c.getArgCount();
		if(argc != pars.size()) return false;
		for(int i = 0; i < argc; i++) {
			Type a = c.getArg(i).getType();
			Type p = pars.get(i).getType();
			if(!p.isAssignable(a)) return false;
		}
		return true;
	}
	
	/**
	 * スコープを内部に持つ関数を返します。
	 * 
	 * @return 関数 見つからない場合null
	 */
	public Function getEnclosureFunction() {
		FunctionScope s = getEnclosureFunctionScope();
		return s != null? s.getOwner() : null;
	}
	
	/**
	 * スコープを内部に持つ関数スコープを返します。
	 * 
	 * @return 関数スコープ 見つからない場合null
	 * @see #getEnclosureFunction()
	 */
	public FunctionScope getEnclosureFunctionScope() {
		Scope scope = this;
		while(scope instanceof CompoundScope) {
			scope = scope.getParent();
		}
		if(scope instanceof FunctionScope) {
			return (FunctionScope) scope;
		}
		return null;
	}
	
	/**
	 * スコープを内部に持つ関数の戻り値の型を返します。
	 * 
	 * @return 関数の戻り値の型 関数がない場合null
	 * @see #getEnclosureFunction()
	 */
	public Type getFunctionReturnType() {
		Function f = getEnclosureFunction();
		return f != null? f.getReturnType() : null;
	}
	
	/**
	 * 指定された名前のクラスに対応するimport文を検索します。
	 * 
	 * @param name クラスの単純名
	 * @return 対応するimport文 存在しなければnull
	 */
	public Import searchImport(String name) {
		if(parent == null) return null;
		return parent.searchImport(name);
	}

}
