/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import leaf.veins.shell.VeinShell;
import leaf.veins.ui.main.MainFrame;

/**
 * フレームワークがアプリケーションを起動する際に必ず使用されます。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/01/01 
 *
 */
public final class Launcher<A extends Application> {
	private final MainFrame frame;
	private final VeinShell shell;
	private final Class<? extends Application> appclass;
	
	/**
	 * アプリケーションのクラスを指定してランチャーを構築します。
	 * 
	 * @param appclass 起動するクラス
	 */
	public Launcher(Class<A> appclass) {
		this.appclass = appclass;
		this.frame = MainFrame.getInstance();
		this.shell = VeinShell.getInstance();
	}
	
	/**
	 * アプリケーションを起動します。起動に失敗するとnullを返します。
	 * 
	 * @return 起動済みアプリケーション
	 */
	public final A launch() {
		try {
			Method method = appclass.getMethod(
				"newInstance", MainFrame.class);
			@SuppressWarnings("unchecked")
			A app = (A) method.invoke(null, frame);
			app.installCommands(shell);
			frame.setJMenuBar(app.createMenuBar());
			frame.setJToolBar(app.createToolBar());
			app.installFinished();
			return app;
		} catch (IllegalAccessException ex) {
		} catch (NoSuchMethodException ex) {
		} catch (ClassCastException ex) {
		} catch (SecurityException  ex) {
		} catch (IllegalArgumentException  ex) {
		} catch (InvocationTargetException ex) {
		}
		return null;
	}

}
