/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.list;

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
 *チェックボックスによる選択操作をサポートするリストの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年9月12日
 */
public class LeafCheckList extends JComponent implements Scrollable{
	private final Box box;
	private ListModel data;
	private ListSelectionHandler handler;
	private ListSelectionModel selection;
	private LinkedList<CheckBoxItem> items;
	private final CheckItemListener itemListener;
	private int visibleRowCount = 8;
	
	/**
	 *空のリストモデルでリストを生成します。
	 */
	public LeafCheckList(){
		this(new DefaultListModel());
	}
	/**
	 *リストモデルを指定してリストを生成します。
	 *@param model データモデル
	 */
	public LeafCheckList(ListModel model){
		super();
		setLayout(new BorderLayout());
		box = Box.createVerticalBox();
		add(box, BorderLayout.CENTER);
		box.setBackground(Color.WHITE);
		box.setOpaque(true);
		this.itemListener = new CheckItemListener();
		selection = new DefaultListSelectionModel();
		setModel(model);
	}
	/**
	 *リストモデルを設定します。
	 *@param model データモデル
	 */
	public void setModel(ListModel model){
		ListModel old = this.data;
		this.items = new LinkedList<CheckBoxItem>();
		model.addListDataListener(new ModelObserver());
		
		box.removeAll();
		final int size = (this.data = model).getSize();
		for(int i=0; i<size; i++){
			Object value = model.getElementAt(i);
			CheckBoxItem cb = new CheckBoxItem(value);
			items.add((CheckBoxItem)box.add(cb));
		}
		firePropertyChange("model", old, model);
	}
	/**
	 *リストモデルを返します。
	 *@return データモデル
	 */
	public ListModel getModel(){
		return data;
	}
	/**
	 *リスト項目として使用されるチェックボックスです。
	 */
	private class CheckBoxItem extends JCheckBox{
		public CheckBoxItem(Object item){
			super(String.valueOf(item));
			setOpaque(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setFocusable(false);
			setRequestFocusEnabled(false);
			addItemListener(LeafCheckList.this.itemListener);
		}
		public void setValue(Object value){
			setText(String.valueOf(value));
		}
	}
	/**
	 *チェックボックスの選択状態変更を監視します。
	 */
	private class CheckItemListener implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			JCheckBox cb = (JCheckBox)e.getItemSelectable();
			int index = items.indexOf(cb);
			if(cb.isSelected()){
				selection.addSelectionInterval(index, index);
			}else{
				selection.removeSelectionInterval(index, index);
			}
		}
	}
	/**
	 *リストモデルの変更を監視します。
	 */
	private class ModelObserver implements ListDataListener{
		public void contentsChanged(ListDataEvent e){
			final int index0 = e.getIndex0();
			final int index1 = e.getIndex1();
			for(int i=index0; i<=index1; i++){
				Object val = data.getElementAt(i);
				items.get(i).setValue(val);
			}
		}
		public void intervalAdded(ListDataEvent e){
			final int index0 = e.getIndex0();
			final int index1 = e.getIndex1();
			for(int i=index0; i<=index1; i++){
				Object val = data.getElementAt(i);
				JCheckBox cb = new CheckBoxItem(val);
				items.add(i, (CheckBoxItem)box.add(cb, i));
			}
		}
		public void intervalRemoved(ListDataEvent e){
			final int index0 = e.getIndex0();
			final int index1 = e.getIndex1();
			for(int i=index0; i<=index1; i++){
				box.remove(items.remove(index0));
			}
		}
	}
	/**
	 *リスト選択モデルの選択状態の変更を受け取ります。
	 */
	private class ListSelectionHandler implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e){
			final int first = e.getFirstIndex();
			final int last = e.getLastIndex();
			final boolean adj= e.getValueIsAdjusting();
			for(int i=first; i<=last; i++){
				items.get(i).setSelected(selection.isSelectedIndex(i));
			}
			fireSelectionValueChanged(first, last, adj);
		}
	}
	/**
	 *リストの選択状態に変更が加えられたことを通知します。
	 *@param first 変更開始位置
	 *@param last  変更終了位置
	 *@param adj このイベントが一連の変更操作の一部である場合
	 */
	protected void fireSelectionValueChanged(int first, int last, boolean adj){
		ListSelectionListener[] listeners
		= listenerList.getListeners(ListSelectionListener.class);
		ListSelectionEvent e = null;
		for(int i=listeners.length-1; i>=0; i--){
			if(e == null) e = new ListSelectionEvent(this, first, last, adj);
			listeners[i].valueChanged(e);
		}
	}
	/**
	 *リストの選択モデルを設定します。
	 *@param model 選択モデル
	 */
	public void setSelectionModel(ListSelectionModel model){
		ListSelectionModel old = this.selection;
		if(this.handler != null){
			old.removeListSelectionListener(this.handler);
			model.addListSelectionListener(this.handler);
		}
		this.selection = model;
		firePropertyChange("selectionModel", old, model);
	}
	/**
	 *リストの選択モデルを返します。
	 *@return 選択モデル
	 */
	public ListSelectionModel getSelectionModel(){
		return selection;
	}
	/**
	 *選択状態が変更されるたびに通知されるリストにリスナーを追加します。
	 *@param listener 追加するリスナー
	 */
	public void addListSelectionListener(ListSelectionListener listener){
		if(this.handler == null){
			this.handler = new ListSelectionHandler();
			this.selection.addListSelectionListener(this.handler);
		}
		listenerList.add(ListSelectionListener.class, listener);
	}
	/**
	 *リストから選択リスナーを削除します。
	 *@param listener 削除するリスナー
	 */
	public void removeListSelectionListener(ListSelectionListener listener){
		listenerList.remove(ListSelectionListener.class, listener);
	}
	/**
	 *リスト内の指定された座標に最も近いセルのインデックスを返します。
	 *@param location 座標
	 */
	public int locationToIndex(Point location){
		final int size = data.getSize();
		int now = getInsets().top;
		int max = location.y;
		for(int i=0; i<size; i++){
			if(now >= max) return i;
			now += items.get(i).getHeight();
		}
		return size-1;
	}
	/**
	 *リスト内の指定されたインデックスのセルの座標範囲を返します。
	 *@return セルの矩形範囲
	 */
	public Rectangle getCellBounds(int index){
		return items.get(index).getBounds();
	}
	/**
	 *リスト内の指定されたインデックスのセルの原点座標を返します。
	 *@param index インデックス
	 */
	public Point indexToLocation(int index){
		return items.get(index).getLocation();
	}
	/**
	 *リスト内の指定されたインデックスが可視範囲に入るようにスクロールします。
	 *@param index 見えるようにするインデックス
	 */
	public void ensureIndexIsVisible(int index){
		super.scrollRectToVisible(getCellBounds(index));
	}
	/**
	 *スクロールしないで可視範囲に表示可能な最大行数を指定します。
	 *@param visibleRowCount 最大行数
	 */
	public void setVisibleRowCount(int visibleRowCount){
		final int old = this.visibleRowCount;
		this.visibleRowCount = visibleRowCount;
		firePropertyChange("visibleRowCount", old, visibleRowCount);
	}
	/**
	 *スクロールしないで可視範囲に表示可能な最大行数を返します。
	 *@return 最大行数
	 */
	public int getVisibleRowCount(){
		return visibleRowCount;
	}
	/**
	 *ビューポートの推奨されるサイズを計算します。
	 *@return ビューポートのサイズ
	 */
	@Override
	public Dimension getPreferredScrollableViewportSize(){
		final Insets insets = getInsets();
		final int dy = insets.top + insets.bottom;
		final int visibleRowCount = getVisibleRowCount();
		
		if(getModel().getSize() > 0){
			Dimension dim = new Dimension(getPreferredSize());
			Rectangle rect = getCellBounds(0);
			dim.height = (visibleRowCount * rect.height) + dy;
			return dim;
		}else return getPreferredSize();
	}
	/**
	 *次または前のブロックを表示するためにスクロールする距離を返します。
	 *@param rect ビューポート内の可視範囲
	 *@param orient {@link javax.swing.SwingConstants#VERTICAL}
	 *@param direct 上に移動する場合は負、下に移動する場合は正
	 *@return ブロック増分値
	 */
	@Override
	public int getScrollableBlockIncrement(Rectangle rect, int orient, int direct){
		assert (orient != VERTICAL) : "illegal scroll orientation";
		if(orient == VERTICAL){
			int inc = rect.height;
			if(direct > 0){ // Scroll Down
				Point p = new Point(rect.x, rect.y + rect.height - 1);
				int last = locationToIndex(p);
				Rectangle lastRect = getCellBounds(last);
				inc = lastRect.height;
			}else{ // Scroll Up
				Point p = new Point(rect.x, rect.y - rect.height);
				int newFirst = locationToIndex(p);
				int oldFirst = locationToIndex(getVisibleRect().getLocation());
				if(oldFirst == -1) oldFirst = locationToIndex(rect.getLocation());
				Rectangle newFirstRect = getCellBounds(newFirst);
				Rectangle oldFirstRect = getCellBounds(oldFirst);
				while((newFirstRect.y + rect.height
					 < oldFirstRect.y + oldFirstRect.height)
					&& (newFirstRect.y < oldFirstRect.y)){
					newFirstRect = getCellBounds(++newFirst);
				}
				inc = rect.y - newFirstRect.y;
				if((inc <= 0) && (newFirstRect.y > 0)){
					newFirstRect = getCellBounds(--newFirst);
					inc = rect.y - newFirstRect.y;
				}
			}
			return inc;
		}else return rect.width;
	}
	/**
	 *次または前の行を表示するためにスクロールする距離を返します。
	 *@param rect ビューポート内の可視範囲
	 *@param orient {@link javax.swing.SwingConstants#VERTICAL}
	 *@param direct 上に移動する場合は負、下に移動する場合は正
	 *@return ユニット増分値
	 */
	@Override
	public int getScrollableUnitIncrement(Rectangle rect, int orient, int direct){
		assert(orient != VERTICAL) : "illegal scroll orientation";
		if(orient == VERTICAL){
			int row = locationToIndex(rect.getLocation());
			if(direct > 0){ // Scroll Down
				Rectangle bounds = getCellBounds(row);
				return bounds.height - (rect.y - bounds.y);
			}else{ // Scroll Up
				Rectangle bounds = getCellBounds(row);
				if((bounds.y == rect.y) && (row == 0)) return 0;
				if( bounds.y == rect.y){
					Rectangle prevRect = getCellBounds(row-1);
					return (prevRect.y >= bounds.y)? 0 : prevRect.height;
				}else return rect.y - bounds.y;
			}
		}else return (getFont() != null)? getFont().getSize() : 1;
	}
	/**
	 *リストコンポーネントの幅をビューポートの幅に強制一致させるか返します。
	 *@return 強制一致させる場合trueを返す
	 */
	@Override
	public boolean getScrollableTracksViewportWidth(){
		if(getParent() instanceof JViewport){
			JViewport vp = (JViewport)getParent();
			return (vp.getWidth() > getPreferredSize().width);
		}
		return false;
	}
	/**
	 *リストコンポーネントの高さをビューポートの高さに強制一致させるか返します。
	 *@return 強制一致させる場合trueを返す
	 */
	@Override
	public boolean getScrollableTracksViewportHeight(){
		if(getParent() instanceof JViewport){
			JViewport vp = (JViewport)getParent();
			return (vp.getHeight() > getPreferredSize().height);
		}
		return false;
	}
}