/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.common.util;

import leaf.script.common.vm.Closure;

import java.io.Serializable;
import javax.script.ScriptException;
import static leaf.manager.LeafReflectManager.isCastable;

/**
 *プログラム処理系で用いられる中間言語コードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月29日
 */
public final class Code implements Serializable{
	private final Object value;
	private final Class  type;
	
	/**
	*値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(Object value){
		if((this.value = value) != null){
			this.type = value.getClass();
		}else{
			this.type = Object.class;
		}
	}
	/**
	*真偽値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(boolean value){
		this.value = value;
		this.type  = boolean.class;
	}
	/**
	*文字値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(char value){
		this.value = value;
		this.type  = char.class;
	}
	/**
	*バイト値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(byte value){
		this.value = value;
		this.type  = byte.class;
	}
	/**
	*整数値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(short value){
		this.value = value;
		this.type  = short.class;
	}
	/**
	*整数値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(int value){
		this.value = value;
		this.type  = int.class;
	}
	/**
	*倍精度整数値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(long value){
		this.value = value;
		this.type  = long.class;
	}
	/**
	*浮動小数点数値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(float value){
		this.value = value;
		this.type = float.class;
	}
	/**
	*倍精度浮動小数点数値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(double value){
		this.value = value;
		this.type  = double.class;
	}
	/**
	*値とクラスを指定してコードを生成します。
	*@param value 値
	*@param type 値のクラス
	*@throws ScriptException 型が不適切な場合
	*/
	public Code(Object value, Class type)
	throws ScriptException{
		this.type  = type;
		this.value = value;
		if(!isCastable(value, type)){
			throw error(value, type);
		}
	}
	/**
	*コードの内容が空でないか返します。
	*@return 空の場合true
	*/
	public boolean isNull(){
		return (value == null);
	}
	/**
	*コードの内容をオブジェクトとして返します。
	*@return コードの値
	*/
	public Object getValue(){
		return value;
	}
	/**
	*コードの内容の型を返します。
	*@return コードの値のクラス
	*/
	public Class getType(){
		return type;
	}
	/**
	*コードの内容が数値であるか返します。
	*@return 数値である場合true
	*/
	public boolean isNumber(){
		return (value instanceof Number);
	}
	/**
	*コードの内容が真偽値であるか返します。
	*@return 真偽値である場合true
	*/
	public boolean isBoolean(){
		return (value instanceof Boolean);
	}
	/**
	*コードの内容をClassオブジェクトとして返します。
	*@return クラス
	*@throws ScriptException 型が不適切な場合
	*/
	public Class toClass() throws ScriptException{
		try{
			return (Class)value;
		}catch(ClassCastException ex){
			throw error(value, Class.class);
		}
	}
	/**
	*コードの内容を整数値として返します。
	*@return 整数値
	*@throws ScriptException 型が不適切な場合
	*/
	public int toInt() throws ScriptException{
		try{
			return ((Number)value).intValue();
		}catch(ClassCastException ex){
			throw error(value, Number.class);
		}
	}
	/**
	*コードの内容を倍精度整数値として返します。
	*@return 倍精度整数値
	*@throws ScriptException 型が不適切な場合
	*/
	public long toLong() throws ScriptException{
		try{
			return ((Number)value).longValue();
		}catch(ClassCastException ex){
			throw error(value, Number.class);
		}
	}
	/**
	*コードの内容を小数値として返します。
	*@return 小数値
	*@throws ScriptException 型が不適切な場合
	*/
	public float toFloat() throws ScriptException{
		try{
			return ((Number)value).floatValue();
		}catch(ClassCastException ex){
			throw error(value, Number.class);
		}
	}
	/**
	*コードの内容を倍精度小数値として返します。
	*@return 倍精度小数値
	*@throws ScriptException 型が不適切な場合
	*/
	public double toDouble() throws ScriptException{
		try{
			return ((Number)value).doubleValue();
		}catch(ClassCastException ex){
			throw error(value, Number.class);
		}
	}
	/**
	*コードの内容を真偽値として返します。
	*@return 真偽値
	*@throws ScriptException 型が不適切な場合
	*/
	public boolean toBoolean() throws ScriptException{
		try{
			return (Boolean)value;
		}catch(ClassCastException ex){
			throw error(value, Boolean.class);
		}
	}
	/**
	*コードの内容を局所関数として返します。
	*@throws ScriptException 型が不適切な場合
	*/
	public Closure toClosure() throws ScriptException{
		try{
			return (Closure)value;
		}catch(ClassCastException ex){
			throw error(value, Closure.class);
		}
	}
	/**
	*コードの内容の文字列化表現を返します。
	*@return 文字列表現
	*/
	public String toString(){
		return String.valueOf(value);
	}
	/**
	*コードが指定されたオブジェクトと等値であるか返します。
	*@param obj 比較対象
	*@return 対象がコードかつ等値である場合true
	*@see #equals(Object)
	*/
	public boolean equals(Object obj){
		try{
			return value.equals(((Code)obj).getValue());
		}catch(NullPointerException ex){
			return (value == obj);
		}catch(ClassCastException ex){
			return false;
		}
	}
	/**
	*不正な型変換の例外を返します。
	*@param value 対象値
	*@param type  型
	*/
	private ScriptException error(Object value, Class type){
		return new ScriptException(
			value + "(" + value.getClass().getName()  +
			") is not an instance of " + type.getName()
		);
	}
}