/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import javax.swing.JOptionPane;

import leaf.util.lang.LocalizeManager;

/**
 * ブラウザの起動に関するユーティリティです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/04/30 
 *
 */
public final class BrowseUtils {
	
	private static final LocalizeManager localize;
	private static Desktop desktop;
	
	static {
		localize = LocalizeManager.get(BrowseUtils.class);
	}
	
	/**
	 * 指定されたURIを閲覧するソフトウェアを起動します。
	 *
	 * @param uri 閲覧するURI
	 */
	public static void browse(URI uri){
		if(desktop == null && Desktop.isDesktopSupported()){
			desktop = Desktop.getDesktop();
		}
		
		if(desktop.isSupported(Desktop.Action.BROWSE)){
			try {
				desktop.browse(uri);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null,
					ex.getMessage(),
					localize.translate("browse"),
					JOptionPane.INFORMATION_MESSAGE);
			}
		}else{
			JOptionPane.showMessageDialog(null,
				localize.translate("browse_not_supported"),
				localize.translate("browse"),
				JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
