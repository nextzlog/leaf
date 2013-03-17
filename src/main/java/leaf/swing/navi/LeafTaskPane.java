/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.navi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Windowsのナビゲーションウィンドウに相当するコンテナです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年7月10日
 * @see LeafExpandPane
 *
 */
public class LeafTaskPane extends JComponent {
	private static final long serialVersionUID = 1L;
	private final Box box;
	private final Component glue = Box.createVerticalGlue();
	
	/**
	 * LeafTaskPaneを構築します。
	 */
	public LeafTaskPane() {
		setLayout(new BorderLayout());
		
		JScrollPane scroll = new JScrollPane(
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, BorderLayout.CENTER);
		
		box = Box.createVerticalBox();
		box.setOpaque(false);
		box.setBackground(Color.GRAY);
		box.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		scroll.getVerticalScrollBar().setUnitIncrement(25);
		setMinimumSize(new Dimension(100, 0));
		scroll.setViewportView(box);
		scroll.getViewport().addComponentListener(new ResizeHandler());
	}
	
	private class ResizeHandler extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e){
			box.revalidate();
		}
	}
	
	/**
	 * 指定されたコンポーネントを末尾に追加します。
	 * 
	 * @param comp 追加するコンポーネント
	 * @return 追加したコンポーネント
	 */
	public Component addComp(Component comp){
		box.remove(glue);
		box.add(Box.createVerticalStrut(5));
		box.add(comp);
		box.add(glue);
		box.revalidate();
		return comp;
	}
	
	/**
	 * 指定されたコンポーネントを指定された位置に追加します。
	 * 
	 * @param comp 追加するコンポーネント
	 * @param index 追加する位置
	 * @return 追加したコンポーネント
	 */
	public Component addComp(Component comp, int index){
		if(index<box.getComponentCount()-1){
			box.add(comp,index);
			box.revalidate();
		}
		return comp;
	}
	
	/**
	 * 指定されたコンポーネントを削除します。
	 * 
	 * @param comp 削除するコンポーネント
	 */
	public void remove(Component comp){
		box.remove(comp);
		box.revalidate();
	}
	
	/**
	 * 指定された位置のコンポーネントを削除します。
	 * 
	 * @param index コンポーネントの順番
	 */
	public void remove(int index){
		if(index<box.getComponentCount()-1){
			box.remove(index);
			box.revalidate();
		}
	}
	
	/**
	 * 全てのコンポーネントを削除します。
	 */
	public void removeAll(){
		box.removeAll();
		box.add(glue);
		box.revalidate();
	}

}
