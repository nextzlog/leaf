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
package leaf.components;

import java.awt.*;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TimeZone;
import javax.swing.*;

import leaf.dialog.LeafDialog;
import leaf.manager.LeafLangManager;

/**
*短針・長針・秒針からなるアナログ時計の実装です。<br>
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年10月30日
*/
public final class LeafClockPane extends JComponent{
	
	private Timer timer;
	private TimeZone zone;
	private Calendar date;
	private final double rad = Math.PI / 180;
	
	private Color dark, bright;
	
	/**
	*アナログ時計を生成します。
	*/
	public LeafClockPane(){
		this(null);
	}
	/**
	*タイムゾーンを指定してアナログ時計を生成します。
	*@param zone タイムゾーン
	*/
	public LeafClockPane(TimeZone zone){
		setBackground(dark   = Color.BLACK);
		setForeground(bright = Color.WHITE);
		setPreferredSize(new Dimension(120,120));
		setMinimumSize(new Dimension(100,100));
		this.zone = (zone != null)? zone : TimeZone.getDefault();
		timer = new Timer();
		timer.schedule(new ExTimer(),0,1000);
	}
	/**
	*時計を表示するモーダレスなダイアログを生成して返します。
	*@param owner 親フレーム
	*@return ダイアログ
	*/
	public LeafDialog createDialog(Frame owner){
		LeafDialog dialog = new LeafDialog(
			owner,LeafLangManager.get("Clock","時計"),false
		);
		dialog.add(this,BorderLayout.CENTER);
		dialog.pack();
		return dialog;
	}
	/**
	*時計を表示するモーダレスなダイアログを生成して返します。
	*@param owner 親ファイアログ
	*@return ダイアログ
	*/
	public LeafDialog createDialog(Dialog owner){
		LeafDialog dialog = new LeafDialog(
			owner,LeafLangManager.get("Clock","時計"),false
		);
		dialog.add(this,BorderLayout.CENTER);
		dialog.pack();
		return dialog;
	}
	/**
	*アナログ時計にタイムゾーンを設定します。
	*@param zone タイムゾーン
	*/
	public void setTimeZone(TimeZone zone){
		this.zone = zone;
	}
	/**
	*アナログ時計のタイムゾーンを返します。
	*@return タイムゾーン
	*/
	public TimeZone getTimeZone(){
		return zone;
	}
	/**
	*この時計を描画します。
	*@param g グラフィックス
	*/
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON
		);
		
		double h = date.get(date.HOUR);
		double m = date.get(date.MINUTE);
		double s = date.get(date.SECOND);
		
		g2.setPaint(getForeground());
		
		int radiusx = getWidth()/2;
		int radiusy = getHeight()/2;
		
		/*時計の枠線*/
		g2.fill3DRect(0,0,getWidth(),getHeight(),true);
		g2.setPaint(getBackground());
		
		g2.fillRect(
			(int)(radiusx*0.05),
			(int)(radiusy*0.05),
			(int)(radiusx*1.9),
			(int)(radiusy*1.9)
		);
		
		g2.setPaint(getForeground());
		
		/*時計の中心の丸*/
		g2.fillOval(
			(int)(radiusx*0.95),
			(int)(radiusy*0.95),
			(int)(radiusx*0.1),
			(int)(radiusy*0.1)
		);
		
		int fsize = Math.min(radiusx,radiusy)/4;
		g2.setFont(getFont().deriveFont((float)fsize));
		FontMetrics met = g2.getFontMetrics();
		
		/*文字盤*/
		for(int i=1;i<=12;i++){
			double radian = i*30*rad;
			int dx = (int)(radiusx*(1+Math.sin(radian)*0.8));
			int dy = (int)(radiusy*(1-Math.cos(radian)*0.8));
			String text = String.valueOf(i);
			g2.drawString(text,dx-(met.stringWidth(text))/2,dy+fsize/2);
		}
		
		/*短針*/
		double hrad = (h*30 + m/2)*rad;
		int[] hxs = {
			radiusx,
			(int)(radiusx*(1+Math.sin(hrad-0.2)/3)),
			(int)(radiusx*(1+Math.sin(hrad)*2/3)),
			(int)(radiusx*(1+Math.sin(hrad+0.2)/3))
		};
		int[] hys = {
			radiusy,
			(int)(radiusy*(1-Math.cos(hrad-0.1)/3)),
			(int)(radiusy*(1-Math.cos(hrad)*2/3)),
			(int)(radiusy*(1-Math.cos(hrad+0.1)/3))
		};
		
		g2.fillPolygon(hxs,hys,4);
		
		/*長針*/
		double mrad = (m*6 + s/10)*rad;
		int[] mxs = {
			radiusx,
			(int)(radiusx*(1+Math.sin(mrad-0.1)/3)),
			(int)(radiusx*(1+Math.sin(mrad)*0.8)),
			(int)(radiusx*(1+Math.sin(mrad+0.1)/3))
		};
		int[] mys = {
			radiusy,
			(int)(radiusy*(1-Math.cos(mrad-0.1)/3)),
			(int)(radiusy*(1-Math.cos(mrad)*0.8)),
			(int)(radiusy*(1-Math.cos(mrad+0.1)/3))
		};
		
		g2.fillPolygon(mxs,mys,4);
		
		/*秒針*/
		double srad = s*6*rad;
		int sx = (int)(radiusx*(1+Math.sin(srad)*0.8));
		int sy = (int)(radiusy*(1-Math.cos(srad)*0.8));
		
		g2.drawLine(radiusx,radiusy,sx,sy);
	}
	/**
	*１秒ごとに再描画します。
	*/
	private class ExTimer extends TimerTask{
		public void run(){
			if(isVisible()){
				date  = Calendar.getInstance(zone);
				int h = date.get(Calendar.HOUR_OF_DAY);
				setBackground((h<6 || h>=18)? dark : bright);
				setForeground((h<6 || h>=18)? bright : dark);
				repaint();
			}
		}
	}
	/**
	*昼間前景色および夜間背景色を設定します。
	*@param color 色
	*/
	public void setDarkColor(Color color){
		dark = color;
	}
	/**
	*夜間前景色および昼間背景色を設定します。
	*@param color 色
	*/
	public void setBrightColor(Color color){
		bright = color;
	}
	/**
	*昼間前景色および夜間背景色を返します。
	*@return 色
	*/
	public Color getDarkColor(){
		return dark;
	}
	/**
	*夜間前景色および昼間背景色を返します。
	*@return 色
	*/
	public Color getBrightColor(){
		return bright;
	}
	/**
	*スレッドを生成して計時を再開します。
	*/
	public void restart(){
		try{
			timer = new Timer();
			timer.schedule(new ExTimer(),0,1000);
		}catch(NullPointerException ex){}
	}
	/**
	*スレッドを破棄して時計を終了します。
	*/
	public void stop(){
		try{
			timer.cancel();
			timer = null;
		}catch(NullPointerException ex){}
	}
}