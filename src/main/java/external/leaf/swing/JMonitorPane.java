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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComponent;

/**
 * 数値をリアルタイムに監視するユーザーインターフェースです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2011年7月23日
 */
public abstract class JMonitorPane extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final int BAR_CELL_WIDTH        = 20;
	private static final int BAR_NUMBER_OF_CELLS   = 20;
	private static final int GRAPH_GRID_INTERVAL   = 20;
	private static final int GRAPH_NUMBER_OF_GRIDS = 20;
	private static final int MARGIN = 5;
	
	private final int step;
	
	private BufferedImage image;
	private Color gridColor = Color.GRAY;
	private Graphics2D graphics;
	private Line2D tangent;
	private Rectangle mfrect, murect;
	private Timer timer;
	private int interval = 100, sample, bufindex = 0, column = 0;
	private Dimension size;
	private int[] buffer = new int[100];
	
	/**
	 * 100段階表示の{@link JMonitorPane}を作成します。
	 */
	public JMonitorPane(){
		this(100);
	}
	
	/**
	 * 観測対象量の量子化段階数を指定して{@link JMonitorPane}を作成します。
	 * 
	 * @param step 量子化段階数
	 */
	public JMonitorPane(final int step){
		super();
		this.step = step;
		setForeground(Color.GREEN);
		setBackground(Color.BLACK);
		
		tangent = new Line2D.Float();
		mfrect  = new Rectangle();
		murect  = new Rectangle();
		
		setPreferredSize(new Dimension(200, 110));
		addComponentListener(new ResizeListener());
	}
	
	private class ResizeListener extends ComponentAdapter{
		@Override
		public void componentResized(ComponentEvent e){
			final int width = getWidth(), height = getHeight();
			if(width > 0 && height > 0){
				size = new Dimension(width, height);
				image = (BufferedImage) createImage(width, height);
				if(image != null)graphics = image.createGraphics();
			}
		}
	}
	
	/**
	 * モニターの表示を初期化します。
	 */
	public void clear() {
		clear(buffer.length);
	}
	
	private void clear(int width) {
		buffer = new int[width];
		bufindex = 0;
	}
	
	/**
	 * モニターによって自動的にトリガーされ、観測対象量をサンプリングします。
	 * 
	 * @return モニターが定期的にサンプリングする量
	 */
	protected abstract int sample();
	
	/**
	 * モニターのサンプリング周期をミリ秒単位で設定します。
	 * 
	 * @param ms サンプリング周期
	 * @throws IllegalArgumentException 正数でない周期を指定した場合
	 */
	public void setSamplingInterval(int ms) throws IllegalArgumentException{
		final int old = this.interval;
		if(ms > 0) this.interval = ms;
		else throw new IllegalArgumentException("not positive : " + ms);
		firePropertyChange("samplingInterval", old, ms);
	}
	
	/**
	 * モニターのサンプリング周期の設定値をミリ秒単位で返します。
	 * 
	 * @return サンプリング周期
	 */
	public int getSamplingInterval(){
		return interval;
	}
	
	/**
	 * モニターのグリッドの表示色を設定します。
	 * 
	 * @param color グリッドを表示するのに用いる色
	 */
	public void setGridColor(Color color){
		final Color old = gridColor;
		this.gridColor = (color == null)? Color.WHITE : color;
		firePropertyChange("gridColor", old, color);
	}
	
	/**
	 * モニターのグリッドの表示色を返します。
	 * 
	 * @return グリッドを表示するのに用いる色
	 */
	public Color getGridColor(){
		return gridColor;
	}
	
	/**
	 * モニターの自動的なサンプリング動作を開始または停止します。
	 * 
	 * @param b 開始する場合は真 停止する場合は偽
	 */
	public synchronized void setAutoSamplingEnabled(boolean b){
		if(b != (timer == null)) return;
		if(b){
			timer = new Timer(true);
			timer.schedule(new AutoSamplingTask(), 0, interval);
		}else{
			timer.cancel();
			timer = null;
		}
		firePropertyChange("autoSamplingEnabled", !b, b);
	}
	
	/**
	 * モニターの自動的なサンプリング動作が稼働中であるか返します。
	 * 
	 * @return 稼働中である場合は真 停止中である場合は偽
	 */
	public boolean isAutoSamplingEnabled(){
		return timer != null;
	}
	
	private class AutoSamplingTask extends TimerTask{
		@Override
		public void run(){
			sample = Math.min(step, Math.max(0, sample()));
			buffer[bufindex] = sample = step - sample;
			if(++bufindex == buffer.length)bufindex = 0;
			if(column == 0)column = GRAPH_GRID_INTERVAL;
			if(isShowing()) repaint();
			column--;
		}
	}
	
	/**
	 * モニターを描画します。
	 * 
	 * @param g モニタを描画するのに用いるグラフィックス
	 */
	@Override
	protected void paintComponent(Graphics g){
		if(image != null){
			graphics.setColor(getBackground());
			graphics.fillRect(0, 0, size.width, size.height);
			graphics.setColor(getForeground());
			
			paintBarGraph(sample);
			paintCurvedGraph();
			
			g.drawImage(image, 0, 0, this);
		}else super.paintComponent(g);
	}
	
	private void paintBarGraph(final int value){
		final int height = size.height - MARGIN;
		final int cellHeight  = height / BAR_NUMBER_OF_CELLS;
		final int filledLevel = BAR_NUMBER_OF_CELLS * value / step;
		
		mfrect.setSize(BAR_CELL_WIDTH - 1, cellHeight);
		murect.setSize(BAR_CELL_WIDTH, cellHeight - 1);
		graphics.setColor(getForeground());
		
		int i = 0;
		for(; i < filledLevel; i++){
			mfrect.setLocation(MARGIN, MARGIN + i * cellHeight);
			graphics.draw(mfrect);
		}
		for(; i < BAR_NUMBER_OF_CELLS; i++){
			murect.setLocation(MARGIN, MARGIN + i * cellHeight);
			graphics.fill(murect);
		}
	}
	
	private void paintCurvedGraph(){
		final int height = size.height - MARGIN;
		final int gridHeight = height / GRAPH_NUMBER_OF_GRIDS;
		if(gridHeight > 0){
			final int x = BAR_CELL_WIDTH + 10;
			final int y = MARGIN;
			final int w = size.width - x - MARGIN;
			final int h = gridHeight * GRAPH_NUMBER_OF_GRIDS;
			
			paintGrid(gridHeight, x, y, w, h);
			
			if(w > 0 && w != buffer.length) clear(w);
			paintCurve(x, y, w, h);
		}
	}
	
	private void paintGrid(int gridHeight, int x, int y, int w, int h){
		graphics.setColor(gridColor);
		graphics.drawRect(x, y, w, h);
		for(int n = y; n <= h; n += gridHeight){
			tangent.setLine(x, n, x + w, n);
			graphics.draw(tangent);
		}
		for(int n = x + column; n < w + x; n += GRAPH_GRID_INTERVAL){
			tangent.setLine(n, y, n, y + h);
			graphics.draw(tangent);
		}
	}
	
	private void paintCurve(int x, int y, int w, int h){
		graphics.setColor(getForeground());
		buffer[bufindex] = 0;
		for(int n = x, m = bufindex; n < x + w; n++, m++){
			if(m == buffer.length) m = 0;
			int buf1 = (m >= 0)? buffer[m]   : buffer[w-1];
			int buf2 = (m >= 1)? buffer[m-1] : buffer[w-1];
			
			if(buf1 == 0 || buf2 == 0) continue;
			if(buf1 == buf2) graphics.fillRect(n, y+buf1*h/step, 1, 1);
			else graphics.drawLine(n-1, y+buf2*h/step, n, y+buf1*h/step);
		}
	}

}