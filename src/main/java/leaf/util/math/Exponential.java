/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.math;

import java.math.BigDecimal;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * ネイピア数を底とする指数計算を行います。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2012年2月2日
 */
final class Exponential extends Taylor {
	public Exponential(int scale) {
		super(scale);
	}
	
	@Override
	public BigDecimal value(BigDecimal arg){
		BigDecimal result = BigDecimal.ONE, old = result;
		for(int i = 1; true; i++, old = result){
			result = result.add(arg.pow(i).divide
			(factorial(i), scale + 4, HALF_EVEN));
			if(old.compareTo(result) == 0)
			return result.setScale(scale, HALF_EVEN);
		}
	}
}