/**************************************************************************************
���C�u�����uLeafAPI�v �J���J�n�F2010�N6��8��
�J������FPure Java SE 6
�J���ҁF����A�}�`���A�����N���u
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
 *�����t�@�C�����Đ�����ȈՋ@�\���A�v���P�[�V���������ɒ񋟂��܂��B
 *
 *@author ����A�}�`���A�����N���u
 *@since Leaf 1.0 �쐬�F2009�N3��12��
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
	 *�e�t���[�����w�肵�ă��[�_���X�_�C�A���O�𐶐����܂��B
	 *@param owner �e�t���[��
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
	 *�e�_�C�A���O���w�肵�ă��[�_���X�_�C�A���O�𐶐����܂��B
	 *@param owner �e�_�C�A���O
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
	 *�_�C�A���O�̕\���Ɣz�u�����������܂��B
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
	 *�{�^���̏����ݒ���s���܂��B
	 *@param button �{�^��
	 */
	private void initButton(AbstractButton button){
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setRequestFocusEnabled(false);
	}
	/**
	 *�w�肳�ꂽ�����t�@�C����ǂݍ��݂܂��B
	 *@param file �����t�@�C��
	 *@throws IOException �ǂݍ��݂Ɏ��s�����ꍇ
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
	 *�Đ����~���Ă���_�C�A���O����܂��B
	 */
	@Override public void dispose(){
		if(worker != null){
			worker.cancel(true);
			worker = null;
		}
		super.dispose();
	}
	/**
	 *�����Đ����ɃC���W�P�[�^�[�������X�V���܂��B
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
	 *�C���W�P�[�^�̎����X�V�C�x���g���󂯎��܂��B
	 */
	private class IndicateListener implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent e){
			try{
				indicator.setValue(worker.getProgress());
			}catch(NullPointerException ex){}
		}
	}
}