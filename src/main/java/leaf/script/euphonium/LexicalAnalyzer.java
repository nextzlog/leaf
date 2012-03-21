/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium;

import java.io.*;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import javax.script.ScriptException;

import leaf.manager.LeafLocalizeManager;

/**
 *AriCE処理系の字句解析器の実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.1 作成：2011年6月27日 搭載：2010年9月28日
 */
final class LexicalAnalyzer extends CodesAndTokens{
	private final HashMap<String, Keyword> keywords;
	private final HashMap<String, Operator> operators;
	private final LinkedList<String> operatorList;
	
	private final ArrayDeque<Token> stack;
	private LineNumberReader reader;
	private String line;
	private int index;
	
	private final LeafLocalizeManager localize;
	
	/**
	*字句解析器を構築します。
	*/
	public LexicalAnalyzer(){
		stack = new ArrayDeque<Token>();
		keywords  = new HashMap<String, Keyword>();
		operators = new HashMap<String, Operator>();
		operatorList = new LinkedList<String>();
		
		for(Keyword  kw :  Keyword.values())
			keywords.put (kw.toString(), kw);
		for(Operator op : Operator.values()){
			operators.put(op.toString(), op);
			operatorList.add(op.toString());
		}
		localize = LeafLocalizeManager.getInstance(getClass());
	}
	/**
	*スクリプトを読み込むリーダを設定します。
	*@param source リーダ
	*@throws ScriptException 読み込みに失敗した場合
	*/
	public void load(Reader source) throws ScriptException{
		reader = new LineNumberReader(source);
		readNextLine();
	}
	/**
	*スクリプトを読み込むリーダーを閉じます。
	*@throws ScriptException 閉じるのに失敗した場合
	*/
	private void closeReader() throws ScriptException{
		try{
			reader.close();
		}catch(IOException ex){
			throw new ScriptException(ex);
		}
	}
	/**
	*次の行をリーダーから読み込みます。
	*@throws ScriptException 読み込み例外時
	*/
	private void readNextLine() throws ScriptException{
		try{
			while((line = reader.readLine())!=null){
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
	/**
	*指定された桁位置の文字を返します。
	*@param column 桁
	*@return 取得した文字
	*@throws ScriptException 読み込み例外時
	*/
	private char charAt(int column) throws ScriptException{
		try{
			return line.charAt(column);
		}catch(Exception ex){
			return '\0';
		}
	}
	/**
	*字句に指定文字を追加すると演算子の一部になるか検査します。
	*@param token 検査対象の字句
	*@param ch 追加する文字
	*@return 演算子の場合true
	*/
	private boolean isOperator(Token token, char ch){
		String target = token + Character.toString(ch);
		for(String op : operatorList){
			if(op.indexOf(target) >= 0) return true;
		}
		return false;
	}
	/**
	*字句に対応する演算子を返します。
	*@param token 字句
	*@return 対応する演算子
	*/
	private Enum getOperator(Token token){
		return operators.get(token.toString());
	}
	/**
	*字句が予約語かどうか返します。
	*@param token 字句
	*@return 予約語ならtrue
	*/
	private boolean isKeyword(Token token){
		Keyword kw = keywords.get(token.toString());
		if(kw != null) token.setType(kw);
		return kw != null;
	}
	/**
	*字句違反があった場合に例外を通知します。
	*@param ch 字句違反があった場所の文字
	*@return 例外を生成します
	*/
	private ScriptException error(char ch){
		index++; return error("error_char_exception", ch);
	}
	/**
	*字句違反があった場合に例外を通知します。
	*@param key メッセージの国際化キー
	*@param args メッセージの引数
	*@return 生成した例外
	*/
	private ScriptException error(String key, Object... args){
		int line = reader.getLineNumber();
		String msg =  localize.translate(key, args)
		+ " at line : " + line + "\n => " + getLine();
		return new ScriptException(msg, null, line, index+1);
	}
	/**
	*次の字句を取得します。ソースコードの
	*終端に到達した場合、nullを返します。
	*@return 次の字句
	*@throws ScriptException
	*読み込みに失敗した場合もしくは字句違反がある場合
	*/
	public Token getNextToken() throws ScriptException{
		if(stack.size()>0) return stack.pop();
		return read(new Token());
	}
	/**
	*取得済みの字句をバッファに待避します。
	*@param token 待避する字句
	*/
	public void ungetToken(Token token){
		stack.push(token);
	}
	/**
	*指定した文字が10進数半角数字であるか返します。
	*@param ch 対象の文字
	@return 10進数半角数字であるならtrue
	*/
	private boolean isDigit(char ch){
		switch(ch){
		case '0' : case '1' : case '2' :
		case '3' : case '4' : case '5' :
		case '6' : case '7' : case '8' :
		case '9' : return true;
		}
		return false;
	}
	/**
	*指定した文字が16進数半角数字であるか返します。
	*@param ch 対象の文字
	@return 16進数半角数字であるならtrue
	*/
	private boolean isHexDigit(char ch){
		switch(ch){
		case '0' : case '1' : case '2' :
		case '3' : case '4' : case '5' :
		case '6' : case '7' : case '8' :
		case '9' : case 'a' : case 'b' :
		case 'c' : case 'd' : case 'e' :
		case 'f' : case 'A' : case 'B' : 
		case 'C' : case 'D' : case 'E' :
		case 'F' : return true;
		}
		return false;
	}
	/**
	*指定した文字が8進数半角数字であるか返します。
	*@param ch 対象の文字
	@return 8進数半角数字であるならtrue
	*/
	private boolean isOctDigit(char ch){
		switch(ch){
		case '0' : case '1' : case '2' :
		case '3' : case '4' : case '5' :
		case '6' : case '7' : return true;
		}
		return false;
	}
	/**
	*次の字句を解析して取得します。リーダー
	*が終端に到達した場合、nullを返します。
	*@param token 読み込み中の字句
	*@return 次の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private Token read(Token token) throws ScriptException{
		loop: while(line != null){
			if(index >= line.length()) readNextLine();
			char ch = charAt(index++);
			if(ch == '0') return readZero(token);
			if(isDigit(ch))
				return readInteger(token.append(ch));
			if(Character.isJavaIdentifierStart(ch))
				return readIdentifier(token.append(ch));
			switch(ch){
				case '\0': break loop;
				case '\'': return readCharacter(token);
				case '\"': return readString(token);
				case '/' : {
					switch(charAt(index++)){
						case '/':
							readNextLine();
							continue loop;
						case '*':
							skipBlockComment();
							continue loop;
						default : index--;
					}
				}
			}
			if(isOperator(token, ch))
				return readOperator(token.append(ch));
			if(!Character.isWhitespace(ch)) throw error(ch);
		}
		if(line != null) readNextLine();
		return null;
	}
	/**
	*0で始まる数定数を取得します。
	*@param token 読み取り中の字句
	*@return 次の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private Token readZero(Token token) throws ScriptException{
		char ch = charAt(index++);
		if(ch == 'x' || ch == 'X') return readHexInteger(token);
		if(isDigit(ch)) return readOctInteger(token.append(ch));
		if(ch == '.' && isDigit(charAt(index)))
			return readDouble(token.append('.'));
		index--; return token.append('0').setType(TokenType.INTEGER);
	}
	/**
	*10進整定数を取得します。
	*@param token 読み込み中の字句
	*@return 次の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private Token readInteger(Token token) throws ScriptException{
		while(true){
			char ch = charAt(index++);
			if(ch == 'd' || ch == 'D')
				return token.setType(TokenType.DOUBLE);
			if(ch == '.' && isDigit(charAt(index)))
				return readDouble(token.append(ch));
			if(isDigit(ch)) token.append(ch);
			else if(ch != '_') break;
		}
		index--; return token.setType(TokenType.INTEGER);
	}
	/**
	*16進整定数を取得します。
	*@param token 読み込み中の字句
	*@return 次の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private Token readHexInteger(Token token) throws ScriptException{
		for(; true; index++){
			char ch = charAt(index);
			if(isHexDigit(ch)) token.append(ch);
			else if(ch != '_') break;
		}
		token = new Token(token.toInteger(16));
		return token.setType(TokenType.INTEGER);
	}
	/**
	* 8進整定数を取得します。
	*@param token 読み込み中の字句
	*@return 次の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private Token readOctInteger(Token token) throws ScriptException{
		for(; true; index++){
			char ch = charAt(index);
			if(isOctDigit(ch)) token.append(ch);
			else if(ch != '_') break;
		}
		token = new Token(token.toInteger(8));
		return token.setType(TokenType.INTEGER);
	}
	/**
	*10進小数定数を取得します。
	*@param token 読み込み中の字句
	*@return 次の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private Token readDouble(Token token) throws ScriptException{
		while(true){
			char ch = charAt(index++);
			if(ch == 'd' || ch == 'D') break;
			if(isDigit(ch)) token.append(ch);
			else if(ch != '_'){
				index--; break;
			}
		}
		return token.setType(TokenType.DOUBLE);
	}
	/**
	*文字定数を取得します。
	*@param token 読み込み中の字句
	*@return 次の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private Token readCharacter(Token token) throws ScriptException{
		char ch = charAt(index++);
		if(ch == '\\') readEscape(token);
		else token.append(ch);
		ch = charAt(index++);
		if(ch != '\'') throw error(ch);
		return token.setType(TokenType.CHARACTER);
	}
	/**
	*文字列定数を取得します。
	*@param token 読み込み中の字句
	*@return 次の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private Token readString(Token token) throws ScriptException{
		loop: while(true){
			char ch = charAt(index++);
			switch(ch){
			case '\0': throw error("readString_exception");
			case '\"': break loop;
			case '\\': readEscape(token); break;
			default  : token.append(ch);
			}
		}
		return token.setType(TokenType.STRING);
	}
	/**
	*エスケープ文字を取得します。
	*@param token 読み込み中の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private void readEscape(Token token) throws ScriptException{
		char ch = charAt(index++);
		switch(ch){
		case'\0' : throw  error(ch);
		case 'n' : ch = '\n'; break;
		case 'r' : ch = '\r'; break;
		case 't' : ch = '\t'; break;
		case 'f' : ch = '\f'; break;
		case 'b' : ch = '\b'; break;
		case '0' : ch = '\0'; break;
		}
		if(ch == 'u'){
			char[] codes = new char[4];
			for(int i=0;i<4;i++) codes[i] = charAt(index++);
			ch = (char)Integer.parseInt(new String(codes),16);
		}
		token.append(ch);
	}
	/**
	*識別子を取得します。
	*@param token 読み込み中の字句
	*@return 次の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private Token readIdentifier(Token token) throws ScriptException{
		for(; true; index++){
			char ch = charAt(index);
			if(ch == '\0' || !Character.isJavaIdentifierPart(ch)) break;
			else token.append(ch);
		}
		if(isKeyword(token)) return token;
		return token.setType(TokenType.IDENTIFIER);
	}
	/**
	*演算子を取得します。
	*@param token 読み込み中の字句
	*@return 次の字句
	*@throws ScriptException 字句違反があった場合
	*/
	private Token readOperator(Token token) throws ScriptException{
		for(; true; index++){
			char ch = charAt(index);
			if(ch == '\0' || !isOperator(token, ch)) break;
			else token.append(ch);
		}
		return token.setType(getOperator(token));
	}
	/**
	*コメント終了符号に到達するまでブロックコメントを無視します。
	*@throws ScriptException 読み込み例外時
	*/
	private void skipBlockComment() throws ScriptException{
		while(true){
			char ch = charAt(index++);
			if(index >= line.length()) readNextLine();
			else if(ch == '*' && charAt(index) == '/') break;
		}
		if(++index >= line.length()) readNextLine();
	}
	/**
	*次の字句が適切な型の場合に字句を返します。
	*文脈によって予約語も識別子として有効です。
	*@param expected 期待される型
	*@return 次の型
	*@throws ScriptException 期待されない型の場合
	*/
	public Token checkNextToken(Enum... expected)
	throws ScriptException{
		Token next = getNextToken();
		if(next != null){
			for(Enum exp : expected){
				if(next.getType() == exp) return next;
				if(exp == TokenType.IDENTIFIER
				&& next.isIdentifier())   return next;
			}
		}
		throw error("checkNextToken_exception", next);
	}
	/**
	*字句解析器が現在読み込んでいる行の文字列を返します。
	*@return 現在の行のスクリプト
	*/
	public String getLine(){
		return (line != null)? line : "[EOF]";
	}
	/**
	*字句解析器が現在読み込んでいる部分の行番号を返します。
	*@return 1から始まる行番号
	*/
	public int getLineNumber(){
		return reader.getLineNumber();
	}
	/**
	*字句解析器が現在読み込んでいる部分の桁番号を返します。
	*@return 1から始まる桁番号
	*/
	public int getColumnNumber(){
		return index + 1;
	}
}