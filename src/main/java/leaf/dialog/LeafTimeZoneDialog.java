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
package leaf.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TimeZone;
import java.text.DateFormat;

import leaf.components.LeafClockPane;
import leaf.manager.LeafLangManager;

/**
*時計の表示とシステムのタイムゾーンの設定を行うモーダルダイアログです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年1月4日
*
*@see LeafClockPane
*@see LeafClockDialog
*/
public final class LeafTimeZoneDialog extends LeafDialog{
	
	private final LeafClockPane clock;
	private JPanel panel;
	private JLabel lbzone;
	private ClockLabel lbtime, lbutc;
	private JComboBox combo;
	private JButton bok, bcancel;
	
	private Timer timer;
	private TimeZone zone, utczone;
	private boolean isChanged = CANCEL_OPTION;
	
	private final String[] ids;
	
	/**
	*親フレームと指定してダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafTimeZoneDialog(Frame owner){
		this(owner, null);
	}
	/**
	*親ダイアログを指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*/
	public LeafTimeZoneDialog(Dialog owner){
		this(owner, null);
	}
	/**
	*親フレームとデフォルトのタイムゾーンを指定してダイアログを生成します。
	*@param owner 親フレーム
	*@param zone タイムゾーン
	*/
	public LeafTimeZoneDialog(Frame owner, TimeZone zone){
		super(owner, null, true);
		
		getContentPane().setPreferredSize(new Dimension(380,210));
		pack();
		setResizable(false);
		setLayout(null);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
				stop();
			}
		});
		utczone   = TimeZone.getTimeZone("UTC");
		this.zone = (zone != null)? zone : TimeZone.getDefault();
		
		ids = TimeZone.getAvailableIDs();
		Arrays.sort(ids);
		
		clock  = new LeafClockPane();
		init();
	}
	/**
	*親ダイアログとデフォルトのタイムゾーンを指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*@param zone タイムゾーン
	*/
	public LeafTimeZoneDialog(Dialog owner, TimeZone zone){
		super(owner, null, true);
		
		getContentPane().setPreferredSize(new Dimension(380,210));
		pack();
		setResizable(false);
		setLayout(null);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
				stop();
			}
		});
		utczone   = TimeZone.getTimeZone("UTC");
		this.zone = (zone != null)? zone : TimeZone.getDefault();
		
		ids = TimeZone.getAvailableIDs();
		Arrays.sort(ids);
		
		clock = new LeafClockPane();
		init();
	}
	/**
	*ダイアログの表示を初期化します。
	*/
	public void init(){
		
		setTitle(LeafLangManager.get("Time Zone", "タイムゾーン"));
		getContentPane().removeAll();
		
		/*アナログ時計*/
		clock.setBounds(5, 15, 150, 150);
		add(clock);
		
		/*情報パネル*/
		panel = new JPanel(new GridLayout(4, 1));
		panel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			LeafLangManager.get("Information", "インフォメーション")
		));
		panel.setBounds(160, 10, 210, 155);
		add(panel);
		
		/*タイムゾーン名*/
		panel.add(lbzone = new JLabel());
		
		/*時刻*/
		panel.add(lbtime = new ClockLabel(zone));
		
		/*UTCゾーン名*/
		panel.add(new JLabel(utczone.getDisplayName()));
		
		/*UTC時刻*/
		panel.add(lbutc = new ClockLabel(utczone));
		
		/*タイムゾーン選択*/
		combo = new JComboBox(ids);
		combo.setSelectedItem(zone.getID());
		combo.setBounds(5, 180, 150, 22);
		add(combo);
		
		combo.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				setTimeZone((String)combo.getSelectedItem());
			}
		});
		
		/*OKボタン*/
		bok = new JButton("OK");
		bok.setMnemonic(KeyEvent.VK_O);
		bok.setBounds(162, 180, 100, 22);
		add(bok);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = OK_OPTION;
				stop();
				dispose();
			}
		});
		
		/*キャンセルボタン*/
		bcancel = new JButton(LeafLangManager.get("Cancel","キャンセル"));
		bcancel.setMnemonic(KeyEvent.VK_C);
		bcancel.setBounds(270, 180, 100, 22);
		add(bcancel);
		
		bcancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = CANCEL_OPTION;
				stop();
				dispose();
			}
		});
		
		setTimeZone(TimeZone.getDefault());
		restart();
	}
	/**
	*タイムゾーン名を指定してダイアログを更新します。
	*@param ID タイムゾーンの名前
	*/
	public void setTimeZone(String ID){
		setTimeZone(TimeZone.getTimeZone(ID));
	}
	/**
	*タイムゾーンを指定してダイアログを更新します。
	*@param zone タイムゾーン
	*/
	public void setTimeZone(TimeZone zone){
		clock.setTimeZone(this.zone = zone);
		lbzone.setText(zone.getDisplayName());
		lbtime.setTimeZone(zone);
	}
	/**
	*設定されたタイムゾーンを返します。
	*@return タイムゾーン
	*/
	public TimeZone getTimeZone(){
		return zone;
	}
	/**
	*ダイアログを表示します。
	*@return OKボタンで閉じられた場合true
	*/
	public boolean showDialog(){
		restart();
		setVisible(true);
		return isChanged;
	}
	/**
	*現在時刻を表示するラベルです
	*/
	private class ClockLabel extends JLabel{
		private TimeZone zone;
		private final DateFormat format;
		/**
		*タイムゾーンを指定してラベルを生成します。
		*@param zone タイムゾーン
		*/
		public ClockLabel(TimeZone zone){
			format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);
			setTimeZone(zone);
		}
		/**
		*タイムゾーンを設定します。
		*@param zone タイムゾーン
		*/
		public void setTimeZone(TimeZone zone){
			format.setTimeZone(this.zone = zone);
		}
		/**
		*時計表示を更新します。
		*/
		public void update(){
			setText(format.format(Calendar.getInstance(zone).getTime()));
		}
	}
	/**
	*１秒ごとに再描画します。
	*/
	private class ClockTimer extends TimerTask{
		public void run(){
			if(LeafTimeZoneDialog.this.isVisible()){
				lbtime.update();
				lbutc.update();
			}
		}
	}
	/**
	*スレッドを停止して計時を終了します。
	*/
	private void stop(){
		try{
			clock.stop();
			timer.cancel();
			timer = null;
		}catch(NullPointerException ex){}
	}
	/**
	*スレッドを生成して計時を再開します。
	*/
	private void restart(){
		try{
			timer = new Timer();
			timer.schedule(new ClockTimer(),0,1000);
			clock.restart();
		}catch(NullPointerException ex){}
	}
}