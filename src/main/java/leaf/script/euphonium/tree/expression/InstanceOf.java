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
import static leaf.manager.LeafReflectManager.isCastable;

import javax.script.ScriptException;

/**
 *構文解析木で演算子「instanceof」を表現するノードの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月30日
 */
public final class InstanceOf extends BinaryNode{
	/**
	 *左右の子を指定してノードを生成します。
	 *@param a 左の子
	 *@param b 右の子(型を示す子)
	 */
	public InstanceOf(Node a, LiteralNode b){
		super(a, b);
	}
	/**
	 *解析木の値を再帰的に計算して返します。
	 *@return 式の値
	 *@throws ScriptException 計算規則違反時
	 */
	public Code value() throws ScriptException{
		Object value = left().value().getValue();
		Class  clazz = right().value().toClass();
		return new Code(isCastable(value, clazz));
	}
	/**
	 *ノードの前置記法による文字列化表現を返します。
	 *@return このノードの文字列化表現
	 */
	public String toPrefixString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(instanceof ");
		sb.append(left().toPrefixString());
		sb.append(' ');
		sb.append(right().toPrefixString());
		sb.append(")");
		return new String(sb);
	}
}