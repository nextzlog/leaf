/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.file;

import java.util.EventListener;

/**
 * {@link LeafFileTree}のファイル選択イベントを受信するリスナーです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.0 作成：2010年7月10日
 *
 */
public interface FileSelectionListener extends EventListener {
	/**
	 * ファイル選択時に呼び出されます。
	 * 
	 * @param e ファイル選択イベント
	 */
	public void fileSelected(FileSelectionEvent e);

}