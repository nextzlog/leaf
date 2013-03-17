/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

/**
 * LISPの文字列アトム型を実装するクラスです。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public final class LispString extends Atom{
	private String value;
	
	/**
	 * 文字列値を指定してアトムを構築します。
	 * 
	 * @param value 文字列値
	 */
	public LispString(String value){
		this.value = value;
	}
	
	@Override public String toString(){
		return '"' + value + '"';
	}
	
	@Override public String toDisplayString(){
		return value;
	}
	
	@Override public boolean isEqual(Sexp sexp){
		if(sexp instanceof LispString){
			return ((LispString)sexp).value.equals(value);
		}
		return false;
	}
}
