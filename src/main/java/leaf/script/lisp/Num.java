/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

import java.math.BigDecimal;

/**
 * 数値アトム型を実装する抽象クラスです。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public abstract class Num extends Atom implements Comparable<Num>{
	/**
	 * この数値アトムに指定された数値アトムを加算した結果を返します。
	 * 
	 * @param num 加算する数値アトム
	 * @return 加算した結果を表す数値アトム
	 */
	public abstract Num add(Num num);
	
	/**
	 * この数値アトムに指定された数値アトムを減算した結果を返します。
	 * 
	 * @param num 減算する数値アトム
	 * @return 減算した結果を表す数値アトム
	 */
	public abstract Num subtract(Num num);
	
	/**
	 * この数値アトムに指定された数値アトムを乗算した結果を返します。
	 * 
	 * @param num 乗算する数値アトム
	 * @return 乗算した結果を表す数値アトム
	 */
	public abstract Num multiply(Num num);
	
	/**
	 * この数値アトムに指定された数値アトムを除算した商を返します。
	 * 
	 * @param num 除算する数値アトム
	 * @return 商を表す数値アトム
	 */
	public abstract Num divide(Num num);
	
	/**
	 * この数値アトムに指定された数値アトムを剰余した剰余を返します。
	 * 
	 * @param num 除算する数値アトム
	 * @return 剰余を表す数値アトム
	 */
	public abstract Num mod(Num num);
	
	/**
	 * この数値アトムが整数アトム型であるか返します。
	 * 
	 * @return 整数アトム型であればtrue
	 */
	public abstract boolean isInt();
	
	/**
	 * この数値アトムの値をlong型で返します。
	 * 
	 * @return long型での値
	 * @see #getBigDecimal()
	 */
	public abstract long value();
	
	/**
	 * この数値アトムの値を{@link BigDecimal}で返します。
	 * 
	 * @return BigDecimalでの値
	 */
	public abstract BigDecimal getBigDecimal();
	
	/**
	 * この数値アトムの値を指定された数値アトムと比較します。
	 * 
	 * @param num 比較対象の数値アトム
	 * @return 自分が大きければ正、小さければ負、等しければ0
	 */
	@Override public int compareTo(Num num){
		if(isInt() && num.isInt()){
			long mine = value();
			long your = num.value();
			if(mine > your) return  1;
			if(mine < your) return -1;
			else return 0;
		}
		return getBigDecimal().compareTo(num.getBigDecimal());
	}
	
	@Override public String toString(){
		if(isInt()) return String.valueOf(value());
		return getBigDecimal().toString();
	}
	
	@Override public boolean isEqual(Sexp sexp){
		if(sexp instanceof Num) return compareTo((Num)sexp) == 0;
		return false;
	}
}
