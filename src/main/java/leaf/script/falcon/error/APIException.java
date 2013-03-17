/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.error;

import leaf.script.falcon.ast.expr.App;

/**
 * コンストラクタやメソッドが見つからない場合に通知されます。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/02/19
 *
 */
public class APIException extends OperationException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 指定されたメッセージで例外を構築します。
	 * 
	 * @param msg 通知するメッセージ
	 * @param line 行番号
	 */
	public APIException(String msg, int line) {
		super(msg, line);
	}
	
	/**
	 * 指定された式に対応する例外を構築します。
	 * 
	 * @param app
	 */
	public APIException(App app) {
		super(app.toString(), app.getLine());
	}

}