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
 *構文解析木で三項演算子を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月21日
 */
public final class TernaryNode extends Node{
	private Node cond, a, b;
	
	/**
	 *条件式及び左右の子を指定してノードを生成します。
	 *@param cond 条件式
	 *@param a 左の子
	 *@param b 右の子
	 */
	public TernaryNode(Node cond, Node a, Node b){
		this.cond = cond;
		this.a = a;
		this.b = b;
	}
	/**
	 *解析木の値を再帰的に計算して返します。
	 *@return 式の値
	 *@throws ScriptException 計算規則違反時
	 */
	public Code value() throws ScriptException{
		return (cond.value().toBoolean()? a: b).value();
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(if ");
		sb.append(cond.toPrefixString());
		sb.append(" ");
		sb.append(a.toPrefixString());
		sb.append(" ");
		sb.append(b.toPrefixString());
		sb.append(")");
		return new String(sb);
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public void bind(Code name, Code value){
		cond.bind(name, value);
		a.bind(name, value);
		b.bind(name, value);
	}
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public void replace(Code from, Code to){
		cond.replace(from, to);
		a.replace(from, to);
		b.replace(from, to);
	}
	/**
	 *式の中の定数式を畳みこみます。
	 *@return 最適化済みの解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public Node fold() throws ScriptException{
		cond = cond.fold();
		a = a.fold(); b = b.fold();
		if(cond instanceof LiteralNode){
			return (cond.value().toBoolean())? a: b;
		}
		return this;
	}
}