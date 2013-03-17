/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

import java.math.BigDecimal;
import static java.math.MathContext.DECIMAL64;

/**
 * LISPの実数アトム型を実装するクラスです。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public final class Real extends Num{
	private int value = 0;
	private BigDecimal big;
	
	/**
	 * 整数値を指定して実数アトムを構築します。
	 * 
	 * @param value 整数値
	 */
	public Real(int value){
		this.value = value;
	}
	
	/**
	 * 実数値を指定して実数アトムを構築します。
	 * 
	 * @param value 実数値
	 */
	public Real(BigDecimal value){
		this.big = value;
	}
	
	/**
	 * 指定した文字列で実数アトムを構築します。
	 * 
	 * @param value 実数を表す文字列
	 */
	public Real(String value){
		this(new BigDecimal(value));
	}
	
	/**
	 * 指定した整数値に対応する実数アトムを返します。
	 * 
	 * @param value 整数値
	 * @return 対応する実数アトム
	 */
	public static Real valueOf(long value){
		if(value > Cache.min && value < Cache.max){
			return Cache.cache[(int)value - Cache.min];
		}
		if(value >= Integer.MIN_VALUE
		&& value <= Integer.MAX_VALUE){
			return new Real((int)value);
		}
		return new Real(String.valueOf(value));
	}
	
	/**
	 * 指定した文字列を実数アトム型に変換します。
	 * 
	 * @param s 実数を表す文字列
	 * @return 対応する実数アトム
	 */
	public static Real parse(String s){
		BigDecimal bd = new BigDecimal(s);
		if(s.indexOf('.') < 0) try{
			return valueOf(bd.longValueExact());
		}catch(ArithmeticException ex){}
		return new Real(bd);
	}
	
	@Override public Num add(Num num){
		if(isInt() && num.isInt()){
			return valueOf(value() + num.value());
		}
		BigDecimal your = num.getBigDecimal();
		return new Real(getBigDecimal().add(your));
	}
	
	@Override public Num subtract(Num num){
		if(isInt() && num.isInt()){
			return valueOf(value() - num.value());
		}
		BigDecimal your = num.getBigDecimal();
		return new Real(getBigDecimal().subtract(your));
	}
	
	@Override public Num multiply(Num num){
		if(isInt() && num.isInt()){
			return valueOf(value() * num.value());
		}
		BigDecimal your = num.getBigDecimal();
		return new Real(getBigDecimal().multiply(your));
	}
	
	@Override public Num divide(Num num){
		if(isInt() && num.isInt()){
			return valueOf(value() / num.value());
		}
		BigDecimal your = num.getBigDecimal();
		return new Real(getBigDecimal().divide(your, DECIMAL64));
	}
	
	@Override public Num mod(Num num){
		if(isInt() && num.isInt()){
			return valueOf(value() % num.value());
		}
		BigDecimal your = num.getBigDecimal();
		return new Real(getBigDecimal().remainder(your));
	}
	
	@Override public boolean isInt(){
		return big == null;
	}
	
	@Override public long value() throws ArithmeticException{
		if(isInt()) return value;
		throw new ArithmeticException("BigDecimal : " + big);
	}
	
	@Override public BigDecimal getBigDecimal(){
		return big != null? big : BigDecimal.valueOf(value);
	}
	
	private static class Cache{
		static final int min = -128;
		static final int max =  127;
		static final Real[] cache;
		
		static{
			cache = new Real[max-min+1];
			for(int i = 0; i < cache.length; i++){
				cache[i] = new Real(min + i);
			}
		}
		private Cache(){}
	}
}