/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import leaf.script.falcon.ast.Node;

/**
 * 文の構文木の抽象クラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public abstract class Stmt extends Node {
	/**
	 * 出現する行を指定して木を構築します。
	 * 
	 * @param line 行番号
	 */
	public Stmt(int line) {
		super(line);
	}

}
