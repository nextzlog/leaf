/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import java.io.PrintWriter;

import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;

/**
 * 空の文の構文木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Empty extends Stmt {
	/**
	 * 木を構築します。
	 * 
	 * @param line 行番号
	 */
	public Empty(int line) {
		super(line);
	}

	@Override
	public void print(PrintWriter pw) {
		pw.println(";");
		pw.flush();
	}
	
	@Override
	public Type resolve(Scope scope) {
		return null;
	}
	
	@Override
	public void gencode(CodeList list) {}

}
