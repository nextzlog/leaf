/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.com;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.event.*;

import static javax.swing.SwingConstants.VERTICAL;

/**
 * チェックボックスによる選択・選択解除操作を可能にするリストの実装です。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2011/09/12
 *
 */
public class LeafCheckList<E> extends JComponent implements Scrollable {
	private static final long serialVersionUID = 1L;
	private final Box box;
	private ListModel<E> data;
	private ListSelectionHandler handler;
	private ListSelectionModel selection;
	private LinkedList<CheckBoxItem> items;
	private final CheckItemListener itemListener;
	private final ModelObserver modelObserver;
	private int visibleRowCount = 8;
	
	/**
	 * 空のリストモデルでリストを構築します。
	 */
	public LeafCheckList() {
		this(new DefaultListModel<E>());
	}
	
	/**
	 * リストのモデルを指定してリストを構築します。
	 * 
	 * @param model モデル
	 */
	public LeafCheckList(ListModel<E> model) {
		setLayout(new BorderLayout());
		box = Box.createVerticalBox();
		add(box, BorderLayout.CENTER);
		box.setBackground(Color.WHITE);
		box.setOpaque(true);
		this.modelObserver = new ModelObserver();
		this.itemListener = new CheckItemListener();
		setSelectionModel(new DefaultListSelectionModel());
		setModel(model);
	}
	
	/**
	 * このリストにデータモデルを関連付けます。
	 * 
	 * @param model データモデル
	 */
	public void setModel(ListModel<E> model) {
		ListModel<E> old = this.data;
		items = new LinkedList<CheckBoxItem>();
		model.removeListDataListener(modelObserver);
		model.addListDataListener(modelObserver);
		
		box.removeAll();
		final int size = (this.data = model).getSize();
		for(int i = 0; i < size; i++) {
			Object value = model.getElementAt(i);
			CheckBoxItem cb = new CheckBoxItem(value);
			cb.setSelected(selection.isSelectedIndex(i));
			box.add(cb);
			items.add(cb);
		}
		firePropertyChange("model", old, model);
	}
	
	/**
	 * このリストに関連付けられたデータモデルを返します。
	 * 
	 * @return データモデル
	 */
	public ListModel<E> getModel() {
		return data;
	}
	
	private class CheckBoxItem extends JCheckBox {
		private static final long serialVersionUID = 1L;
		public CheckBoxItem(Object item) {
			super(String.valueOf(item));
			setOpaque(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setFocusable(false);
			setRequestFocusEnabled(false);
			addItemListener(LeafCheckList.this.itemListener);
		}
		
		public void setValue(E value) {
			setText(String.valueOf(value));
		}
	}
	
	private class CheckItemListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			JCheckBox cb = (JCheckBox)e.getItemSelectable();
			int index = items.indexOf(cb);
			if(cb.isSelected()) {
				selection.addSelectionInterval(index, index);
			} else {
				selection.removeSelectionInterval(index, index);
			}
		}
	}
	
	private class ModelObserver implements ListDataListener {
		@Override
		public void contentsChanged(ListDataEvent e) {
			final int index0 = e.getIndex0();
			final int index1 = e.getIndex1();
			for(int i=index0; i<=index1; i++) {
				E val = data.getElementAt(i);
				items.get(i).setValue(val);
			}
		}
		
		@Override
		public void intervalAdded(ListDataEvent e) {
			final int index0 = e.getIndex0();
			final int index1 = e.getIndex1();
			for(int i=index0; i<=index1; i++) {
				E val = data.getElementAt(i);
				CheckBoxItem cb = new CheckBoxItem(val);
				box.add(cb, i);
				items.add(i, cb);
			}
			box.revalidate();
		}
		
		@Override
		public void intervalRemoved(ListDataEvent e) {
			final int index0 = e.getIndex0();
			final int index1 = e.getIndex1();
			for(int i=index0; i<=index1; i++) {
				box.remove(items.remove(index0));
			}
			box.revalidate();
		}
	}
	
	private class ListSelectionHandler implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			final int first = e.getFirstIndex();
			final int last = e.getLastIndex();
			final boolean adj= e.getValueIsAdjusting();
			for(int i=first; i<=last; i++) {
				items.get(i).setSelected(selection.isSelectedIndex(i));
			}
			fireSelectionValueChanged(first, last, adj);
		}
	}
	
	/**
	 * データの選択状態に変更が加えられたことを通知します。
	 * 
	 * @param first 変更開始位置
	 * @param last  変更終了位置
	 * @param adj このイベントが一連の変更操作の一部である場合
	 */
	protected void fireSelectionValueChanged(int first, int last, boolean adj) {
		ListSelectionListener[] listeners
		= listenerList.getListeners(ListSelectionListener.class);
		ListSelectionEvent e = null;
		for(int i=listeners.length-1; i>=0; i--) {
			if(e == null) e = new ListSelectionEvent(this, first, last, adj);
			listeners[i].valueChanged(e);
		}
	}
	
	/**
	 * リストに選択モデルを設定します。
	 * 
	 * @param newModel 選択モデル
	 */
	public void setSelectionModel(ListSelectionModel newModel) {
		ListSelectionModel oldModel = this.selection;
		if(oldModel == null) {
			this.handler = new ListSelectionHandler();
		} else {
			oldModel.removeListSelectionListener(this.handler);
		}
		newModel.addListSelectionListener(this.handler);
		this.selection = newModel;
		firePropertyChange("selectionModel", oldModel, newModel);
	}
	
	/**
	 * リストの選択モデルを返します。
	 * 
	 * @return 選択モデル
	 */
	public ListSelectionModel getSelectionModel() {
		return selection;
	}
	
	/**
	 * 選択状態が変更されるたびに通知されるリストにリスナーを追加します。
	 * 
	 * @param listener 追加するリスナー
	 */
	public void addListSelectionListener(ListSelectionListener listener) {
		listenerList.add(ListSelectionListener.class, listener);
	}
	
	/**
	 * リストから選択リスナーを削除します。
	 * 
	 * @param listener 削除するリスナー
	 */
	public void removeListSelectionListener(ListSelectionListener listener) {
		listenerList.remove(ListSelectionListener.class, listener);
	}
	
	/**
	 * リスト内の指定された座標に最も近いセルのインデックスを返します。
	 * 
	 * @param location 座標
	 */
	public int locationToIndex(Point location) {
		final int size = data.getSize();
		int now = getInsets().top;
		int max = location.y;
		for(int i=0; i<size; i++) {
			if(now >= max) return i;
			now += items.get(i).getHeight();
		}
		return size - 1;
	}
	
	/**
	 * リスト内の指定されたインデックスのセルの座標範囲を返します。
	 * 
	 * @return セルの矩形範囲
	 */
	public Rectangle getCellBounds(int index) {
		if(index < 0) index = 0;
		if(index >= items.size()) index = items.size() -1;
		return items.isEmpty()? null : items.get(index).getBounds();
	}
	
	/**
	 * リスト内の指定されたインデックスのセルの原点座標を返します。
	 * 
	 * @param index インデックス
	 */
	public Point indexToLocation(int index) {
		return items.get(index).getLocation();
	}
	
	/**
	 * リスト内の指定されたインデックスが可視範囲に入るようにスクロールします。
	 * 
	 * @param index 見えるようにするインデックス
	 */
	public void ensureIndexIsVisible(int index) {
		super.scrollRectToVisible(getCellBounds(index));
	}
	
	/**
	 * スクロールしないで可視範囲に表示可能な最大行数を指定します。
	 * 
	 * @param visibleRowCount 最大行数
	 */
	public void setVisibleRowCount(int visibleRowCount) {
		final int old = this.visibleRowCount;
		this.visibleRowCount = visibleRowCount;
		firePropertyChange("visibleRowCount", old, visibleRowCount);
	}
	
	/**
	 * スクロールしないで可視範囲に表示可能な最大行数を返します。
	 * 
	 * @return 最大行数
	 */
	public int getVisibleRowCount() {
		return visibleRowCount;
	}
	
	/**
	 * ビューポートの推奨されるサイズを計算します。
	 * 
	 * @return ビューポートのサイズ
	 */
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		final Insets insets = getInsets();
		final int dy = insets.top + insets.bottom;
		final int visibleRowCount = getVisibleRowCount();
		
		if(getModel().getSize() > 0) {
			Dimension dim = new Dimension(getPreferredSize());
			Rectangle rect = getCellBounds(0);
			dim.height = (visibleRowCount * rect.height) + dy;
			return dim;
		} else return getPreferredSize();
	}
	
	/**
	 * 次または前のブロックを表示するためにスクロールする距離を返します。
	 * 
	 * @param rect ビューポート内の可視範囲
	 * @param orient {@link javax.swing.SwingConstants#VERTICAL}
	 * @param direct 上に移動する場合は負、下に移動する場合は正
	 * @return ブロック増分値
	 */
	@Override
	public int getScrollableBlockIncrement(Rectangle rect, int orient, int direct) {
		assert (orient != VERTICAL) : "illegal scroll orientation";
		if(orient == VERTICAL) {
			int inc = rect.height;
			if(direct > 0) { // Scroll Down
				Point p = new Point(rect.x, rect.y + rect.height - 1);
				int last = locationToIndex(p);
				if(last != -1) {
					Rectangle lastRect = getCellBounds(last);
					inc = lastRect.height;
				}
			} else { // Scroll Up
				Point p = new Point(rect.x, rect.y - rect.height);
				int newFirst = locationToIndex(p);
				int oldFirst = locationToIndex(getVisibleRect().getLocation());
				if(newFirst != -1) {
					if(oldFirst == -1) {
						oldFirst = locationToIndex(rect.getLocation());
					}
					Rectangle newFirstRect = getCellBounds(newFirst);
					Rectangle oldFirstRect = getCellBounds(oldFirst);
					while((newFirstRect.y + rect.height
						 < oldFirstRect.y + oldFirstRect.height)
						&& (newFirstRect.y < oldFirstRect.y)) {
						newFirstRect = getCellBounds(++newFirst);
					}
					inc = rect.y - newFirstRect.y;
					if((inc <= 0) && (newFirstRect.y > 0)) {
						newFirstRect = getCellBounds(--newFirst);
						inc = rect.y - newFirstRect.y;
					}
				}
			}
			return inc;
		} else return rect.width;
	}
	
	/**
	 * 次または前の行を表示するためにスクロールする距離を返します。
	 * 
	 * @param rect ビューポート内の可視範囲
	 * @param orient {@link javax.swing.SwingConstants#VERTICAL}
	 * @param direct 上に移動する場合は負、下に移動する場合は正
	 * @return ユニット増分値
	 */
	@Override
	public int getScrollableUnitIncrement(Rectangle rect, int orient, int direct) {
		assert(orient != VERTICAL) : "illegal scroll orientation";
		if(orient == VERTICAL) {
			int row = locationToIndex(rect.getLocation());
			if(row == -1) return 0;
			if(direct > 0) { // Scroll Down
				Rectangle bounds = getCellBounds(row);
				return bounds.height - (rect.y - bounds.y);
			} else { // Scroll Up
				Rectangle bounds = getCellBounds(row - 1);
				if((bounds.y == rect.y) && (row == 0)) return 0;
				if( bounds.y == rect.y) {
					Rectangle prevRect = getCellBounds(row-1);
					return (prevRect.y >= bounds.y)? 0 : prevRect.height;
				} else return rect.y - bounds.y;
			}
		} else return (getFont() != null)? getFont().getSize() : 1;
	}
	
	/**
	 * リストコンポーネントの幅をビューポートの幅に強制一致させるか返します。
	 * 
	 * @return 強制一致させる場合trueを返す
	 */
	@Override
	public boolean getScrollableTracksViewportWidth() {
		if(getParent() instanceof JViewport) {
			JViewport vp = (JViewport)getParent();
			return (vp.getWidth() > getPreferredSize().width);
		}
		return false;
	}
	
	/**
	 * リストコンポーネントの高さをビューポートの高さに強制一致させるか返します。
	 * 
	 * @return 強制一致させる場合trueを返す
	 */
	@Override
	public boolean getScrollableTracksViewportHeight() {
		if(getParent() instanceof JViewport) {
			JViewport vp = (JViewport)getParent();
			return (vp.getHeight() > getPreferredSize().height);
		}
		return false;
	}

}