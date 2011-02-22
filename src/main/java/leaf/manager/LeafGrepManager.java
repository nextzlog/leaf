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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.*;
import java.util.regex.*;
import java.util.ArrayList;

//Modified From : http://www.syboos.jp/java/doc/grep-files-by-java-nio.html

/**
*正規表現GREP検索機能を手軽に実装するクラスです。
*子ディレクトリ内の再帰検索にも対応しており、
*{@link FileFilter}を用いて制御できます。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月4日
*@see LeafFileManager
*/
public class LeafGrepManager{
	
	/**絶対パスを表示するように指定します。*/
	public static final int ABSOLUTE_PATH = 0;
	/**相対パスを表示するように指定します。*/
	public static final int PATH = 1;
	/**ファイル名を表示するように指定します。*/
	public static final int FILE_NAME = 2;
	
	private final Pattern LINE_SEPARATOR = Pattern.compile(".*\r?\n");
	private int format = PATH;
	
	/**
	*検索対象のディレクトリとファイルフィルタ、文字セット、検索パターンを指定して
	*GREP検索します。マッチした各行をファイル名・行番号付きで抽出して返します。
	*マッチする行がなかった場合、空の配列が返されます。
	*@param dir 検索するディレクトリ
	*@param filter ファイルフィルタ
	*@param charset 文字セット
	*@param pattern 検索パターン
	*/
	public String[] grep
		(File dir,FileFilter filter,Charset charset,Pattern pattern){
		
		ArrayList<String> list = new ArrayList<String>(100);
		File[] files = new LeafFileManager().listFiles(dir,filter);
		
		if(files==null)return null;
		for(int i=0;i<files.length;i++){
			try{
				String path;
				switch(format){
					case PATH:
						path = files[i].getPath();
						break;
					case FILE_NAME:
						path = files[i].getName();
						break;
					default:
						path = files[i].getAbsolutePath();
				}
				String[] ret = grep(files[i], charset, pattern);
				for(int j=0;j<ret.length;j++){
					list.add(path + ret[j]);
				}
			}catch(UnmappableCharacterException ex){
				//無視して次のファイルへ
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		list.add(0,"\n");
		list.add(0,LeafLangManager.get("Found","検出数") + " " + (list.size()-1) + "\n");
		list.add(0,LeafLangManager.get("Root Folder","検索フォルダ") + " " + dir + "\n");
		list.add(0,LeafLangManager.get("Pattern","検索条件") + " " + pattern + "\n");
		
		list.trimToSize();
		return list.toArray(new String[0]);
	}
	/**
	*検索対象のファイルと文字セット、検索パターンを指定して
	*GREP検索します。マッチした各行を行番号付きで抽出して返します。
	*マッチする行がなかった場合、空の配列が返されます。
	*@param file 検索するファイル
	*@param charset 文字セット
	*@param pattern 検索する文字列パターン
	*@return 抽出結果
	*@throws IOException 入出力に異常があった場合
	*/
	public String[] grep
		(File file, Charset charset, Pattern pattern) throws IOException{
		
		FileInputStream stream = new FileInputStream(file);
		FileChannel channel = stream.getChannel();
		MappedByteBuffer buffer = channel.map(
			FileChannel.MapMode.READ_ONLY, 0, channel.size()
		);
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer cb = decoder.decode(buffer);
		String[] ret = grep(cb,pattern);
		channel.close();
		stream.close();
		return ret;
	}
	/**
	*抽出を実行します。
	*@param cb 文字バッファ
	*@param pattern 検索パターン
	*@return 抽出結果
	*/
	private String[] grep(CharBuffer cb, Pattern pattern){
		Matcher lm = LINE_SEPARATOR.matcher(cb);
		Matcher pm = null;
		
		int current = 0;
		ArrayList<String> list = new ArrayList<String>();
		while(lm.find()){
			current++;
			String cs = lm.group();
			if(pm == null){
				pm = pattern.matcher(cs);
			}else{
				pm.reset(cs);
			}
			if(pm.find()){
				list.add("(" + current + "," + pm.start() + ") :" + cs);
			}
			if(lm.end() == cb.limit()){
				break;
			}
		}
		list.trimToSize();
		return list.toArray(new String[0]);
	}
	/**
	*抽出結果に表示されるファイルパスの表示形式を設定します。
	*@param format 表示形式 {@link #ABSOLUTE_PATH} {@link #PATH} {@link #FILE_NAME}
	*/
	public void setFilePathFormat(int format){
		this.format = format;
	}
}