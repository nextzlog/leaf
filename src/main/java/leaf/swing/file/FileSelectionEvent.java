/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.file;

import java.io.File;
import java.util.EventObject;

/**
 * {@link LeafFileTree}のファイル選択イベントです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.1 作成：2010年9月18日
 *
 */
public class FileSelectionEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private final File file;
	
	/**
	 * イベントの発生源とファイルを指定してイベントを構築します。
	 * 
	 * @param source イベントの発生源
	 * @param file イベントに関連付けるファイル
	 */
	public FileSelectionEvent(Object source, File file){
		super(source);
		this.file = file;
	}
	
	/**
	 * 関連付けられたファイルを返します。
	 * 
	 * @return ファイル
	 */
	public File getFile(){
		return file;
	}

}