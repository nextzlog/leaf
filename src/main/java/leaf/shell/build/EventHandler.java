/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.shell.build;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

/**
 *ビルド文書解析時の各種イベント毎の処理を最初に実行します。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
abstract class EventHandler {
	private Map<QName, ElementHandler> handlers = null;
	
	/**
	 *このハンドラーに各要素に対応する隷下ハンドラーを追加します。
	 *
	 *@param handler 追加するハンドラー
	 */
	protected final void addHandler(ElementHandler handler){
		if(handlers == null){
			handlers = new HashMap<QName, ElementHandler>();
		}
		handlers.put(handler.name(), handler);
	}
	/**
	 *指定した要素名に対応する隷下属性ハンドラーを返します。
	 *
	 *@param name 解析する属性
	 *@return 対応する要素ハンドラー
	 */
	protected final ElementHandler getHandler(QName name){
		return (handlers == null)? null : handlers.get(name);
	}
	/**
	 *イベントオブジェクトを受け取って処理を実行します。
	 *
	 *@param e 受け取るイベント
	 *@throws Exception この処理が発生しうる例外
	 */
	public abstract void handle(XMLEvent e) throws Exception;
}
