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
 *構文解析木で条件分岐構文を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年11月26日
 */
public final class If extends Statement{
	private Node cond;
	private Statement node_t, node_f;
	
	/**
	 *条件式と真文、偽文を指定してノードを生成します。
	 *@param cond 条件式
	 *@param node_t 真文 null可
	 *@param node_f 偽文 null可
	 */
	public If(Node cond, Statement node_t, Statement node_f){
		this.cond = cond;
		this.node_t = node_t;
		this.node_f = node_f;
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(if ");
		sb.append(cond);
		if(node_t != null) sb.append(' ').append(node_t);
		if(node_f != null) sb.append(' ').append(node_f);
		return new String(sb.append(")"));
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public void bind(Code name, Code value){
		cond.bind(name, value);
		if(node_t != null) node_t.bind(name, value);
		if(node_f != null) node_f.bind(name, value);
	}
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public void replace(Code from, Code to){
		cond.replace(from, to);
		if(node_t != null) node_t.replace(from, to);
		if(node_f != null) node_f.replace(from, to);
	}
	/**
	 *式の中の定数式を畳みこみます。
	 *@return 最適化済みの解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public Node fold() throws ScriptException{
		cond = cond.fold();
		if(node_t != null) node_t = (Statement) node_t.fold();
		if(node_f != null) node_f = (Statement) node_f.fold();
		return this;
	}
	/**
	 *制御戻し文に制御が必ず到達するか確認します。
	 *@return 制御戻し文に制御が必ず到達するなら真
	 */
	public boolean checkReturn(){
		if(node_t != null && node_f != null){
			boolean cr_t = node_t.checkReturn();
			boolean cr_f = node_f.checkReturn();
			return cr_t && cr_f;
		}else return false;
	}
	/**
	 *デッドコードを削除して最適化処理を施します。
	 *@return 最適化された解析木
	 */
	public Statement removeDeadNode(){
		if(node_t != null) node_t = node_t.removeDeadNode();
		if(node_f != null) node_f = node_f.removeDeadNode();
		if(cond instanceof LiteralNode) try{
			if(cond.value().toBoolean())
			     return node_t;
			else return node_f;
		}catch(ScriptException ex){}
		return this;
	}
}