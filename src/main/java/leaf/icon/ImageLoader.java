/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.icon;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import javax.imageio.ImageIO;

/**
 * 画像データをロードします。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年9月30日
 */
final class ImageLoader {
	private ImageLoader() {}
	private static URLClassLoader loader = null;
	
	/**
	 * 画像のロードに利用するクラスローダーを準備します。
	 * 
	 * @return クラスローダー
	 * @throws IOException 準備に失敗した場合
	 */
	private static URLClassLoader loader() throws IOException {
		Class<LeafIcons> clazz = LeafIcons.class;
		String file = clazz.getSimpleName() + ".class";
		URL curl = clazz.getResource(file);
		URLConnection conn = curl.openConnection();
		URL root = ((JarURLConnection)conn).getJarFileURL();
		return URLClassLoader.newInstance(new URL[] {root});
	}
	/**
	 * 指定したパスで画像を取り出します。
	 * 
	 * @param path 画像までのパス
	 * @return 画像が存在しない場合nullを返す
	 */
	public static Image load(String path) {
		InputStream stream = null;
		try{
			if(loader == null) loader = loader();
			stream = loader.getResourceAsStream(path);
			return ImageIO.read(stream);
		}catch(IllegalArgumentException ex) {
			return null;
		}catch(IOException ex) {
			return null;
		}finally{
			try{
				if(stream != null) stream.close();
			}catch(IOException ex) {}
		}
	}
}