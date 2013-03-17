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
 * このクラスの継承クラスがLISPの述語関数を実装することを示します。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public abstract class Predicate extends Function{
	/**
	 * 述語関数を構築します。
	 */
	public Predicate(){}
	
	@Override
	public final Sexp invoke(Sexp args) throws ScriptException{
		return predicate(args)? T.T : F.F;
	}
	
	/**
	 * この述語関数に引数を渡して真偽値を評価します。
	 * 
	 * @param args 引数 複数個の引数はリストにまとめられる
	 * @return {@link T}であればtrue {@link F}であればfalse
	 */
	public abstract boolean predicate(Sexp args) throws ScriptException;
}