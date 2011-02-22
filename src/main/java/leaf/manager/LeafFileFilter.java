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
import java.util.ArrayList;
import javax.swing.filechooser.*;

import leaf.manager.*;

/**
*{@link ArrayList}を用いて拡張子を指定できるファイルフィルタです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月30日
*/
public class LeafFileFilter extends FileFilter{
	
	/*秘匿フィールド*/
	private final ArrayList<String> extensions;
	private final String description;
	
	/**
	*フィルターの内容を示す文字列と、拡張子を列挙した
	*リストを指定してファイルフィルタを生成します。
	*リストが空の場合、全ての拡張子を指定します。
	*@param description フィルターの内容を示す文字列
	*@param list 受け付ける拡張子の配列を表すリスト
	*/
	public LeafFileFilter(String description,ArrayList<String> list){
		this.extensions = list;
		this.description = description;
	}
	/**
	*ファイルを受け付けるか返します。
	*ファイルがディレクトリの場合もtrueを返します。
	*@param file ファイル
	*@return ファイルを受け付ける場合はtrue
	*/
	public boolean accept(File file){
		if(file.isDirectory())return true;
		String suf = LeafFileManager.getSuffix(file);
		if(extensions==null)return true;
		if(suf==null)return false;
		return extensions.contains(suf);
	}
	/**
	*このフィルターの文字列による表現を返します。
	*@return 文字列による表現
	*/
	public String getDescription(){
		return description+"("+LeafArrayManager.getStringFromList(";",extensions)+")";
	}
}