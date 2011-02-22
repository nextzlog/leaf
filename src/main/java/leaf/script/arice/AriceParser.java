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

import java.util.*;
import javax.script.ScriptException;

/**
*AriCEコンパイラの構文パーサの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月28日
*/
public class AriceParser{
	
	/**
	*演算子の定義です。
	*/
	public static enum Operators {
		EQUAL        ("=="),
		NOT_EQUAL    ("!="),
		GREATER      (">"),
		GREATER_EQUAL(">="),
		LESS         ("<"),
		LESS_EQUAL   ("<="),
		ADD          ("+"),
		SUB          ("-"),
		MUL          ("*"),
		DIV          ("/"),
		MOD          ("%"),
		AND          ("&"),
		OR           ("|"),
		XOR          ("^"),
		ASSIGN       ("="),
		OPEN_PARENS  ("("),
		CLOSE_PARENS (")"),
		OPEN_BRACE   ("{"),
		CLOSE_BRACE  ("}"),
		COMMA        (","),
		SEMICOLON    (";");
		
		private String desc;
		/**コンストラクタ*/
		private Operators(String desc){
			this.desc = desc;
		}
		/**文字列化*/
		public String toString(){
			return desc;
		}
	}
	/**
	*制御キーワードの定義です。
	*/
	public static enum Keywords {
		IF     ("if"),
		ELSE   ("else"),
		WHILE  ("while"),
		CALL   ("call"),
		FUNCT  ("routine"),
		RETURN ("return"),
		WRITE  ("write"),
		WRITELN("writeln"),
		EXIT   ("exit");
		
		private String desc;
		/**コンストラクタ*/
		private Keywords(String desc){
			this.desc = desc;
		}
		/**文字列化*/
		public String toString(){
			return desc;
		}
	}
	/**
	*その他のトークンの定義です。
	*/
	public static enum OtherTokens {
		INT, IDENTIFIER, STRING
	}
	
	private final ArrayList<Code>   bytecodes;
	private final ArrayList<String> varnames;
	private final ArrayList<Label>  labels;
	
	private Token nextToken = null;
	private final AriceLexAnalyzer analyzer;
	
	/**
	*スクリプトを指定してパーサーを生成します。
	*@param script 解析対象のスクリプト
	*/
	public AriceParser(String script){
		analyzer  = new AriceLexAnalyzer(script);
		bytecodes = new ArrayList<Code>(8192);
		varnames  = new ArrayList<String>(32);
		labels    = new ArrayList<Label>(32);
	}
	/**
	*構文解析を開始します。
	*@return 中間言語の配列
	*@throws ScriptException 構文解析エラーがあった場合
	*/
	public Code[] parse() throws ScriptException{
		
		Token token;
		while((token = getNextToken()) != null){
			ungetToken(token);
			parseStatement();
		}
		setLabels();
		return bytecodes.toArray(new Code[0]);
	}
	/**
	*次のトークンを取得します。
	*@return 次のトークン
	*@throws ScriptException 構文エラーがあった場合
	*/
	private Token getNextToken() throws ScriptException{
		if(nextToken != null){
			Token ret = nextToken;
			nextToken = null;
			return ret;
		}else{
			return analyzer.getNextToken();
		}
	}
	/**
	*一度取得したトークンを押し戻して待避させておきます。
	*@param token 待避させるトークン
	*/
	private void ungetToken(Token token){
		nextToken = token;
	}
	/**
	*構文違反があった場合に例外を通知します。
	*@return 生成した例外
	*/
	private ScriptException error(String msg){
		return new ScriptException(
			msg, null, analyzer.getLineNumber(), analyzer.getColumnNumber()
		);
	}
	/**
	*指定された名前を持つ変数を検索します。
	*@param name 変数名
	*@return 変数の番号
	*@throws ScriptException 変数が存在しない場合
	*/
	public int searchVariable(String name) throws ScriptException{
		int index = varnames.indexOf(name);
		if(index >= 0) return index;
		else throw error("Variable \"" + name + "\" does not exist.");
	}
	/**
	*変数を検索し、存在しない場合生成して返します。s
	*@param name 変数名
	*@return 変数の番号
	*/
	public int searchOrCreateVariable(String name){
		int index = varnames.indexOf(name);
		if(index >= 0) return index;
		varnames.add(name);
		return varnames.size() -1;
	}
	
	/**
	*ラベルを検索し、存在しない場合生成して返します。
	*@param name ラベルの名前
	*@return 見つかったラベル
	*/
	private Label searchOrCreateLabel(String name){
		for(Label label : labels){
			String n = label.getName();
			if(n!=null && n.equals(name)) return label;
		}
		Label label = new Label(name);
		labels.add(label);
		return label;
	}
	/**
	*次のトークンを確認します。
	*@param expected 期待されるトークンの型
	*@throws IOException 期待されないトークンが検出された場合
	*/
	private void checkNextToken(Enum expected) throws ScriptException{
		Token next = getNextToken();
		if(next == null || next.getType() != expected){
			throw error("Token \"" + expected + 
				"\" is expected here instead of \"" + next + "\".");
		}
	}
	/**
	*初期状態から構文を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseStatement() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.getType() == Keywords.IF){
			parseIf();
		}else if(token.getType() == Keywords.WHILE){
			parseWhile();
		}else if(token.getType() == Keywords.WRITE){
			parseWrite();
		}else if(token.getType() == Keywords.WRITELN){
			parseWriteln();
		}else if(token.getType() == Keywords.CALL){
			parseCall();
		}else if(token.getType() == Keywords.RETURN){
			parseReturn();
		}else if(token.getType() == Keywords.FUNCT){
			parseRoutine();
		}else if(token.getType() == Keywords.EXIT){
			parseExit();
		}else if(token.getType() == OtherTokens.IDENTIFIER){
			parseAssign(token.toString());
		}else{
			throw error("Bad statement here : \"" + token + "\"");
		}
	}
	/**
	*基本式の構文を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parsePrimary() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.getType() == OtherTokens.INT){
			bytecodes.add(new Code(AriceVirtualMachine.OP_PUSH));
			bytecodes.add(new Code(token.toInteger()));
		}else if(token.getType() == OtherTokens.STRING){
			bytecodes.add(new Code(AriceVirtualMachine.OP_PUSH));
			bytecodes.add(new Code(token.toString()));
		}else if(token.getType() == Operators.OPEN_PARENS){
			parseExpression();
			checkNextToken(Operators.CLOSE_PARENS);
		}else if(token.getType() == OtherTokens.IDENTIFIER){
			bytecodes.add(new Code(AriceVirtualMachine.OP_VAR));
			bytecodes.add(new Code(searchVariable(token.toString())));
		}
	}
	/**
	*負の数のある式を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseUnary() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.getType()==Operators.SUB){
			parsePrimary();
			bytecodes.add(new Code(AriceVirtualMachine.OP_MIN));
		}else{
			ungetToken(token);
			parsePrimary();
		}
	}
	/**
	*乗算演算子または除算演算子またはMOD演算子を主演算子とする式を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseMultiplicative() throws ScriptException{
		
		parseUnary();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if(token.getType() != Operators.MUL
			&& token.getType() != Operators.DIV
			&& token.getType() != Operators.MOD){
				ungetToken(token);
				return;
			}
			parseUnary();
			
			if(token.getType() == Operators.MUL){
				bytecodes.add(new Code(AriceVirtualMachine.OP_MUL));
			}else if(token.getType() == Operators.DIV){
				bytecodes.add(new Code(AriceVirtualMachine.OP_DIV));
			}else{
				bytecodes.add(new Code(AriceVirtualMachine.OP_MOD));
			}
		}
	}
	
	/**
	*加算演算子または減算演算子を主演算子とする式を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseAdditive() throws ScriptException{
		
		parseMultiplicative();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if(token.getType() != Operators.ADD
			&& token.getType() != Operators.SUB){
				ungetToken(token);
				return;
			}
			parseMultiplicative();
			
			if(token.getType() == Operators.ADD){
				bytecodes.add(new Code(AriceVirtualMachine.OP_ADD));
			}else{
				bytecodes.add(new Code(AriceVirtualMachine.OP_SUB));
			}
		}
	}
	
	/**
	*比較演算子からなる式を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseComparative() throws ScriptException{
		
		parseAdditive();
		Token token;
		
		while(true){
			token = getNextToken();
			if( token == null
			||( token.getType() != Operators.EQUAL
			&&  token.getType() != Operators.NOT_EQUAL
			&&  token.getType() != Operators.GREATER
			&&  token.getType() != Operators.GREATER_EQUAL
			&&  token.getType() != Operators.LESS
			&&  token.getType() != Operators.LESS_EQUAL)){
				ungetToken(token);
				return;
			}
			parseAdditive();
			
			if(token.getType() == Operators.EQUAL){
				bytecodes.add(new Code(AriceVirtualMachine.OP_EQU));
			}else if(token.getType() == Operators.NOT_EQUAL){
				bytecodes.add(new Code(AriceVirtualMachine.OP_NEQ));
			}else if(token.getType() == Operators.GREATER){
				bytecodes.add(new Code(AriceVirtualMachine.OP_GRET));
			}else if(token.getType() == Operators.GREATER_EQUAL){
				bytecodes.add(new Code(AriceVirtualMachine.OP_GREQ));
			}else if(token.getType() == Operators.LESS){
				bytecodes.add(new Code(AriceVirtualMachine.OP_LESS));
			}else{
				bytecodes.add(new Code(AriceVirtualMachine.OP_LSEQ));
			}
		}
	}
	/**
	*論理式配列式を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseLogical() throws ScriptException{
		
		parseComparative();
		Token token;
		
		while(true){
			token = getNextToken();
			if( token == null
			||( token.getType() != Operators.AND
			&&  token.getType() != Operators.OR
			&&  token.getType() != Operators.XOR)){
				ungetToken(token);
				return;
			}
			parseComparative();
			
			if(token.getType() == Operators.AND){
				bytecodes.add(new Code(AriceVirtualMachine.OP_AND));
			}else if(token.getType() == Operators.OR){
				bytecodes.add(new Code(AriceVirtualMachine.OP_OR));
			}else{
				bytecodes.add(new Code(AriceVirtualMachine.OP_XOR));
			}
		}
	}
	
	/**
	*条件式を解析します。s
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseExpression() throws ScriptException{
		parseLogical();
	}
	/**
	*if文を解析します。
	*@throws ScriptExeption 構文エラーがあった場合
	*/
	private void parseIf() throws ScriptException{
		
		checkNextToken(Operators.OPEN_PARENS);
		parseExpression();
		checkNextToken(Operators.CLOSE_PARENS);
		
		Label elseLabel = new Label(null);
		labels.add(elseLabel);
		
		bytecodes.add(new Code(AriceVirtualMachine.OP_GOTO_ZERO));
		bytecodes.add(new Code(labels.size()-1));
		
		parseBlock();
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.getType() == Keywords.ELSE){
			Label endIfLabel = new Label(null);
			labels.add(endIfLabel);
			
			bytecodes.add(new Code(AriceVirtualMachine.OP_GOTO));
			bytecodes.add(new Code(labels.size()-1));
			
			elseLabel.setAddress(bytecodes.size());
			
			parseBlock();
			
			endIfLabel.setAddress(bytecodes.size());
		}else{
			ungetToken(token);
			elseLabel.setAddress(bytecodes.size());
		}
	}
	/**
	*while文を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseWhile() throws ScriptException{
		
		Label loopLabel = new Label(null);
		labels.add(loopLabel);
		int index = labels.size()-1;
		loopLabel.setAddress(bytecodes.size());
		
		checkNextToken(Operators.OPEN_PARENS);
		parseExpression();
		checkNextToken(Operators.CLOSE_PARENS);
		
		Label endLoopLabel = new Label(null);
		labels.add(endLoopLabel);
		
		bytecodes.add(new Code(AriceVirtualMachine.OP_GOTO_ZERO));
		bytecodes.add(new Code(labels.size()-1));
		
		parseBlock();
		
		bytecodes.add(new Code(AriceVirtualMachine.OP_GOTO));
		bytecodes.add(new Code(index));
		
		endLoopLabel.setAddress(bytecodes.size());
	}
	/**
	*write文を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseWrite() throws ScriptException{
		
		checkNextToken(Operators.OPEN_PARENS);
		parseExpression();
		checkNextToken(Operators.CLOSE_PARENS);
		
		bytecodes.add(new Code(AriceVirtualMachine.OP_PRINT));
		
		checkNextToken(Operators.SEMICOLON);
	}
	/**
	*writeln文を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseWriteln() throws ScriptException{
		
		checkNextToken(Operators.OPEN_PARENS);
		parseExpression();
		checkNextToken(Operators.CLOSE_PARENS);
		
		bytecodes.add(new Code(AriceVirtualMachine.OP_PRINT));
		bytecodes.add(new Code(AriceVirtualMachine.OP_PRINTLN));
		
		checkNextToken(Operators.SEMICOLON);
	}
	/**
	*代入文を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseAssign(String name) throws ScriptException{
		
		int index = searchOrCreateVariable(name);
		
		checkNextToken(Operators.ASSIGN);
		parseExpression();
		
		bytecodes.add(new Code(AriceVirtualMachine.OP_ASSN));
		bytecodes.add(new Code((index<0)?varnames.size():index));
		if(index<0) varnames.add(name);
		
		checkNextToken(Operators.SEMICOLON);
	}
	/**
	*exit文を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseExit() throws ScriptException{
		
		checkNextToken(Operators.SEMICOLON);
		bytecodes.add(new Code(AriceVirtualMachine.OP_EXIT));
	}
	/**
	*routine文を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseRoutine() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null || token.getType() != OtherTokens.IDENTIFIER){
			throw error("Routine name is required");
		}
		
		Label endLabel = new Label(null);
		labels.add(endLabel);
		
		bytecodes.add(new Code(AriceVirtualMachine.OP_GOTO));
		bytecodes.add(new Code(labels.size()-1));
		
		Label startLabel = searchOrCreateLabel(token.toString());
		labels.add(startLabel);
		startLabel.setAddress(bytecodes.size());
		
		parseBlock();
		
		bytecodes.add(new Code(AriceVirtualMachine.OP_RETURN));
		
		endLabel.setAddress(bytecodes.size());
	}
	/**
	*call文を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseCall() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null || token.getType() != OtherTokens.IDENTIFIER){
			throw error("Routine name is required.");
		}
		
		Label label = searchOrCreateLabel(token.toString());
		labels.add(label);
		
		bytecodes.add(new Code(AriceVirtualMachine.OP_GOTO_SUB));
		bytecodes.add(new Code(labels.indexOf(label)));
		
		checkNextToken(Operators.SEMICOLON);
	}
	/**
	*return文を解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseReturn() throws ScriptException{
		
		bytecodes.add(new Code(AriceVirtualMachine.OP_RETURN));
		checkNextToken(Operators.SEMICOLON);
	}
	/**
	*ブロックを解析します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	private void parseBlock() throws ScriptException{
		
		checkNextToken(Operators.OPEN_BRACE);
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null){
				throw error("Close brace not exist");
			}else if(token.getType() == Operators.CLOSE_BRACE){
				return;
			}
			ungetToken(token);
			parseStatement();
		}
	}
	/**
	*生成されたコードにラベルの実アドレスを書き込みます。
	*@throws ScriptException まず発生しない例外
	*/
	private void setLabels() throws ScriptException{
		
		int length = bytecodes.size();
		for(int i=0; i<length; i++){
			int code = bytecodes.get(i).toInteger();
			//2ワード命令
			if(code == AriceVirtualMachine.OP_PUSH
			|| code == AriceVirtualMachine.OP_VAR
			|| code == AriceVirtualMachine.OP_ASSN)
			{
				i++;
			}//ラベルを使用する命令
			else if(code == AriceVirtualMachine.OP_GOTO
				||  code == AriceVirtualMachine.OP_GOTO_ZERO
				||  code == AriceVirtualMachine.OP_GOTO_SUB)
			{
				bytecodes.set(i+1, new Code(labels.get
					(bytecodes.get(i+1).toInteger()).getAddress()));
				i++;
			}
		}
	}
}