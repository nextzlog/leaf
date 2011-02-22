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
package leaf.media.aquestalk;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;

import leaf.components.text.*;
import leaf.dialog.LeafDialog;
import leaf.manager.*;

/**
*AquesTalk2をアプリケーションから簡単に操作するためのダイアログです。<br>
*AquesTalk2を制御するには以下のパラメータが必要です。<br>
*<ul>
*<li>Phontファイル…声のもととなるデータです
*<li>読み上げ速度…標準速度に対する100分比率で50以上300以下の整数
*</ul><br>
*<b>AquesTalk2は環境に合わせて別途ダウンロードしてください。</b><br>
*必要なDLL : AquesTalk2Da
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年10月2日
*/

public final class AquesTalkDialog extends LeafDialog{
	
	private JLabel phontlb, speedlb;
	private LeafTextField phontfld;
	private JSlider speedsld;
	private JSpinner speedspin;
	private LeafTextPane testpane;
	private JButton bopen, btest, breset, bclose;
	
	private final JFileChooser chooser;
	private final LeafFileManager filemanager;
	
	private final AquesTalkManager manager;
	private ExSwingWorker worker = null;
	private File  file;
	
	/**
	*親フレームを指定してモーダレスなダイアログを生成します。
	*@param owner 親フレーム
	*/
	public AquesTalkDialog(Frame owner){
		this(owner, null, 100, false);
	}
	/**
	*親ダイアログを指定してモーダレスなダイアログを生成します。
	*@param owner 親ダイアログ
	*/
	public AquesTalkDialog(Dialog owner){
		this(owner, null, 100, false);
	}
	/**
	*親フレームとPhontファイルを指定してダイアログを生成します。
	*@param owner 親フレーム
	*@param file Phontファイル
	*@param speed 読み上げ速度
	*@param modal モーダルの場合true
	*/
	public AquesTalkDialog(Frame owner, File file, int speed, boolean modal){
		super(owner, null, modal);
		getContentPane().setPreferredSize(new Dimension(500, 180));
		pack();
		setResizable(false);
		setLayout(null);
		
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
			LeafLangManager.get("Phont File","Phontファイル"),"phont"
		));
		filemanager = new LeafFileManager();
		manager = new AquesTalkManager();
		
		init(file,speed);
	}
	/**
	*親ダイアログとPhontファイル及び読み上げ速度を指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*@param file Phontファイル
	*@param speed 読み上げ速度
	*@param modal モーダルの場合true
	*/
	public AquesTalkDialog(Dialog owner, File file, int speed, boolean modal){
		super(owner, null, modal);
		getContentPane().setPreferredSize(new Dimension(500, 180));
		pack();
		setResizable(false);
		setLayout(null);
		
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
			LeafLangManager.get("Phont File","Phontファイル"),"phont"
		));
		filemanager = new LeafFileManager();
		manager = new AquesTalkManager();
		
		init(file,speed);
	}
	/**
	*Phontファイルと読み上げ速度を指定してダイアログを初期化します。
	*@param file Phontファイル
	*@param speed 読み上げ速度
	*/
	public void init(final File file, final int speed){
		setTitle("AquesTalk2 " + LeafLangManager.get("Console","コンソール"));
		
		getContentPane().removeAll();
		
		/*Phont選択*/
		phontlb = new JLabel("Phont");
		phontlb.setBounds(5,10,60,20);
		add(phontlb);
		
		phontfld = new LeafTextField();
		phontfld.setBounds(65,10,320,20);
		phontfld.setEditable(false);
		add(phontfld);
		
		bopen = new JButton(LeafLangManager.get("Open","参照"));
		bopen.setBounds(400,10,100,20);
		add(bopen);
		
		bopen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openPhontFile();
			}
		});
		
		/*読み上げスピード*/
		speedlb = new JLabel(LeafLangManager.get("Speed","速度"));
		speedlb.setBounds(5,40,60,20);
		add(speedlb);
		
		speedsld = new JSlider(50,300,speed);
		speedsld.setBounds(65,40,320,40);
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
		
		speedspin = new JSpinner(new SpinnerNumberModel(speed,50,300,1));
		speedspin.setBounds(400,40,100,20);
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
		
		/*テスト発声*/
		testpane = new LeafTextPane();
		testpane.setOpaque(true);
		LeafTextScrollPane scroll = new LeafTextScrollPane(testpane,true,false);
		scroll.setVerticalScrollBarPolicy(scroll.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(5,85,380,85);
		add(scroll);
		
		btest = new JButton(LeafLangManager.get("Test","テスト"));
		btest.setBounds(400,70,100,20);
		add(btest);
		
		btest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					speak(testpane.getText());
				}catch(Exception ex){
					showMessage(LeafLangManager.get(
						"Failed to drive AquesTalk2","AquesTalk2の起動に失敗しました"
					));
				}
			}
		});
		
		/*リセット*/
		breset = new JButton(LeafLangManager.get("Reset","リセット"));
		breset.setBounds(400,100,100,20);
		add(breset);
		breset.setEnabled(false);
		
		breset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					setPhontFile(file);
					speedsld.setValue(speed);
					breset.setEnabled(false);
				}catch(IOException ex){}
			}
		});
		
		/*閉じる*/
		bclose = new JButton(LeafLangManager.get("Close","閉じる"));
		bclose.setBounds(400,150,100,20);
		add(bclose);
		
		bclose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		try{
			setPhontFile(file);
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	/**
	*使用中の{@link AquesTalkManager}のインスタンスを返します。
	*@return AquesTalkManager
	*/
	public AquesTalkManager getAquesTalkManager(){
		return manager;
	}
	/**
	*指定された文字列データを非同期に発生します。
	*発声中新規にこのメソッドを実行しても処理されません。
	*@param data 文字列データ
	*@throws IOException 出力エラーが起きた場合
	*@see #isSpeaking()
	*/
	public void speak(String data) throws IOException {
		if(worker == null){
			worker = new ExSwingWorker(data.replaceAll("\r?\n",""));
			worker.execute();
		}
	}
	/**
	*発声動作中かどうか返します。
	*@return 発声動作中の場合true
	*/
	public boolean isSpeaking(){
		return (worker != null);
	}
	/**
	*Phontデータを開きます。
	*/
	private void openPhontFile(){
		if(chooser.showOpenDialog(this) == chooser.APPROVE_OPTION){
			breset.setEnabled(true);
			try{
				setPhontFile(chooser.getSelectedFile());
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	/**
	*参照するPhontファイルを設定します。
	*@param file Phontファイル
	*@throws IOException ファイルのロードに失敗した場合
	*/
	public void setPhontFile(File file) throws IOException {
		if(file!=null&&file.exists()){
			file = file.getAbsoluteFile();
			if(file.equals(this.file)) return;
			phontfld.setText(file.getName());
		}else{
			this.file = null;
			phontfld.setText(LeafLangManager.get("Default","デフォルト"));
		}
		manager.load(file);
	}
	/**
	*参照するPhontファイルを返します。
	*@return Phontファイル
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
	private class ExSwingWorker extends SwingWorker<String,String>{
		private final String data;
		public ExSwingWorker(String data){
			super();
			this.data = data;
		}
		public String doInBackground(){
			btest.setEnabled(false);
			try{
				manager.speak(data,speedsld.getValue());
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return "Done";
		}
		public void done(){
			btest.setEnabled(true);
			worker = null;
		}
	}
}