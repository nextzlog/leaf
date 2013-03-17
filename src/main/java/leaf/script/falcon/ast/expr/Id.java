/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.expr;

import java.io.PrintWriter;

import leaf.script.falcon.ast.stmt.Decl;
import leaf.script.falcon.ast.stmt.Local;
import leaf.script.falcon.ast.stmt.Param;
import leaf.script.falcon.ast.stmt.Scope;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.lex.Token;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * 識別子の式木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Id extends Expr {
	private final String id;
	private Decl decl;
	
	/**
	 * 字句を指定して式木を構築します。
	 * 
	 * @param token 字句
	 */
	public Id(Token token) {
		super(token.getLine());
		this.id = token.getToken();
	}
	
	/**
	 * 識別子を返します。
	 * 
	 * @return 識別子
	 */
	public String getId() {
		return id;
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(id);
	}

	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		decl = scope.searchDecl(id);
		if(decl != null) {
			return setType(decl.getType());
		}
		throw new ResolutionException(this);
	}

	@Override
	public void gencode(CodeList list) {
		if(decl instanceof Param) {
			list.add(InstructionSet.APUSH);
			list.add(decl.getIndex());
		}
		else if(decl instanceof Local) {
			list.add(InstructionSet.LPUSH);
			list.add(decl.getIndex());
		}
	}

}
