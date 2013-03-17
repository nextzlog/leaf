/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

/**
 * LISPのシンボル型を実装するクラスです。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public final class Symbol extends Atom{
	private String name;
	private Sexp value;
	
	/**
	 * 名前を指定してシンボルを構築します。
	 * 
	 * @param name シンボルの名前
	 */
	public Symbol(String name){
		this.name = name;
	}
	
	/**
	 * シンボルにS式を束縛します。
	 * 
	 * @param value S式
	 */
	public Sexp bind(Sexp value){
		return this.value = value;
	}
	
	/**
	 * シンボルに束縛されているS式を取り除きます。
	 * 
	 * @return このシンボルへの参照
	 */
	public Sexp unbind(){
		value = null;
		return this;
	}
	
	/**
	 * シンボルの名前を返します。
	 * 
	 * @return シンボルの名前
	 */
	public String name(){
		return name;
	}
	
	/**
	 * シンボルに束縛されているS式を評価することなく返します。
	 * 
	 * @return 束縛されているS式
	 */
	public Sexp value(){
		return value;
	}
	
	@Override public String toString(){
		return name;
	}
	
	@Override public boolean isEqual(Sexp sexp){
		return sexp == this;
	}
}
