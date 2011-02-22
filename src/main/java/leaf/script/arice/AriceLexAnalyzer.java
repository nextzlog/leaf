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
public class AriceLexAnalyzer{

	private int index;
	private int lineNum = 1;
	private int columnNum = 1;
	private final String script;
	
	private final AriceParser.Operators[] opers
		= AriceParser.Operators.values();
	private final AriceParser.Keywords[]  keys
		= AriceParser.Keywords.values();
	
	private int state;
	private final int INITIAL_STATE    = 0;
	private final int INT_VALUE_STATE  = 1;
	private final int IDENTIFIER_STATE = 2;
	private final int STRING_STATE     = 3;
	private final int OPERATOR_STATE   = 4;
	private final int COMMENT_STATE    = 5;
	
	/**
	*スクリプトを指定してレキシカルアナライザを生成します。
	*@param script 解析対象のスクリプト
	*/
	public AriceLexAnalyzer(String script){
		this.script = script;
	}
	/**
	*スクリプトから次のトークンを取り出します。
	*@return トークン
	*@throws ScriptException 構文エラーがあった場合
	*/
	public Token getNextToken() throws ScriptException{
		
		Token token = new Token();
		state = INITIAL_STATE;
		char ch;
		
		if(index >= script.length()) return null;
		
		loop:
		for(;index<script.length();index++){
			ch = script.charAt(index);
			if(ch == '\n'){
				lineNum ++;
				columnNum = 1;
			}else{
				columnNum ++;
			}
			
			switch(state){
				case INITIAL_STATE:
					if(Character.isDigit(ch)){
						token.append(ch);
						state = INT_VALUE_STATE;
					}else if(Character.isLetter(ch) || ch == '_'){
						token.append(ch);
						state = IDENTIFIER_STATE;
					}else if(ch == '\"'){
						state = STRING_STATE;
					}else if(isOperator(token, ch)){
						token.append(ch);
						state = OPERATOR_STATE;
					}else if(ch == '#'){
						state = COMMENT_STATE;
					}else if(!Character.isWhitespace(ch)){
						throw error(ch);
					}
					break;
				case INT_VALUE_STATE:
					if(Character.isDigit(ch)){
						token.append(ch);
					}else{
						token.setType(AriceParser.OtherTokens.INT);
						break loop;
					}
					break;
				case IDENTIFIER_STATE:
					if(Character.isLetterOrDigit(ch) || ch == '_'){
						token.append(ch);
					}else{
						break loop;
					}
					break;
				case STRING_STATE:
					if(ch == '\"'){
						token.setType(AriceParser.OtherTokens.STRING);
						index ++;
						break loop;
					}else{
						token.append(ch);
					}
					break;
				case OPERATOR_STATE:
					if(isOperator(token,ch)){
						token.append(ch);
					}else{
						break loop;
					}
					break;
				case COMMENT_STATE:
					if(ch == '\n' || ch == '\n'){
						state = INITIAL_STATE;
					}
					break;
				default:
			}
		}
		if(state == IDENTIFIER_STATE && !isKeyword(token)){
			token.setType(AriceParser.OtherTokens.IDENTIFIER);
		}else if(state == OPERATOR_STATE){
			token.setType(getOperator(token.toString()));
		}
		if(token.getValue().length()==0) return null;
		return token;
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
		return new ScriptException(
			"Lexical Error : Cannot use \"" + Character.toString(ch) + "\" here.",
			null, lineNum, columnNum
		);
	}
	/**
	*レキシカルアナライザが現在参照している行番号を返します。
	*@return 1から始まる行番号
	*/
	public int getLineNumber(){
		return lineNum;
	}
	/**
	*レキシカルアナライザが現在参照している桁番号を返します。
	*@return 1から始まる桁番号
	*/
	public int getColumnNumber(){
		return columnNum;
	}
}