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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position.Bias;

import leaf.swing.text.LeafTextField;

import static java.awt.Font.*;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 *フォント設定ダイアログをアプリケーション向けに提供します。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2008年10月
 */
public final class LeafFontDialog extends LeafDialog{
	private final GraphicsEnvironment ge;
	private final Integer[] sizes = {
		8,9,10,11,12,13,14,15,16,18,20,22,24,26,28,36,48,72
	};
	private JLabel namelb, stylelb, sizelb, samplelb;
	private JList namelist, stylelist, sizelist;
	private LeafTextField namefld, stylefld, sizefld, pitchfld;
	private JButton bok, bcancel;
	private Font font = new Font(MONOSPACED, PLAIN, 13);
	private boolean isChanged = CANCEL_OPTION;
	
	/**
	 *親フレームを指定してモーダルダイアログを生成します。
	 *@param owner 親フレーム
	 */
	public LeafFontDialog(Frame owner){
		super(owner, true);
		setContentSize(new Dimension(500, 260));
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
			}
		});
		setLayout(null);
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		init();
	}
	/**
	 *親ダイアログを指定してモーダルダイアログを生成します。
	 *@param owner 親ダイアログ
	 */
	public LeafFontDialog(Dialog owner){
		super(owner, true);
		setContentSize(new Dimension(500, 260));
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
			}
		});
		setLayout(null);
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		init();
	}
	/**
	 *ダイアログの表示と配置を初期化します。
	 */
	@Override public void init(){
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		/*font names*/
		namelb = new JLabel(translate("label_font_name"));
		namelb.setBounds(10, 5, 220, 20);
		add(namelb);
		
		namefld = new LeafTextField();
		namefld.setBounds(10, 25, 220, 20);
		add(namefld);
		
		namelist = new JList(ge.getAvailableFontFamilyNames());
		namelist.setSelectionMode(SINGLE_SELECTION);
		
		JScrollPane scroll = new JScrollPane(namelist);
		scroll.setBounds(10, 47, 220, 110);
		add(scroll);
		
		namelist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				String name = (String) namelist.getSelectedValue();
				if(namelist.hasFocus()) namefld.setText(name);
				update(new Font(name, font.getStyle(), font.getSize()));
			}
		});
		
		namefld.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				String name = namefld.getText();
				if(font.getName().equals(name)) return;
				int index = namelist.getNextMatch(name, 0, Bias.Forward);
				if(index >= 0){
					namelist.setSelectedIndex(index);
					namelist.ensureIndexIsVisible(index);
				}
			}
		});
		
		/*font styles*/
		stylelb = new JLabel(translate("label_font_style"));
		stylelb.setBounds(240, 5, 80, 20);
		add(stylelb);
		
		stylefld = new LeafTextField();
		stylefld.setBounds(240, 25, 80, 20);
		stylefld.setEditable(false);
		add(stylefld);
		
		DefaultListModel model = new DefaultListModel();
		model.addElement(translate("font_style_plain"));
		model.addElement(translate("font_style_bold"));
		model.addElement(translate("font_style_italic"));
		model.addElement(translate("font_style_bold_italic"));
		
		stylelist = new JList(model);
		stylelist.setSelectionMode(SINGLE_SELECTION);
		
		scroll = new JScrollPane(stylelist);
		scroll.setBounds(240, 47, 80, 110);
		add(scroll);
		
		stylelist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				int index = stylelist.getSelectedIndex();
				stylefld.setText((String)stylelist.getSelectedValue());
				update(font.deriveFont(index));
			}
		});
		
		/*font size*/
		sizelb = new JLabel(translate("label_font_size"));
		sizelb.setBounds(330, 5, 60, 20);
		add(sizelb);
		
		sizefld = new LeafTextField();
		sizefld.setBounds(330, 25, 60, 20);
		sizefld.setEditable(false);
		add(sizefld);
		
		sizelist = new JList(sizes);
		sizelist.setSelectionMode(SINGLE_SELECTION);
		
		scroll = new JScrollPane(sizelist);
		scroll.setBounds(330, 47, 60, 88);
		add(scroll);
		
		sizelist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				Integer size = (Integer)sizelist.getSelectedValue();
				sizefld.setText(String.valueOf(size));
				update(font.deriveFont(size.floatValue()));
			}
		});
		
		/*monospace or not*/
		pitchfld = new LeafTextField();
		pitchfld.setBounds(330, 137, 60, 20);
		pitchfld.setEditable(false);
		add(pitchfld);
		
		/*font samble*/
		samplelb = new JLabel(translate("label_font_sample"), JLabel.CENTER);
		samplelb.setBounds(10, 180, 380, 60);
		add(samplelb);
		
		samplelb.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), translate("label_font_sample_title")));
		
		/*ok button*/
		bok = new JButton(translate("button_ok"));
		bok.setBounds(400, 25, 100, 22);
		add(bok);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = OK_OPTION;
				dispose();
			}
		});
		
		/*cancel button*/
		bcancel = new JButton(translate("button_cancel"));
		bcancel.setBounds(400, 49, 100, 22);
		add(bcancel);
		
		bcancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = CANCEL_OPTION;
				dispose();
			}
		});
	}
	/**
	*フォントの変更時に表示を更新します。
	*@param font 新しいフォント
	*/
	private void update(Font font){
		this.font = font;
		samplelb.setFont(font.deriveFont(24f));
		FontMetrics met = samplelb.getFontMetrics(font);
		if(met.charWidth('m') == met.charWidth('i')){
			pitchfld.setText(translate("pitch_fixed"));
		}else{
			pitchfld.setText(translate("pitch_unfixed"));
		}
	}
	/**
	*フォント設定画面をモーダルで表示します。
	*@param font デフォルトのフォント
	*@return 選択されたフォント キャンセルされた場合null
	*/
	public Font showDialog(Font font){
		this.font = font;
		namefld.setText(font.getFamily());
		namelist.setSelectedValue(font.getFamily(), true);
		stylelist.setSelectedIndex(font.getStyle());
		sizelist.setSelectedValue(font.getSize(), true);
		setVisible(true);
		return (isChanged == OK_OPTION)? this.font : null;
	}
}