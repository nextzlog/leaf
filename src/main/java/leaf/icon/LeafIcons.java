/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.icon;

import java.awt.Image;
import javax.swing.Icon;

/**
 * LeafAPIで用意されているアイコンリソースを提供します。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.0 作成：2010年6月12日
 */
public final class LeafIcons {
	private LeafIcons() {}
	private static final String ICON_DIR_PATH  = "resource/images/16x16/";
	private static final String IMAGE_DIR_PATH = "resource/images/style/";
	
	/**
	 * 指定した名前の画像を取り出します。
	 * 
	 * @param name 画像の名前
	 * @return 画像が存在しない場合nullを返す
	 */
	public static Image getImage(String name) {
		return ImageCache.getImage(IMAGE_DIR_PATH + name + ".png");
	}
	
	/**
	 * 指定した名前でサイズが16x16のアイコンを取り出します。
	 * 
	 * @param name アイコンの名前
	 * @return 存在しない場合nullを返す
	 */
	public static Icon getIcon(String name) {
		return IconCache.getIcon(ICON_DIR_PATH + name + ".png");
	}
}
