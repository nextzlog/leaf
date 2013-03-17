/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.com;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.Timer;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

/**
 * タスクが進行中であることを視覚的に表示するラベルです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年5月23日
 *
 */
public class LeafBusyLabel extends JLabel{
	private static final long serialVersionUID = 1L;
	private final IndicatorIcon icon;
	private final Timer timer;
	
	/**
	 * ラベルを構築します。
	 */
	public LeafBusyLabel() {
		icon  = new IndicatorIcon();
		addComponentListener(icon);
		timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				icon.next();
				repaint();
			}
		});
		setForeground(Color.DARK_GRAY);
		icon.update(2d);
		setIcon(icon);
	}
	
	private class IndicatorIcon extends ComponentAdapter implements Icon {
		private java.util.List<Shape> list;
		private double sx = 0d, sy = 0d;
		private boolean running;
		private Dimension dim;
		
		protected void update(double r) {
			Dimension dim = new Dimension((int)(r*8+sx*2),(int)(r*8+sy*2));
			if(r>0 && !dim.equals(this.dim)) {
				this.dim = dim;
				list = new LinkedList<Shape>(Arrays.asList(
					new Ellipse2D.Double(sx+3*r, sy+0*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+5*r, sy+1*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+6*r, sy+3*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+5*r, sy+5*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+3*r, sy+6*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+1*r, sy+5*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+0*r, sy+3*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+1*r, sy+1*r, 2*r, 2*r)
				));
			}
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
			if(isShowing()) {
				int r = Math.max(getHeight(), getWidth()) >> 3;
				update((double)r);
			}
		}
		
		@Override
		public void componentShown(ComponentEvent e) {
			int r = Math.max(getHeight(), getWidth()) >> 3;
			update((double)r);
		}
		
		public void next() {
			if(running) list.add(list.remove(0));
		}
		
		@Override
		public void paintIcon(Component comp,Graphics g,int x,int y) {
			if(running) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setPaint(new Color(0,0,0,0));
				g2.fillRect(x,y,getIconWidth(),getIconHeight());
				g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
				g2.setColor(getForeground());
				g2.translate(x,y);
				float alpha = 0.0f;
				for(Shape s:list) {
					g2.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, alpha));
					alpha += 0.125f;
					g2.fill(s);
				}
				g2.translate(-x,-y);
			}
		}
		
		@Override
		public int getIconWidth() {
			return dim.width;
		}
		
		@Override
		public int getIconHeight() {
			return dim.height;
		}
	}
	
	/**
	 * インジケータの状態を返します。
	 * 
	 * @return インジケータが回転中の場合、trueを返します。
	 */
	public boolean isRunning() {
		return icon.running;
	}
	
	/**
	 * インジケータの回転を開始します。
	 */
	public void start() {
		icon.running = true;
		timer.start();
	}
	
	/**
	 * インジケータの回転を終了します。
	 */
	public void stop() {
		icon.running = false;
		timer.stop();
	}
}