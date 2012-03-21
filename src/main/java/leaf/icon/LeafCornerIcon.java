/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.icon;

import java.awt.*;
import javax.swing.Icon;

/**
*ステータスバーのコーナーアイコンの実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.3 作成：2011年5月5日
*/
public final class LeafCornerIcon implements Icon{
	public void paintIcon(Component comp,Graphics g,int x,int y){
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(Color.GRAY);
		g2.draw3DRect( 4, 12, 1, 1, false);
		g2.draw3DRect( 8,  8, 1, 1, false);
		g2.draw3DRect( 8, 12, 1, 1, false);
		g2.draw3DRect(12,  4, 1, 1, false);
		g2.draw3DRect(12,  8, 1, 1, false);
		g2.draw3DRect(12, 12, 1, 1, false);
	}
	public int getIconWidth(){
		return 16;
	}
	public int getIconHeight(){
		return 16;
	}
}
