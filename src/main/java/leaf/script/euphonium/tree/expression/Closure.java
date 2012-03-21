/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium.tree.expression;

import leaf.script.common.tree.*;
import leaf.script.common.util.Code;

import java.util.List;
import javax.script.ScriptException;

/**
 *構文解析木で第一級関数式を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年11月27日
 */
public final class Closure extends Node{
	private final int params, locals;
	private Statement body;
	
	/**
	 *変数の個数及び本文を指定してノードを生成します。
	 *@param params 引数の個数
	 *@param locals 変数の個数
	 *@param body 本文
	 */
	public Closure(int params, int locals, Statement body){
		this.params = params;
		this.locals = locals;
		this.body = body;
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
		sb.append("(closure (");
		sb.append(params);
		sb.append(")");
		if(body != null) sb.append(' ').append(body);
		return new String(sb.append(")"));
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public void bind(Code name, Code value){
		body.bind(name, value);
	}
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public void replace(Code from, Code to){
		body.replace(from, to);
	}
	/**
	 *式の中の定数式を畳みこみます。
	 *@return 最適化済みの解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public Node fold() throws ScriptException{
		body = (Statement) body.fold();
		return this;
	}
	/**
	 *制御戻し文に制御が必ず到達するか確認します。
	 *@return 制御戻し文に制御が必ず到達するなら真
	 */
	public boolean checkReturn(){
		return body.checkReturn();
	}
	/**
	 *デッドコードを削除して最適化処理を施します。
	 *@return 最適化された解析木
	 */
	public void removeDeadNode(){
		body = body.removeDeadNode();
	}
}