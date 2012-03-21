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
import static leaf.script.common.util.LeafReflectUnit.newInstance;

import java.util.List;
import javax.script.ScriptException;

/**
 *構文解析木でインスタンス生成を表現する実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月29日
 */
public final class New extends Node{
	private final Class clazz;
	private final List<Node> args;
	/**
	*ノードを生成します。
	*@param clas クラス
	*@param args コンストラクタの引数
	*/
	public New(Class clas, List<Node> args){
		this.clazz = clas;
		this.args  = args;
	}
	/**
	*解析木の値を再帰的に計算して返します。
	*@return 式の値
	*@throws ScriptException 計算規則違反時
	*/
	public Code value() throws ScriptException{
		Code[] codes = new Code[args.size()];
		int index = 0;
		for(Node arg : args)
			codes[index++] = arg.value();
		return newInstance(clazz, codes);
	}
	/**
	*ノードの前置記法による文字列化表現を返します。
	*@return このノードの文字列化表現
	*/
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(new ");
		sb.append(clazz.getCanonicalName());
		for(Node node : args)
			sb.append(' ').append(node);
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