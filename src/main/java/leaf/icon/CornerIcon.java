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
 * ステータスバーのコーナーアイコンの実装です。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年5月5日
 */
public final class CornerIcon implements Icon {
	@Override
	public void paintIcon(Component comp,Graphics g,int x,int y) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(Color.GRAY);
		g2.draw3DRect( 4, 12, 1, 1, false);
		g2.draw3DRect( 8,  8, 1, 1, false);
		g2.draw3DRect( 8, 12, 1, 1, false);
		g2.draw3DRect(12,  4, 1, 1, false);
		g2.draw3DRect(12,  8, 1, 1, false);
		g2.draw3DRect(12, 12, 1, 1, false);
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
