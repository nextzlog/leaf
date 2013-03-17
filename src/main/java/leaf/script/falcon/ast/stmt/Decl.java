/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import leaf.script.falcon.type.Type;

/**
 * 変数や引数の宣言の抽象クラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public abstract class Decl {
	private final String name;
	private final Type type;
	private int index = -1;
	
	/**
	 * 指定された型と名前で宣言を構築します。
	 * 
	 * @param type 型
	 * @param name 変数名
	 */
	public Decl(Type type, String name) {
		this.type = type;
		this.name = name;
	}
	
	/**
	 * この宣言の名前を返します。
	 * 
	 * @return 変数名
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * この宣言の型を返します。
	 * 
	 * @return 変数の型
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * この宣言に固有の番号を割り当てます。
	 * 
	 * @param index 宣言の番号
	 */
	void allocate(int index) {
		this.index = index;
	}
	
	/**
	 * 宣言に割り当てられた番号を返します。
	 * 
	 * @return 宣言の番号
	 */
	public int getIndex() {
		return index;
	}

}
