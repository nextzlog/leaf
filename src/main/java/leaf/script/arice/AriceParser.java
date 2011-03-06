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
final class AriceParser extends AriceCodesAndTokens{
	
	private final ArrayList<Code>    medcodes;
	private final AriceClassTable    classes;
	private final AriceNameTable     names;
	private final AriceBlockTable    blocks;
	private final AriceFunctionTable functions;
	
	private final AriceLexAnalyzer analyzer;
	private Token nextToken = null;
	
	/**
	*パーサーを初期化して生成します。
	*/
	public AriceParser(){
		analyzer  = new AriceLexAnalyzer  ();
		medcodes  = new ArrayList<Code>   (1024);
		classes   = new AriceClassTable   (analyzer, 32);
		functions = new AriceFunctionTable(analyzer, 32);
		blocks    = new AriceBlockTable   (analyzer, 64);
		names     = new AriceNameTable    (analyzer, 64, 8);
	}
	/**
	*パーサを初期化します。
	*/
	private void init(){
		functions.clear();
		medcodes.clear();
		classes.clear();
		blocks.clear();
		names.clear();
	}
	/**
	*スクリプトを指定してコンパイルします。
	*@param script スクリプト
	*@return 中間言語コード列
	*@throws ScriptException 構文エラーがあった場合
	*/
	public Code[] compile(String script) throws ScriptException{
		analyzer.load(script);
		createHeader();
		Token token;
		while((token = getNextToken()) != null){
			ungetToken(token);
			parseStatement();
		}
		setAddresses();
		functions.checkAllDefined();
		Code[] codes = medcodes.toArray(new Code[0]);
		init();
		return codes;
	}
	/**
	*中間言語コードにヘッダーを追加します。
	*/
	private void createHeader() throws ScriptException{
		
		final Token name = new Token("main");
		final int count   = 0;
		
		functions.add(name, count);
		
		add(new Code(OP_CALL));
		add(new Code(blocks.searchOrCreateBlock(name.toString(), count)));
		
		add(new Code(OP_CALL_DEL));
		add(new Code(count));
		
		add(new Code(OP_EXIT));
	}
	/**
	*中間言語コードに追加します。
	*@param code 追加するコード
	*/
	private void add(Code code){
		medcodes.add(code);
	}
	/**
	*次のトークンを取得します。
	*@return 次のトークン
	*/
	private Token getNextToken() throws ScriptException{
		return analyzer.getNextToken();
	}
	/**
	*一度取得したトークンを押し戻して待避させておきます。
	*@param token 待避させるトークン
	*/
	private void ungetToken(Token token){
		analyzer.ungetToken(token);
	}
	/**
	*構文違反があった場合に例外を通知します。
	*@return 生成した例外
	*/
	private ScriptException error(String msg){
		int line = analyzer.getLineNumber();
		int colm = analyzer.getColumnNumber();
		return new ScriptException(
			msg+" at line : "+line+"\n => "+analyzer.getLine(),null,line,colm
		);
	}
	/**
	*次のトークンが適切な型であるか確認します。
	*@param expected 期待されるトークンの型
	*@return 次のトークン
	*@throws IOException 期待されないトークンが検出された場合
	*/
	private Token checkNextToken(Enum... expected) throws ScriptException{
		return analyzer.checkNextToken(expected);
	}
	/**
	*ステートメントを解析します。
	*/
	private void parseStatement() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(functions.isFunctionDefining()){
			if(token.getType() instanceof Keywords){
				switch((Keywords)token.getType()){
					case BREAK  :parseBreak();      return;
					case EXIT   :parseExit();       return;
					case FOR    :parseFor();        return;
					case IF     :parseIf();         return;
					case RETURN :parseReturn();     return;
					case SET    :parseSet();        return;
					case SWITCH :parseSwitch();     return;
					case VAR    :parseDeclarative();return;
					case WRITE  :parseWrite();      return;
					case WRITELN:parseWriteln();    return;
				}
			}
			parseIdentifier(token);
			add(new Code(OP_DEL));
			add(new Code(1));
			checkNextToken(Operators.SEMICOLON);
		}else if(token.getType() == Keywords.FUNCT ){
			parseFunction();
		}else if(token.getType() == Keywords.IMPORT){
			parseImport();
		}else{
			throw error("Illegal statement here : \"" + token + "\"");
		}
	}
	/**
	*基本式の構文を解析します。
	*/
	private void parsePrimary() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.getType() instanceof Tokens){
			switch((Tokens)token.getType()){
				case INTEGER:
					add(new Code(OP_LIT_PUSH));
					add(new Code(token.toInteger()));
					return;
				case DOUBLE:
					add(new Code(OP_LIT_PUSH));
					add(new Code(token.toDouble()));
					return;
				case CHARACTER:
					add(new Code(OP_LIT_PUSH));
					add(new Code(token.toCharacter()));
					return;
				case STRING:
					add(new Code(OP_LIT_PUSH));
					add(new Code(token.toString()));
					return;
				case IDENTIFIER:
					parseIdentifier(token);
					return;
			}
		}else if(token.getType() instanceof Keywords){
			switch((Keywords)token.getType()){
				case TRUE :
					add(new Code(OP_LIT_PUSH));
					add(new Code(true));
					return;
				case FALSE:
					add(new Code(OP_LIT_PUSH));
					add(new Code(false));
					return;
				case NULL :
					add(new Code(OP_LIT_PUSH));
					add(new Code(null));
					return;
				case GET  :
					parseGet();
					return;
				case NEW  :
					parseConstruct();
					return;
				default:
					parseIdentifier(token);
					return;
			}
		}else{
			switch((Operators)token.getType()){
				case OPEN_PARENS:
					parseExpression();
					checkNextToken(Operators.CLOSE_PARENS);
					return;
				default:
					ungetToken(token);
			}
		}
	}
	/**
	*識別子を解析します。
	*@param token 識別子
	*/
	private void parseIdentifier(Token token) throws ScriptException{
		
		Token next = getNextToken();
		if(next == null) return;
		
		ungetToken(next);
		
		if(next.getType() == Operators.OPEN_PARENS){
			parseCall(token);
		}else if(next.getType() == Operators.DECLARE){
			parseShortDeclarative(token);
		}else if(  next.getType() == Operators.ASSIGN
				|| next.getType() == Operators.ASSIGN_ADD
				|| next.getType() == Operators.ASSIGN_SUB
				|| next.getType() == Operators.ASSIGN_MUL
				|| next.getType() == Operators.ASSIGN_DIV
				|| next.getType() == Operators.ASSIGN_MOD
				|| next.getType() == Operators.ASSIGN_POW
				|| next.getType() == Operators.ASSIGN_AND
				|| next.getType() == Operators.ASSIGN_OR
				|| next.getType() == Operators.ASSIGN_XOR
				|| next.getType() == Operators.ASSIGN_LEFT
				|| next.getType() == Operators.ASSIGN_RIGHT)
		{
			parseAssign(token);
		}else if(classes.exists(token.toString())){
			if(next.getType() == Operators.DOT){//static
				add(new Code(OP_LIT_PUSH));
				add(new Code(null, classes.get(token)));
			}else{ //型変換
				parseDot();
				add(new Code(OP_CLASS_CAST));
				add(new Code(classes.get(token)));
			}
		}else{ //変数/引数
			int index = names.searchParameter(token.toString());
			boolean isArg = index >= 0;
			if(isArg){
				add(new Code(OP_ARG_PUSH));
				add(new Code(index));
			}else{
				index = names.searchVariable(token.toString());
				add(new Code(OP_VAR_PUSH));
				add(new Code(index));
			}if(next.getType() == Operators.INCREMENT){
				getNextToken();
				add(new Code(isArg? OP_ARG_INCR : OP_VAR_INCR));
				add(new Code(index));
			}else if(next.getType() == Operators.DECREMENT){
				getNextToken();
				add(new Code(isArg? OP_ARG_DECR : OP_VAR_DECR));
				add(new Code(index));
			}
		}
		parseDot();
	}
	/**
	*配列演算子式を解析します。
	*/
	private void parseArray() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.isType(Operators.OPEN_SQUARE)){
			int count = 0;
			while(true){
				if(parseExpression())count++;
				token = checkNextToken(
					Operators.CLOSE_SQUARE,
					Operators.COMMA
				);
				if(token.isType(Operators.CLOSE_SQUARE)) break;
			}
			add(new Code(OP_TO_LIST));
			add(new Code(count));
		}else{
			ungetToken(token);
			parsePrimary();
		}
	}
	/**
	*ドット演算子式を解析します。
	*/
	private void parseDot() throws ScriptException{
		
		parseArray();
		Token token, next;
		
		while(true){
			token = getNextToken();
			
			if(token == null || token.getType() != Operators.DOT){
				ungetToken(token);
				return;
			}
			token = checkNextToken(Tokens.IDENTIFIER);
			
			next  = getNextToken();
			if(next == null) return;
			
			if(next.getType() == Operators.OPEN_PARENS){
				parseMethodCall(token);
			}else if(next.getType() == Operators.ASSIGN){
				if(!parseExpression()){
					throw error(token + " is not assigned.");
				}
				add(new Code(OP_FIELD_ASSN));
				add(new Code(token.toString()));
			}else{
				ungetToken(next);
				add(new Code(OP_FIELD_PUSH));
				add(new Code(token.toString()));
			}
		}
	}
	/**
	*単項演算子式を解析します。
	*/
	private void parseUnary() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.getType() == Operators.ADD){
			parseDot();
		}else if(token.getType() == Operators.SUB){
			parseDot();
			add(new Code(OP_REV));
		}else if(token.isType(Operators.NOT, Operators.BIT_NOT)){
			parseDot();
			add(new Code(OP_NOT));
		}else{
			ungetToken(token);
			parseDot();
		}
	}
	/**
	*自乗演算子を主演算子とする式を解析します。
	*/
	private void parsePower() throws ScriptException{
		
		parseUnary();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if(token.getType() != Operators.POW){
				ungetToken(token);
				return;
			}
			parsePower();
			add(new Code(OP_POW));
		}
	}
	/**
	*乗算演算子または除算演算子またはMOD演算子を主演算子とする式を解析します。
	*/
	private void parseMultiplicative() throws ScriptException{
		
		parsePower();
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
			parsePower();
			add(new Code(token.toCode()));
		}
	}
	
	/**
	*加算演算子または減算演算子を主演算子とする式を解析します。
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
			add(new Code(token.toCode()));
		}
	}
	
	/**
	*ビットシフト演算子を主演算子とする式を解析します。
	*/
	private void parseBitShift() throws ScriptException{
		
		parseAdditive();
		Token token = getNextToken();
		if(token == null) return;
		
		if( token.getType() != Operators.BIT_LEFT
		&&  token.getType() != Operators.BIT_RIGHT){
			ungetToken(token);
			return;
		}
		parseAdditive();
		add(new Code(token.toCode()));
	}
	/**
	*関係比較演算子を主演算子とする式を解析します。
	*/
	private void parseRelational() throws ScriptException{
		
		parseBitShift();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if( token.getType() != Operators.GREATER
			&&  token.getType() != Operators.GREATER_EQUAL
			&&  token.getType() != Operators.LESS
			&&  token.getType() != Operators.LESS_EQUAL
			&&  token.getType() != Keywords.INSTOF){
				ungetToken(token);
				return;
			}
			if(token.getType() != Keywords.INSTOF){
				parseBitShift();
				add(new Code(token.toCode()));
			}else{
				add(new Code(OP_METHOD));
				add(new Code("getClass"));
				add(new Code(0));
				add(new Code(OP_LIT_PUSH));
				add(new Code(classes.get(getNextToken())));
				add(new Code(OP_EQU ));
			}
		}
	}
	/**
	*等値比較演算子を主演算子とする式を解析します。
	*/
	private void parseComparative() throws ScriptException{
		
		parseRelational();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if( token.getType() != Operators.EQUAL
			&&  token.getType() != Operators.NOT_EQUAL){
				ungetToken(token);
				return;
			}
			parseRelational();
			add(new Code(token.toCode()));
		}
	}
	/**
	*論理積演算子を主演算子とする式を解析します。
	*/
	private void parseAnd() throws ScriptException{
		
		parseComparative();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if( token.getType() != Operators.AND){
				ungetToken(token);
				return;
			}
			parseComparative();
			add(new Code(OP_AND));
		}
	}
	/**
	*排他的論理和演算子を主演算子とする式を解析します。
	*/
	private void parseExclusiveOr() throws ScriptException{
		
		parseAnd();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if( token.getType() != Operators.XOR){
				ungetToken(token);
				return;
			}
			parseAnd();
			add(new Code(OP_XOR));
		}
	}
	/**
	*論理和演算子を主演算子とする式を解析します。
	*/
	private void parseOr() throws ScriptException{
		
		parseExclusiveOr();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if( token.getType() != Operators.OR){
				ungetToken(token);
				return;
			}
			parseExclusiveOr();
			add(new Code(OP_OR));
		}
	}
	/**
	*短絡論理積演算子を主演算子とする式を解析します。
	*/
	private void parseShortCircuitAnd() throws ScriptException{
		
		parseOr();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if( token.getType() != Operators.SHORT_AND){
				ungetToken(token);
				return;
			}
			int label = blocks.addBlock();
			
			add(new Code(OP_SHORT_AND));
			add(new Code(label));
			
			parseOr();
			
			add(new Code(OP_AND));
			blocks.setAddress(label, medcodes.size());
		}
	}
	/**
	*短絡論理和演算子を主演算子とする式を解析します。
	*/
	private void parseShortCircuitOr() throws ScriptException{
		
		parseShortCircuitAnd();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if( token.getType() != Operators.SHORT_OR){
				ungetToken(token);
				return;
			}
			int label = blocks.addBlock();
			
			add(new Code(OP_SHORT_OR));
			add(new Code(label));
			
			parseShortCircuitAnd();
			
			add(new Code(OP_OR));
			blocks.setAddress(label, medcodes.size());
		}
	}
	/**
	*三項演算子を主演算子とする式を解析します。
	*/
	private void parseTernary() throws ScriptException{
		
		parseShortCircuitOr();
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return;
			
			if( token.getType() != Operators.TERNARY){
				ungetToken(token);
				return;
			}
			int elseBlock = blocks.addBlock();
			
			add(new Code(OP_GOTO_FALSE));
			add(new Code(elseBlock));
			
			parseTernary();
			
			int endBlock  = blocks.addBlock();
			add(new Code(OP_GOTO));
			add(new Code(endBlock));
			
			checkNextToken(Operators.COLON);
			
			blocks.setAddress(elseBlock, medcodes.size());
			parseTernary();
			
			blocks.setAddress(endBlock, medcodes.size());
		}
	}
	/**
	*条件式を解析します。
	*@return 条件式が空でない場合
	*/
	private boolean parseExpression() throws ScriptException{
		int start = medcodes.size();
		parseTernary();
		return (start < medcodes.size());
	}
	/**
	*if文を解析します。
	*/
	private void parseIf() throws ScriptException{
		
		names.enterChildScope();
		
		checkNextToken(Operators.OPEN_PARENS);
		if(!parseExpression()){
			add(new Code(OP_LIT_PUSH));
			add(new Code(true));
		}
		checkNextToken(Operators.CLOSE_PARENS);
		
		int elseBlock = blocks.addBlock();
		
		add(new Code(OP_GOTO_FALSE));
		add(new Code(elseBlock));
		
		parseBlock();
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.getType() == Keywords.ELSE){
			int endIfBlock = blocks.addBlock();
			
			add(new Code(OP_GOTO));
			add(new Code(endIfBlock));
			
			blocks.setAddress(elseBlock, medcodes.size());
			
			names.enterAnotherChildScope();
			parseBlock();
			
			blocks.setAddress(endIfBlock, medcodes.size());
		}else{
			ungetToken(token);
			blocks.setAddress(elseBlock, medcodes.size());
		}
		names.exitChildScope();
	}
	/**
	*for文を解析します。
	*/
	private void parseFor() throws ScriptException{
		
		names.enterChildScope();
		
		Token name = checkNextToken(
			Operators.OPEN_PARENS,
			Tokens.IDENTIFIER
		);
		
		if(name.getType() == Tokens.IDENTIFIER){
			checkNextToken(Operators.OPEN_PARENS);
		}else name = null;
		
		int start  = medcodes.size();
		parseExpression();
		
		Token token = checkNextToken(
			Operators.CLOSE_PARENS,
			Operators.SEMICOLON
		);
		
		if(token.getType() == Operators.CLOSE_PARENS)
			parseShortFor(start, name);
		else
			parseLongFor (name);
		
		names.exitChildScope();
	}
	/**
	*初期化式・条件式・再初期化式を持つfor文を解析します。
	*@param name for文の名前
	*/
	private void parseLongFor(Token name) throws ScriptException{
		
		int ifBlock  = blocks.addBlock();
		blocks.setAddress(ifBlock, medcodes.size());
		
		boolean omis = false; //条件式省略
		
		//条件式
		Token token = getNextToken();
		if(token == null){
			throw error("For Block is not closed.");
		}
		
		if(token.getType() != Operators.SEMICOLON){
			ungetToken(token);
			parseExpression();
			checkNextToken(Operators.SEMICOLON);
		}else omis = true;
		
		//入れ子構造
		int endBlock = blocks.addBlock(name);
		int intBlock = blocks.addBlock();
		int resBlock = blocks.addBlock();
		
		if(omis){
			add(new Code(OP_GOTO));
			add(new Code(intBlock));
		}else{
			add(new Code(OP_GOTO_FALSE));
			add(new Code(endBlock));
			add(new Code(OP_GOTO));
			add(new Code(intBlock));
		}
		
		//再初期化式
		blocks.setAddress(resBlock, medcodes.size());
		if(parseExpression()){
			add(new Code(OP_DEL));
			add(new Code(1));
		}
		checkNextToken(Operators.CLOSE_PARENS);
		
		add(new Code(OP_GOTO));
		add(new Code(ifBlock));
		
		//本文
		blocks.setAddress(intBlock, medcodes.size());
		parseBlock();
		
		add(new Code(OP_GOTO));
		add(new Code(resBlock));
		
		blocks.setAddress(endBlock, medcodes.size());
	}
	/**
	*条件式のみを持つfor文を解析します。
	*@oaram start for文の開始位置
	*@param name for文の名前
	*/
	private void parseShortFor(int start, Token name) throws ScriptException{
		
		int ifBlock  = blocks.addBlock();
		blocks.setAddress(ifBlock, start);
		
		int endBlock = blocks.addBlock(name);
		
		//条件式省略確認
		if(start < medcodes.size()){
			add(new Code(OP_GOTO_FALSE));
			add(new Code(endBlock));
		}
		
		parseBlock();
		
		add(new Code(OP_GOTO));
		add(new Code(ifBlock));
		
		blocks.setAddress(endBlock, medcodes.size());
	}
	/**
	*switch文を解析します。
	*/
	private void parseSwitch() throws ScriptException{
		
		names.enterChildScope();
		
		Token token = checkNextToken(
			Operators.OPEN_PARENS,
			Tokens.IDENTIFIER
		);
		
		if(token.getType() == Tokens.IDENTIFIER){
			checkNextToken(Operators.OPEN_PARENS);
		}else token = null;
		
		boolean omis = !parseExpression(); //式省略
		
		checkNextToken(Operators.CLOSE_PARENS);
		checkNextToken(Operators.OPEN_BRACE);
		
		int endBlock = blocks.addBlock(token);
		while(true){
			token = checkNextToken(
				Operators.CLOSE_BRACE,
				Keywords.DEFAULT,
				Keywords.CASE
			);
			if(token.isType(Operators.CLOSE_BRACE)) break;
			if(token.isType(Keywords.DEFAULT))parseDefault(endBlock);
			else parseCase(endBlock, omis);
		}
		blocks.setAddress(endBlock, medcodes.size());
		names.exitChildScope();
		
		if(!omis){
			add(new Code(OP_DEL));
			add(new Code(1));
		}
	}
	/**
	*case文を解析します。
	*@param endSwitch switch文のブロック
	*@param omis 条件式が省略されている場合
	*/
	private void parseCase(int endSwitch, boolean omis) throws ScriptException{
		
		names.enterChildScope();
		int end = blocks.addBlock();
		
		if(!omis)add(new Code(OP_DUP));
		int start = medcodes.size();
		if(!parseExpression()){
			throw error("Conditional expression is not written.");
		}
		if(!omis)add(new Code(OP_EQU));
		
		checkNextToken(Operators.COLON);
		
		add(new Code(OP_GOTO_FALSE));
		add(new Code(end));
		
		Token token;
		while(true){
			ungetToken(token = getNextToken());
			if(token == null){
				throw error("Switch block is not closed.");
			}else if(token.isType(
				Operators.CLOSE_BRACE,
				Keywords.DEFAULT,
				Keywords.CASE
			))break;
			parseStatement();
		}
		add(new Code(OP_GOTO));
		add(new Code(endSwitch));
		
		blocks.setAddress(end, medcodes.size());
		names.exitChildScope();
	}
	/**
	*default文を解析します。
	*@param endSwitch switch文のブロック
	*/
	private void parseDefault(int endSwitch) throws ScriptException{
		
		names.enterChildScope();
		int end = blocks.addBlock();
		
		checkNextToken(Operators.COLON);
		
		Token token;
		while(true){
			ungetToken(token = getNextToken());
			if(token == null){
				throw error("Switch block is not closed.");
			}else if(token.isType(Keywords.CASE)){
				throw error("Default block should be the last.");
			}else if(token.isType(Keywords.DEFAULT)){
				throw error("Default block is duplicate.");
			}else if(token.isType(Operators.CLOSE_BRACE))break;
			parseStatement();
		}
		blocks.setAddress(end, medcodes.size());
		names.exitChildScope();
	}
	/**
	*break文を解析します。
	*/
	private void parseBreak() throws ScriptException{
		
		Token token = getNextToken();
		
		if(token == null || token.getType() != Tokens.IDENTIFIER){
			throw error("Block name is required here.");
		}
		checkNextToken(Operators.SEMICOLON);
		
		add(new Code(OP_GOTO));
		add(new Code(blocks.searchBlock(token.toString())));
	}
	/**
	*write文を解析します。
	*/
	private void parseWrite() throws ScriptException{
		
		checkNextToken(Operators.OPEN_PARENS);
		if(!parseExpression()){
			add(new Code(OP_LIT_PUSH));
			add(new Code(""));
		}
		checkNextToken(Operators.CLOSE_PARENS);
		
		add(new Code(OP_PRINT));
		
		checkNextToken(Operators.SEMICOLON);
	}
	/**
	*writeln文を解析します。
	*/
	private void parseWriteln() throws ScriptException{
		
		checkNextToken(Operators.OPEN_PARENS);
		if(!parseExpression()){
			add(new Code(OP_LIT_PUSH));
			add(new Code("\n"));
		}
		checkNextToken(Operators.CLOSE_PARENS);
		
		add(new Code(OP_PRINT));
		add(new Code(OP_PRINTLN));
		
		checkNextToken(Operators.SEMICOLON);
	}
	/**
	*略式変数宣言文を解析します。
	*@param name 変数名
	*/
	private void parseShortDeclarative(Token name) throws ScriptException{
		
		checkNextToken(Operators.DECLARE);
		
		if(!parseExpression()){
			throw error("Variable " + name + " is not initialized.");
		}
		int index = names.createVariable(name.toString());
		
		add(new Code(OP_VAR_ASSN));
		add(new Code(index));
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.getType() != Operators.SEMICOLON){
			add(new Code(OP_VAR_PUSH));
			add(new Code(index));
		}
		ungetToken(token);
	}
	/**
	*変数宣言文を解析します。
	*/
	private void parseDeclarative() throws ScriptException{
		
		Token token, next;
		
		while(true){
			token = checkNextToken(
				Tokens.IDENTIFIER,
				Operators.COMMA,
				Operators.SEMICOLON
			);
			
			if(token.isIdentifier()){
				next  = checkNextToken(
					Operators.ASSIGN,
					Operators.COMMA,
					Operators.SEMICOLON
				);
				
				int index = names.createVariable(token.toString());
				
				if(next.getType() == Operators.ASSIGN){
					parseExpression();
				}else{
					add(new Code(OP_LIT_PUSH));
					add(new Code(null));
				}
				add(new Code(OP_VAR_ASSN));
				add(new Code(index));
				
				if(next.getType() == Operators.SEMICOLON) return;
			}else if(token.getType() == Operators.SEMICOLON) return;
		}
	}
	/**
	*代入文を解析します。
	*@param name 変数名
	*/
	private void parseAssign(Token name) throws ScriptException{
		
		int index = names.searchParameter(name.toString());
		boolean isParam   = (index >= 0);
		if(!isParam)index = names.searchVariable(name.toString());
		
		Token token = getNextToken();
		
		if(token.getType() != Operators.ASSIGN){
			
			if(isParam)
				add(new Code(OP_ARG_PUSH));
			else
				add(new Code(OP_VAR_PUSH));
			add(new Code(index));
			
			parseExpression();
			add(new Code(token.toCode()));
		}else parseExpression();
		
		if(isParam){
			add(new Code(OP_ARG_ASSN));
			add(new Code(index));
			add(new Code(OP_ARG_PUSH));
			add(new Code(index));
		}else{
			add(new Code(OP_VAR_ASSN));
			add(new Code(index));
			add(new Code(OP_VAR_PUSH));
			add(new Code(index));
		}
	}
	/**
	*exit文を解析します。
	*/
	private void parseExit() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.getType() == Operators.SEMICOLON){
			add(new Code(OP_LIT_PUSH));
			add(new Code(null));
		}else{
			ungetToken(token);
			parseExpression();
			checkNextToken(Operators.SEMICOLON);
		}
		
		add(new Code(OP_EXIT));
	}
	/**
	*function文を解析します。
	*/
	private void parseFunction() throws ScriptException{
		
		Token name = getNextToken();
		if(name == null) return;
		
		if(name == null || !name.isIdentifier()){
			throw error("Function name is required here.");
		}
		names.clear();
		
		int startAddress = medcodes.size();
		
		add(new Code(OP_FRAME));
		add(null); //この部分にローカル変数の個数を代入
		int index = medcodes.size() -1;
		
		int count = parseParameters(name);
		parseBlock();
		
		add(new Code(OP_LIT_PUSH));
		add(new Code(null));
		add(new Code(OP_RETURN));
		
		int block = blocks.searchOrCreateBlock(name.toString(), count);
		blocks.setAddress(block, startAddress);
		
		medcodes.set(index, new Code(names.getVariableCount()));
		
		functions.endDefine();
		names.clear();
	}
	/**
	*引数の宣言文を解析します。
	*@param name 関数名
	*@return 引数の個数
	*/
	private int parseParameters(Token name) throws ScriptException{
		
		checkNextToken(Operators.OPEN_PARENS);
		Token token;
		
		while(true){
			token = checkNextToken(
				Tokens.IDENTIFIER,
				Operators.CLOSE_PARENS
			);
			if(token.isType(Operators.CLOSE_PARENS))break;
			names.createParameter(token.toString());
			
			token = checkNextToken(
				Operators.COMMA,
				Operators.CLOSE_PARENS
			);
			if(token.isType(Operators.CLOSE_PARENS))break;
		}
		int count = names.getParameterCount();
		functions.define(name, count);
		return count;
	}
	/**
	*関数のコール文を解析します。
	*@param name 呼び出す関数名
	*/
	private void parseCall(Token name) throws ScriptException{
		
		if(name == null || !name.isIdentifier()){
			throw error("Function name is required here.");
		}
		checkNextToken(Operators.OPEN_PARENS);
		
		int count = parseArguments();
		
		functions.add(name, count);
		int label = blocks.searchOrCreateBlock(name.toString(), count);
		
		add(new Code(OP_CALL));
		add(new Code(label));
		
		add(new Code(OP_CALL_DEL));
		add(new Code(count));
	}
	/**
	*実引数文を解析します。
	*@return 引数の数
	*/
	private int parseArguments() throws ScriptException{
		
		Token token;
		int count = 0;
		
		while(true){
			token = getNextToken();
			if(token == null){
				throw error("Argument parens is not closed.");
			}
			
			if(!token.isType(Operators.CLOSE_PARENS)){
				ungetToken(token);
				parseExpression();
				token = checkNextToken(
					Operators.COMMA,
					Operators.CLOSE_PARENS
				);
				count++;
			}
			if(token.isType(Operators.CLOSE_PARENS)) return count;
		}
	}
	/**
	*return文を解析します。
	*/
	private void parseReturn() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null) return;
		
		if(token.isType(Operators.SEMICOLON)){
			add(new Code(OP_LIT_PUSH));
			add(new Code(null));
		}else{
			ungetToken(token);
			parseExpression();
			checkNextToken(Operators.SEMICOLON);
		}
		
		add(new Code(OP_RETURN));
	}
	/**
	*get文を解析します。
	*/
	private void parseGet() throws ScriptException{
		
		checkNextToken(Operators.OPEN_PARENS);
		parseExpression();
		checkNextToken(Operators.CLOSE_PARENS);
		
		add(new Code(OP_BIND_GET));
	}
	/**
	*set文を解析します。
	*/
	private void parseSet() throws ScriptException{
		
		checkNextToken(Operators.OPEN_PARENS);
		parseExpression();
		checkNextToken(Operators.COMMA);
		parseExpression();
		checkNextToken(Operators.CLOSE_PARENS);
		
		add(new Code(OP_BIND_SET));
		
		checkNextToken(Operators.SEMICOLON);
	}
	/**
	*new文を解析します。
	*/
	private void parseConstruct() throws ScriptException{
		
		//クラス名
		Token token = checkNextToken(Tokens.IDENTIFIER);
		Class cls   = classes.get(token);
		
		checkNextToken(Operators.OPEN_PARENS);
		int count = parseArguments();
		
		add(new Code(OP_INSTANCE));
		add(new Code(cls));
		add(new Code(count));
	}
	/**
	*メソッドコール文を解析します。
	*@param name メソッド名
	*/
	private void parseMethodCall(Token name) throws ScriptException{
		
		if(name == null || !name.isIdentifier()){
			throw error("Method name is required here.");
		}
		int count = parseArguments();
		
		add(new Code(OP_METHOD));
		add(new Code(name.toString()));
		add(new Code(count));
	}
	/**
	*import文を解析します。
	*/
	private void parseImport() throws ScriptException{
		
		StringBuilder name = new StringBuilder(32);
		
		while(true){
			Token token = checkNextToken(
				Operators.SEMICOLON,
				Tokens.IDENTIFIER,
				Operators.DOT,
				Operators.MUL
			);
			if(token.getType() != Operators.SEMICOLON){
				name.append(token);
			}else break;
		}
		classes.add(name.toString());
	}
	/**
	*ブロックを解析します。
	*/
	private void parseBlock() throws ScriptException{
		
		Token token = getNextToken();
		if(token == null){
			throw error("Open brace is not written.");
		}
		
		boolean omis = !token.isType(Operators.OPEN_BRACE);
		if(omis) ungetToken(token);
		
		while(true){
			token = getNextToken();
			if(token == null){
				throw error("Block is not closed.");
			}else if(token.isType(Operators.CLOSE_BRACE)){
				return;
			}
			ungetToken(token);
			parseStatement();
			if(omis)return;
		}
	}
	/**
	*生成されたコードにブロックの実アドレスを書き込みます。
	*@throws ScriptException まず発生しない例外
	*/
	private void setAddresses() throws ScriptException{
		
		int length = medcodes.size();
		for(int i=0; i<length; i++){
			int code = medcodes.get(i).toInteger();
			//2ワード命令
			switch(code){
			case OP_DEL       :
			case OP_LIT_PUSH  :
			case OP_VAR_PUSH  :
			case OP_VAR_ASSN  :
			case OP_ARG_PUSH  :
			case OP_ARG_ASSN  :
			case OP_TO_LIST   :
			case OP_FRAME     :
			case OP_CALL_DEL  :
			case OP_FIELD_ASSN:
			case OP_FIELD_PUSH:
			case OP_CLASS_CAST:
				i++;
				break;
			//3ワード命令
			case OP_INSTANCE  :
			case OP_METHOD    :
				i += 2;
				break;
			//ブロック使用命令
			case OP_GOTO      :
			case OP_GOTO_FALSE:
			case OP_CALL      :
			case OP_SHORT_AND :
			case OP_SHORT_OR  :
				medcodes.set(i+1, new Code(
					blocks.getAddress(medcodes.get(i+1).toInteger())
				));
				i++;
			}
		}
	}
}