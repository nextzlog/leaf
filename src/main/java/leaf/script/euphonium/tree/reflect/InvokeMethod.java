/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium.tree.reflect;

import leaf.script.common.tree.*;
import leaf.script.common.util.Code;
import static leaf.script.common.util.LeafReflectUnit.invokeMethod;

import java.util.List;
import javax.script.ScriptException;

/**
 *構文解析木でメソッド呼び出しを表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月28日
 */
public final class InvokeMethod extends Node{
	private final String name;
	private final Node object;
	private final List<Node> args;
	/**
	*子を指定してノードを生成します。
	*@param name メソッドの名前
	*@param obj  メソッドが属するオブジェクト
	*@param args メソッドに渡す引数
	*/
	public InvokeMethod(String name, Node obj, List<Node> args){
		this.name = name;
		this.object = obj;
		this.args = args;
	}
	/**
	*解析木の値を再帰的に計算して返します。
	*@return 式の値
	*@throws ScriptException 計算規則違反時
	*/
	public Code value() throws ScriptException{
		Code[] codes = new Code[args.size()];
		int index = 0;
		for(Node arg : args)codes[index++] = arg.value();
		return invokeMethod(name, object.value(), codes);
	}
	/**
	*ノードの前置記法による文字列化表現を返します。
	*@return このノードの文字列化表現
	*/
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(name);
		sb.append(' ');
		sb.append(object);
		for(Node arg : args)sb.append(' ').append(arg);
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
	public final Node fold() throws ScriptException{
		int index = 0;
		for(Node arg : args){
			args.set(index++, arg.fold());
		}
		return this;
	}
}