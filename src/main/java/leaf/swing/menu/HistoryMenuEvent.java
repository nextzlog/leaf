/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.menu;

import java.util.EventObject;

/**
 * 履歴がクリックされた時に通知されるイベントです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年5月6日
 *
 */
public class HistoryMenuEvent extends EventObject{
	private static final long serialVersionUID = 1L;
	private final Object item;
	
	/**
	 * イベント発生源と対応するアイテムを指定してイベントを発行します。
	 * 
	 * @param source イベントの発生源
	 * @param item イベントに対応する履歴アイテム
	 */
	public HistoryMenuEvent(Object source, Object item){
		super(source);
		this.item = item;
	}
	
	/**
	 * ユーザーが選択した履歴アイテムを返します。
	 * 
	 * @return イベントに対応する履歴アイテム
	 */
	public Object getItem(){
		return item;
	}

}