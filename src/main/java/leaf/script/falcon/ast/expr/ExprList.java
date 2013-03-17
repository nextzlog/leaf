/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import java.util.ArrayList;

/**
 * 式のリストです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class ExprList extends ArrayList<Expr> {
	private static final long serialVersionUID = 1L;

	/**
	 * リストを構築します。
	 */
	public ExprList() {
		super(5);
	}
	
	/**
	 * 式の型の配列を返します。
	 * 
	 * @return 型の配列
	 */
	public final Class<?>[] getTypes() {
		Class<?>[] types = new Class<?>[size()];
		for(int i = 0; i < size(); i++) {
			types[i] = get(i).getType().getType();
		}
		return types;
	}

}
