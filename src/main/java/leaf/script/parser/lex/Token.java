/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.lex;

/**
 * 文字列や整数、識別子など、全ての字句の基底となるクラスです。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public abstract class Token {

	/**
	 * この字句を示す文字列を返します。
	 * 
	 * @return 字句の文字列
	 */
	public abstract String toString();

	/**
	 * この字句が識別子として利用可能であるか返します。
	 * 
	 * @return 識別子として利用可能である場合trueを返す
	 */
	public abstract boolean isIdentifier();

}
