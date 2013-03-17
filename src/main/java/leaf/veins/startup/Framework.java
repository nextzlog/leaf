/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.startup;

import leaf.util.hibernate.Properties;
import leaf.veins.app.Application;
import leaf.veins.app.Launcher;
import leaf.veins.plugin.ModuleManager;
import leaf.veins.startup.os.OS;
import leaf.veins.ui.main.MainFrame;

/**
 * アプリケーションを動作させる前に必ずこのクラスで起動してください。
 * 
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/01/03 
 *
 */
public final class Framework {
	
	/**
	 * 起動前のアプリケーションのクラスを指定してフレームワークを起動します。
	 *
	 * @param appclass 起動するアプリケーションのクラス
	 * @throws Exception 起動に失敗した場合
	 */
	public static void startup(Class<? extends Application> appclass) {
		OS.startup(System.getProperty("os.name"));
		
		MainFrame frame = MainFrame.getInstance();
		new WindowBounds(frame, WindowBounds.MAIN_FRAME).applyBounds();
		
		Launcher<? extends Application> launcher = new Launcher<>(appclass);
		launcher.launch();
		
		frame.setVisible(true);
		ModuleManager.getInstance().loadAllModules();
	}
	
	/**
	 * 全てのモジュールを終了してからフレームワークとJava実行環境を終了します。
	 * 
	 * @param status 終了ステータスコード
	 */
	public static void shutdown(int status){
		MainFrame frame = MainFrame.getInstance();
		if(!ModuleManager.getInstance().shutdownAllModules()) return;
		
		new WindowBounds(frame, WindowBounds.MAIN_FRAME).saveBounds();
		frame.getStatusBar().saveNewsBarURL();
		
		OS.exit(System.getProperty("os.name"));
		Properties.save();
		
		System.exit(status);
	}

}
