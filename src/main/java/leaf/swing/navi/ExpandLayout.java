/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.navi;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import static leaf.swing.navi.LeafExpandPane.TITLE_HEIGHT;

/**
 * {@link LeafExpandPane}の展開収納アニメーションを実現します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2011年11月12日
 *
 */
final class ExpandLayout extends BorderLayout {
	private static final long serialVersionUID = 1L;
	private final LeafExpandPane leafExpandPane;
	private int height;
	private int pheight;
	private Timer animator = null;
	private boolean isExpanded;
	private static final int STEP = 30;
	
	public ExpandLayout(LeafExpandPane leafExpandPane) {
		super(2, 2);
		this.leafExpandPane = leafExpandPane;
		this.isExpanded = leafExpandPane.isExpanded;
		this.height  = LeafExpandPane.TITLE_HEIGHT;
		this.pheight = LeafExpandPane.TITLE_HEIGHT;
	}
	
	@Override
	public Dimension preferredLayoutSize(Container target) {
		Dimension ps = super.preferredLayoutSize(target);
		pheight = ps.height;
		
		if(animator != null) {
			if(isExpanded) {
				if(leafExpandPane.getHeight() > TITLE_HEIGHT) {
					height -= pheight / STEP;
				}
			} else if(leafExpandPane.getHeight() < pheight) {
				height += pheight / STEP;
			}
			
			if(height <= TITLE_HEIGHT || height >= pheight) {
				animator.stop();
				animator = null;
				
				if(height > TITLE_HEIGHT) height = pheight;
				else height = TITLE_HEIGHT;
				
				isExpanded = height != TITLE_HEIGHT;
				if(!isExpanded) {
					leafExpandPane.remove(leafExpandPane.content);
				}
				
				SwingUtilities.invokeLater(new ScrollTask());
				
				leafExpandPane.firePropertyChange(
					"expanded", !isExpanded, isExpanded);
				leafExpandPane.fireExpandListeners();
			}
		}
		
		ps.height = height;
		return ps;
	}
	
	private class ScrollTask implements Runnable {
		@Override
		public void run() {
			Rectangle rect = leafExpandPane.content.getBounds();
			leafExpandPane.content.scrollRectToVisible(rect);
		}
	}
	
	private class ExpandTask implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ExpandLayout.this.leafExpandPane.revalidate();
		}
	}
	
	public void setExpanded(boolean expanded) {
		if(isExpanded != expanded && animator == null) {
			animator = new Timer(5, new ExpandTask());
			animator.start();
		}
	}
	
	public boolean isExpandingOrFolding() {
		return animator != null;
	}

}