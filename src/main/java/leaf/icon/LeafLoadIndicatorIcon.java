/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.icon;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

/**
*作業が進行していることを視覚的に通知するアイコンです。
*@author 東大アマチュア無線クラブ 所属部員 Reise
*@since Leaf 1.0 作成：2010年5月23日
*/
public class LeafLoadIndicatorIcon implements Icon{
	/**秘匿フィールド*/
	private final Color col = new Color(0.5f,0.5f,0.5f);
	private final double r = 2.0d;
	private final double sx = 0d;
	private final double sy = 0d;
	private final Dimension dim = new Dimension((int)(r*8+sx*2),(int)(r*8+sy*2));
	private boolean running = false;
	/*マップ*/
	private final java.util.List<Shape> list = new ArrayList<Shape>(Arrays.asList(
		new Ellipse2D.Double(sx+3*r, sy+0*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+5*r, sy+1*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+6*r, sy+3*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+5*r, sy+5*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+3*r, sy+6*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+1*r, sy+5*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+0*r, sy+3*r, 2*r, 2*r),
		new Ellipse2D.Double(sx+1*r, sy+1*r, 2*r, 2*r)));
	/**アイコンの回転アニメーションを１コマ先に進めます。*/
	public void next(){
		if(running)list.add(list.remove(0));
	}
	/**
	*アイコンの回転を設定します。
	*@param running アイコンを回転させる場合true
	*/
	public void setRunning(boolean running){
		this.running = running;
	}
	/**
	*アイコンの回転状況を取得します。
	*@return アイコンが回転中ならtrue
	*/
	public boolean isRunning(){
		return running;
	}
	/**アイコンの幅を返します。*/
	public int getIconWidth(){
		return dim.width;
	}
	/**アイコンの高さを返します*/
	public int getIconHeight(){
		return dim.height;
	}
	/**アイコンを描画します。*/
	public void paintIcon(Component comp,Graphics g,int x,int y){
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(new Color(0,0,0,0));
		g2.fillRect(x,y,getIconWidth(),getIconHeight());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(col);
		float alpha = 0.0f;
		g2.translate(x,y);//原点指定
		for(Shape s:list){
			alpha = running?alpha+0.1f:0.5f;
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
			g2.fill(s);
		}
		g2.translate(-x,-y);
	}
}