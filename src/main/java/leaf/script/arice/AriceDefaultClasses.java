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
package leaf.script.arice;

import java.util.ArrayList;
import javax.script.ScriptException;

import leaf.manager.LeafLangManager;

/**
*AriCE言語で基本となるクラスを予めインポートします。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年2月22日
*/
final class AriceDefaultClasses{
	
	/**
	*標準クラスをクラステーブルに登録します。
	*@param table クラステーブル
	*/
	public static void importDefaultClasses(AriceClassTable table){
		try{
			table.add(LeafLangManager.class);
			table.addPackage("leaf.manager");
			table.addPackage("java.lang");
			table.add(ArrayList.class);
		}catch(ScriptException ex){}
	}
}