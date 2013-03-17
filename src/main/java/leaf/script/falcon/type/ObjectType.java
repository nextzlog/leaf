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
 * 汎用的なオブジェクト型です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2013/02/15
 *
 */
public final class ObjectType extends Type {
	/**
	 * Objectクラスのオブジェクト型を構築します。
	 */
	public ObjectType() {
		this(Object.class);
	}
	
	/**
	 * クラスを指定してオブジェクト型を構築します。
	 * 
	 * @param type クラス
	 */
	public ObjectType(Class<?> type) {
		super(type);
	}
	
	@Override
	public Type getCommonType(Type a) {
		return new ObjectType(getCommonClass(a));
	}

	@Override
	public boolean isAssignable(Type a) {
		Class<?> mc = getType();
		Class<?> ac = a.getType();
		return mc.isAssignableFrom(ac);
	}

	@Override
	public InstructionSet cast(Type a) {
		return null;
	}

}
