/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.unix;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 条件を指定することによってファイルやディレクトリを検索します。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.4 作成：2012年3月21日
 */
public class LeafFind{
	
	private LeafFind(){}
	
	/**
	 * ファイルの拡張子を返します。
	 * 
	 * @param file ファイル
	 * @return 拡張子
	 */
	public static String getSuffix(File file){
		if(file == null) return null;
		String name = file.getName();
		int index = name.lastIndexOf(".");
		if(index < 0) return null;
		return name.substring(index + 1).toLowerCase();
	}
	
	/**
	 * ファイルが拡張子を持つか確認します。
	 * 
	 * @param file ファイル
	 * @return 拡張子を持つ場合true
	 */
	public static boolean hasSuffix(File file){
		return getSuffix(file) != null;
	}
	
	/**
	 * 指定したディレクトリ内のファイルを深さ優先検索します。
	 * 
	 * @param root 起点ディレクトリ
	 * @param filter フィルタ
	 * 
	 * @return ファイルの一覧
	 */
	public static File[] listFiles(File root, FileFilter filter){
		List<File> list = new ArrayList<File>();
		listFiles(list, root, filter);
		return list.toArray(new File[0]);
	}
	
	/**
	 * 指定したディレクトリ内の全てのファイルを再帰的に検索します。
	 * 
	 * @param list 見つかったファイルを格納するリスト
	 * @param dir 現在のディレクトリ
	 * @param filter フィルタ
	 */
	private static void listFiles(List<File> list, File dir, FileFilter filter){
		File[] children = dir.listFiles(filter);
		if(children == null) return;
		for(File child : children){
			if(child.isDirectory()) listFiles(list, child, filter);
			else list.add(child);
		}
	}
	
	/**
	 * 指定したディレクトリ内のファイルを深さ優先検索します。
	 * 
	 * @param root 起点ディレクトリ
	 * @param filter フィルタ
	 * 
	 * @return ファイルの一覧
	 */
	public static File[] listFiles(File root, FilenameFilter filter){
		List<File> list = new ArrayList<File>();
		listFiles(list, root, filter);
		return list.toArray(new File[0]);
	}
	
	/**
	 * 指定したディレクトリ内の全てのファイルを再帰的に検索します。
	 * 
	 * @param list 見つかったファイルを格納するリスト
	 * @param dir 現在のディレクトリ
	 * @param filter フィルタ
	 */
	private static void listFiles(List<File> list, File dir, FilenameFilter filter){
		File[] children = dir.listFiles(filter);
		if(children == null) return;
		for(File child : children){
			if(child.isDirectory()) listFiles(list, child, filter);
			else list.add(child);
		}
	}
}
