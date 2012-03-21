/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.taskpane;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

/**
 *タスク領域の実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年7月10日
 *@see LeafExpandPane
 */
public class LeafTaskPane extends JScrollPane{
	
	private final Box box;
	private final Component glue = Box.createVerticalGlue();
	
	/**
	 *タスク領域を生成します。
	 */
	public LeafTaskPane(){
		super(VERTICAL_SCROLLBAR_AS_NEEDED,HORIZONTAL_SCROLLBAR_NEVER);
		
		box = Box.createVerticalBox();
		box.setOpaque(false);
		box.setBackground(getBackground().darker());
		box.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		getVerticalScrollBar().setUnitIncrement(25);
		setMinimumSize(new Dimension(100,0));
		getViewport().add(box);
		
		getViewport().addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent e){
				box.revalidate();
			}
		});
	}
	/**
	 *指定されたコンポーネントを末尾に追加します。
	 *@param comp 追加するコンポーネント
	 *@return 追加したコンポーネント
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
	 *指定されたコンポーネントを指定された位置に追加します。
	 *@param comp 追加するコンポーネント
	 *@param index 追加する位置
	 *@return 追加したコンポーネント
	 */
	public Component addComp(Component comp, int index){
		if(index<box.getComponentCount()-1){
			box.add(comp,index);
			box.revalidate();
		}
		return comp;
	}
	/**
	 *指定されたコンポーネントを削除します。
	 *@param comp 削除するコンポーネント
	 */
	public void remove(Component comp){
		box.remove(comp);
		box.revalidate();
	}
	/**
	 *指定された位置のコンポーネントを削除します。
	 *@param index コンポーネントの順番
	 */
	public void remove(int index){
		if(index<box.getComponentCount()-1){
			box.remove(index);
			box.revalidate();
		}
	}
	/**
	 *全てのコンポーネントを削除します。
	 */
	public void removeAll(){
		box.removeAll();
		box.add(glue);
		box.revalidate();
	}
}
