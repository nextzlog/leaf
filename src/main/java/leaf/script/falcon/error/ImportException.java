/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.error;

import leaf.script.falcon.ast.stmt.Import;
import leaf.script.falcon.lex.Token;

/**
 * importされた型が見つからない場合に通知される例外です。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/02/18
 *
 */
public final class ImportException extends SyntaxException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 指定されたメッセージで例外を構築します。
	 * 
	 * @param msg 通知するメッセージ
	 * @param line 行番号
	 */
	public ImportException(String msg, int line) {
		super(msg, line);
	}
	
	/**
	 * 指定した字句に対して例外を構築します。
	 * 
	 * @param token 例外の原因となる字句
	 */
	public ImportException(Token token) {
		super("class not found", token);
	}
	
	/**
	 * 指定されたimport文に対応する例外を構築します。
	 * 
	 * @param i 例外の原因となる文
	 */
	public ImportException(Import i) {
		super(i.toString(), i.getLine());
	}

}