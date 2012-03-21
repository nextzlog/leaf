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

import java.util.List;
import javax.script.ScriptException;

/**
 *構文解析木でswitch文を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年11月26日
 */
public final class Switch extends Statement{
	private Default def;
	private Node cond;
	private final List<Case> cases;
	
	/**
	 *条件式と隷下の文を指定してノードを生成します。
	 *@param cond 条件式
	 *@param def  default文
	 *@param cases 各case文
	 */
	public Switch(Node cond, Default def, List<Case> cases){
		this.cond  = cond;
		this.def   = def;
		this.cases = cases;
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(switch ").append(cond);
		if(def != null) sb.append(' ').append(def);
		if(cases != null) for(Case c : cases){
			sb.append(' ').append(c);
		}
		return new String(sb.append(")"));
	}
	/**
	 *式の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public void bind(Code name, Code value){
		cond.bind(name, value);
		if(def != null) def.bind(name, value);
		if(cases != null) for(Case c : cases){
			c.bind(name, value);
		}
	}
	/**
	 *式の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public void replace(Code from, Code to){
		cond.replace(from, to);
		if(def != null) def.replace(from, to);
		if(cases != null) for(Case c : cases){
			c.replace(from, to);
		}
	}
	/**
	 *式の中の定数式を畳みこみます。
	 *@return 最適化済みの解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public Node fold() throws ScriptException{
		cond = cond.fold();
		if(def != null) def = (Default) def.fold();
		if(cases != null){
			int index = 0;
			for(Case c : cases){
				cases.set(index++,(Case) c.fold());
			}
		}
		return this;
	}
	/**
	 *制御戻し文に制御が必ず到達するか確認します。
	 *@return 制御戻し文に制御が必ず到達するなら真
	 */
	public boolean checkReturn(){
		boolean check = true;
		for(Case c : cases){
			check &= c.checkReturn();
			if (!check) return false;
		}
		return def != null && def.checkReturn();
	}
	/**
	 *デッドコードを削除して最適化処理を施します。
	 *@return 最適化された解析木
	 */
	public Statement removeDeadNode(){
		if(def != null) def.removeDeadNode();
		if(cases != null && !cases.isEmpty()){
			for(Case c : cases) c.removeDeadNode();
			return this;
		}else return def;
	}
}