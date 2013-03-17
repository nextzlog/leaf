/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayDeque;
import javax.script.ScriptException;

/**
 * LISP処理系のS式リーダー部の字句解析器です。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
final class Lex {
	private final ArrayDeque<Token> stack;
	private LineNumberReader reader;
	private String line;
	private int index;
	
	public Lex(){
		stack = new ArrayDeque<Token>();
	}
	
	public void load(Reader source) throws ScriptException{
		reader = new LineNumberReader(source);
		readNextLine();
	}
	
	private void closeReader() throws ScriptException{
		try{
			reader.close();
		}catch(IOException ex){
			throw new ScriptException(ex);
		}
	}
	
	private void readNextLine() throws ScriptException{
		try{
			while((line = reader.readLine()) != null){
				if(!line.isEmpty()) break;
			}
			index = 0;
		}catch(IOException ex){
			closeReader();
			throw new ScriptException(ex);
		}finally{
			if(line == null) closeReader();
		}
	}
	
	private char charAt(int column){
		try{
			return line.charAt(column);
		}catch(Exception ex){
			return '\0';
		}
	}
	
	public Token getNext() throws ScriptException{
		if(stack.isEmpty()) return read(new Token());
		return stack.pop();
	}
	
	public void unget(Token token){
		stack.push(token);
	}
	
	public Token checkNext(TokenType... expected) throws ScriptException{
		Token next = getNext();
		if(next != null){
			for(TokenType exp : expected){
				if(next.getType() == exp) return next;
			}
		}
		throw error("unexpected token:" + next);
	}
	
	private boolean isDigit(char ch){
		switch(ch){
		case '0' : case '1' : case '2' :
		case '3' : case '4' : case '5' :
		case '6' : case '7' : case '8' :
		case '9' : return true;
		}
		return false;
	}
	
	private Token read(Token token) throws ScriptException {
		while(line != null){
			if(index >= line.length()) readNextLine();
			char ch = charAt(index++);
			
			if(Character.isWhitespace(ch)) continue;
			switch(ch){
			case '"' : return readString(token);
			case '(' : return readLeftBrace(token);
			case ')' : return readRightBrace(token);
			case '-' : return readMinus(token.append(ch));
			case '.' : return readDot(token);
			case '\'': return readQuote(token);
			case '\0': return null;
			}
			if(isDigit(ch)) return readInt(token.append(ch));
			return readSymbol(token.append(ch));
		}
		return null;
	}
	
	private Token readDot(Token token){
		return token.append('.').setType(TokenType.DOT);
	}
	
	private Token readQuote(Token token){
		return token.append('\'').setType(TokenType.QUOTE);
	}
	
	private Token readLeftBrace(Token token){
		return token.append('(').setType(TokenType.L_BRACE);
	}
	
	private Token readRightBrace(Token token){
		return token.append(')').setType(TokenType.R_BRACE);
	}
	
	private Token readInt(Token token){
		while(true){
			char ch = charAt(index++);
			if(isDigit(ch)) token.append(ch);
			else if(ch == '.') return readReal(token);
			else if(ch != '_') break;
		}
		index--;
		return token.setType(TokenType.REAL);
	}
	
	private Token readReal(Token token){
		token.append('.');
		while(true){
			char ch = charAt(index++);
			if(isDigit(ch)) token.append(ch);
			else if(ch != '_') break;
		}
		index--;
		return token.setType(TokenType.REAL);
	}
	
	private Token readMinus(Token token){
		char ch = charAt(index);
		if(isDigit(ch)) return readInt(token);
		return readSymbol(token);
	}
	
	private Token readString(Token token) throws ScriptException{
		while(true){
			char ch = charAt(index++);
			if(ch == '\0') throw error(ch);
			if(ch == '\"') break;
			if(ch == '\\') readEscape(token);
			else token.append(ch);
		}
		return token.setType(TokenType.STRING);
	}
	
	private void readEscape(Token token) throws ScriptException{
		char ch = charAt(index++);
		switch(ch){
		case '\0': throw error(ch);
		case 'n' : ch = '\n'; break;
		case 'r' : ch = '\r'; break;
		case 't' : ch = '\t'; break;
		case 'f' : ch = '\f'; break;
		case 'b' : ch = '\b'; break;
		case '0' : ch = '\0'; break;
		}
		token.append(ch);
	}
	
	private Token readSymbol(Token token){
		while(true){
			char ch = charAt(index++);
			if(ch == '(' || ch == ')' || ch == '\0'
			|| Character.isWhitespace(ch)){
				index--;
				return token.setType(TokenType.SYMBOL);
			}else token.append(ch);
		}
	}
	
	private ScriptException error(char ch){
		return error("lexical error:" + ch);
	}
	
	private ScriptException error(String msg){
		int ln = reader.getLineNumber();
		String message = msg + " at line:" + ln
		+ "\n=> " + (line != null? line : "[EOF]");
		return new ScriptException(message, null, ln, index+1);
	}
}