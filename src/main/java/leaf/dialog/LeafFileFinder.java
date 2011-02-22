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
import javax.swing.*;

import leaf.dialog.LeafDialog;
import leaf.components.LeafLoadIndicator;
import leaf.manager.*;
/**
*ファイル検索用の画面です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年9月6日
*@see LeafFileManager
*/
public final class LeafFileFinder extends LeafDialog{
	
	/**GUI*/
	private JComboBox fncomb,excomb,dcomb;
	private JCheckBox subch,casech;
	private JButton bdir,bsrch,bexit;
	private JLabel fnlb,exlb,dlb;
	
	private LeafLoadIndicator indicator;
	private ExSwingWorker worker = null;
	
	private final LeafFileManager manager;
	
	private final JFileChooser chooser;
	private final int HISTORY_MAX = 20;
	
	private File[] result = new File[0];
	private boolean isApproved = CANCEL_OPTION;
	
	/**
	*親フレームを指定してモーダルなファイル検索ダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafFileFinder(Frame owner){
		super(owner, null, true);
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
		
		manager = new LeafFileManager();
		
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		init();
	}
	/**
	*親フレームとデフォルトの拡張子、検索対象ディレクトリを指定して
	*モーダルなファイル検索ダイアログを生成します。
	*@param owner 親フレーム
	*@param ext 拡張子の正規表現
	*@param dir 検索ディレクトリ
	*/
	public LeafFileFinder(Frame owner, String ext, File dir){
		this(owner);
		addItem(excomb,ext);
		addItem(dcomb,dir.getAbsolutePath());
	}
	/**
	*ファイル検索ダイアログを表示します。
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(){
		setVisible(true);
		return isApproved;
	}
	/**
	*デフォルトの部分一致文字列を
	*指定してファイル検索ダイアログを表示します。
	*@param part ファイル名の一部
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(String part){
		addItem(fncomb,part);
		return showDialog();
	}
	/**
	*デフォルトの検索対象ディレクトリを
	*指定してファイル検索ダイアログを表示します。
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
	*デフォルトの部分一致文字列、検索対象ディレクトリを
	*指定してファイル検索ダイアログを表示します。
	*@param part ファイル名の一部
	*@param dir  検索対象ディレクトリ
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(String part, File dir){
		addItem(fncomb,part);
		return showDialog(dir);
	}
	/**
	*デフォルトの部分一致文字列、検索対象拡張子、検索対象ディレクトリを
	*指定してファイル検索ダイアログを表示します。
	*@param part ファイル名の一部
	*@param ext 拡張子の正規表現
	*@param dir  検索対象ディレクトリ
	*@return 検索ボタンで閉じられた場合true
	*/
	public boolean showDialog(String part, String ext, File dir){
		addItem(fncomb,part);
		addItem(excomb,ext);
		return showDialog(dir);
	}
	/**
	*検索した結果を配列で返します。
	*@return 検索結果のファイル配列
	*/
	public File[] getResult(){
		return result;
	}
	/**
	*検索結果を並べた選択ダイアログを表示して、
	*ユーザーが選択したファイルを返します。
	*ファイルが見つからなかった場合やユーザーが
	*取り消しボタンを押した場合は空のファイルを返します。
	*@param parent ダイアログの親コンポーネント
	*@return 選択されたファイル
	*/
	public File showSelectDialog(Component parent){
		if(result!=null&&result.length>0){
			File file = (File)JOptionPane.showInputDialog(parent,
				LeafLangManager.get("Select File","ファイル選択"),
				LeafLangManager.get("Search Result","検索結果"),
				JOptionPane.QUESTION_MESSAGE,null,result,result[0]);
			return file;
		}else{
			JOptionPane.showMessageDialog(parent,
				LeafLangManager.get("Not found.","見つかりませんでした。"),
				LeafLangManager.get("File Search","ファイル検索"),
				JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
	}
	/**
	*ファイル検索ダイアログを初期化します。
	*/
	public void init(){
		
		setTitle(LeafLangManager.get("File Search","ファイル検索"));
		getContentPane().removeAll();
		
		/*検索条件*/
		fnlb = new JLabel(LeafLangManager.get("Partial","部分一致"));
		fnlb.setBounds(5,10,60,20);
		add(fnlb);
		fncomb = new JComboBox();
		fncomb.setEditable(true);
		fncomb.setBounds(65,10,400,20);
		add(fncomb);
		
		fncomb.getEditor().addActionListener(new ActionListener(){
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
		excomb.setBounds(65,35,400,20);
		add(excomb);
		
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
		subch.setBounds(5,90,200,20);
		subch.setMnemonic(KeyEvent.VK_F);
		add(subch);
		
		/*大文字と小文字を区別*/
		casech = new JCheckBox(LeafLangManager.get(
			"Case Sensitive","大文字と小文字を区別(C)"),true);
		casech.setBounds(5,110,200,20);
		casech.setMnemonic(KeyEvent.VK_C);
		add(casech);
		
		/*ロードインジケータ*/
		indicator = new LeafLoadIndicator();
		indicator.setBounds(340,92,16,16);
		indicator.setVisible(false);
		add(indicator);
		
		/*検索ボタン*/
		bsrch = new JButton(LeafLangManager.get("Search","検索(S)"));
		bsrch.setMnemonic(KeyEvent.VK_S);
		bsrch.setBounds(365,90,100,20);
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
		bexit.setBounds(365,115,100,20);
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
			JOptionPane.showMessageDialog(this,
				LeafLangManager.get("Folder not exists","存在しないフォルダです"),
				LeafLangManager.get("File Search","ファイル検索"),
				JOptionPane.INFORMATION_MESSAGE
			);
			return false;
		}
		return true;
	}
	/**検索開始*/
	private void search(){
		if(getText(fncomb).equals("")) return;
		if(!exists(new File(getText(dcomb)))) return;
		addItem(fncomb,getText(fncomb));
		addItem(excomb,getText(excomb));
		addItem(dcomb, getText(dcomb ));
		try{
			if(!indicator.isRunning()){
				indicator.setVisible(true);
				indicator.start();
				worker = new ExSwingWorker();
				worker.execute();
				bsrch.setEnabled(false);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**バックグラウンド処理*/
	private class ExSwingWorker extends SwingWorker<String,String>{
		public ExSwingWorker(){
			super();
		}
		public String doInBackground(){
			if(casech.isSelected()){
				result = manager.search(
					new File(getText(dcomb)),
					getText(fncomb),
					getText(excomb),
					subch.isSelected()
				);
			}else{
				result = manager.searchIgnoreCase(
					new File(getText(dcomb)),
					getText(fncomb),
					getText(excomb),
					subch.isSelected()
				);
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