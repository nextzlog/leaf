/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.thema;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 *エディタをユーザーの好みで装飾するためのカーソルファクトリです。
 *
 *@since 2011年12月28日
 */
public final class CursorFactory {
	/**
	 *カーソル設定を指定してカーソルを生成します。
	 *
	 *@param cs カーソル設定
	 *@return 生成されたカーソル
	 */
	public static Cursor createCursor(CursorSettings cs){
		Cursor cursor = Cursor.getPredefinedCursor(cs.getType());
		File file = cs.getPhotoFile();
		
		if(file != null) try{
			BufferedImage image = ImageIO.read(file);
			Rectangle trim = cs.getPhotoTrimRect();
			
			if(trim != null) image = image.getSubimage
				(trim.x, trim.y, trim.width, trim.height);
			
			Point spot = new Point(0, 0);
			String name = cursor.getName();
			
			Toolkit kit = Toolkit.getDefaultToolkit();
			cursor =  kit.createCustomCursor(image, spot, name);
		}catch(IOException ex){}
		
		return cursor;
	}
}
