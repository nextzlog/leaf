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
import java.awt.geom.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedList;

import static java.awt.RenderingHints.*;

/**
 *タスクが進行中であることを視覚的に表示するアイコンです。
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年5月23日
 */
public class LeafBusyIcon implements Icon{
	private final Color col = Color.DARK_GRAY;
	private final double r  = 2d;
	private final double sx = 0d;
	private final double sy = 0d;
	private final Dimension dim;
	private final java.util.List<Shape> list
	= new LinkedList<Shape>(Arrays.asList(
		new Ellipse2D.Double(sx+3*r, sy+0*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+5*r, sy+1*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+6*r, sy+3*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+5*r, sy+5*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+3*r, sy+6*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+1*r, sy+5*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+0*r, sy+3*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+1*r, sy+1*r, 2*r, 2*r)
	));
	/**
	*アイコンを生成します。
	*/
	public LeafBusyIcon(){
		dim = new Dimension((int)(r*8+sx*2),(int)(r*8+sy*2));
	}
	/**
	*アイコンの回転アニメーションを１コマ先に進めます。
	*/
	public void next(){
		list.add(list.remove(0));
	}
	/**
	*アイコンの幅を返します。
	*@return アイコンの幅
	*/
	public int getIconWidth(){
		return dim.width;
	}
	/**
	*アイコンの高さを返します。
	*@return アイコンの高さ
	*/
	public int getIconHeight(){
		return dim.height;
	}
	/**
	*アイコンを描画します。
	*@param g グラフィックス
	*@param x 描画位置
	*@param y 描画位置
	*/
	public void paintIcon(Component comp,Graphics g,int x,int y){
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(new Color(0,0,0,0));
		g2.fillRect(x,y,getIconWidth(),getIconHeight());
		g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g2.setColor(col);
		g2.translate(x,y);
		float alpha = 0.0f;
		for(Shape s:list){
			g2.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, alpha));
			alpha += 0.125f;
			g2.fill(s);
		}
		g2.translate(-x,-y);
	}
}