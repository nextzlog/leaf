/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

/**
 * LISPで空リストを表すアトム型の実装です。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public final class Nil extends Atom implements Listp{
	/**
	 * NILの唯一のインスタンスです。
	 */
	public static final Nil NIL = new Nil();
	
	private Nil(){}
	
	/**
	 * リストのCAR部を評価せずに返します。
	 * 
	 * @return NIL
	 */
	@Override public Sexp car(){
		return this;
	}
	
	/**
	 * リストのCDR部を評価せずに返します。
	 * 
	 * @return NIL
	 */
	@Override public Sexp cdr(){
		return this;
	}
	
	/**
	 * NILを示す文字列をS式の形式で返します。
	 * 
	 * @return "()"
	 */
	@Override public String toString(){
		return "()";
	}
	
	@Override public boolean isEqual(Sexp sexp){
		return sexp == this;
	}
}
