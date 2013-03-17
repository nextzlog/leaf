/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import leaf.script.falcon.ast.Node;
import leaf.script.falcon.error.TypeCastException;
import leaf.script.falcon.type.Type;

/**
 * 式の構文木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public abstract class Expr extends Node {
	private Type type;
	
	/**
	 * 式の木を構築します。
	 * 
	 * @param line 行番号
	 */
	public Expr(int line) {
		super(line);
	}
	
	/**
	 * 式の型を設定します。
	 * 
	 * @param type 式の型
	 * @return type
	 */
	public Type setType(Type type) {
		return this.type = type;
	}
	
	/**
	 * 式の型を返します。
	 * 
	 * @return 式の型
	 */
	public final Type getType() {
		return type;
	}
	
	/**
	 * この式が指定された型であるか確認します。
	 * 
	 * @param type 型
	 * @return 型が一致すればtrue
	 */
	public boolean isType(Type type) {
		return this.type.equals(type);
	}
	
	/**
	 * 指定された型に型変換された式を返します。
	 * 
	 * @param type 変換する型
	 * @return 型変換された式
	 * @throws TypeCastException 型変換できない場合
	 */
	public Expr cast(Type type) throws TypeCastException {
		if(this.type == type) return this;
		if(type.isAssignable(getType())) {
			return new Cast(type, this);
		}
		throw new TypeCastException(this);
	}

}
