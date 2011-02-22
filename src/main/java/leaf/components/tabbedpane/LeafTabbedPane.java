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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;

import leaf.icon.LeafCloseIcon;
import leaf.manager.LeafLangManager;

/**
*閉じるボタンを持ったタブ領域です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年3月12日
*@see TabListener
*/
public class LeafTabbedPane extends JTabbedPane implements ChangeListener{
	
	private final Icon iclose = new LeafCloseIcon();
	private final Dimension buttonsize;
	private final ArrayList<TabListener> listeners = new ArrayList<TabListener>(1);
	
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
	*タブの表示位置とレイアウトポリシーを指定してからのタブ領域を生成します。
	*@param tabPlacement タブの表示位置
	*@param tabLayoutPolicy レイアウトポリシー
	*/
	public LeafTabbedPane(int tabPlacement, int tabLayoutPolicy){
		super(tabPlacement,tabLayoutPolicy);
		
		addChangeListener(this);
		addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e){
				getSelectedComponent().requestFocusInWindow();
			}
		});
		
		buttonsize = new Dimension(iclose.getIconWidth(),iclose.getIconHeight());
	}
	
	/**
	*コンポーネントの名前をタイトルに指定してタブ項目を追加します。
	*@param component 追加するコンポーネント
	*@return 追加したコンポーネント
	*/
	public Component add(Component component){
		addTab(component.getName(),null,component,getTabCount());
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
		addTab(component.getName(),null,component,index);
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
			addTab((String)constraints, null, component,getTabCount());
		}else if(constraints instanceof Icon){
			addTab(null,(Icon)constraints, component,getTabCount());
		}else{
			addTab(component.getName(),null,component,getTabCount());
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
			addTab((String)constraints, null, component,index);
		}else if(constraints instanceof Icon){
			addTab(null,(Icon)constraints, component,index);
		}else{
			addTab(component.getName(),null,component,index);
		}
	}
	/**
	*タイトルを指定してタブ項目を追加します。
	*@param title タイトル
	*@param component 追加するコンポーネント
	*@return 追加したコンポーネント
	*/
	public Component add(String title, Component component){
		addTab(title,null,component,getTabCount());
		return component;
	}
	/**
	*タイトルを指定してタブ項目を追加します。
	*@param title タイトル
	*@param component 追加するコンポーネント
	*/
	public void addTab(String title, Component component){
		addTab(title,null,component,getTabCount());
	}
	/**
	*タイトルとアイコンを指定してタブ項目を追加します。
	*@param title タイトル
	*@param icon アイコン
	*@param component 追加するコンポーネント
	*/
	public void addTab(String title, Icon icon, Component component){
		addTab(title, icon, component, getTabCount());
	}
	/**
	*タイトルとアイコン、ツールチップを指定してタブ項目を追加します。
	*@param title タイトル
	*@param icon アイコン
	*@param component 追加するコンポーネント
	*@param tip ツールチップ
	*/
	public void addTab(String title, Icon icon, Component component, String tip){
		addTab(title, icon, component, getTabCount());
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
		LeafTabTitlePane tab = new LeafTabTitlePane(title,icon,component);
		super.add(component,title,index);
		if(index>=0&&index<getTabCount()){
			super.setIconAt(index, icon);
			setTabComponentAt(index,tab);
			setSelectedIndex(index);
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
		public LeafTabTitlePane(String title, Icon icon, Component component){
			
			super(new BorderLayout());
			setOpaque(false);
			
			label = new JLabel(title,icon,JLabel.LEFT);
			label.setBorder(BorderFactory.createEmptyBorder(0,0,0,4));
			
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
					Component tab = LeafTabbedPane.this.getComponentAt(
						indexOfTabComponent(LeafTabTitlePane.this)
					);
					for(TabListener lis: listeners){
						if(!lis.tabClosing(tab))return;
					}
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
	}
	/**
	*選択されたタブにのみ閉じるボタンを表示するために実装されます。
	*/
	public void stateChanged(ChangeEvent e){
		int sel = getSelectedIndex();
		for(int i=0;i<getTabCount();i++){
			LeafTabTitlePane tab = (LeafTabTitlePane)getTabComponentAt(i);
			if(tab!=null)tab.setCloseButtonVisible(i==sel);
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
}
