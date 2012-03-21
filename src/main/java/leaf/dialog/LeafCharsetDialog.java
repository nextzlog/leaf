/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.dialog;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.nio.charset.Charset;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import leaf.manager.LeafCharsetManager;

/**
 *実行環境で利用可能な文字コードのリストから、
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
	*親フレームを指定して設定ダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafCharsetDialog(Frame owner){
		super(owner, true);
		
		setContentSize(new Dimension(420, 270));
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
			}
		});
		setLayout(null);
		init();
	}
	/**
	*親ダイアログを指定して設定ダイアログを生成します。
	*@param owner 親ダイアログ
	*/
	public LeafCharsetDialog(Dialog owner){
		super(owner, true);
		
		setContentSize(new Dimension(420, 270));
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
			}
		});
		setLayout(null);
		init();
	}
	/**
	*使用できる文字セットを追加したリストモデルを生成します。
	*@return リストモデル
	*/
	private DefaultListModel createUsableModel(){
		DefaultListModel model = new DefaultListModel();
		Charset[] usable = LeafCharsetManager.availableCharsets();
		for(int i=0;i<usable.length;i++){
			model.add(i,usable[i]);
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
	*選択されている文字セットの一覧を設定します。
	*@param chsets 文字セットの配列
	*/
	public void setSelectedCharsets(Charset[] chsets){
		usemodel.clear();
		for(Charset chset : chsets){
			usemodel.addElement(chset);
		}
	}
	/**
	*選択されている文字セットの一覧を返します。
	*@return 文字セットの配列
	*/
	public Charset[] getSelectedCharsets(){
		int length = usemodel.getSize();
		Charset[] chsets = new Charset[length];
		for(int i=0;i<length;i++){
			chsets[i] = (Charset)usemodel.get(i);
		}
		return chsets;
	}
	/**
	*ダイアログの表示と配置を初期化します。
	*/
	@Override public void init(){
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		/*選択された文字コード*/
		uselb = new JLabel(translate("label_selected_codes"));
		uselb.setBounds(5,10,160,20);
		add(uselb);
		
		uselist = new JList(usemodel = new DefaultListModel());
		uselist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				bdel.setEnabled(usemodel.getSize()>0);
			}
		});
		
		JScrollPane scroll = new JScrollPane(uselist);
		scroll.setBounds(5,30,160,200);
		add(scroll);
		
		/*使用する文字コード*/
		usablelb = new JLabel(translate("label_available_codes"));
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
		badd = new JButton(translate("button_add"));
		badd.setBounds(170,120,80,22);
		badd.setEnabled(false);
		add(badd);
		
		badd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addCharset();
			}
		});
		
		/*削除*/
		bdel = new JButton(translate("button_delete"));
		bdel.setBounds(170,160,80,22);
		bdel.setEnabled(false);
		add(bdel);
		
		bdel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeCharset();
			}
		});
		
		/*OK*/
		bok = new JButton(translate("button_ok"));
		bok.setBounds(180,240,100,20);
		add(bok);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(usemodel.getSize() > 0){
					isApproved = OK_OPTION;
					dispose();
				}else{
					showMessage(translate("button_ok_action_error"));
				}
			}
		});
		
		/*キャンセル*/
		bcancel = new JButton(translate("button_cancel"));
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
