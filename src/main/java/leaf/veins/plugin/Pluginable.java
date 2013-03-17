/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.plugin;

import java.awt.Window;

import javax.swing.JDialog;

/**
 * プラグイン可能なモジュールが実装するインターフェースです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/12/28 
 *
 */
public interface Pluginable extends Removable {
	/**
	 * プラグイン固有の名前を返します。
	 * 
	 * @return プラグインの名前
	 */
	public String getName();
	
	/**
	 * このプラグインが設定管理画面を提供するか返します。
	 * 
	 * @return 設定管理画面を持つ場合true 持たない場合false
	 */
	public boolean hasConfigurationDialog();
	
	/**
	 * このプラグインの設定管理画面を構築します。
	 * 
	 * @param owner 管理画面の所有者
	 * @return 生成された管理画面
	 */
	public JDialog createConfigurationDialog(Window owner);

}
