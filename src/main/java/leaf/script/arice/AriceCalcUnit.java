/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.arice;

import javax.script.ScriptException;
import java.lang.reflect.*;
import java.util.List;

import leaf.manager.LeafReflectManager;

/**
*AriCE言語の演算機能の実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年2月16日
*/
final class AriceCalcUnit{
	
	/**
	*コードに値を加算します。
	*@param val1 加算する値
	*@param val2 加算する値
	*@return 加算した結果
	*@throws ScriptException
	*/
	public Code add(Object val1, Object val2){
		if(val1 instanceof Integer && val2 instanceof Integer){
			return new Code((Integer)val1 + (Integer)val2);
		}else if(val1 instanceof Number && val2 instanceof Number){
			return new Code(((Number)val1).doubleValue() + ((Number)val2).doubleValue());
		}
		return new Code(val1.toString() + val2.toString());
	}
	/**
	*コードに値を減算します。
	*@param val1 減算する値
	*@param val2 減算する値
	*@return 減算した結果
	*@throws ScriptException
	*/
	public Code sub(Object val1, Object val2) throws ScriptException{
		if(val1 instanceof Integer && val2 instanceof Integer){
			return new Code((Integer)val1 - (Integer)val2);
		}else if(val1 instanceof Number && val2 instanceof Number){
			return new Code(((Number)val1).doubleValue() - ((Number)val2).doubleValue());
		}if(val1 instanceof String && val2 instanceof String){
			return new Code(((String)val1).replaceAll(val2+"",""));
		}
		throw error("\""+val1+"\" or \""+val2+"\" is not a number.");
	}
	/**
	*コードに値を乗算します。
	*@param val1 乗算する値
	:@param val2 乗算する値
	*@return 乗算した結果
	*@throws ScriptException
	*/
	public Code mul(Object val1, Object val2) throws ScriptException{
		if(val1 instanceof Integer && val2 instanceof Integer){
			return new Code((Integer)val1 * (Integer)val2);
		}else if(val1 instanceof Number && val2 instanceof Number){
			return new Code(((Number)val1).doubleValue() * ((Number)val2).doubleValue());
		}if(val1 instanceof String && val2 instanceof Number){
			StringBuilder sb = new StringBuilder();
			int cnt = ((Number)val2).intValue();
			for(int i=0; i<cnt;i++){
				sb.append(val1);
			}
			return new Code(sb.toString());
		}else if(val2 instanceof String && val1 instanceof Integer){
			StringBuilder sb = new StringBuilder();
			int cnt = ((Number)val1).intValue();
			for(int i=0; i<cnt;i++){
				sb.append(val2);
			}
			return new Code(sb.toString());
		}
		throw error("\""+val1+"\" or \""+val2+"\" is not a number.");
	}
	/**
	*コードに値を除算します。
	*@param val1 除算する値
	*@param val2 除算する値
	*@return 除算した結果
	*@throws ScriptException
	*/
	public Code div(Object val1, Object val2) throws ScriptException{
		if(val1 instanceof Integer && val2 instanceof Integer){
			return new Code((Integer)val1 / (Integer)val2);
		}else if(val1 instanceof Number && val2 instanceof Number){
			return new Code(((Number)val1).doubleValue() / ((Number)val2).doubleValue());
		}
		throw error("\""+val1+"\" or \""+val2+"\" is not a number.");
	}
	/**
	*コードのMOD値を返します。
	*@param val1 対象の数
	*@param val2 割る数
	*@return MODの結果
	*@throws ScriptException
	*/
	public Code mod(Object val1, Object val2) throws ScriptException{
		if(val1 instanceof Integer && val2 instanceof Integer){
			return new Code((Integer)val1 % (Integer)val2);
		}else if(val1 instanceof Number && val2 instanceof Number){
			return new Code(((Number)val1).doubleValue() % ((Number)val2).doubleValue());
		}
		throw error("\""+val1+"\" or \""+val2+"\" is not a number.");
	}
	/**
	*コードの自乗値を返します。
	*@param val1 対象の数
	*@param val2 乗数
	*@return 自乗した結果
	*@throws ScripException
	*/
	public Code pow(Object val1, Object val2) throws ScriptException{
		if(val1 instanceof Integer && val2 instanceof Integer){
			return new Code(
				(int)Math.pow(((Number)val1).doubleValue(),((Number)val2).doubleValue())
			);
		}else if(val1 instanceof Number && val2 instanceof Number){
			return new Code(
				Math.pow(((Number)val1).doubleValue(), ((Number)val2).doubleValue())
			);
		}
		throw error("\""+val1+"\" or \""+val2+"\" is not a number.");
	}
	/**
	*コードに値をAND演算して返します。
	*@param val1 対象値
	*@param val2 対象値
	*@return ANDの結果
	*@throws ScriptException
	*/
	public Code and(Object val1, Object val2) throws ScriptException{
		if(val1 instanceof Integer && val2 instanceof Integer){
			return new Code((Integer)val1 & (Integer)val2);
		}else if(val1 instanceof Boolean && val2 instanceof Boolean){
			return new Code(Boolean.valueOf((Boolean)val1 && (Boolean)val2));
		}else{
			throw error("\""+val1+"\" or \""+val2+"\" is not a integer or a boolean.");
		}
	}
	/**
	*コードに値をOR演算して返します。
	*@param val1 対象値
	*@param val2 対象値
	*@return ORの結果
	*@throws ScriptException
	*/
	public Code or(Object val1, Object val2) throws ScriptException{
		if(val1 instanceof Integer && val2 instanceof Integer){
			return new Code((Integer)val1 | (Integer)val2);
		}else if(val1 instanceof Boolean && val2 instanceof Boolean){
			return new Code(Boolean.valueOf((Boolean)val1 || (Boolean)val2));
		}else{
			throw error("\""+val1+"\" or \""+val2+"\" is not a integer or a boolean.");
		}
	}
	
	/**
	*コードに値をXOR演算して返します。
	*@param val1 対象値
	*@param val2 対象値
	*@return XORの結果
	*@throws ScriptException
	*/
	public Code xor(Object val1, Object val2) throws ScriptException{
		if(val1 instanceof Integer && val2 instanceof Integer){
			return new Code((Integer)val1 ^ (Integer)val2);
		}else if(val1 instanceof Boolean && val2 instanceof Boolean){
			return new Code(Boolean.valueOf((Boolean)val1 ^ (Boolean)val2));
		}else{
			throw error("\""+val1+"\" or \""+val2+"\" is not a integer or a boolean.");
		}
	}
	/**
	*コードをNOT演算して返します。
	*@param value 対象値
	*@return NOTの結果
	*@throws ScriptException
	*/
	public Code not(Object value) throws ScriptException{
		if(value instanceof Boolean){
			return new Code(!(Boolean)value);
		}else if(value instanceof Integer){
			return new Code(~(Integer)value);
		}else{
			throw error("\"" + value + "\" is not an boolean or integer.");
		}
	}
	/**
	*コードをインクリメントして返します。
	*@poram code コード
	*@return 結果
	*@throws ScriptException
	*/
	public Code increment(Code code) throws ScriptException{
		if(code.getValue() instanceof Integer){
			return new Code(code.toInteger() + 1);
		}else if(code.getValue() instanceof Number){
			return new Code(code.toDouble()  + 1);
		}else{
			throw error("\"" + code + "\" is not a number.");
		}
	}
	/**
	*コードをデクリメントして返します。
	*@poram code コード
	*@return 結果
	*@throws ScriptException
	*/
	public Code decrement(Code code) throws ScriptException{
		if(code.getValue() instanceof Integer){
			return new Code(code.toInteger() - 1);
		}else if(code.getValue() instanceof Number){
			return new Code(code.toDouble()  - 1);
		}else{
			throw error("\"" + code + "\" is not a number.");
		}
	}
	/**
	*コードを>=比較して返します。
	*@param code1 コード
	*@param code2 コード
	*@return 比較した結果
	*@throws ScriptException
	*/
	public Code isBiggerOrEqual(Code code1, Code code2) throws ScriptException{
		return new Code(code1.toDouble() >= code2.toDouble());
	}
	/**
	*コードを>比較して返します。
	*@param code1 コード
	*@param code2 コード
	*@return 比較した結果
	*@throws ScriptException
	*/
	public Code isBigger(Code code1, Code code2) throws ScriptException{
		return new Code(code1.toDouble() > code2.toDouble());
	}
	/**
	*コードを<=比較して返します。
	*@param code1 コード
	*@param code2 コード
	*@return 比較した結果
	*@throws ScriptException
	*/
	public Code isLesserOrEqual(Code code1, Code code2) throws ScriptException{
		return new Code(code1.toDouble() <= code2.toDouble());
	}
	/**
	*コードを<比較して返します。
	*@param code1 コード
	*@param code2 コード
	*@return 比較した結果
	*@throws ScriptException
	*/
	public Code isLesser(Code code1, Code code2) throws ScriptException{
		return new Code(code1.toDouble() < code2.toDouble());
	}
	/**
	*コードを==比較して返します。
	*@param code1 コード
	*@param code2 コード
	*@return 比較した結果
	*@throws ScriptException
	*/
	public Code equals(Code code1, Code code2) throws ScriptException{
		if(code1.getValue() instanceof Number && code2.getValue() instanceof Number){
			return new Code(code1.toDouble() == code2.toDouble());
		}try{
			return new Code(code1.getValue().equals(code2.getValue()));
		}catch(NullPointerException ex){
			return new Code(code1.getValue() == null && code2.getValue() == null);
		}
	}
	/**
	*コードを!=比較して返します。
	*@param code1 コード
	*@param code2 コード
	*@return 比較した結果
	*@throws ScriptException
	*/
	public Code differs(Code code1, Code code2) throws ScriptException{
		if(code1.getValue() instanceof Number && code2.getValue() instanceof Number){
			return new Code(code1.toDouble() != code2.toDouble());
		}try{
			return new Code(!code1.getValue().equals(code2.getValue()));
		}catch(NullPointerException ex){
			return new Code(code1.getValue() != null || code2.getValue() != null);
		}
	}
	/**
	*コードをビット左シフト演算して返します。
	*@param code1 コード
	*@param code2 コード
	*@return シフトした結果
	*@throws ScriptException
	*/
	public Code bitleft(Code code1, Code code2) throws ScriptException{
		return new Code(code1.toInteger() << code2.toInteger());
	}
	/**
	*コードをビット右シフト演算して返します。
	*@param code1 コード
	*@param code2 コード
	*@return シフトした結果
	*@throws ScriptException
	*/
	public Code bitright(Code code1, Code code2) throws ScriptException{
		return new Code(code1.toInteger() >> code2.toInteger());
	}
	/**
	*コードの値を名前に持つクラスのインスタンスを返します。
	*@param name クラス名のコード
	*@param args コンストラクタの引数
	*@return クラスのインスタンス 例外時は例外
	*@throws ScriptException インスタンス化に失敗した場合
	*/
	public Code getInstanceCode(Code name, Code... args) throws ScriptException{
		try{
			Class<?>   target = name.toClass();
			Class<?>[] clases = new Class<?>[args.length];
			Object[]   values = new Object[args.length];
			for(int i=0;i<values.length;i++){
				clases[i] = args[i].getType();
				values[i] = args[i].getValue();
			}
			return new Code(LeafReflectManager.newInstance(target, values, clases));
		}catch(InvocationTargetException ex){
			return new Code(ex.getCause());
		}catch(Exception ex){
			throw error(ex);
		}
	}
	/**
	*格納値のオブジェクトに対しメソッドを実行します。
	*@param code 対象コード
	*@param name メソッド名
	*@param args 引数
	*@return メソッドの戻り値 例外時は例外
	*throws ScriptException メソッドの実行に失敗した場合
	*/
	public Code invokeMethod(Code code, Code name, Code... args) throws ScriptException{
		Class<?> target = (code.isNull())?code.getType() : code.getValue().getClass();
		try{
			Class<?>[] clases = new Class<?>[args.length];
			Object[]   values = new Object[args.length];
			for(int i=0;i<values.length;i++){
				clases[i] = args[i].getType();
				values[i] = args[i].getValue();
			}
			return new Code(LeafReflectManager.invokeMethod(
				code.getValue(), target, name.toString(), values, clases
			));
		}catch(InvocationTargetException ex){
			return new Code(ex.getCause());
		}catch(Exception ex){
			throw error(ex);
		}
	}
	/**
	*格納値のオブジェクトのフィールドを参照します。
	*@param code 対象コード
	*@param name フィールド名
	*@return フィールド値
	*@throws ScriptException フィールドの参照に失敗した場合
	*/
	public Code getField(Code code, Code name) throws ScriptException{
		Class<?> target = (code.isNull())?code.getType() : code.getValue().getClass();
		try{
			Field field  = target.getField(name.toString());
			return new Code(field.get(code.getValue()), field.getType());
		}catch(Exception ex){
			throw error(ex);
		}
	}
	/**
	*格納値のオブジェクトのフィールドに代入します。
	*@param code 対象コード
	*@param name フィールド名
	*@param val  代入する値
	*@throws ScriptException フィールドへの代入に失敗した場合
	*/
	public void setField(Code code, Code name, Code val) throws ScriptException{
		Class<?> target = (code.isNull())?code.getType() : code.getValue().getClass();
		try{
			target.getField(name.toString()).set(code.getValue(), val.getValue());
		}catch(Exception ex){
			throw error(ex);
		}
	}
	/**
	*指定されたオブジェクトを型変換します。
	*@param code 対象コード
	*@param cast クラス
	*@throws ScriptException キャストに失敗した場合
	*/
	public Code cast(Code code, Code cast) throws ScriptException{
		if(code.getValue() == null){
			return new Code(null, cast.toClass());
		}else try{
			return new Code(code.getValue(), cast.toClass());
		}catch(ClassCastException ex){
			throw error(ex);
		}
	}
	/**例外を生成します*/
	private ScriptException error(Object msg){
		return new ScriptException(msg.toString());
	}
	private ScriptException error(Exception ex){
		return new ScriptException(ex);
	}
}
