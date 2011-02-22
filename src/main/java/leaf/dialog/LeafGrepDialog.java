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
import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.util.regex.*;
import javax.swing.*;

import leaf.components.LeafLoadIndicator;
import leaf.manager.LeafCharsetManager;
import leaf.manager.LeafFileManager;
import leaf.manager.LeafGrepManager;
import leaf.manager.LeafLangManager;

/**
*正規表現GREP検索用の画面です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年9月4日
*@see LeafGrepManager
*/
public final class LeafGrepDialog extends LeafDialog{
	
	/**GUI*/
	private JComboBox ptcomb,excomb,dcomb,cscomb;
	private JCheckBox subch,casech,regch,dotch;
	private JButton bdir,bsrch,bexit;
	private JLabel ptlb,exlb,dlb;
	
	private LeafLoadIndicator indicator;
	private ExSwingWorker worker = null;
	
	private final LeafGrepManager manager;
	
	private final JFileChooser chooser;
	private final int HISTORY_MAX = 20;
	
	private String[] result = null;
	private boolean isApproved = CANCEL_OPTION;
	
	/**
	*親フレームを指定してモーダルなGREP検索ダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafGrepDialog(Frame owner){
		super(owner, "GREP", true);
		setLayout(null);
		getContentPane().setPreferredSize(new Dimension(465,150));
		pack();
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
				if(worker!=null){
					worker.cancel(false);
					indicator.stop();
					indicator.setVisible(false);
				}
			}
		});
		
		manager = new LeafGrepManager();
		
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		init();
	}
	/**
	*親フレームとデフォルトの拡張子、検索対象ディレクトリを指定して
	*モーダルなGREP検索ダイアログを生成します。
	*@param owner 親フレーム
	*@param ext 拡張子の正規表現
	*@param dir 検索ディレクトリ
	*/
	public LeafGrepDialog(Frame owner, String ext, File dir){
		this(owner);
		addItem(excomb,ext);
		addItem(dcomb,dir.getAbsolutePath());
	}
	/**
	*GREP検索ダイアログを表示します。
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(){
		setVisible(true);
		return isApproved;
	}
	/**
	*デフォルトの検索パターンを
	*指定してGREP検索ダイアログを表示します。
	*@param pattern 正規表現文字列
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(String pattern){
		addItem(ptcomb,pattern);
		return showDialog();
	}
	/**
	*デフォルトの検索対象ディレクトリを
	*指定してGREP検索ダイアログを表示します。
	*@param dir  検索対象ディレクトリ
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(File dir){
		if(dir!=null){
			addItem(dcomb,dir.getAbsolutePath());
			chooser.setCurrentDirectory(dir);
		}
		return showDialog();
	}
	/**
	*デフォルトの検索パターン、検索対象ディレクトリを
	*指定してGREP検索ダイアログを表示します。
	*@param pattern 正規表現文字列
	*@param dir  検索対象ディレクトリ
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(String pattern, File dir){
		addItem(ptcomb,pattern);
		return showDialog(dir);
	}
	/**
	*デフォルトの検索パターン、検索対象拡張子、検索対象ディレクトリを
	*指定してGREP検索ダイアログを表示します。
	*@param pattern 正規表現文字列
	*@param ext 拡張子の正規表現
	*@param dir  検索対象ディレクトリ
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(String pattern, String ext, File dir){
		addItem(ptcomb,pattern);
		addItem(excomb,ext);
		return showDialog(dir);
	}
	/**
	*GREP検索した結果を配列で返します。
	*@return 検索結果を記した文字列の配列
	*/
	public String[] getResult(){
		return result;
	}
	/**
	*GREP検索ダイアログを初期化します。
	*/
	public void init(){
		
		getContentPane().removeAll();
		
		/*検索条件*/
		ptlb = new JLabel(LeafLangManager.get("Pattern","条件"));
		ptlb.setBounds(5,10,60,20);
		add(ptlb);
		ptcomb = new JComboBox();
		ptcomb.setEditable(true);
		ptcomb.setBounds(65,10,400,20);
		add(ptcomb);
		
		ptcomb.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search();
				isApproved = OK_OPTION;
			}
		});
		
		/*拡張子*/
		exlb = new JLabel(LeafLangManager.get("Extension","拡張子"));
		exlb.setBounds(5,35,60,20);
		add(exlb);
		excomb = new JComboBox();
		excomb.setEditable(true);
		excomb.setBounds(65,35,320,20);
		add(excomb);
		
		/*文字セット*/
		cscomb = new JComboBox(LeafCharsetManager.getCharsetNames());
		cscomb.setBounds(390,35,75,20);
		add(cscomb);
		
		/*ディレクトリ*/
		dlb = new JLabel(LeafLangManager.get("Folder","フォルダ"));
		dlb.setBounds(5,60,60,20);
		add(dlb);
		dcomb = new JComboBox();
		dcomb.setEditable(true);
		dcomb.setBounds(65,60,380,20);
		add(dcomb);
		
		dcomb.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!exists(new File(getText(dcomb)))) openDirectory();
			}
		});
		
		/*ディレクトリボタン*/
		bdir = new JButton("...");
		bdir.setBounds(447,60,18,20);
		add(bdir);
		
		bdir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openDirectory();
			}
		});
		
		/*サブフォルダ検索*/
		subch = new JCheckBox(LeafLangManager.get(
			"Search In Subfolder","サブフォルダからも検索(F)"),true);
		subch.setBounds(5,94,200,20);
		subch.setMnemonic(KeyEvent.VK_F);
		add(subch);
		
		/*大文字と小文字を区別*/
		casech = new JCheckBox(LeafLangManager.get(
			"Case Sensitive","大文字と小文字を区別(C)"),true);
		casech.setBounds(5,114,200,20);
		casech.setMnemonic(KeyEvent.VK_C);
		add(casech);
		
		/*正規表現有効*/
		regch = new JCheckBox(LeafLangManager.get(
			"Regex Search","正規表現検索(G)"),true);
		regch.setBounds(210,94,120,20);
		regch.setMnemonic(KeyEvent.VK_G);
		add(regch);
		
		regch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dotch.setEnabled(regch.isSelected());
			}
		});
		
		/*DOTALL*/
		dotch = new JCheckBox(LeafLangManager.get(
			"DOTALL MODE","DOTALL モード"));
		dotch.setBounds(210,114,120,20);
		dotch.setMnemonic(KeyEvent.VK_D);
		add(dotch);
		
		/*ロードインジケータ*/
		indicator = new LeafLoadIndicator();
		indicator.setBounds(340,94,16,16);
		indicator.setVisible(false);
		add(indicator);
		
		/*検索ボタン*/
		bsrch = new JButton(LeafLangManager.get("Search","検索(S)"));
		bsrch.setMnemonic(KeyEvent.VK_S);
		bsrch.setBounds(365,92,100,20);
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
		bexit.setBounds(365,117,100,20);
		bexit.setMnemonic(KeyEvent.VK_X);
		add(bexit);
		
		bexit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isApproved = CANCEL_OPTION;
				if(worker!=null){
					worker.cancel(false);
					indicator.stop();
					indicator.setVisible(false);
				}
				dispose();
			}
		});
		
		addItem(excomb,"(txt|text|htm|html|java)");
		addItem(dcomb, chooser.getCurrentDirectory().getPath());
	}
	/**ディレクトリの選択*/
	private void openDirectory(){
		if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
			addItem(dcomb, chooser.getSelectedFile().getAbsolutePath());
		}
	}
	/**存在するディレクトリかどうか確認*/
	private boolean exists(File dir){
		if(dir==null||!dir.exists()||!dir.isDirectory()){
			showMessage(LeafLangManager.get(
				"Folder not exists","存在しないフォルダです"
			));
			return false;
		}
		return true;
	}
	/**検索開始*/
	private void search(){
		if(getText(ptcomb).equals("")) return;
		if(!exists(new File(getText(dcomb)))) return;
		int opt = 0;
		if(!casech.isSelected()){
			opt = opt | Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
		}if(!regch.isSelected()){
			opt = opt | Pattern.LITERAL;
		}if(dotch.isSelected()){
			opt = opt | Pattern.DOTALL;
		}
		addItem(ptcomb,getText(ptcomb));
		addItem(excomb,getText(excomb));
		addItem(dcomb, getText(dcomb ));
		try{
			if(!indicator.isRunning()){
				indicator.setVisible(true);
				indicator.start();
				worker = new ExSwingWorker(opt);
				worker.execute();
				bsrch.setEnabled(false);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**バックグラウンド処理*/
	private class ExSwingWorker extends SwingWorker<String,String>{
		private final int opt;
		public ExSwingWorker(int opt){
			super();
			this.opt = opt;
		}
		public String doInBackground(){
			try{
				result = manager.grep(
					new File(getText(dcomb)),
					new ExFileFilter(getText(excomb)),
					Charset.forName((String)cscomb.getSelectedItem()),
					Pattern.compile(getText(ptcomb),opt)
				);
			}catch(Exception ex){
				showMessage(LeafLangManager.get(
					"Pattern Syntax Error","検索パターンの構文が不正です"
				));
			}
			return "Done";
		}
		public void done(){
			indicator.stop();
			indicator.setVisible(false);
			bsrch.setEnabled(true);
			dispose();
		}
	}
	/**ファイルフィルタ*/
	private class ExFileFilter implements FileFilter{
		private final String regex;
		public ExFileFilter(String regex){
			this.regex = regex;
		}
		public boolean accept(File f){
			if(f.isDirectory()){
				return subch.isSelected();
			}else{
				String suf = LeafFileManager.getSuffix(f);
				return (suf!=null&&suf.matches(regex));
			}
		}
		public String getDescription(){
			return regex;
		}
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