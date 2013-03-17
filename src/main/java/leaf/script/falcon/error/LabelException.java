/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.error;

/**
 * break文やcontinue文のラベルが解決できない時に通知される例外です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/24
 *
 */
public final class LabelException extends ResolutionException {
	private static final long serialVersionUID = 1L;

	/**
	 * メッセージを指定して例外を構築します。
	 * 
	 * @param msg  通知するメッセージ
	 * @param line 行番号
	 */
	public LabelException(String msg, int line) {
		super(msg, line);
	}

}
