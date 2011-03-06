/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.type;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import leaf.components.text.LeafTextField;
import leaf.dialog.LeafDialog;
import leaf.manager.LeafLangManager;
import leaf.media.LeafAudioDialog;

/**
*タイプ音の設定を行うためのモーダルダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年10月31日
*@see LeafTypingSoundManager
*/
public final class LeafTypingOptionDialog extends LeafDialog{
	
	/**秘匿フィールド*/
	private boolean change = CANCEL_OPTION;
	private File file;
	
	/**GUI*/
	private JPanel borderpanel;
	private JCheckBox ch1;
	private LeafTextField tfpath;
	private JButton bplay,bopen,bok,bcancel;
	private final JFileChooser chooser;
	private final LeafAudioDialog dialog;

	/**
	*親フレームを指定して設定ダイアログを生成します。
	*@param owner ダイアログの親フレーム
	*/
	public LeafTypingOptionDialog(Frame owner){
		this(owner, null);
	}
	/**
	*親フレームと、デフォルトの音声ファイルを指定して設定ダイアログを生成します。
	*@param owner ダイアログの親フレーム
	*@param file デフォルトの音声ファイル
	*/
	public LeafTypingOptionDialog(Frame owner, File file){
		
		super(owner,null,true);
		getContentPane().setPreferredSize(new Dimension(350,130));
		pack();
		setResizable(false);
		setLayout(null);
		this.file = file;
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				change = CANCEL_OPTION;
				dialog.dispose();
			}
		});
		
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
			"AIFC/AIFF/AU/SND/WAV","aifc","aif","aiff","au","snd","wav"
		));
		
		dialog = new LeafAudioDialog(this, file);
		
		init();
	}
	/**
	*ダイアログを初期化します。
	*/
	public void init(){
		
		setTitle(LeafLangManager.get("Typing Sound Options","タイプ音の設定"));
		getContentPane().removeAll();
		
		/*チェックボックス*/
		ch1 = new JCheckBox(LeafLangManager.get(
			"Enable Typing Sound","タイプ音を有効にする"
		));
		ch1.setBounds(5,5,200,20);
		add(ch1);
		
		ch1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				enableButtons(ch1.isSelected());
			}
		});
		
		/*プレーヤ*/
		bplay = new JButton(LeafLangManager.get(
			"Player","プレーヤ"
		));
		bplay.setBounds(230,7,100,20);
		add(bplay);
		
		bplay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.setVisible(true);
			}
		});
		
		/*枠線*/
		borderpanel = new JPanel();
		borderpanel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			LeafLangManager.get("Typing Sound","タイプ音")
		));
		borderpanel.setBounds(0,30,350,60);
		borderpanel.setLayout(null);
		add(borderpanel);
		
		/*パス表示フィールド*/
		tfpath = new LeafTextField(100);
		tfpath.setBounds(10,25,200,20);
		tfpath.setEditable(false);
		borderpanel.add(tfpath);
		
		/*参照ボタン*/
		bopen = new JButton(LeafLangManager.get("Open","参照"));
		bopen.setBounds(230,25,80,20);
		borderpanel.add(bopen);
		
		bopen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openFile();
			}
		});
		
		/*OK/CNCELボタン*/
		bok = new JButton("OK");
		bok.setBounds(120,100,100,20);
		add(bok);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				change = OK_OPTION;
				dispose();
			}
		});
		
		bcancel = new JButton(LeafLangManager.get("Cancel","キャンセル"));
		bcancel.setBounds(230,100,100,20);
		add(bcancel);
		
		bcancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				change = CANCEL_OPTION;
				dispose();
			}
		});
		
		/*初期設定*/
		update();
	}
	/**ファイル参照*/
	private void openFile(){
		chooser.setSelectedFile(file);
		if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
			update();
		}
	}
	/**ボタン等を使用可能/不可能にする*/
	private void enableButtons(boolean opt){
		tfpath.setEnabled(opt);
		bopen.setEnabled(opt);
	}
	/**新しいファイルに対する表示の更新*/
	private void update(){
		ch1.setSelected(file!=null);
		enableButtons(file!=null);
		tfpath.setText((file!=null)?file.getAbsolutePath():
			LeafLangManager.get("Unusable path","無効なファイルパス")
		);
		dialog.load(file);
	}
	/**
	*設定ダイアログを表示します。
	*@return OKボタンで閉じられた場合、{@link LeafDialog#OK_OPTION}を返します。
	*/
	public boolean showDialog(){
		super.setVisible(true);
		return change;
	}
	/**
	*選択されたファイルを返します。「タイプ音を無効にする」場合、nullが返されます。
	*@return 選択された音声ファイル
	*/
	public File getSelectedFile(){
		return (ch1.isSelected())?file:null;
	}
}
