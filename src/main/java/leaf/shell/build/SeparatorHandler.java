/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.shell.build;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

/**
 * セパレータを追加する記述に対するマーカーとなります。
 * 従ってこのハンドラーでは実質的な処理は行われません。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年12月11日
 */
final class SeparatorHandler extends ElementHandler{
	/**
	 * ハンドラーを構築します。
	 */
	public SeparatorHandler(){}
	/**
	 * このハンドラーが処理する要素の名前を返します。
	 * 
	 * @return item
	 */
	@Override public QName name(){
		return new QName("separator");
	}
	/**
	 * スタックにプッシュするためのダミーを返します。
	 * 
	 * @param e 受け取るイベント
	 * @return nullでないダミーコンポーネント
	 */
	@Override public JComponent handle(StartElement e){
		return new JSeparator(); // dummy (not null)
	}
}
