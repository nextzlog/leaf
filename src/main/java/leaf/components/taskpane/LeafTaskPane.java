/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components.taskpane;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

/**
*タスク領域の実装です。
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
	*コンポーネントを末尾に追加します。
	*@param comp 追加するコンポーネント
	*/
	public void addComp(Component comp){
		box.remove(glue);
		box.add(Box.createVerticalStrut(5));
		box.add(comp);
		box.add(glue);
		box.revalidate();
	}
}