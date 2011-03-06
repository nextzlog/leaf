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

/**
*AriCE言語の中間言語コードの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月29日
*/
final class Code{
	
	private final Object value;
	private final Class  type;
	
	/**
	*値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(Object value){
		this.value = value;
		this.type  = (value!=null)?value.getClass():Object.class;
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
	*ショート値を指定してコードを生成します。
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
	*文字コード値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(char value){
		this.value = value;
		this.type  = char.class;
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
	*真偽値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(boolean value){
		this.value = value;
		this.type  = boolean.class;
	}
	/**
	*値とクラスを指定してコードを生成します。
	*@param value 値
	*@param type 値のクラス
	*/
	public Code(Object value, Class type){
		this.value = value;
		this.type  = type;
	}
	/**
	*コードの値を返します。
	*@return 値
	*/
	public Object getValue(){
		return value;
	}
	/**
	*コードのクラスを返します。
	*@return クラス
	*/
	public Class getType(){
		return type;
	}
	/**
	*コードの表すクラスを返します。
	*用途が限定されているので例外をスローしません。
	*@return クラス
	*/
	public Class toClass(){
		return (Class)value;
	}
	/**
	*コードの整数値を返します。
	*@return 整数値
	*@throws ScriptException
	*/
	public int toInteger() throws ScriptException{
		if(value instanceof Number){
			return ((Number)value).intValue();
		}
		throw error("\"" + value + "\" is not an integer.");
	}
	/**
	*コードの数値を返します。
	*@return 数値
	*@throws ScriptException
	*/
	public double toDouble() throws ScriptException{
		if(value instanceof Number){
			return ((Number)value).doubleValue();
		}
		throw error("\"" + value + "\" is not a number.");
	}
	/**
	*コードの真偽値を返します。
	*@return 真偽値
	*@throws ScriptException
	*/
	public boolean isTrue() throws ScriptException{
		if(value instanceof Boolean){
			return (Boolean)value;
		}else{
			throw error("\"" + value + "\" is not a boolean.");
		}
	}
	/**
	*コードが空でないか返します。
	*@return 空の場合true
	*/
	public boolean isNull(){
		return (value == null);
	}
	/**
	*コードの文字列化表現を返します。
	*@return 文字列表現
	*/
	public String toString(){
		try{
			return value.toString();
		}catch(NullPointerException ex){
			return "null";
		}
	}
	/**例外を生成します*/
	private ScriptException error(Object msg){
		return new ScriptException(msg.toString());
	}
}