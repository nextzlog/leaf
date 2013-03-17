/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.type;

/**
 * 数値型を表現する型システム内の型です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2013/02/15
 *
 */
public abstract class NumberType extends Type {
	private final int order;
	
	/**
	 * クラスと優先順位を指定して型を構築します。
	 * 
	 * @param type 数値型のクラス
	 * @param order 0を最上位とする正の優先順位
	 */
	public NumberType(Class<?> type, int order) {
		super(type);
		this.order = order;
	}
	
	@Override
	public final Type getCommonType(Type a) {
		if(a instanceof NumberType) {
			NumberType na = (NumberType) a;
			return na.order < order ? na : this;
		}
		return new ObjectType();
	}

	@Override
	public final boolean isAssignable(Type a) {
		return a instanceof NumberType;
	}

}