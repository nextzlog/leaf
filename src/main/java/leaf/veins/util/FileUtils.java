/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.util;

import java.io.File;

/**
 * ファイル操作のユーティリティです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/12/28 
 *
 */
public class FileUtils {
	/**
	 * 指定されたルート下の階層を順に並べてファイルを構築します。
	 * 
	 * @param root 起点となるディレクトリ
	 * @param dir  ディレクトリ階層の配列
	 * @return ファイル
	 */
	public static File newFile(File root, String... dir) {
		File file = root;
		for(String f : dir) {
			file = new File(file, f);
		}
		return file;
	}
	
	/**
	 * 実行ディレクトリ下の階層を順に並べてファイルを構築します。
	 * 
	 * @param dir ディレクトリ名の配列
	 * @return ファイル
	 */
	public static File newFile(String... dir) {
		return newFile(new File("."), dir);
	}

}
