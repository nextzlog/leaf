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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import leaf.manager.LeafLangManager;

/**
*テキスト領域用の置換ダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成；2010年5月22日 正規表現対応：2010年9月8日
*@see LeafSearchDialog
*@see LeafGrepDialog
*/
public final class LeafReplaceDialog extends LeafDialog{
	
	/**GUI*/
	private JComboBox scomb,rcomb;
	private JLabel slb,rlb;
	private JCheckBox clipch,casech,regch,dotch;
	private JRadioButton textrb,startrb,endrb;
	private JButton bupward,bdownward,brepl,ball,bexit;
	
	private Matcher matcher;
	private JTextComponent component;
	
	private final int HISTORY_MAX = 20;
	private final int SEARCH_UPWARD = 0, SEARCH_DOWNWARD = 1;
	/**
	*親フレームを指定してモーダレスな置換ダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafReplaceDialog(Frame owner){
		super(owner,null,false);
		setLayout(null);
		getContentPane().setPreferredSize(new Dimension(480,150));
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
	*親ダイアログを指定してモーダレスな置換ダイアログを生成します。
	*@param owner
	*/
	public LeafReplaceDialog(Dialog owner){
		super(owner,null,false);
		setLayout(null);
		getContentPane().setPreferredSize(new Dimension(480,150));
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
	*検索・置換対象のテキストコンポーネントを指定して置換ダイアログを生成します。
	*@param component 検索・置換操作対象のテキスト領域
	*/
	public void showDialog(JTextComponent component){
		this.component = component;
		setVisible(true);
	}
	/**
	*検索対象のテキストコンポーネントを設定します。
	*@param component 検索操作対象のテキスト領域
	*/
	public void setTextComponent(JTextComponent component){
		this.component = component;
	}
	/**
	*現在の検索キーワードを返します。
	*@return 最後に入力された検索文字列
	*/
	public String getSearchText(){
		return matcher.pattern().pattern();
	}
	/**
	*検索キーワードを設定します。
	*@param text 検索文字列
	*/
	public void setSearchText(String text){
		addItem(scomb,text);
	}
	/**
	*置換ダイアログを初期化します。{@link LeafLangManager}による
	*言語指定が更新されていた場合、使用言語にも反映されます。
	*/
	public void init(){
		
		setTitle(LeafLangManager.get("Replace","置換"));
		
		getContentPane().removeAll();
		
		/*置換前*/
		slb = new JLabel(LeafLangManager.get("Before","置換前"));
		slb.setBounds(5,10,45,20);
		add(slb);
		
		scomb = new JComboBox();
		scomb.setEditable(true);
		scomb.setBounds(50,10,315,20);
		add(scomb);
		
		scomb.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start(SEARCH_DOWNWARD);
			}
		});
		
		/*置換後*/
		rlb = new JLabel(LeafLangManager.get("After","置換後"));
		rlb.setBounds(5,35,45,20);
		add(rlb);
		
		rcomb = new JComboBox();
		rcomb.setEditable(true);
		rcomb.setBounds(50,35,315,20);
		add(rcomb);
		
		rcomb.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start(SEARCH_DOWNWARD);
			}
		});
		
		/*クリップボードから貼り付け*/
		clipch = new JCheckBox(LeafLangManager.get(
			"Paste from Clipboard","クリップボードから貼り付け(P)")
		);
		clipch.setMnemonic(KeyEvent.VK_P);
		clipch.setBounds(5,65,200,20);
		add(clipch);
		
		clipch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setClipboardPasteMode(clipch.isSelected());
			}
		});
		
		/*大文字と小文字を区別*/
		casech = new JCheckBox(LeafLangManager.get(
			"Case Sensitive","大文字と小文字を区別(C)"),true
		);
		casech.setBounds(5,85,200,20);
		casech.setMnemonic(KeyEvent.VK_C);
		add(casech);
		
		/*正規表現*/
		regch = new JCheckBox(LeafLangManager.get(
			"Regex Search","正規表現検索(G)"),true
		);
		regch.setBounds(5,105,200,20);
		regch.setMnemonic(KeyEvent.VK_G);
		add(regch);
		
		regch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dotch.setEnabled(regch.isSelected());
				ball.setEnabled(regch.isSelected());
			}
		});
		
		/*DOTALL*/
		dotch = new JCheckBox(LeafLangManager.get(
			"DOTALL MODE","DOTALLモード"
		));
		dotch.setBounds(5,125,200,20);
		dotch.setMnemonic(KeyEvent.VK_O);
		add(dotch);
		
		/*置換対象*/
		JPanel panel = new JPanel(null);
		panel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			LeafLangManager.get("Target","置換対象")
		));
		panel.setBounds(240,65,125,85);
		add(panel);
		ButtonGroup group = new ButtonGroup();
		
		textrb = new JRadioButton(
			LeafLangManager.get("Selected(0)","選択文字列(0)"),true
		);
		textrb.setBounds(5,20,115,20);
		textrb.setMnemonic(KeyEvent.VK_0);
		panel.add(textrb);
		group.add(textrb);
		
		startrb = new JRadioButton(
			LeafLangManager.get("Start Point(1)","選択開始点(1)")
		);
		startrb.setBounds(5,40,115,20);
		startrb.setMnemonic(KeyEvent.VK_1);
		panel.add(startrb);
		group.add(startrb);
		
		endrb = new JRadioButton(
			LeafLangManager.get("End Point(2)","選択終了点(2)")
		);
		endrb.setBounds(5,60,115,20);
		endrb.setMnemonic(KeyEvent.VK_2);
		panel.add(endrb);
		group.add(endrb);
		
		/*上検索*/
		bupward = new JButton(LeafLangManager.get("Upward","上検索(U)"));
		bupward.setBounds(380,10,100,22);
		bupward.setMnemonic(KeyEvent.VK_U);
		add(bupward);
		
		bupward.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start(SEARCH_UPWARD);
			}
		});
		
		/*下検索*/
		bdownward = new JButton(LeafLangManager.get("Downward","下検索(D)"));
		bdownward.setBounds(380,35,100,22);
		bdownward.setMnemonic(KeyEvent.VK_D);
		add(bdownward);
		
		bdownward.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start(SEARCH_DOWNWARD);
			}
		});
		
		/*置換*/
		brepl = new JButton(LeafLangManager.get("Replace","置換(R)"));
		brepl.setBounds(380,65,100,22);
		brepl.setMnemonic(KeyEvent.VK_R);
		add(brepl);
		
		brepl.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				replace();
			}
		});
		
		/*全て置換*/
		ball = new JButton(LeafLangManager.get("Replace All","全て置換(A)"));
		ball.setBounds(380,90,100,22);
		ball.setMnemonic(KeyEvent.VK_A);
		add(ball);
		
		ball.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				replaceAll();
			}
		});
		
		/*閉じる*/
		bexit = new JButton(LeafLangManager.get("Exit","閉じる(X)"));
		bexit.setBounds(380,125,100,22);
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
	private void updatePattern(boolean isLiteral){
		int opt = 0;
		if(!casech.isSelected()){
			opt = opt | Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
		}
		if(dotch.isSelected()){
			opt = opt | Pattern.DOTALL;
		}
		Pattern pattern;
		try{
			if(isLiteral){
				opt = opt | Pattern.LITERAL;
				pattern = Pattern.compile(getText(scomb),opt);
			}else{
				pattern = Pattern.compile("(" + getText(scomb) + ")",opt);
			}
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
			showMessage(LeafLangManager.translate(
				"Not found \"[arg]\" [arg].",
				"「[arg]」が[arg]見つかりません",
				getText(scomb), getOrientText(ward)
			));
		}
	}
	/**検索方向を表す文字列を返す*/
	private String getOrientText(int ward){
		switch(ward){
			case SEARCH_UPWARD:
				return LeafLangManager.get("upward","前方に");
			case SEARCH_DOWNWARD:
				return LeafLangManager.get("downward","後方に");
			default:
				return null;
		}
	}
	/**検索方向を指定して検索*/
	private boolean search(int ward){
		updatePattern(!regch.isSelected());
		component.requestFocusInWindow();
		try{
			switch(ward){
				case SEARCH_UPWARD:
					return searchUpward(component.getSelectionStart());
				case SEARCH_DOWNWARD:
					return searchDownward(component.getSelectionEnd());
				default:
					return false;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	/**前方検索*/
	private boolean searchUpward(int position){
		if(matcher==null)return true;
		boolean found = false;
		int start = 0, end = 0;
		while(matcher.find(end)){
			if(matcher.end() <= position
			&&component.getSelectionStart()!=matcher.start()){
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
	/**選択文字列を置換*/
	private void replace(){
		try{
			int start = component.getSelectionStart();
			int end   = component.getSelectionEnd();
			
			addItem(rcomb,getText(rcomb));
			
			if(matcher.find(start)){
				if(start==matcher.start() && end==matcher.end()){
					if(clipch.isSelected()){
						if(startrb.isSelected()){
							component.select(start,start);
						}else if(endrb.isSelected()){
							component.select(end,end);
						}
						component.paste();
					}else{
						if(startrb.isSelected()){
							component.select(start,start);
						}else if(endrb.isSelected()){
							component.select(end,end);
						}
						component.replaceSelection(getText(rcomb));
					}
				}
			}
			start(SEARCH_DOWNWARD);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**全て置換*/
	private void replaceAll(){
		updatePattern(false);
		String repl;
		if(clipch.isSelected()){
			repl = getClipboardText();
		}else{
			repl = getText(rcomb);
		}
		if(startrb.isSelected()){
			repl = repl + "$1";
		}else if(endrb.isSelected()){
			repl = "$1" + repl;
		}
		component.setText(matcher.replaceAll(repl));
		addItem(rcomb,getText(rcomb));
	}
	/**クリップボードから貼り付けを設定*/
	private void setClipboardPasteMode(boolean mode){
		if(mode){
			if(getClipboardText()==null){
				showMessage(LeafLangManager.get(
					"Not exists Available Data in clipboard",
					"クリップボードに有効なデータがありません"
				));
				clipch.setSelected(mode = false);
			}
		}
		rcomb.setEnabled(!mode);
	}
	/**クリップボードから文字列取得*/
	private String getClipboardText(){
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		try{
			return (String)clip.getData(DataFlavor.stringFlavor);
		}catch(Exception ex){
			return null;
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