/**********************************************************************************
 * leaf - Java Library for pafelog
 * Copyright(c) 2010 - 2013 by University of Tokyo Amateur Radio Club
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL) as published by the
 * Free Software Foundation, either version 3 of the License, or(at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************/
package leaf.swing;

import java.awt.*;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

/**
 * 短針・長針・秒針で構成されるアナログ時計の実装です。
 *
 * @author 東大アマチュア無線クラブ
 * @since 2010年10月30日
 */
public final class LeafClockPane extends JComponent {
	private static final long serialVersionUID = 1L;
	private final double rad = Math.PI / 180;
	private final Color dark;
	private final Color bright;
	private TimeZone zone;
	private Calendar date;
	private Timer timer;

	/**
	 * デフォルトのタイムゾーンでアナログ時計を構築します。
	 */
	public LeafClockPane() {
		this(null);
	}

	/**
	 * タイムゾーンを指定してアナログ時計を生成します。
	 *
	 * @param zone タイムゾーン
	 */
	public LeafClockPane(TimeZone zone) {
		setBackground(dark = Color.BLACK);
		setForeground(bright = Color.WHITE);
		setPreferredSize(new Dimension(120, 120));
		setMinimumSize(new Dimension(100, 100));
		this.zone = (zone != null) ?
				zone : TimeZone.getDefault();
		date = Calendar.getInstance(this.zone);
	}

	/**
	 * アナログ時計のタイムゾーンを返します。
	 *
	 * @return タイムゾーン
	 */
	public TimeZone getTimeZone() {
		return zone;
	}

	/**
	 * アナログ時計にタイムゾーンを設定します。
	 *
	 * @param zone タイムゾーン
	 */
	public void setTimeZone(TimeZone zone) {
		this.zone = zone;
	}

	/**
	 * この時計を描画します。
	 *
	 * @param g グラフィックス
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON
		);

		double h = date.get(Calendar.HOUR);
		double m = date.get(Calendar.MINUTE);
		double s = date.get(Calendar.SECOND);

		g2.setPaint(getForeground());

		int radiusx = getWidth() / 2;
		int radiusy = getHeight() / 2;
		int mg = radiusx / 20;

		/*時計の枠線*/
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setPaint(getBackground());

		g2.fillRect(mg, mg, (radiusx - mg) * 2, (radiusy - mg) * 2);
		g2.setPaint(getForeground());

		/*時計の中心の丸*/
		g2.fillOval(
				(int) (radiusx * 0.95),
				(int) (radiusy * 0.95),
				(int) (radiusx * 0.1),
				(int) (radiusy * 0.1)
		);

		int fsize = Math.min(radiusx, radiusy) / 4;
		g2.setFont(getFont().deriveFont((float) fsize));
		FontMetrics met = g2.getFontMetrics();

		/*文字盤*/
		for (int i = 1; i <= 12; i++) {
			double radian = i * 30 * rad;
			int dx = (int) (radiusx * (1 + Math.sin(radian) * 0.8));
			int dy = (int) (radiusy * (1 - Math.cos(radian) * 0.8));
			String text = String.valueOf(i);
			g2.drawString(text,
					dx - (met.stringWidth(text)) / 2, dy + fsize / 2);
		}

		/*短針*/
		double hrad = (h * 30 + m / 2) * rad;
		int[] hxs = {
				radiusx,
				(int) (radiusx * (1 + Math.sin(hrad - 0.2) / 3)),
				(int) (radiusx * (1 + Math.sin(hrad) * 2 / 3)),
				(int) (radiusx * (1 + Math.sin(hrad + 0.2) / 3))
		};
		int[] hys = {
				radiusy,
				(int) (radiusy * (1 - Math.cos(hrad - 0.1) / 3)),
				(int) (radiusy * (1 - Math.cos(hrad) * 2 / 3)),
				(int) (radiusy * (1 - Math.cos(hrad + 0.1) / 3))
		};

		g2.fillPolygon(hxs, hys, 4);

		/*長針*/
		double mrad = (m * 6 + s / 10) * rad;
		int[] mxs = {
				radiusx,
				(int) (radiusx * (1 + Math.sin(mrad - 0.1) / 3)),
				(int) (radiusx * (1 + Math.sin(mrad) * 0.8)),
				(int) (radiusx * (1 + Math.sin(mrad + 0.1) / 3))
		};
		int[] mys = {
				radiusy,
				(int) (radiusy * (1 - Math.cos(mrad - 0.1) / 3)),
				(int) (radiusy * (1 - Math.cos(mrad) * 0.8)),
				(int) (radiusy * (1 - Math.cos(mrad + 0.1) / 3))
		};

		g2.fillPolygon(mxs, mys, 4);

		/*秒針*/
		double srad = s * 6 * rad;
		int sx = (int) (radiusx * (1 + Math.sin(srad) * 0.8));
		int sy = (int) (radiusy * (1 - Math.cos(srad) * 0.8));

		g2.drawLine(radiusx, radiusy, sx, sy);
	}

	/**
	 * 時計の自動的な再描画を開始します。
	 */
	public void start() {
		timer = new Timer();
		timer.schedule(new RepaintTimer(), 0, 1000);
	}

	/**
	 * 時計の自動的な再描画を停止します。
	 */
	public void stop() {
		if (timer != null) timer.cancel();
		timer = null;
	}

	private class RepaintTimer extends TimerTask {
		public void run() {
			if (!isShowing()) return;
			date = Calendar.getInstance(zone);
			int h = date.get(Calendar.HOUR_OF_DAY);
			setBackground((h < 6 || h >= 18) ? dark : bright);
			setForeground((h < 6 || h >= 18) ? bright : dark);
			repaint();
		}
	}
}