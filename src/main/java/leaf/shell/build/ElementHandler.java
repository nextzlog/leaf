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
import javax.xml.stream.events.StartElement;

/**
 *ビルド文書解析時に各種要素毎の処理を実行します。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
abstract class ElementHandler extends Handler<StartElement>{
	private Map<QName, AttributeHandler> handlers = null;
	
	/**
	 *このハンドラーに各属性に対応する隷下ハンドラーを追加します。
	 *
	 *@param handler 追加するハンドラー
	 */
	protected final void addHandler(AttributeHandler handler){
		if(handlers == null){
			handlers = new HashMap<QName, AttributeHandler>();
		}
		handlers.put(handler.name(), handler);
	}
	/**
	 *指定した属性名に対応する隷下属性ハンドラーを返します。
	 *
	 *@param name 解析する属性
	 *@return 対応する属性ハンドラー
	 */
	protected final AttributeHandler getHandler(QName name){
		return (handlers == null)? null : handlers.get(name);
	}
}