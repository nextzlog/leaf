/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.arice;

import java.util.Arrays;
import javax.script.ScriptException;

/**
*AriCE言語のレキシカルアナライザの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月28日
*/
final class AriceLexAnalyzer extends AriceCodesAndTokens{

	private int index, line, column, start;
	private String script;
	
	private final Operators[] opers = Operators.values();
	private final Keywords [] keys  = Keywords.values();
	
	private int state;
	private final int INITIAL       = 0;
	private final int INTEGER       = 1;
	private final int DOUBLE        = 2;
	private final int CHARACTER     = 3;
	private final int STRING        = 4;
	private final int ESCAPE        = 5;
	private final int IDENTIFIER    = 6;
	private final int OPERATOR      = 7;
	private final int LINE_COMMENT  = 8;
	private final int BLOCK_COMMENT = 9;
	
	private Token next = null;
	
	/**
	*ソースコードを読み込みます。
	*@param script ソースコード
	*/
	public void load(String script){
		this.script = script.replaceAll("(\r\n|\r)","\n");
		index = start  = 0;
		line  = column = 1;
	}
	/**
	*スクリプトから次のトークンを取り出します。
	*@return トークン
	*@throws ScriptException 構文エラーがあった場合
	*/
	private Token read() throws ScriptException{
		
		Token token = new Token();
		state = INITIAL;
		char ch;
		
		loop:
		for(;index<script.length();index++){
			ch = script.charAt(index);
			switch(state){
			case INITIAL:
				if(Character.isDigit(ch)){
					token.append(ch);
					state = INTEGER;
					break;
				}else if(Character.isLetter(ch) || ch == '_'){
					token.append(ch);
					state = IDENTIFIER;
					break;
				}else if(ch == '\''){
					state = CHARACTER;
					break;
				}else if(ch == '\"'){
					state = STRING;
					break;
				}else if(ch == '/'){
					char next = charAt(index+1);
					if(next == '/'){
						state = LINE_COMMENT;
						index++;
						break;
					}else if(next == '*'){
						state = BLOCK_COMMENT;
						index++;
						break;
					}
				}if(isOperator(token, ch)){
					token.append(ch);
					state = OPERATOR;
				}else if(!Character.isWhitespace(ch)){
					throw error(ch);
				}
				break;
			case INTEGER:
				if(Character.isDigit(ch)){
					token.append(ch);
				}else if(ch == '.'
					&& Character.isDigit(charAt(index+1))){
					token.append(ch);
					state = DOUBLE;
				}else{
					token.setType(Tokens.INTEGER);
					break loop;
				}
				break;
			case DOUBLE:
				if(Character.isDigit(ch)){
					token.append(ch);
				}else{
					token.setType(Tokens.DOUBLE);
					break loop;
				}
				break;
			case CHARACTER:
				if(ch == '\\'){
					ch = charAt(++index);
					if(ch == 'n') ch = '\n';
					if(ch == 'r') ch = '\r';
					if(ch == 't') ch = '\t';
				}
				token.append(ch);
				token.setType(Tokens.CHARACTER);
				ch = charAt(++index);
				if(ch != '\''){
					throw error(ch);
				}
				index++;
				break loop;
			case STRING:
				if(ch == '\"'){
					token.setType(Tokens.STRING);
					index ++;
					break loop;
				}else if(ch == '\\'){
					state = ESCAPE;
				}else if(ch == '\n'){
					throw error(ch);
				}else{
					token.append(ch);
				}
				break;
			case ESCAPE:
				if(ch =='\n')throw error(ch);
				else if(ch == 'n') ch = '\n';
				else if(ch == 'r') ch = '\r';
				else if(ch == 't') ch = '\t';
				else if(ch == 'f') ch = '\f';
				else if(ch == 'b') ch = '\b';
				else if(ch == '0') ch = '\0';
				else if(ch == 'u') {
					int code = 0;
					for(int i=0;i<4;i++){
						ch = Character.toLowerCase(charAt(++index));
						if(ch>='0'&&ch<='9'){
							code = code * 16 + (ch-'0');
						}else if(ch>='a'&&ch<='f'){
							code = code * 16 + (ch-'a'+10);
						}else throw error(ch);
					}ch = (char)code;
				}
				token.append(ch);
				state = STRING;
				break;
			case IDENTIFIER:
				if(Character.isLetterOrDigit(ch) || ch == '_'){
					token.append(ch);
				}else{
					break loop;
				}
				break;
			case OPERATOR:
				if(isOperator(token,ch)){
					token.append(ch);
				}else{
					break loop;
				}
				break;
			case LINE_COMMENT:
				if(ch == '\n' || ch == '\n'){
					state = INITIAL;
				}
				break;
			case BLOCK_COMMENT:
				if(ch == '/' && charAt(index-1) == '*'){
					state = INITIAL;
				}
			}
			if(ch == '\n'){
				line ++;
				column = 1;
				start  = index + 1;
			}else{
				column ++;
			}
		}
		if(state == IDENTIFIER && !isKeyword(token)){
			token.setType(Tokens.IDENTIFIER);
		}else if(state == OPERATOR){
			token.setType(getOperator(token.toString()));
		}
		if(token.getType() == null) return null;
		return token;
	}
	/**
	*指定位置の文字を読み込みます。
	*@param index 位置
	*@return 文字
	*@throws ScriptException 位置が無効の場合
	*/
	private char charAt(int index) throws ScriptException{
		if(index < script.length() && index >= 0)return script.charAt(index);
		else throw error("Finished in the middle of source code.");
	}
	/**
	*指定されたトークンに指定された文字を追加すると演算子の一部になるか検査します。
	*@param token 検査対象のトークン
	*@param ch 追加する文字
	*@return 演算子の場合true
	*/
	private boolean isOperator(Token token, char ch){
		String target = token + Character.toString(ch);
		for(int i=0;i<opers.length;i++){
			if(opers[i].toString().indexOf(target)>=0) return true;
		}
		return false;
	}
	/**
	*指定されたトークンに対応する演算子の列挙型要素を取り出します。
	*@param token トークン
	*@return トークンに対し定義される列挙型の要素
	*/
	private Enum getOperator(String token){
		for(Enum oper: opers){
			if(token.equals(oper.toString())) return oper;
		}
		return null;
	}
	/**
	*指定されたトークンがキーワードとして登録されているかどうか返します。
	*@param token 検査対象のトークン
	*@return キーワードならtrue
	*/
	private boolean isKeyword(Token token){
		for(int i=0;i<keys.length;i++){
			if(token.equals(keys[i].toString())){
				token.setType(keys[i]);
				return true;
			}
		}
		return false;
	}
	/**
	*構文違反があった場合に例外を通知します。
	*@param ch 構文違反があった場所の文字
	*@return 例外を生成します。
	*/
	private ScriptException error(char ch){
		index++;
		return error("Character \'" + ch + "\' is illegal here.");
	}
	/**
	*構文違反があった場合に例外を通知します。
	*@return 生成した例外
	*/
	private ScriptException error(String msg){
		return new ScriptException(
			msg+" at line : "+line+"\n => "+getLine(),null,line,column
		);
	}
	/**
	*次のトークンを取得します。
	*@return 次のトークン
	*@throws ScriptException 構文エラーがあった場合
	*/
	public Token getNextToken() throws ScriptException{
		Token ret = next;
		if(next != null){
			next = null;
			return ret;
		}else{
			return read();
		}
	}
	/**
	*一度取得したトークンを押し戻して待避させておきます。
	*@param token 待避させるトークン
	*/
	public void ungetToken(Token token){
		next = token;
	}
	/**
	*次のトークンが適切な型であるか確認します。
	*文脈によっては予約語も識別子となることに注意が必要です。
	*@param expected 期待されるトークンの型
	*@return 次のトークン
	*@throws IOException 期待されないトークンが検出された場合
	*/
	public Token checkNextToken(Enum... expected) throws ScriptException{
		Token next = getNextToken();
		if(next != null){
			for(Enum exp : expected){
				if(next.getType() == exp) return next;
				if(exp==Tokens.IDENTIFIER&&next.isIdentifier())return next;
			}
		}
		throw error("\"" + next + "\" is not expected here.");
	}
	/**
	*構文違反用に行を返します。
	*@return 現在の行
	*/
	protected String getLine(){
		return script.substring(start, index);
	}
	/**
	*レキシカルアナライザが現在参照している行番号を返します。
	*@return 1から始まる行番号
	*/
	public int getLineNumber(){
		return line;
	}
	/**
	*レキシカルアナライザが現在参照している桁番号を返します。
	*@return 1から始まる桁番号
	*/
	public int getColumnNumber(){
		return column;
	}
}