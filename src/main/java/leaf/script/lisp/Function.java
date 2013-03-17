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
 * LISPの組み込み関数を実装する全てのクラスの基底クラスです。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public abstract class Function extends Sexp{
	protected final Bindings bindings;
	protected final Eval eval;
	
	/**
	 * 関数を構築します。
	 */
	public Function(){
		bindings = new Bindings();
		eval = new Eval();
	}
	
	/**
	 * 指定された環境で関数を構築します。
	 * 
	 * @param bindings 環境
	 */
	public Function(Bindings bindings){
		this.bindings = bindings;
		eval = new Eval();
	}
	
	@Override
	public final String toString(){
		return "<function " + name() + ">";
	}
	
	@Override
	public final boolean isEqual(Sexp sexp){
		return getClass().isInstance(sexp);
	}
	
	/**
	 * この組み込み関数の名前を返します。
	 * 
	 * @return 関数の名前
	 */
	public abstract String name();
	
	/**
	 * この組み込み関数に渡される引数の個数が正しいか確認します。
	 * 
	 * @param argSize 引数の個数
	 * @return 引数が多ければ正 少なければ負 適切であれば0
	 */
	public abstract int verifyArgumentSize(int argSize);
	
	/**
	 * この組み込み関数に引数を渡して評価します。
	 * 
	 * @param args 引数 複数個の引数はリストにまとめられる
	 * @return 関数を適用した結果のS式
	 */
	public abstract Sexp invoke(Sexp args) throws ScriptException;

}
