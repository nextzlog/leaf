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
package leaf.media;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import leaf.components.LeafButtons;
import leaf.dialog.LeafDialog;
import leaf.icon.LeafIcons;
import leaf.manager.LeafLangManager;

/**
*音声ファイルを簡単に再生するための専用ダイアログです。
*WAVE、AU、AIFF、SNDの各形式に対応します。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2009年3月12日
*@see LeafAudioPlayer
*/
public final class LeafAudioDialog extends LeafDialog{
	
	private final LeafAudioPlayer player = new LeafAudioPlayer();
	
	private final JFileChooser chooser = new JFileChooser();
	private final JToolBar toolbar = new JToolBar();
	private final JProgressBar progress = new JProgressBar();
	private final JLabel label = new JLabel();
	private JButton bopen, bplay, bstop;
	private JToggleButton bloop;
	
	/**
	*親フレームを指定してダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafAudioDialog(Frame owner){
		this(owner, null);
	}
	/**
	*親ダイアログを指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*/
	public LeafAudioDialog(Dialog owner){
		this(owner, null);
	}
	/**
	*親フレームとデフォルトの音声ファイルを指定してダイアログを生成します。
	*@param owner 親フレーム
	*@param file 音声ファイル
	*/
	public LeafAudioDialog(Frame owner, File file){
		super(owner,null,false);
		getContentPane().setPreferredSize(new Dimension(280,50));
		pack();
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				player.stop();
			}
		});
		init(file);
		new ExThread().start();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
			"AIFC/AIFF/AU/SND/WAV","aifc","aif","aiff","au","snd","wav"
		));
	}
	/**
	*親ダイアログとデフォルトの音声ファイルを指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*@param file 音声ファイル
	*/
	public LeafAudioDialog(Dialog owner, File file){
		super(owner,null,false);
		getContentPane().setPreferredSize(new Dimension(280,50));
		pack();
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				player.stop();
			}
		});
		init(file);
		new ExThread().start();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
			"AIFC/AIFF/AU/SND/WAV","aifc","aif","aiff","au","snd","wav"
		));
	}
	/**
	*音声ファイルを指定してこのダイアログを初期化します。
	*@param file 音声ファイル
	*/
	public void init(File file){
		
		setTitle(LeafLangManager.get("Audio Player","オーディオプレーヤー"));
		getContentPane().removeAll();
		
		LeafIcons icons = new LeafIcons();
		
		toolbar.removeAll();
		toolbar.setFloatable(false);
		add(toolbar, BorderLayout.NORTH);
		
		ActionListener oa = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openFile();
			}
		};
		bopen = LeafButtons.createButton(
			"Open", "開く", icons.getIcon(icons.OPEN), oa
		);
		toolbar.add(bopen);
		
		ActionListener pa = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(player.isPlaying()) player.pause();
				else player.start();
			}
		};
		bplay = LeafButtons.createButton(
			"Play/Stop", "再生/一時停止", icons.getIcon(icons.PLAY), pa
		);
		toolbar.add(bplay);
		
		ActionListener sa = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				player.stop();
			}
		};
		
		bstop = LeafButtons.createButton(
			"Stop", "停止", icons.getIcon(LeafIcons.STOP), sa
		);
		toolbar.add(bstop);
		
		ActionListener la = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				player.setLoopMode(!player.isLoopMode());
			}
		};
		
		bloop = LeafButtons.createToggleButton(
			"Loop", "連続再生", icons.getIcon(LeafIcons.LOOP), la
		);
		toolbar.add(bloop);
		
		progress.setStringPainted(true);
		add(progress, BorderLayout.CENTER);
		
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setText(LeafLangManager.get("No File Selected","未選択"));
		add(label, BorderLayout.SOUTH);
		
		setIconImage(icons.getIcon("logo").getImage());
		
		repaint();
		
		if(file!=null){
			load(file);
			chooser.setSelectedFile(file);
		}
	}
	/**
	*バックグラウンド処理
	*/
	private class ExThread extends Thread{
		public void run(){
			long position = 0;
			try{
				while(true){
					position = player.getFramePosition();
					progress.setValue((int)
						(100* position / (player.getFrameLength()+1))%100
					);
					if(position >= player.getFrameLength()){
						player.stop();
						if(player.isLoopMode())player.start();
					}
					Thread.sleep(500);
				}
			}catch(Exception ex){
				ex.printStackTrace();
				label.setText(ex.toString());
			}
		}
	}
	/**
	*ファイルを開きます。
	*/
	private void openFile(){
		if(chooser.showOpenDialog(this)==chooser.APPROVE_OPTION){
			load(chooser.getSelectedFile());
		}
	}
	/**
	*指定された音声ファイルを読み込みます。
	*@param file 音声ファイル
	*@return 読み込みに失敗した場合false
	*/
	public boolean load(File file) {
		
		try{
			player.load(file);
			label.setText((file!=null)?file.getName():LeafLangManager.get(
				"No File Selected","ファイルが選択されていません"
			));
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			label.setText(ex.toString());
			return false;
		}
	}
	/**
	*音声の再生を停止してからダイアログを閉じます。
	*/
	public void dispose(){
		player.stop();
		super.dispose();
	}
}
