/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.shell.build;

import javax.swing.JComponent;
import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

/**
 * 各種イベント処理クラスの基底となるクラスです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年12月11日
 */
abstract class Handler<E extends XMLEvent> {
	/**
	 * このハンドラが担当するイベントの名前を返します。
	 * 
	 * @return イベント名
	 */
	public abstract QName name();
	
	/**
	 * イベントオブジェクトを受け取って処理を実行します。
	 * 
	 * @param e 受け取るイベント
	 * @return 処理の結果生成されたコンポーネント
	 * @throws Exception この処理が発生しうる例外
	 */
	public abstract JComponent handle(E e) throws Exception;
}