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
package leaf.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import leaf.dialog.LeafDialog;
import leaf.manager.*;
/**
*{@link JTabbedPane}のタブ検索用の画面です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年9月6日
*@see leaf.components.tabbedpane.LeafTabbedPane
*/
public final class LeafTabFinder extends LeafDialog{
	
	/**GUI*/
	private JComboBox tabcomb;
	private JList reslist;
	private JButton bsrch,bexit;
	private JLabel tablb,reslb;
	
	private final int HISTORY_MAX = 20;
	private final JTabbedPane tabpane;
	
	private ArrayList<Integer> result = new ArrayList<Integer>();
	private ArrayList<String> titles = new ArrayList<String>();
	
	private boolean isApproved = CANCEL_OPTION;
	
	/**
	*親フレームと検索対象の{@link JTabbedPane}、デフォルトの部分一致
	*文字列を指定してモーダレスなタブ検索ダイアログを生成します。
	*@param owner 親フレーム
	*@param tab 検索対象のJTabbedPane
	*/
	public LeafTabFinder(Frame owner, JTabbedPane tab){
		super(owner, null, false);
		setLayout(null);
		getContentPane().setPreferredSize(new Dimension(465,150));
		pack();
		setResizable(false);
		
		this.tabpane = tab;
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
			}
		});
		
		init();
		
		tab.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				search();
			}
		});
	}
	/**
	*親フレームと検索対象の{@link JTabbedPane}、デフォルトの部分一致
	*文字列を指定してモーダレスなタブ検索ダイアログを生成します。
	*@param owner 親フレーム
	*@param tab 検索対象のJTabbedPane
	*@param part タブのタイトルの一部
	*/
	public LeafTabFinder(Frame owner, JTabbedPane tab, String part){
		this(owner, tab);
		addItem(tabcomb,part);
	}
	/**
	*タブ検索ダイアログを表示します。
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(){
		search();
		setVisible(true);
		return isApproved;
	}
	/**
	*デフォルトの部分一致文字列を
	*指定してタブ検索ダイアログを表示します。
	*@param part タブのタイトルの一部
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(String part){
		addItem(tabcomb,part);
		return showDialog();
	}
	/**
	*検索した結果を配列で返します。
	*@return 検索結果のインデックス配列
	*/
	public int[] getResult(){
		int[] ret = new int[result.size()];
		for(int i=0;i<ret.length;i++){
			ret[i] = result.get(i);
		}
		return ret;
	}
	/**
	*検索した結果のタイトル一覧を返します。
	*@return 検索結果のタイトル配列
	*/
	public String[] getTitles(){
		String[] ret = titles.toArray(new String[0]);
		return ret;
	}
	/**
	*ファイル検索ダイアログを初期化します。
	*/
	public void init(){
		
		setTitle(LeafLangManager.get("Tab Search","タブ検索"));
		getContentPane().removeAll();
		
		/*検索条件*/
		tablb = new JLabel(LeafLangManager.get("Partial","部分一致"));
		tablb.setBounds(5,10,60,20);
		add(tablb);
		tabcomb = new JComboBox();
		tabcomb.setEditable(true);
		tabcomb.setBounds(65,10,290,20);
		add(tabcomb);
		
		/*検索結果一覧*/
		reslb = new JLabel(LeafLangManager.get("Result","検索結果"));
		reslb.setBounds(5,40,60,20);
		add(reslb);
		reslist = new JList();
		JScrollPane scroll = new JScrollPane(reslist);
		scroll.setBounds(65,40,290,100);
		add(scroll);
		
		reslist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				int index = reslist.getSelectedIndex();
				if(index>=0&&index<result.size()&&index<tabpane.getTabCount()){
					tabpane.setSelectedIndex(result.get(index));
				}
			}
		});
		
		/*検索ボタン*/
		bsrch = new JButton(LeafLangManager.get("Search","検索(S)"));
		bsrch.setMnemonic(KeyEvent.VK_S);
		bsrch.setBounds(365,75,100,20);
		bsrch.setMnemonic(KeyEvent.VK_S);
		getRootPane().setDefaultButton(bsrch);
		add(bsrch);
		
		bsrch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search();
				isApproved = OK_OPTION;
			}
		});
		
		/*閉じるボタン*/
		bexit = new JButton(LeafLangManager.get("Exit","閉じる(X)"));
		bexit.setMnemonic(KeyEvent.VK_X);
		bexit.setBounds(365,110,100,20);
		bexit.setMnemonic(KeyEvent.VK_X);
		add(bexit);
		
		bexit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isApproved = CANCEL_OPTION;
				dispose();
			}
		});
	}
	/**検索開始*/
	private void search(){
		result.clear();
		titles.clear();
		addItem(tabcomb,getText(tabcomb));
		String part = getText(tabcomb).toLowerCase();
		int current = -1;
		for(int i=0;i<tabpane.getTabCount();i++){
			String title = tabpane.getTitleAt(i).toLowerCase();
			if(title.indexOf(part)>=0){
				result.add(i);
				titles.add(tabpane.getTitleAt(i));
				if(tabpane.getSelectedIndex()==i)current = result.size()-1;
			}
		}
		result.trimToSize();
		titles.trimToSize();
		reslist.setListData(titles.toArray(new String[0]));
		if(current>=0)reslist.setSelectedIndex(current);
	}
	/**アイテムの取得*/
	private String getText(JComboBox combo){
		return (String)combo.getEditor().getItem();
	}
	/**アイテムの追加*/
	private void addItem(JComboBox combo,String str){
		if(str==null||str.length()==0) return;
		DefaultComboBoxModel model = (DefaultComboBoxModel)combo.getModel();
		model.removeElement(str);
		model.insertElementAt(str,0);
		if(model.getSize()>HISTORY_MAX){
			model.removeElementAt(HISTORY_MAX);
		}
		combo.setSelectedIndex(0);
	}
}