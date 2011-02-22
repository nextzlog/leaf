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
package leaf.components.menu;

/**
*履歴が選択された時に呼び出される専用のリスナーです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月6日
*@see LeafHistoryMenu
*/
public interface HistoryMenuListener{
	/**
	*履歴が選択されたときに呼び出されます。
	*param filepath ユーザーによって選択されたファイル履歴
	*/
	public void historyClicked(String filepath);
}