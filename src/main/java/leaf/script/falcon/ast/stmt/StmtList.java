/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import java.util.ArrayList;

/**
 * 文木のリストです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class StmtList extends ArrayList<Stmt> {
	private static final long serialVersionUID = 1L;

	/**
	 * リストを構築します。
	 */
	public StmtList() {
		super();
	}
}
