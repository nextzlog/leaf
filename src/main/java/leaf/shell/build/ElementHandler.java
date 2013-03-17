/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.shell.build;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

/**
 * ビルド文書解析時に各種要素毎の処理を実行します。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年12月11日
 */
abstract class ElementHandler extends Handler<StartElement>{
	private Map<QName, AttributeHandler> handlers = null;
	
	/**
	 * このハンドラーに各属性に対応する隷下ハンドラーを追加します。
	 * 
	 * @param handler 追加するハンドラー
	 */
	protected final void addHandler(AttributeHandler handler){
		if(handlers == null){
			handlers = new HashMap<QName, AttributeHandler>();
		}
		handlers.put(handler.name(), handler);
	}
	/**
	 * 指定した属性名に対応する隷下属性ハンドラーを返します。
	 * 
	 * @param name 解析する属性
	 * @return 対応する属性ハンドラー
	 */
	protected final AttributeHandler getHandler(QName name){
		return (handlers == null)? null : handlers.get(name);
	}
	
	/**
	 * 指定されたコンポーネントにセパレータを追加します。
	 * 
	 * @param comp コンポーネント
	 */
	public void addSeparator(JComponent comp){
		if(comp instanceof JToolBar){
			((JToolBar)comp).addSeparator();
		}
		else if (comp instanceof JMenu){
			((JMenu)comp).addSeparator();
		}
		else if (comp instanceof JPopupMenu){
			((JPopupMenu)comp).addSeparator();
		}
		else comp.add(new JSeparator()); // uncommon
	}
}