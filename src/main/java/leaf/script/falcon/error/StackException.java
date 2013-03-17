/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.error;

import javax.script.ScriptException;

/**
 * スタックオーバーフロー時に通知される実行時例外です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
public class StackException extends ScriptException {
	private static final long serialVersionUID = 1L;

	/**
	 * この例外のもとになった例外を指定して例外を構築します。
	 * 
	 * @param ex 通知される例外
	 */
	public StackException(IndexOutOfBoundsException ex) {
		super(ex);
	}

}
