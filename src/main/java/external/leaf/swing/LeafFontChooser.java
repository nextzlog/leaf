/**********************************************************************************
 * leaf - Java Library for pafelog
 * Copyright(c) 2010 - 2013 by University of Tokyo Amateur Radio Club
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL) as published by the
 * Free Software Foundation, either version 3 of the License, or(at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************/
package leaf.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position.Bias;

import leaf.util.Localizer;

import static java.awt.Font.MONOSPACED;
import static java.awt.Font.PLAIN;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * フォント設定ダイアログです。
 *
 * @author 東大アマチュア無線クラブ
 * @since 2008年10月
 */
public final class LeafFontChooser extends JDialog {
	private static final long serialVersionUID = 1L;
	private final Localizer localizer;

	private final GraphicsEnvironment ge;
	private final Integer[] sizes = {8, 9, 10, 11, 12, 13, 14, 15, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};

	private JLabel namelb, stylelb, sizelb, samplelb;
	private JList<String> namelist;
	private JList<String> stylelist;
	private JList<Integer> sizelist;
	private JScrollPane namescroll, stylescroll, sizescroll;
	private JTextField namefld, stylefld, sizefld;
	private JButton bok, bcancel;
	private Font font = new Font(MONOSPACED, PLAIN, 13);
	private boolean isChanged = false;

	/**
	 * 親フレームを指定してモーダルダイアログを生成します。
	 *
	 * @param owner 親フレーム
	 */
	public LeafFontChooser(Frame owner) {
		super(owner, true);
		setResizable(false);

		localizer = Localizer.getInstance(getClass());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				isChanged = false;
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
	public LeafFontChooser(Dialog owner) {
		super(owner, true);
		setResizable(false);

		localizer = Localizer.getInstance(getClass());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				isChanged = false;
			}
		});
		setLayout(null);
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		initialize();
	}

	private void initialize() {
		setTitle(localizer.translate("title"));
		getContentPane().removeAll();

		/*font names*/
		namelb = new JLabel(localizer.translate("label_font_name"));
		namefld = new JTextField();

		add(namelb);
		add(namefld);

		namelist = new JList<>(ge.getAvailableFontFamilyNames());
		namelist.setSelectionMode(SINGLE_SELECTION);

		namescroll = new JScrollPane(namelist);
		add(namescroll);

		/*font styles*/
		stylelb = new JLabel(localizer.translate("label_font_style"));
		stylefld = new JTextField();
		stylefld.setEditable(false);

		add(stylelb);
		add(stylefld);

		DefaultListModel<String> model = new DefaultListModel<>();
		model.addElement(localizer.translate("font_style_plain"));
		model.addElement(localizer.translate("font_style_bold"));
		model.addElement(localizer.translate("font_style_italic"));
		model.addElement(localizer.translate("font_style_bold_italic"));

		stylelist = new JList<>(model);
		stylelist.setSelectionMode(SINGLE_SELECTION);

		stylescroll = new JScrollPane(stylelist);
		add(stylescroll);

		/*font size*/
		sizelb = new JLabel(localizer.translate("label_font_size"));
		sizefld = new JTextField();
		sizefld.setEditable(false);

		add(sizelb);
		add(sizefld);

		sizelist = new JList<>(sizes);
		sizelist.setSelectionMode(SINGLE_SELECTION);

		sizescroll = new JScrollPane(sizelist);
		add(sizescroll);

		/*font sample*/
		samplelb = new JLabel(localizer.translate("label_font_sample"), JLabel.CENTER);
		add(samplelb);

		samplelb.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED), localizer.translate("label_font_sample_title")));

		bok = new JButton(localizer.translate("button_ok"));
		bcancel = new JButton(localizer.translate("button_cancel"));

		add(bok);
		add(bcancel);

		layoutComponents();

		namefld.addKeyListener(new FontNameListener());

		namelist.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String name = namelist.getSelectedValue();
				if (namelist.hasFocus()) namefld.setText(name);
				update(new Font(name, font.getStyle(), font.getSize()));
			}
		});

		stylelist.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = stylelist.getSelectedIndex();
				stylefld.setText(stylelist.getSelectedValue());
				update(font.deriveFont(index));
			}
		});

		sizelist.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Integer size = sizelist.getSelectedValue();
				sizefld.setText(String.valueOf(size));
				update(font.deriveFont(size.floatValue()));
			}
		});

		bok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isChanged = true;
				dispose();
			}
		});

		bcancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isChanged = false;
				dispose();
			}
		});
	}

	private void layoutComponents() {
		int pref_tf = namefld.getPreferredSize().height;

		namelb.setBounds(10, 5, 220, pref_tf);
		stylelb.setBounds(240, 5, 80, pref_tf);
		sizelb.setBounds(330, 5, 60, pref_tf);

		namefld.setBounds(10, 5 + pref_tf, 220, pref_tf);
		stylefld.setBounds(240, 5 + pref_tf, 80, pref_tf);
		sizefld.setBounds(330, 5 + pref_tf, 60, pref_tf);

		namescroll.setBounds(10, 7 + pref_tf * 2, 220, 110);
		stylescroll.setBounds(240, 7 + pref_tf * 2, 80, 110);
		sizescroll.setBounds(330, 7 + pref_tf * 2, 60, 110);

		samplelb.setFont(new Font(MONOSPACED, PLAIN, 24));
		int pref_lb = samplelb.getPreferredSize().height;
		samplelb.setBounds(10, 140 + pref_tf * 2, 380, pref_lb);

		int pref_b = bok.getPreferredSize().height;
		bok.setBounds(400, 5 + pref_tf, 100, pref_b);
		bcancel.setBounds(400, 8 + pref_tf + pref_b, 100, pref_b);

		setSize(new Dimension(510, 220 + pref_lb));
	}

	private void update(Font font) {
		this.font = font;
		samplelb.setFont(font.deriveFont(24f));
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
		return (isChanged == true) ? this.font : null;
	}

	private class FontNameListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			String name = namefld.getText();
			if (font.getName().equals(name)) return;
			int index = namelist.getNextMatch(name, 0, Bias.Forward);
			if (index >= 0) {
				namelist.setSelectedIndex(index);
				namelist.ensureIndexIsVisible(index);
			}
		}
	}

}
