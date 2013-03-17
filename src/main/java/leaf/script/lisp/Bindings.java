/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

import java.util.HashMap;

/**
 * シンボルとその値との束縛を保持する環境オブジェクトです。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public final class Bindings extends HashMap<String, Symbol>{
	private static final long serialVersionUID = 1L;

	/**
	 * 指定されたシンボルを環境に登録します。
	 * 
	 * @param value シンボル
	 * @return もともと登録されてた同名のシンボル
	 */
	public Symbol put(Symbol value){
		return super.put(value.name(), value);
	}
	
	/**
	 * 指定された関数を環境に登録します。
	 * 
	 * @param func 関数
	 * @return もともと登録されていた同名のシンボル
	 */
	public Symbol put(Function func){
		Symbol symbol = new Symbol(func.name());
		symbol.bind(func);
		return put(symbol);
	}

}