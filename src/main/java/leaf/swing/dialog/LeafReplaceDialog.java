/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.dialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import leaf.swing.dialog.LeafDialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

/**
 * {@link JTextComponent}の文字列検索・置換ダイアログです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成；2010年5月22日
 *
 */
@SuppressWarnings("serial")
public final class LeafReplaceDialog extends LeafDialog {
	private JButton button_close;
	private JButton button_find_down;
	private JButton button_find_up;
	private JButton button_replace;
	private JButton button_replace_all;
	private JCheckBox ch_case;
	private JCheckBox ch_dotall;
	private JCheckBox ch_paste;
	private JCheckBox ch_regex;
	private JComboBox<String> combo_pattern;
	private JComboBox<String> combo_replace;
	private JLabel label_pattern;
	private JLabel label_replace;
	private JPanel panel_region;
	private JRadioButton radio_region_selection;
	private JRadioButton radio_region_wholetext;
	
	private int offset = 0;
	private String region = "";
	private Matcher matcher;
	private JTextComponent textComp;
	
	private static final int HISTORY_MAX = 30;
	
	/**
	 * 親フレームを指定してモーダレスダイアログを生成します。
	 *
	 * @param owner 親フレーム
	 */
	public LeafReplaceDialog(Frame owner) {
		super(owner, false);
		setContentSize(new Dimension(480, 150));
		setResizable(false);
		setLayout(null);
		initialize();
		
		addComponentListener(new DialogShownListener());
	}
	/**
	 * 親ダイアログを指定してモーダレスダイアログを生成します。
	 *
	 * @param owner 親ダイアログ
	 */
	public LeafReplaceDialog(Dialog owner) {
		super(owner, false);
		setContentSize(new Dimension(480, 150));
		setResizable(false);
		setLayout(null);
		initialize();
		
		addComponentListener(new DialogShownListener());
	}
	
	private class DialogShownListener extends ComponentAdapter {
		@Override
		public void componentShown(ComponentEvent e) {
			if(textComp != null) {
				String selected = textComp.getSelectedText();
				if(selected == null) {
					radio_region_wholetext.setSelected(true);
				} else addItem(combo_pattern, selected);
			}
		}
	}
	
	/**
	 * 操作対象のコンポーネントを指定します。
	 *
	 * @param comp 検索/置換対象のテキストコンポーネント
	 */
	public void setTextComponent(JTextComponent comp) {
		this.textComp = comp;
	}
	
	/**
	 * 操作対象のコンポーネントを返します。
	 * @return 検索/置換対象のテキストコンポーネント
	 */
	public JTextComponent getTextComponent() {
		return textComp;
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
		
		label_replace = new JLabel(translate("label_replace"));
		combo_replace = new JComboBox<>();
		combo_replace.setEditable(true);
		
		add(label_replace);
		add(combo_replace);
		
		ch_paste  = new JCheckBox(translate("check_paste"));
		ch_case   = new JCheckBox(translate("check_case_sensitive"));
		ch_regex  = new JCheckBox(translate("check_regex"));
		ch_dotall = new JCheckBox(translate("check_dotall"));
		
		ch_regex.setSelected(true);
		
		ch_paste.setMnemonic(KeyEvent.VK_P);
		ch_case.setMnemonic(KeyEvent.VK_C);
		ch_regex.setMnemonic(KeyEvent.VK_G);
		ch_dotall.setMnemonic(KeyEvent.VK_O);
		
		add(ch_paste);
		add(ch_case);
		add(ch_regex);
		add(ch_dotall);
		
		panel_region = new JPanel(new GridLayout(2, 1));
		panel_region.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), translate("panel_region")));
		add(panel_region);
		
		radio_region_selection = new JRadioButton(translate("radio_region_selection"));
		radio_region_selection.setMnemonic(KeyEvent.VK_S);
		panel_region.add(radio_region_selection);
		
		radio_region_wholetext = new JRadioButton(translate("radio_region_wholetext"));
		radio_region_wholetext.setMnemonic(KeyEvent.VK_W);
		radio_region_wholetext.setSelected(true);
		panel_region.add(radio_region_wholetext);
		
		ButtonGroup group = new ButtonGroup();
		group.add(radio_region_selection);
		group.add(radio_region_wholetext);
		
		button_find_up     = new JButton(translate("button_find_up"));
		button_find_down   = new JButton(translate("button_find_down"));
		button_replace     = new JButton(translate("button_replace"));
		button_replace_all = new JButton(translate("button_replace_all"));
		button_close       = new JButton(translate("button_close"));
		
		add(button_find_up);
		add(button_find_down);
		add(button_replace);
		add(button_replace_all);
		add(button_close);
		
		button_find_up    .setMnemonic(KeyEvent.VK_U);
		button_find_down  .setMnemonic(KeyEvent.VK_D);
		button_replace    .setMnemonic(KeyEvent.VK_R);
		button_replace_all.setMnemonic(KeyEvent.VK_A);
		button_close      .setMnemonic(KeyEvent.VK_C);
		
		layoutComponents();
		
		combo_pattern.getEditor().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(button_find_down.isEnabled()) search(SEARCH_DOWNWARD);
			}
		});
		
		combo_replace.getEditor().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(button_find_down.isEnabled()) search(SEARCH_DOWNWARD);
			}
		});
		
		ch_paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setClipboardPasteMode(ch_paste.isSelected());
			}
		});
		
		ch_regex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ch_dotall.setEnabled(ch_regex.isSelected());
				button_replace_all.setEnabled(ch_regex.isSelected());
			}
		});
		
		radio_region_selection.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					setRegion(false);
				}
			}
		});
		
		radio_region_wholetext.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					setRegion(true);
				}
			}
		});
		
		button_find_up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search(SEARCH_UPWARD);
			}
		});
		
		button_find_down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search(SEARCH_DOWNWARD);
			}
		});
		
		button_replace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				replaceSelection();
			}
		});
		
		button_replace_all.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				replaceAll();
			}
		});
		
		button_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	private void layoutComponents() {
		int label_y = 10;
		int pref = button_find_up.getPreferredSize().height;
		
		label_pattern.setBounds( 5, label_y, 60 , pref);
		combo_pattern.setBounds(65, label_y, 300, pref);
		
		label_y += pref + 5;
		
		label_replace.setBounds( 5, label_y, 60 , pref);
		combo_replace.setBounds(65, label_y, 300, pref);
		
		int ch_y = label_y + pref + 10;
		int rb_y = radio_region_selection.getPreferredSize().height;
		Insets b = panel_region.getBorder().getBorderInsets(panel_region);
		
		panel_region.setBounds(230, ch_y, 140, 10 + rb_y * 2 + b.top + b.bottom);
		
		ch_y += setBounds(ch_paste , 5, ch_y, 220) + 5;
		ch_y += setBounds(ch_case  , 5, ch_y, 220) + 5;
		ch_y += setBounds(ch_regex , 5, ch_y, 220) + 5;
		ch_y += setBounds(ch_dotall, 5, ch_y, 220) + 5;
		
		int button_y = 10;
		
		button_y += setBounds(button_find_up    , 380, button_y, 115) +  3;
		button_y += setBounds(button_find_down  , 380, button_y, 115) + 10;
		button_y += setBounds(button_replace    , 380, button_y, 115) +  3;
		button_y += setBounds(button_replace_all, 380, button_y, 115) + 20;
		button_y += setBounds(button_close      , 380, button_y, 115) + 10;
		
		setContentSize(new Dimension(500, Math.max(button_y, ch_y) + 10));
	}
	
	private void setRegion(boolean isWholeRegion) {
		if(isWholeRegion) {
			region = textComp.getText();
			offset = 0;
			button_find_up.setEnabled(true);
			button_find_down.setEnabled(true);
			button_replace.setEnabled(true);
		} else {
			region = textComp.getSelectedText();
			if(region == null)  region = "";
			offset = textComp.getSelectionStart();
			button_find_up.setEnabled(false);
			button_find_down.setEnabled(false);
			button_replace.setEnabled(false);
		}
	}
	
	/**
	 * 正規表現パターンを更新します。
	 * 
	 * @param isLiteralMode 正規表現検索を無効にする場合
	 */
	private void updatePattern(boolean isLiteralMode) {
		int opt = 0;
		if(!ch_case.isSelected()) {
			opt |= Pattern.UNICODE_CASE;
			opt |= Pattern.CASE_INSENSITIVE;
		}
		
		if(isLiteralMode) opt |= Pattern.LITERAL;
		if(ch_dotall.isSelected()) opt |= Pattern.DOTALL;
		
		String txt = getText(combo_pattern);
		
		try {
			Pattern p = Pattern.compile(txt, opt);
			this.matcher = p.matcher(this.region);
		}
		
		catch (PatternSyntaxException ex) {	
			showMessage(ex.getDescription());
			matcher = null;
		}
	}
	
	/**
	 * 検索方向を指定して文字列の検索を開始します。
	 * 
	 * @param ward 検索方向
	 * 
	 * {@link #SEARCH_DOWNWARD}
	 * {@link #SEARCH_UPWARD}
	 * 
	 * @return 文字列が見つかった場合その位置 見つからなければ-1
	 */
	public int search (int ward) {
		setRegion(radio_region_wholetext.isSelected());
		String patternText = getText(combo_pattern);
		
		if(patternText.isEmpty()) {
			showMessage(translate("search_pattern_empty"));
			return -1;
		} else {
			addItem(combo_pattern, getText(combo_pattern));
			updatePattern(!ch_regex.isSelected());
			textComp.requestFocusInWindow();
			
			boolean found = false;
			
			if(ward == SEARCH_UPWARD) {
				found = searchUpward(textComp.getSelectionStart());
			} else {
				found = searchDownward(textComp.getSelectionEnd());
			}
			
			return found? textComp.getSelectionStart() : -1;
		}
	}
	
	private boolean searchUpward (int caretPosition) {
		if(matcher == null) return true;
		
		boolean found = false;
		int start = 0, end = 0, mstart, mend;
		
		while(matcher.find(end)) {
			mstart = matcher.start();
			mend   = matcher.end();
			
			if(mend <= caretPosition
			&& textComp.getSelectionStart() != mstart) {
				start = mstart;
				end   = mend;
				found = true;
			}
			
			else if(caretPosition < 0) {
				start = mstart;
				end   = mend;
				found = true;
			}
			
			else break;
		}
		
		if(found) textComp.select(start, end);
		else showMessage(translate("not_found", getText(combo_pattern)));
		
		return found;
	}
	
	private boolean searchDownward(int position) {
		if(matcher == null) return true;
		if(matcher.find(position)) {
			textComp.select(matcher.start(), matcher.end());
			return true;
		} else {
			showMessage(translate("not_found", getText(combo_pattern)));
			return false;
		}
	}
	
	private void replaceSelection() {
		setRegion(radio_region_wholetext.isSelected());
		addItem(combo_replace, getText(combo_replace));
		int start = - offset + textComp.getSelectionStart();
		int end   = - offset + textComp.getSelectionEnd();
		if(matcher == null && search(SEARCH_DOWNWARD) == -1) return;
		if(matcher.find(start)) {
			if(start == matcher.start() && end == matcher.end()) {
				if(ch_paste.isSelected()) textComp.paste();
				else textComp.replaceSelection(getText(combo_replace));
			}
		}
		search(SEARCH_DOWNWARD);
	}
	
	private void replaceAll() {
		setRegion(radio_region_wholetext.isSelected());
		updatePattern(false);
		String repl = (ch_paste.isSelected())?
		getClipboardText() : getText(combo_replace);
		textComp.setSelectionStart(offset);
		textComp.setSelectionEnd(offset + region.length());
		textComp.replaceSelection(matcher.replaceAll(repl));
		addItem(combo_replace, getText(combo_replace));
	}
	
	private void setClipboardPasteMode(boolean mode) {
		if(mode) {
			if(getClipboardText() == null) {
				showMessage(translate("clipboard_empty"));
				ch_paste.setSelected(mode = false);
			}
		}
		combo_replace.setEnabled(!mode);
	}
	
	private String getClipboardText() {
		try{
			Toolkit kit = Toolkit.getDefaultToolkit();
			Clipboard clip = kit.getSystemClipboard();
			return (String)clip.getData(DataFlavor.stringFlavor);
		}catch(Exception ex) {
			return null; // must be null
		}
	}
	
	private int setBounds(JComponent comp, int x, int y, int width) {
		Rectangle bounds = new Rectangle(comp.getPreferredSize());
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		comp.setBounds(bounds);
		return bounds.height;
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
	
	/**前方を検索します。*/
	public static final int SEARCH_UPWARD   = 0;
	/**後方を検索します。*/
	public static final int SEARCH_DOWNWARD = 1;
}
