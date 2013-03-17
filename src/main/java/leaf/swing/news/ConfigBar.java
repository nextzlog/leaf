/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.news;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.*;

/**
 * ニュースリーダーの設定画面の実装です。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年5月4日
 */
@SuppressWarnings("serial")
final class ConfigBar extends JComponent {
	private boolean isExpanded = false;
	private JTextField urlfld;
	private JButton bok, bcancel;
	private JToolBar box;
	private Timer animator;
	private URL url;
	
	public ConfigBar(final LeafNewsBar bar) {
		super();
		setLayout(new ExpandLayout());
		
		box = new JToolBar();
		box.setFloatable(false);
		box.setLayout(new ToolBarLayout());
		add(box, BorderLayout.CENTER);
		
		box.add(new JLabel(" URL "));
		box.add(urlfld = new JTextField());
		
		box.add(bok =  new NotBorderedButton("button_ok"));
		bok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				expand(false);
				try{
					String url = urlfld.getText();
					bar.setURL(url.isEmpty()? null : url);
					bar.update();
				}catch(MalformedURLException ex) {}
			}
		});
		box.add(bcancel = new NotBorderedButton("button_cancel"));
		bcancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				expand(false);
				bar.setURL(url);
			}
		});
	}
	
	public void setURL(URL url) {
		urlfld.setText(url != null? url.toString() : "");
		this.url = url;
	}
	
	private class ToolBarLayout extends BoxLayout {
		public ToolBarLayout() {
			super(box, BoxLayout.X_AXIS);
		}
		
		@Override
		public Dimension maximumLayoutSize(Container target) {
			return target.getMaximumSize();
		}
	}
	
	private class ExpandLayout extends BorderLayout {
		private int h = 0, ph = 0;
		public ExpandLayout() {
			super(2, 2);
		}
		
		@Override
		public Dimension preferredLayoutSize(Container target) {
			Dimension ps = super.preferredLayoutSize(target);
			ph = ps.height;
			if(animator != null) {
				if( isExpanded && getHeight() > 0 ) h -= 2;
				if(!isExpanded && getHeight() < ph) h += 2;
				if( h <= 0 || h >= ph ) {
					animator.stop();
					animator = null;
					if(h <= 0) h = 0;
					else{
						h = ph;
						urlfld.requestFocusInWindow();
					}
					isExpanded = h != 0;
				}
			}
			ps.height = h;
			return ps;
		}
	}
	
	public void expand(boolean exp) {
		if(isExpanded == exp) return;
		if(animator != null && animator.isRunning()) return;
		isExpanded = getHeight() != 0;
		animator = new Timer(5, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				revalidate();
			}
		});
		animator.start();
	}
}
