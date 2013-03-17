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
 * int型を表現する型システム内の型です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2013/02/15
 *
 */
public final class IntType extends NumberType {
	public static final IntType TYPE = new IntType();
	
	private IntType() {
		super(int.class, 1);
	}
	
	@Override
	public InstructionSet cast(Type a) {
		if(a == DoubleType.TYPE) {
			return InstructionSet.ITOD;
		}
		return null;
	}

}
