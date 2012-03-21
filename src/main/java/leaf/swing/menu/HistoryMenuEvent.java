/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.menu;

import java.util.EventObject;

/**
 *履歴が選択された時に通知されるイベントです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年5月6日
 *@see LeafHistoryMenu
 */
public class HistoryMenuEvent extends EventObject{
	private final Object item;
	/**
	 *発生源と対応するアイテムを指定してイベントを発行します。
	 *@param source イベントの発生源
	 *@param item イベントに対応する履歴アイテム
	 */
	public HistoryMenuEvent(Object source, Object item){
		super(source);
		this.item = item;
	}
	/**
	 *ユーザーが選択した履歴アイテムを返します。
	 *@return イベントに対応する履歴アイテム
	 */
	public Object getItem(){
		return item;
	}
}