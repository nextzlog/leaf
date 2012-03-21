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
 *構文解析木で制御戻し構文を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年11月26日
 */
public final class Return extends Statement{
	private Node exp;
	
	/**
	 *返り値の式を指定してノードを生成します。
	 *@param exp 戻り値の式
	 */
	public Return(Node exp){
		this.exp = exp;
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(return ");
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
	 *式の中の定数式を畳みこみます。
	 *@return 最適化済みの解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public Node fold() throws ScriptException{
		exp = exp.fold();
		return this;
	}
	/**
	 *制御戻し文に制御が必ず到達するか確認します。
	 *@return 制御戻し文に制御が必ず到達するなら真
	 */
	public boolean checkReturn(){
		return true;
	}
	/**
	 *デッドコードを削除して最適化処理を施します。
	 *@return 最適化された解析木
	 */
	public Statement removeDeadNode(){
		return this;
	}
}