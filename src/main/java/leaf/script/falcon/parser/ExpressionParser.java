/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.parser;

import leaf.script.falcon.ast.expr.*;
import leaf.script.falcon.error.ProgramNotEnded;
import leaf.script.falcon.error.SyntaxException;
import leaf.script.falcon.error.TokenException;
import leaf.script.falcon.lex.Token;
import leaf.script.falcon.lex.TokenType;
import leaf.script.falcon.lex.Tokenizer;

/**
 * LL構文解析の式の構文解析器です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
public final class ExpressionParser {
	private Tokenizer lex;
	
	/**
	 * 字句解析器を指定して構文解析器を構築します。
	 * 
	 * @param lex 字句解析器
	 */
	public ExpressionParser(Tokenizer lex) {
		this.lex = lex;
	}
	
	/**
	 * 関数の実引数のリストを構文解析します。
	 * 
	 * @return 引数のリスト
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private ExprList parseArgList() throws SyntaxException {
		ExprList list = new ExprList();
		lex.checkToken(TokenType.LPAREN);
		
		Token token = null;
		while(lex.hasToken()) {
			Expr arg = parseExpression();
			if(arg != null) list.add(arg);
			token = lex.getToken();
			if(token.isType(TokenType.RPAREN)) return list;
			if(!token.isType(TokenType.COMMA)) break;
		}
		
		throw new TokenException(TokenType.COLON, token);
	}
	
	/**
	 * 識別子の式を構文解析します。
	 * 
	 * @param id 識別子の字句
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseId(Token id) throws SyntaxException {
		Token token = lex.getToken();
		lex.ungetToken(token);
		
		if(!token.isType(TokenType.LPAREN)) {
			return new Id(id);
		} else {
			ExprList args = parseArgList();
			return new Call(id, args);
		}
	}
	
	/**
	 * 最も優先順位の高い演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parsePrimary() throws SyntaxException {
		Token token = lex.getToken();
		if(token == null) return null;
		
		Literal literal = token.toLiteral();
		if(literal != null) return literal;
		
		if(token.isType(TokenType.LPAREN)) {
			Expr e = parseExpression();
			lex.checkToken(TokenType.RPAREN);
			return new Paren(token.getLine(), e);
		}
		
		if(!token.isType(TokenType.ID)) {
			lex.ungetToken(token);
			return null;
		}
		
		return parseId(token);
	}
	
	/**
	 * 単項演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseUnary() throws SyntaxException {
		Token token = lex.getToken();
		if(token == null) return null;
		
		if(token.isType(TokenType.PLUS)) {
			Expr e = parseUnary();
			return new Plus(token.getLine(), e);
		}
		
		if(token.isType(TokenType.MINUS)) {
			Expr e = parseUnary();
			return new Minus(token.getLine(), e);
		}
		
		if(token.isType(TokenType.BANG)) {
			Expr e = parseUnary();
			return new LogNeg(token.getLine(), e);
		}
		
		lex.ungetToken(token);
		return parsePrimary();
	}
	
	/**
	 * 乗算除算演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseMultiplicative() throws SyntaxException {
		Expr l = parseUnary();
		if(l == null) return null;
		
		while(lex.hasToken()) {
			Token token = lex.getToken();
			
			if(token.isType(TokenType.MUL)) {
				Expr r = parseUnary();
				if(r == null) break;
				l = new Mul(token, l, r);
			} 
			else if(token.isType(TokenType.DIV)) {
				Expr r = parseUnary();
				if(r == null) break;
				l = new Div(token, l, r);
			}
			else if(token.isType(TokenType.REM)) {
				Expr r = parseUnary();
				if(r == null) break;
				l = new Rem(token, l, r);
			}
			else {
				lex.ungetToken(token);
				return l;
			}
		}
		
		throw new ProgramNotEnded(l);
	}
	
	/**
	 * 加減算演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseAdditive() throws SyntaxException {
		Expr l = parseMultiplicative();
		if(l == null) return null;
		
		while(lex.hasToken()) {
			Token token = lex.getToken();
			
			if(token.isType(TokenType.PLUS)) {
				Expr r = parseMultiplicative();
				if(r == null) break;
				l = new Add(token, l, r);
			} 
			else if(token.isType(TokenType.MINUS)) {
				Expr r = parseMultiplicative();
				if(r == null) break;
				l = new Sub(token, l, r);
			}
			else {
				lex.ungetToken(token);
				return l;
			}
		}
		
		throw new ProgramNotEnded(l);
	}
	
	/**
	 * 算術比較演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseRelational() throws SyntaxException {
		Expr l = parseAdditive();
		if(l == null) return null;
		
		while(lex.hasToken()) {
			Token token = lex.getToken();
			
			if(token.isType(TokenType.LT)) {
				Expr r = parseAdditive();
				if(r == null) break;
				l = new Lt(token, l, r);
			} 
			else if(token.isType(TokenType.GT)) {
				Expr r = parseAdditive();
				if(r == null) break;
				l = new Gt(token, l, r);
			}
			else if(token.isType(TokenType.LE)) {
				Expr r = parseAdditive();
				if(r == null) break;
				l = new Le(token, l, r);
			}
			else if(token.isType(TokenType.GE)) {
				Expr r = parseAdditive();
				if(r == null) break;
				l = new Ge(token, l, r);
			}
			else {
				lex.ungetToken(token);
				return l;
			}
		}
		
		throw new ProgramNotEnded(l);
	}
	
	/**
	 * 関係演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseEquality() throws SyntaxException {
		Expr l = parseRelational();
		if(l == null) return null;
		
		while(lex.hasToken()) {
			Token token = lex.getToken();
			
			if(token.isType(TokenType.EQ)) {
				Expr r = parseRelational();
				if(r == null) break;
				l = new Eq(token, l, r);
			} 
			else if(token.isType(TokenType.NEQ)) {
				Expr r = parseRelational();
				if(r == null) break;
				l = new Neq(token, l, r);
			}
			else {
				lex.ungetToken(token);
				return l;
			}
		}
		
		throw new ProgramNotEnded(l);
	}
	
	/**
	 * 論理積演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseAnd() throws SyntaxException {
		Expr l = parseEquality();
		if(l == null) return null;
		
		while(lex.hasToken()) {
			Token token = lex.getToken();
			
			if(token.isType(TokenType.AND)) {
				Expr r = parseEquality();
				if(r == null) break;
				l = new And(token, l, r);
			} else {
				lex.ungetToken(token);
				return l;
			}
		}
		
		throw new ProgramNotEnded(l);
	}
	
	/**
	 * 排他的論理和演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseExclusiveOr() throws SyntaxException {
		Expr l = parseAnd();
		if(l == null) return null;
		
		while(lex.hasToken()) {
			Token token = lex.getToken();
			
			if(token.isType(TokenType.CARET)) {
				Expr r = parseAnd();
				if(r == null) break;
				l = new Xor(token, l, r);
			} else {
				lex.ungetToken(token);
				return l;
			}
		}
		
		throw new ProgramNotEnded(l);
	}
	
	/**
	 * 論理和演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseInclusiveOr() throws SyntaxException {
		Expr l = parseExclusiveOr();
		if(l == null) return null;
		
		while(lex.hasToken()) {
			Token token = lex.getToken();
			
			if(token.isType(TokenType.OR)) {
				Expr r = parseExclusiveOr();
				if(r == null) break;
				l = new Or(token, l, r);
			} else {
				lex.ungetToken(token);
				return l;
			}
		}
		
		throw new ProgramNotEnded(l);
	}
	
	/**
	 * 短絡論理積演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseConditionalAnd() throws SyntaxException {
		Expr l = parseInclusiveOr();
		if(l == null) return null;
		
		while(lex.hasToken()) {
			Token token = lex.getToken();
			
			if(token.isType(TokenType.SC_AND)) {
				Expr r = parseInclusiveOr();
				if(r == null) break;
				l = new ShortAnd(token, l, r);
			} else {
				lex.ungetToken(token);
				return l;
			}
		}
		
		throw new ProgramNotEnded(l);
	}
	
	/**
	 * 短絡論理和演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseConditionalOr() throws SyntaxException {
		Expr l = parseConditionalAnd();
		if(l == null) return null;
		
		while(lex.hasToken()) {
			Token token = lex.getToken();
			
			if(token.isType(TokenType.SC_OR)) {
				Expr r = parseConditionalAnd();
				if(r == null) break;
				l = new ShortOr(token, l, r);
			} else {
				lex.ungetToken(token);
				return l;
			}
		}
		
		throw new ProgramNotEnded(l);
	}
	
	/**
	 * 三項演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseTernary() throws SyntaxException {
		Expr cond = parseConditionalOr();
		Token op = lex.getToken();
		if(op == null) return cond;
		
		if(!op.isType(TokenType.QUEST)) {
			lex.ungetToken(op);
			return cond;
		}
		
		Expr l = parseExpression();
		lex.checkToken(TokenType.COLON);
		Expr r = parseTernary();
		
		return new Ternary(cond, l, r);
	}
	
	/**
	 * 代入演算子の式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	private Expr parseAssign() throws SyntaxException {
		Expr id = parseTernary();
		Token op = lex.getToken();
		if(op == null) return id;
		
		if(!op.isType(TokenType.ASSIGN)) {
			lex.ungetToken(op);
			return id;
		}
		
		Expr assn = parseExpression();
		return new Assign(op, id, assn);
	}
	
	/**
	 * 式を構文解析します。
	 * 
	 * @return 式
	 * @throws SyntaxException 構文エラーがある場合
	 */
	public Expr parseExpression() throws SyntaxException {
		return parseAssign();
	}

}
