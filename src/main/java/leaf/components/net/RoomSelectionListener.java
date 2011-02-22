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
package leaf.components.net;

/**
*ルームがクリックされたときに呼び出されるリスナーです
*/
public interface RoomSelectionListener{
	/**
	*ルームがクリックされたときに呼び出されます。
	*@param roomname クリックされたルーム名
	*/
	public void roomSelected(String roomname);
}
