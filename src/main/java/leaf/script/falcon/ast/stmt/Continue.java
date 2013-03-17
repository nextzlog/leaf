/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import java.io.PrintWriter;

import leaf.script.falcon.error.LabelException;
import leaf.script.falcon.lex.TokenType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * continue文の構文木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Continue extends Stmt {
	private Label label;

	/**
	 * 木を構築します。
	 * 
	 * @param line 行番号
	 */
	public Continue(int line) {
		super(line);
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(TokenType.CONTINUE);
		pw.println(";");
		pw.flush();
	}
	
	@Override
	public Type resolve(Scope scope)
			throws LabelException {
		LabelName ln = LabelName.LOOP_START;
		label = scope.searchLabel(ln);
		if(label != null) return null;
		throw new LabelException(
			"loop not found", getLine());
	}
	
	@Override
	public void gencode(CodeList list) {
		list.add(InstructionSet.JUMP);
		list.add(label);
	}

}
