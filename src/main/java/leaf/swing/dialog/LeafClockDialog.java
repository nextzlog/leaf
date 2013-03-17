/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TimeZone;
import java.text.DateFormat;

import leaf.swing.com.LeafClockPane;

/**
 * 世界時計の表示機能を持つモーダレス時計ダイアログです。
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.2 作成：2011年1月5日
 * @see LeafClockPane
 */
@SuppressWarnings("serial")
public final class LeafClockDialog extends LeafDialog {
	private TimeZone timezone, utczone;
	private final LeafClockPane clock;
	private ClockLabel lbtime, lbutc;
	private JButton bclose;
	private JComboBox<String> combo;
	private JLabel lbzone;
	private JPanel panel_info;
	private Timer timer;
	private final String[] ids;
	
	/**
	 * 親フレームと指定してダイアログを生成します。
	 *
	 * @param owner 親フレーム
	 */
	public LeafClockDialog(Frame owner){
		this(owner, null);
	}
	
	/**
	 * 親ダイアログを指定してダイアログを生成します。
	 *
	 * @param owner 親ダイアログ
	 */
	public LeafClockDialog(Dialog owner){
		this(owner, null);
	}
	
	/**
	 * 親フレームとタイムゾーンを指定してダイアログを生成します。
	 *
	 * @param owner 親フレーム
	 * @param tz デフォルトのタイムゾーン
	 */
	public LeafClockDialog(Frame owner, TimeZone tz){
		super(owner, false);
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
		initialize();
	}
	
	/**
	 * 親ダイアログとタイムゾーンを指定してダイアログを生成します。
	 *
	 * @param owner 親ダイアログ
	 * @param tz デフォルトのタイムゾーン
	 */
	public LeafClockDialog(Dialog owner, TimeZone tz){
		super(owner, false);
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
		initialize();
	}
	
	/**
	 * ダイアログの表示と配置を初期化します。
	 */
	@Override public void initialize(){
		setTitle(translate("title"));
		getContentPane().removeAll();
		
		add(clock);
		
		panel_info = new JPanel(new GridLayout(4, 1));
		panel_info.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			translate("panel_information")
		));
		add(panel_info);
		
		panel_info.add(lbzone = new JLabel());
		panel_info.add(lbtime = new ClockLabel(timezone));
		
		panel_info.add(new JLabel(utczone.getDisplayName()));
		panel_info.add(lbutc = new ClockLabel(utczone));
		
		combo = new JComboBox<>(ids);
		combo.setSelectedItem(timezone.getID());
		add(combo);
		
		combo.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				setTimeZone(TimeZone.getTimeZone(
					(String)combo.getSelectedItem()));
			}
		});
		
		bclose = new JButton(translate("button_close"));
		bclose.setMnemonic(KeyEvent.VK_C);
		add(bclose);
		
		bclose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				stop();
				dispose();
			}
		});
		
		layoutComponents();
		
		setTimeZone(TimeZone.getDefault());
		start();
	}
	
	private void layoutComponents() {
		clock.setBounds(5, 15, 150, 150);
		panel_info.setBounds(160, 10, 210, 155);
		
		int pref = bclose.getPreferredSize().height;
		combo.setBounds(5, 180, 150, pref);
		bclose.setBounds(270, 180, 100, pref);
		
		setContentSize(new Dimension(380, 190 + pref));
	}
	
	/**
	 * 選択されているタイムゾーンを返します。
	 *
	 * @return 時計のタイムゾーン
	 */
	public TimeZone getTimeZone(){
		return timezone;
	}
	
	/**
	 *タイムゾーンを指定してダイアログを更新します。
	 *
	 *@param tz タイムゾーン
	 */
	public void setTimeZone(TimeZone tz){
		clock.setTimeZone(timezone = tz);
		lbzone.setText(tz.getDisplayName());
		lbtime.setTimeZone(tz);
	}
	
	private class ClockLabel extends JLabel {
		private TimeZone zone;
		private final DateFormat format;
		
		public ClockLabel(TimeZone zone){
			format = DateFormat.getDateTimeInstance(
				DateFormat.FULL, DateFormat.MEDIUM);
			setTimeZone(zone);
		}
		
		public void setTimeZone(TimeZone zone){
			format.setTimeZone(this.zone = zone);
		}
		
		public void update(){
			setText(format.format(
				Calendar.getInstance(zone).getTime()));
		}
	}
	
	private class ClockTimer extends TimerTask{
		public void run(){
			if(LeafClockDialog.this.isVisible()){
				lbtime.update();
				lbutc.update();
			}
		}
	}
	
	private void start(){
		try{
			timer = new Timer();
			timer.schedule(new ClockTimer(), 0, 1000);
			clock.start();
		}catch(NullPointerException ex){}
	}
	
	private void stop(){
		try{
			clock.stop();
			timer.cancel();
			timer = null;
		}catch(NullPointerException ex){}
	}
	
	/**
	 * ダイアログの表示と非表示を切り替えます。
	 *
	 * @param visible 表示する場合true
	 */
	public void setVisible(boolean visible){
		if(visible) start();
		else stop();
		super.setVisible(visible);
	}
}