/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.util;

import java.io.File;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.logging.Logger;

import leaf.util.lang.LocalizeManager;

/**
 * 地域化の設定を読み込むフレームワークです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/01/13 
 *
 */
public class LocalizeSettings {
	/**
	 * 指定したクラスと同じ場所にあるlocalize.xmlを読み込んで適用します。
	 * 
	 * @param c localize.xmlを読み込むパスのルートとなるクラス
	 * @return 読み込みに成功すればtrue
	 */
	public static boolean loadLocalizeSettings(Class<?> c) {
		try {
			applyLocalizeSettings(c);
			return true;
		} catch (InvalidPropertiesFormatException ex) {
			String msg = ex.getLocalizedMessage();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(msg);
		} catch (IOException ex) {
			String msg = ex.getLocalizedMessage();
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(msg);
		}
		return false;
	}
	
	private static void applyLocalizeSettings(Class<?> c)
		throws InvalidPropertiesFormatException, IOException {
		File home = Resources.getJarFileOf(c).getParentFile();
		java.util.Properties prop = new java.util.Properties();
		prop.loadFromXML(c.getResourceAsStream("localize.xml"));
		
		for(Object key : prop.keySet()) {
			String n = String.valueOf(key);
			String p = prop.getProperty(n);
			File dir = new File(home, p);
			LocalizeManager.setRoot(n, dir);
		}
	}

}
