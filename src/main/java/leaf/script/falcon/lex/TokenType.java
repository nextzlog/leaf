/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.lex;

import java.util.ArrayList;
import java.util.List;

public enum TokenType {
	DOUBLE_LITERAL,
	INT_LITERAL,
	ID,
	PLUS     ("+"),
	MINUS    ("-"),
	MUL      ("*"),
	DIV      ("/"),
	REM      ("%"),
	AND      ("&"),
	OR       ("|"),
	CARET    ("^"),
	SC_AND   ("&&"),
	SC_OR    ("||"),
	BANG     ("!"),
	LT       ("<"),
	GT       (">"),
	LE       ("<="),
	GE       (">="),
	EQ       ("=="),
	NEQ      ("!="),
	QUEST    ("?"),
	LPAREN   ("("),
	RPAREN   (")"),
	LBRACE   ("{"),
	RBRACE   ("}"),
	COLON    (":"),
	SEMICOLON(";"),
	COMMA    (","),
	PERIOD   ("."),
	ASSIGN   ("="),
	BOOLEAN  ("boolean" , true),
	BREAK    ("break"   , true),
	CONTINUE ("continue", true),
	DEFINE   ("define"  , true),
	DOUBLE   ("double"  , true),
	ELSE     ("else"    , true),
	FALSE    ("false"   , true),
	IF       ("if"      , true),
	IMPORT   ("import"  , true),
	INT      ("int"     , true),
	NEW      ("new"     , true),
	PRINT    ("print"   , true),
	RETURN   ("return"  , true),
	TRUE     ("true"    , true),
	WHILE    ("while"   , true);
	
	public final String token;
	public final boolean isKeyword;
	
	TokenType() {
		this(null, false);
	}
	
	TokenType(String token) {
		this(token, false);
	}
	
	TokenType(String token, boolean kw) {
		this.token = token;
		this.isKeyword = kw;
	}
	
	@Override
	public String toString() {
		return token != null? token : name();
	}

	/**
	 * 予約語に指定されている列挙子のリストを返します。
	 * 
	 * @return 予約語のリスト
	 */
	public static List<TokenType> getKeywordList() {
		List<TokenType> list = new ArrayList<TokenType>();
		for(TokenType t : values()) {
			if(t.isKeyword) list.add(t);
		}
		return list;
	}

}
