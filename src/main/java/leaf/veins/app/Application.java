/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.app;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import leaf.veins.shell.VeinShell;
import leaf.veins.ui.main.MainFrame;

/**
 * フレームワークが起動するアプリケーションは必ずこのクラスを継承します。
 * 
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/03/27 
 *
 */
public abstract class Application {
	/**
	 * アプリケーションは必ずこのメソッドで起動されます。
	 * 
	 * @param frame アプリケーションが適用されるメイン画面
	 * @return 起動直後のアプリケーション
	 */
	public static Application newInstance(MainFrame frame) {
		return null;
	}
	
	/**
	 * このアプリケーションが提供するメニューバーを構築します。
	 * 
	 * @return メイン画面に表示するメニューバー
	 */
	public abstract JMenuBar createMenuBar();
	
	/**
	 * このアプリケーションが提供するツールバーを構築します。
	 * 
	 * @return メイン画面に表示するツールバー
	 */
	public abstract JToolBar createToolBar();
	
	/**
	 * このアプリケーションが提供するコマンドをシェルに登録します。
	 * 
	 * {@link #createMenuBar()} {@link #createToolBar()}の前に呼び出されます。
	 *
	 * 
	 * @param shell 対象となるシェル
	 */
	public abstract void installCommands(VeinShell shell);
	
	/**
	 * このアプリケーションのインストールが完全に終了した時に呼び出されます。
	 * 
	 */
	public abstract void installFinished();

}
