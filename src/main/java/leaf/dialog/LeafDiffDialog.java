/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.*;

import leaf.manager.LeafCharsetManager;
import leaf.swing.label.LeafBusyLabel;

import leaf.util.diff.*;

/**
 *テキストファイル間の差分抽出機能を提供するダイアログです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年9月20日
 *@see LeafDiff
 */
public final class LeafDiffDialog extends LeafDialog{
	private JButton bold, bnew, bdiff, bclose;
	private JComboBox oldcomb, newcomb, ocscomb, ncscomb;
	private JLabel oldlb, newlb;
	private JPanel oldpane, newpane;
	private JCheckBox casech;
	private LeafBusyLabel indicator;
	private DiffWorker worker;
	private final JFileChooser chooser;
	private final int HISTORY_MAX = 20;
	private String oldtext, newtext;
	private boolean isFinished = CANCEL_OPTION;
	private EditList result = new EditList();
	
	/**
	 *親フレームを指定してモーダルダイアログを生成します。
	 *@param owner 親フレーム
	 */
	public LeafDiffDialog(Frame owner){
		super(owner, true);
		setContentSize(new Dimension(485, 85));
		setResizable(false);
		setLayout(null);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(worker != null) worker.cancel(false);
				isFinished = CANCEL_OPTION;
			}
		});
		chooser = new JFileChooser();
		init();
	}
	/**
	 *親ダイアログを指定してモーダルダイアログを生成します。
	 *@param owner 親ダイアログ
	 */
	public LeafDiffDialog(Dialog owner){
		super(owner, true);
		setContentSize(new Dimension(485, 85));
		setResizable(false);
		setLayout(null);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(worker != null) worker.cancel(false);
				isFinished = CANCEL_OPTION;
			}
		});
		chooser = new JFileChooser();
		init();
	}
	/**
	 *ダイアログを表示します。
	 *@return 抽出完了した場合true
	 */
	public boolean showDialog(){
		setVisible(true);
		return isFinished;
	}
	/**
	 *デフォルトの比較元ファイルと
	 *比較先ファイルを指定してダイアログを表示します。
	 *@param oldFile 比較元ファイル
	 *@param newFile 比較先ファイル
	 *@return 抽出完了した場合true
	 */
	public boolean showDialog(File oldFile, File newFile){
		if(oldFile != null) addItem(oldcomb, oldFile.getAbsolutePath());
		if(newFile != null) addItem(newcomb, newFile.getAbsolutePath());
		oldtext = newtext = null;
		return showDialog();
	}
	/**
	 *比較元文字列とデフォルトの
	 *比較先ファイルを指定してダイアログを表示します。
	 *@param oldtext 比較元文字列
	 *@param newFile 比較先ファイル
	 *@return 抽出完了した場合true
	 */
	public boolean showDialog(String oldtext, File newFile){
		this.oldtext = oldtext;
		this.newtext = null;
		Component[] comps = oldpane.getComponents();
		for(int i=0;i<comps.length;i++){
			comps[i].setEnabled(false);
		}
		comps = newpane.getComponents();
		for(int i=0;i<comps.length;i++){
			comps[i].setEnabled(true);
		}
		if(newFile!=null)
			addItem(newcomb, newFile.getAbsolutePath());
		return showDialog();
	}
	/**
	 *比較先文字列とデフォルトの
	 *比較元ファイルを指定してダイアログを表示します。
	 *@param oldFile 比較元ファイル
	 *@param newtext 比較先文字列
	 *@return 抽出完了した場合true
	 */
	public boolean showDialog(File oldFile, String newtext){
		this.newtext = newtext;
		this.oldtext = null;
		Component[] comps = newpane.getComponents();
		for(int i=0;i<comps.length;i++){
			comps[i].setEnabled(false);
		}
		comps = oldpane.getComponents();
		for(int i=0;i<comps.length;i++){
			comps[i].setEnabled(true);
		}
		if(oldFile!=null) addItem(oldcomb, oldFile.getAbsolutePath());
		return showDialog();
	}
	/**
	 *抽出した差分を表す編集リストを返します。
	 *@return 差分のリスト
	 */
	public EditList getResult(){
		return result;
	}
	/**
	 *ダイアログの表示と配置を初期化します。
	 */
	@Override public void init(){
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		/*old text*/
		oldpane = new JPanel(null);
		oldpane.setBounds(0,10,490,20);
		add(oldpane);
		
		/*old file*/
		oldlb = new JLabel(translate("button_open_old_file"));
		oldlb.setBounds(5,0,50,20);
		oldpane.add(oldlb);
		oldcomb = new JComboBox();
		oldcomb.setBounds(55,0,325,20);
		oldcomb.setEditable(true);
		oldpane.add(oldcomb);
		
		oldcomb.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start();
			}
		});
		
		/*select old file*/
		bold = new JButton("...");
		bold.setBounds(382,0,23,20);
		oldpane.add(bold);
		
		bold.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openOldFile();
			}
		});
		
		/*charset of old file*/
		ocscomb = new JComboBox(LeafCharsetManager.getCharsets());
		ocscomb.setBounds(410,0,80,20);
		oldpane.add(ocscomb);
		
		/*new text*/
		newpane = new JPanel(null);
		newpane.setBounds(0,35,490,20);
		add(newpane);
		
		/*new file*/
		newlb = new JLabel(translate("button_open_new_file"));
		newlb.setBounds(5,0,50,20);
		newpane.add(newlb);
		newcomb = new JComboBox();
		newcomb.setBounds(55,0,325,20);
		newcomb.setEditable(true);
		newpane.add(newcomb);
		
		newcomb.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start();
			}
		});
		
		/*select new file*/
		bnew = new JButton("...");
		bnew.setBounds(382,0,23,20);
		newpane.add(bnew);
		
		bnew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openNewFile();
			}
		});
		
		/*charset of new file*/
		ncscomb = new JComboBox(LeafCharsetManager.getCharsets());
		ncscomb.setBounds(410,0,80,20);
		newpane.add(ncscomb);
		
		/*indicator*/
		indicator = new LeafBusyLabel();
		indicator.setBounds(10,70,20,20);
		add(indicator);
		
		/*checkbox : case sensitive*/
		casech = new JCheckBox(translate("case_sensitive"), true);
		casech.setBounds(10, 68, 250, 22);
		casech.setMnemonic(KeyEvent.VK_S);
		add(casech);
		
		/*button : start diff*/
		bdiff = new JButton(translate("button_diff"));
		bdiff.setBounds(265,68,100,22);
		bdiff.setMnemonic(KeyEvent.VK_D);
		add(bdiff);
		
		bdiff.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start();
			}
		});
		
		/*button : close*/
		bclose = new JButton(translate("button_close"));
		bclose.setBounds(385,68,100,22);
		bclose.setMnemonic(KeyEvent.VK_C);
		add(bclose);
		
		bclose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(worker!=null){
					worker.cancel(false);
					isFinished = CANCEL_OPTION;
				}
				dispose();
			}
		});
	}
	/**比較元ファイルの選択*/
	private void openOldFile(){
		File file = new File(getText(oldcomb));
		if(file.isDirectory()) chooser.setCurrentDirectory(file);
		else chooser.setSelectedFile(file);
		if(chooser.showOpenDialog(this)
			== JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
			addItem(oldcomb, file.getAbsolutePath());
		}
	}
	/**比較先ファイルの選択*/
	private void openNewFile(){
		File file = new File(getText(newcomb));
		if(file.isDirectory())
			chooser.setCurrentDirectory(file);
		else
			chooser.setSelectedFile(file);
		if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
			addItem(newcomb, file.getAbsolutePath());
		}
	}
	/**有効なファイルか確認*/
	private boolean exists(File file){
		if(file.getPath().isEmpty()){
			showMessage(translate("exists_file_is_empty"));
			return false;
		}
		if(file.isDirectory()){
			showMessage(translate("exists_file_is_directory", file));
			return false;
		}
		if(!file.exists()){
			showMessage(translate("exists_file_not_exist", file));
			return false;
		}
		return true;
	}
	/**差分抽出開始*/
	private void start(){
		File oldFile = oldtext!=null ?null: new File(getText(oldcomb));
		File newFile = newtext!=null ?null: new File(getText(newcomb));
		if(oldFile != null && !exists(oldFile)){openOldFile(); return;}
		if(newFile != null && !exists(newFile)){openNewFile(); return;}
		
		addItem(oldcomb, getText(oldcomb));
		addItem(newcomb, getText(newcomb));
		
		if(!indicator.isRunning()){
			indicator.start();
			worker = new DiffWorker(oldFile, newFile);
			worker.execute();
			bdiff.setEnabled(false);
		}
	}
	/**バックグラウンド処理*/
	private class DiffWorker extends SwingWorker{
		private final File oldFile, newFile;
		private final boolean isCaseSensitive;
		public DiffWorker(File oldFile, File newFile){
			this.oldFile = oldFile;
			this.newFile = newFile;
			isCaseSensitive = casech.isSelected();
		}
		@Override protected String doInBackground() throws IOException{
			LeafDiff diff = new LeafDiff(){
				@Override
				protected boolean equals(Object oobj, Object nobj){
					if(isCaseSensitive) return oobj.equals(nobj);
					return ((String)oobj).equalsIgnoreCase((String)nobj);
				}
			};
			if(oldFile == null && newFile != null){
				result = diff.compare(oldtext, newFile,
					(Charset)ncscomb.getSelectedItem());
			}else if(oldFile != null && newFile == null){
				result = diff.compare(oldFile, newtext,
					(Charset)ocscomb.getSelectedItem());
			}else{
				result = diff.compare(oldFile, newFile,
					(Charset)ocscomb.getSelectedItem(),
					(Charset)ncscomb.getSelectedItem());
			}
			return "Done";
		}
		@Override public void done(){
			indicator.stop();
			bdiff.setEnabled(true);
			isFinished = OK_OPTION;
			dispose();
		}
	}
	/**アイテムの取得*/
	private String getText(JComboBox combo){
		return (String)combo.getEditor().getItem();
	}
	/**アイテムの追加*/
	private void addItem(JComboBox combo, String str){
		if(str==null||str.isEmpty()) return;
		DefaultComboBoxModel model =
		(DefaultComboBoxModel)combo.getModel();
		model.removeElement(str);
		model.insertElementAt(str,0);
		if(model.getSize()>HISTORY_MAX){
			model.removeElementAt(HISTORY_MAX);
		}
		combo.setSelectedIndex(0);
	}
}
