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
package leaf.components.taskpane;

import java.util.*;

/**
*LeafExpandPaneの展開状態の変更を表現するイベントクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年7月10日
*@see LeafTaskPane
*@see LeafExpandPane
*/
public class ExpandEvent extends EventObject{
	public ExpandEvent(Object source){
		super(source);
	}
}