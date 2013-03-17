/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.dialog;

import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;

import leaf.swing.com.LeafBusyLabel;
import leaf.swing.dialog.LeafDialog;
import leaf.util.unix.LeafGrep;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;

/**
 * GREP検索機能をGUIアプリケーション向けに提供するダイアログです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年9月4日
 *
 * @see LeafGrep
 */
@SuppressWarnings("serial")
public final class LeafGrepDialog extends LeafDialog {
	private JButton button_dir;
	private JButton button_search;
	private JButton button_close;
	private JCheckBox ch_subdir;
	private JCheckBox ch_case;
	private JCheckBox ch_regex;
	private JCheckBox ch_dotall;
	private JComboBox<String>  combo_pattern;
	private JComboBox<String>  combo_file;
	private JComboBox<String>  combo_dir;
	private JComboBox<Charset> combo_chset;
	private JLabel label_pattern;
	private JLabel label_file;
	private JLabel label_dir;
	private JPanel panel_card;
	private JPanel card_1;
	private JPanel card_2;
	private CardLayout cardlayout;
	private JProgressBar progress;
	private LeafBusyLabel indicator;
	private GrepWorker worker = null;
	private final JFileChooser chooser;
	
	private StringWriter result = null;
	private boolean isApproved = CANCEL_OPTION;
	private static final int HISTORY_MAX = 30;
	
	/**
	 * 親フレームを指定してモーダルダイアログを生成します。
	 *
	 * @param owner 親フレーム
	 */
	public LeafGrepDialog(Frame owner) {
		super(owner, true);
		setResizable(false);
		getContentPane().setLayout(null);
		addWindowListener(new DialogCloseListener());
		
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(DIRECTORIES_ONLY);

		initialize();
	}
	
	/**
	 * 親ダイアログを指定してモーダルダイアログを生成します。
	 *
	 * @param owner 親ダイアログ
	 */
	public LeafGrepDialog(Dialog owner) {
		super(owner, true);
		setResizable(false);
		getContentPane().setLayout(null);
		addWindowListener(new DialogCloseListener());
		
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(DIRECTORIES_ONLY);

		initialize();
	}
	
	private class DialogCloseListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			isApproved = CANCEL_OPTION;
			if(worker != null) worker.cancel(false);
		}
	}
	
	/**
	 * ダイアログを表示します。
	 *
	 * @return 検索ボタンで閉じられた場合true
	 */
	public boolean showDialog() {
		setVisible(true);
		return isApproved;
	}
	
	/**
	 * デフォルトの検索文字列パターンを設定します。
	 * 
	 * @param pattern 正規表現文字列
	 */
	public void setSearchTextPattern(String pattern) {
		addItem(combo_pattern, pattern);
	}
	
	/**
	 * デフォルトの検索対象ファイル名のパターンを設定します。
	 * 
	 * @param pattern 正規表現文字列
	 */
	public void setFileNamePattern(String pattern) {
		addItem(combo_file, pattern);
	}
	
	/**
	 * デフォルトの検索開始ディレクトリを設定します。
	 * 
	 * @param dir 検索開始ディレクトリ
	 */
	public void setDirectory(File dir) {
		if(dir == null) return;
		if(dir.isFile()) dir = dir.getParentFile();
		addItem(combo_dir, dir.getAbsolutePath());
		chooser.setCurrentDirectory(dir);
	}
	
	/**
	 * 使用する文字セットのリストを設定します。
	 * 
	 * @param chsets 文字セットの配列
	 */
	public void setCharsetList(Charset[] chsets) {
		combo_chset.setModel(new DefaultComboBoxModel<>(chsets));
	}
	
	/**
	 * GREP検索した結果を読みだすリーダーを返します。
	 *
	 * @return 検索結果
	 */
	public Reader getResult() {
		return new StringReader(result.toString());
	}
	
	/**
	 * ダイアログの表示と配置を初期化します。
	 */
	@Override
	public void initialize() {
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		label_pattern = new JLabel(translate("label_pattern"));
		combo_pattern = new JComboBox<>();
		combo_pattern.setEditable(true);
		
		add(label_pattern);
		add(combo_pattern);
		
		label_file = new JLabel(translate("label_filename"));
		combo_file = new JComboBox<>();
		combo_file.setEditable(true);
		
		add(label_file);
		add(combo_file);
		
		combo_chset = new JComboBox<>();
		add(combo_chset);
		
		panel_card = new JPanel(cardlayout = new CardLayout());
		add(panel_card);
		
		card_1 = new JPanel((LayoutManager) null);
		card_2 = new JPanel((LayoutManager) null);
		
		panel_card.add(card_1, "rootdir");
		panel_card.add(card_2, "progress");
		
		label_dir = new JLabel(translate("label_root_directory"));
		combo_dir = new JComboBox<>();
		combo_dir.setEditable(true);
		
		button_dir = new JButton("...");
		
		card_1.add(label_dir);
		card_1.add(combo_dir);
		card_1.add(button_dir);
		
		indicator = new LeafBusyLabel();
		progress = new JProgressBar();
		progress.setStringPainted(true);
		
		card_2.add(indicator);
		card_2.add(progress);
		
		ch_subdir = new JCheckBox(translate("check_search_in_sub_dirs"));
		ch_subdir.setMnemonic(KeyEvent.VK_S);
		ch_subdir.setSelected(true);
		
		ch_case = new JCheckBox(translate("check_case_sensitive"));
		ch_case.setMnemonic(KeyEvent.VK_C);
		ch_case.setSelected(true);
		
		ch_regex = new JCheckBox(translate("check_regex"));
		ch_regex.setMnemonic(KeyEvent.VK_R);
		ch_regex.setSelected(true);
		
		ch_dotall = new JCheckBox(translate("check_dotall"));
		ch_dotall.setMnemonic(KeyEvent.VK_D);
		
		add(ch_subdir);
		add(ch_case);
		add(ch_regex);
		add(ch_dotall);
		
		button_search = new JButton(translate("button_find"));
		button_search.setMnemonic(KeyEvent.VK_F);
		
		button_close = new JButton(translate("button_close"));
		button_close.setMnemonic(KeyEvent.VK_ESCAPE);
		
		add(button_search);
		add(button_close);
		
		addItem(combo_file,".*");
		addItem(combo_dir, chooser.getCurrentDirectory().getPath());
		
		chooser.setLocale(getLocale());
		chooser.updateUI();
		
		layoutComponents();
		
		combo_pattern.getEditor().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
				isApproved = OK_OPTION;
			}
		});
		
		combo_dir.getEditor().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!exists(new File(getText(combo_dir)))) selectDirectory();
			}
		});
		
		button_dir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectDirectory();
			}
		});
		
		ch_regex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ch_dotall.setEnabled(ch_regex.isSelected());
			}
		});
		
		button_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
				isApproved = OK_OPTION;
			}
		});
		
		button_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isApproved = CANCEL_OPTION;
				if(worker!=null) worker.cancel(false);
				dispose();
			}
		});
	}
	
	private void layoutComponents() {
		int label_y = 10;
		int pref = button_search.getPreferredSize().height;
		
		label_pattern.setBounds( 5, label_y,  60, pref);
		combo_pattern.setBounds(65, label_y, 430, pref);
		
		label_y += pref + 5;
		
		label_file .setBounds(  5, label_y,  60, pref);
		combo_file .setBounds( 65, label_y, 325, pref);
		combo_chset.setBounds(395, label_y, 100, pref);
		
		label_y += pref + 5;
		
		panel_card.setBounds(5, label_y, 490, pref);
		card_1.setSize(460, pref);
		card_2.setSize(460, pref);
		
		label_dir .setBounds(  0, 0,  60, pref);
		combo_dir .setBounds( 60, 0, 390, pref);
		button_dir.setBounds(452, 0,  38, pref);
		
		indicator.setBounds(0, Math.max(0, (pref - 16) / 2), 16, 16);
		progress .setBounds(20, 0, 470, pref);
		
		int ch_y = label_y + pref + 10;
		ch_y += setBounds(ch_subdir, 5, ch_y, 210) + 5;
		ch_y += setBounds(ch_case  , 5, ch_y, 210) + 5;
		
		ch_y = label_y + pref + 10;
		ch_y += setBounds(ch_regex , 220, ch_y, 170) +  5;
		ch_y += setBounds(ch_dotall, 220, ch_y, 170) + 10;
		
		int button_y = label_y + pref + 10;
		button_y += setBounds(button_search, 395, button_y, 100) +  5;
		button_y += setBounds(button_close , 395, button_y, 100) + 10;
		
		setContentSize(new Dimension(500, Math.max(button_y, ch_y)));
	}
	
	private int setBounds(JComponent comp, int x, int y, int width){
		Rectangle bounds = new Rectangle(comp.getPreferredSize());
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		comp.setBounds(bounds);
		return bounds.height;
	}
	
	private void selectDirectory() {
		if(chooser.showOpenDialog(this) == APPROVE_OPTION) {
			addItem(combo_dir, chooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	private boolean exists(File dir) {
		if(dir == null || !dir.exists() || !dir.isDirectory()) {
			showMessage(translate("exists_dir_not_exist", dir));
			return false;
		}
		return true;
	}
	
	private void search() {
		if(getText(combo_pattern).isEmpty()) {
			showMessage(translate("search_pattern_empty"));
		} else {
			if(exists(new File(getText(combo_dir)))) {
				int option = 0;
				if(!ch_case.isSelected()) {
					option |= Pattern.UNICODE_CASE;
					option |= Pattern.CASE_INSENSITIVE;
				}
				if(!ch_regex.isSelected()) {
					option |= Pattern.LITERAL;
				}
				if(ch_dotall.isSelected()) {
					option |= Pattern.DOTALL;
				}
				addItem(combo_pattern, getText(combo_pattern));
				addItem(combo_file, getText(combo_file));
				addItem(combo_dir, getText(combo_dir));
				if(!indicator.isRunning()) try {
					result = new StringWriter();
					worker = new GrepWorker(result, option);
					worker.execute();
					button_search.setEnabled(false);
					cardlayout.last(panel_card);
					indicator.start();
				} catch(PatternSyntaxException ex) {
					showMessage(ex.getDescription());
					result = null;
				}
			}
		}
	}
	
	private class GrepWorker extends SwingWorker<String, String> {
		private class Grep extends LeafGrep {
			public Grep(Writer writer) {
				super(writer);
			}
			
			@Override
			public void progress(File file, int index, int step) {
				setProgress(100 * index / step);
				publish(file.getName());
			}
		}
		
		private Writer writer;
		private Pattern pattern;
		private FileFilter filter;
		private Charset charset;
		private File root;
		
		public GrepWorker(Writer writer, int option) throws PatternSyntaxException {
			this.writer = writer;
			this.root = new File(getText(combo_dir));
			this.filter = new GrepFileFilter(getText(combo_file));
			
			this.pattern = Pattern.compile(getText(combo_pattern), option);
			this.charset = (Charset) combo_chset.getSelectedItem();
			
			addPropertyChangeListener(new ProgressListener());
		}
		
		@Override
		protected String doInBackground() {
			try {
				new Grep(writer).grep(root, filter, charset, pattern);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			return "Done";
		}
		
		@Override
		protected void process(List<String> chunks) {
			if(!chunks.isEmpty()) {
				progress.setString(String.valueOf(chunks.get(0)));
			}
		}
		
		@Override
		protected void done() {
			button_search.setEnabled(true);
			indicator.stop();
			progress.setValue(0);
			cardlayout.first(panel_card);
			dispose();
			if(writer != null) try{
				writer.close();
			} catch(IOException ex) {}
		}
	}
	
	private class ProgressListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent e) {
			if(worker != null) progress.setValue(worker.getProgress());
		}
	}
	
	private class GrepFileFilter implements FileFilter{
		private final String pattern;
		public GrepFileFilter(String pattern) {
			this.pattern = pattern;
		}
		
		@Override
		public boolean accept(File f) {
			if(f.isDirectory()) return ch_subdir.isSelected();
			return f.getName().matches(pattern);
		}
	}
	
	private String getText(JComboBox<String> combo) {
		return (String) combo.getEditor().getItem();
	}
	
	private void addItem(JComboBox<String> combo, String text) {
		DefaultComboBoxModel<String> model;
		if(text != null && !text.isEmpty()) {
			model = (DefaultComboBoxModel<String>) combo.getModel();
			model.removeElement(text);
			model.insertElementAt(text, 0);
			if(model.getSize() > HISTORY_MAX) {
				model.removeElementAt(HISTORY_MAX);
			}
			combo.setSelectedIndex(0);
		}
	}

}