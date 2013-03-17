/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import leaf.script.falcon.error.TypeCastException;
import leaf.script.falcon.type.Type;

/**
 * 演算子と関数適用の式木の抽象クラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public abstract class App extends Expr {
	protected ExprList args;
	
	/**
	 * 式木を構築します。
	 * 
	 * @param line 字句
	 */
	public App(int line) {
		super(line);
		this.args = new ExprList();
	}
	
	/**
	 * 引数のリストを指定して式木を構築します。
	 * 
	 * @param line 行番号
	 * @param args 引数のリスト
	 */
	public App(int line, ExprList args) {
		super(line);
		this.args = args;
	}
	
	/**
	 * 引数の木を追加します。
	 * 
	 * @param expr 追加する引数
	 */
	public void addArg(Expr expr) {
		args.add(expr);
	}
	
	/**
	 * 指定された番号の引数を返します。
	 * 
	 * @param n 番号
	 * @return 引数のリスト
	 */
	public Expr getArg(int n) {
		return args.get(n);
	}
	
	/**
	 * 引数の個数を返します。
	 * 
	 * @return 引数の個数
	 */
	public int getArgCount() {
		return args.size();
	}
	
	/**
	 * 引数に型変換の式木を挿入します。
	 * 
	 * @param n 引数の番号
	 * @param type 型
	 * @throws TypeCastException 型変換エラー
	 */
	public void cast(int n, Type type)
			throws TypeCastException {
		args.set(n, args.get(n).cast(type));
	}

}
