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
 * 平方根の計算を行うクラスです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.4 作成：2012年9月19日
 */
final class SquareRoot extends Taylor {
	public SquareRoot(int scale) {
		super(scale);
	}
	
	@Override
	public BigDecimal value(BigDecimal arg){
		double dval = Math.sqrt(arg.doubleValue());
		BigDecimal x = BigDecimal.valueOf(dval);
		BigDecimal bd_2 = new BigDecimal(2);
		for(int nowscale = 16; nowscale <= scale; nowscale *= 2){
			int divscale = Math.min(scale, nowscale * 2);
			x = x.subtract(x.pow(2).subtract(arg).divide(
				x.multiply(bd_2), divscale, HALF_EVEN));
		}
		return x;
	}
}
