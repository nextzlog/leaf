/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.dialog.chooser;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.*;

import leaf.manager.*;

/**
*拡張子をArrayListを用いて動的に指定することができるファイルフィルタです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月30日
*/
public class LeafActiveFileFilter extends FileFilter{
	
	/*秘匿フィールド*/
	private final ArrayList<String> extensions;
	private final String description;
	
	/**
	*フィルターの内容を示す文字列と、拡張子を列挙したArrayList<String>を指定してファイルフィルタを生成します
	*@param description フィルターの内容を示す文字列
	*@param list 受け付ける拡張子の配列を表すArrayList<String>
	*/
	public LeafActiveFileFilter(String description,ArrayList<String> list){
		this.extensions = list;
		this.description = description;
	}
	/**
	*ファイルを受け付けるか返します
	*@param file ファイル
	*@return ファイルを受け付ける場合はtrue
	*/
	public boolean accept(File file){
		if(file.isDirectory())return true;
		String suf = LeafFileExtensionManager.getExtension(file);
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