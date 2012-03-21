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
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

/**
 *{@link JTextComponent}向けに文字列検索ダイアログを提供します。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成；2010年5月22日 正規表現対応：2010年9月8日
 *@see LeafReplaceDialog
 */
public final class LeafSearchDialog extends LeafDialog{
	private JCheckBox casech, regch, dotch;
	private JComboBox srchcomb;
	private JLabel srchlb;
	private JRadioButton uprb, downrb;
	private JButton bnext, bfirst, bclose;
	private Matcher matcher;
	private JTextComponent comp;
	private final int HISTORY_MAX = 30;
	
	/**
	 *親フレームを指定してモーダレスダイアログを生成します。
	 *@param owner 親フレーム
	 */
	public LeafSearchDialog(Frame owner){
		super(owner, false);
		setContentSize(new Dimension(480, 100));
		setResizable(false);
		setLayout(null);
		init();
		
		addComponentListener(new ExComponentListener());
	}
	/**
	 *親ダイアログを指定してモーダレスダイアログを生成します。
	 *@param owner 親ダイアログ
	 */
	public LeafSearchDialog(Dialog owner){
		super(owner, false);
		setContentSize(new Dimension(480, 100));
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
				addItem(srchcomb, comp.getSelectedText());
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
				search((downrb.isSelected())?
					SEARCH_DOWNWARD : SEARCH_UPWARD);
			}
		});
		
		/*case sensitive*/
		casech = new JCheckBox(translate("check_case_sensitive"));
		casech.setBounds(5, 40, 190, 20);
		casech.setMnemonic(KeyEvent.VK_C);
		add(casech);
		
		/*regex search*/
		regch = new JCheckBox(translate("check_regex"));
		regch.setBounds(5, 60, 190, 20);
		regch.setMnemonic(KeyEvent.VK_G);
		regch.setSelected(true);
		add(regch);
		
		regch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dotch.setEnabled(regch.isSelected());
			}
		});
		
		/*dotall*/
		dotch = new JCheckBox(translate("check_dotall"));
		dotch.setBounds(5, 80, 190, 20);
		dotch.setMnemonic(KeyEvent.VK_O);
		add(dotch);
		
		/*search direction*/
		JPanel panel = new JPanel(null);
		panel.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), translate("panel_direction")));
		panel.setBounds(195, 45, 170, 50);
		add(panel);
		ButtonGroup group = new ButtonGroup();
		
		uprb = new JRadioButton(translate("radio_direction_up"));
		uprb.setBounds(5, 20, 80, 20);
		uprb.setMnemonic(KeyEvent.VK_U);
		panel.add(uprb);
		group.add(uprb);
		
		downrb = new JRadioButton(translate("radio_direction_down"));
		downrb.setBounds(85, 20, 80, 20);
		downrb.setMnemonic(KeyEvent.VK_D);
		downrb.setSelected(true);
		panel.add(downrb);
		group.add(downrb);
		
		/*button : search next*/
		bnext = new JButton(translate("button_find_next"));
		bnext.setBounds(380, 10, 100, 22);
		bnext.setMnemonic(KeyEvent.VK_N);
		add(bnext);
		
		bnext.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search((downrb.isSelected())?
					SEARCH_DOWNWARD : SEARCH_UPWARD);
			}
		});
		
		/*button : search first*/
		bfirst = new JButton(translate("button_find_first"));
		bfirst.setBounds(380, 35, 100, 22);
		bfirst.setMnemonic(KeyEvent.VK_F);
		add(bfirst);
		
		bfirst.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search((downrb.isSelected())?
					SEARCH_FIRST : SEARCH_LAST);
			}
		});
		
		/*button : close dialog*/
		bclose = new JButton(translate("button_close"));
		bclose.setBounds(380, 78, 100, 22);
		bclose.setMnemonic(KeyEvent.VK_C);
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
			Pattern pat = Pattern.compile(getText(srchcomb), opt);
			matcher = pat.matcher(comp.getText());
		}catch(PatternSyntaxException ex){
			showMessage(ex.getDescription());
			matcher = null;
		}
	}
	/**
	 *検索方向を指定して検索を開始します。
	 *@param ward 検索方向
	 *@return 見つかった場合true
	 */
	public boolean search(int ward){
		String pat = getText(srchcomb);
		if(pat.isEmpty()){
			showMessage(translate("search_pattern_empty"));
			return false;
		}else{
			addItem(srchcomb, getText(srchcomb));
			updatePattern(!regch.isSelected());
			comp.requestFocusInWindow();
			switch(ward){
			case SEARCH_UPWARD:
				return searchUpward(comp.getSelectionStart());
			case SEARCH_DOWNWARD:
				return searchDownward(comp.getSelectionEnd());
			case SEARCH_FIRST:
				return searchDownward(0);
			case SEARCH_LAST:
				return searchUpward(-1);
			}
			return false;
		}
	}
	/**
	 *前方を検索します。
	 *@param position 検索開始位置 負の場合末尾検索
	 *@return 見つかった場合true
	 */
	private boolean searchUpward(int position){
		if(matcher == null) return true;
		boolean found = false;
		int start = 0, end = 0;
		while(matcher.find(end)){
			if(matcher.end() <= position
			&& comp.getSelectionStart() != matcher.start()){
				start = matcher.start();
				end = matcher.end();
				found = true;
			}else if(position < 0){
				start = matcher.start();
				end   = matcher.end();
				found = true;
			}else break;
		}
		if(found) comp.select(start, end);
		else{
			showMessage(translate("not_found", getText(srchcomb)));
		}
		return found;
	}
	/**
	 *後方を検索します。
	 *@param position 検索開始位置
	 *@return 見つかった場合true
	 */
	private boolean searchDownward(int position){
		if(matcher == null) return true;
		if(matcher.find(position)){
			comp.select(matcher.start(), matcher.end());
			return true;
		}else{
			showMessage(translate("not_found", getText(srchcomb)));
			return false;
		}
	}
	/**コンボボックスの値を得る*/
	private String getText(JComboBox combo){
		return (String)combo.getEditor().getItem();
	}
	/**コンボボックスに追加*/
	private void addItem(JComboBox combo, String str){
		if(str==null||str.isEmpty()) return;
		DefaultComboBoxModel model =
		(DefaultComboBoxModel)combo.getModel();
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
	/*+先頭を検索します。*/
	public static final int SEARCH_FIRST = 2;
	/**末尾を検索します。*/
	public static final int SEARCH_LAST  = 3;
}