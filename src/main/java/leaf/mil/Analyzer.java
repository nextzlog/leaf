/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.mil;

/**レキシカルアナライザ*/
class Analyzer{

	private int index;
	private int current_line;
	private final String script;
	
	/**演算子/制御文*/
	private final Parser.Operators[] opers
		= Parser.Operators.values();
	private final Parser.Keywords[] keys
		= Parser.Keywords.values();
	
	/**状態遷移の定義*/
	private int state;
	private final int INITIAL_STATE     = 0;
	private final int INT_VALUE_STATE   = 1;
	private final int IDENTIFIER_STATE  = 2;
	private final int STRING_STATE      = 3;
	private final int OPERATOR_STATE    = 4;
	private final int COMMENT_STATE     = 5;
	
	/**コンストラクタ*/
	public Analyzer(String script){
		this.script = script;
	}
	/**トークン取り出し*/
	public Token getNext(){
		
		Token token = new Token();
		state = INITIAL_STATE;
		char ch;
		
		if(index >= script.length())
			return null;
		
		loop:
		for(;index<script.length();index++){
			ch = script.charAt(index);
			switch(state){
			case INITIAL_STATE:
				if(Character.isDigit(ch)){//数字
					token.add(ch);
					state = INT_VALUE_STATE;
				}else if(Character.isLetter(ch)){//英字
					token.add(ch);
					state = IDENTIFIER_STATE;
				}else if(ch == '\"'){//文字列
					state = STRING_STATE;
				}else if(isOperator(token,ch)){
					token.add(ch);
					state = OPERATOR_STATE;
				}else if(Character.isWhitespace(ch)){//空白
					if(ch == '\n'){//改行
						current_line ++;
					}
				}else if(ch == '#'){//コメント
					state = COMMENT_STATE;
				}else{
					error("Character out of order");
				}break;
			case INT_VALUE_STATE:
				if(Character.isDigit(ch)){
					token.add(ch);
				}else{
					token.setType(Parser.OtherTokens.INT);
					break loop;
				}break;
			case IDENTIFIER_STATE:
				if(Character.isLetterOrDigit(ch) || ch == '_'){
					token.add(ch);
				}else{
					break loop;
				}break;
			case STRING_STATE:
				if(ch == '\"'){
					token.setType(Parser.OtherTokens.STRING);
					index ++;
					break loop;
				}else{
					token.add(ch);
				}break;
			case OPERATOR_STATE:
				if(isOperator(token,ch)){
					token.add(ch);
				}else{
					break loop;
				}break;
			case COMMENT_STATE:
				if(ch == '\n'){
					state = INITIAL_STATE;
				}break;
			default:
			}
		}
		if(state == IDENTIFIER_STATE && !isKeyword(token)){
			token.setType(Parser.OtherTokens.IDENTIFIER);
		}else if(state == OPERATOR_STATE){
			token.setType(getOperator(token.toString()));
		}
		return token;
	}
	
	/**1文字追加して演算子かどうか検索*/
	private boolean isOperator(Token token,char ch){
		for(int i=0;i<opers.length;i++){
			if(opers[i].toString().equals(
				token+Character.toString(ch)))return true;
		}
		return false;
	}
	
	/**演算子を取り出す*/
	private Enum getOperator(String token){
		
		for(Enum oper : opers){
			if(token.equals(oper.toString()))
				return oper;
		}
		return null;
	}
	
	/**キーワードかどうか返す*/
	private boolean isKeyword(Token token){
		for(int i=0;i<keys.length;i++){
			if(keys[i].toString().
				equals(token.toString())){
				token.setType(keys[i]);
				return true;
			}
		}
		return false;
	}
	
	/**エラー通知*/
	private void error(String msg){
		System.out.println("Lex Error:"
			+ msg + " at line." + current_line);
	}
	
	/**現在の行番号を返す*/
	public int getLineNumber(){
		return current_line;
	}
}