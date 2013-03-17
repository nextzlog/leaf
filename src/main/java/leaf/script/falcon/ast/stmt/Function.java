/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import java.io.PrintWriter;

import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.lex.TokenType;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.InstructionSet;

/**
 * 関数定義の構文木の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
public class Function extends Stmt {
	private final Type type;
	private final String name;
	private final Label label;
	private Stmt body;
	private FunctionScope scope;
	
	/**
	 * 返り値の型と名前を指定して関数の木を構築します。
	 * 
	 * @param line 行番号
	 * @param type 返り値の型
	 * @param name 関数の名前
	 */
	public Function(int line, Type type, String name) {
		super(line);
		this.type = type;
		this.name = name;
		label = new Label(name);
		scope = new FunctionScope(this);
	}
	
	/**
	 * 関数の返り値の型を返します。
	 * 
	 * @return 返り値の型
	 */
	public Type getReturnType() {
		return type;
	}
	
	/**
	 * 関数の名前を返します。
	 * 
	 * @return 関数の名前
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 関数のラベルを返します。
	 * 
	 * @return 関数のラベル
	 */
	public Label getLabel() {
		return label;
	}
	
	/**
	 * 関数の本文を返します。
	 * 
	 * @return 本文
	 */
	public Stmt getBody() {
		return body;
	}
	
	/**
	 * 関数の本文を設定します。
	 * 
	 * @param body 本文
	 */
	public void setBody(Stmt body) {
		this.body = body;
	}
	
	/**
	 * 関数のスコープを返します。
	 * 
	 * @return スコープ
	 */
	public FunctionScope getScope() {
		return scope;
	}
	
	/**
	 * 関数に引数宣言を追加します。
	 * 
	 * @param param
	 */
	public void addParam(Param param) {
		scope.addParam(param);
	}
	
	private void printParams(PrintWriter pw) {
		pw.print('(');
		DeclList decls = scope.decls;
		for(int i = 0; i < decls.size(); i++) {
			if(i != 0) pw.print(", ");
			Decl d = decls.get(i);
			pw.print(d.getType());
			pw.print(' ');
			pw.print(d.getName());
		}
		pw.print(')');
	}

	@Override
	public void print(PrintWriter pw) {
		pw.print(TokenType.DEFINE);
		pw.print(' ');
		pw.print(type);
		pw.print(' ');
		pw.print(name);
		pw.print(' ');
		printParams(pw);
		pw.print(' ');
		body.print(pw);
		pw.flush();
	}

	@Override
	public Type resolve(Scope scope)
			throws ResolutionException {
		body.resolve(this.scope);
		return null;
	}

	@Override
	public void gencode(CodeList list) {
		label.setJump(list.size());
		list.add(InstructionSet.FRAME);
		list.add(scope.getParamCount());
		list.add(scope.getLocalCount());
		body.gencode(list);
		list.add(InstructionSet.PUSH);
		list.add(null);
		list.add(InstructionSet.RET);
	}

}
