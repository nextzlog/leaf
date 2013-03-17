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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

/**
 * {@link JTextComponent}の文字列検索用のダイアログです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年5月22日
 *
 */
@SuppressWarnings("serial")
public final class LeafSearchDialog extends LeafDialog {
	private JButton button_close;
	private JButton button_first;
	private JButton button_next;
	private JCheckBox ch_case;
	private JCheckBox ch_dotall;
	private JCheckBox ch_regex;
	private JComboBox<String> combo_pattern;
	private JLabel label_pattern;
	private JPanel panel_updown;
	private JRadioButton radio_down;
	private JRadioButton radio_up;
	
	private Matcher matcher;
	private JTextComponent textComp;
	
	private static final int HISTORY_MAX = 30;
	
	/**
	 * このダイアログの所有者を指定してモーダレスダイアログを構築します。
	 * 
	 * @param owner このダイアログの親となるFrame
	 */
	public LeafSearchDialog(Frame owner){
		super(owner, false);
		setResizable(false);
		getContentPane().setLayout(null);
		initialize();
		
		addComponentListener(new DialogShownListener());
	}
	
	/**
	 * このダイアログの所有者を指定してモーダレスダイアログを構築します。
	 * 
	 * @param owner このダイアログの親となるDialog
	 */
	public LeafSearchDialog(Dialog owner){
		super(owner, false);
		setResizable(false);
		getContentPane().setLayout(null);
		initialize();
		
		addComponentListener(new DialogShownListener());
	}
	
	
	private class DialogShownListener extends ComponentAdapter{
		public void componentShown(ComponentEvent e){
			if(textComp != null){
				addItem(combo_pattern, textComp.getSelectedText());
			}
		}
	}
	
	/**
	 * このダイアログに関連付けるJTextComponentを指定します。
	 * 
	 * @param comp 検索対象
	 */
	public void setTextComponent(JTextComponent comp){
		final JTextComponent old = this.textComp;
		this.textComp = comp;
		
		firePropertyChange("textComponent", old, comp);
	}
	
	/**
	 * このダイアログに関連付けられたJTextComponentを返します。
	 * 
	 * @return 検索対象
	 */
	public JTextComponent getTextComponent(){
		return textComp;
	}
	
	
	@Override
	public void initialize() {
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		label_pattern = new JLabel(translate("label_pattern"));
		combo_pattern = new JComboBox<>();
		combo_pattern.setEditable(true);
		
		add(label_pattern);
		add(combo_pattern);
		
		ch_case = new JCheckBox(translate("check_case_sensitive"));
		ch_regex = new JCheckBox(translate("check_regex"));
		ch_dotall = new JCheckBox(translate("check_dotall"));
		
		ch_regex.setSelected(true);
		
		ch_case.setMnemonic(KeyEvent.VK_C);
		ch_regex.setMnemonic(KeyEvent.VK_G);
		ch_dotall.setMnemonic(KeyEvent.VK_O);
		
		add(ch_case);
		add(ch_regex);
		add(ch_dotall);
		
		panel_updown = new JPanel(new GridLayout(1, 2));
		panel_updown.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), translate("panel_direction")));
		add(panel_updown);
		
		radio_up = new JRadioButton(translate("radio_direction_up"));
		radio_up.setMnemonic(KeyEvent.VK_U);
		panel_updown.add(radio_up);
		
		radio_down = new JRadioButton(translate("radio_direction_down"));
		radio_down.setMnemonic(KeyEvent.VK_D);
		radio_down.setSelected(true);
		panel_updown.add(radio_down);
		
		ButtonGroup group = new ButtonGroup();
		group.add(radio_up);
		group.add(radio_down);
		
		button_next  = new JButton(translate("button_find_next"));
		button_first = new JButton(translate("button_find_first"));
		button_close = new JButton(translate("button_close"));
		
		add(button_next);
		add(button_first);
		add(button_close);
		
		button_next .setMnemonic(KeyEvent.VK_N);
		button_first.setMnemonic(KeyEvent.VK_F);
		button_close.setMnemonic(KeyEvent.VK_C);
		
		layoutComponents();
		
		combo_pattern.getEditor().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search((radio_down.isSelected())?
					SEARCH_DOWNWARD : SEARCH_UPWARD);
			}
		});
		
		ch_regex.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ch_dotall.setEnabled(ch_regex.isSelected());
			}
		});
		
		button_next.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search((radio_down.isSelected())?
					SEARCH_DOWNWARD : SEARCH_UPWARD);
			}
		});
		
		button_first.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search((radio_down.isSelected())?
					SEARCH_FIRST : SEARCH_LAST);
			}
		});
		
		button_close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
	}
	
	private void layoutComponents(){
		int label_y = 10;
		int pref = button_next.getPreferredSize().height;
		
		label_pattern.setBounds( 5, label_y, 60 , pref);
		combo_pattern.setBounds(65, label_y, 300, pref);
		
		int ch_y = label_y + pref + 10;
		int rb_h = radio_up.getPreferredSize().height;
		Insets b = panel_updown.getBorder().getBorderInsets(panel_updown);
		
		panel_updown.setBounds(200, ch_y, 170, 10 + rb_h + b.top + b.bottom);
		
		ch_y += setBounds(ch_case  , 5, ch_y, 190) + 5;
		ch_y += setBounds(ch_regex , 5, ch_y, 190) + 5;
		ch_y += setBounds(ch_dotall, 5, ch_y, 190) + 5;
		
		int button_y = 10;
		
		button_y += setBounds(button_next , 380, button_y, 115) + 3;
		button_y += setBounds(button_first, 380, button_y, 115) + 23;
		button_y += setBounds(button_close, 380, button_y, 115) + 10;
		
		setContentSize(new Dimension(500, Math.max(button_y, ch_y) + 10));
	}
	
	/**
	 * 正規表現パターンを更新します。
	 * 
	 * @param isLiteralMode 正規表現検索を無効にする場合
	 */
	private void updatePattern(boolean isLiteralMode){
		int opt = 0;
		if(!ch_case.isSelected()){
			opt |= Pattern.UNICODE_CASE;
			opt |= Pattern.CASE_INSENSITIVE;
		}
		
		if(isLiteralMode) opt |= Pattern.LITERAL;
		if(ch_dotall.isSelected()) opt |= Pattern.DOTALL;
		
		String patternText = getText(combo_pattern);
		
		try {
			Pattern pattern = Pattern.compile(patternText, opt);
			matcher = pattern.matcher(textComp.getText());
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
	 * {@link #SEARCH_FIRST}
	 * {@link #SEARCH_LAST}
	 * {@link #SEARCH_UPWARD}
	 * 
	 * @return 文字列が見つかった場合その位置 見つからなければ-1
	 */
	public int search (int ward) {
		String patternText = getText(combo_pattern);
		
		if(patternText.isEmpty()){
			showMessage(translate("search_pattern_empty"));
			return -1;
		}
		else {
			addItem(combo_pattern, getText(combo_pattern));
			updatePattern(!ch_regex.isSelected());
			textComp.requestFocusInWindow();
			
			boolean found = false;
			
			switch(ward){
				case SEARCH_UPWARD:
					found = searchUpward(textComp.getSelectionStart());
					break;
				case SEARCH_DOWNWARD:
					found = searchDownward(textComp.getSelectionEnd());
					break;
				case SEARCH_FIRST: found = searchDownward(0); break;
				case SEARCH_LAST:  found = searchUpward(-1);  break;
			}
			
			return found? textComp.getSelectionStart() : -1;
		}
	}
	
	private boolean searchUpward (int caretPosition){
		if(matcher == null) return true;
		
		boolean found = false;
		int start = 0, end = 0, mstart, mend;
		
		while(matcher.find(end)){
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
	
	private boolean searchDownward(int position){
		if(matcher == null) return true;
		
		if(matcher.find(position)) {
			textComp.select(matcher.start(), matcher.end());
			return true;
		}
		else {
			showMessage(translate("not_found", getText(combo_pattern)));
			return false;
		}
	}
	
	private int setBounds(JComponent comp, int x, int y, int width){
		Rectangle bounds = new Rectangle(comp.getPreferredSize());
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		comp.setBounds(bounds);
		return bounds.height;
	}
	
	private String getText(JComboBox<String> combo){
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
	
	/**前方を検索する動作を指定します*/
	public static final int SEARCH_UPWARD   = 0;
	
	/**後方を検索する動作を指定します*/
	public static final int SEARCH_DOWNWARD = 1;
	
	/**先頭を検索する動作を指定します*/
	public static final int SEARCH_FIRST = 2;
	
	/**末尾を検索する動作を指定します*/
	public static final int SEARCH_LAST  = 3;
}
