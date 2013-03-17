/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.math;

import java.math.BigDecimal;
import static java.math.BigDecimal.*;
import static java.math.RoundingMode.HALF_EVEN;
import static leaf.util.math.Constants.LN_2;

/**
 * ネイピア数を底とする対数計算を行います。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2012年2月2日
 */
final class Logarithm extends Taylor{
	public Logarithm(int scale) {
		super(scale);
	}
	
	@Override
	public BigDecimal value(BigDecimal arg){
		BigDecimal log = ZERO, old = log;
		arg = arg.subtract(ONE);
		for(int i = 1; true; i++, old = log){
			BigDecimal add = arg.pow(i).divide(valueOf(i), scale + 4, HALF_EVEN);
			log = (i % 2 == 1)? log.add(add): log.subtract(add);
			if(old.compareTo(log) == 0) return log.setScale(scale, HALF_EVEN);
		}
	}
	
	/**
	 * 自然対数を計算します。
	 * 
	 * @param val 対数を計算する値
	 * @return 対数
	 */
	public BigDecimal log(BigDecimal val) {
		if(val.compareTo(ZERO) <= 0) {
			throw new ArithmeticException("log(" + val + ")");
		}
		if(val.compareTo(BigDecimal.ONE) < 0){
			return log(ONE.divide(val, scale, HALF_EVEN)).negate();
		}
		
		int n = 0;
		for(; val.compareTo(BigDecimal.ONE) >= 0; n++){
			val = val.divide(Constants.TWO, scale, HALF_EVEN);
		}
		
		return LN_2.multiply(valueOf(n)).add(value(val));
	}
}