/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.tab;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import javax.swing.event.*;

import leaf.icon.CloseIcon;

/**
 * 閉じるボタンとドラッグアンドドロップ機能を持ったタブ領域です。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年3月12日
 *
 */
public class LeafTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	
	/*DnD用フィールド*/
	private static final int LINE_WIDTH = 5;
	private final TabTitleGhost glasspane;
	private final Rectangle cursorRect  = new Rectangle();
	private int index = -1;
	private final DragSourceListener dsl;
	private final Transferable trans;
	private final DragGestureListener dgl;
	
	/**
	 * 空のタブ領域を生成します。
	 */
	public LeafTabbedPane() {
		this(TOP, SCROLL_TAB_LAYOUT);
	}
	
	/**
	 * タブの表示位置を指定して空のタブ領域を生成します。
	 * 
	 * @param tabPlacement タブの表示位置
	 */
	public LeafTabbedPane(int tabPlacement) {
		this(tabPlacement,SCROLL_TAB_LAYOUT);
	}
	
	/**
	 * タブの表示位置とレイアウトポリシーを指定して空のタブ領域を生成します。
	 * 
	 * @param tabPlacement タブの表示位置
	 * @param tabLayoutPolicy レイアウトポリシー
	 */
	public LeafTabbedPane(int tabPlacement, int tabLayoutPolicy) {
		super(tabPlacement,tabLayoutPolicy);
		
		addChangeListener(new TabChangeHandler());
		addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				getSelectedComponent().requestFocusInWindow();
			}
		});
		
		glasspane = new TabTitleGhost();
		
		dsl   = new TabDragSourceHandler();
		trans = new TabTransferable();
		dgl   = new TabDragGestureListener();
		
		DropTargetListener dtl = new TabDropTargetHandler();
		new DropTarget(glasspane, DnDConstants.ACTION_COPY_OR_MOVE, dtl, true);
	}
	
	/**
	 * コンポーネントの名前をタイトルに指定してタブを追加します。
	 * 
	 * @param comp 追加するコンポーネント
	 * @return 追加したコンポーネント
	 */
	@Override
	public Component add(Component comp) {
		addTab(comp.getName(), null, comp, null, getTabCount());
		return comp;
	}
	/**
	 * コンポーネントの名前をタイトルに指定して、指定された位置にタブを追加します。
	 * 
	 * @param comp 追加するコンポーネント
	 * @param index 追加する位置
	 * @return 追加したコンポーネント
	 */
	@Override
	public Component add(Component comp,int index) {
		addTab(comp.getName(), null, comp, null, index);
		return comp;
	}
	
	/**
	 * タブに表示するオブジェクトを指定してタブを追加します。
	 * 
	 * @param comp 追加するコンポーネント
	 * @param constraints タブで表示されるオブジェクト
	 */
	@Override
	public void add(Component comp, Object constraints) {
		if(constraints instanceof String) {
			addTab((String)constraints, null, comp, null, getTabCount());
		} else if(constraints instanceof Icon) {
			addTab(null, (Icon)constraints, comp, null, getTabCount());
		} else {
			addTab(comp.getName(), null, comp, null, getTabCount());
		}
	}
	
	/**
	 * タブに表示するオブジェクトを指定して、指定した位置にタブを追加します。
	 * 
	 * @param comp 追加するコンポーネント
	 * @param constraints タブで表示されるオブジェクト
	 */
	@Override
	public void add(Component comp, Object constraints, int index) {
		if(constraints instanceof String) {
			addTab((String)constraints, null, comp, null, index);
		} else if(constraints instanceof Icon) {
			addTab(null, (Icon)constraints, comp, null, index);
		} else {
			addTab(comp.getName(), null, comp, null, index);
		}
	}
	
	/**
	 * タイトルを指定してタブを追加します。
	 * 
	 * @param title タイトル
	 * @param comp 追加するコンポーネント
	 * @return 追加したコンポーネント
	 */
	@Override
	public Component add(String title, Component comp) {
		addTab(title, null, comp, null, getTabCount());
		return comp;
	}
	
	/**
	 * タイトルを指定してタブを追加します。
	 * 
	 * @param title タイトル
	 * @param comp 追加するコンポーネント
	 */
	@Override
	public void addTab(String title, Component comp) {
		addTab(title, null, comp, null, getTabCount());
	}
	
	/**
	 * タイトルとアイコンを指定してタブを追加します。
	 * 
	 * @param title タイトル
	 * @param icon アイコン
	 * @param comp 追加するコンポーネント
	 */
	@Override
	public void addTab(String title, Icon icon, Component comp) {
		addTab(title, icon, comp, null, getTabCount());
	}
	
	/**
	 * タイトルとアイコン、ツールチップを指定してタブを追加します。
	 * 
	 * @param title タイトル
	 * @param icon アイコン
	 * @param comp 追加するコンポーネント
	 * @param tip ツールチップ
	 */
	@Override
	public void addTab(String title, Icon icon, Component comp, String tip) {
		addTab(title, icon, comp, null, getTabCount());
		setToolTipTextAt(getTabCount()-1, tip);
	}
	
	/**
	 * タイトルとアイコンを指定して、指定した位置にタブを追加します。
	 * 
	 * @param title タイトル
	 * @param icon アイコン
	 * @param comp 追加するコンポーネント
	 * @param index 追加する位置
	 */
	public void addTab(String title, Icon icon, Component comp, int index) {
		addTab(title, icon, comp, null, index);
	}
	
	/**
	 * タイトルとアイコンを指定して、指定した位置にタブを追加します。
	 * 
	 * @param title タイトル
	 * @param icon アイコン
	 * @param comp 追加するコンポーネント
	 * @param tip ツールチップ
	 * @param index 追加する位置
	 */
	public void addTab(String title, Icon icon, Component comp, String tip, int index) {
		TabTitleLabel tab = new TabTitleLabel(title,icon,tip);
		super.add(comp, title, index);
		if(index >= 0 && index < getTabCount()) {
			super.setIconAt(index, icon);
			super.setTabComponentAt(index,tab);
			super.setToolTipTextAt(index, tip);
			setSelectedIndex(index);
			comp.requestFocusInWindow();
			if(getTabCount() == 1) tab.setCloseButtonVisible(true);
		}
	}
	
	/**
	 * {@link TabCloseListener}を追加します。
	 * 
	 * @param listener 登録するTabListener
	 */
	public void addTabListener(TabCloseListener listener) {
		listenerList.add(TabCloseListener.class, listener);
	}
	
	/**
	 * {@link TabCloseListener}を削除します。
	 * 
	 * @param listener 削除するTabListener
	 */
	public void removeTabListener(TabCloseListener listener) {
		listenerList.remove(TabCloseListener.class, listener);
	}
	
	/**
	 * {@link TabCloseListener}のリストを返します。
	 * 
	 * @return リスナーのリスト
	 */
	private TabCloseListener[] getTabListenerList() {
		return listenerList.getListeners(TabCloseListener.class);
	}
	
	/**
	 * 指定した位置のタブのタイトルを変更します。
	 * 
	 * @param index 変更するタブの位置
	 * @param title 新しいタイトル
	 */
	public void setTitleAt(int index, String title) {
		super.setTitleAt(index,title);
		((TabTitleLabel)getTabComponentAt(index)).setTitle(title);
	}
	
	/**
	 * 指定した位置のタイトルとツールチップを変更します。
	 * 
	 * @param index 変更するタブの位置
	 * @param title 新しいタイトル
	 * @param tip 新しいツールチップ
	 */
	public void setTitleAt(int index, String title, String tip) {
		setTitleAt(index,title);
		setToolTipTextAt(index,tip);
	}
	
	/**
	 * 指定した位置のタブのアイコンを変更します。
	 * 
	 * @param index 変更するタブの位置
	 * @param icon 新しいアイコン
	 */
	public void setIconAt(int index, Icon icon) {
		super.setIconAt(index,icon);
		((TabTitleLabel)getTabComponentAt(index)).setIcon(icon);
	}
	
	/**
	 * 指定した位置のツールチップを変更します。
	 * 
	 * @param index 変更するタブの位置
	 * @param tip 新しいツールチップ
	 */
	public void setToolTipTextAt(int index, String tip) {
		super.setToolTipTextAt(index, tip);
		((TabTitleLabel)getTabComponentAt(index)).setToolTipText(tip);
	}
	
	private final class TabChangeHandler implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			int selected = getSelectedIndex();
			for(int i = 0; i < getTabCount(); i++) {
				TabTitleLabel tab = (TabTitleLabel) getTabComponentAt(i);
				if(tab != null) tab.setCloseButtonVisible(i == selected);
			}
		}
	}
	
	private final class TabTitleLabel extends JPanel {
		private static final long serialVersionUID = 1L;
		private final Icon icon_close;
		private final JLabel label_title;
		private final JButton button_close;
		
		public TabTitleLabel(String title, Icon icon, String tooltip) {
			super(new BorderLayout());
			setOpaque(false);
			
			label_title = new JLabel(title, icon, JLabel.LEFT);
			label_title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
			label_title.setToolTipText(tooltip);
			label_title.addMouseListener(new ClickHandler());
			
			icon_close = new CloseIcon();
			Dimension size = new Dimension();
			size.width  = icon_close.getIconWidth();
			size.height = icon_close.getIconHeight();
			
			button_close = new JButton(new CloseAction());
			button_close.setPreferredSize(size);
			
			button_close.setBorderPainted(false);
			button_close.setContentAreaFilled(false);
			button_close.setFocusable(false);
			button_close.setFocusPainted(false);
			button_close.setToolTipText("close");
			
			add(label_title,  BorderLayout.CENTER);
			add(button_close, BorderLayout.EAST);
			
			setBorder(BorderFactory.createEmptyBorder(2, 1, 1, 1));
			setCloseButtonVisible(false);
			
			DragSource ds = DragSource.getDefaultDragSource();
			ds.createDefaultDragGestureRecognizer(
				label_title, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
		}
		
		private class ClickHandler extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {
				setSelectedIndex(indexOfTabComponent(TabTitleLabel.this));
			}
		}
		
		private class CloseAction extends AbstractAction {
			private static final long serialVersionUID = 1L;

			public CloseAction() {
				super(null, icon_close);
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TabTitleLabel.this.close();
			}
		}
		
		public void setTitle(String title) {
			label_title.setText(title);
			fireStateChanged();
		}
		
		public void setIcon(Icon icon) {
			label_title.setIcon(icon);
			fireStateChanged();
		}
		
		public void setToolTipText(String tip) {
			label_title.setToolTipText(tip);
		}
		
		public void setCloseButtonVisible(boolean visible) {
			button_close.setVisible(visible);
		}
		
		public void close() {
			int index = indexOfTabComponent(TabTitleLabel.this);
			Component cmp = LeafTabbedPane.this.getComponentAt(index);
			TabCloseEvent e = new TabCloseEvent(this, cmp);
			for(TabCloseListener l : getTabListenerList()) {
				if(!l.tabClosing(e)) return;
			}
			index = indexOfTabComponent(TabTitleLabel.this);
			if(index >= 0) LeafTabbedPane.this.remove(index);
		}
	}
	
	private final class TabDropTargetHandler extends DropTargetAdapter {
		private Point old = new Point();
		
		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if(!isDragAcceptable(e)) e.rejectDrag();
			else e.acceptDrag(e.getDropAction());
		}
		
		@Override
		public void dragOver(final DropTargetDragEvent e) {
			Point point = e.getLocation();
			switch(getTabPlacement()) {
			case TOP : case BOTTOM :
				initVerticalLine(getTargetIndex(point));
				break;
			case LEFT : case RIGHT :
				initHorizontalLine(getTargetIndex(point));
				break;
			}
			glasspane.setPoint(point);
			if(!old.equals(point)) glasspane.repaint();
			old = point;
		}
		
		@Override
		public void drop(DropTargetDropEvent e) {
			if(isDropAcceptable(e)) {
				moveTab(index, getTargetIndex(e.getLocation()));
				e.dropComplete(true);
			} else e.dropComplete(false);
			repaint();
		}
		
		private boolean isDragAcceptable(DropTargetDragEvent e) {
			Transferable t = e.getTransferable();
			DataFlavor[] f = e.getCurrentDataFlavors();
			return t.isDataFlavorSupported(f[0]) && index >= 0;
		}
		
		private boolean isDropAcceptable(DropTargetDropEvent e) {
			Transferable t = e.getTransferable();
			DataFlavor[] f = t.getTransferDataFlavors();
			return t.isDataFlavorSupported(f[0]) && index >= 0;
		}
		
		private void moveTab(int prev, int next) {
			if(next < 0 || prev == next) return;
			Component cmp = getComponentAt(prev);
			Component tab = getTabComponentAt(prev);
			String title  = getTitleAt(prev);
			Icon icon     = getIconAt(prev);
			String tip    = getToolTipTextAt(prev);
			boolean flg   = isEnabledAt(prev);
			int target = prev > next ? next : next -1;
			remove(prev);
			addTab(title, icon, cmp, tip, target);
			setEnabledAt(target, flg);
			setTabComponentAt(target, tab);
			if(flg) setSelectedIndex(target);
		}
		
		private void initVerticalLine(int i) {
			if(i < 0 || index == i || i - index == 1) {
				cursorRect.setRect(0, 0, 0, 0);
			} else if(i == 0) {
				Rectangle r = SwingUtilities.convertRectangle(
					LeafTabbedPane.this, getBoundsAt(0), glasspane);
				cursorRect.setLocation(r.x - LINE_WIDTH /2, r.y);
				cursorRect.setSize(LINE_WIDTH, r.height);
			} else{
				Rectangle r = SwingUtilities.convertRectangle(
					LeafTabbedPane.this, getBoundsAt(i-1), glasspane);
				cursorRect.setLocation(r.x + r.width - LINE_WIDTH/2, r.y);
				cursorRect.setSize(LINE_WIDTH, r.height);
			}
		}
		
		private void initHorizontalLine(int i) {
			if(i < 0 || index == i || i - index == 1) {
				cursorRect.setRect(0, 0, 0, 0);
			} else if(i == 0) {
				Rectangle r = SwingUtilities.convertRectangle(
					LeafTabbedPane.this, getBoundsAt(0), glasspane);
				cursorRect.setLocation(r.x, r.y - LINE_WIDTH /2);
				cursorRect.setSize(r.width, LINE_WIDTH);
			} else {
				Rectangle r = SwingUtilities.convertRectangle(
					LeafTabbedPane.this, getBoundsAt(i-1), glasspane);
				cursorRect.setLocation(r.x, r.y + r.width - LINE_WIDTH/2);
				cursorRect.setSize(r.width, LINE_WIDTH);
			}
		}
	}
	
	private final class TabDragSourceHandler extends DragSourceAdapter {
		@Override
		public void dragEnter(DragSourceDragEvent e) {
			e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
		}
		
		@Override
		public void dragExit(DragSourceEvent e) {
			e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
			cursorRect.setRect(0, 0, 0, 0);
			glasspane.setPoint(new Point(-1000, -1000));
			glasspane.repaint();
		}
		
		@Override
		public void dragOver(DragSourceDragEvent e) {
			Point point = e.getLocation();
			SwingUtilities.convertPointFromScreen(point, glasspane);
			DragSourceContext c = e.getDragSourceContext();
			int idx = getTargetIndex(point);
			if(getTabAreaBounds().contains(point)
			&& idx >= 0 && idx != index && idx != index + 1) {
				c.setCursor(DragSource.DefaultMoveDrop);
				glasspane.setCursor(DragSource.DefaultMoveDrop);
			} else{
				c.setCursor(DragSource.DefaultMoveNoDrop);
				glasspane.setCursor(DragSource.DefaultMoveNoDrop);
			}
		}
		
		@Override
		public void dragDropEnd(DragSourceDropEvent e) {
			cursorRect.setRect(0,0,0,0);
			index = -1;
			glasspane.setVisible(false);
			glasspane.setImage(null);
		}
		
		private Rectangle getTabAreaBounds() {
			Rectangle ret = getBounds();
			Component cmp = getSelectedComponent();
			int index = 0;
			while(cmp == null && index < getTabCount()) {
				cmp = getComponentAt(index++);
			}
			Rectangle cmprect = cmp == null?
				new Rectangle() : cmp.getBounds();
			switch(getTabPlacement()) {
			case TOP:
				ret.height -= cmprect.height;
				break;
			case BOTTOM:
				ret.y += cmprect.y + cmprect.height;
				ret.height -= cmprect.height;
				break;
			case LEFT:
				ret.width  -= cmprect.width;
				break;
			case RIGHT:
				ret.x += cmprect.x + cmprect.width;
				ret.width -= cmprect.width;
			}
			ret.grow(2, 2);
			return ret;
		}
	}
	
	private final class TabTransferable implements Transferable {
		final String NAME = LeafTabbedPane.class.getName();
		private final DataFlavor flavor;
		
		public TabTransferable() {
			flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
		}
		
		@Override
		public Object getTransferData(DataFlavor flavor) {
			return LeafTabbedPane.this.getTabComponentAt(index);
		}
		
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			DataFlavor[] f = {flavor};
			return f;
		}
		
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.getHumanPresentableName().equals(NAME);
		}
	}
	
	private final class TabDragGestureListener implements DragGestureListener {
		@Override
		public void dragGestureRecognized(DragGestureEvent e) {
			if(getTabCount() <= 1) return;
			Component comp = e.getComponent().getParent();
			index = indexOfTabComponent((TabTitleLabel) comp);
			if(!isEnabledAt(index)) return;
			initGlassPane(getTabComponentAt(index), e.getDragOrigin());
			try {
				e.startDrag(DragSource.DefaultMoveDrop, trans, dsl);
			} catch(InvalidDnDOperationException ex) {
				ex.printStackTrace();
			}
		}
		
		private void initGlassPane(Component cmp, Point point) {
			getRootPane().setGlassPane(glasspane);
			BufferedImage image = new BufferedImage(
				cmp.getWidth(), cmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.getGraphics();
			cmp.paint(g);
			glasspane.setImage(image);
			Point glasspt = SwingUtilities.convertPoint(cmp, point, glasspane);
			glasspane.setPoint(glasspt);
			glasspane.setVisible(true);
		}
	}
	
	private final class TabTitleGhost extends JPanel {
		private static final long serialVersionUID = 1L;
		private final AlphaComposite comp;
		private Point location;
		private BufferedImage image;
		
		public TabTitleGhost() {
			setOpaque(false);
			location = new Point();
			comp = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.7f);
		}
		
		public void setImage(BufferedImage image) {
			this.image = image;
		}
		
		public void setPoint(Point point) {
			this.location = point;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setComposite(comp);
			if(image != null) {
				double lx = location.getX();
				double ly = location.getY();
				int iw = image.getWidth(this);
				int ih = image.getHeight(this);
				int x = (int) (lx - iw / 2d);
				int y = (int) (ly - ih / 2d);
				g2.drawImage(image, x, y, null);
			}
			if(index >= 0) {
				g2.setPaint(Color.BLUE);
				g2.fill(cursorRect);
			}
		}
	}
	
	private final int getTargetIndex(Point point) {
		Point ptab = SwingUtilities.convertPoint(
			glasspane, point, LeafTabbedPane.this);
		final int tabPlace = getTabPlacement();
		
		for(int i = 0; i < getTabCount(); i++) {
			Rectangle r = getBoundsAt(i);
			if(tabPlace == TOP || tabPlace == BOTTOM) {
				r.setRect(r.x-r.width/2, r.y,  r.width, r.height);
			} else{
				r.setRect(r.x, r.y-r.height/2, r.width, r.height);
			}
			if(r.contains(ptab))return i;
		}
		
		Rectangle r = getBoundsAt(getTabCount() -1);
		if(tabPlace == TOP || tabPlace == BOTTOM) {
			r.setRect(r.x + r.width/2, r.y,  r.width, r.height);
		} else{
			r.setRect(r.x, r.y + r.height/2, r.width, r.height);
		}
		
		return (r.contains(ptab))? getTabCount() : -1;
	}
	
	/**
	 * タブのタイトルが昇順に並ぶようにタブをソートします。
	 */
	public void sortInAscending() {
		int index = getSelectedIndex();
		ArrayList<TabSortInfo> tabs = new ArrayList<TabSortInfo>();
		for(int i = 0; i < getTabCount(); i++) {
			tabs.add(new TabSortInfo(i));
		}
		Collections.sort(tabs);
		removeAll();
		for(TabSortInfo tab : tabs) {
			addTab(tab.title, tab.icon, tab.comp, tab.tooltip);
		}
		setSelectedIndex(index);
	}
	
	private class TabSortInfo implements Comparable<TabSortInfo> {
		public final String title, tooltip;
		public final Component comp;
		public final Icon icon;
		
		public TabSortInfo(int index) {
			this.title = getTitleAt(index);
			this.tooltip = getToolTipTextAt(index);
			this.icon  = getIconAt(index);
			this.comp = getComponentAt(index);
		}
		
		@Override
		public int compareTo(TabSortInfo to) {
			return title.compareTo(to.title);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof TabSortInfo) {
				return (compareTo((TabSortInfo)obj) == 0);
			} else{
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			return title.hashCode() + comp.hashCode();
		}
	}

}
