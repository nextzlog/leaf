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
 *余弦関数の計算を行います。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2012年2月2日
 */
final class Cosine extends Taylor{
	@Override public BigDecimal value(BigDecimal arg){
		BigDecimal cos = BigDecimal.ONE, old = cos;
		for(int i = 1; true; i++, old = cos){
			BigDecimal add = arg.pow(i << 1).divide
			(factorial(i << 1), SCALE + 4, HALF_EVEN);
			cos = (i % 2 == 0)? cos.add(add) : cos.subtract(add);
			if(old.compareTo(cos) == 0)
			return cos.setScale(SCALE, HALF_EVEN);
		}
	}
}