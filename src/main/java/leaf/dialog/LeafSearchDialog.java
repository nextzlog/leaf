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
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;

import leaf.manager.LeafLangManager;

/**
*テキスト領域用の検索ダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*@see LeafReplaceDialog
*/
public class LeafSearchDialog extends LeafDialog implements ActionListener{

	private final int UPWARD_ORIENT=0,DOWNWARD_ORIENT=1,FROM_TOP=2,FROM_TAIL=3;
	private final int HISTORY_MAX = 20;

	/**GUI*/
	private JComboBox scombo;
	private JButton bs1,bs2,bsa,bsb,bcancel;
	private JLabel lb;
	private JCheckBox ch;
	private JTextComponent textComponent = null;

	/**
	*親フレームを指定して検索ダイアログを生成します。
	*@param frame 親フレーム
	*/
	public LeafSearchDialog(JFrame frame){
		super(frame,null,false);
		setLayout(null);
		setSize(520,200);
		setResizable(false);
		addWindowListener( new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				dispose();
			}
		});

		init();
	}
	/**
	*ダイアログを初期化します。
	*/
	public void init(){
		
		setTitle(LeafLangManager.get("Search","検索"));
		
		getContentPane().removeAll();
		
		lb = new JLabel(LeafLangManager.get("Search","検索語"));
		lb.setBounds(10,10,60,20);
		add(lb);
		
		scombo = new JComboBox();
		scombo.setEditable(true);
		
		scombo.setBounds(70,10,300,20);
		add(scombo);

		ch = new JCheckBox(LeafLangManager.get("Case Sensitive","大文字と小文字を区別(C)"),false);
		ch.setMnemonic(KeyEvent.VK_C);
		ch.setBounds(10,70,220,20);
		add(ch);

		bs1 = new JButton(LeafLangManager.get("Upward","上検索(U)"));
		bs1.setBounds(380,10,120,22);
		bs1.setMnemonic(KeyEvent.VK_U);
		bs1.addActionListener(this);
		add(bs1);
		bs2 = new JButton(LeafLangManager.get("Downward","下検索(D)"));
		bs2.setBounds(380,35,120,22);
		bs2.setMnemonic(KeyEvent.VK_D);
		bs2.addActionListener(this);
		add(bs2);
		getRootPane().setDefaultButton(bs2);
		bsa=new JButton(LeafLangManager.get("First","先頭検索(F)"));
		bsa.setBounds(380,70,120,22);
		bsa.setMnemonic(KeyEvent.VK_F);
		bsa.addActionListener(this);
		add(bsa);
		bsb= new JButton(LeafLangManager.get("Last","末尾検索(L)"));
		bsb.setBounds(380,95,120,22);
		bsb.setMnemonic(KeyEvent.VK_L);
		bsb.addActionListener(this);
		add(bsb);

		bcancel = new JButton(LeafLangManager.get("Exit","閉じる(X)"));
		bcancel.setBounds(380,130,120,22);
		bcancel.setMnemonic(KeyEvent.VK_X);
		bcancel.addActionListener(this);
		add(bcancel);
	}
	public void actionPerformed(ActionEvent e){
		
		Object obj = e.getSource();
		addItem(scombo,getText(scombo));
		
		if(obj==bs1){
			search(UPWARD_ORIENT);
		}else if(obj==bs2){
			search(DOWNWARD_ORIENT);
		}else if(obj==bsa){
			search(FROM_TOP);
		}else if(obj==bsb){
			search(FROM_TAIL);
		}else{
			dispose();
		}
	}
	private void search(int orient){
		String text = textComponent.getText()
			.replaceAll("\r\n","\n").replaceAll("\r","\n");
		if(text.length()==0) return;
		int start = 1;
		String target = getText(scombo);
		if(!ch.isSelected()){
			text=text.toLowerCase();
			target=target.toLowerCase();
		}
		if(orient == UPWARD_ORIENT){
			if(target.equalsIgnoreCase(textComponent.getSelectedText())){
				textComponent.setCaretPosition(Math.max
				(0,textComponent.getCaretPosition()-target.length()-1));
			}
			start = text.lastIndexOf(target,textComponent.getCaretPosition());
		}else if(orient==DOWNWARD_ORIENT){
			start = text.indexOf(target,textComponent.getCaretPosition());
		}else if(orient==FROM_TOP){
			start = text.indexOf(target);
		}else{
			start = text.lastIndexOf(target);
		}
		if(start==-1){
			JOptionPane.showMessageDialog(this,LeafLangManager.get
				("Not found","\""+getText(scombo)+"\"は見つかりませんでした。"),
				LeafLangManager.get("Search","検索"),JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		textComponent.select(start,start + target.length());
	}
	
	private String getText(JComboBox combo){
		return (String)combo.getEditor().getItem();
	}
	
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
	
	/**
	*この検索ダイアログを表示します。
	*{@link LeafDialog#setVisible(boolean)}は使用すべきではありません。
	*@param textComponent 検索するテキストコンポーネント
	*/
	public void showDialog(JTextComponent textComponent){
		this.textComponent = textComponent;
		if(textComponent.getSelectedText()!=null)
			addItem(scombo,textComponent.getSelectedText());
		super.setVisible(true);
	}
	/**
	*検索する文字列を返します。
	*@return 検索ボックスの文字列
	*/
	public String getSearchText(){
		return getText(scombo);
	}
}