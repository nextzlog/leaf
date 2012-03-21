/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.dialog;

import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;

import leaf.manager.LeafCharsetManager;
import leaf.manager.LeafFileManager;
import leaf.manager.LeafGrepManager;
import leaf.swing.label.LeafBusyLabel;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;

/**
 *GREP検索機能をアプリケーション向けに提供するダイアログです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年9月4日
 *@see LeafGrepManager
 */
public final class LeafGrepDialog extends LeafDialog{
	private JButton bdir, bsrch, bclose;
	private JCheckBox subch, casech, regch, dotch;
	private JComboBox ptcomb, excomb, dircomb, cscomb;
	private JLabel ptlb, exlb, dirlb;
	private JPanel cardpanel;
	private CardLayout cardlayout;
	private JProgressBar progress;
	private LeafBusyLabel indicator;
	private GrepWorker worker = null;
	private final JFileChooser chooser;
	private final int HISTORY_MAX = 20;
	private String result = null;
	private boolean isApproved = CANCEL_OPTION;
	
	/**
	 *親フレームを指定してモーダルダイアログを生成します。
	 *@param owner 親フレーム
	 */
	public LeafGrepDialog(Frame owner){
		super(owner, true);
		setContentSize(new Dimension(465, 150));
		setResizable(false);
		setLayout(null);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
				if(worker != null) worker.cancel(false);
			}
		});
		
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(DIRECTORIES_ONLY);
		
		init();
	}
	/**
	 *親ダイアログを指定してモーダルダイアログを生成します。
	 *@param owner 親ダイアログ
	 */
	public LeafGrepDialog(Dialog owner){
		super(owner, true);
		setContentSize(new Dimension(465, 150));
		setResizable(false);
		setLayout(null);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
				if(worker != null) worker.cancel(false);
			}
		});
		
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(DIRECTORIES_ONLY);
		
		init();
	}
	/**
	 *ダイアログを表示します。
	 *@return 検索ボタンで閉じられた場合true
	 */
	public boolean showDialog(){
		setVisible(true);
		return isApproved;
	}
	/**
	 *デフォルトの検索パターンを指定してダイアログを表示します。
	 *@param pattern 正規表現文字列
	 *@return 検索ボタンで閉じられた場合true
	 */
	public boolean showDialog(String pattern){
		addItem(ptcomb, pattern);
		return showDialog();
	}
	/**
	 *デフォルトの検索対象ディレクトリを指定してダイアログを表示します。
	 *@param dir  検索対象ディレクトリ
	 *@return 検索ボタンで閉じられた場合true
	 */
	public boolean showDialog(File dir){
		if(dir!=null){
			if(dir.isFile()) dir = dir.getParentFile();
			addItem(dircomb, dir.getAbsolutePath());
			chooser.setCurrentDirectory(dir);
		}
		return showDialog();
	}
	/**
	 *デフォルトの検索パターン、検索対象ディレクトリを
	 *指定してダイアログを表示します。
	 *@param pattern 正規表現文字列
	 *@param dir  検索対象ディレクトリ
	 *@return 検索ボタンで閉じられた場合true
	 */
	public boolean showDialog(String pattern, File dir){
		addItem(ptcomb, pattern);
		return showDialog(dir);
	}
	/**
	 *デフォルトの検索パターン、検索対象拡張子、
	 *検索対象ディレクトリを指定してダイアログを表示します。
	 *@param pattern 正規表現文字列
	 *@param ext 拡張子の正規表現
	 *@param dir  検索対象ディレクトリ
	 *@return 検索ボタンで閉じられた場合true
	 */
	public boolean showDialog(String pattern, String ext, File dir){
		addItem(ptcomb, pattern);
		addItem(excomb, ext);
		return showDialog(dir);
	}
	/**
	 *GREP検索した結果を文字列で返します。
	 *@return 検索結果
	 */
	public String getResult(){
		return result;
	}
	/**
	 *ダイアログの表示と配置を初期化します。
	 */
	@Override public void init(){
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		/*grep pattern*/
		ptlb = new JLabel(translate("label_pattern"));
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
		
		/*filename extension pattern*/
		exlb = new JLabel(translate("label_filename_extension"));
		exlb.setBounds(5,35,60,20);
		add(exlb);
		excomb = new JComboBox();
		excomb.setEditable(true);
		excomb.setBounds(65,35,320,20);
		add(excomb);
		
		/*charset*/
		cscomb = new JComboBox(LeafCharsetManager.getCharsets());
		cscomb.setBounds(390,35,75,20);
		add(cscomb);
		
		/*card layout*/
		cardpanel = new JPanel(cardlayout = new CardLayout());
		cardpanel.setBounds(5,60,460,20);
		add(cardpanel);
		
		/*root directory*/
		final JPanel card1 = new JPanel();
		card1.setSize(460,20);
		card1.setLayout(null);
		cardpanel.add(card1, "rootdir");
		
		dirlb = new JLabel(translate("label_root_directory"));
		dirlb.setBounds(0,0,60,20);
		card1.add(dirlb);
		dircomb = new JComboBox();
		dircomb.setEditable(true);
		dircomb.setBounds(60,0,380,20);
		card1.add(dircomb);
		
		dircomb.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!exists(new File(getText(dircomb)))) openDirectory();
			}
		});
		
		/*progressbar & task indicator*/
		final JPanel card2 = new JPanel();
		card2.setSize(460,20);
		card2.setLayout(null);
		cardpanel.add(card2, "progress");
		
		indicator = new LeafBusyLabel();
		indicator.setBounds(0,2,16,16);
		card2.add(indicator);
		
		progress = new JProgressBar();
		progress.setBounds(20,0,440,20);
		progress.setStringPainted(true);
		card2.add(progress);
		
		/*button : select root directory*/
		bdir = new JButton("...");
		bdir.setBounds(442, 0, 18, 20);
		card1.add(bdir);
		
		bdir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openDirectory();
			}
		});
		
		/*search in subfolders*/
		subch = new JCheckBox(translate("check_search_in_sub_dirs"));
		subch.setBounds(5,94,200,20);
		subch.setMnemonic(KeyEvent.VK_S);
		subch.setSelected(true);
		add(subch);
		
		/*case sensitive*/
		casech = new JCheckBox(translate("check_case_sensitive"));
		casech.setBounds(5,114,200,20);
		casech.setMnemonic(KeyEvent.VK_C);
		casech.setSelected(true);
		add(casech);
		
		/*enable regex search*/
		regch = new JCheckBox(translate("check_regex"), true);
		regch.setBounds(210,94,140,20);
		regch.setMnemonic(KeyEvent.VK_R);
		add(regch);
		
		regch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dotch.setEnabled(regch.isSelected());
			}
		});
		
		/*enable dotall mode*/
		dotch = new JCheckBox(translate("check_dotall"));
		dotch.setBounds(210,114,140,20);
		dotch.setMnemonic(KeyEvent.VK_D);
		add(dotch);
		
		/*button : start search*/
		bsrch = new JButton(translate("button_find"));
		bsrch.setMnemonic(KeyEvent.VK_F);
		bsrch.setBounds(365,92,100,20);
		add(bsrch);
		
		bsrch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search();
				isApproved = OK_OPTION;
			}
		});
		
		/*button : close dialog*/
		bclose = new JButton(translate("button_close"));
		bclose.setBounds(365,117,100,20);
		add(bclose);
		
		bclose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isApproved = CANCEL_OPTION;
				if(worker!=null) worker.cancel(false);
				dispose();
			}
		});
		
		addItem(excomb,"(txt|text|htm|html|java)");
		addItem(dircomb, chooser.getCurrentDirectory().getPath());
		
		chooser.setLocale(getLocale());
		chooser.updateUI();
	}
	/**
	 *検索ルートディレクトリを選択します。
	 */
	private void openDirectory(){
		if(chooser.showOpenDialog(this) == APPROVE_OPTION){
			addItem(dircomb, chooser.getSelectedFile().getAbsolutePath());
		}
	}
	/**
	 *存在するディレクトリであるか確認します。
	 *@param dir 調べるディレクトリ
	 *@return 存在しない場合false
	 */
	/**存在するディレクトリか確認*/
	private boolean exists(File dir){
		if(dir == null || !dir.exists() || !dir.isDirectory()){
			showMessage(translate("exists_dir_not_exist", dir));
			return false;
		}
		return true;
	}
	/**
	 *検索を開始します。
	 */
	private void search(){
		if(getText(ptcomb).isEmpty()){
			showMessage(translate("search_pattern_empty"));
		}else{
			if(exists(new File(getText(dircomb)))){
				int opt = 0;
				if(!casech.isSelected()){
					opt |= Pattern.UNICODE_CASE;
					opt |= Pattern.CASE_INSENSITIVE;
				}if(!regch.isSelected()){
					opt |= Pattern.LITERAL;
				}if(dotch.isSelected()){
					opt |= Pattern.DOTALL;
				}
				addItem(ptcomb, getText(ptcomb));
				addItem(excomb, getText(excomb));
				addItem(dircomb, getText(dircomb));
				try{
					if(!indicator.isRunning()){
						worker = new GrepWorker(opt);
						worker.execute();
						bsrch.setEnabled(false);
						cardlayout.last(cardpanel);
						indicator.start();
					}
				}catch(PatternSyntaxException ex){
					showMessage(ex.getDescription());
				}
			}
		}
	}
	/**
	 *検索タスク実行中にインジケータを自動更新します。
	 */
	private class GrepWorker extends SwingWorker<String, String>{
		private class GrepManager extends LeafGrepManager{
			public GrepManager(StringWriter sw){
				super(sw);
			}
			@Override
			public void progress(File file, int index, int step){
				setProgress(100 * index / step);
				publish(file.getName());
			}
		}
		private Pattern pattern;
		private Charset chset = (Charset)cscomb.getSelectedItem();
		private File root = new File(getText(dircomb));
		private FileFilter filter = new ExFileFilter(getText(excomb));
		private StringWriter sw = new StringWriter();
		public GrepWorker(int opt) throws PatternSyntaxException{
			pattern = Pattern.compile(getText(ptcomb), opt);
			addPropertyChangeListener(new ProgressListener());
		}
		@Override
		protected String doInBackground(){
			new GrepManager(sw).grep(root, filter, chset, pattern);
			return "Done";
		}
		@Override
		protected void process(List<String> chunks){
			if(!chunks.isEmpty()){
				progress.setString(String.valueOf(chunks.get(0)));
			}
		}
		@Override
		protected void done(){
			result = sw.toString();
			bsrch.setEnabled(true);
			indicator.stop();
			progress.setValue(0);
			cardlayout.first(cardpanel);
			dispose();
		}
	}
	/**
	 *インジケータの自動更新イベントを受け取ります。
	 */
	private class ProgressListener implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent e){
			try{
				progress.setValue(worker.getProgress());
			}catch(NullPointerException ex){}
		}
	}
	/**ファイルフィルタ*/
	private class ExFileFilter implements FileFilter{
		private final String regex;
		public ExFileFilter(String regex){
			this.regex = regex;
		}
		@Override public boolean accept(File f){
			if(f.isDirectory()){
				return subch.isSelected();
			}else{
				String suf = LeafFileManager.getSuffix(f);
				return (suf!=null && suf.matches(regex));
			}
		}
	}
	/**アイテムの取得*/
	private String getText(JComboBox combo){
		return (String)combo.getEditor().getItem();
	}
	/**コンボボックスに追加*/
	private void addItem(JComboBox combo, String str){
		if(str == null || str.isEmpty()) return;
		DefaultComboBoxModel model
		= (DefaultComboBoxModel)combo.getModel();
		model.removeElement(str);
		model.insertElementAt(str, 0);
		if(model.getSize()>HISTORY_MAX){
			model.removeElementAt(HISTORY_MAX);
		}
		combo.setSelectedIndex(0);
	}
}
