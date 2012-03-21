/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.shell.build;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.xml.namespace.QName;
import leaf.shell.LeafShell;

/**
 *"menu"要素の開始イベントを処理します。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
final class MenuHandler extends ItemHandler{
	/**
	 *シェルを指定してハンドラーを構築します。
	 *
	 *@param shell 関連付けられるシェル
	 */
	public MenuHandler(LeafShell shell){
		super(shell);
	}
	/**
	 *このハンドラーが処理する要素の名前を返します。
	 *
	 *@return menu
	 */
	@Override public QName name(){
		return new QName("menu");
	}
	/**
	 *このハンドラーがデフォルトで生成するコンポーネントを指定します。
	 *
	 *@return JMenuのインスタンスを返す
	 */
	@Override protected AbstractButton createDefaultButton(){
		return new JMenu();
	}
}
