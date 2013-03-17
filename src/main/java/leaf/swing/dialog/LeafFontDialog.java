/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.dialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position.Bias;

import static java.awt.Font.*;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * フォント設定ダイアログをGUIアプリケーション向けに提供します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2008年10月
 *
 */
@SuppressWarnings("serial")
public final class LeafFontDialog extends LeafDialog {
	private final GraphicsEnvironment ge;
	private final Integer[] sizes = {
		8,9,10,11,12,13,14,15,16,18,20,22,24,26,28,36,48,72
	};
	private JLabel namelb, stylelb, sizelb, samplelb;
	private JList<String> namelist;
	private JList<String> stylelist;
	private JList<Integer> sizelist;
	private JScrollPane namescroll, stylescroll, sizescroll;
	private JTextField namefld, stylefld, sizefld;
	private JButton bok, bcancel;
	private Font font = new Font(MONOSPACED, PLAIN, 13);
	private boolean isChanged = CANCEL_OPTION;
	
	/**
	 * 親フレームを指定してモーダルダイアログを生成します。
	 *
	 * @param owner 親フレーム
	 */
	public LeafFontDialog(Frame owner) {
		super(owner, true);
		setResizable(false);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				isChanged = CANCEL_OPTION;
			}
		});
		setLayout(null);
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		initialize();
	}
	/**
	 * 親ダイアログを指定してモーダルダイアログを生成します。
	 *
	 * @param owner 親ダイアログ
	 */
	public LeafFontDialog(Dialog owner) {
		super(owner, true);
		setResizable(false);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				isChanged = CANCEL_OPTION;
			}
		});
		setLayout(null);
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		initialize();
	}
	/**
	 * ダイアログの表示と配置を初期化します。
	 */
	@Override public void initialize() {
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		/*font names*/
		namelb = new JLabel(translate("label_font_name"));
		namefld = new JTextField();
		
		add(namelb);
		add(namefld);
		
		namelist = new JList<>(ge.getAvailableFontFamilyNames());
		namelist.setSelectionMode(SINGLE_SELECTION);
		
		namescroll = new JScrollPane(namelist);
		add(namescroll);
		
		/*font styles*/
		stylelb = new JLabel(translate("label_font_style"));
		stylefld = new JTextField();
		stylefld.setEditable(false);
		
		add(stylelb);
		add(stylefld);
		
		DefaultListModel<String> model = new DefaultListModel<>();
		model.addElement(translate("font_style_plain"));
		model.addElement(translate("font_style_bold"));
		model.addElement(translate("font_style_italic"));
		model.addElement(translate("font_style_bold_italic"));
		
		stylelist = new JList<>(model);
		stylelist.setSelectionMode(SINGLE_SELECTION);
		
		stylescroll = new JScrollPane(stylelist);
		add(stylescroll);
		
		/*font size*/
		sizelb = new JLabel(translate("label_font_size"));
		sizefld = new JTextField();
		sizefld.setEditable(false);
		
		add(sizelb);
		add(sizefld);
		
		sizelist = new JList<>(sizes);
		sizelist.setSelectionMode(SINGLE_SELECTION);
		
		sizescroll = new JScrollPane(sizelist);
		add(sizescroll);
		
		/*font samble*/
		samplelb = new JLabel(translate("label_font_sample"), JLabel.CENTER);
		add(samplelb);
		
		samplelb.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), translate("label_font_sample_title")));
		
		bok = new JButton(translate("button_ok"));
		bcancel = new JButton(translate("button_cancel"));
		
		add(bok);
		add(bcancel);
		
		layoutComponents();
		
		namefld.addKeyListener(new FontNameListener());
		
		namelist.addListSelectionListener(new ListSelectionListener() {
			@Override public void valueChanged(ListSelectionEvent e) {
				String name = (String) namelist.getSelectedValue();
				if(namelist.hasFocus()) namefld.setText(name);
				update(new Font(name, font.getStyle(), font.getSize()));
			}
		});
		
		stylelist.addListSelectionListener(new ListSelectionListener() {
			@Override public void valueChanged(ListSelectionEvent e) {
				int index = stylelist.getSelectedIndex();
				stylefld.setText((String)stylelist.getSelectedValue());
				update(font.deriveFont(index));
			}
		});
		
		sizelist.addListSelectionListener(new ListSelectionListener() {
			@Override public void valueChanged(ListSelectionEvent e) {
				Integer size = (Integer)sizelist.getSelectedValue();
				sizefld.setText(String.valueOf(size));
				update(font.deriveFont(size.floatValue()));
			}
		});
		
		bok.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				isChanged = OK_OPTION;
				dispose();
			}
		});
		
		bcancel.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				isChanged = CANCEL_OPTION;
				dispose();
			}
		});
	}
	
	private void layoutComponents() {
		int pref_tf = namefld.getPreferredSize().height;
		
		namelb .setBounds(10, 5, 220, pref_tf);
		stylelb.setBounds(240, 5, 80, pref_tf);
		sizelb .setBounds(330, 5, 60, pref_tf);
		
		namefld .setBounds(10 , 5 + pref_tf, 220, pref_tf);
		stylefld.setBounds(240, 5 + pref_tf,  80, pref_tf);
		sizefld .setBounds(330, 5 + pref_tf,  60, pref_tf);
		
		namescroll .setBounds(10 , 7 + pref_tf * 2, 220, 110);
		stylescroll.setBounds(240, 7 + pref_tf * 2, 80 , 110);
		sizescroll .setBounds(330, 7 + pref_tf * 2, 60 , 110);
		
		samplelb.setFont(new Font(MONOSPACED, PLAIN, 24));
		int pref_lb = samplelb.getPreferredSize().height;
		samplelb.setBounds(10, 140 + pref_tf * 2, 380, pref_lb);
		
		int pref_b = bok.getPreferredSize().height;
		bok.setBounds(400, 5 + pref_tf, 100, pref_b);
		bcancel.setBounds(400, 8 + pref_tf + pref_b, 100, pref_b);
		
		setContentSize(new Dimension(510, 220 + pref_lb));
	}
	
	private void update(Font font) {
		this.font = font;
		samplelb.setFont(font.deriveFont(24f));
	}
	
	private class FontNameListener extends KeyAdapter {
		@Override public void keyReleased(KeyEvent e) {
			String name = namefld.getText();
			if(font.getName().equals(name)) return;
			int index = namelist.getNextMatch(name, 0, Bias.Forward);
			if(index >= 0) {
				namelist.setSelectedIndex(index);
				namelist.ensureIndexIsVisible(index);
			}
		}
	}
	
	/**
	 * フォント設定画面をモーダルで表示します。
	 *
	 * @param font デフォルトのフォント
	 * @return 選択されたフォント キャンセルされた場合null
	 */
	public Font showDialog(Font font) {
		this.font = font;
		namefld.setText(font.getFamily());
		namelist.setSelectedValue(font.getFamily(), true);
		stylelist.setSelectedIndex(font.getStyle());
		sizelist.setSelectedValue(font.getSize(), true);
		setVisible(true);
		return (isChanged == OK_OPTION)? this.font : null;
	}

}