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
*テキスト領域用の置換ダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*@see LeafReplaceDialog
*/

public class LeafReplaceDialog extends LeafDialog implements ActionListener{
	
	private final int UPWARD_ORIENT=0,DOWNWARD_ORIENT=1;
	private final int HISTORY_MAX = 20;
	
	/**GUI*/
	private JComboBox scombo,rcombo;
	private JButton bs1,bs2,brepl,ball,bcancel;
	private JLabel lb1,lb2;
	private JCheckBox ch;
	private JPanel rbpanel;
	private ButtonGroup rbgroup;
	private JRadioButton[] rb = new JRadioButton[3];
	private JTextComponent textComponent = null;
	
	/**
	*親フレームを指定して検索ダイアログを生成します。
	*@param frame 親フレーム
	*/
	public LeafReplaceDialog(JFrame frame){
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
		
		setTitle(LeafLangManager.get("Replace","置換"));
		
		getContentPane().removeAll();

		lb1 = new JLabel(LeafLangManager.get("Before","置換前"));
		lb1.setBounds(10,10,60,20);
		add(lb1);
		
		scombo = new JComboBox();
		scombo.setEditable(true);
		
		scombo.setBounds(70,10,300,20);
		add(scombo);
		
		lb2 = new JLabel(LeafLangManager.get("After","置換後"));
		lb2.setBounds(10,40,60,20);
		add(lb2);
		
		rcombo = new JComboBox();
		rcombo.setEditable(true);
		
		rcombo.setBounds(70,40,300,20);
		add(rcombo);

		ch = new JCheckBox(LeafLangManager.get
			("Case Sensitive","大文字と小文字を区別")+"(C)",false);
		ch.setMnemonic(KeyEvent.VK_C);
		ch.setBounds(10,70,220,20);
		add(ch);
		
		rbgroup = new ButtonGroup();
		rb[0] = new JRadioButton(LeafLangManager.get("Initial","始点挿入")+"(I)",false);
		rb[0].setMnemonic(KeyEvent.VK_I);
		rb[1] = new JRadioButton(LeafLangManager.get("Replace","置換")+"(S)",true);
		rb[1].setMnemonic(KeyEvent.VK_S);
		rb[2] = new JRadioButton(LeafLangManager.get("Terminal","終点挿入")+"(T)",false);
		rb[2].setMnemonic(KeyEvent.VK_T);

		rbpanel = new JPanel();
		rbpanel.setBounds(240,66,130,90);
		
		rbpanel.add(rb[0],BorderLayout.NORTH);
		rbpanel.add(rb[1],BorderLayout.CENTER);
		rbpanel.add(rb[2],BorderLayout.SOUTH);
		for(int n=0;n<=2;n++){
			rbgroup.add(rb[n]);
			rb[n].setPreferredSize(new Dimension(120,14));
		}
		
		rbpanel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),LeafLangManager.get("Mode","動作")));
		add(rbpanel);

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
		brepl=new JButton(LeafLangManager.get("Replace","置換(R)"));
		brepl.setBounds(380,70,120,22);
		brepl.setMnemonic(KeyEvent.VK_R);
		brepl.addActionListener(this);
		add(brepl);
		ball= new JButton(LeafLangManager.get("Replace All","全て置換(A)"));
		ball.setBounds(380,95,120,22);
		ball.setMnemonic(KeyEvent.VK_A);
		ball.addActionListener(this);
		add(ball);
		bcancel = new JButton(LeafLangManager.get("Exit","閉じる(X)"));
		bcancel.setBounds(380,130,120,22);
		bcancel.setMnemonic(KeyEvent.VK_X);
		bcancel.addActionListener(this);
		add(bcancel);
	}
	public void actionPerformed(ActionEvent e){
		
		Object obj = e.getSource();
		
		addItem(scombo,getText(scombo));
		addItem(rcombo,getText(rcombo));
		
		if(obj==bs1){
			search(UPWARD_ORIENT);
		}else if(obj==bs2){
			search(DOWNWARD_ORIENT);
		}else if(obj==brepl){
			if(getText(scombo).equalsIgnoreCase(textComponent.getSelectedText())){
				if(rb[0].isSelected())
					textComponent.replaceSelection(getText(rcombo)+getText(scombo));
				else if(rb[1].isSelected())
					textComponent.replaceSelection(getText(rcombo));
				else
					textComponent.replaceSelection(getText(scombo)+getText(rcombo));
				search(1);//次の検索
			}
		}else if(obj==ball){
			if(rb[0].isSelected())
				textComponent.setText(textComponent.getText().
					replaceAll(getText(scombo),getText(rcombo)+getText(scombo)));
			else if(rb[1].isSelected())
				textComponent.setText(textComponent.getText().
					replaceAll(getText(scombo),getText(rcombo)));
			else
				textComponent.setText(textComponent.getText().
					replaceAll(getText(scombo),getText(scombo)+getText(rcombo)));
			JOptionPane.showMessageDialog(this,LeafLangManager.get
				("All Replaced","全て置換しました。"),
				LeafLangManager.get("Replace","置換"),JOptionPane.INFORMATION_MESSAGE);
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
		}else{
			start = text.indexOf(target,textComponent.getCaretPosition());
		}
		if(start==-1){
			JOptionPane.showMessageDialog(this,LeafLangManager.get
				("Not found","\""+getText(scombo)+"\"は見つかりませんでした。"),
				LeafLangManager.get("Replace","置換"),JOptionPane.INFORMATION_MESSAGE);
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
	*この置換ダイアログを表示します。
	*{@link LeafDialog#setVisible(boolean)}は使用すべきではありません。
	*@param textComponent 置換するテキストコンポーネント
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
	/**
	*置換後の文字列を返します。
	*@return 置換ボックスの文字列
	*/
	public String getReplaceText(){
		return getText(rcombo);
	}
}
