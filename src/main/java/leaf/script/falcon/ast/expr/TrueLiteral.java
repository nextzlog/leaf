/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import leaf.script.falcon.lex.Token;
import leaf.script.falcon.type.BooleanType;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * trueを表現する式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class TrueLiteral extends Literal {
	/**
	 * 対応する字句を指定して式木を構築します。
	 * 
	 * @param token 字句
	 */
	public TrueLiteral(Token token) {
		super(token, BooleanType.TYPE);
	}
	
	@Override
	public void gencode(CodeList list) {
		list.add(InstructionSet.PUSH);
		list.add(Boolean.TRUE);
	}

}
