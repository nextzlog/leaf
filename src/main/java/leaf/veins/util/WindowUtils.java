/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.util;

import java.awt.*;

import javax.swing.JFrame;

import leaf.veins.ui.main.MainFrame;

/**
 *ウィンドウ設計に関するユーティリティを提供します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 */
public final class WindowUtils {

	/**
	 *JFrameのインセットを返します。
	 *
	 *@return インセット
	 */
	public static Insets getJFrameInsets(){
		return MainFrame.getInstance().getInsets();
	}
	
	/**
	 *指定されたJFrameに、インセットを加算したサイズを適用します。
	 *
	 *
	 *@param frame サイズを適用するフレーム
	 *@param size インセットを除いたフレームのサイズ
	 */
	public static void setContentSize(JFrame frame, Dimension size){
		Dimension frameSize = new Dimension(size);
		Insets insets = getJFrameInsets();
		
		frameSize.width  += insets.left + insets.right;
		frameSize.height += insets.top + insets.bottom;
		
		frame.setSize(frameSize);
	}
	
	/**
	 * 指定されたJFrameをフルスクリーン表示するか設定します。
	 * 
	 * 
	 * @param window フルスクリーン表示するJFrame
	 * @param b フルスクリーン表示する場合true
	 */
	public static void setFullScreen(JFrame window, boolean b){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = ge.getDefaultScreenDevice();
		
		if(b) {
			window.dispose();
			window.setUndecorated(true);
			window.setVisible(true);
			device.setFullScreenWindow(window);
		}
		
		else {
			device.setFullScreenWindow(null);
			window.dispose();
			window.setUndecorated(false);
			window.setVisible(true);
			window.repaint();
		}
		
		window.requestFocus();
	}
	
	/**
	 * 指定されたJFrameがフルスクリーン表示されているか返します。
	 * 
	 * @param window 確認するJFrame
	 * @return フルスクリーン表示されていればtrue
	 */
	public static boolean isFullScreen(JFrame window){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = ge.getDefaultScreenDevice();
		
		return device.getFullScreenWindow() != window;
	}
}
