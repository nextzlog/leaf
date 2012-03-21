/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.manager;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *ファイル関連のメソッドを提供するマネージャです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年9月5日
 */
public class LeafFileManager{
	private LeafFileManager(){}
	/**
	*ファイルの拡張子を返します。
	*@param file ファイル
	*@return 拡張子
	*/
	public static String getSuffix(File file){
		if(file == null) return null;
		String name = file.getName();
		int index = name.lastIndexOf(".");
		if(index < 0) return null;
		return name.substring(index+1).toLowerCase();
	}
	/**
	*ファイルを部分一致検索します。
	*@param root 起点ディレクトリ
	*@param name 部分一致させる名前
	*@return 見つかったファイル
	*/
	public static File[] search(File root, String name){
		return search(root, name, ".+", true);
	}
	/**
	*ファイルを部分一致検索します。
	*@param root 起点ディレクトリ
	*@param name 部分一致させる名前
	*@param suff 拡張子の正規表現
	*@param recr 再帰検索の場合true
	*@return 見つかったファイル
	*/
	public static File[] search
	(File root, final String name, final String suff, final boolean recr){
		return listFiles(root, new FileFilter(){
			public boolean accept(File file){
				if(file.isDirectory()) return recr;
				else{
					String suf = getSuffix(file);
					if(suf!=null&&suf.matches(suff)){
						return (file.getName().indexOf(name) >= 0);
					}else return false;
				}
			}
		});
	}
	/**
	*ファイルを大文字と小文字を区別せずに部分一致検索します。
	*@param root 起点ディレクトリ
	*@param name 部分一致させる名前
	*@param suff 拡張子の正規表現
	*@param recr 再帰検索の場合true
	*@return 見つかったファイル
	*/
	public static File[] searchIgnoreCase
	(File root, String name, final String suff, final boolean recr){
		final String namelc = name.toLowerCase();
		return listFiles(root, new FileFilter(){
			public boolean accept(File file){
				if(file.isDirectory()) return recr;
				else{
					String suf = getSuffix(file);
					if(suf!=null&&suf.matches(suff)){
						return (file.getName().toLowerCase().indexOf(namelc) >= 0);
					}else return false;
				}
			}
		});
	}
	/**
	*指定したディレクトリ内の全てのファイルを再帰検索します。
	*@param root 起点ディレクトリ
	*@param filter フィルタ
	*/
	public static File[] listFiles(File root, FileFilter filter){
		ArrayList<File> list = new ArrayList<File>(1000);
		listFiles(list, root, filter);
		list.trimToSize();
		return list.toArray(new File[0]);
	}
	/**再帰検索用内部メソッド*/
	private static void listFiles
	(List<File> list, File dir, FileFilter filter){
		File[] children = dir.listFiles(filter);
		if(children == null) return;
		for(File child : children){
			if(child.isDirectory()) listFiles(list, child, filter);
			else list.add(child);
		}
	}
	/**
	*指定したディレクトリ内の全てのファイルを再帰検索します。
	*@param root 起点ディレクトリ
	*@param filter フィルタ
	*/
	public static File[] listFiles(File root, FilenameFilter filter){
		ArrayList<File> list = new ArrayList<File>(1000);
		listFiles(list, root, filter);
		list.trimToSize();
		return list.toArray(new File[0]);
	}
	/**再帰検索用内部メソッド*/
	private static void listFiles
	(List<File> list, File dir, FilenameFilter filter){
		File[] children = dir.listFiles(filter);
		if(children == null) return;
		for(File child : children){
			if(child.isDirectory()) listFiles(list, child, filter);
			else list.add(child);
		}
	}
	/**
	*ディレクトリ内のファイル一覧の表現を返します。
	*@param dir ディレクトリ
	*@return 最終更新日時+種別+ファイル名の配列
	*/
	public static String[] dir(File dir){
		File[] files = dir.listFiles();
		String[] res = new String[files.length];
		DateFormat format = DateFormat.getDateTimeInstance();
		for(int i=0; i<files.length; i++){
			res[i] = String.format(
				"%-24s%-7s%s",
				format.format(files[i].lastModified()),
				(files[i].isDirectory())? "<DIR>" : "",
				files[i].getName()
			);
		}
		return res;
	}
}