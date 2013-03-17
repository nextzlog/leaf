/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.lex;

/**
 * 構文解析器に渡される字句解析器が備えるべきメソッドを定義します。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public interface Lexer {
	
	/**
	 * 字句解析器のキューにある指定された順位の字句を削除することなく取得します。
	 * 
	 * @param index 字句の順位
	 * @return 対応する字句
	 */
	public Token getToken(int index) throws IndexOutOfBoundsException;
	
	/**
	 * 字句解析器のキューに貯められた最初の字句をキューから削除して返します。
	 * 
	 * @return キュー内の最初の字句
	 */
	public Token getNextToken();

}
