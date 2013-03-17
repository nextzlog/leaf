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

/**
 * 画像データのキャッシュです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年9月30日
 */
final class ImageCache {
	private ImageCache() {}
	private static Map<String, Image> cache;
	
	/**
	 * 指定した名前の画像データを返します。
	 * 
	 * @param name 画像ファイルの名前
	 * @return 画像
	 */
	public static Image getImage(String name) {
		if(cache == null) cache = new WeakHashMap<>();
		
		Image image = cache.get(name);
		if(image != null) return image;
		
		return ImageLoader.load(name);
	}
}