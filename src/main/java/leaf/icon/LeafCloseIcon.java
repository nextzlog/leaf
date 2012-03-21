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
*閉じるアイコンの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*/
public final class LeafCloseIcon implements Icon{
	public void paintIcon(Component comp,Graphics g,int x,int y){
		g.translate(x,y);//原点指定
		g.setColor(Color.BLACK);
		g.drawLine(4,4,11,11);
		g.drawLine(4,5,10,11);
		g.drawLine(5,4,11,10);
		g.drawLine(11,4,4,11);
		g.drawLine(11,5,5,11);
		g.drawLine(10,4,4,10);
		g.translate(-x,-y);
	}
	public int getIconWidth(){
		return 16;
	}
	public int getIconHeight(){
		return 16;
	}
}