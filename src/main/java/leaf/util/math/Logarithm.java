/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.math;

import java.math.BigDecimal;
import static java.math.RoundingMode.HALF_EVEN;
import static leaf.util.math.Constants.SCALE;

/**
 *ネイピア数を底とする対数計算を行います。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2012年2月2日
 */
final class Logarithm extends Taylor{
	@Override public BigDecimal value(BigDecimal arg){
		BigDecimal log = BigDecimal.ZERO, old = log;
		arg = arg.subtract(BigDecimal.ONE);
		for(int i = 1; true; i++, old = log){
			BigDecimal add = arg.pow(i).divide
			(BigDecimal.valueOf(i), SCALE + 4, HALF_EVEN);
			log = (i % 2 == 1)? log.add(add): log.subtract(add);
			if(old.compareTo(log) == 0)
			return log.setScale(SCALE, HALF_EVEN);
		}
	}
}