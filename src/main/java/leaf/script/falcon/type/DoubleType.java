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
 * double型を表現する型システム内の型です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2013/02/15
 *
 */
public final class DoubleType extends NumberType {
	public static final DoubleType TYPE = new DoubleType();
	
	private DoubleType() {
		super(double.class, 0);
	}

	@Override
	public InstructionSet cast(Type a) {
		if(a == IntType.TYPE) {
			return InstructionSet.DTOI;
		}
		return null;
	}

}
