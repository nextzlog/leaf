/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
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

/**
*ファイルの再帰的検索などファイル関連の便利なメソッドをまとめたクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月5日
*/
public class LeafFileManager{
	
	/**
	*システムのホームディレクトリを返します。
	*@return ユーザー指定のホームディレクトリ
	*/
	public static File getHomeDirectory(){
		return new File(System.getProperty("user.home"));
	}
	/**
	*ファイルから拡張子を取り出します。ファイルがnullの場合nullを返します。
	*@param file 対象のファイル
	*@return 拡張子を表す文字列
	*/
	public static String getSuffix(File file) {
		if(file!=null){
			int point = file.getName().lastIndexOf(".");
			if (point != -1) {
				return file.getName().substring(point + 1).toLowerCase();
    		}
		}return null;
	}
	/**
	*拡張子のリストを指定してファイルフィルタを生成します。
	*@param list 拡張子のリスト
	*@param dir ディレクトリを含める場合true
	*/
	public static FileFilter createFileFilter
		(final ArrayList<String> list, final boolean dir){
		
		FileFilter filter = new FileFilter(){
			public boolean accept(File file){
				if(file.isDirectory()){
					return dir;
				}else{
					return list.contains(getSuffix(file));
				}
			}
		};
		return filter;
	}
	
	/**
	*起点ディレクトリとファイル名の一部を指定してファイルを再帰的に検索します。
	*このメソッドは{#searchIgnoreCase(File,String,String,boolean)}の簡易メソッドです。
	*@param dir 起点ディレクトリ
	*@param name 検索対象のファイル名の一部
	*@return 見つかったファイル
	*/
	public File[] search (File dir, final String name){
		
		return search(dir, name, ".+", true);
	}
	/**
	*起点ディレクトリとファイル名の一部と拡張子を指定してファイルを検索します。
	*isRecursiveをtrueにすると子ディレクトリ内も含めた再帰的検索を行います。
	*@param dir 起点ディレクトリ
	*@param name 検索対象のファイル名の一部
	*@param ext 検索対象の拡張子の正規表現
	*@param isRecursive 再帰的検索の場合true
	*@return 見つかったファイル
	*/
	public File[] search
		(File dir, final String name, final String ext, final boolean isRecursive){
		
		FileFilter filter = new FileFilter(){
			public boolean accept(File file){
				if(file.isDirectory()){
					return isRecursive;
				}else{
					String suf = getSuffix(file);
					if(suf!=null&&suf.matches(ext)){
						return (file.getName().indexOf(name)>=0);
					}else{
						return false;
					}
				}
			}
		};
		return listFiles(dir, filter);
	}
	/**
	*起点ディレクトリとファイル名の一部と拡張子を指定し、
	*大文字と小文字の区別を無視してファイルを検索します。
	*isRecursiveをtrueにすると子ディレクトリ内も含めた再帰的検索を行います。
	*@param dir 起点ディレクトリ
	*@param name 検索対象のファイル名の一部
	*@param ext 検索対象の拡張子の正規表現
	*@param isRecursive 再帰的検索の場合true
	*@return 見つかったファイル
	*/
	public File[] searchIgnoreCase
		(File dir, String name, final String ext, final boolean isRecursive){
		
		final String nameic = name.toLowerCase();
		FileFilter filter = new FileFilter(){
			public boolean accept(File file){
				if(file.isDirectory()){
					return isRecursive;
				}else{
					String suf = getSuffix(file);
					if(suf!=null&&suf.matches(ext)){
						return (file.getName().toLowerCase().indexOf(nameic)>=0);
					}else{
						return false;
					}
				}
			}
		};
		return listFiles(dir, filter);
	}
	/**
	*起点ディレクトリと拡張子を指定してファイルを検索します。
	*isRecursiveをtrueにすると子ディレクトリ内も含めた再帰的検索を行います。
	*@param dir 起点ディレクトリ
	*@param ext 検索対象の拡張子の正規表現
	*@param isRecursive 再帰的検索の場合true
	*@return 見つかったファイル
	*/
	public File[] search (File dir, final String ext, final boolean isRecursive){
		
		FileFilter filter = new FileFilter(){
			public boolean accept(File file){
				if(file.isDirectory()){
					return isRecursive;
				}else{
					String suf = getSuffix(file);
					return (suf!=null&&suf.matches(ext));
				}
			}
		};
		return listFiles(dir, filter);
	}
	/**
	*ディレクトリとファイルフィルタを指定して、子ディレクトリ内も含め全ての
	*ファイルを再帰的に検索して取り出します。
	*@param dir 検索開始の起点ディレクトリ
	*@param filter ファイルフィルタ
	*/
	public File[] listFiles (File dir, FileFilter filter) {
		ArrayList<File> list = new ArrayList<File>(1000);
		list(list, dir, filter);
		list.trimToSize();
		return list.toArray(new File[0]);
	}
	/**再帰検索用内部メソッド*/
	private void list(ArrayList<File> list, File dir, FileFilter filter){
		File[] children = dir.listFiles(filter);
		if(children == null) return;
		for(File child : children){
			if(child.isDirectory()){
				list(list, child, filter);
			}else{
				list.add(child);
			}
		}
	}
	/**
	*ディレクトリとファイル名フィルタを指定して、子ディレクトリ内も含め全ての
	*ファイルを再帰的に検索して取り出します。
	*@param dir 検索開始の起点ディレクトリ
	*@param filter ファイル名フィルタ
	*/
	public File[] listFiles (File dir, FilenameFilter filter) {
		ArrayList<File> list = new ArrayList<File>(100);
		list(list, dir, filter);
		list.trimToSize();
		return list.toArray(new File[0]);
	}
	/**再帰検索用内部メソッド*/
	private void list(ArrayList<File> list, File dir, FilenameFilter filter){
		File[] children = dir.listFiles(filter);
		if(children == null) return;
		for(File child : children){
			if(child.isDirectory()){
				list(list, child, filter);
			}else{
				list.add(child);
			}
		}
	}
	/**
	*指定されたディレクトリのファイル一覧の表現を返します。
	*@return 最終更新日時+種別+ファイル名の配列
	*/
	public String[] dir(File dir){
		File[] files = dir.listFiles();
		String[] res = new String[files.length];
		DateFormat format = DateFormat.getDateTimeInstance();
		for(int i=0; i<files.length; i++){
			res[i] = String.format(
				"%-24s%-7s%-16s", format.format(files[i].lastModified()),
				((files[i].isDirectory())? "<DIR>" : ""), files[i].getName()
			);
		}
		return res;
	}
}