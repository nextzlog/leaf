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

/**
*AriCE言語の中間言語コードの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月29日
*/
class Code{
	
	private final Object value;
	
	/**
	*値を指定してコードを生成します。
	*@param value 値
	*/
	public Code(Object value){
		this.value = value;
	}
	/**
	*整数値を返します。
	*@return 整数値
	*@throws ScriptException キャストできない時
	*/
	public int toInteger() throws ScriptException{
		if(value instanceof Number){
			return ((Number)value).intValue();
		}
		throw error("Failed to cast \"" + value + "\" as an integer.");
	}
	/**
	*浮動小数点数値を返します。
	*@return 浮動小数点数値
	*@throw ScriptException キャストできない時
	*/
	public double toDouble() throws ScriptException{
		if(value instanceof Number){
			return ((Number)value).doubleValue();
		}
		throw error("Failed to cast \"" + value + "\" as an number.");
	}
	/**
	*文字列値を返します。
	*@return 文字列値
	*/
	public String toString(){
		return value.toString();
	}
	/**例外を生成します*/
	private ScriptException error(String msg){
		return new ScriptException(msg, null, 0, 0);
	}
}