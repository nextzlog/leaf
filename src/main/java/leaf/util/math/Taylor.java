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
 *テイラー級数展開の計算を行う基底クラスです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2012年2月2日
 */
abstract class Taylor{
	public abstract BigDecimal value(BigDecimal arg);
	
	/**
	 *テイラー級数展開の計算で用いる階乗を返します。
	 *
	 *@param 階乗の終了値
	 *@return  階乗値
	 */
	public static final BigDecimal factorial(int n){
		if(n < 0) throw new ArithmeticException();
		if(n < 16){
			int fact = 1;
			for(int i = 2; i <= n; i++) fact *= i;
			return BigDecimal.valueOf(fact);
		}
		
		BigDecimal result = BigDecimal.ONE;
		for(int i = 2; i <= n; i++){
			result = result.multiply(BigDecimal.valueOf(i));
		}
		return result;
	}
}