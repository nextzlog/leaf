/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.startup;

import java.awt.Window;

import leaf.veins.ui.main.MainFrame;

/**
 * ウィンドウの位置と大きさを記憶します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/10/21 
 *
 */
public class WindowBounds extends leaf.util.hibernate.WindowBounds {
	public static final String MAIN_FRAME = MainFrame.class.getName();
	
	public WindowBounds(Window window, String name){
		super(window, name);
	}

}
