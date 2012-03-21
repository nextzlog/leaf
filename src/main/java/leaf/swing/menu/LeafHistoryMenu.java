/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.menu;

import java.awt.event.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.util.List;
import java.util.ArrayList;

import leaf.manager.LeafLocalizeManager;

/**
 *「最近使ったファイル」など、履歴を表示するメニューです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.0 修正：2011年3月28日
 *@see HistoryMenuListener
 */
public class LeafHistoryMenu extends JMenu{
	private final LeafLocalizeManager localize;
	private List<Object> list;
	
	/**
	 *同時に表示される履歴の最大数です。
	 */
	public static final int HISTORY_MAX = 16;
	
	/**
	 *空の履歴メニューを生成します。
	 */
	public LeafHistoryMenu(){
		this(null);
	}
	/**
	 *初期履歴を指定して履歴メニューを生成します。
	 *
	 *@param arr 履歴
	 */
	public LeafHistoryMenu(Object[] arr){
		super("History");
		localize = LeafLocalizeManager.getInstance(LeafHistoryMenu.class);
		setText(localize.translate("menu_text"));
		setMnemonic(KeyEvent.VK_H);
		list = new ArrayList<Object>();
		addAll(arr);
	}
	/**
	 *履歴リストを設定します。
	 *@param arr 履歴の配列
	 */
	public void addAll(Object[] arr){
		list.clear();
		if(arr != null) for(Object a : arr) list.add(a);
		update();
	}
	/**
	 *履歴の末尾にアイテムを追加します。
	 *
	 *@param item 追加履歴
	 */
	public void addItem(Object item){
		list.remove(item);
		list.add(0, item);
		if(list.size() > HISTORY_MAX) list.remove(HISTORY_MAX);
		update();
	}
	/**
	 *履歴を全て消去します。
	 */
	public void clear(){
		list.clear();
		update();
	}
	/**
	 *履歴の表示を更新します。
	 */
	private void update(){
		int size = Math.min(HISTORY_MAX, list.size());
		removeAll();
		for(int i=0; i<size; i++) add(createHistoryMenuItem(list.get(i), i));
		addSeparator();
		add(createClearMenuItem());
		setEnabled(list.size() > 0);
	}
	/**
	 *各履歴に対応するメニューアイテムを生成します。
	 *
	 *@param item 対応するアイテム
	 *@param num アイテムの番号
	 */
	private JMenuItem createHistoryMenuItem(final Object item, int num){
		String text = String.valueOf(item);
		String index = Integer.toHexString(num).toUpperCase();
		JMenuItem menuItem = new JMenuItem(index + "  " + text);
		menuItem.setMnemonic(index.charAt(0));
		menuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				fireHistoryClicked(item);
			}
		});
		return menuItem;
	}
	/**
	 *履歴消去のメニューアイテムを生成します。
	 *@return アイテム
	 */
	private JMenuItem createClearMenuItem(){
		JMenuItem item = new JMenuItem(localize.translate("clear_text"));
		item.setMnemonic(KeyEvent.VK_C);
		item.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				list.clear();
				update();
			}
		});
		return item;
	}
	/**
	 *HistoryMenuListenerを呼び出します。
	 *
	 *@param item クリックされたアイテム
	 */
	private void fireHistoryClicked(Object item){
		final HistoryMenuEvent e = new HistoryMenuEvent(this, item);
		HistoryMenuListener[] listeners = 
		listenerList.getListeners(HistoryMenuListener.class);
		for(HistoryMenuListener l : listeners) l.historyClicked(e);
	}
	/**
	 *HistoryMenuListenerを追加します。
	 *
	 *@param listener リスナー
	 */
	public void addHistoryMenuListener(HistoryMenuListener listener){
		listenerList.add(HistoryMenuListener.class, listener);
	}
	/**
	 *HistoryMenuListenerを削除します。
	 *
	 *@param listener 削除するリスナー
	 */
	public void removeHistoryMenuListener(HistoryMenuListener listener){
		listenerList.remove(HistoryMenuListener.class, listener);
	}
	/**
	 *履歴を返します。
	 *
	 *@return 履歴
	 */
	public Object[] getAll(){
		return list.toArray();
	}
}
