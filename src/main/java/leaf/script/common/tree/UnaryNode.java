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
 *構文解析木で単項演算子を表現するノードの基底実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月21日
 */
public abstract class UnaryNode extends Node{
	private Node child;
	
	/**
	 *子を指定してノードを生成します。
	 *@param child 子
	 */
	public UnaryNode(Node child){
		this.child = child;
	}
	/**
	 *子を返します。
	 */
	public final Node child(){
		return child;
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public final void bind(Code name, Code value){
		child.bind(name, value);
	}
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public final void replace(Code from, Code to){
		child.bind(from, to);
	}
	/**
	 *式の中の定数式を畳みこみます。
	 *@return 最適化済みの解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public final Node fold() throws ScriptException{
		child = child.fold();
		if(child instanceof LiteralNode){
			return new LiteralNode(value());
		}
		return this;
	}
}