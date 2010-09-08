/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import leaf.icon.LeafIcons;
import leaf.components.LeafButtons;
import leaf.manager.LeafLangManager;
import leaf.media.LeafAudioPlayer;

/**
*音声ファイルを簡単に再生するための専用ダイアログです。<br>
*再生・一時停止・停止・ループ再生の機能があります。<br>
*WAVE、AU、AIFF、SNDの各形式に対応します。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2009年3月12日
*@see LeafAudioPlayer
*/
public class LeafAudioDialog extends LeafDialog implements ActionListener,Runnable{
	
	/**秘匿フィールド*/
	private final LeafAudioPlayer player;
	private final Thread thread;
	
	/**GUI*/
	private final JFileChooser chooser;
	private JToolBar toolbar;
	private JProgressBar progress;
	private JLabel label;
	
	/**
	*親フレームとダイアログを生成します。
	*@param parent 親フレーム
	*/
	public LeafAudioDialog(Frame parent){
		this(parent,null);
	}
	/**
	*親フレームと音声ファイルを指定してダイアログを生成します。
	*@param parent 親フレーム
	*@param file 音声ファイル
	*/
	public LeafAudioDialog(Frame parent,File file){
		super(parent,null,false);
		player = new LeafAudioPlayer();
		chooser = new AudioFileChooser(file);
		
		this.init();
		
		this.setSize(300,90);
		this.setResizable(false);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				player.stop();
			}
		});
		
		if(file!=null)input(file);
		
		thread = new Thread(this);
		thread.start();
	}
	/**
	*ダイアログを初期化します。
	*/
	public void init(){
		
		this.setTitle(LeafLangManager.get("MusicPlayer","ミュージックプレーヤー"));
		
		this.getContentPane().removeAll();
		
		LeafIcons icons = new LeafIcons();
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		this.add(toolbar,BorderLayout.NORTH);
		toolbar.add(LeafButtons.getButton("Open","開く",icons.getIcon("open"),this));
		toolbar.add(LeafButtons.getButton("Play/Pause","再生/一時停止",icons.getIcon("play"),this));
		toolbar.add(LeafButtons.getButton("Stop","停止",icons.getIcon("stop"),this));
		toolbar.add(LeafButtons.getToggleButton("Loop","ループ",icons.getIcon("loop"),this));
		
		progress = new JProgressBar();
		progress.setStringPainted(true);
		this.add(progress,BorderLayout.CENTER);
		
		label = new JLabel(LeafLangManager.get("No File Selected","未選択"),JLabel.CENTER);
		label.setPreferredSize(new Dimension(300,20));
		this.add(label,BorderLayout.SOUTH);
		
		this.setIconImage(icons.getIcon("logo").getImage());
	}
	/**
	*ダイアログを表示します。<br>
	*LeafAudioDialogに限れば、{@link LeafDialog#setVisible(boolean) setVisible(true)}でも同様に動作します。
	*/
	public void showDialog(){
		this.setVisible(true);
	}
	public void actionPerformed(ActionEvent e){
		String cmd = ((AbstractButton)e.getSource()).getActionCommand();
		if(cmd.equals("Open")){
			player.stop();
			open();
		}else if(cmd.equals("Play/Pause")){
			if(player.isPlaying()){
				player.pause();
			}else{
				player.start();
			}
		}else if(cmd.equals("Stop")){
			player.stop();
		}else{
			player.setLoopMode(!player.isLoopMode());
		}
	}
	/**
	*プレーヤの再生状態を表示するために実行されるスレッドです。
	*/
	public void run(){
		long position = 0;
		while(true){
			try{
				progress.setValue((int)((100*(player.getFramePosition())/player.getFrameLength()))%100);
				if(!player.isLoopMode()&&player.getFramePosition()>=player.getFrameLength())player.stop();
				if(player.isPlaying())Thread.sleep(100);
				else Thread.sleep(500);
			}catch(Exception ex){
				JOptionPane.showMessageDialog(this,LeafLangManager.get("Error has occurred in the thread.","スレッドの処理でエラーが発生しました。"),"エラー",JOptionPane.WARNING_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
	/**開く*/
	private void open(){
		AudioFileChooser chooser = new AudioFileChooser(player.getFile());
		if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
			input(chooser.getSelectedFile());
		}
	}
	/**
	*指定された音声ファイルを読み込みます。
	*@param file 音声ファイル
	*/
	public void input(File file){
		try{
			player.init(file);
			label.setText(player.getFile().getName()+"  "+player.getMicrosecondLength());
		}catch(Exception ex){
			JOptionPane.showMessageDialog(this,LeafLangManager.get("Error has occurred in initializing the player.","プレーヤの初期化に失敗しました。"),"エラー",JOptionPane.INFORMATION_MESSAGE);
			ex.printStackTrace();
		}
	}
	/**専用チューザ*/
	private class AudioFileChooser extends JFileChooser{
		public AudioFileChooser(File file){
			super(file);
			this.setSelectedFile(file);
			javax.swing.filechooser.FileFilter[] filter = new FileNameExtensionFilter[5];
 			filter[0] = new FileNameExtensionFilter("AIFC(*.aifc)","aifc");
 			filter[1] = new FileNameExtensionFilter("AIFF(*.aif;*.aiff)","aif","aiff");
 			filter[2] = new FileNameExtensionFilter("AU(*.au)","au");
 			filter[3] = new FileNameExtensionFilter("SND(*.snd)","snd");
 			filter[4] = new FileNameExtensionFilter("WAVE(*.wav)","wav");
			for(int i=0;i<=4;i++){
				addChoosableFileFilter(filter[i]);
			}
		}
	}
}
