/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.ast;

import java.util.List;

/**
 * 複数の構文木を隷下に持つ構文木を実装します。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public class ListNode extends Node {
	
	private final List<Node> children;
	
	/**
	 * 隷下の構文木を指定して構文木を構築します。
	 * 
	 * @param list 隷下の構文木
	 */
	public ListNode(List<Node> list) {
		this.children = list;
	}

	/**
	 * 指定された番号の直属の構文木を返します。
	 * 
	 * @param index 子を指定する番号
	 * @return indexに対応する直下の構文木
	 */
	@Override
	public Node getChild(int index) {
		return children.get(index);
	}

	/**
	 * この構文木が直属とする隷下の構文木の個数を返します。
	 * 
	 * @return 直属の構文木の個数を示す整数値
	 */
	@Override
	public int getChildCount() {
		return children.size();
	}

	/**
	 * この構文木を構造的に表現する文字列を返します。
	 * 
	 * @return 構文木の構造を示す文字列
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("(");
		String separator = "";
		for(Node child : children) {
			sb.append(separator).append(child);
			separator = " ";
		}
		
		return sb.append(")").toString();
	}

}
