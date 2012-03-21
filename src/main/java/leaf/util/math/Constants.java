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

/**
 *数値計算で用いられる各定数を定義します。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 2012年2月2日
 */
final class Constants{
	public static final int SCALE = 50;
	
	public static final BigDecimal E = new BigDecimal
	("2.71828182845904523536028747135266249775724709369995957");
	
	public static final BigDecimal PI = new BigDecimal
	("3.14159265358979323846264338327950288419716939937510582");
	
	public static final BigDecimal PI_2MUL = new BigDecimal
	("6.28318530717958647692528676655900576839433879875021164");
	
	public static final BigDecimal PI_2DIV = new BigDecimal
	("1.57079632679489661923132169163975144209858469968755291");
	
	public static final BigDecimal LN_2 = new BigDecimal
	("0.69314718055994530941723212145817656807550013436026");
	
	public static final BigDecimal TWO = new BigDecimal(2);
}