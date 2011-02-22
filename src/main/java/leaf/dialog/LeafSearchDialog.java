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
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import leaf.manager.LeafLangManager;

/**
*テキスト領域用の検索ダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成；2010年5月22日 正規表現対応：2010年9月8日
*@see LeafSearchDialog
*@see LeafGrepDialog
*/
public final class LeafSearchDialog extends LeafDialog{
	
	/**GUI*/
	private JComboBox scomb;
	private JLabel slb;
	private JCheckBox casech,regch,dotch;
	private JRadioButton upwardrb,downwardrb;
	private JButton bnext,bfirst,bexit;
	
	private Matcher matcher;
	private JTextComponent component;
	
	private final int HISTORY_MAX = 20;
	private final int SEARCH_UPWARD = 0, SEARCH_DOWNWARD = 1;
	private final int SEARCH_FIRST  = 2, SEARCH_LAST= 3;
	/**
	*親フレームを指定してモーダレスな検索ダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafSearchDialog(Frame owner){
		super(owner,null,false);
		setLayout(null);
		getContentPane().setPreferredSize(new Dimension(480,100));
		pack();
		setResizable(false);
		addWindowListener( new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				dispose();
			}
		});

		init();
	}
	/**
	*親ダイアログを指定してモーダレスな検索ダイアログを生成します。
	*@param owner
	*/
	public LeafSearchDialog(Dialog owner){
		super(owner,null,false);
		setLayout(null);
		getContentPane().setPreferredSize(new Dimension(480,100));
		pack();
		setResizable(false);
		addWindowListener( new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				dispose();
			}
		});

		init();
	}
	/**
	*検索・検索対象のテキストコンポーネントを指定して検索ダイアログを生成します。
	*@param component 検索操作対象のテキスト領域
	*/
	public void showDialog(JTextComponent component){
		this.component = component;
		setVisible(true);
	}
	/**
	*現在の検索キーワードを返します。
	*@return 最後に入力された検索文字列
	*/
	public String getSearchText(){
		return matcher.pattern().pattern();
	}
	/**
	*ダイアログを表示することなく「次を検索」を実行します。
	*@param component 検索操作対象のテキスト領域
	*/
	public void searchNext(JTextComponent component){
		start(SEARCH_DOWNWARD);
	}
	/**
	*ダイアログを表示することなく「前を検索」を実行します。
	*@param component 検索操作対象のテキスト領域
	*/
	public void searchPrevious(JTextComponent component){
		start(SEARCH_UPWARD);
	}
	/**
	*検索ダイアログを初期化します。{@link LeafLangManager}による
	*言語指定が更新されていた場合、使用言語にも反映されます。
	*/
	public void init(){
		
		setTitle(LeafLangManager.get("Search","検索"));
		
		getContentPane().removeAll();
		
		/*検索条件*/
		slb = new JLabel(LeafLangManager.get("Search","検索語"));
		slb.setBounds(5,10,45,20);
		add(slb);
		
		scomb = new JComboBox();
		scomb.setEditable(true);
		scomb.setBounds(50,10,315,20);
		add(scomb);
		
		scomb.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start((downwardrb.isSelected())?SEARCH_DOWNWARD:SEARCH_UPWARD);
			}
		});
		
		/*大文字と小文字を区別*/
		casech = new JCheckBox(LeafLangManager.get(
			"Case Sensitive","大文字と小文字を区別(C)"),true);
		casech.setBounds(5,40,190,20);
		casech.setMnemonic(KeyEvent.VK_C);
		add(casech);
		
		/*正規表現*/
		regch = new JCheckBox(LeafLangManager.get(
			"Regex Search","正規表現検索(R)"),true);
		regch.setBounds(5,60,190,20);
		regch.setMnemonic(KeyEvent.VK_R);
		add(regch);
		
		regch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dotch.setEnabled(regch.isSelected());
			}
		});
		
		/*DOTALL*/
		dotch = new JCheckBox(LeafLangManager.get(
			"DOTALL MODE","DOTALLモード"));
		dotch.setBounds(5,80,190,20);
		dotch.setMnemonic(KeyEvent.VK_O);
		add(dotch);
		
		/*検索する方向*/
		JPanel panel = new JPanel(null);
		panel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			LeafLangManager.get("Direction","検索方向")
		));
		panel.setBounds(195,45,170,50);
		add(panel);
		ButtonGroup group = new ButtonGroup();
		
		upwardrb = new JRadioButton(
			LeafLangManager.get("Upward","上方(U)"));
		upwardrb.setBounds(5,20,80,20);
		upwardrb.setMnemonic(KeyEvent.VK_U);
		panel.add(upwardrb);
		group.add(upwardrb);
		
		downwardrb = new JRadioButton(
			LeafLangManager.get("Downward","下方(D)"),true);
		downwardrb.setBounds(85,20,80,20);
		downwardrb.setMnemonic(KeyEvent.VK_D);
		panel.add(downwardrb);
		group.add(downwardrb);
		
		/*次を検索*/
		bnext = new JButton(LeafLangManager.get("Next","次を検索(N)"));
		bnext.setBounds(380,10,100,22);
		bnext.setMnemonic(KeyEvent.VK_N);
		add(bnext);
		
		bnext.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start((downwardrb.isSelected())?SEARCH_DOWNWARD:SEARCH_UPWARD);
			}
		});
		
		/*先頭検索*/
		bfirst = new JButton(LeafLangManager.get("First","先頭検索(F)"));
		bfirst.setBounds(380,35,100,22);
		bfirst.setMnemonic(KeyEvent.VK_F);
		add(bfirst);
		
		bfirst.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start((downwardrb.isSelected())?SEARCH_FIRST:SEARCH_LAST);
			}
		});
		
		/*閉じる*/
		bexit = new JButton(LeafLangManager.get("Exit","閉じる(X)"));
		bexit.setBounds(380,78,100,22);
		bexit.setMnemonic(KeyEvent.VK_X);
		add(bexit);
		
		bexit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		
		repaint();
	}
	/**検索パターンを更新*/
	private void updatePattern(){
		int opt = 0;
		if(!casech.isSelected()){
			opt = opt | Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
		}
		if(!regch.isSelected()){
			opt = opt | Pattern.LITERAL;
		}
		if(dotch.isSelected()){
			opt = opt | Pattern.DOTALL;
		}
		try{
			Pattern pattern = Pattern.compile(getText(scomb),opt);
			matcher = pattern.matcher(component.getText());
		}catch(Exception ex){
			showMessage(LeafLangManager.get(
				"Pattern Syntax Error","検索パターンの構文が不正です"
			));
			matcher = null;
		}
	}
	/**検索方向を指定して検索を開始する*/
	private void start(int ward){
		
		if(getText(scomb).length()==0)return;
		addItem(scomb,getText(scomb));
		if(!search(ward)){
			showMessage(LeafLangManager.get("Not found.",
				getOrientText(ward) +" \" "+ getText(scomb)+" \" が見つかりません。"
			));
		}
	}
	/**検索方向を表す文字列を返す*/
	private String getOrientText(int ward){
		switch(ward){
			case SEARCH_UPWARD:
				return LeafLangManager.get("Upward","前方に");
			case SEARCH_DOWNWARD:
				return LeafLangManager.get("Downward","後方に");
			case SEARCH_FIRST:
				return LeafLangManager.get("Nothing","全く");
			case SEARCH_LAST:
				return LeafLangManager.get("Nothing","全く");
			default:
				return null;
		}
	}
	/**検索方向を指定して検索*/
	private boolean search(int ward){
		updatePattern();
		try{
			switch(ward){
				case SEARCH_UPWARD:
					return searchUpward(component.getSelectionStart());
				case SEARCH_DOWNWARD:
					return searchDownward(component.getSelectionEnd());
				case SEARCH_FIRST:
					return searchDownward(0);
				case SEARCH_LAST:
					return searchUpward(-1);
				default:
					return false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	/**前方検索(positionが負の場合末尾検索)*/
	private boolean searchUpward(int position){
		if(matcher==null)return true;
		boolean found = false;
		int start = 0, end = 0;
		while(matcher.find(end)){
			if(matcher.end() <= position
			&& component.getSelectionStart()!=matcher.start()){
				start = matcher.start();
				end   = matcher.end();
				found = true;
			}else if(position < 0){
				start = matcher.start();
				end   = matcher.end();
				found = true;
			}else{
				break;
			}
		}
		component.select(start,end);
		return found;
	}
	/**後方検索*/
	private boolean searchDownward(int position){
		if(matcher==null)return true;
		if(matcher.find(position)){
			component.select(matcher.start(),matcher.end());
			return true;
		}else{
			return false;
		}
	}
	/**コンボボックスの値を得る*/
	private String getText(JComboBox combo){
		return (String)combo.getEditor().getItem();
	}
	/**コンボボックスに追加*/
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