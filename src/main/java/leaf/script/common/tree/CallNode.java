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
 *構文解析木で関数呼び出しを表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年11月27日
 */
public final class CallNode extends Node{
	private final Node func;
	private final List<Node> args;
	
	/**
	 *関数木と引数を指定してノードを生成します。
	 *@param func 関数名または関数の定義を示す木
	 *@param args 引数のリスト
	 */
	public CallNode(Node func, List<Node> args){
		this.func = func;
		this.args = args;
	}
	/**
	 *呼び出す対象の関数木を返します。
	 *@return 関数名または関数の定義を示す木
	 */
	public Node getFunctionNode(){
		return func;
	}
	/**
	 *関数に渡す引数のリストを返します。
	 *@return 引数リスト
	 */
	public List<Node> getArguments(){
		return args;
	}
	/**
	 *このメソッド呼び出しは例外を発生します。
	 *@throws ScriptException 必ず発生する例外
	 */
	public Code value() throws ScriptException{
		throw new ScriptException("undefined");
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(call ");
		sb.append(func);
		for(Node arg : args) sb.append(arg);
		return new String(sb.append(")"));
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public void bind(Code name, Code value){
		for(Node arg : args) arg.bind(name, value);
	}
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public void replace(Code from, Code to){
		for(Node arg : args) arg.replace(from, to);
	}
	/**
	 *定数式を畳み込んで最適化処理を施します。
	 *@return 最適化された解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public Node fold() throws ScriptException{
		int index = 0;
		for(Node arg : args){
			args.set(index++, arg.fold());
		}
		return this;
	}
}