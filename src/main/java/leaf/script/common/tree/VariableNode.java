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
 *構文解析木で変数を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月12日
 */
public final class VariableNode extends Node{
	private Code name, value;
	
	/**
	 *変数名を指定してノードを生成します。
	 *@param name 変数名
	 */
	public VariableNode(Code name){
		this.name  = name;
	}
	/**
	 *ノードの変数値を返します。
	 *@return 変数の値
	 *@throws ScriptException 値が束縛されていない場合
	 */
	public Code value() throws ScriptException{
		if(value != null) return value;
		throw new ScriptException(name + " is not bound.");
	}
	/**
	 *前置記法の文字列表現を返します。
	 *@return このノードの表現
	 */
	public String toPrefixString(){
		return String.valueOf(name);
	}
	/**
	 *式の中の該当する変数を全て束縛します。
	 *束縛すると変数は定数として扱われます。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public final void bind(Code name, Code value){
		if(this.name.equals(name)){
			this.value = value;
		}
	}
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
		if(value == null) return this;
		return new LiteralNode(value);
	}
}