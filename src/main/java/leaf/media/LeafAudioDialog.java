/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.media;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import leaf.dialog.LeafDialog;
import leaf.icon.LeafIcons;

/**
 *音声ファイルを再生する簡易機能をアプリケーション向けに提供します。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2009年3月12日
 *@see LeafAudioPlayer
 */
public final class LeafAudioDialog extends LeafDialog{
	private final LeafAudioPlayer player;
	private final JFileChooser chooser;
	private JProgressBar indicator;
	private JLabel label;
	private JButton bopen, bstop;
	private JToggleButton bplay, bloop;
	private PlayWorker worker;
	private final IndicateListener listener;
	
	/**
	 *親フレームを指定してモーダレスダイアログを生成します。
	 *@param owner 親フレーム
	 */
	public LeafAudioDialog(Frame owner){
		super(owner, false);
		setContentSize(new Dimension(280, 50));
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(worker != null){
					worker.cancel(true);
					worker = null;
				}
			}
		});
		init();
		player = new LeafAudioPlayer();
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		"AIFC/AIFF/AU/SND/WAV","aifc","aif","aiff","au","snd","wav"));
		listener = new IndicateListener();
	}
	/**
	 *親ダイアログを指定してモーダレスダイアログを生成します。
	 *@param owner 親ダイアログ
	 */
	public LeafAudioDialog(Dialog owner){
		super(owner, false);
		setContentSize(new Dimension(280, 50));
		setResizable(false);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(worker != null){
					worker.cancel(true);
					worker = null;
				}
			}
		});
		init();
		player = new LeafAudioPlayer();
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		"AIFC/AIFF/AU/SND/WAV","aifc","aif","aiff","au","snd","wav"));
		listener = new IndicateListener();
	}
	/**
	 *ダイアログの表示と配置を初期化します。
	 */
	@Override public void init(){
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		JToolBar toolbar = new JToolBar();
		add(toolbar, BorderLayout.NORTH);
		toolbar.setFloatable(false);
		
		bopen = new JButton(LeafIcons.getIcon("OPEN"));
		bopen.setToolTipText(translate("button_open"));
		toolbar.add(bopen);
		initButton(bopen);
		
		bopen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(chooser.showOpenDialog(LeafAudioDialog.this) 
				== JFileChooser.APPROVE_OPTION){
					try{
						load(chooser.getSelectedFile());
					}catch(IOException ex){}
				}
			}
		});
		
		bplay = new JToggleButton(LeafIcons.getIcon("PLAY"));
		bplay.setToolTipText(translate("button_play"));
		bplay.setEnabled(false);
		toolbar.add(bplay);
		initButton(bplay);
		
		bplay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(bplay.isSelected()){
					bopen.setEnabled(false);
					bloop.setEnabled(false);
					bstop.setEnabled(true);
					worker = new PlayWorker();
					worker.addPropertyChangeListener(listener);
					player.start();
					worker.execute();
				}else if(worker != null){
					player.pause();
					worker.cancel(true);
					worker = null;
				}
			}
		});
		
		bstop = new JButton(LeafIcons.getIcon("STOP"));
		bstop.setToolTipText(translate("button_stop"));
		bstop.setEnabled(false);
		toolbar.add(bstop);
		initButton(bstop);
		
		bstop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				worker.cancel(true);
				worker = null;
			}
		});
		
		bloop = new JToggleButton(LeafIcons.getIcon("LOOP"));
		bloop.setToolTipText(translate("button_loop"));
		bloop.setEnabled(false);
		toolbar.add(bloop);
		initButton(bloop);
		
		bloop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				player.setLoopMode(bloop.isSelected());
			}
		});
		
		add(indicator = new JProgressBar(), BorderLayout.CENTER);
		add(label = new JLabel(), BorderLayout.SOUTH);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setText(translate("label_no_file_selected"));
	}
	/**
	 *ボタンの初期設定を行います。
	 *@param button ボタン
	 */
	private void initButton(AbstractButton button){
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setRequestFocusEnabled(false);
	}
	/**
	 *指定された音声ファイルを読み込みます。
	 *@param file 音声ファイル
	 *@throws IOException 読み込みに失敗した場合
	 */
	public void load(File file) throws IOException{
		try{
			player.load(file);
			label.setText(file.getName());
			bplay.setEnabled(true);
			bstop.setEnabled(true);
			bloop.setEnabled(true);
			chooser.setSelectedFile(file);
		}catch(IOException ex){
			label.setText(ex.toString());
			bplay.setEnabled(false);
			bstop.setEnabled(false);
			bloop.setEnabled(false);
			throw ex;
		}
	}
	/**
	 *再生を停止してからダイアログを閉じます。
	 */
	@Override public void dispose(){
		if(worker != null){
			worker.cancel(true);
			worker = null;
		}
		super.dispose();
	}
	/**
	 *音声再生中にインジケーターを自動更新します。
	 */
	private class PlayWorker extends SwingWorker<String, String>{
		@Override public String doInBackground(){
			final long length = player.getFrameLength();
			if(length == 0) return "Done";
			while(!isCancelled()){
				long pos = player.getFramePosition();
				setProgress(((int)(100 * pos / length)) %100);
				if(!player.isLoopMode() && pos >= length) break;
				try{
					Thread.sleep(100);
				}catch(InterruptedException ex){}
			}
			player.setFramePosition(0);
			return "Done";
		}
		@Override public void done(){
			if(player.isPlaying()){
				indicator.setValue(0);
				player.stop();
			}
			bplay.setSelected(false);
			bopen.setEnabled(true);
			bloop.setEnabled(true);
			bstop.setEnabled(false);
			worker = null;
		}
	}
	/**
	 *インジケータの自動更新イベントを受け取ります。
	 */
	private class IndicateListener implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent e){
			try{
				indicator.setValue(worker.getProgress());
			}catch(NullPointerException ex){}
		}
	}
}