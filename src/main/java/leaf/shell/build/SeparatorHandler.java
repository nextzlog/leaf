/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.shell.build;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

/**
 *"separator"要素の開始イベントを処理します。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
class SeparatorHandler extends ElementHandler{
	/**
	 *ハンドラーを構築します。
	 */
	public SeparatorHandler(){}
	/**
	 *このハンドラーが処理する要素の名前を返します。
	 *
	 *@return item
	 */
	@Override public QName name(){
		return new QName("separator");
	}
	/**
	 *イベントオブジェクトを受け取って処理を実行します。
	 *
	 *@param e 受け取るイベント
	 *@return 処理の結果生成されたコンポーネント
	 *@throws Exception この処理が発生しうる例外
	 */
	@Override public JComponent handle(StartElement e) throws Exception{
		return new JSeparator();
	}
}
