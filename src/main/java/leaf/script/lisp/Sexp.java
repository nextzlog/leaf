/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

import javax.script.ScriptException;

/**
 * S式を表現する抽象クラスです。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public abstract class Sexp{
	/**
	 * S式を構築します。
	 */
	public Sexp(){}
	
	/**
	 * このS式をアトム型にキャストして返します。
	 * 
	 * @return アトム型にキャストされたS式
	 * @throws ScriptException アトムでない場合
	 */
	public final Atom asAtom() throws ScriptException{
		if(this instanceof Atom) return (Atom) this;
		throw new ScriptException("not atom:" + this);
	}
	
	/**
	 * このS式がアトムである場合に、その真偽値を返します。
	 * 
	 * @return 真偽値
	 * @throws ScriptException {@link T}もしくは{@link Nil}でない場合
	 */
	public final boolean asBoolean() throws ScriptException{
		if(this == T.T) return true;
		if(this == F.F) return false;
		throw new ScriptException("not boolean:" + this);
	}
	
	/**
	 * このS式をリスト型にキャストして返します。
	 * 
	 * @return リスト型にキャストされたS式
	 * @throws ScriptException リストでない場合
	 */
	public final List asList() throws ScriptException{
		if(this instanceof List) return (List) this;
		throw new ScriptException("not list:" + this);
	}
	
	/**
	 * このS式をラムダ式にキャストして返します。
	 * 
	 * @return ラムダ式にキャストされたS式
	 * @throws ScriptException ラムダ式でない場合
	 */
	public final Lambda asLambda() throws ScriptException{
		if(this instanceof Lambda) return (Lambda) this;
		throw new ScriptException("not lambda:" + this);
	}
	
	/**
	 * このS式を組み込み関数型にキャストして返します。
	 * 
	 * @return 組み込み関数型にキャストされたS式
	 * @throws ScriptException 組み込み関数でない場合
	 */
	public final Function asFunction() throws ScriptException{
		if(this instanceof Function) return (Function) this;
		throw new ScriptException("not function:" + this);
	}
	
	/**
	 * このS式を数値アトム型にキャストして返します。
	 * 
	 * @return 数値アトム型にキャストされたS式
	 * @throws ScriptException 数値アトムでない場合
	 */
	public final Num asNum() throws ScriptException{
		if(this instanceof Num) return (Num) this;
		throw new ScriptException("not number:" + this);
	}
	
	/**
	 * このS式をシンボル型にキャストして返します。
	 * 
	 * @return シンボル型にキャストされたS式
	 * @throws ScriptException シンボル型でない場合
	 */
	public final Symbol asSymbol() throws ScriptException{
		if(this instanceof Symbol) return (Symbol) this;
		throw new ScriptException("not symbol:" + this);
	}
	
	/**
	 * このオブジェクトを表す文字列をS式のフォーマットで返します。
	 * 
	 * @return S式
	 */
	public abstract String toString();
	
	/**
	 * 標準出力に出力するのに適した形式で文字列を返します。
	 * 
	 * @return 文字列
	 */
	public String toDisplayString(){
		return toString();
	}
	
	/**
	 * この式が指定した式と静的に等価であるか確認します。
	 * メソッド実行により式が評価されることはありません。
	 */
	public abstract boolean isEqual(Sexp sexp);
}
