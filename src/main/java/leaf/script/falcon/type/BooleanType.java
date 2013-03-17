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
 * 真偽値型を表現する型システム内の型です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2013/02/15
 *
 */
public final class BooleanType extends Type {
	public static final BooleanType TYPE = new BooleanType();
	
	public BooleanType() {
		super(boolean.class);
	}

	@Override
	public Type getCommonType(Type a) {
		if(a instanceof BooleanType) {
			return this;
		}
		return new ObjectType();
	}

	@Override
	public boolean isAssignable(Type a) {
		return a instanceof BooleanType;
	}

	@Override
	public InstructionSet cast(Type a) {
		return null;
	}

}
