/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

/**
 * 字句解析器から出力される字句の種類を列挙します。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
enum TokenType{
	REAL, STRING, SYMBOL, L_BRACE, R_BRACE, QUOTE, DOT;
}