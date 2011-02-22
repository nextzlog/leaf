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
package leaf.cursor;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.awt.geom.Arc2D;

/**
*写真付きのカーソルを手軽に実装するクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年8月28日
*/
public class LeafCursorFactory{
	
	
	private static final double RADIUS = 0.25;
	
	/**
	*画像ファイルとトリミング座標、カーソルのタイプを指定してカーソルを生成します。
	*@param file 画像ファイル
	*@param trim トリミング座標
	*@param type ファイルが空もしくは存在しない場合の代替カーソルのタイプ
	*/
	public static Cursor createCursor(File file,Rectangle trim,int type){
		Cursor cursor = Cursor.getPredefinedCursor(type);
		if(file!=null&&file.exists()){
			BufferedImage image;
			try{
				image = ImageIO.read(file);
				if(trim != null){
					image = image.getSubimage(trim.x,trim.y,trim.width,trim.height);
				}
				Graphics2D g2 = image.createGraphics();
				g2.setPaint(Color.BLACK);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
				int[] xs = {0,(int)(RADIUS*image.getHeight()),0};
				int[] ys = {0,0,(int)(RADIUS*image.getWidth())};
				Polygon pol = new Polygon(xs,ys,3);
				g2.fill(pol);
				Point spot = new Point(0,0);
				Toolkit kit = Toolkit.getDefaultToolkit();
				cursor = kit.createCustomCursor(image,spot,cursor.getName());
			}catch(Exception ex){}
		}
		return cursor;
	}
}
