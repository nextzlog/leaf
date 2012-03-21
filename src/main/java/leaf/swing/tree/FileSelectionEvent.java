/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.tree;

import java.io.File;
import java.util.EventObject;

/**
 *{@link LeafFileTree}のファイル選択イベントです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年9月18日
 *@see FileSelectionListener
 */
public class FileSelectionEvent extends EventObject{

	private final File file;
	
	/**
	 *発生源とファイルを指定してイベントを生成します。
	 *@param source イベントの発生源
	 *@param file イベントに関連付けるファイル
	 */
	public FileSelectionEvent(Object source, File file){
		super(source);
		this.file = file;
	}
	/**
	 *関連付けられたファイルを返します。
	 *@return ファイル
	 */
	public File getFile(){
		return file;
	}
}