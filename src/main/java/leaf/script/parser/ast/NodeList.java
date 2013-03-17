/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.ast;

import java.util.ArrayList;

/**
 * 構文解析器内部で用いられる構文木の線型リストです。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public final class NodeList extends ArrayList<Node> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 空のリストを構築します。
	 */
	public NodeList() {
		super(5);
	}
	
	/**
	 * リストの内容を適切な構文木で返します。
	 * 
	 * @return リストの内容を示す構文木
	 */
	public final Node toNode() {
		if(size() == 1) return get(0);
		
		return new ListNode(this);
	}

}
