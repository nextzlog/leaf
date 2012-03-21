/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
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

import leaf.swing.LeafClockPane;

/**
 *世界時計の表示機能を持つモーダレス時計ダイアログです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.2 作成：2011年1月5日
 @see LeafClockPane
 */
public final class LeafClockDialog extends LeafDialog{
	private TimeZone timezone, utczone;
	private final LeafClockPane clock;
	private ClockLabel lbtime, lbutc;
	private JButton bclose;
	private JComboBox combo;
	private JLabel lbzone;
	private JPanel panel;
	private Timer timer;
	private final String[] ids;
	
	/**
	*親フレームと指定してダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafClockDialog(Frame owner){
		this(owner, null);
	}
	/**
	*親ダイアログを指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*/
	public LeafClockDialog(Dialog owner){
		this(owner, null);
	}
	/**
	*親フレームとタイムゾーンを指定してダイアログを生成します。
	*@param owner 親フレーム
	*@param tz デフォルトのタイムゾーン
	*/
	public LeafClockDialog(Frame owner, TimeZone tz){
		super(owner, false);
		setContentSize(new Dimension(380, 210));
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				stop();
			}
		});
		timezone = (tz!=null)? tz : TimeZone.getDefault();
		utczone  = TimeZone.getTimeZone("UTC");
		Arrays.sort(ids = TimeZone.getAvailableIDs());
		
		clock = new LeafClockPane();
		setLayout(null);
		init();
	}
	/**
	*親ダイアログとタイムゾーンを指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*@param tz デフォルトのタイムゾーン
	*/
	public LeafClockDialog(Dialog owner, TimeZone tz){
		super(owner, false);
		setContentSize(new Dimension(380, 210));
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				stop();
			}
		});
		timezone = (tz!=null)? tz : TimeZone.getDefault();
		utczone  = TimeZone.getTimeZone("UTC");
		Arrays.sort(ids = TimeZone.getAvailableIDs());
		
		clock = new LeafClockPane();
		setLayout(null);
		init();
	}
	/**
	*ダイアログの表示と配置を初期化します。
	*/
	@Override public void init(){
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		/*アナログ時計*/
		clock.setBounds(5, 15, 150, 150);
		add(clock);
		
		/*情報パネル*/
		panel = new JPanel(new GridLayout(4, 1));
		panel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			translate("panel_information")
		));
		panel.setBounds(160, 10, 210, 155);
		add(panel);
		
		/*タイムゾーン名*/
		panel.add(lbzone = new JLabel());
		
		/*時刻*/
		panel.add(lbtime = new ClockLabel(timezone));
		
		/*UTCゾーン名*/
		panel.add(new JLabel(utczone.getDisplayName()));
		
		/*UTC時刻*/
		panel.add(lbutc = new ClockLabel(utczone));
		
		/*タイムゾーン選択*/
		combo = new JComboBox(ids);
		combo.setSelectedItem(timezone.getID());
		combo.setBounds(5, 180, 150, 22);
		add(combo);
		
		combo.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				setTimeZone(TimeZone.getTimeZone(
					(String)combo.getSelectedItem()));
			}
		});
		
		/*閉じるボタン*/
		bclose = new JButton(translate("button_close"));
		bclose.setMnemonic(KeyEvent.VK_C);
		bclose.setBounds(270, 180, 100, 22);
		add(bclose);
		
		bclose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				stop();
				dispose();
			}
		});
		
		setTimeZone(TimeZone.getDefault());
		start();
	}
	/**
	*選択されているタイムゾーンを返します。
	*@return 時計のタイムゾーン
	*/
	public TimeZone getTimeZone(){
		return timezone;
	}
	/**
	*タイムゾーンを指定してダイアログを更新します。
	*@param tz タイムゾーン
	*/
	public void setTimeZone(TimeZone tz){
		clock.setTimeZone(timezone = tz);
		lbzone.setText(tz.getDisplayName());
		lbtime.setTimeZone(tz);
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
			format = DateFormat.getDateTimeInstance(
				DateFormat.FULL, DateFormat.MEDIUM);
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
			setText(format.format(
				Calendar.getInstance(zone).getTime()));
		}
	}
	/**
	*１秒ごとに再描画します。
	*/
	private class ClockTimer extends TimerTask{
		public void run(){
			if(LeafClockDialog.this.isVisible()){
				lbtime.update();
				lbutc.update();
			}
		}
	}
	/**
	*時計の自動的な再描画を開始します。
	*/
	private void start(){
		try{
			timer = new Timer();
			timer.schedule(new ClockTimer(), 0, 1000);
			clock.start();
		}catch(NullPointerException ex){}
	}
	/**
	*時計の自動的な再描画を停止します。
	*/
	private void stop(){
		try{
			clock.stop();
			timer.cancel();
			timer = null;
		}catch(NullPointerException ex){}
	}
	/**
	*ダイアログの表示と非表示を切り替えます。
	*@param visible 表示する場合true
	*/
	public void setVisible(boolean visible){
		if(visible) start();
		else stop();
		super.setVisible(visible);
	}
}