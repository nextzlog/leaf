/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.common.tree;

import leaf.script.common.util.Code;
import javax.script.ScriptException;

/**
 *構文解析木で二項関係演算子を表現するノードの基底実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月21日
 */
public abstract class RelationalNode extends BinaryNode{
	/**
	 *左右の子を指定してノードを生成します。
	 *@param a 左の子
	 *@param b 右の子
	 */
	public RelationalNode(Node a, Node b){
		super(a, b);
	}
}