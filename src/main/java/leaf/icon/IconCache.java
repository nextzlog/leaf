/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.icon;

import java.awt.Image;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.ImageIcon;

/**
 * アイコンのキャッシュです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年9月30日
 */
final class IconCache {
	private IconCache() {}
	private static Map<String, ImageIcon> cache;
	
	/**
	 * 指定した名前のアイコンデータを返します。
	 * 
	 * @param name アイコンファイルの名前
	 * @return アイコン
	 */
	public static ImageIcon getIcon(String name) {
		if(cache == null) cache = new WeakHashMap<>();
		
		ImageIcon icon = cache.get(name);
		if(icon != null) return icon;
		
		Image image = ImageCache.getImage(name);
		if(image == null) return null;
		
		icon = new ImageIcon(image);
		cache.put(name, icon);
		return icon;
	}
}