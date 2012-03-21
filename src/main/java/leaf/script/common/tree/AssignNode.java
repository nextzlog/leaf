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

import java.util.List;
import javax.script.ScriptException;

/**
 *構文解析木で代入文を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年11月27日
 */
public final class AssignNode extends Node{
	private Node var, exp;
	
	/**
	 *変数木と式を指定してノードを生成します。
	 *@param var 変数木
	 *@param exp 代入式
	 */
	public AssignNode(VariableNode var, Node exp){
		this.var = var;
		this.exp = exp;
	}
	/**
	 *代入対象の変数木を返します。
	 *@return 変数木
	 */
	public VariableNode name(){
		return (VariableNode)var;
	}
	/**
	 *子を返します。
	 *@return 代入式
	 */
	public Node child(){
		return exp;
	}
	/**
	 *解析木の値を再帰的に計算して返します。
	 *@return 式の値
	 *@throws ScriptException 計算規則違反時
	 */
	public Code value() throws ScriptException{
		return exp.value();
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(assign ");
		sb.append(var);
		sb.append(' ');
		sb.append(exp);
		return new String(sb.append(")"));
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public void bind(Code name, Code value){
		exp.bind(name, value);
	}
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public void replace(Code from, Code to){
		exp.replace(from, to);
	}
	/**
	 *定数式を畳み込んで最適化処理を施します。
	 *@return 最適化された解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public Node fold() throws ScriptException{
		exp = exp.fold();
		return this;
	}
}