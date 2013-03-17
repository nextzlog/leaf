/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import leaf.script.falcon.lex.Token;
import leaf.script.falcon.type.DoubleType;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * double型の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class DoubleLiteral extends Literal {
	private double value;
	
	/**
	 * 実数値を表す字句を指定して式木を構築します。
	 * 
	 * @param token 字句
	 */
	public DoubleLiteral(Token token) {
		super(token, DoubleType.TYPE);
		String str = token.getToken();
		value = Double.parseDouble(str);
	}
	
	/**
	 * 実数値を返します。
	 * 
	 * @return 実数値
	 */
	public double getValue() {
		return value;
	}
	
	@Override
	public void gencode(CodeList list) {
		list.add(InstructionSet.PUSH);
		list.add(value);
	}

}
