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
 *構文解析木のノードの基底実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月12日
 */
public abstract class Node{
	
	/**
	 *構文解析木を生成します。
	 */
	public Node(){}
	
	/**
	 *解析木の値を再帰的に計算して返します。
	 *@return 式の値
	 *@throws ScriptException 計算規則違反時
	 */
	public abstract Code value() throws ScriptException;
	
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public abstract String toPrefixString();
	
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return 文字列化表現
	 */
	@Override public final String toString(){
		return toPrefixString();
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public abstract void bind(Code name, Code value);
	
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public abstract void replace(Code from, Code to);
	
	/**
	 *定数式を畳み込んで最適化処理を施します。
	 *@return 最適化された解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public abstract Node fold() throws ScriptException;
}