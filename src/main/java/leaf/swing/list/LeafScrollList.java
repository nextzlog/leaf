/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.Vector;
import javax.swing.*;

/**
 *文字列を自動でスクロール表示する機能を持つリストの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年5月28日
 */
public class LeafScrollList extends JList{
	/**
	 *空のモデルでリストを生成します。
	 */
	public LeafScrollList(){
		super();
		init();
	}
	/**
	 *指定したモデルでリストを生成します。
	 *@param model リストモデル
	 */
	public LeafScrollList(ListModel model){
		super(model);
		init();
	}
	/**
	 *リストコンポーネントを初期化します。
	 */
	private void init(){
		setSelectionBackground(Color.BLACK);
		setSelectionForeground(Color.WHITE);
		setCellRenderer(new ScrollListCellRenderer(this));
	}
	/**
	 *スクロールするためのセルレンダラーの実装です
	 */
	private class ScrollListCellRenderer
	extends JPanel implements ListCellRenderer, HierarchyListener{
		private final javax.swing.Timer timer;
		private final ScrollLabel label;
		private boolean isRunning;
		private final Font font;
		private int index;
		private float xx;
		public ScrollListCellRenderer(final JList list){
			super(new BorderLayout());
			add(label = new ScrollLabel(), BorderLayout.CENTER);
			label.setFont(font = new Font(Font.MONOSPACED, Font.PLAIN, 12));
			timer = new javax.swing.Timer(100, new ActionListener(){
				public void actionPerformed(ActionEvent e){
					int index = list.getSelectedIndex();
					isRunning = index >= 0;
					if(isRunning)list.repaint(list.getCellBounds(index,index));
				}
			});
			list.addHierarchyListener(this);
			label.setOpaque(false);
			this.setOpaque(true);
		}
		@Override
		public Component getListCellRendererComponent
		(JList list, Object obj, int index, boolean isSelected, boolean cellHasFocus){
			if(isSelected) setBackground(list.getSelectionBackground());
			else setBackground(list.getBackground());
			label.setText(String.valueOf(obj));
			this.index = index;
			return this;
		}
		@Override
		public void hierarchyChanged(HierarchyEvent e){
			if((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED)!=0){
				if(LeafScrollList.this.isDisplayable()) timer.start();
				else timer.stop();
			}
		}
		/**
		 *文字をスクロール表示するラベルです。
		 *選択背景色の描画は下のパネルが行います。
		 */
		private class ScrollLabel extends JLabel{
			@Override
			public void paintComponent(Graphics g){
				Graphics2D g2  = (Graphics2D)g;
				FontRenderContext frc = g2.getFontRenderContext();
				GlyphVector gv = font.createGlyphVector(frc, getText());
				int cw = LeafScrollList.this.getVisibleRect().width;
				int vw = (int)gv.getVisualBounds().getWidth();
				if(index == getSelectedIndex()){
					if(vw>cw){
						float ascent = font.getLineMetrics(getText(), frc).getAscent();
						float yy = ascent / 2f + (float) gv.getVisualBounds().getY();
						xx = (cw + vw <= xx)? 0f : xx + 4f;
						g2.setPaint(getSelectionForeground());
						g2.drawGlyphVector(gv, cw-xx, getHeight()/2f-yy);
						return;
					}else label.setForeground(getSelectionForeground());
				}else label.setForeground(LeafScrollList.this.getForeground());
				super.paintComponent(g);
			}
		}
	}
}