/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.parser;

import leaf.script.falcon.ast.expr.Expr;
import leaf.script.falcon.ast.stmt.*;
import leaf.script.falcon.error.SyntaxException;
import leaf.script.falcon.error.ImportException;
import leaf.script.falcon.lex.Token;
import leaf.script.falcon.lex.TokenType;
import leaf.script.falcon.lex.Tokenizer;
import leaf.script.falcon.type.Type;

/**
 * LL構文解析の文の構文解析器です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
public class StatementParser {
	private ExpressionParser exparser;
	private Tokenizer lex;
	
	/**
	 * 字句解析器を指定して構文解析器を構築します。
	 * 
	 * @param lex 字句解析器
	 */
	public StatementParser(Tokenizer lex) {
		this.lex = lex;
		exparser = new ExpressionParser(lex);
	}
	
	/**
	 * empty文を構文解析します。
	 * 
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parseEmpty() throws SyntaxException {
		Token token = lex.checkToken(TokenType.SEMICOLON);
		return new Empty(token.getLine());
	}
	
	/**
	 * continue文を構文解析します。
	 * 
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parseContinue() throws SyntaxException {
		Token token = lex.checkToken(TokenType.CONTINUE);
		lex.checkToken(TokenType.SEMICOLON);
		return new Continue(token.getLine());
	}
	
	/**
	 * break文を構文解析します。
	 * 
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parseBreak() throws SyntaxException {
		Token token = lex.checkToken(TokenType.BREAK);
		lex.checkToken(TokenType.SEMICOLON);
		return new Break(token.getLine());
	}
	
	/**
	 * return文を構文解析します。
	 * 
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parseReturn() throws SyntaxException {
		Token token = lex.checkToken(TokenType.RETURN);
		Expr e = exparser.parseExpression();
		lex.checkToken(TokenType.SEMICOLON);
		return new Return(token.getLine(), e);
	}
	
	/**
	 * print文を構文解析します。
	 * 
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parsePrint() throws SyntaxException {
		Token token = lex.checkToken(TokenType.PRINT);
		Expr e = exparser.parseExpression();
		lex.checkToken(TokenType.SEMICOLON);
		return new Print(token.getLine(), e);
	}
	
	/**
	 * 式文を構文解析します。
	 * 
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parseExpression() throws SyntaxException {
		Expr e = exparser.parseExpression();
		if(e == null) return null;
		lex.checkToken(TokenType.SEMICOLON);
		return new ExprStmt(e);
	}
	
	/**
	 * 変数宣言を構文解析します。
	 * 
	 * @param scope スコープ
	 * @return 変数宣言 変数宣言でない場合null
	 * @throws SyntaxException SyntaxException 構文エラーがある場合
	 */
	private Local parseLocal(Scope scope) throws SyntaxException {
		if(!lex.hasToken()) return null;
		
		Type type = parseType(scope);
		if(type == null) return null;
		
		Token id = lex.checkToken(TokenType.ID);
		lex.checkToken(TokenType.SEMICOLON);
		return new Local(type, id.getToken());
	}
	
	/**
	 * 複合文を構文解析します。
	 * 
	 * @param scope スコープ
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parseCompound(Scope scope) throws SyntaxException {
		Token token = lex.checkToken(TokenType.LBRACE);
		Compound comp = new Compound(token.getLine());
		scope = comp.getScope().setParent(scope);
		
		while(lex.hasToken()) {
			Local local = parseLocal(scope);
			if(local == null) break;
			comp.addLocal(local);
		}
		
		while(lex.hasToken()) {
			Stmt stmt = parseStatement(scope);
			if(stmt == null) break;
			comp.addStmt(stmt);
		}
		
		lex.checkToken(TokenType.RBRACE);
		return comp;
	}
	
	/**
	 * else文を構文解析します。
	 * 
	 * @param scope スコープ
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parseElse(Scope scope) throws SyntaxException {
		Token token = lex.getToken();
		if(token == null) return null;
		
		if(token.isType(TokenType.ELSE)) {
			return parseStatement(scope);
		}
		
		lex.ungetToken(token);
		return null;
	}
	
	/**
	 * if文を構文解析します。
	 * 
	 * @param scope スコープ
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parseIf(Scope scope) throws SyntaxException {
		Token token = lex.checkToken(TokenType.IF);
		lex.checkToken(TokenType.LPAREN);
		Expr cond = exparser.parseExpression();
		lex.checkToken(TokenType.RPAREN);
		
		Stmt tstmt = parseStatement(scope);
		Stmt fstmt = parseElse(scope);
		
		return new If(token.getLine(), cond, tstmt, fstmt);
	}
	
	/**
	 * while文を構文解析します。
	 * 
	 * @param scope スコープ
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parseWhile(Scope scope) throws SyntaxException {
		Token token = lex.checkToken(TokenType.WHILE);
		lex.checkToken(TokenType.LPAREN);
		Expr cond = exparser.parseExpression();
		lex.checkToken(TokenType.RPAREN);
		
		Stmt body = parseStatement(scope);
		return new While(token.getLine(), cond, body);
	}
	
	/**
	 * 文を構文解析します。
	 * 
	 * @param scope スコープ
	 * @return 文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Stmt parseStatement(Scope scope) throws SyntaxException {
		Token token = lex.getToken();
		lex.ungetToken(token);
		
		switch(token.getType()) {
		case SEMICOLON:
			return parseEmpty();
		case LBRACE:
			return parseCompound(scope);
		case CONTINUE:
			return parseContinue();
		case BREAK:
			return parseBreak();
		case RETURN:
			return parseReturn();
		case PRINT:
			return parsePrint();
		case IF:
			return parseIf(scope);
		case WHILE:
			return parseWhile(scope);
		default:
			return parseExpression();
		}
	}
	
	/**
	 * 関数や変数などの型を構文解析します。
	 * 
	 * @param scope スコープ
	 * @return 型 見つからない場合null
	 * @throws SyntaxException 字句解析エラーの場合
	 */
	private Type parseType(Scope scope) throws SyntaxException {
		Token token = lex.getToken();
		if(token == null) return null;
		
		Type type = token.toPrimitiveType();
		if(type != null) return type;
		
		Import imp = scope.searchImport(token.getToken());
		if(imp != null) return imp.getType();
		
		lex.ungetToken(token);
		return null;
	}
	
	/**
	 * 関数の引数を構文解析します。
	 * 
	 * @param scope スコープ
	 * @return 引数の宣言
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Param parseParameter(Scope scope) throws SyntaxException {
		if(!lex.hasToken()) throw error("parameter type not specified");
		
		Type type = parseType(scope);
		if(type != null) {
			Token id = lex.checkToken(TokenType.ID);
			return new Param(type, id.getToken());
		}
		
		throw new ImportException(lex.getToken());
	}
	
	/**
	 * 関数の戻り値の型を構文解析します。
	 * 
	 * @param scope スコープ
	 * @return 戻り値の型
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Type parseReturnType(Scope scope) throws SyntaxException {
		if(!lex.hasToken()) throw error("return type not specified");
		
		Type type = parseType(scope);
		if(type != null) return type;
		
		throw new ImportException(lex.getToken());
	}
	
	/**
	 * 関数定義を構文解析します。
	 * 
	 * @param scope スコープ
	 * @return 関数定義
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Function parseFunction(Scope scope) throws SyntaxException {
		Token token = lex.checkToken(TokenType.DEFINE);
		Type type = parseReturnType(scope);
		String name = lex.checkToken(TokenType.ID).getToken();
		lex.checkToken(TokenType.LPAREN);
		
		Function func = new Function(token.getLine(), type, name);
		scope = func.getScope().setParent(scope);
		
		while(lex.hasToken()) {
			token = lex.getToken();
			if(token.isType(TokenType.RPAREN)) break;
			if(!token.isType(TokenType.COMMA)) {
				lex.ungetToken(token);
				func.addParam(parseParameter(scope));
			}
		}
		
		func.setBody(parseStatement(scope));
		return func;
	}
	
	/**
	 * import文を構文解析します。
	 * 
	 * @return import文
	 * @throws SyntaxException 構文エラーがある場合
	 */
	public Import parseImport() throws SyntaxException {
		Token token = lex.checkToken(TokenType.IMPORT);
		StringBuilder sb = new StringBuilder();
		final int line = token.getLine();
		
		while((token = lex.getToken()) != null) {
			if(token.isType(TokenType.SEMICOLON)) {
				return new Import(line, sb.toString());
			} else if(token.isType(TokenType.PERIOD)) {
				sb.append(".");
			} else if(token.isType(TokenType.ID)) {
				sb.append(token);
			} else break;
		}
		
		throw error("import not ended: " + token, token);
	}
	
	/**
	 * プログラムを構文解析します。
	 * 
	 * @return プログラム
	 * @throws SyntaxException 構文エラーがある場合
	 */
	public Program parseProgram() throws SyntaxException {
		Program program = new Program();
		ProgramScope scope = program.getScope();
		
		while(lex.hasToken()) {
			Token token = lex.getToken();
			lex.ungetToken(token);
			if(token.isType(TokenType.IMPORT)) {
				program.addImport(parseImport());
			} else {
				program.addFunction(parseFunction(scope));
			}
		}
		
		return program;
	}

	private SyntaxException error(String msg, Token tok) {
		return new SyntaxException(msg, tok.getLine());
	}
	
	private SyntaxException error(String msg) {
		return new SyntaxException(msg, lex.getLine());
	}

}