/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.dialog;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import leaf.components.text.*;
import leaf.manager.*;

/**
*Leafのネットワーク機能の設定を行うダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年6月5日
*/
public class LeafNetOptionDialog extends LeafDialog implements ActionListener{
	
	/**秘匿フィールド*/
	private boolean option = CANCEL_OPTION;
	
	/**GUI部品*/
	private final JButton bok,bcancel;
	private final JPanel dtlkpane;
	private final JLabel nicklab,servlab,portlab;
	private final LeafTextField nickfld,servfld;
	private final JSpinner spport;
	
	/**
	*親フレームを指定してダイアログを生成します。
	*@param parent このダイアログの親フレーム
	*/
	public LeafNetOptionDialog(JFrame parent){
		super(parent,"Data Link Settings",true);
		this.setSize(300,180);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				option = CANCEL_OPTION;
				dispose();
			}
		});
		this.setLayout(null);
		
		dtlkpane = new JPanel();
		dtlkpane.setLayout(null);
		dtlkpane.setBounds(5,5,280,110);
		dtlkpane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),LeafLangManager.get("DataLink","データリンク")));
		this.add(dtlkpane);
		
		nicklab = new JLabel(LeafLangManager.get("Nickname","ニックネーム"),JLabel.RIGHT);
		nicklab.setBounds(5,20,60,20);
		dtlkpane.add(nicklab);
		
		nickfld = new LeafTextField(50);
		nickfld.setBounds(70,20,200,20);
		dtlkpane.add(nickfld);
		
		servlab = new JLabel(LeafLangManager.get("Server","サーバー"),JLabel.RIGHT);
		servlab.setBounds(5,50,60,20);
		dtlkpane.add(servlab);
		
		servfld = new LeafTextField(50);
		servfld.setHintText(LeafLangManager.get("Write the server name","サーバーの名前を入力してください"));
		servfld.setBounds(70,50,200,20);
		dtlkpane.add(servfld);
		
		portlab = new JLabel(LeafLangManager.get("Port","ポート"),JLabel.RIGHT);
		portlab.setBounds(5,80,60,20);
		dtlkpane.add(portlab);
		
		SpinnerNumberModel model = new SpinnerNumberModel(5555,5000,5555,5);
		spport = new JSpinner(model);
		spport.setBounds(70,80,200,20);
		((JSpinner.NumberEditor)spport.getEditor()).getTextField().setEditable(false);
		dtlkpane.add(spport);
		
		bok = new JButton("OK");
		bok.setBounds(60,120,80,20);
		this.add(bok);
		bok.addActionListener(this);
		
		bcancel = new JButton(LeafLangManager.get("CANCEL","キャンセル"));
		bcancel.setBounds(150,120,80,20);
		this.add(bcancel);
		bcancel.addActionListener(this);
	}
	/**
	*設定されたニックネームを返します。
	*@return ユーザーのニックネーム
	*/
	public String getNickName(){
		return nickfld.getText();
	}
	/**
	*設定された接続先サーバー名を返します。
	*@return 接続先サーバー名
	*/
	public String getServerName(){
		return servfld.getText();
	}
	/**
	*設定された通信用ポート番号を返します。
	*@return 接続ポート
	*/
	public int getPortNumber(){
		return (Integer)spport.getValue();
	}
	/**
	*このダイアログを表示します。
	*{@link LeafDialog#setVisible(boolean)}は使用すべきでありません。
	*/
	public boolean showDialog(JFrame parent){
		this.setVisible(true);
		return option;
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==bok)option = OK_OPTION;
		else option = CANCEL_OPTION;
		dispose();
	}
}