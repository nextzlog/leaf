/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.ast;

/**
 * 構文解析木により生成される構文解析木の根底となるクラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public abstract class Node {
	
	/**
	 * 指定された番号の直属の構文木を返します。
	 * 
	 * @param index 子を指定する番号
	 * @return indexに対応する直下の構文木
	 */
	public abstract Node getChild(int index);
	
	/**
	 * この構文木が直属とする隷下の構文木の個数を返します。
	 * 
	 * @return 直属の構文木の個数を示す整数値
	 */
	public abstract int getChildCount();
	
	/**
	 * この構文木が隷下に構文木を持つか確認します。
	 * 
	 * @return 隷下に構文木を持つ場合trueを返す
	 */
	public final boolean hasChildren() {
		return getChildCount() > 0;
	}
	
	/**
	 * この構文木を構造的に表現する文字列を返します。
	 * 
	 * @return 構文木の構造を示す文字列
	 */
	public abstract String toString();
}
