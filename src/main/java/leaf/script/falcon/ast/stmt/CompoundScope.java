/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

/**
 * 複合文のスコープです。ローカル変数を管理します。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class CompoundScope extends Scope {

	/**
	 * このスコープに宣言を追加します。
	 * 
	 * @param local 追加する宣言
	 */
	public void addLocal(Local local) {
		Function f = getEnclosureFunction();
		f.getScope().allocate(local);
		super.decls.add(local);
	}

}