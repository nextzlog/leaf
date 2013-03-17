/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

/**
 * LISTP関数が真を返す型であることを保証します。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public interface Listp{
	/**
	 * リストのCAR部を評価せずに返します。
	 * 
	 * @return CAR部
	 */
	public abstract Sexp car();
	
	/**
	 * リストのCDR部を評価せずに返します。
	 * 
	 * @return CDR部
	 */
	public abstract Sexp cdr();
}