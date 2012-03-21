/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.shell;

import javax.swing.JMenuItem;
import javax.swing.JButton;

/**
 *シェルに統合される各種コマンドの基底クラスです。
 *継承クラスは、その短縮名をもってコマンド名とします。
 *
 *@author 東大アマチュア無線クラブ
 *@since 2011年8月31日
 */
public abstract class Command {
	/**
	 *このオブジェクトが担当するコマンドを処理します。
	 *
	 *@param args コマンドへの引数
	 *@throws Exception この処理が発生しうる例外
	 */
	public abstract void process(Object... args) throws Exception;
}
