/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.math;

import java.math.BigDecimal;

/**
 * テイラー級数展開の計算を行う基底クラスです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2012年2月2日
 */
abstract class Taylor{
	public final int scale;
	
	/**
	 * スケールを指定してこのオブジェクトを構築します。
	 * 
	 * @param scale 計算精度
	 */
	public Taylor(int scale) {
		this.scale = scale;
	}
	
	/**
	 * テイラー展開を求めます。
	 * 
	 * @param arg 引数
	 */
	public abstract BigDecimal value(BigDecimal arg);
	
	/**
	 * テイラー級数展開の計算で用いる階乗を返します。
	 * 
	 * @param 階乗の終了値
	 * @return  階乗値
	 */
	public final BigDecimal factorial(int n){
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