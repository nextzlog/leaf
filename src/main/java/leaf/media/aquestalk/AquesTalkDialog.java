/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.media.aquestalk;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import leaf.dialog.LeafDialog;
import leaf.swing.text.*;

/**
 *AquesTalk2操作画面をアプリケーション向けに提供します。
 *<pre>
 *声種ファイル…声のもととなるデータです
 *読み上げ速度…標準速度に対する100分比率で50以上300以下の整数
 *</pre>
 *AquesTalk2エンジンは環境に合わせて別途ダウンロードしてください。
 *<br><br>
 *必要なDLL : AquesTalk2Da.dll(so) (leaf.jarと同じディレクトリに配置)
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年10月2日
 */
public final class AquesTalkDialog extends LeafDialog{
	private JButton bopen, btest, breset, bclose;
	private JLabel phontlb, speedlb;
	private JSlider speedsld;
	private JSpinner speedspin;
	private LeafTextField phontfld;
	private LeafTextPane testpane;
	private final JFileChooser chooser;
	private final AquesTalkManager manager;
	private File file;
	
	/**
	 *親フレームを指定してモーダレスダイアログを生成します。
	 *@param owner 親フレーム
	 *@throws AquesTalkException エンジンが見つからない場合
	 */
	public AquesTalkDialog(Frame owner)
	throws AquesTalkException{
		super(owner, false);
		manager = new AquesTalkManager();
		setContentSize(new Dimension(500, 180));
		setResizable(false);
		setLayout(null);
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		translate("chooser_file_filter_description"), "phont"));
		init();
	}
	/**
	 *親ダイアログを指定してモーダレスダイアログを生成します。
	 *@param owner 親ダイアログ
	 *@throws AquesTalkException エンジンが見つからない場合
	 */
	public AquesTalkDialog(Dialog owner)
	throws AquesTalkException{
		super(owner, false);
		manager = new AquesTalkManager();
		setContentSize(new Dimension(500, 180));
		setResizable(false);
		setLayout(null);
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		translate("chooser_file_filter_description"), "phont"));
		init();
	}
	/**
	 *ダイアログの表示と配置を初期化します。
	 */
	@Override public void init(){
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		/*select phont file*/
		phontlb = new JLabel(translate("label_phont"));
		phontlb.setBounds(5, 10, 60, 20);
		add(phontlb);
		
		phontfld = new LeafTextField("Default");
		phontfld.setBounds(65, 10, 320, 20);
		phontfld.setEditable(false);
		add(phontfld);
		
		bopen = new JButton(translate("button_open"));
		bopen.setBounds(400, 10, 100, 20);
		add(bopen);
		
		bopen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openPhontFile();
			}
		});
		
		/*speed*/
		speedlb = new JLabel(translate("label_speed"));
		speedlb.setBounds(5, 40, 60, 20);
		add(speedlb);
		
		speedsld = new JSlider(50, 300, 100);
		speedsld.setBounds(65, 40, 320, 40);
		speedsld.setLabelTable(speedsld.createStandardLabels(50));
		speedsld.setPaintLabels(true);
		add(speedsld);
		
		speedsld.addChangeListener(new ChangeListener(){
			private int value = speedsld.getValue();
			public void stateChanged(ChangeEvent e){
				int now = speedsld.getValue();
				if(now != value){
					speedspin.setValue(value = now);
					breset.setEnabled(true);
				}
			}
		});
		
		SpinnerModel model = new SpinnerNumberModel(100, 50, 300, 1);
		speedspin = new JSpinner(model);
		speedspin.setBounds(400, 40, 100, 20);
		add(speedspin);
		
		speedspin.addChangeListener(new ChangeListener(){
			private int value = (Integer)speedspin.getValue();
			public void stateChanged(ChangeEvent e){
				int now = (Integer)speedspin.getValue();
				if(now != value){
					speedsld.setValue(value = now);
				}
			}
		});
		
		/*test speak*/
		testpane = new LeafTextPane();
		testpane.setLineCursorVisible(false);
		JScrollPane scroll = new JScrollPane(testpane);
		scroll.setBounds(5, 85, 380, 85);
		add(scroll);
		
		btest = new JButton(translate("button_test"));
		btest.setBounds(400, 70, 100, 20);
		add(btest);
		
		btest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					speak(testpane.getText());
				}catch(AquesTalkException ex){
					showMessage(translate(
						"button_test_action_error"));
				}
			}
		});
		
		/*reset*/
		breset = new JButton(translate("button_reset"));
		breset.setBounds(400, 100, 100, 20);
		add(breset);
		breset.setEnabled(false);
		
		breset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					setPhontFile(file);
					speedsld.setValue(100);
					breset.setEnabled(false);
				}catch(AquesTalkException ex){}
			}
		});
		
		/*close*/
		bclose = new JButton(translate("button_close"));
		bclose.setBounds(400, 150, 100, 20);
		add(bclose);
		
		bclose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
	}
	/**
	 *使用中の{@link AquesTalkManager}のインスタンスを返します。
	 *@return AquesTalkManager
	 */
	public AquesTalkManager getAquesTalkManager(){
		return manager;
	}
	/**
	 *指定された文字列データを非同期に発声します。
	 *@param data 文字列データ
	 *@throws AquesTalkException 出力エラーが起きた場合
	 */
	public void speak(String data) throws AquesTalkException {
		btest.setEnabled(false);
		new SpeakWorker(data.replaceAll("\r?\n","")).execute();
	}
	/**
	 *声種データを開きます。
	 */
	private void openPhontFile(){
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			breset.setEnabled(true);
			try{
				setPhontFile(chooser.getSelectedFile());
			}catch(AquesTalkException ex){
				ex.printStackTrace();
			}
		}
	}
	/**
	 *参照する声種ファイルを設定します。
	 *@param file 声種ファイル
	 *@throws AquesTalkException ファイルのロードに失敗した場合
	 */
	public void setPhontFile(File file) throws AquesTalkException{
		if((this.file = file) == null) phontfld.setText("Default");
		else phontfld.setText(file.getName());
		manager.load(file);
	}
	/**
	 *参照する声種ファイルを返します。
	 *@return 声種ファイル
	 */
	public File getPhontFile(){
		return file;
	}
	/**
	 *発声速度を設定します。
	 *@param speed 50から300までの整数
	 */
	public void setSpeed(int speed){
		speedsld.setValue(speed);
	}
	/**
	 *発声速度を返します。
	 *@return 発声速度
	 */
	public int getSpeed(){
		return speedsld.getValue();
	}
	/**
	 *テスト用のテキストを設定します。
	 *@param text テスト発声用のテキスト
	 */
	public void setTestText(String text){
		testpane.setText(text);
	}
	/**
	 *テスト用のテキストを返します。
	 *@return テスト発声用のテキスト
	 */
	public String getTestText(){
		return testpane.getText();
	}
	/**バックグラウンド処理*/
	private class SpeakWorker extends SwingWorker<String,String>{
		private final String text;
		public SpeakWorker(String text){
			this.text = text;
		}
		public String doInBackground(){
			try{
				manager.speak(text, speedsld.getValue());
			}catch(AquesTalkException ex){
				ex.printStackTrace();
			}
			return "Done";
		}
		public void done(){
			btest.setEnabled(true);
		}
	}
}