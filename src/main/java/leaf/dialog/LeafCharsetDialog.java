/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
***************************************************************************************
flgdis:labelの表示値が何を示しているか / flgnum:numに値が入っているか
total:計算の結果数値 / dis:表示値 / memo:メモリ / dnm:分母 / nmr:分子
**************************************************************************************/
package leaf.dialog;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import leaf.manager.LeafCharsetManager;
import leaf.manager.LeafLangManager;

/**
*システムで利用可能な文字コードのリストから、
*使用する文字コードのリストを設定するダイアログです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成 2010年11月13日
*@see LeafCharsetManager
*/
public final class LeafCharsetDialog extends LeafDialog{
	
	private JList uselist,usablelist;
	private JLabel uselb,usablelb;
	private JButton badd,bdel,bok,bcancel;
	
	private DefaultListModel usemodel, usablemodel;
	
	private boolean isApproved = CANCEL_OPTION;
	
	/**
	*デフォルトで何も選択されていない設定ダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafCharsetDialog(Frame owner){
		this(owner, null);
	}
	/**
	*デフォルトで何も選択されていない設定ダイアログを生成します。
	*@param owner 親ダイアログ
	*/
	public LeafCharsetDialog(Dialog owner){
		this(owner, null);
	}
	/**
	*デフォルトの文字セット名を指定して設定ダイアログを生成します。
	*@param owner 親フレーム
	*@param names 文字セット名のリスト
	*/
	public LeafCharsetDialog(Frame owner, ArrayList<String> names){
		
		super(owner,LeafLangManager.get("Character Code","文字コード"),true);
		setLayout(null);
		getContentPane().setPreferredSize(new Dimension(420,270));
		pack();
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
			}
		});
		
		init(names);
	}
	/**
	*デフォルトの文字セット名を指定して設定ダイアログを生成します。
	*@param owner 親ダイアログ
	*@param names 文字セット名のリスト
	*/
	public LeafCharsetDialog(Dialog owner, ArrayList<String> names){
		
		super(owner,LeafLangManager.get("Character Code","文字コード"),true);
		setLayout(null);
		getContentPane().setPreferredSize(new Dimension(420,270));
		pack();
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
			}
		});
		
		init(names);
	}
	/**
	*使用できる文字セットを追加したリストモデルを生成します。
	*@return リストモデル
	*/
	private DefaultListModel createUsableModel(){
		DefaultListModel model = new DefaultListModel();
		String [] usable = LeafCharsetManager.availableCharsets();
		for(int i=0;i<usable.length;i++){
			model.add(i,usable[i]);
		}
		return model;
	}
	/**
	*選択する文字セットを追加したリストモデルを生成します。
	*@param names 文字セット名
	*@return リストモデル
	*/
	private DefaultListModel createUseModel(ArrayList<String> names){
		DefaultListModel model = new DefaultListModel();
		if(names!=null){
			int length = names.size();
			for(int i=0;i<length;i++){
				model.add(i,names.get(i));
			}
		}
		return model;
	}
	/**
	*選択する文字セットを追加します。
	*/
	private void addCharset(){
		int index = usablelist.getSelectedIndex();
		usemodel.add(usemodel.getSize(), usablemodel.get(index));
		uselist.setSelectedIndex(usemodel.getSize()-1);
		if(index<usablemodel.getSize()-1){
			usablelist.setSelectedIndex(index+1);
		}else{
			usablelist.clearSelection();
			badd.setEnabled(false);
		}
	}
	/**
	*選択された文字セットを削除します。
	*/
	private void removeCharset(){
		int index = uselist.getSelectedIndex();
		usemodel.remove(index);
		if(index>=usemodel.getSize()){
			uselist.setSelectedIndex(index-1);
		}else{
			uselist.setSelectedIndex(index);
		}
		badd.setEnabled(
			usablemodel.getSize()>0 &&
			usemodel.indexOf(usablelist.getSelectedValue())<0
		);
	}
	/**
	*ダイアログを表示します。
	*@return 設定を変更した場合true
	*/
	public boolean showDialog(){
		setVisible(true);
		return isApproved;
	}
	/**
	*選択されている文字セット名のリストを返します。
	*@return 文字セット名のリスト
	*/
	public ArrayList<String> getSelectedCharsets(){
		int length = usemodel.getSize();
		ArrayList<String> list = new ArrayList<String>(length);
		for(int i=0;i<length;i++){
			list.add((String)usemodel.get(i));
		}
		return list;
	}
	/**
	*ダイアログを初期化します。
	*@param names 文字セット名のリスト
	*/
	public void init(ArrayList<String> names){
		
		getContentPane().removeAll();
		
		/*選択された文字コード*/
		uselb = new JLabel(
			LeafLangManager.get("Selected Codes","選択された文字コード")
		);
		uselb.setBounds(5,10,160,20);
		add(uselb);
		
		uselist = new JList(usemodel = createUseModel(names));
		uselist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				bdel.setEnabled(usemodel.getSize()>0);
			}
		});
		
		JScrollPane scroll = new JScrollPane(uselist);
		scroll.setBounds(5,30,160,200);
		add(scroll);
		
		/*使用する文字コード*/
		usablelb = new JLabel(
			LeafLangManager.get("Usable Codes","使用できる文字コード")
		);
		usablelb.setBounds(255,10,160,20);
		add(usablelb);
		
		usablelist = new JList(usablemodel = createUsableModel());
		usablelist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				badd.setEnabled(
					usablemodel.getSize()>0 &&
					usemodel.indexOf(usablelist.getSelectedValue())<0
				);
			}
		});
		
		scroll = new JScrollPane(usablelist);
		scroll.setBounds(255,30,160,200);
		add(scroll);
		
		/*追加*/
		badd = new JButton(LeafLangManager.get("Add","追加"));
		badd.setBounds(170,120,80,22);
		badd.setEnabled(false);
		add(badd);
		
		badd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addCharset();
			}
		});
		
		/*削除*/
		bdel = new JButton(LeafLangManager.get("Del","削除"));
		bdel.setBounds(170,160,80,22);
		bdel.setEnabled(false);
		add(bdel);
		
		bdel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeCharset();
			}
		});
		
		/*OK*/
		bok = new JButton("OK");
		bok.setBounds(180,240,100,20);
		add(bok);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(usemodel.getSize()>0){
					isApproved = OK_OPTION;
					dispose();
				}else{
					showMessage(LeafLangManager.get(
						"Select 1 character codes at least.",
						"文字コードを1個以上選択してください"
					));
				}
			}
		});
		
		/*キャンセル*/
		bcancel = new JButton(LeafLangManager.get("Cancel","キャンセル"));
		bcancel.setBounds(300,240,100,20);
		add(bcancel);
		
		bcancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isApproved = CANCEL_OPTION;
				dispose();
			}
		});
	}
}
