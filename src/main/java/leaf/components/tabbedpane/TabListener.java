/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.components.tabbedpane;

import java.awt.Component;
import leaf.icon.*;

/**
*タブ項目が閉じられた時に呼び出されるリスナーです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年3月12日
*@see LeafTabbedPane
*/
public interface TabListener{
	/**
	*タブ項目の「閉じる」ボタンが押されたことを通知します。
	*/
	public boolean tabClosing(Component tab);
}