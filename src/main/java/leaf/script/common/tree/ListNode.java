/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.common.tree;

import leaf.script.common.tree.*;
import leaf.script.common.util.Code;

import java.util.List;
import javax.script.ScriptException;

/**
 *構文解析木で文の並びを表現するノードの基底実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年11月26日
 */
public final class ListNode extends Statement{
	private final List<Statement> children;
	
	/**
	 *子を指定して文リストの木を生成します。
	 *@param children 子のリスト
	 */
	public ListNode(List<Statement> children){
		this.children = children;
	}
	/**
	 *文のリストを返します。
	 *@return このリストに含まれる文のリスト
	 */
	public List<Statement> getList(){
		return children;
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(list");
		final int size = children.size();
		for(int i=0; i<size; i++){
			sb.append(' ');
			sb.append(children.get(i).toPrefixString());
		}
		return new String(sb.append(")"));
	}
	/**
	 *文の中の変数を全て束縛します。
	 *@param name 変数名
	 *@param value 束縛値
	 */
	public void bind(Code name, Code value){
		final int size = children.size();
		for(int i=0; i<size; i++){
			children.get(i).bind(name, value);
		}
	}
	/**
	 *文の中の定数fromを定数toに置換します。
	 *@param from 検索する定数from
	 *@param to 置換後の定数to
	 */
	public void replace(Code from, Code to){
		final int size = children.size();
		for(int i=0; i<size; i++){
			children.get(i).replace(from, to);
		}
	}
	/**
	 *式の中の定数式を畳みこみます。
	 *@return 最適化済みの解析木
	 *@throws ScriptException 計算規則違反時
	 */
	public Node fold() throws ScriptException{
		for(Node child : children) child.fold();
		return this;
	}
	/**
	 *制御戻し文に制御が必ず到達するか確認します。
	 *@return 制御戻し文に制御が必ず到達するなら真
	 */
	public boolean checkReturn(){
		for(Statement child : children){
			if(child.checkReturn()) return true;
		}
		return false;
	}
	/**
	 *デッドコードを削除して最適化処理を施します。
	 *@return 最適化された解析木
	 */
	public Statement removeDeadNode(){
		boolean isDead = false;
		int index = 0;
		while(index < children.size()){
			Statement child = children.get(index);
			child = child.removeDeadNode();
			if(child != null){
				children.set (index++, child);
				if(child.checkReturn()) break;
			}else children.remove(index);
		}
		while(index < children.size()){
			children.remove(index);
		}
		if(children.size() > 1) return this;
		if(children.size() == 0) return null;
		else return children.get(0);
	}
}
