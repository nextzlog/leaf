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
 *構文解析木で文を表現するノードの基底クラスです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年11月26日
 */
public abstract class Statement extends Node{
	
	/**
	 *文の木を生成します。
	 */
	public Statement(){}
	
	/**
	 *このメソッド呼び出しは例外を発生します。
	 *@throws ScriptException 必ず発生する例外
	 */
	public final Code value() throws ScriptException{
		throw new ScriptException("undefined");
	}
	/**
	 *制御戻し文に制御が必ず到達するか確認します。
	 *@return 制御戻し文に制御が必ず到達するなら真
	 */
	public abstract boolean checkReturn();
	
	/**
	 *デッドコードを削除して最適化処理を施します。
	 *@return 最適化された解析木
	 */
	public abstract Statement removeDeadNode();
}
