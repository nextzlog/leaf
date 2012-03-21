/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium.tree.statement;

import leaf.script.common.tree.*;
import leaf.script.common.util.Code;

import javax.script.ScriptException;

/**
 *構文解析木でfor文を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年11月26日
 */
public final class For extends Statement{
	private Node init, cond, next;
	private Statement body;
	
	/**
	 *条件式と本文を指定してノードを生成します。
	 *@param cond 条件式
	 *@param body 本文
	 */
	public For(Node cond, Statement body){
		this(null, cond, null, body);
	}
	/**
	 *各制御式と本文を指定してノードを生成します。
	 *@param init 初期化式
	 *@param cond 条件式
	 *@param next 次状態式
	 *@param body 本文
	 */
	public For(Node init, Node cond, Node next, Statement body){
		this.init = init;
		this.cond = cond;
		this.body = body;
		this.next = next;
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(for");
		if(init != null) sb.append(' ').append(init);
		if(cond != null) sb.append(' ').append(cond);
		if(next != null) sb.append(' ').append(next);
		if(body != null) sb.append(' ').append(body);
		return new String(sb.append(")"));
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public void bind(Code name, Code value){
		if(init != null) init.bind(name, value);
		if(cond != null) cond.bind(name, value);
		if(body != null) body.bind(name, value);
		if(next != null) next.bind(name, value);
	}
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public void replace(Code from, Code to){
		if(init != null) init.replace(from, to);
		if(cond != null) cond.replace(from, to);
		if(body != null) body.replace(from, to);
		if(next != null) next.replace(from, to);
	}
	/**
	 *式の中の定数式を畳みこみます。
	 *@return 最適化済みの解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public Node fold() throws ScriptException{
		if(init != null) init = init.fold();
		if(cond != null) cond = cond.fold();
		if(body != null) body = (Statement) body.fold();
		if(next != null) next = next.fold();
		return this;
	}
	/**
	 *制御戻し文に制御が必ず到達するか確認します。
	 *@return 制御戻し文に制御が必ず到達するなら真
	 */
	public boolean checkReturn(){
		if(body != null && cond instanceof LiteralNode){
			try{
				if(cond.value().toBoolean())
					return body.checkReturn();
			}catch(ScriptException ex){}
		}
		return false;
	}
	/**
	 *デッドコードを削除して最適化処理を施します。
	 *@return 最適化された解析木
	 */
	public Statement removeDeadNode(){
		if(cond instanceof LiteralNode) try{
			if(!cond.value().toBoolean()) return null;
		}catch(ScriptException ex){}
		body = body.removeDeadNode();
		return this;
	}
}