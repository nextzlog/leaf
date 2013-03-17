/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.plugin;

/**
 * 取り外し可能なモジュールが実装するインターフェースです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/12/28 
 *
 */
public interface Removable {
	/**
	 * このモジュールが開始する時に呼び出されます。
	 */
	public void start();
	
	/**
	 * このモジュールを終了します。
	 * 
	 * @return 終了準備が整った場合true
	 */
	public boolean shutdown();

}
