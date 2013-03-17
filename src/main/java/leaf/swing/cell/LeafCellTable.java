/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.cell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;

/**
 * 二次元セルオートマータを視覚化するGUIコンポーネントです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.4 作成：2012年3月16日
 *
 */
public class LeafCellTable extends JComponent {
	private static final long serialVersionUID = 1L;
	private Automata automata;
	private Dimension cellsize;
	private Image screen;
	private Graphics graph;
	
	private Timer timer;
	private int interval = 100;
	
	/**
	 * コンポーネントを構築します。
	 */
	public LeafCellTable() {
		setBackground(Color.BLACK);
		cellsize = new Dimension(10, 10);
		addMouseListener(new ClickListener());
	}
	
	private final class ClickListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if(automata != null && !automata.isUpdating()) {
				final Dimension size = getPreferredSize();
				final int cw = cellsize.width;
				final int ch = cellsize.height;
				final int tw = size.width;
				final int th = size.height;
				int ex = e.getX() - (getWidth () - tw) / 2;
				int ey = e.getY() - (getHeight() - th) / 2;
				if (ex >= 0 && ex < tw && ey >= 0 && ey < th) {
					automata.cellPressed(ex / cw, ey / ch);
					repaint();
				}
			}
		}
	}
	
	/**
	 * このテーブルコンポーネントにセルオートマータを設定します。
	 * 
	 * @param automata セルオートマータ
	 */
	public void setCellAutomata(Automata automata) {
		this.automata = automata;
		updatePreferredSize(cellsize);
		repaint();
	}
	
	/**
	 * このテーブルコンポーネントのセルオートマータを返します。
	 * 
	 * @return セルオートマータ
	 */
	public Automata getCellAutomata() {
		return automata;
	}
	
	/**
	 * このテーブルコンポーネントの1セルの大きさを設定します。
	 * 
	 * @param cellsize 1セルの大きさ
	 */
	public void setCellSize(Dimension cellsize) {
		final Dimension old = this.cellsize;
		firePropertyChange("cellSize", old, cellsize);
		updatePreferredSize(cellsize);
	}
	
	private void updatePreferredSize(Dimension cellsize) {
		if(automata != null && cellsize != null) {
			final int aw = automata.getWidth();
			final int ah = automata.getHeight();
			final int tw = aw * cellsize.width;
			final int th = ah * cellsize.height;
			setPreferredSize(new Dimension(tw, th));
			this.cellsize = cellsize;
		}
	}
	
	/**
	 * セルオートマータのテーブルを初期化します。
	 */
	public final void init() {
		if(automata != null) automata.init();
		repaint();
	}
	
	/**
	 * セルオートマータを次の世代に移行します。
	 */
	public void updateNext() {
		automata.updateNext();
		repaint();
	}
	
	/**
	 * セルテーブルの世代更新周期をミリ秒単位で設定します。
	 * 
	 * @param ms 世代更新周期
	 * @throws IllegalArgumentException 正数でない周期を指定した場合
	 */
	public void setAutoUpdateInterval(int ms) throws IllegalArgumentException{
		final int old = this.interval;
		if(ms > 0) this.interval = ms;
		else throw new IllegalArgumentException("not positive : " + ms);
		firePropertyChange("autoUpdateInterval", old, ms);
	}
	
	/**
	 * セルテーブルの世代更新周期の設定値をミリ秒単位で返します。
	 * 
	 * @return 世代更新周期
	 */
	public int getAutoUpdateInterval() {
		return interval;
	}
	
	/**
	 * セルテーブルの自動的な世代更新を開始または停止します。
	 * 
	 * @param b 開始する場合はtrue 停止する場合はfalse
	 */
	public synchronized void setAutoUpdateEnabled(boolean b) {
		if(b != (timer == null)) return;
		if(b) {
			timer = new Timer(true);
			timer.schedule(new AutoUpdateTask(), 0, interval);
		} else {
			timer.cancel();
			timer = null;
			automata.setUpdating(false);
		}
		firePropertyChange("autoUpdateEnabled", !b, b);
	}
	
	/**
	 * セルテーブルの自動的な世代更新動作が稼働中であるか返します。
	 * 
	 * @return 稼働中である場合はtrue 停止中である場合はfalse
	 */
	public boolean isAutoUpdateEnabled() {
		return timer != null;
	}
	
	private class AutoUpdateTask extends TimerTask {
		@Override
		public void run() {
			try{
				updateNext();
			}catch(IllegalArgumentException ex) {}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		final Dimension size = getPreferredSize();
		if(screen == null) {
			screen = createImage(size.width, size.height);
			graph  = screen.getGraphics();
		}
		
		graph.setColor(getBackground());
		graph.fillRect(0, 0, size.width, size.height);
		
		if(automata == null) return;
		
		final int cw = cellsize.width;
		final int ch = cellsize.height;
		final int iw = (getWidth () - size.width ) / 2;
		final int ih = (getHeight() - size.height) / 2;
		
		for(int x = 0; x < automata.getWidth() ; x++) {
		for(int y = 0; y < automata.getHeight(); y++) {
			graph.setColor(automata.getCellColor(x, y));
			graph.fillRect(x * cw, y * ch, cw-1, ch-1);
		}
		}
		g.clearRect(iw, ih, size.width, size.height);
		g.drawImage(screen, iw, ih, this);
	}

}
