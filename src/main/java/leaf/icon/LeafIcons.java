/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.icon;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *LeafAPIで用意されているアイコンリソースを提供します。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年6月12日
 */
public final class LeafIcons{
	
	private LeafIcons(){}
	private static URLClassLoader loader = null;
	private static final String ICON_DIR_PATH  = "images/16x16/";
	private static final String IMAGE_DIR_PATH = "images/style/";
	
	/**
	 *画像のロードに利用するクラスローダーを準備します。
	 *
	 *@return クラスローダー
	 *@throws IOException 準備に失敗した場合
	 */
	private static URLClassLoader loader() throws IOException{
		Class clazz = LeafIcons.class;
		String file = clazz.getSimpleName() + ".class";
		URL curl = clazz.getResource(file);
		URLConnection conn = curl.openConnection();
		URL root = ((JarURLConnection)conn).getJarFileURL();
		return URLClassLoader.newInstance(new URL[] {root});
	}
	/**
	 *指定したパスで画像を取り出します。
	 *
	 *@param path 画像までのパス
	 *@return 画像が存在しない場合nullを返す
	 */
	private static Image load(String path){
		InputStream stream = null;
		try{
			if(loader == null) loader = loader();
			stream = loader.getResourceAsStream(path);
			return ImageIO.read(stream);
		}catch(IllegalArgumentException ex){
			return null;
		}catch(IOException ex){
			return null;
		}finally{
			try{
				if(stream != null) stream.close();
			}catch(IOException ex){}
		}
	}
	/**
	 *指定した名前の画像を取り出します。
	 *
	 *@param name 画像の名前
	 *@return 画像が存在しない場合nullを返す
	 */
	public static Image getImage(String name){
		return load(IMAGE_DIR_PATH + name + ".png");
	}
	/**
	 *指定した名前でサイズが16x16のアイコンを取り出します。
	 *
	 *@param name アイコンの名前
	 *@return 存在しない場合nullを返す
	 */
	public static Icon getIcon(String name){
		Image img = load(ICON_DIR_PATH + name + ".png");
		return (img != null)? new ImageIcon(img) : null;
	}
}
