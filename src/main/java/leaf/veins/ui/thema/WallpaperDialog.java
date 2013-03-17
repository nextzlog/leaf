/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.thema;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JSpinner.NumberEditor;

import leaf.swing.dialog.LeafDialog;
import leaf.veins.ui.com.PhotoCropper;

/**
 * 壁紙を設定するダイアログです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 */
public final class WallpaperDialog extends LeafDialog {
	private static final long serialVersionUID = 1L;
	private JFileChooser chooser;
	private JTextField text_file;
	private PhotoCropper cropper;
	private JPanel panel_photo;
	private JPanel border_trim;
	private JButton button_open;
	private JButton button_color;
	private JButton button_ok;
	private JButton button_cancel;
	private JSpinner spin_trans;
	private JCheckBox check_enable;
	private JLabel label_color;
	private JLabel label_trans;
	
	private boolean isApproved = CANCEL_OPTION;
	private WallpaperSettings settings;
	
	/**
	 * 所有者となるウィンドウを指定してダイアログを構築します。
	 * 
	 * @param owner 所有者となるウィンドウ
	 */
	public WallpaperDialog(Window owner) {
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);
		
		setLayout(null);
		setResizable(false);
		setLocationRelativeTo(owner);
		
		initialize();
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				isApproved = CANCEL_OPTION;
			}
		});
		
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		"JPEG / PNG / GIF", "jpg", "jpeg", "png", "gif"));
	}
	
	@Override
	public void initialize() {
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		check_enable = new JCheckBox(new CheckEnableAction());
		add(check_enable);
		
		panel_photo = new JPanel();
		panel_photo.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		panel_photo.setLayout(null);
		add(panel_photo);
		
		text_file = new JTextField(translate("file_unavailable"));
		text_file.setEditable(false);
		panel_photo.add(text_file);
		
		border_trim = new JPanel();
		border_trim.setBorder(new EtchedBorder());
		border_trim.setLayout(new BorderLayout());
		panel_photo.add(border_trim);
		
		cropper = new PhotoCropper();
		cropper.setToolTipText(translate("cropper"));
		border_trim.add(cropper, BorderLayout.CENTER);
		
		button_open = new JButton(new OpenFileAction());
		button_open.setEnabled(false);
		panel_photo.add(button_open);
		
		label_color = new JLabel(translate("label_color"), JLabel.CENTER);
		label_color.setOpaque(true);
		panel_photo.add(label_color);
		
		button_color = new JButton(new SelectColorAction());
		panel_photo.add(button_color);
		
		label_trans = new JLabel(translate("label_trans", JLabel.CENTER));
		panel_photo.add(label_trans);
		
		SpinnerModel model_trans = new SpinnerNumberModel(50, 0, 100, 5);
		spin_trans = new JSpinner(model_trans);
		panel_photo.add(spin_trans);
		
		button_ok = new JButton(new OkAction());
		add(button_ok);
		
		button_cancel = new JButton(new CancelAction());
		add(button_cancel);
		
		NumberEditor ed = (NumberEditor) spin_trans.getEditor();
		ed.getTextField().setEditable(false);
		
		layoutComponents();
	}
	
	private void layoutComponents() {
		int pref_h = button_open.getPreferredSize().height;
		Insets b = panel_photo.getBorder().getBorderInsets(panel_photo);
		
		check_enable.setBounds(10 + b.left, 5, 200, pref_h);
		
		int y_comp = b.top + 10;
		
		text_file    .setBounds(b.left +  10, y_comp, 300, pref_h);
		button_open  .setBounds(b.left + 320, y_comp, 120, pref_h);
		
		y_comp += pref_h + 10;
		border_trim  .setBounds(b.left +  10, y_comp, 300, 225);
		label_color  .setBounds(b.left + 320, y_comp, 120, pref_h);
		
		y_comp += pref_h + 10;
		button_color .setBounds(b.left + 320, y_comp, 120, pref_h);
		
		y_comp = border_trim.getY() + border_trim.getHeight() - pref_h;
		spin_trans   .setBounds(b.left + 320, y_comp, 120, pref_h);
		
		y_comp -= pref_h - 10;
		label_trans  .setBounds(b.left + 320, y_comp, 120, pref_h);
		
		y_comp += pref_h * 2 + 20 + b.bottom;
		panel_photo.setBounds(10, 10 + pref_h, 460, y_comp);
		
		y_comp = 20 + pref_h + panel_photo.getHeight();
		button_ok    .setBounds(b.left + 195, y_comp, 120, pref_h);
		button_cancel.setBounds(b.left + 330, y_comp, 120, pref_h);
		
		setContentSize(new Dimension(480, y_comp + pref_h + 10));
	}
	
	private class CheckEnableAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public CheckEnableAction() {
			super(translate("check_enable"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setButtonsEnabled(check_enable.isSelected());
		}
	}
	
	private class OpenFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public OpenFileAction() {
			super(translate("button_open"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			chooser.setSelectedFile(settings.getPhotoFile());
			int ret = chooser.showOpenDialog(getContentPane());
			if(ret == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				settings.setPhotoFilePath(file.getPath());
				openPhotoFile(file);
			}
		}
	}
	
	private class SelectColorAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public SelectColorAction() {
			super(translate("button_color"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Color color = JColorChooser.showDialog(
				getContentPane(), getTitle(),
				settings.getBackgroundColor());
			if(color != null) {
				settings.setBackgroundColor(color);
				label_color.setBackground(color);
			}
		}
	}
	
	private class OkAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public OkAction() {
			super(translate("button_ok"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			isApproved = OK_OPTION;
			dispose();
		}
	}
	
	private class CancelAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public CancelAction() {
			super(translate("button_cancel"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			isApproved = CANCEL_OPTION;
			dispose();
		}
	}
	
	private void setButtonsEnabled(boolean enabled) {
		text_file.setEnabled(enabled);
		button_open.setEnabled(enabled);
		cropper.setVisible(enabled);
		spin_trans.setEnabled(enabled);
	}

	private void openPhotoFile(File file) {
		chooser.setSelectedFile(file);
		if(file != null) try {
			cropper.setImage(ImageIO.read(file));
			text_file.setText(file.getPath());
		} catch (IOException ex) {
			settings.setPhotoFilePath(null);
			text_file.setText(translate("file_unavailable"));
		}
		
		check_enable.setSelected(file != null);
		setButtonsEnabled(file != null);
	}
	
	/**
	 * 設定ダイアログを表示します。
	 * 
	 * @param ws 既存の設定
	 * @return ダイアログがOKボタンで閉じられた場合true
	 */
	public boolean showDialog(WallpaperSettings ws) {
		settings = (WallpaperSettings) ws.clone();
		label_color.setBackground(ws.getBackgroundColor());
		openPhotoFile(ws.getPhotoFile());
		cropper.setTrimRect(ws.getPhotoTrimRect());
		
		int trans = 100 - (int)(ws.getPhotoAlpha() * 100);
		spin_trans.setValue(trans);
		setVisible(true);
		
		if(isApproved != OK_OPTION) return CANCEL_OPTION;
		
		trans = (Integer) spin_trans.getValue();
		settings.setPhotoAlpha((100f - trans) / 100);
		settings.setPhotoTrimRect(cropper.getTrimRect());
		if(!check_enable.isSelected())
			settings.setPhotoFilePath(null);
		
		return OK_OPTION;
	}
	
	/**
	 * 壁紙の設定内容を返します。
	 * 
	 * @return 設定
	 */
	public WallpaperSettings getSettings() {
		return settings;
	}

}
