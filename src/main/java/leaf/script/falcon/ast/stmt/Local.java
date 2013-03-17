/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import leaf.script.falcon.type.Type;

/**
 * ローカル変数の宣言の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/25
 *
 */
public final class Local extends Decl {
	/**
	 * 型と名前を指定してローカル変数を構築します。
	 * 
	 * @param type 変数の型
	 * @param name 変数の名前
	 */
	public Local(Type type, String name) {
		super(type, name);
	}

}
