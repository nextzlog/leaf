/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.icon;

import java.awt.*;
import javax.swing.Icon;

/**
 * 閉じるアイコンの実装です。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.0 作成：2010年5月22日
 */
public final class CloseIcon implements Icon {
	@Override
	public void paintIcon(Component comp,Graphics g,int x,int y) {
		g.translate(x,y);
		g.setColor(Color.BLACK);
		g.drawLine(4,4,11,11);
		g.drawLine(4,5,10,11);
		g.drawLine(5,4,11,10);
		g.drawLine(11,4,4,11);
		g.drawLine(11,5,5,11);
		g.drawLine(10,4,4,10);
		g.translate(-x,-y);
	}
	
	@Override
	public int getIconWidth() {
		return 16;
	}
	
	@Override
	public int getIconHeight() {
		return 16;
	}
}