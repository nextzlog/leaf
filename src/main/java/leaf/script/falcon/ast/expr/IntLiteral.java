/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import leaf.script.falcon.lex.Token;
import leaf.script.falcon.type.IntType;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * int型の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class IntLiteral extends Literal {
	private int value;
	
	/**
	 * 整数値を表す字句を指定して式木を構築します。
	 * 
	 * @param token 字句
	 */
	public IntLiteral(Token token) {
		super(token, IntType.TYPE);
		String str = token.getToken();
		value = Integer.parseInt(str);
	}
	
	/**
	 * 整数値を返します。
	 * 
	 * @return 整数値
	 */
	public int getValue() {
		return value;
	}
	
	@Override
	public void gencode(CodeList list) {
		list.add(InstructionSet.PUSH);
		list.add(value);
	}

}
