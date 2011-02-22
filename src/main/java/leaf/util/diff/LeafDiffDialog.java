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
package leaf.util.diff;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;

import leaf.manager.LeafLangManager;
import leaf.components.LeafLoadIndicator;
import leaf.components.text.LeafTextPane;
import leaf.components.text.LeafTextScrollPane;
import leaf.dialog.LeafDialog;

/**
*二つのテキストファイルを比較して差分を抽出するダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月20日
*@see LeafDiffManager
*/
public final class LeafDiffDialog extends LeafDialog{
	
	/**GUI*/
	private JPanel oldpane, newpane;
	private JComboBox oldcomb, newcomb, ocscomb, ncscomb;
	private LeafTextPane restext;
	private JButton bold, bnew, bcopy, bdiff, bsave, bexit;
	private JLabel oldlb, newlb, reslb;
	
	private LeafLoadIndicator indicator;
	private ExSwingWorker worker;
	
	private final JFileChooser chooser;
	private final int HISTORY_MAX = 20;
	private final String[] chsets = {"SJIS","JIS","EUC-JP","UTF8","UTF-16"};
	
	private String oldText, newText;
	private boolean isFinished = CANCEL_OPTION;
	private Edit[] result = new Edit[0];
	
	/**
	*親フレームを指定してモーダルな差分抽出ダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafDiffDialog(Frame owner){
		super(owner, "Diff", true);
		setLayout(null);
		getContentPane().setPreferredSize(new Dimension(485,260));
		pack();
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(worker!=null){
					worker.cancel(false);
					indicator.stop();
					indicator.setVisible(false);
					isFinished = CANCEL_OPTION;
				}
			}
		});
		
		chooser = new JFileChooser();
		
		init();
	}
	/**
	*親フレームとデフォルトの比較元ファイル、比較先ファイルを指定して
	*モーダルな差分抽出ダイアログを生成します。
	*@param owner 親フレーム
	*@param oldFile 比較元ファイル
	*@param newFile 比較先ファイル
	*/
	public LeafDiffDialog(Frame owner, File oldFile, File newFile){
		this(owner);
		addItem(oldcomb, oldFile.getAbsolutePath());
		addItem(newcomb, newFile.getAbsolutePath());
	}
	/**
	*差分抽出ダイアログを表示します。
	*@return 抽出完了した場合true
	*/
	public boolean showDialog(){
		setVisible(true);
		return isFinished;
	}
	/**
	*デフォルトの比較元ファイルと比較先ファイルを指定して
	*差分抽出ダイアログを表示します。
	*@param oldFile 比較元ファイル
	*@param newFile 比較先ファイル
	*@return 抽出完了した場合true
	*/
	public boolean showDialog(File oldFile, File newFile){
		if(oldFile!=null)
			addItem(oldcomb, oldFile.getAbsolutePath());
		if(newFile!=null)
			addItem(newcomb, newFile.getAbsolutePath());
		oldText = newText = null;
		return showDialog();
	}
	/**
	*比較元文字列とデフォルトの比較先ファイルを指定して
	*差分抽出ダイアログを表示します。
	*@param oldText 比較元文字列
	*@param newFile 比較先ファイル
	*@return 抽出完了した場合true
	*/
	public boolean showDialog(String oldText, File newFile){
		this.oldText = oldText;
		this.newText = null;
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
	*比較先文字列とデフォルトの比較元ファイルを指定して
	*差分抽出ダイアログを表示します。
	*@param oldFile 比較元ファイル
	*@param newText 比較先文字列
	*@return 抽出完了した場合true
	*/
	public boolean showDialog(File oldFile, String newText){
		this.newText = newText;
		this.oldText = null;
		Component[] comps = newpane.getComponents();
		for(int i=0;i<comps.length;i++){
			comps[i].setEnabled(false);
		}
		comps = oldpane.getComponents();
		for(int i=0;i<comps.length;i++){
			comps[i].setEnabled(true);
		}
		if(oldFile!=null)
			addItem(oldcomb, oldFile.getAbsolutePath());
		return showDialog();
	}
	/**
	*抽出した差分を表す編集内容の配列を返します。
	*@return 編集内容の配列
	*/
	public Edit[] getResult(){
		return result;
	}
	/**
	*抽出した差分を表す編集内容を文字列表現で返します。
	*@return 編集内容の表現
	*/
	public String getResultText(){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<result.length;i++){
			sb.append(result[i].toString() + "\n");
		}
		return sb.toString();
	}
	/**
	*差分抽出ダイアログを初期化します。
	*/
	public void init(){
		
		getContentPane().removeAll();
		
		/*比較元*/
		oldpane = new JPanel(null);
		oldpane.setBounds(0,10,490,20);
		add(oldpane);
		
		/*比較元ファイル*/
		oldlb = new JLabel(LeafLangManager.get("Old File","比較元"));
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
		
		/*比較元参照*/
		bold = new JButton("...");
		bold.setBounds(382,0,23,20);
		oldpane.add(bold);
		
		bold.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openOldFile();
			}
		});
		
		/*比較元文字コード*/
		ocscomb = new JComboBox(chsets);
		ocscomb.setBounds(410,0,80,20);
		oldpane.add(ocscomb);
		
		/*比較先*/
		newpane = new JPanel(null);
		newpane.setBounds(0,35,490,20);
		add(newpane);
		
		/*比較先ファイル*/
		newlb = new JLabel(LeafLangManager.get("New File","比較先"));
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
		
		/*比較先参照*/
		bnew = new JButton("...");
		bnew.setBounds(382,0,23,20);
		newpane.add(bnew);
		
		bnew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openNewFile();
			}
		});
		
		/*比較先文字コード*/
		ncscomb = new JComboBox(chsets);
		ncscomb.setBounds(410,0,80,20);
		newpane.add(ncscomb);
		
		/*検索結果*/
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			LeafLangManager.get("Result","抽出結果")
		));
		panel.setBounds(5,60,375,195);
		add(panel);
		restext = new LeafTextPane();
		restext.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
		restext.setOpaque(true);
		restext.setEditable(false);
		panel.add(new LeafTextScrollPane(restext));
		
		/*ロードインジケータ*/
		indicator = new LeafLoadIndicator();
		indicator.setBounds(425,90,20,20);
		indicator.setVisible(false);
		add(indicator);
		
		/*コピーボタン*/
		bcopy = new JButton(LeafLangManager.get("Copy","コピー(C)"));
		bcopy.setBounds(385,130,100,22);
		bcopy.setMnemonic(KeyEvent.VK_C);
		add(bcopy);
		
		bcopy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setClipboardText(restext.getText());
			}
		});
		
		/*抽出ボタン*/
		bdiff = new JButton(LeafLangManager.get("Diff","抽出(D)"));
		bdiff.setBounds(385,160,100,22);
		bdiff.setMnemonic(KeyEvent.VK_D);
		add(bdiff);
		
		bdiff.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				start();
			}
		});
		
		/*差分保存ボタン*/
		bsave = new JButton(LeafLangManager.get("Save","保存(S)"));
		bsave.setBounds(385,190,100,22);
		bsave.setMnemonic(KeyEvent.VK_S);
		add(bsave);
		
		bsave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				save();
			}
		});
		
		/*閉じるボタン*/
		bexit = new JButton(LeafLangManager.get("Exit","閉じる(X)"));
		bexit.setBounds(385,220,100,22);
		bexit.setMnemonic(KeyEvent.VK_X);
		add(bexit);
		
		bexit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(worker!=null){
					worker.cancel(false);
					indicator.stop();
					indicator.setVisible(false);
					isFinished = CANCEL_OPTION;
				}
				dispose();
			}
		});
		
		String home = chooser.getCurrentDirectory().getPath();
		addItem(oldcomb, home);
		addItem(newcomb, home);
	}
	/**比較元ファイルの選択*/
	private void openOldFile(){
		File file = new File(getText(oldcomb));
		if(file.isDirectory())
			chooser.setCurrentDirectory(file);
		else
			chooser.setSelectedFile(file);
		if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
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
		if(file==null||!file.exists()||file.isDirectory()){
			JOptionPane.showMessageDialog(this,
				LeafLangManager.get("File not exists","存在しないファイルです"),
				"Diff",JOptionPane.INFORMATION_MESSAGE
			);
			return false;
		}
		return true;
	}
	/**差分抽出開始*/
	private void start(){
		File oldFile = (oldText!=null)?null: new File(getText(oldcomb));
		File newFile = (newText!=null)?null: new File(getText(newcomb));
		if(oldFile!=null&&!exists(oldFile)) return;
		if(newFile!=null&&!exists(newFile)) return;
		
		addItem(oldcomb, getText(oldcomb));
		addItem(newcomb, getText(newcomb));
		
		try{
			if(!indicator.isRunning()){
				indicator.setVisible(true);
				indicator.start();
				worker = new ExSwingWorker(oldFile, newFile);
				worker.execute();
				bdiff.setEnabled(false);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**バックグラウンド処理*/
	private class ExSwingWorker extends SwingWorker{
		private final File oldFile, newFile;
		public ExSwingWorker(File oldFile, File newFile){
			this.oldFile = oldFile;
			this.newFile = newFile;
		}
		public String doInBackground(){
			try{
				if(oldFile==null&&newFile!=null){
					result = LeafDiffManager.createManager(
						oldText, newFile,
						(String)ncscomb.getSelectedItem()
					).compare();
				}else if(oldFile!=null&&newFile==null){
					result = LeafDiffManager.createManager(
						oldFile, newText,
						(String)ocscomb.getSelectedItem()
					).compare();
				}else{
					result = LeafDiffManager.createManager(
						oldFile, newFile,
						(String)ocscomb.getSelectedItem(),
						(String)ncscomb.getSelectedItem()
					).compare();
				}
				restext.setText(getResultText());
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return "Done";
		}
		public void done(){
			indicator.stop();
			indicator.setVisible(false);
			bdiff.setEnabled(true);
			isFinished = OK_OPTION;
		}
	}
	/**差分の保存*/
	private void save(){
		chooser.setSelectedFile(new File(getText(oldcomb)));
		if(chooser.showSaveDialog(this)==chooser.APPROVE_OPTION){
			try{
				new EditList(result).save(chooser.getSelectedFile());
			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this,
					LeafLangManager.get("Failed to save","保存に失敗しました"),
					"Diff",JOptionPane.INFORMATION_MESSAGE
				);
			}
		}
	}
	/**クリップボードに設定*/
	private void setClipboardText(String text){
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection ss = new StringSelection(text);
		clip.setContents(ss, ss);
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