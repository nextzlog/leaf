/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

/**
 * LISPで真を表すアトム型の実装です。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public final class T extends Atom{
	/**
	 * Tの唯一のインスタンスです。
	 */
	public static final T T = new T();
	
	private T(){}
	
	/**
	 * Tを示す文字列をS式の形式で返します。
	 * 
	 * @return "t"
	 */
	@Override public String toString(){
		return "t";
	}
	
	@Override public boolean isEqual(Sexp sexp){
		return sexp == this;
	}
}
