/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.manager;

import java.io.*;
import java.util.*;

/**
*ファイルの拡張子を取り出すメソッドを提供します。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年3月23日
*/
public class LeafFileExtensionManager{
	/**
	*ファイルから拡張子を取り出します。ファイルがnullの場合nullを返します。
	*@param file 対象のファイル
	*@return 拡張子を表す文字列
	*/
	public static String getExtension(File file) {
		if(file!=null){
			int point = file.getName().lastIndexOf(".");
			if (point != -1) {
				return file.getName().substring(point + 1).toLowerCase();
    		}
		}return null;
	}
}