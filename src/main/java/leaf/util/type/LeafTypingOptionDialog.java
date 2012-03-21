/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.type;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import leaf.dialog.LeafDialog;
import leaf.media.LeafAudioDialog;
import leaf.swing.text.LeafTextField;

/**
 *{@link LeafTypingSoundPlayer}の設定を行うためのダイアログです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年10月31日
 */
public final class LeafTypingOptionDialog extends LeafDialog{
	private JButton bplay, bopen, bok, bcancel;
	private JCheckBox enablech;
	private JPanel borderpanel;
	private LeafTextField pathfld;
	private final JFileChooser chooser;
	private final LeafAudioDialog player;
	private boolean isApproved = CANCEL_OPTION;
	private File file;
	
	/**
	 *親フレームを指定してモーダルダイアログを生成します。
	 *@param owner ダイアログの親フレーム
	 */
	public LeafTypingOptionDialog(Frame owner){
		super(owner, true);
		setContentSize(new Dimension(350, 130));
		setResizable(false);
		setLayout(null);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
			}
		});
		
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		"AIFC/AIFF/AU/SND/WAV","aifc","aif","aiff","au","snd","wav"));
		
		player = new LeafAudioDialog(this);
		init();
	}
	/**
	 *親ダイアログを指定してモーダルダイアログを生成します。
	 *@param owner ダイアログの親ダイアログ
	 */
	public LeafTypingOptionDialog(Dialog owner){
		super(owner, true);
		setContentSize(new Dimension(350, 130));
		setResizable(false);
		setLayout(null);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isApproved = CANCEL_OPTION;
			}
		});
		
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		"AIFC/AIFF/AU/SND/WAV","aifc","aif","aiff","au","snd","wav"));
		
		player = new LeafAudioDialog(this);
		init();
	}
	/**
	 *ダイアログの表示と配置を初期化します。
	 */
	@Override public void init(){
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		/*enable typing sound*/
		enablech = new JCheckBox(translate("checkbox_enable_sound"));
		enablech.setBounds(5, 5, 200, 20);
		add(enablech);
		
		enablech.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				enableButtons(enablech.isSelected());
			}
		});
		
		/*button : show player dialog*/
		bplay = new JButton(translate("button_show_player"));
		bplay.setBounds(230, 7, 100, 20);
		add(bplay);
		
		bplay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				player.setVisible(true);
			}
		});
		
		/*border*/
		borderpanel = new JPanel();
		borderpanel.setLayout(null);
		borderpanel.setBounds(0, 30, 350, 60);
		borderpanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		add(borderpanel);
		
		/*file path*/
		pathfld = new LeafTextField();
		pathfld.setBounds(10, 25, 200, 20);
		pathfld.setEditable(false);
		borderpanel.add(pathfld);
		
		/*button : open file*/
		bopen = new JButton(translate("button_open"));
		bopen.setBounds(230, 25, 100, 20);
		borderpanel.add(bopen);
		
		bopen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				chooser.setSelectedFile(file);
				if(chooser.showOpenDialog(LeafTypingOptionDialog.this)
				== JFileChooser.APPROVE_OPTION){
					file = chooser.getSelectedFile();
					update();
				}
			}
		});
		
		/*button : ok*/
		bok = new JButton(translate("button_ok"));
		bok.setBounds(120, 100, 100, 20);
		add(bok);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isApproved = OK_OPTION;
				dispose();
			}
		});
		
		/*button : cancel*/
		bcancel = new JButton(translate("button_cancel"));
		bcancel.setBounds(230, 100, 100, 20);
		add(bcancel);
		
		bcancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isApproved = CANCEL_OPTION;
				dispose();
			}
		});
		update();
	}
	/**
	 *ボタン等を使用可能/不可能に設定します。
	 *@param b 使用可能にする場合true
	 */
	private void enableButtons(boolean b){
		pathfld.setEnabled(b);
		bopen.setEnabled(b);
	}
	/**
	 *新しいファイルに対して表示を更新します。
	 */
	private void update(){
		boolean b = (file != null) && file.canRead();
		enablech.setSelected(b);
		enableButtons(b);
		if(b) pathfld.setText(file.getAbsolutePath());
		else pathfld.setText(translate("textfield_unavailable_file"));
		try{
			if(b) player.load(file);
		}catch(IOException ex){
			pathfld.setText(translate("textfield_unavailable_file"));
		}
	}
	/**
	 *ダイアログを表示します。
	 *@return OKボタンで閉じられた場合{@link #OK_OPTION}を返す
	 */
	public boolean showDialog(){
		setVisible(true);
		return isApproved;
	}
	/**
	 *指定されたファイルを選択します。
	 *@param file 音声ファイル 無効にする場合null
	 */
	public void setSelectedFile(File file){
		this.file = file;
		update();
	}
	/**
	 *選択されたファイルを返します。
	 *@return 音声ファイル 無効にする場合null
	 */
	public File getSelectedFile(){
		return (enablech.isSelected())? file : null;
	}
}