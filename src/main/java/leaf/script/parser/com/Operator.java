/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.com;

import leaf.script.parser.ast.Node;

/**
 * 演算子を定義するためのクラスです。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public abstract class Operator {
	private int order;
	private Associative assoc;
	
	public enum Associative {
		LEFT, RIGHT
	}
	
	public Operator(int order, Associative assoc) {
		this.order = order;
		this.assoc = assoc;
	}
	
	public int getOrder() {
		return order;
	}
	
	public Associative getAssociative() {
		return assoc;
	}
	
	public abstract Node createNode();
	
}