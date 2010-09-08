/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components.tabbedpane;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import leaf.icon.*;

/**
*閉じるボタンを持ったタブ領域です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年3月12日
*/
public class LeafTabbedPane extends JTabbedPane implements ChangeListener{
	/**フィールド*/
	private final Icon icon = new LeafCloseIcon();
	private final Dimension buttonsize;
	/**タブ領域を生成します。*/
	public LeafTabbedPane(){
		super(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
		buttonsize = new Dimension(icon.getIconWidth(),icon.getIconHeight());
		this.addChangeListener(this);
		addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e){
				getSelectedComponent().requestFocusInWindow();
			}
		});
	}
	/**
	*TabListenerとタイトルを指定してコンポーネントを追加します。
	*@param lis タブが閉じられたことを通知する先のリスナー
	*@param title 項目のタイトル
	*@param content 追加するコンポーネント
	*/
	public void addTab(final TabListener lis,String title,JComponent content){
		addTab(lis,title,content,title);
	}
	/**
	*TabListenerとタイトル、ツールチップを指定してコンポーネントを追加します。
	*@param lis タブが閉じられたことを通知する先のリスナー
	*@param title 項目のタイトル
	*@param content 追加するコンポーネント
	*@param tooltip ツールチップテキスト
	*/
	public void addTab(final TabListener lis,String title,JComponent content,String tooltip){
		final LeafTabTitlePane tab = new LeafTabTitlePane(lis,title,content);
		super.addTab(null,null,content,tooltip);
		this.setTabComponentAt(this.getTabCount()-1,tab);
		this.setSelectedIndex(this.getTabCount()-1);
		if(getTabCount()==1)tab.setCloseButtonVisible(true);
	}
	/**
	*指定したインデックスのタブ項目のタイトルとツールチップを変更します。
	*@param index 変更するタブ項目のインデックス
	*@param title 新しいタイトル
	*@param tooltip 新しいツールチップテキスト
	*/
	public void setTitleAt(int index,String title,String tooltip){
		((LeafTabTitlePane)getTabComponentAt(index)).setTitle(title);
		this.setToolTipTextAt(index,tooltip);
	}
	/**タブのタイトル部分*/
	private class LeafTabTitlePane extends JPanel{
		private final JLabel label;
		private final JButton button;
		LeafTabTitlePane(final TabListener lis,String title,final JComponent content){
			super(new BorderLayout());
			setOpaque(false);
			/*閉じるボタン付き*/
			label = new JLabel(title);
			label.setBorder(BorderFactory.createEmptyBorder(0,0,0,4));
			button = new JButton(icon);
			button.setPreferredSize(buttonsize);
			/*閉じるボタンの表示設定*/
			button.setBorderPainted(false);
			button.setFocusPainted(false);
			button.setContentAreaFilled(false);
			button.setFocusable(false);
			button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Component tab = LeafTabbedPane.this.getComponentAt
					(LeafTabbedPane.this.indexOfTabComponent(LeafTabTitlePane.this));
					if(lis.tabClosing(tab))LeafTabbedPane.this.remove(tab);
				}
			});
			add(label,BorderLayout.WEST);
			add(button,BorderLayout.EAST);
			setBorder(BorderFactory.createEmptyBorder(2,1,1,1));
			setCloseButtonVisible(false);
		}
		/**タイトル設定*/
		public void setTitle(String title){
			label.setText(title);
		}
		/**タイトル取得*/
		public String getTitle(){
			return label.getText();
		}
		/**閉じるボタンを表示するか設定*/
		public void setCloseButtonVisible(boolean opt){
			button.setVisible(opt);
		}
		/**ラベルの文字色変更*/
		public void setLabelForeground(Color col){
			label.setForeground(col);
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
}
