/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

import java.util.Stack;

import javax.script.ScriptException;

/**
 * S式オブジェクトを評価する評価器の実装です。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public final class Eval {
	private final Stack<Sexp> stack;
	
	/**
	 * 評価器を構築します。
	 */
	public Eval() {
		stack = new Stack<Sexp>();
	}
	
	/**
	 * 指定されたS式オブジェクトを評価して値を返します。
	 * 
	 * @param form 評価するS式
	 * @return S式の持つ値
	 */
	public Sexp eval(Sexp form) throws ScriptException {
		if(form instanceof Symbol) return symbol((Symbol)form);
		if(form instanceof Atom) return form;
		if(form instanceof List) return list((List)form);
		if(form instanceof Function) return form;
		throw new IllegalArgumentException(String.valueOf(form));
	}
	
	private Sexp symbol(Symbol symbol) throws ScriptException {
		final Sexp value = symbol.value();
		if(value != null) return value;
		throw new ScriptException("unbound symbol:" + symbol);
	}
	
	/**
	 * リストを評価します。リストの全ての要素が評価されます。
	 * 
	 * @param list 評価するリスト
	 * @return 評価された結果のS式
	 */
	private Sexp list(List list) throws ScriptException {
		final Sexp car = eval(list.car());
		if(car instanceof Lambda) {
			return lambda  (car.asLambda(), list);
		}else{
			return function(car.asFunction(), list);
		}
	}
	
	/**
	 * 組み込み関数の適用式を評価します。
	 * 
	 * @param func 適用する関数
	 * @param form 関数適用の式
	 * @return 評価された結果のS式
	 */
	private Sexp function(Function func, List form) throws ScriptException {
		final int argSize = form.size() - 1;
		final int vas = func.verifyArgumentSize(argSize);
		if(vas > 0) throw new ScriptException("too many arguments:" + form);
		if(vas < 0) throw new ScriptException("too few arguments:"  + form);
		return func.invoke(form.cdr());
	}
	
	/**
	 * ラムダ式の適用式を評価します。
	 * 
	 * @param func 適用するラムダ式
	 * @param form 関数適用の式
	 * @return 評価された結果のS式
	 */
	private Sexp lambda(Lambda func, List form) throws ScriptException {
		final int argSize = form.size() - 1;
		final List cdr = func.cdr().asList();
		final Sexp pars = cdr.car();
		final List body = cdr.cdr().asList();
		final int vas = func.verifyArgumentSize(argSize);
		if(vas > 0) throw new ScriptException("too many arguments:" + form);
		if(vas < 0) throw new ScriptException("too few arguments:"  + form);
		if(argSize == 0) return body(body);
		else return bind(pars.asList(), body, form.cdr().asList());
	}
	
	/**
	 * 古いシンボルをスタックに保存してから環境に引数を束縛し関数を適用します。
	 * 
	 * @param pars 仮引数のリスト
	 * @param body 手続き部の本体
	 * @param args 実引数のリスト
	 * @return 評価された結果のS式
	 */
	private Sexp bind(List pars, List body, List args) throws ScriptException {
		final int oldSize = stack.size();
		arguments(pars, args);
		save(pars, oldSize);
		Sexp result = body(body);
		final int sp = recover(pars, oldSize);
		for(int i = oldSize; i < sp; i++) stack.remove(oldSize);
		return result;
	}
	
	/**
	 * 実引数に格納されているシンボルを評価してスタックに保存します。
	 * 
	 * @param pars 仮引数のリスト
	 * @param args 実引数のリスト
	 */
	private void arguments(List pars, List args) throws ScriptException {
		while(!pars.isEmpty()) {
			stack.add(eval(args.car()));
			if(args.cdr() == Nil.NIL) break;
			args = args.cdr().asList();
			pars = pars.cdr().asList();
		}
	}
	
	/**
	 * 古いシンボルをスタックに保存します。
	 * 
	 * @param pars 仮引数のリスト
	 * @param sp スタックポインタ
	 */
	private void save(List pars, int sp) throws ScriptException {
		while(!pars.isEmpty()) {
			Symbol symbol = pars.car().asSymbol();
			Sexp swap = symbol.value();
			symbol.bind(stack.get(sp));
			stack.set(sp++, swap);
			if(pars.cdr() == Nil.NIL) break;
			pars = pars.cdr().asList();
		}
	}
	
	/**
	 * 古いシンボルをスタックから回復します。
	 * 
	 * @param pars 仮引数のリスト
	 * @param sp スタックポインタ
	 * @return 現在のスタックポインタ
	 */
	private int recover(List pars, int sp) throws ScriptException {
		while(!pars.isEmpty()) {
			Symbol symbol = (Symbol) pars.car();
			symbol.bind(stack.get(sp++));
			if(pars.cdr() == Nil.NIL) break;
			pars = pars.cdr().asList();
		}
		return sp;
	}
	
	/**
	 * 関数の手続き部分を実行します。
	 * 
	 * @param body 関数の手続き部
	 * @return 評価した結果のS式
	 */
	private Sexp body(List body) throws ScriptException {
		while(true) {
			Sexp result = eval(body.car());
			if(body.isTerminal()) return result;
			body = body.cdr().asList();
		}
	}

}
