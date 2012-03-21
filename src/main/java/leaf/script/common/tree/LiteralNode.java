/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.common.tree;

import leaf.script.common.util.Code;
import javax.script.ScriptException;

/**
 *構文解析木で定数を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月12日
 */
public final class LiteralNode extends Node{
	private Code value;
	
	/**
	 *定数値を指定してノードを生成します。
	 *@param value 定数値
	 */
	public LiteralNode(Code value){
		this.value = value;
	}
	/**
	 *真偽値を指定してノードを生成します。
	 *@param value 真偽値
	 */
	public LiteralNode(boolean value){
		this(new Code(value));
	}
	/**
	 *文字値を指定してノードを生成します。
	 *@param value 文字値
	 */
	public LiteralNode(char value){
		this(new Code(value));
	}
	/**
	 *整数値を指定してノードを生成します。
	 *@param value 整数値
	 */
	public LiteralNode(int value){
		this(new Code(value));
	}
	/**
	 *小数値を指定してノードを生成します。
	 *@param value 小数値
	 */
	public LiteralNode(double value){
		this(new Code(value));
	}
	/**
	 *文字列値を指定してノードを生成します。
	 *@param value 文字列値
	 */
	public LiteralNode(String value){
		this(new Code(value));
	}
	/**
	 *ノードの定数値を返します。
	 *@return 式の値
	 */
	public Code value(){
		return value;
	}
	/**
	 *前置記法の文字列表現を返します。
	 *@return このノードの表現
	 */
	public String toPrefixString(){
		return String.valueOf(value);
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public void bind(Code name, Code value){}
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public void replace(Code from, Code to){
		try{
			if(value.equals(from)) value = to;
		}catch(NullPointerException ex){
			if(value == from) value = to;
		}
	}
	/**
	 *定数式を畳み込んで最適化処理を施します。
	 *@return 最適化された解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public Node fold() throws ScriptException{
		return this;
	}
}