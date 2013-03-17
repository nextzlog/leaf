/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.thema;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;

import leaf.swing.dialog.LeafAudioDialog;
import leaf.swing.dialog.LeafDialog;

/**
 *タイピング音の設定を行うダイアログです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 */
public class TypingSoundDialog extends LeafDialog {
	private static final long serialVersionUID = 1L;
	private JPanel panel_sound;
	private JCheckBox cb_enable;
	private JButton b_open, b_play, b_ok, b_cancel;
	private JTextField tf_file;
	
	private LeafAudioDialog player;
	private boolean isApproved = false;
	private File file;
	
	private JFileChooser chooser;
	
	public TypingSoundDialog(JFrame frame) {
		super(frame, true);
		setLayout(null);
		setResizable(false);
		
		player = new LeafAudioDialog(this);
		
		initialize();
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
			}
		});
		
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter
		("AIFC/AIFF/AU/SND/WAV", "aifc","aif","aiff","au","snd","wav"));
	}

	@Override
	public void initialize() {
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		cb_enable = new JCheckBox(translate("checkbox_enable"));
		add(cb_enable);
		
		panel_sound = new JPanel();
		add(panel_sound);
		
		panel_sound.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		panel_sound.setLayout(null);
		
		tf_file = new JTextField();
		tf_file.setEditable(false);
		panel_sound.add(tf_file);
		
		b_open   = new JButton(translate("button_open"));
		b_play   = new JButton(translate("button_play"));
		b_ok     = new JButton(translate("button_ok"));
		b_cancel = new JButton(translate("button_cancel"));
		
		panel_sound.add(b_open);		
		panel_sound.add(b_play);
		add(b_ok);
		add(b_cancel);
		
		cb_enable.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e) {
				boolean b = cb_enable.isSelected();
				tf_file.setEnabled(b);
				b_open.setEnabled(b);
				b_play.setEnabled(b);
			}
		});
		
		b_open.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				chooseFile();
			}
		});
		
		b_play.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				player.setVisible(true);
			}
		});
		
		b_ok.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				isApproved = OK_OPTION;
				dispose();
			}
		});
		
		b_cancel.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				isApproved = CANCEL_OPTION;
				dispose();
			}
		});
		
		layoutComponents();
	}
	
	private void layoutComponents() {
		int pref_h = b_open.getPreferredSize().height;
		Insets b = panel_sound.getBorder().getBorderInsets(panel_sound);
		
		cb_enable.setBounds(10 + b.left, 5, 200, pref_h);
		
		int y_comp = b.top + 10;
		tf_file.setBounds(b.left +  10, y_comp, 300, pref_h);
		b_open .setBounds(b.left + 320, y_comp, 120, pref_h);
		
		y_comp += pref_h + 10;
		b_play  .setBounds(b.left + 320,  y_comp, 120, pref_h);
		
		y_comp += pref_h + 10 + b.bottom;
		panel_sound.setBounds(10, 10 + pref_h, 460, y_comp);
		
		y_comp = 20 + pref_h + panel_sound.getHeight();
		b_ok    .setBounds(b.left + 195, y_comp, 120, pref_h);
		b_cancel.setBounds(b.left + 330, y_comp, 120, pref_h);
		
		setContentSize(new Dimension(480, y_comp + pref_h + 10));
	}
	
	private void chooseFile(){
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			setSelectedFile(chooser.getSelectedFile());
		}
	}
	
	/**
	 *指定されたファイルを選択します。
	 *
	 *@param file 音声ファイル
	 */
	public void setSelectedFile(File file){
		boolean b = file != null && file.canRead();
		
		chooser.setSelectedFile(this.file = file);
		
		cb_enable.setSelected(b);
		tf_file.setEnabled(b);
		b_open.setEnabled(b);
		b_play.setEnabled(b);
		
		if(b) tf_file.setText(file.getAbsolutePath());
		else tf_file.setText(translate("file_unavailable"));
		
		try {
			if(file != null) player.load(file);
		} catch (IOException ex) {
			tf_file.setText(translate("file_unavailable"));
		}
	}
	
	/**
	 *選択されたファイルを返します。
	 *
	 *@return 音声ファイル
	 */
	public File getSelectedFile(){
		return cb_enable.isSelected()? file : null;
	}
	
	/**
	 *ダイアログを表示します。
	 *
	 *@return OKボタンで閉じられた場合{@link #OK_OPTION}
	 */
	public boolean showDialog(){
		setVisible(true);
		return isApproved;
	}
}
