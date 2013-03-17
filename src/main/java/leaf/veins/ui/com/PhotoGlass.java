/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.com;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;

import leaf.icon.BusyIcon;

import static java.awt.AlphaComposite.SRC_OVER;
import static java.awt.event.HierarchyEvent.DISPLAYABILITY_CHANGED;

/**
 * ガラスレイヤー上に表示される画像表示コンポーネントです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/04/30 設計：2010/04/12
 *
 */
@SuppressWarnings("serial")
public class PhotoGlass extends JComponent {
	private final Color screenColor;
	private final Color windowColor;
	private final BusyIcon indicator;
	private final Rectangle rect;
	private int width = 0, height = 0;
	private float alpha = 0f;
	private ImageIcon image;
	private String name;
	private Timer timer;
	
	/**
	 * 指定されたURLの画像を表示するコンポーネントを構築します。
	 * 
	 * @param url 画像のURL
	 */
	public PhotoGlass(URL url) {
		rect = new Rectangle();
		indicator = new BusyIcon();
		
		screenColor = new Color(70, 70, 70, 140);
		windowColor = Color.WHITE;
		
		image = new ImageIcon(url);
		name  = url.getFile();
		
		setOpaque(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		addMouseListener(new MouseMotionHandler());
		addHierarchyListener(new HierarchyHandler());
	}
	
	private class MouseMotionHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			setVisible(false);
		}
	}
	
	private class HierarchyHandler implements HierarchyListener {
		@Override
		public void hierarchyChanged(HierarchyEvent e) {
			if((e.getChangeFlags() & DISPLAYABILITY_CHANGED) != 0
			&& timer != null && !isDisplayable()) {
				timer.stop();
			}
		}
	}
	
	private class ExpandTask implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			indicator.next();
			repaint();
		}
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		JRootPane root = SwingUtilities.getRootPane(this);
		if(root != null && isVisible() != visible) {
			root.getLayeredPane().setVisible(!visible);
		}
		
		if(visible && (timer == null || !timer.isRunning())) {
			width = height = 40;
			alpha = 0f;
			timer = new Timer(10, new ExpandTask());
			timer.start();
		} else if(timer!=null) timer.stop();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		JRootPane root = SwingUtilities.getRootPane(this);
		if(root != null) root.getLayeredPane().print(g);
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		if(image == null) return;
		
		FontMetrics met = g2.getFontMetrics();
		int inset = met.getHeight();
		
		if (height < image.getIconHeight() + 20 + inset) {
			height += image.getIconHeight() / 16;
		} else if (width < image.getIconWidth() + 20) {
			height = image.getIconHeight() + 20 + inset;
			width += image.getIconWidth() / 8;
		} else if (alpha < 1f) {
			width =  image.getIconWidth() + 20;
			alpha += 0.05f;
		} else timer.stop();
		
		rect.setSize(width, height);
		Rectangle screen = getBounds();
		rect.x = screen.x + (screen.width  - rect.width ) /2;
		rect.y = screen.y + (screen.height - rect.height) /2;
		
		g2.setPaint(screenColor);
		g2.fill(screen);
		g2.setPaint(windowColor);
		g2.fill(rect);
		
		if(alpha > 0) {
			if(alpha > 1f) alpha = 1f;
			g2.setComposite(AlphaComposite.getInstance(SRC_OVER, alpha));
			g2.drawImage(image.getImage(),
				rect.x + 10, rect.y + 10,
				image.getIconWidth(), image.getIconHeight(), this);
			g2.setPaint(Color.BLACK);
			g2.drawString(name,
				rect.x + (rect.width - met.stringWidth(name)) / 2,
				rect.y + rect.height - inset + 5);
		} else {
			indicator.paintIcon(this, g2,
				screen.x + (screen.width  - indicator.getIconWidth() ) / 2,
				screen.y + (screen.height - indicator.getIconHeight()) / 2);
		}
		g2.setComposite(AlphaComposite.getInstance(SRC_OVER, 1f));
	}

}
