/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.type;

import leaf.script.falcon.vm.InstructionSet;

/**
 * 型システムにおける型を表現するクラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2013/02/15
 *
 */
public abstract class Type {
	private final Class<?> type;
	
	/**
	 * 対応するクラスを指定して型を構築します。
	 * 
	 * @param type 型に対応するクラス
	 */
	public Type(Class<?> type) {
		this.type = type;
	}
	
	/**
	 * この型が表現するクラスを返します。
	 * 
	 * @return 型に対応するクラス
	 */
	public Class<?> getType() {
		return type;
	}
	
	/**
	 * 指定されたオブジェクトと等しいか返します。
	 * 
	 * @param obj 比較するオブジェクト
	 * @return objがTypeであり、かつ同じ型の場合
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Type) {
			return ((Type) obj).type == type;
		}
		return false;
	}
	
	/**
	 * この型の文字列による表現を返します。
	 * 
	 * @return 型の名前
	 */
	@Override
	public final String toString() {
		return type.getCanonicalName();
	}
	
	/**
	 * 指定された型とこの型の最大公約数の型を返します。
	 * 
	 * @param a 他方の型
	 * @return 共通するクラス
	 */
	public final Class<?> getCommonClass(Type a) {
		Class<?> ac = a.type;
		while(ac != null) {
			Class<?> mc = this.type;
			while(mc != null) {
				if(mc == ac) return mc;
				mc = mc.getSuperclass();
			}
			ac = ac.getSuperclass();
		}
		return Object.class;
	}
	
	/**
	 * 指定された型とこの型の最大公約数の型を返します。
	 * 
	 * @param a 他方の型
	 * @return 共通する型
	 */
	public abstract Type getCommonType(Type a);
	
	/**
	 * この型の式に指定された型の式を代入できるか返します。
	 * 
	 * @param a 代入される式の型
	 * @return 代入可能な場合true
	 */
	public abstract boolean isAssignable(Type a);
	
	/**
	 * この型の式を指定された型の式に変換する命令を返します。
	 * 
	 * @param a 型変換後の型
	 * @return 型変換のための命令 存在しなければnull
	 */
	public abstract InstructionSet cast(Type a);

}
