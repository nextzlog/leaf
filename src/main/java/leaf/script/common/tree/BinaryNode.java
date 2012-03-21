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
 *構文解析木で二項演算子を表現するノードの基底実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月12日
 */
public abstract class BinaryNode extends Node{
	private Node a, b;
	
	/**
	 *左右の子を指定してノードを生成します。
	 *@param a 左の子
	 *@param b 右の子
	 */
	public BinaryNode(Node a, Node b){
		this.a = a;
		this.b = b;
	}
	/**
	 *左の子を返します。
	 */
	public final Node left(){
		return a;
	}
	/**
	 *右の子を返します。
	 */
	public final Node right(){
		return b;
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public final void bind(Code name, Code value){
		a.bind(name, value);
		b.bind(name, value);
	}
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public final void replace(Code from, Code to){
		a.replace(from, to);
		b.replace(from, to);
	}
	/**
	 *式の中の定数式を畳みこみます。
	 *@return 最適化済みの解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public final Node fold() throws ScriptException{
		a = left().fold(); b = right().fold();
		if(a instanceof LiteralNode
		&& b instanceof LiteralNode){
			return new LiteralNode(value());
		}
		return this;
	}
}