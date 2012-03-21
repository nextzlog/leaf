/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import javax.script.ScriptException;

import leaf.manager.LeafReflectManager;

/**
 *言語処理系で用いられるAPIの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年8月21日
 */
public final class LeafReflectUnit{
	private LeafReflectUnit(){}
	/**
	*クラスのコンストラクタを呼び出します。
	*@param clas インスタンス化するクラス
	*@param args 引数を順番に格納するコードの配列
	*@return クラスのインスタンス
	*@throws ScriptException インスタンス化に失敗した場合
	*/
	public static Code newInstance(Class clas, Code... args)
	throws ScriptException{
		try{
			Object[] arguments = new Object[args.length];
			for(int i=0; i<args.length; i++){
				arguments[i] = args[i].getValue();
			}
			return new Code(
				LeafReflectManager.newInstance(clas, arguments));
		}catch(InvocationTargetException ex){
			throw new ScriptException((Exception)ex.getCause());
		}catch(Exception ex){
			ex.setStackTrace(new StackTraceElement[0]);
			throw new ScriptException(ex);
		}
	}
	/**
	*コードの内容オブジェクトのメソッドを呼び出します。
	*@param name メソッド名
	*@param code オブジェクトを格納するコード
	*@param args 引数を順番に格納するコードの配列
	*@return メソッドの戻り値を格納するコード
	*@throws ScriptException メソッドの実行に失敗した場合
	*/
	public static Code invokeMethod(String name, Code code, Code... args)
	throws ScriptException{
		try{
			Object[] arguments = new Object[args.length];
			for(int i=0; i<args.length; i++){
				arguments[i] = args[i].getValue();
			}
			if(code.isNull()){ //static method
				return new Code(LeafReflectManager.invokeMethod(
					code.getType(), name, arguments));
			}else{
				return new Code(LeafReflectManager.invokeMethod(
					code.getValue(), name, arguments));
			}
		}catch(InvocationTargetException ex){
			throw new ScriptException((Exception)ex.getCause());
		}catch(Exception ex){
			ex.setStackTrace(new StackTraceElement[0]);
			throw new ScriptException(ex);
		}
	}
	/**
	*コードの内容オブジェクトのフィールドの値を取得します。
	*@param name フィールド名
	*@param code オブジェクトを格納するコード
	*@return フィールド値
	*@throws ScriptException フィールドの参照に失敗した場合
	*/
	public static Code getField(String name, Code code)
	throws ScriptException{
		Class<?> clas = code.isNull()?
		code.getType() : code.getValue().getClass();
		try{
			Field field = clas.getField(name);
			return new Code(
				field.get(code.getValue()), field.getType());
		}catch(Exception ex){
			ex.setStackTrace(new StackTraceElement[0]);
			throw new ScriptException(ex);
		}
	}
	/**
	*コードの内容オブジェクトのフィールドに値を代入します。
	*@param name フィールド名
	*@param code オブジェクトを格納するコード
	*@param assn 代入する値を格納するコード
	*@throws ScriptException フィールドの代入に失敗した場合
	*/
	public static void setField(String name, Code code, Code assn)
	throws ScriptException{
		Class<?> clas = code.isNull()?
		code.getType() : code.getValue().getClass();
		try{
			Field field = clas.getField(name);
			field.set(code.getValue(), assn.getValue());
		}catch(Exception ex){
			ex.setStackTrace(new StackTraceElement[0]);
			throw new ScriptException(ex);
		}
	}
}