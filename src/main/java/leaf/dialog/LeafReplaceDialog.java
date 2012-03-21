/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.dialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

/**
 *{@link JTextComponent}向けに文字列検索/置換ダイアログを提供します。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成；2010年5月22日 正規表現対応：2010年9月8日
 *@see LeafSearchDialog
 */
public final class LeafReplaceDialog extends LeafDialog{
	private JCheckBox clipch, casech, regch, dotch;
	private JComboBox srchcomb, replcomb;
	private JLabel srchlb, repllb;
	private JRadioButton slctrb, allrb;
	private JButton bup, bdown, brepl, ball, bclose;
	private Matcher matcher;
	private JTextComponent comp;
	private final int HISTORY_MAX = 30;
	private String region = "";
	private int offset = 0;
	
	/**
	 *親フレームを指定してモーダレスダイアログを生成します。
	 *@param owner 親フレーム
	 */
	public LeafReplaceDialog(Frame owner){
		super(owner, false);
		setContentSize(new Dimension(480, 150));
		setResizable(false);
		setLayout(null);
		init();
		
		addComponentListener(new ExComponentListener());
	}
	/**
	 *親ダイアログを指定してモーダレスダイアログを生成します。
	 *@param owner 親ダイアログ
	 */
	public LeafReplaceDialog(Dialog owner){
		super(owner, false);
		setContentSize(new Dimension(480, 150));
		setResizable(false);
		setLayout(null);
		init();
		
		addComponentListener(new ExComponentListener());
	}
	/**
	 *ダイアログが表示された際の動作を指定します。
	 */
	private class ExComponentListener extends ComponentAdapter{
		public void componentShown(ComponentEvent e){
			if(comp != null){
				String selected = comp.getSelectedText();
				if(selected != null){
					addItem(srchcomb, selected);
					slctrb.setSelected(true);
				}else allrb.setSelected(true);
			}
		}
	}
	/**
	 *操作対象のコンポーネントを指定します。
	 *@param comp 検索/置換対象のテキストコンポーネント
	 */
	public void setTextComponent(JTextComponent comp){
		this.comp = comp;
	}
	/**
	 *操作対象のコンポーネントを返します。
	 *@return 検索/置換対象のテキストコンポーネント
	 */
	public JTextComponent getTextComponent(){
		return comp;
	}
	/**
	 *ダイアログの表示と配置を初期化します。
	 */
	@Override public void init(){
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		/*search pattern*/
		srchlb = new JLabel(translate("label_pattern"));
		srchlb.setBounds(5, 10, 50, 20);
		add(srchlb);
		
		srchcomb = new JComboBox();
		srchcomb.setEditable(true);
		srchcomb.setBounds(55, 10, 310, 20);
		add(srchcomb);
		
		srchcomb.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(allrb.isSelected()) search(SEARCH_DOWNWARD);
			}
		});
		
		/*replace*/
		repllb = new JLabel(translate("label_replace"));
		repllb.setBounds(5, 35, 50, 20);
		add(repllb);
		
		replcomb = new JComboBox();
		replcomb.setEditable(true);
		replcomb.setBounds(55, 35, 310, 20);
		add(replcomb);
		
		replcomb.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(allrb.isSelected()) search(SEARCH_DOWNWARD);
			}
		});
		
		/*paste from clipboard*/
		clipch = new JCheckBox(translate("check_clipboard"));
		clipch.setMnemonic(KeyEvent.VK_P);
		clipch.setBounds(5, 65, 200, 20);
		add(clipch);
		
		clipch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setClipboardPasteMode(clipch.isSelected());
			}
		});
		
		/*case sensitive*/
		casech = new JCheckBox(translate("check_case_sensitive"));
		casech.setBounds(5, 85, 200, 20);
		casech.setMnemonic(KeyEvent.VK_C);
		add(casech);
		
		/*regex search*/
		regch = new JCheckBox(translate("check_regex"));
		regch.setBounds(5, 105, 200, 20);
		regch.setMnemonic(KeyEvent.VK_G);
		regch.setSelected(true);
		add(regch);
		
		regch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dotch.setEnabled(regch.isSelected());
				ball.setEnabled(regch.isSelected());
			}
		});
		
		/*dotall mode*/
		dotch = new JCheckBox(translate("check_dotall"));
		dotch.setBounds(5, 125, 200, 20);
		dotch.setMnemonic(KeyEvent.VK_O);
		add(dotch);
		
		/*search region*/
		JPanel panel = new JPanel(null);
		panel.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), translate("panel_region")));
		panel.setBounds(240, 65, 125, 70);
		add(panel);
		ButtonGroup group = new ButtonGroup();
		
		slctrb = new JRadioButton(translate("radio_region_selected"));
		slctrb.setBounds(5, 20, 115, 20);
		slctrb.setMnemonic(KeyEvent.VK_S);
		slctrb.setSelected(true);
		panel.add(slctrb);
		group.add(slctrb);
		
		slctrb.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				if(e.getStateChange() == ItemEvent.SELECTED){
					region = comp.getSelectedText();
					if(region == null)  region = "";
					offset = comp.getSelectionStart();
					bup.setEnabled(false);
					bdown.setEnabled(false);
					brepl.setEnabled(false);
				}
			}
		});
		
		allrb = new JRadioButton(translate("radio_region_whole"));
		allrb.setBounds(5, 40, 115, 20);
		allrb.setMnemonic(KeyEvent.VK_W);
		panel.add(allrb);
		group.add(allrb);
		allrb.setSelected(true);
		
		allrb.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				if(e.getStateChange() == ItemEvent.SELECTED){
					region = comp.getText();
					offset = 0;
					bup.setEnabled(true);
					bdown.setEnabled(true);
					brepl.setEnabled(true);
				}
			}
		});
		
		/*button : search upward*/
		bup = new JButton(translate("button_find_up"));
		bup.setBounds(380, 10, 100, 22);
		bup.setMnemonic(KeyEvent.VK_U);
		add(bup);
		
		bup.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search(SEARCH_UPWARD);
			}
		});
		
		/*button : search downward*/
		bdown = new JButton(translate("button_find_down"));
		bdown.setBounds(380, 35, 100, 22);
		bdown.setMnemonic(KeyEvent.VK_D);
		add(bdown);
		
		bdown.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search(SEARCH_DOWNWARD);
			}
		});
		
		/*button : replace*/
		brepl = new JButton(translate("button_replace"));
		brepl.setBounds(380, 65, 100, 22);
		brepl.setMnemonic(KeyEvent.VK_R);
		add(brepl);
		
		brepl.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				replaceSelection();
			}
		});
		
		/*button : replace all*/
		ball = new JButton(translate("button_replace_all"));
		ball.setBounds(380,90,100,22);
		ball.setMnemonic(KeyEvent.VK_A);
		add(ball);
		
		ball.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				replaceAll();
			}
		});
		
		/*button : close dialog*/
		bclose = new JButton(translate("button_close"));
		bclose.setBounds(380,125,100,22);
		add(bclose);
		
		bclose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
	}
	/**
	 *検索パターンを更新します。
	 *@param isLiteralMode 正規表現検索を無効にする場合true
	 */
	private void updatePattern(boolean isLiteralMode){
		int opt = 0;
		if(!casech.isSelected()){
			opt |= Pattern.UNICODE_CASE;
			opt |= Pattern.CASE_INSENSITIVE;
		}if(dotch.isSelected()){
			opt |= Pattern.DOTALL;
		}try{
			if(isLiteralMode) opt |= Pattern.LITERAL;
			String text = getText(srchcomb);
			Pattern p = Pattern.compile(text, opt);
			this.matcher = p.matcher(this.region);
		}catch(PatternSyntaxException ex){
			showMessage(ex.getDescription());
			this.matcher = null;
		}
	}
	/**
	 *検索方向を指定して検索を開始します。
	 *@param ward 検索方向
	 *@return 見つかった場合true
	 */
	public boolean search(int ward){
		String pattern = getText(srchcomb);
		if(pattern.isEmpty()){
			showMessage(translate("search_pattern_empty"));
			return false;
		}else try{
			addItem(srchcomb, pattern);
			updatePattern(!regch.isSelected());
			comp.requestFocusInWindow();
			int position = comp.getCaretPosition();
			switch(ward){
			case SEARCH_UPWARD:
				return searchUpward(position);
			case SEARCH_DOWNWARD:
				return searchDownward(position);
			}
			return false;
		}catch(IndexOutOfBoundsException ex){
			return false; //2011年12月4日例外処理追加：修正の必要
		}
	}
	/**
	 *前方を検索します。
	 *@param position 検索開始位置
	 *@return 見つかった場合true
	 */
	private boolean searchUpward(int position){
		if(matcher == null) return true;
		int start = offset, end = offset;
		boolean found = false;
		while(matcher.find(end - offset)){
			if(offset + matcher.end() < position){
				start = offset + matcher.start();
				end   = offset + matcher.end();
				found = true;
			}else break;
		}
		if(found) comp.select(start, end);
		else showMessage(translate("not_found",
				matcher.pattern().pattern()));
		return found;
	}
	/**
	 *後方を検索します。
	 *@param position 検索開始位置
	 *@return 見つかった場合true
	 */
	private boolean searchDownward(int position){
		if(matcher == null) return true;
		if(matcher.find(position - offset)){
			int start = offset + matcher.start();
			int end   = offset + matcher.end();
			comp.select(start, end);
			return true;
		}else{
			showMessage(translate("not_found",
				matcher.pattern().pattern()));
			return false;
		}
	}
	/**
	 *選択されている文字列を置換します。
	 */
	private void replaceSelection(){
		addItem(replcomb, getText(replcomb));
		int start = - offset + comp.getSelectionStart();
		int end   = - offset + comp.getSelectionEnd();
		if(matcher == null && !search(SEARCH_DOWNWARD)) return;
		if(matcher.find(start)){
			if(start == matcher.start() && end == matcher.end()){
				if(clipch.isSelected()) comp.paste();
				else comp.replaceSelection(getText(replcomb));
			}
		}
		search(SEARCH_DOWNWARD);
	}
	/**
	 *パターンに適合する全ての文字列を置換します。
	 *正規表現検索を有効にする必要があります。
	 */
	private void replaceAll(){
		updatePattern(false);
		String repl = (clipch.isSelected())?
		getClipboardText() : getText(replcomb);
		comp.setSelectionStart(offset);
		comp.setSelectionEnd(offset + region.length());
		comp.replaceSelection(matcher.replaceAll(repl));
		addItem(replcomb, getText(replcomb));
	}
	/**
	 *クリップボードから貼り付けを設定します。
	 *@param mode 貼り付ける場合true
	 */
	private void setClipboardPasteMode(boolean mode){
		if(mode){
			if(getClipboardText() == null){
				showMessage(translate("clipboard_empty"));
				clipch.setSelected(mode = false);
			}
		}
		replcomb.setEnabled(!mode);
	}
	/**クリップボードから文字列を取得*/
	private String getClipboardText(){
		try{
			Toolkit kit = Toolkit.getDefaultToolkit();
			Clipboard clip = kit.getSystemClipboard();
			return (String)clip.getData(DataFlavor.stringFlavor);
		}catch(Exception ex){
			return null; // must be null
		}
	}
	/**コンボボックスの値を得る*/
	private String getText(JComboBox combo){
		return (String)combo.getEditor().getItem();
	}
	/**コンボボックスに追加*/
	private void addItem(JComboBox combo, String str){
		if(str==null||str.isEmpty()) return;
		DefaultComboBoxModel model
		= (DefaultComboBoxModel)combo.getModel();
		model.removeElement(str);
		model.insertElementAt(str, 0);
		if(model.getSize()>HISTORY_MAX){
			model.removeElementAt(HISTORY_MAX);
		}
		combo.setSelectedIndex(0);
	}
	/**前方を検索します。*/
	public static final int SEARCH_UPWARD   = 0;
	/**後方を検索します。*/
	public static final int SEARCH_DOWNWARD = 1;
}
