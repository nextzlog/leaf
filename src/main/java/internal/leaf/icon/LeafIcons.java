/**********************************************************************************
 * leaf - Java Library for pafelog
 * Copyright(c) 2010 - 2013 by University of Tokyo Amateur Radio Club
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL) as published by the
 * Free Software Foundation, either version 3 of the License, or(at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package leaf.icon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;

/**
 * LeafAPIで用意されているアイコンリソースを提供します。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2010年6月12日
 */
public final class LeafIcons {
	private LeafIcons() {}
	private static final String ICON_DIR_PATH  = "images/16x16/";
	private static final String IMAGE_DIR_PATH = "images/style/";
	
	private static URLClassLoader loader = null;
	
	/**
	 * 指定した名前の画像を取り出します。
	 * 
	 * @param name 画像の名前
	 * @return 画像が存在しない場合nullを返す
	 */
	public static javafx.scene.image.Image getImageFX(String name) {
		BufferedImage img = loadImage(IMAGE_DIR_PATH + name + ".png");
		return SwingFXUtils.toFXImage(img, null);
	}
	
	/**
	 * 指定した名前でサイズが16x16のアイコンを取り出します。
	 * 
	 * @param name アイコンの名前
	 * @return 存在しない場合nullを返す
	 */
	public static javafx.scene.image.Image getIconFX(String name) {
		BufferedImage img = loadImage(ICON_DIR_PATH + name + ".png");
		return SwingFXUtils.toFXImage(img, null);
	}
	
	/**
	 * 指定した名前の画像を取り出します。
	 * 
	 * @param name 画像の名前
	 * @return 画像が存在しない場合nullを返す
	 */
	public static java.awt.Image getImage(String name) {
		return loadImage(IMAGE_DIR_PATH + name + ".png");
	}
	
	/**
	 * 指定した名前でサイズが16x16のアイコンを取り出します。
	 * 
	 * @param name アイコンの名前
	 * @return 存在しない場合nullを返す
	 */
	public static java.awt.Image getIcon(String name) {
		return loadImage(ICON_DIR_PATH + name + ".png");
	}
	
	/**
	 * 指定したパスで画像を取り出します。
	 * 
	 * @param path 画像までのパス
	 * @return 画像が存在しない場合nullを返す
	 */
	private static BufferedImage loadImage(String path) {
		InputStream stream = null;
		try {
			if(loader == null) loader = loader();
			stream = loader.getResourceAsStream(path);
			return ImageIO.read(stream);
		} catch(IllegalArgumentException ex) {
			return null;
		} catch(IOException ex) {
			return null;
		} finally {
			try{
				if(stream != null) stream.close();
			}catch(IOException ex) {}
		}
	}
	
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

}
