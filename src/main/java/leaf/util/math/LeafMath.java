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
import static leaf.util.math.Constants.PI_2DIV;
import static leaf.util.math.Constants.PI_2MUL;

/**
 * 指数関数、対数関数、三角関数など数値計算を高精度で実行します。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2012年2月2日
 */
public final class LeafMath {
	private final Exponential exponential;
	private final Logarithm logarithm;
	private final SquareRoot sqrt;
	private final Cosine cosine;
	private final int scale;
	
	/**
	 * 計算精度を指定してオブジェクトを構築します。
	 * 
	 * @param scale 計算精度
	 */
	public LeafMath(int scale) {
		this.scale = scale;
		exponential = new Exponential(scale);
		logarithm = new Logarithm(scale);
		sqrt = new SquareRoot(scale);
		cosine = new Cosine(scale);
	}
	
	/**
	 * 指数関数を計算します。
	 * 
	 * @param exp 指数
	 * @return  指数の計算値
	 */
	public BigDecimal exp(BigDecimal exp){
		return exponential.value(exp);
	}
	
	/**
	 * 平方根を計算します。
	 * 
	 * @param base 平方根を求める数
	 * @return 平方根
	 */
	public BigDecimal sqrt(BigDecimal base) {
		return sqrt.value(base);
	}
	
	/**
	 * 累乗値を計算します。
	 * 
	 * @param base ベース
	 * @param exp  指数
	 * @return 累乗の計算値
	 */
	public BigDecimal pow(BigDecimal base, BigDecimal exp){
		return exp(exp.multiply(log(base)));
	}
	
	/**
	 * ネイピア数を底とする自然対数を計算します。
	 * 
	 * @param val 0よりも大きい数
	 * @return 自然対数値
	 * @throws ArithmeticException valが0以下の場合
	 */
	public BigDecimal log(BigDecimal val){
		return logarithm.log(val);
	}
	
	/**
	 * 正弦関数を計算します。
	 * 
	 * @param rad 孤度法での角度
	 * @return 正弦値
	 */
	public BigDecimal sin(BigDecimal rad){
		return cosine.value(rad.remainder(PI_2MUL).subtract(PI_2DIV));
	}
	
	/**
	 * 余弦関数を計算します。
	 * 
	 * @param rad 孤度法での角度
	 * @return 余弦値
	 */
	public BigDecimal cos(BigDecimal rad){
		return cosine.value(rad.remainder(PI_2MUL));
	}
	
	/**
	 * 正接関数を計算します。
	 * 
	 * @param rad 孤度法での角度
	 * @return 正接値
	 */
	public BigDecimal tan(BigDecimal rad){
		rad = rad.remainder(PI_2MUL);
		BigDecimal sin = cosine.value(rad.subtract(PI_2DIV));
		BigDecimal cos = cosine.value(rad);
		return sin.divide(cos, scale, HALF_EVEN);
	}
}
