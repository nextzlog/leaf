/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.components.tabbedpane;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import javax.swing.event.*;

import leaf.icon.LeafCloseIcon;
import leaf.manager.LeafLangManager;

/**
*閉じるボタンとドラッグアンドドロップ機能を持ったタブ領域です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年3月12日
*@see TabListener
*/
public class LeafTabbedPane extends JTabbedPane{
	
	/*閉じるボタン用*/
	private final Icon iclose = new LeafCloseIcon();
	private final Dimension buttonsize;
	private final ArrayList<TabListener> listeners = new ArrayList<TabListener>(1);
	
	/*DnD用フィールド*/
	private static final int LINE_WIDTH = 5;
	private static final String NAME = "leaf";
	private final GhostGlassPane glasspane;
	private final Rectangle rect  = new Rectangle();
	private int index = -1;
	
	/*DnD用インターフェース*/
	private final DragSourceListener dsl;
	private final Transferable trans;
	private final DragGestureListener dgl;
	
	/**
	*空のタブ領域を生成します。
	*/
	public LeafTabbedPane(){
		this(TOP, SCROLL_TAB_LAYOUT);
	}
	/**
	*タブの表示位置を指定して空のタブ領域を生成します。
	*@param tabPlacement タブの表示位置
	*/
	public LeafTabbedPane(int tabPlacement){
		this(tabPlacement,SCROLL_TAB_LAYOUT);
	}
	/**
	*タブの表示位置とレイアウトポリシーを指定して空のタブ領域を生成します。
	*@param tabPlacement タブの表示位置
	*@param tabLayoutPolicy レイアウトポリシー
	*/
	public LeafTabbedPane(int tabPlacement, int tabLayoutPolicy){
		super(tabPlacement,tabLayoutPolicy);
		
		addChangeListener(new ExChangeListener());
		addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e){
				getSelectedComponent().requestFocusInWindow();
			}
		});
		
		buttonsize = new Dimension(iclose.getIconWidth(),iclose.getIconHeight());
		
		glasspane = new GhostGlassPane();
		
		dsl   = new ExDragSourceAdapter();
		trans = new ExTransferable();
		dgl   = new ExDragGestureListener();
		
		DropTargetListener dtl = new ExDropTargetAdapter();
		new DropTarget(
			glasspane, DnDConstants.ACTION_COPY_OR_MOVE, dtl, true
		);
	}
	/**
	*コンポーネントの名前をタイトルに指定してタブ項目を追加します。
	*@param component 追加するコンポーネント
	*@return 追加したコンポーネント
	*/
	public Component add(Component component){
		addTab(component.getName(), null, component, null, getTabCount());
		return component;
	}
	/**
	*コンポーネントの名前をタイトルに指定して、指定された
	*インデックスにタブ項目を追加します。
	*@param component 追加するコンポーネント
	*@param index 追加する位置
	*@return 追加したコンポーネント
	*/
	public Component add(Component component,int index){
		addTab(component.getName(), null, component, null, index);
		return component;
	}
	/**
	*タブに表示するオブジェクトを指定してタブ項目を追加します。
	*constraintsがStringまたはIcon以外の場合、コンポーネントの
	*名前がタイトルに指定されます。
	*@param component 追加するコンポーネント
	*@param constraints タブで表示されるオブジェクト
	*/
	public void add(Component component, Object constraints){
		if(constraints instanceof String){
			addTab((String)constraints, null, component, null, getTabCount());
		}else if(constraints instanceof Icon){
			addTab(null, (Icon)constraints, component, null, getTabCount());
		}else{
			addTab(component.getName(), null, component, null, getTabCount());
		}
	}
	/**
	*タブに表示するオブジェクトを指定して、指定したインデックスに
	*タブ項目を追加します。constraintsがStringまたはIcon以外の場合、
	*コンポーネントの名前がタイトルに指定されます。
	*@param component 追加するコンポーネント
	*@param constraints タブで表示されるオブジェクト
	*/
	public void add(Component component, Object constraints, int index){
		if(constraints instanceof String){
			addTab((String)constraints, null, component, null, index);
		}else if(constraints instanceof Icon){
			addTab(null, (Icon)constraints, component, null, index);
		}else{
			addTab(component.getName(), null, component, null, index);
		}
	}
	/**
	*タイトルを指定してタブ項目を追加します。
	*@param title タイトル
	*@param component 追加するコンポーネント
	*@return 追加したコンポーネント
	*/
	public Component add(String title, Component component){
		addTab(title, null, component, null, getTabCount());
		return component;
	}
	/**
	*タイトルを指定してタブ項目を追加します。
	*@param title タイトル
	*@param component 追加するコンポーネント
	*/
	public void addTab(String title, Component component){
		addTab(title, null, component, null, getTabCount());
	}
	/**
	*タイトルとアイコンを指定してタブ項目を追加します。
	*@param title タイトル
	*@param icon アイコン
	*@param component 追加するコンポーネント
	*/
	public void addTab(String title, Icon icon, Component component){
		addTab(title, icon, component, null, getTabCount());
	}
	/**
	*タイトルとアイコン、ツールチップを指定してタブ項目を追加します。
	*@param title タイトル
	*@param icon アイコン
	*@param component 追加するコンポーネント
	*@param tip ツールチップ
	*/
	public void addTab(String title, Icon icon, Component component, String tip){
		addTab(title, icon, component, null, getTabCount());
		setToolTipTextAt(getTabCount()-1, tip);
	}
	/**
	*タイトルとアイコンを指定して、指定したインデックスに
	*タブ項目を追加します。
	*@param title タイトル
	*@param icon アイコン
	*@param component 追加するコンポーネント
	*@param index 追加する位置
	*/
	public void addTab(String title, Icon icon, Component component, int index){
		addTab(title, icon, component, null, index);
	}
	/**
	*タイトルとアイコンを指定して、指定したインデックスに
	*タブ項目を追加します。
	*@param title タイトル
	*@param icon アイコン
	*@param component 追加するコンポーネント
	*@param tip ツールチップ
	*@param index 追加する位置
	*/
	public void addTab(String title,Icon icon,Component component,String tip,int index){
		LeafTabTitlePane tab = new LeafTabTitlePane(title,icon,component,tip);
		super.add(component, title, index);
		if(index>=0&&index<getTabCount()){
			super.setIconAt(index, icon);
			super.setTabComponentAt(index,tab);
			super.setToolTipTextAt(index, tip);
			setSelectedIndex(index);
			component.requestFocusInWindow();
			if(getTabCount()==1)tab.setCloseButtonVisible(true);
		}
	}
	/**
	*{@link TabListener}を追加します。
	*@param listener 登録するTabListener
	*/
	public void addTabListener(TabListener listener){
		listeners.add(listener);
	}
	/**
	*{@link TabListener}を削除します。
	*@param listener 削除するTabListener
	*/
	public void removeTabListener(TabListener listener){
		listeners.remove(listener);
	}
	/**
	*指定したインデックスのタブ項目のタイトルを変更します。
	*@param index 変更するタブ項目のインデックス
	*@param title 新しいタイトル
	*/
	public void setTitleAt(int index, String title){
		super.setTitleAt(index,title);
		((LeafTabTitlePane)getTabComponentAt(index)).setTitle(title);
	}
	/**
	*指定したインデックスのタイトルとツールチップを変更します。
	*@param index 変更するタブ項目のインデックス
	*@param title 新しいタイトル
	*@param tip 新しいツールチップ
	*/
	public void setTitleAt(int index, String title, String tip){
		setTitleAt(index,title);
		setToolTipTextAt(index,tip);
	}
	/**
	*指定したインデックスのタブ項目のアイコンを変更します。
	*@param index 変更するタブ項目のインデックス
	*@param icon 新しいアイコン
	*/
	public void setIconAt(int index, Icon icon){
		super.setIconAt(index,icon);
		((LeafTabTitlePane)getTabComponentAt(index)).setIcon(icon);
	}
	/**
	*指定したインデックスのツールチップを変更します。
	*@param index 変更するタブ項目のインデックス
	*@param tip 新しいツールチップ
	*/
	public void setToolTipTextAt(int index, String tip){
		super.setToolTipTextAt(index, tip);
		((LeafTabTitlePane)getTabComponentAt(index)).setToolTipText(tip);
	}
	/**
	*タブ項目のタイトル部分のコンポーネントです。
	*/
	private class LeafTabTitlePane extends JPanel{
		
		private final JLabel label;
		private final JButton bclose;
		
		/**コンストラクタ*/
		public LeafTabTitlePane(String title, Icon icon, Component component, String tip){
			
			super(new BorderLayout());
			setOpaque(false);
			
			label = new JLabel(title,icon,JLabel.LEFT);
			label.setBorder(BorderFactory.createEmptyBorder(0,0,0,4));
			label.setToolTipText(tip);
			
			label.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					setSelectedIndex(indexOfTabComponent(LeafTabTitlePane.this));
				}
			});
			
			DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
				label, DnDConstants.ACTION_COPY_OR_MOVE, dgl
			);
			
			bclose = new JButton(iclose);
			bclose.setPreferredSize(buttonsize);
			
			bclose.setBorderPainted(false);
			bclose.setFocusPainted(false);
			bclose.setContentAreaFilled(false);
			bclose.setFocusable(false);
			
			bclose.setToolTipText(
				LeafLangManager.get("Close","閉じる")
			);
			
			bclose.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					LeafTabTitlePane.this.close();
				}
			});
			add(label, BorderLayout.CENTER);
			add(bclose, BorderLayout.EAST);
			
			setBorder(BorderFactory.createEmptyBorder(2,1,1,1));
			setCloseButtonVisible(false);
		}
		/**タイトル設定*/
		public void setTitle(String title){
			label.setText(title);
			informChangeEvent();
		}
		/**アイコン設定*/
		public void setIcon(Icon icon){
			label.setIcon(icon);
			informChangeEvent();
		}
		/**ツールチップ設定*/
		public void setToolTipText(String tip){
			label.setToolTipText(tip);
		}
		/**閉じるボタンを表示するか設定*/
		public void setCloseButtonVisible(boolean visible){
			bclose.setVisible(visible);
		}
		/**タブを閉じます*/
		public void close(){
			int index = indexOfTabComponent(LeafTabTitlePane.this);
			Component cmp = LeafTabbedPane.this.getComponentAt(index);
			for(TabListener lis: listeners){
				if(!lis.tabClosing(cmp))return;
			}
			if((index = indexOfTabComponent(LeafTabTitlePane.this))>=0){
				LeafTabbedPane.this.remove(index);
			}
		}
	}
	/**
	*選択されたタブにのみ閉じるボタンを表示するために実装されます。
	*/
	private class ExChangeListener implements ChangeListener{
		public void stateChanged(ChangeEvent e){
			int sel = getSelectedIndex();
			for(int i=0;i<getTabCount();i++){
				LeafTabTitlePane tab = (LeafTabTitlePane)getTabComponentAt(i);
				if(tab!=null)tab.setCloseButtonVisible(i==sel);
			}
		}
	}
	/**
	*タブの状態変更を通知します。
	*/
	private void informChangeEvent(){
		ChangeEvent e = new ChangeEvent(this);
		ChangeListener[] listeners = getChangeListeners();
		for(int i=0;i<listeners.length;i++){
			listeners[i].stateChanged(e);
		}
	}
	/**
	*タブをソートします。
	*/
	public void sort(){
		int index = getSelectedIndex();
		ArrayList<TabSet> tabs = new ArrayList<TabSet>(getTabCount());
		for(int i=0;i < getTabCount(); i++){
			tabs.add(new TabSet(i));
		}
		Collections.sort(tabs);
		removeAll();
		for(TabSet tab : tabs){
			addTab(tab.title, tab.icon, tab.component, tab.tooltip);
		}
		setSelectedIndex(index);
	}
	/**
	*ソート用の一時的なタブ情報の保存クラスです。
	*/
	private class TabSet implements Comparable<TabSet>{
		public final String title, tooltip;
		public final Component component;
		public final Icon icon;
		/**コンストラクタ*/
		public TabSet(int index){
			this.title = getTitleAt(index);
			this.tooltip = getToolTipTextAt(index);
			this.icon  = getIconAt(index);
			this.component = getComponentAt(index);
		}
		/**比較します。*/
		public int compareTo(TabSet to){
			return title.compareTo(to.title);
		}
		/**比較します。*/
		public boolean equals(Object obj){
			if(obj instanceof TabSet){
				return (compareTo((TabSet)obj) == 0);
			}else{
				return false;
			}
		}
		/**ハッシュコードを返します。*/
		public int hashCode(){
			return title.hashCode() + component.hashCode();
		}
	}
	/**
	*タブのドロップ操作を受信するインターフェースです。
	*/
	private final class ExDropTargetAdapter extends DropTargetAdapter{
		private Point old = new Point();
		public void dragEnter(DropTargetDragEvent e){
			if(isDragAcceptable(e)){
				e.acceptDrag(e.getDropAction());
			}else{
				e.rejectDrag();
			}
		}
		public void dragOver(final DropTargetDragEvent e){
			Point point = e.getLocation();
			if(getTabPlacement()== TOP || getTabPlacement() == BOTTOM){
				initVerticalLine(getTargetIndex(point));
			}else{
				initHorizontalLine(getTargetIndex(point));
			}
			glasspane.setPoint(point);
			if(!old.equals(point)){
				glasspane.repaint();
			}
			old = point;
		}
		public void drop(DropTargetDropEvent e){
			if(isDropAcceptable(e)){
				moveTab(index, getTargetIndex(e.getLocation()));
				e.dropComplete(true);
			}else{
				e.dropComplete(false);
			}
			repaint();
		}
		private boolean isDragAcceptable(DropTargetDragEvent e){
			Transferable t = e.getTransferable();
			try{
				DataFlavor[] f = e.getCurrentDataFlavors();
				return(t.isDataFlavorSupported(f[0]) && index >= 0);
			}catch(NullPointerException ex){
				return false;
			}
		}
		private boolean isDropAcceptable(DropTargetDropEvent e){
			Transferable t = e.getTransferable();
			try{
				DataFlavor[] f = t.getTransferDataFlavors();
				return(t.isDataFlavorSupported(f[0]) && index>=0);
			}catch(NullPointerException ex){
				return false;
			}
		}
	}
	/**
	*ユーザーのドラッグ操作を監視します。
	*/
	private final class ExDragSourceAdapter extends DragSourceAdapter{
		public void dragEnter(DragSourceDragEvent e){
			e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
		}
		public void dragExit(DragSourceEvent e){
			e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
			rect.setRect(0,0,0,0);
			glasspane.setPoint(new Point(-1000, -1000));
			glasspane.repaint();
		}
		public void dragOver(DragSourceDragEvent e){
			Point point = e.getLocation();
			SwingUtilities.convertPointFromScreen(point, glasspane);
			int idx = getTargetIndex(point);
			if(getTabAreaBounds().contains(point) && idx >= 0
			&& idx != index && idx != index + 1){
				e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
				glasspane.setCursor(DragSource.DefaultMoveDrop);
			}else{
				e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
				glasspane.setCursor(DragSource.DefaultMoveNoDrop);
			}
		}
		public void dragDropEnd(DragSourceDropEvent e){
			rect.setRect(0,0,0,0);
			index = -1;
			glasspane.setVisible(false);
			glasspane.setImage(null);
		}
	}
	/**
	*ドラッグするタブのデータ転送に用います。
	*/
	private final class ExTransferable implements Transferable{
		private final DataFlavor FLAVOR 
			= new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
		public Object getTransferData(DataFlavor flavor){
			return LeafTabbedPane.this.getTabComponentAt(index);
		}
		public DataFlavor[] getTransferDataFlavors(){
			DataFlavor[] f = new DataFlavor[1];
			f[0] = FLAVOR;
			return f;
		}
		public boolean isDataFlavorSupported(DataFlavor flavor){
			return flavor.getHumanPresentableName().equals(NAME);
		}
	}
	/**
	*ドラッグ開始を認識するためのインターフェースです。
	*/
	private final class ExDragGestureListener implements DragGestureListener{
		public void dragGestureRecognized(DragGestureEvent e){
			if(getTabCount() <= 1) return;
			index = indexOfTabComponent((LeafTabTitlePane)e.getComponent().getParent());
			if(!isEnabledAt(index)) return;
			initGlassPane(getTabComponentAt(index), e.getDragOrigin());
			try{
				e.startDrag(DragSource.DefaultMoveDrop, trans, dsl);
			}catch(InvalidDnDOperationException ex){
				ex.printStackTrace();
			}
		}
	}
	/**
	*タブのゴーストの実装です。
	*/
	private final class GhostGlassPane extends JPanel{
		private final AlphaComposite comp;
		private Point location = new Point(0, 0);
		private BufferedImage image;
		/**
		*ゴーストを生成します。
		*/
		public GhostGlassPane(){
			setOpaque(false);
			comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
		}
		/**
		*ゴーストにイメージを設定します。
		*@param image ゴーストのイメージ
		*/
		public void setImage(BufferedImage image){
			this.image = image;
		}
		/**
		*ゴーストの座標を設定します。
		*@param point 座標
		*/
		public void setPoint(Point point){
			this.location = point;
		}
		/**
		*ゴーストを描画します。
		*@param g グラフィックス
		*/
		public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D)g;
			g2.setComposite(comp);
			if(image != null){
				double x = location.getX() - (image.getWidth(this) /2d);
				double y = location.getY() - (image.getHeight(this)/2d);
				g2.drawImage(image, (int)x, (int)y, null);
			}
			if(index >= 0){
				g2.setPaint(Color.BLUE);
				g2.fill(rect);
			}
		}
	}
	/**
	*座標を指定してターゲットとなるタブのインデックスを返します。
	*@param point 座標
	*/
	private int getTargetIndex(Point point){
		Point tabpt = SwingUtilities.convertPoint(glasspane, point, LeafTabbedPane.this);
		boolean isHorizon = (getTabPlacement() == TOP || getTabPlacement() == BOTTOM);
		
		for(int i=0;i<getTabCount();i++){
			Rectangle r = getBoundsAt(i);
			if(isHorizon){
				r.setRect(r.x-r.width/2, r.y,  r.width, r.height);
			}else{
				r.setRect(r.x, r.y-r.height/2, r.width, r.height);
			}
			if(r.contains(tabpt))return i;
		}
		Rectangle r = getBoundsAt(getTabCount() -1);
		if(isHorizon){
			r.setRect(r.x + r.width/2, r.y,  r.width, r.height);
		}else{
			r.setRect(r.x, r.y + r.height/2, r.width, r.height);
		}
		return (r.contains(tabpt))? getTabCount() : -1;
	}
	/**
	*指定したインデックスのタブを指定位置に移動します。
	*@param prev 以前のインデックス
	*@param next 新しいインデックス
	*/
	public void moveTab(int prev, int next){
		if(next < 0 || prev == next) return;
		Component cmp = getComponentAt(prev);
		Component tab = getTabComponentAt(prev);
		String title  = getTitleAt(prev);
		Icon icon     = getIconAt(prev);
		String tip    = getToolTipTextAt(prev);
		boolean flg   = isEnabledAt(prev);
		int target    = (prev > next) ? next : next-1;
		remove(prev);
		addTab(title, icon, cmp, tip, target);
		setEnabledAt(target, flg);
		setTabComponentAt(target, tab);
		if(flg)setSelectedIndex(target);
	}
	/**
	*タブが水平方向の場合に、ターゲットラインを初期化します。
	*@param index 対象のインデックス
	*/
	private void initVerticalLine(int index){
		if(index < 0 || this.index == index || index-this.index == 1){
			rect.setRect(0,0,0,0);
		}else if(index == 0){
			Rectangle r = SwingUtilities.convertRectangle(
				this, getBoundsAt(0), glasspane
			);
			rect.setRect(r.x - LINE_WIDTH /2, r.y, LINE_WIDTH, r.height);
		}else{
			Rectangle r = SwingUtilities.convertRectangle(
				this, getBoundsAt(index-1), glasspane
			);
			rect.setRect(r.x + r.width - LINE_WIDTH/2, r.y, LINE_WIDTH, r.height);
		}
	}
	/**
	*タブが垂直方向の場合に、ターゲットラインを初期化します。
	*@param index 対象のインデックス
	*/
	private void initHorizontalLine(int index){
		if(index < 0 || this.index == index || index-this.index == 1){
			rect.setRect(0,0,0,0);
		}else if(index == 0){
			Rectangle r = SwingUtilities.convertRectangle(
				this, getBoundsAt(0), glasspane
			);
			rect.setRect(r.x, r.y - LINE_WIDTH /2, r.width, LINE_WIDTH);
		}else{
			Rectangle r = SwingUtilities.convertRectangle(
				this, getBoundsAt(index-1), glasspane
			);
			rect.setRect(r.x, r.y + r.width - LINE_WIDTH/2, r.width, LINE_WIDTH);
		}
	}
	/**
	*ガラス領域を初期化します。
	*@param cmp タブコンポーネント
	*@param point タブの座標
	*/
	private void initGlassPane(Component cmp, Point point){
		getRootPane().setGlassPane(glasspane);
		BufferedImage image = new BufferedImage(
			cmp.getWidth(), cmp.getHeight(), BufferedImage.TYPE_INT_ARGB
		);
		Graphics g = image.getGraphics();
		cmp.paint(g);
		glasspane.setImage(image);
		
		Point glasspt = SwingUtilities.convertPoint(cmp, point, glasspane);
		glasspane.setPoint(glasspt);
		glasspane.setVisible(true);
	}
	/**
	*タブ部分の座標を返します。
	*@return タブ部分の座標
	*/
	private Rectangle getTabAreaBounds(){
		Rectangle ret = getBounds();
		Component cmp = getSelectedComponent();
		int index = 0;
		while(cmp == null && index < getTabCount()){
			cmp = getComponentAt(index++);
		}
		Rectangle cmprect = (cmp == null)? new Rectangle() : cmp.getBounds();
		switch(getTabPlacement()){
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
