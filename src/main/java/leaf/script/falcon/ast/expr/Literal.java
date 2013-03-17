/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import java.io.PrintWriter;

import leaf.script.falcon.ast.stmt.Scope;
import leaf.script.falcon.lex.Token;
import leaf.script.falcon.type.Type;

/**
 * 整数や真偽地などリテラルの式木の抽象クラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
public abstract class Literal extends Expr {
	private Token token;
	
	/**
	 * 対応する字句と型を指定して式木を構築します。
	 * 
	 * @param e 字句
	 * @param t 型
	 */
	public Literal(Token e, Type t) {
		super(e.getLine());
		this.token = e;
		setType(t);
	}
	
	/**
	 * このリテラルの字句をそのまま出力します。
	 * 
	 * @param pw 書きこむPrintWriter
	 */
	@Override
	public final void print(PrintWriter pw) {
		pw.print(token);
		pw.flush();
	}
	
	/**
	 * このリテラルの型を返すだけで何も行いません。
	 * 
	 * @param scope スコープは無視されます
	 * @return リテラルの一意に定まる型
	 */
	@Override
	public final Type resolve(Scope scope) {
		return getType();
	}

}
