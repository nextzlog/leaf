/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.lex;

import java.util.ArrayDeque;
import java.util.List;

import leaf.script.falcon.error.SyntaxException;

import static java.lang.Character.*;

/**
 * Falconの字句解析器の実装です。
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public final class Tokenizer {
	private ArrayDeque<Token> queue;
	private ReaderString reader;
	private int line = 1;
	
	private List<TokenType> keywords;
	
	/**
	 * {@link ReaderString}を指定して字句解析器を構築します。
	 * 
	 * @param reader 文字を読み込むReaderString
	 */
	public Tokenizer(ReaderString reader) {
		queue = new ArrayDeque<Token>();
		this.reader = reader;
		keywords = TokenType.getKeywordList();
	}
	
	private boolean isDigit(char ch) {
		switch(ch) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9': return true;
		default: return false;
		}
	}
	
	private Token readNumLiteral(Token token) {
		boolean isInt = true;
		char ch;
		reader.goBack();
		reader.mark();
		loop:
		while((ch = reader.nextChar()) != '\0') {
			if(isDigit(ch) || ch == '_') {
				continue;
			}
			else if(ch == '.' && isInt) {
				isInt = false;
				continue;
			}
			break loop;
		}
		reader.goBack();
		token.setToken(reader.getStringFromMark());
		token.setType(isInt?
			TokenType.INT_LITERAL:
			TokenType.DOUBLE_LITERAL);
		return token;
	}
	
	private Token readPlus(Token token) {
		token.setType(TokenType.PLUS);
		return token;
	}
	
	private Token readMinus(Token token) {
		token.setType(TokenType.MINUS);
		return token;
	}
	
	private Token readMul(Token token) {
		token.setType(TokenType.MUL);
		return token;
	}
	
	private Token readDiv(Token token) {
		token.setType(TokenType.DIV);
		return token;
	}
	
	private Token readRem(Token token) {
		token.setType(TokenType.REM);
		return token;
	}
	
	private Token readAnd(Token token) {
		char next = reader.nextChar();
		if(next == '&') {
			token.setType(TokenType.SC_AND);
		} else {
			token.setType(TokenType.AND);
			reader.goBack();
		}
		return token;
	}
	
	private Token readOr(Token token) {
		char next = reader.nextChar();
		if(next == '|') {
			token.setType(TokenType.SC_OR);
		} else {
			token.setType(TokenType.OR);
			reader.goBack();
		}
		return token;
	}
	
	private Token readCaret(Token token) {
		token.setType(TokenType.CARET);
		return token;
	}
	
	private Token readBang(Token token) {
		char next = reader.nextChar();
		if(next == '=') {
			token.setType(TokenType.NEQ);
		} else {
			token.setType(TokenType.BANG);
			reader.goBack();
		}
		return token;
	}
	
	private Token readLt(Token token) {
		char next = reader.nextChar();
		if(next == '=') {
			token.setType(TokenType.LE);
		} else {
			token.setType(TokenType.LT);
			reader.goBack();
		}
		return token;
	}
	
	private Token readGt(Token token) {
		char next = reader.nextChar();
		if(next == '=') {
			token.setType(TokenType.GE);
		} else {
			token.setType(TokenType.GT);
			reader.goBack();
		}
		return token;
	}
	
	private Token readEq(Token token) {
		char next = reader.nextChar();
		if(next == '=') {
			token.setType(TokenType.EQ);
		} else {
			token.setType(TokenType.ASSIGN);
			reader.goBack();
		}
		return token;
	}
	
	private Token readQuest(Token token) {
		token.setType(TokenType.QUEST);
		return token;
	}
	
	private Token readLparen(Token token) {
		token.setType(TokenType.LPAREN);
		return token;
	}
	
	private Token readRparen(Token token) {
		token.setType(TokenType.RPAREN);
		return token;
	}
	
	private Token readLbrace(Token token) {
		token.setType(TokenType.LBRACE);
		return token;
	}
	
	private Token readRbrace(Token token) {
		token.setType(TokenType.RBRACE);
		return token;
	}
	
	private Token readColon(Token token) {
		token.setType(TokenType.COLON);
		return token;
	}
	
	private Token readSemicolon(Token token) {
		token.setType(TokenType.SEMICOLON);
		return token;
	}
	
	private Token readComma(Token token) {
		token.setType(TokenType.COMMA);
		return token;
	}
	
	private Token readPeriod(Token token) {
		token.setType(TokenType.PERIOD);
		return token;
	}
	
	private Token readKeyword(Token token, String id) {
		for(TokenType kw : keywords) {
			if(id.equals(kw.toString())) {
				token.setType(kw);
				return token;
			}
		}
		return null;
	}
	
	private Token readId(Token token) {
		char ch;
		reader.goBack();
		reader.mark();
		loop:
		while((ch = reader.nextChar()) != '\0') {
			if(isLetterOrDigit(ch)) continue;
			if(ch != '_') break loop;
		}
		reader.goBack();
		String id = reader.getStringFromMark();
		if(readKeyword(token, id) == null) {
			token.setToken(id);
			token.setType(TokenType.ID);
		}
		return token;
	}
	
	private void readLineComment() {
		loop:
		while(true) {
			switch(reader.nextChar()) {
			case '\0': break loop;
			case '\n': break loop;
			}
		}
		this.line++;
	}
	
	private void readBlockComment() {
		char ch;
		int cnt = 0;
		while((ch = reader.nextChar()) != '\0') {
			if(ch == '\n') this.line++;
			else if(ch == '*' && cnt == 0) cnt++;
			else if(ch == '/' && cnt == 1) break;
			else cnt = 0;
		}
	}
	
	private Token getNextToken() throws SyntaxException {
		char ch;
		while((ch = reader.nextChar()) != '\0') {
			if(ch == '\n') this.line++;
			Token token = new Token(this.line);
			if(isWhitespace(ch)) continue;
			if(isDigit(ch))  return readNumLiteral(token);
			if(isLetter(ch)) return readId(token);
			if(ch == '/') {
				char next = reader.nextChar();
				if(next == '/') {
					readLineComment();
					continue;
				}
				if(next == '*') {
					readBlockComment();
					continue;
				}
				reader.goBack();
			}
			switch(ch) {
			case '+': return readPlus     (token);
			case '-': return readMinus    (token);
			case '*': return readMul      (token);
			case '/': return readDiv      (token);
			case '%': return readRem      (token);
			case '&': return readAnd      (token);
			case '|': return readOr       (token);
			case '^': return readCaret    (token);
			case '!': return readBang     (token);
			case '<': return readLt       (token);
			case '>': return readGt       (token);
			case '=': return readEq       (token);
			case '?': return readQuest    (token);
			case '(': return readLparen   (token);
			case ')': return readRparen   (token);
			case '{': return readLbrace   (token);
			case '}': return readRbrace   (token);
			case ':': return readColon    (token);
			case ';': return readSemicolon(token);
			case ',': return readComma    (token);
			case '.': return readPeriod   (token);
			case '_': return readId       (token);
			}
			throw error("unexpected character: " + ch);
		}
		return null;
	}
	
	/**
	 * 次の字句を返します。末尾に到達した場合nullを返します。
	 * 
	 * @return 次の字句 末尾に到達した場合null
	 * @throws SyntaxException 字句違反があった場合
	 */
	public Token getToken() throws SyntaxException {
		if(queue.isEmpty()) return getNextToken();
		return queue.pop();
	}
	
	/**
	 * 次の字句が指定された種類になっているか確認します。
	 * 
	 * @param type 次の字句の種類
	 * @return 種類が一致すればその字句
	 * @throws SyntaxException 字句が指定された種類でない場合
	 */
	public Token checkToken(TokenType type) throws SyntaxException {
		Token token = getToken();
		if(token != null && token.isType(type)) return token;
		throw error("expected token is " + type + " but " + token);
	}
	
	/**
	 * 字句解析器が次の字句を持っているか確認します。
	 * 
	 * @return 次の字句がある場合true
	 * @throws SyntaxException 字句違反があった場合
	 */
	public boolean hasToken() throws SyntaxException {
		Token next = getToken();
		if(next != null) {
			queue.push(next);
			return true;
		}
		return false;
	}
	
	/**
	 * 指定された字句を字句解析器のスタックに待避します。
	 * 
	 * @param token 戻す字句
	 */
	public void ungetToken(Token token) {
		queue.push(token);
	}
	
	/**
	 * 現在の行番号を返します。
	 * 
	 * @return 行番号
	 */
	public int getLine() {
		return line;
	}
	
	private SyntaxException error(String msg) {
		return new SyntaxException(msg, this.line);
	}

}