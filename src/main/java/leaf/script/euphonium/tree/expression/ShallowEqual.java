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
import javax.script.ScriptException;

/**
 *構文解析木で物理的等価(浅い等価)演算子を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月21日
 */
public final class ShallowEqual extends RelationalNode{
	/**
	 *左右の子を指定してノードを生成します。
	 *@param a 左の子
	 *@param b 右の子
	 */
	public ShallowEqual(Node a, Node b){
		super(a, b);
	}
	/**
	 *解析木の値を再帰的に計算して返します。
	 *@return 式の値
	 *@throws ScriptException 計算規則違反時
	 */
	public Code value() throws ScriptException{
		Object left = left().value().getValue();
		Object right = right().value().getValue();
		return new Code(left == right);
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(shallow-equal ");
		sb.append(left().toPrefixString());
		sb.append(" ");
		sb.append(right().toPrefixString());
		sb.append(")");
		return new String(sb);
	}
}