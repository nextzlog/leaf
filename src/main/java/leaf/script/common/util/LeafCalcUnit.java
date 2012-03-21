/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import javax.script.ScriptException;

import leaf.manager.LeafLocalizeManager;

/**
 *言語処理系で用いられる演算機械の実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.2 作成：2011年2月16日
 */
public final class LeafCalcUnit{
	private LeafCalcUnit(){}
	private static LeafLocalizeManager localize
	 = LeafLocalizeManager.getInstance(LeafCalcUnit.class);
	/**
	*算術型変換規則を表現するクラスです。
	*/
	private static final class TypeUnit{
		private static final HashMap<Class, Integer> map;
		private static final ArrayList<Class> classes;
		static{
			map = new HashMap<Class, Integer>();
			classes = new ArrayList<Class>();
			add(Byte.class);
			add(Short.class);
			add(Integer.class);
			add(Long.class);
			add(Float.class);
			add(Double.class);
		}
		/**
		*現在の最強型よりも強い数値型を追加します。
		*@param numcls より強力な型
		*/
		public static void add(Class numcls){
			final int number = classes.size();
			map.put(numcls, number);
			classes.add(numcls);
		}
		/**
		*二項演算の返り値の型を決定します。
		*@param a 演算子の左側のコード
		*@param b 演算子の右側のコード
		*@return 演算の返す型 nullの場合算術式でない
		*/
		public static Class getType(Code a, Code b){
			try{
				Integer anum = map.get(a.getValue().getClass());
				Integer bnum = map.get(b.getValue().getClass());
				if(anum == null || bnum == null) return null;
				return classes.get(Math.max(anum, bnum));
			}catch(NullPointerException ex){
				return null;
			}
		}
	}
	/**
	*コードの値を加算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値 数値でない場合は文字列の結合値
	*/
	public static Code add(Code code1, Code code2){
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			try{
				if(type == Double.class)
					return new Code(code1.toDouble() + code2.toDouble());
				if(type == Float.class)
					return new Code(code1.toFloat() + code2.toFloat());
				if(type == Long.class)
					return new Code(code1.toLong() + code2.toLong());
				if(type == Integer.class)
					return new Code(code1.toInt() + code2.toInt());
				if(type == Short.class)
					return new Code((short)(code1.toInt() + code2.toInt()));
				return new Code((byte)(code1.toInt() + code2.toInt()));
			}catch(ScriptException ex){}
		}
		return new Code(code1.toString().concat(code2.toString()));
	}
	/**
	*コードの値を減算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 演算規則違反時(数値でない場合)
	*/
	public static Code subtract(Code code1, Code code2) throws ScriptException{
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			if(type == Double.class)
				return new Code(code1.toDouble() - code2.toDouble());
			if(type == Float.class)
				return new Code(code1.toFloat() - code2.toFloat());
			if(type == Long.class)
				return new Code(code1.toLong() - code2.toLong());
			if(type == Integer.class)
				return new Code(code1.toInt() - code2.toInt());
			if(type == Short.class)
				return new Code((short)(code1.toInt() - code2.toInt()));
			return new Code((byte)(code1.toInt() - code2.toInt()));
		}
		throw error("-", code1, code2);
	}
	/**
	*コードの値を乗算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 演算規則違反時(数値でない場合)
	*/
	public static Code multiply(Code code1, Code code2) throws ScriptException{
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			if(type == Double.class)
				return new Code(code1.toDouble() * code2.toDouble());
			if(type == Float.class)
				return new Code(code1.toFloat() * code2.toFloat());
			if(type == Long.class)
				return new Code(code1.toLong() * code2.toLong());
			if(type == Integer.class)
				return new Code(code1.toInt() * code2.toInt());
			if(type == Short.class)
				return new Code((short)(code1.toInt() * code2.toInt()));
			return new Code((byte)(code1.toInt() * code2.toInt()));
		}
		throw error("*", code1, code2);
	}
	/**
	*コードの値を除算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 演算規則違反時(数値でない場合)
	*/
	public static Code divide(Code code1, Code code2) throws ScriptException{
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			if(type == Double.class)
				return new Code(code1.toDouble() / code2.toDouble());
			if(type == Float.class)
				return new Code(code1.toFloat() / code2.toFloat());
			if(type == Long.class)
				return new Code(code1.toLong() / code2.toLong());
			if(type == Integer.class)
				return new Code(code1.toInt() / code2.toInt());
			if(type == Short.class)
				return new Code((short)(code1.toInt() / code2.toInt()));
			return new Code((byte)(code1.toInt() / code2.toInt()));
		}
		throw error("/", code1, code2);
	}
	/**
	*コードの値を剰余演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 演算規則違反時(数値でない場合)
	*/
	public static Code mod(Code code1, Code code2) throws ScriptException{
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			if(type == Double.class)
				return new Code(code1.toDouble() % code2.toDouble());
			if(type == Float.class)
				return new Code(code1.toFloat() % code2.toFloat());
			if(type == Long.class)
				return new Code(code1.toLong() % code2.toLong());
			if(type == Integer.class)
				return new Code(code1.toInt() % code2.toInt());
			if(type == Short.class)
				return new Code((short)(code1.toInt() % code2.toInt()));
			return new Code((byte)(code1.toInt() % code2.toInt()));
		}
		throw error("%", code1, code2);
	}
	/**
	*コードの値を累乗した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 演算規則違反時(数値でない場合)
	*/
	public static Code power(Code code1, Code code2) throws ScriptException{
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			double result = Math.pow(code1.toDouble(), code2.toDouble());
			if(type == Double.class) return new Code(result);
			if(type == Float.class) return new Code((float)result);
			if(type == Long.class) return new Code((long)result);
			if(type == Integer.class) return new Code((int)result);
			if(type == Short.class) return new Code((short)result);
			return new Code((byte)result);
		}
		throw error("**", code1, code2);
	}
	/**
	*コードの数値を符号反転せずに返します。
	*@param code 演算子が作用する値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時(数値でない場合)
	*/
	public static Code plus(Code code) throws ScriptException{
		Object val = code.getValue();
		if(val instanceof Number){
			if(val instanceof Double ) return code;
			if(val instanceof Float  ) return code;
			if(val instanceof Long   ) return code;
			if(val instanceof Integer) return code;
			if(val instanceof Short  ) return code;
			if(val instanceof Byte   ) return code;
		}
		throw error("+", code);
	}
	/**
	*コードの数値を符号反転した結果を返します。
	*@param code 演算子が作用する値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時(数値でない場合)
	*/
	public static Code negate(Code code) throws ScriptException{
		Object val = code.getValue();
		if(val instanceof Number){
			if(val instanceof Double) return new Code(-code.toDouble());
			if(val instanceof Float) return new Code(-code.toFloat());
			if(val instanceof Long) return new Code(-code.toLong());
			int result = -code.toInt();
			if(val instanceof Integer) return new Code(result);
			if(val instanceof Short) return new Code((short)result);
			if(val instanceof Byte)  return new Code((byte )result);
		}
		throw error("-", code);
	}
	/**
	*コードの値を論理積演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時(数値または真偽値でない場合)
	*/
	public static Code and(Code code1, Code code2) throws ScriptException{
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			long result = code1.toLong() & code2.toLong();
			if(type == Long.class) return new Code(result);
			if(type == Integer.class) return new Code((int)result);
			if(type == Short.class) return new Code((short)result);
			return new Code((byte)result);
		}else if(code1.isBoolean() && code2.isBoolean())
			return new Code(code1.toBoolean() && code2.toBoolean());
		throw error("&", code1, code2);
	}
	/**
	*コードの値を論理和演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時(数値または真偽値でない場合)
	*/
	public static Code or (Code code1, Code code2) throws ScriptException{
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			long result = code1.toLong() | code2.toLong();
			if(type == Long.class) return new Code(result);
			if(type == Integer.class) return new Code((int)result);
			if(type == Short.class) return new Code((short)result);
			return new Code((byte)result);
		}else if(code1.isBoolean() && code2.isBoolean())
			return new Code(code1.toBoolean() || code2.toBoolean());
		throw error("|", code1, code2);
	}
	/**
	*コードの値を排他的論理和演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時(数値または真偽値でない場合)
	*/
	public static Code xor(Code code1, Code code2) throws ScriptException{
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			long result = code1.toLong() ^ code2.toLong();
			if(type == Long.class) return new Code(result);
			if(type == Integer.class) return new Code((int)result);
			if(type == Short.class) return new Code((short)result);
			return new Code((byte)result);
		}else if(code1.isBoolean() && code2.isBoolean())
			return new Code(code1.toBoolean() ^ code2.toBoolean());
		throw error("^", code1, code2);
	}
	/**
	*コードの値を論理否定演算した結果を返します。
	*@param code 演算子が作用する値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時(数値または真偽値でない場合)
	*/
	public static Code not(Code code) throws ScriptException{
		Object value = code.getValue();
		if(value instanceof Long   ) return new Code(~code.toLong());
		if(value instanceof Integer) return new Code(~code.toInt());
		if(value instanceof Short  ) return new Code((short)(~code.toInt()));
		if(value instanceof Byte   ) return new Code((byte )(~code.toInt()));
		if(value instanceof Boolean) return new Code(!code.toBoolean());
		throw error("!(~)", code);
	}
	/**
	*コードの値をインクリメントした結果を返します。
	*@param code 演算子が作用する値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時(数値でない場合)
	*/
	public static Code increment(Code code) throws ScriptException{
		Object value = code.getValue();
		if(value instanceof Double ) return new Code(code.toDouble()+1);
		if(value instanceof Float  ) return new Code(code.toFloat() +1);
		if(value instanceof Long   ) return new Code(code.toLong()  +1);
		if(value instanceof Integer) return new Code(code.toInt()   +1);
		if(value instanceof Short  ) return new Code((short)(code.toInt()+1));
		if(value instanceof Byte   ) return new Code((byte) (code.toInt()+1));
		throw error("++", code);
	}
	/**
	*コードの値をデクリメントした結果を返します。
	*@param code 演算子が作用する値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時(数値でない場合)
	*/
	public static Code decrement(Code code) throws ScriptException{
		Object value = code.getValue();
		if(value instanceof Double ) return new Code(code.toDouble()-1);
		if(value instanceof Float  ) return new Code(code.toFloat() -1);
		if(value instanceof Long   ) return new Code(code.toLong()  -1);
		if(value instanceof Integer) return new Code(code.toInt()   -1);
		if(value instanceof Short  ) return new Code((short)(code.toInt()-1));
		if(value instanceof Byte   ) return new Code((byte) (code.toInt()-1));
		throw error("--", code);
	}
	/**
	*コードの値を比較演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値 -1 0 +1
	*@throws ScriptException Comparableでない場合
	*/
	@SuppressWarnings("unchecked")
	private static int compare(Code code1, Code code2)
	throws ScriptException{
		Comparable val1, val2;
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			if(type == Double.class){
				val1 = code1.toDouble();
				val2 = code2.toDouble();
			}else if(type == Float.class){
				val1 = code1.toFloat();
				val2 = code2.toFloat();
			}else if(type == Long.class){
				val1 = code1.toLong();
				val2 = code2.toLong();
			}else if(type == Integer.class){
				val1 = code1.toInt();
				val2 = code2.toInt();
			}else if(type == Short.class){
				val1 = (short)code1.toInt();
				val2 = (short)code2.toInt();
			}else{
				val1 = (Byte)code1.getValue();
				val2 = (Byte)code2.getValue();
			}
			return val1.compareTo(val2);
		}try{
			val1 = (Comparable)code1.getValue();
			val2 = (Comparable)code2.getValue();
			return val1.compareTo(val2);
		}catch(ClassCastException ex){
			throw error("<=>", code1, code2);
		}catch(NullPointerException ex){
			throw error("<=>", code1, code2);
		}
	}
	/**
	*コードの値を「>=」演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時({@link Comparable}でない場合)
	*/
	public static Code isGreaterOrEqual(Code code1, Code code2)
	throws ScriptException{
		try{
			return new Code(compare(code1, code2)>=0);
		}catch(ScriptException ex){
			throw error(">=", code1, code2);
		}
	}
	/**
	*コードの値を「>」演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時({@link Comparable}でない場合)
	*/
	public static Code isGreater(Code code1, Code code2)
	throws ScriptException{
		try{
			return new Code(compare(code1, code2)>0);
		}catch(ScriptException ex){
			throw error(">", code1, code2);
		}
	}
	/**
	*コードの値を「<=」演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時({@link Comparable}でない場合)
	*/
	public static Code isLessOrEqual(Code code1, Code code2)
	throws ScriptException{
		try{
			return new Code(compare(code1, code2)<=0);
		}catch(ScriptException ex){
			throw error("<=", code1, code2);
		}
	}
	/**
	*コードの値を「<」演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時({@link Comparable}でない場合)
	*/
	public static Code isLess(Code code1, Code code2)
	throws ScriptException{
		try{
			return new Code(compare(code1, code2)<0);
		}catch(ScriptException ex){
			throw error("<", code1, code2);
		}
	}
	/**
	*コードの数値を大小比較演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return {@link Comparable#compareTo(Object)}の値
	*/
	public static Code compareTo(Code code1, Code code2)
	throws ScriptException{
		try{
			return new Code(compare(code1, code2));
		}catch(ScriptException ex){
			throw error("<=>", code1, code2);
		}
	}
	/**
	*コードの値を構造的等価比較演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*/
	public static Code deepequals(Code code1, Code code2){
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			try{
				boolean result = code1.toDouble() == code2.toDouble();
				if(type == Double.class) return new Code(result);
				if(type == Float.class) return new Code(result);
				return new Code(code1.toLong() == code2.toLong());
			}catch(ScriptException ex){}
		}
		return new Code(code1.equals(code2));
	}
	/**
	*コードの値を構造的等価否定演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*/
	public static Code notequal(Code code1, Code code2){
		Class type = TypeUnit.getType(code1, code2);
		if(type != null){
			try{
				boolean result = code1.toDouble() != code2.toDouble();
				if(type == Double.class) return new Code(result);
				if(type == Float.class) return new Code(result);
				return new Code(code1.toLong() != code2.toLong());
			}catch(ScriptException ex){}
		}
		return new Code(!code1.equals(code2));
	}
	/**
	*コードの整数値を左シフト演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時(整数値でない場合)
	*/
	public static Code bitleft(Code code1, Code code2)
	throws ScriptException{
		try{
			Object val1 = code1.getValue();
			long result = code1.toLong() << code2.toInt();
			if(val1 instanceof Long   ) return new Code(result);
			if(val1 instanceof Integer) return new Code((int)  result);
			if(val1 instanceof Short  ) return new Code((short)result);
			if(val1 instanceof Byte   ) return new Code((byte) result);
		}catch(ScriptException ex){}
		throw error("<<", code1, code2);
	}
	/**
	*コードの整数値を右シフト演算した結果を返します。
	*@param code1 演算子の左側の値
	*@param code2 演算子の右側の値
	*@return 演算結果の値
	*@throws ScriptException 計算規則違反時(整数値でない場合)
	*/
	public static Code bitright(Code code1, Code code2)
	throws ScriptException{
		try{
			Object val1 = code1.getValue();
			long result = code1.toLong() >>  code2.toInt();
			if(val1 instanceof Long   ) return new Code(result);
			if(val1 instanceof Integer) return new Code((int)  result);
			if(val1 instanceof Short  ) return new Code((short)result);
			if(val1 instanceof Byte   ) return new Code((byte) result);
		}catch(ScriptException ex){}
		throw error(">>", code1, code2);
	}
	/**
	*計算規則に違反した際に例外を生成します。
	*@param op 演算子
	*@param code1 第一引数
	*@param code2 第二引数
	*/
	private static ScriptException error(String op, Code code1, Code code2){
		return new ScriptException(localize.translate(
			"error_2_exception", op, code1.getType(), code2.getType()));
	}
	/**
	*計算規則に違反した際に例外を生成します。
	*@param op 演算子
	*@param code 引数
	*/
	private static ScriptException error(String op, Code code){
		return new ScriptException(localize.translate(
			"error_1_exception", op, code.getType()));
	}
}