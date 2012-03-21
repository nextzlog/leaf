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
import static leaf.script.common.util.LeafCalcUnit.increment;

import javax.script.ScriptException;

/**
 *構文解析木でインクリメント演算子を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年11月27日
 */
public final class Increment extends UnaryNode{
	/**
	 *子を指定してノードを生成します。
	 *@param child 子
	 */
	public Increment(Node child){
		super(child);
	}
	/**
	 *解析木の値を再帰的に計算して返します。
	 *@return 式の値
	 *@throws ScriptException 計算規則違反時
	 */
	public Code value() throws ScriptException{
		return increment(child().value());
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(++ ");
		sb.append(child().toPrefixString());
		return new String(sb.append(")"));
	}
}