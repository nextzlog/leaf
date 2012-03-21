/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium;

import leaf.script.common.util.Code;

import java.io.Reader;
import java.util.List;
import java.util.ArrayList;
import javax.script.ScriptException;

import leaf.manager.LeafLocalizeManager;

/**
 *AriCE処理系の構文解析器の実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月28日
 */
final class Parser extends CodesAndTokens{
	
	private final List<Code>    medcodes;
	private final ClassTable    classes;
	private final NameTable     names;
	private final LabelTable    labels;
	private final FunctionTable functions;
	
	private final LexicalAnalyzer analyzer;
	private Token nextToken;
	
	private final LeafLocalizeManager localize;
	
	/**
	*構文解析器を初期化して生成します。
	*/
	public Parser(){
		analyzer  = new LexicalAnalyzer  ();
		medcodes  = new ArrayList<Code>  (1024);
		classes   = new ClassTable   (analyzer);
		functions = new FunctionTable(analyzer);
		labels    = new LabelTable   (analyzer);
		names     = new NameTable    (analyzer);
		
		localize = LeafLocalizeManager.getInstance(getClass());
	}
	/**
	*構文解析器を初期化します。
	*/
	private void initialize(){
		functions.clear();
		medcodes.clear();
		classes.clear();
		labels.clear();
		names.clear();
	}
	/**
	*スクリプトを読み込むリーダを指定してコンパイルします。
	*@param reader リーダー
	*@return 中間言語命令列
	*@throws ScriptException 構文違反があった場合
	*/
	public Code[] compile(Reader reader) throws ScriptException{
		try{
			analyzer.load(reader);
			createHeader();
			
			while(hasNextToken()){
				parseStatement();
			}
			setLabelAddresses();
			functions.checkAllDefined();
			
			return medcodes.toArray(new Code[0]);
		}finally{
			initialize();
		}
	}
	/**
	*中間言語命令列の先頭に初期化部を追加します。
	*/
	private void createHeader() throws ScriptException{
		
		final Token name = new Token("main");
		final int params = 1;
		
		functions.add(name, params);
		int label = labels.searchOrCreateLabel(name, params);
		
		add(new Code(OP_FUNC_CALL));
		add(new Code(params));
		add(new Code(label));
		
		add(new Code(OP_EXIT));
	}
	/**
	*命令を中間言語命令列に追加します。
	*@param code 追加する命令
	*/
	private void add(Code code){
		medcodes.add(code);
	}
	/**
	*次の字句を取得します。
	*@return 次の字句
	*/
	private Token getNextToken() throws ScriptException{
		Token next = analyzer.getNextToken();
		if(next != null) return next;
		throw error("getNextToken_exception");
	}
	/**
	*次の字句が存在するか確認します。
	*@return 存在する場合true
	*/
	private boolean hasNextToken() throws ScriptException{
		Token next = analyzer.getNextToken();
		if(next == null) return false;
		analyzer.ungetToken(next);
		return true;
	}
	/**
	*取得済みの字句をキューに待避します。
	*@param token 待避させる字句
	*/
	private void ungetToken(Token token){
		analyzer.ungetToken(token);
	}
	/**
	*次の字句が適切な方であるか確認します。
	*@param expected 期待される字句の型
	*@return 次の字句
	*@throws ScriptException 期待されない型の場合
	*/
	private Token checkNextToken(Enum... expected)
	throws ScriptException{
		return analyzer.checkNextToken(expected);
	}
	/**
	*構文違反があった場合に例外を生成します。
	*@param key メッセージの国際化キー
	*@param args メッセージの引数
	*@return 生成した例外
	*/
	private ScriptException error(String key, Object... args){
		int line = analyzer.getLineNumber();
		int colm = analyzer.getColumnNumber();
		String msg = localize.translate(key, args);
		return new ScriptException(
			msg+" at line : "+line+"\n => " 
			+ analyzer.getLine(), null, line, colm
		);
	}
	/**
	*構文違反があった場合に例外を生成します。
	*@param token 違反した字句
	*@return 生成した例外
	*/
	private ScriptException error(Token token){
		return error("error_Token_exception", token);
	}
	/**
	*ステートメントを解析します。
	*/
	private void parseStatement() throws ScriptException{
		
		Token token = getNextToken();
		
		if(functions.isFunctionDefining()){
			if(token.getType() instanceof Keyword){
				switch((Keyword)token.getType()){
					case BREAK  :parseBreak();      return;
					case EXIT   :parseExit();       return;
					case FOR    :parseFor();        return;
					case IF     :parseIf();         return;
					case RETURN :parseReturn();     return;
					case SWITCH :parseSwitch();     return;
					case TRY    :parseTry();        return;
					case VAR    :parseDeclarative();return;
					case WRITE  :parseWrite();      return;
					case WRITELN:parseWriteln();    return;
				}
			}
			if(token.isType(Keyword.NEW)){
				parseConstruct();
				parseDot();
				checkNextToken(Operator.SEMICOLON);
			}else if(token.isType(Operator.OPEN_BRACE)){
				ungetToken(token);
				parseBlock();
			}else if(token.isIdentifier()){
				parseIdentifier(token);
				add(new Code(OP_DEL));
				checkNextToken(Operator.SEMICOLON);
			}else{
				throw error(token);
			}
		}else if(token.isType(Keyword.FUNCT) ){
			parseFunction();
		}else if(token.isType(Keyword.IMPORT)){
			parseImport();
		}else{
			throw error(token);
		}
	}
	/**
	*式の最小単位を解析します。
	*/
	private void parsePrimary() throws ScriptException{
		
		Token token = getNextToken();
		
		if(token.getType() instanceof TokenType){
			switch((TokenType)token.getType()){
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
		}else if(token.getType() instanceof Keyword){
			switch((Keyword)token.getType()){
				case TRUE :
					add(new Code(OP_LIT_PUSH));
					add(new Code(true));
					return;
				case FALSE:
					add(new Code(OP_LIT_PUSH));
					add(new Code(false));
					return;
				case FUNCT:
					parseClosure();
					return;
				case NULL :
					add(new Code(OP_LIT_PUSH));
					add(new Code(null));
					return;
				case NEW  :
					parseConstruct();
					return;
				default:
					parseIdentifier(token);
					return;
			}
		}else{
			switch((Operator)token.getType()){
				case OPEN_PARENS:
					parseExpression();
					checkNextToken(Operator.CLOSE_PARENS);
					parseClosureCall();
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
		ungetToken(next);
		
		if(next.isType(Operator.DECLARE)){
			parseShortDeclarative(token);
		}else if(next.isAssignOperator()){
			parseVariableAssign(token);
		}else if(classes.exists(token)){
			if(next.isType(Operator.DOT)){
				add(new Code(OP_LIT_PUSH));
				add(new Code(null, classes.get(token)));
			}else{
				throw error("parseIdentifier_exception");
			}
		}else if(next.isType(Operator.OPEN_PARENS)){
			if(names.exists(token)){ //局所関数
				parseClosureCall(token);
			}else{ //大域関数
				parseFunctionCall(token);
			}
		}else{ //変数/引数
			NameTable.Address addr = names.search(token);
			if(addr.isParam){
				add(new Code(OP_ARG_PUSH));
				add(new Code(addr.index));
			}else{
				add(new Code(OP_VAR_PUSH));
				add(new Code(addr.nest));
				add(new Code(addr.index));
			}if(next.isType(Operator.INCREMENT)){
				getNextToken();
				if(addr.isParam){
					add(new Code(OP_ARG_INCR));
					add(new Code(addr.index));
				}else{
					add(new Code(OP_VAR_INCR));
					add(new Code(addr.nest));
					add(new Code(addr.index));
				}
			}else if(next.isType(Operator.DECREMENT)){
				getNextToken();
				if(addr.isParam){
					add(new Code(OP_ARG_DECR));
					add(new Code(addr.index));
				}else{
					add(new Code(OP_VAR_DECR));
					add(new Code(addr.nest));
					add(new Code(addr.index));
				}
			}
		}
		parseDot();
	}
	/**
	*ドット演算子式を解析します。
	*/
	private void parseDot() throws ScriptException{
		
		parsePrimary();
		Token token, next;
		
		while(true){
			token = getNextToken();
			
			if(!token.isType(Operator.DOT)){
				ungetToken(token);
				return;
			}
			token = checkNextToken(TokenType.IDENTIFIER);
			next  = getNextToken();
			
			if(next.isType(Operator.OPEN_PARENS)){
				parseMethodCall(token);
			}else if(next.isAssignOperator()){
				ungetToken(next);
				parseFieldAssign(token);
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
		
		if(token.isType(Operator.ADD)){
			parseDot();
		}else if(token.isType(Operator.SUB)){
			parseDot();
			add(new Code(OP_NEG));
		}else if(token.isType(Operator.NOT, Operator.BIT_NOT)){
			parseDot();
			add(new Code(OP_NOT));
		}else{
			ungetToken(token);
			parseDot();
		}
	}
	/**
	*累乗演算子を主演算子とする式を解析します。
	*/
	private void parsePower() throws ScriptException{
		
		parseUnary();
		
		while(true){
			Token token = getNextToken();
			
			if(!token.isType(Operator.POW)){
				ungetToken(token);
				return;
			}
			parsePower();
			add(new Code(OP_POW));
		}
	}
	/**
	*乗除算演算子または剰余演算子を主演算子とする式を解析します。
	*/
	private void parseMultiplicative() throws ScriptException{
		
		parsePower();
		
		while(true){
			Token token = getNextToken();
			
			if(token.getType() != Operator.MUL
			&& token.getType() != Operator.DIV
			&& token.getType() != Operator.MOD){
				ungetToken(token);
				return;
			}
			parsePower();
			add(new Code(token.toCode()));
		}
	}
	/**
	*加減算演算子を主演算子とする式を解析します。
	*/
	private void parseAdditive() throws ScriptException{
		
		parseMultiplicative();
		
		while(true){
			Token token = getNextToken();
			
			if(token.getType() != Operator.ADD
			&& token.getType() != Operator.SUB){
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
		
		while(true){
			Token token = getNextToken();
			
			if( token.getType() != Operator.BIT_LEFT
			&&  token.getType() != Operator.BIT_RIGHT){
				ungetToken(token);
				return;
			}
			parseAdditive();
			add(new Code(token.toCode()));
		}
	}
	/**
	*関係比較演算子を主演算子とする式を解析します。
	*/
	private void parseRelational() throws ScriptException{
		
		parseBitShift();
		
		while(true){
			Token token = getNextToken();
			
			if( token.getType() != Operator.GREATER
			&&  token.getType() != Operator.GREATER_EQUAL
			&&  token.getType() != Operator.LESS
			&&  token.getType() != Operator.LESS_EQUAL
			&&  token.getType() != Keyword.INSTOF){
				ungetToken(token);
				return;
			}
			if(token.getType() != Keyword.INSTOF){
				parseBitShift();
				add(new Code(token.toCode()));
			}else{
				add(new Code(OP_OBJ_METHOD));
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
		
		while(true){
			Token token = getNextToken();
			
			if( token.getType() != Operator.IS
			&&  token.getType() != Operator.EQUAL
			&&  token.getType() != Operator.NOT_EQUAL
			&&  token.getType() != Operator.COMPARE){
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
		
		while(true){
			Token token = getNextToken();
			
			if(!token.isType(Operator.AND)){
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
		
		while(true){
			Token token = getNextToken();
			
			if(!token.isType(Operator.XOR)){
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
		
		while(true){
			Token token = getNextToken();
			
			if(!token.isType(Operator.OR)){
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
		
		while(true){
			Token token = getNextToken();
			
			if(!token.isType(Operator.SHORT_AND)){
				ungetToken(token);
				return;
			}
			int label = labels.addLabel();
			
			add(new Code(OP_SHORT_AND));
			add(new Code(label));
			
			parseOr();
			
			add(new Code(OP_AND));
			labels.setAddress(label, medcodes.size());
		}
	}
	/**
	*短絡論理和演算子を主演算子とする式を解析します。
	*/
	private void parseShortCircuitOr() throws ScriptException{
		
		parseShortCircuitAnd();
		
		while(true){
			Token token = getNextToken();
			
			if(!token.isType(Operator.SHORT_OR)){
				ungetToken(token);
				return;
			}
			int label = labels.addLabel();
			
			add(new Code(OP_SHORT_OR));
			add(new Code(label));
			
			parseShortCircuitAnd();
			
			add(new Code(OP_OR));
			labels.setAddress(label, medcodes.size());
		}
	}
	/**
	*三項演算子を主演算子とする式を解析します。
	*/
	private void parseTernary() throws ScriptException{
		
		parseShortCircuitOr();
		
		while(true){
			Token token = getNextToken();
			
			if(!token.isType(Operator.TERNARY)){
				ungetToken(token);
				return;
			}
			int elseLabel = labels.addLabel();
			
			add(new Code(OP_GOTO_FALSE));
			add(new Code(elseLabel));
			
			parseTernary();
			
			int endLabel  = labels.addLabel();
			add(new Code(OP_GOTO));
			add(new Code(endLabel));
			
			checkNextToken(Operator.COLON);
			
			labels.setAddress(elseLabel, medcodes.size());
			parseTernary();
			
			labels.setAddress(endLabel, medcodes.size());
		}
	}
	/**
	*式を解析します。
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
		
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		
		checkNextToken(Operator.OPEN_PARENS);
		if(!parseExpression()){
			add(new Code(OP_LIT_PUSH));
			add(new Code(true));
		}
		checkNextToken(Operator.CLOSE_PARENS);
		
		int elseLabel = labels.addLabel();
		
		add(new Code(OP_GOTO_FALSE));
		add(new Code(elseLabel));
		
		parseBlock();
		
		Token token = getNextToken();
		
		if(token.getType() == Keyword.ELSE){
			int endIfLabel = labels.addLabel();
			
			add(new Code(OP_GOTO));
			add(new Code(endIfLabel));
			
			labels.setAddress(elseLabel, medcodes.size());
			
			names.exitChildScope();
			names.enterChildScope(NameTable.BLOCK_SCOPE);
			
			parseBlock();
			
			labels.setAddress(endIfLabel, medcodes.size());
		}else{
			ungetToken(token);
			labels.setAddress(elseLabel, medcodes.size());
		}
		names.exitChildScope();
	}
	/**
	*for文を解析します。
	*/
	private void parseFor() throws ScriptException{
		
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		
		Token name = checkNextToken(
			Operator.OPEN_PARENS,
			TokenType.IDENTIFIER
		);
		
		if(name.getType() == TokenType.IDENTIFIER){
			checkNextToken(Operator.OPEN_PARENS);
		}else name = null;
		
		int start  = medcodes.size();
		parseExpression();
		
		Token token = checkNextToken(
			Operator.CLOSE_PARENS,
			Operator.SEMICOLON
		);
		
		if(token.getType() == Operator.CLOSE_PARENS)
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
		
		int ifLabel  = labels.addLabel();
		labels.setAddress(ifLabel, medcodes.size());
		
		boolean omis = false; //条件式省略
		
		//条件式
		Token token = getNextToken();
		
		if(token.getType() != Operator.SEMICOLON){
			ungetToken(token);
			parseExpression();
			checkNextToken(Operator.SEMICOLON);
		}else omis = true;
		
		//入れ子構造
		int endLabel = labels.addLabel(name);
		int intLabel = labels.addLabel();
		int resLabel = labels.addLabel();
		
		if(omis){
			add(new Code(OP_GOTO));
			add(new Code(intLabel));
		}else{
			add(new Code(OP_GOTO_FALSE));
			add(new Code(endLabel));
			add(new Code(OP_GOTO));
			add(new Code(intLabel));
		}
		
		//再初期化式
		labels.setAddress(resLabel, medcodes.size());
		if(parseExpression()) add(new Code(OP_DEL));
		checkNextToken(Operator.CLOSE_PARENS);
		
		add(new Code(OP_GOTO));
		add(new Code(ifLabel));
		
		//本文
		labels.setAddress(intLabel, medcodes.size());
		parseBlock();
		
		add(new Code(OP_GOTO));
		add(new Code(resLabel));
		
		labels.setAddress(endLabel, medcodes.size());
	}
	/**
	*条件式のみを持つfor文を解析します。
	*@param start for文の開始位置
	*@param name for文の名前
	*/
	private void parseShortFor(int start, Token name) throws ScriptException{
		
		int ifLabel  = labels.addLabel();
		labels.setAddress(ifLabel, start);
		
		int endLabel = labels.addLabel(name);
		
		//条件式省略確認
		if(start < medcodes.size()){
			add(new Code(OP_GOTO_FALSE));
			add(new Code(endLabel));
		}
		
		parseBlock();
		
		add(new Code(OP_GOTO));
		add(new Code(ifLabel));
		
		labels.setAddress(endLabel, medcodes.size());
	}
	/**
	*switch文を解析します。
	*/
	private void parseSwitch() throws ScriptException{
		
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		
		Token token = checkNextToken(
			Operator.OPEN_PARENS,
			TokenType.IDENTIFIER
		);
		
		if(token.getType() == TokenType.IDENTIFIER){
			checkNextToken(Operator.OPEN_PARENS);
		}else token = null;
		
		boolean omis = !parseExpression(); //式省略
		
		checkNextToken(Operator.CLOSE_PARENS);
		checkNextToken(Operator.OPEN_BRACE);
		
		int endLabel = labels.addLabel(token);
		while(true){
			token = checkNextToken(
				Operator.CLOSE_BRACE,
				Keyword.DEFAULT,
				Keyword.CASE
			);
			if(token.isType(Operator.CLOSE_BRACE)) break;
			if(token.isType(Keyword.DEFAULT))parseDefault(endLabel);
			else parseCase(endLabel, omis);
		}
		labels.setAddress(endLabel, medcodes.size());
		names.exitChildScope();
		
		if(!omis) add(new Code(OP_DEL));
	}
	/**
	*case文を解析します。
	*@param endSwitch switch文のブロック
	*@param omis 条件式が省略されている場合
	*/
	private void parseCase(int endSwitch, boolean omis) throws ScriptException{
		
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		int end = labels.addLabel();
		
		if(!omis)add(new Code(OP_DUP));
		int start = medcodes.size();
		if(!parseExpression()){
			throw error("parseCase_exception");
		}
		if(!omis)add(new Code(OP_EQU));
		
		checkNextToken(Operator.COLON);
		
		add(new Code(OP_GOTO_FALSE));
		add(new Code(end));
		
		Token token;
		while(true){
			ungetToken(token = getNextToken());
			if(token.isType(
				Operator.CLOSE_BRACE,
				Keyword.DEFAULT,
				Keyword.CASE
			))break;
			parseStatement();
		}
		add(new Code(OP_GOTO));
		add(new Code(endSwitch));
		
		labels.setAddress(end, medcodes.size());
		names.exitChildScope();
	}
	/**
	*default文を解析します。
	*@param endSwitch switch文のブロック
	*/
	private void parseDefault(int endSwitch) throws ScriptException{
		
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		int end = labels.addLabel();
		
		checkNextToken(Operator.COLON);
		
		Token token;
		while(true){
			ungetToken(token = getNextToken());
			if(token.isType(Keyword.CASE)){
				throw error("parseDefault_exception_case");
			}else if(token.isType(Keyword.DEFAULT)){
				throw error("parseDefault_exception_default");
			}else if(token.isType(Operator.CLOSE_BRACE))break;
			parseStatement();
		}
		labels.setAddress(end, medcodes.size());
		names.exitChildScope();
	}
	/**
	*break文を解析します。
	*/
	private void parseBreak() throws ScriptException{
		
		Token token = getNextToken();
		
		if(token.getType() != TokenType.IDENTIFIER){
			throw error("parseBreak_exception");
		}
		checkNextToken(Operator.SEMICOLON);
		
		add(new Code(OP_GOTO));
		add(new Code(labels.searchLabel(token)));
	}
	/**
	*例外処理構文を解析します。
	*/
	private void parseTry() throws ScriptException{
		
		int endLabel   = labels.addLabel();
		int catchLabel = labels.addLabel();
		
		add(new Code(OP_TRY_PUSH));
		add(new Code(catchLabel));
		
		parseBlock();
		
		add(new Code(OP_TRY_DEL));
		add(new Code(OP_GOTO));
		add(new Code(endLabel));
		
		checkNextToken(Keyword.CATCH);
		checkNextToken(Operator.OPEN_PARENS);
		
		labels.setAddress(catchLabel, medcodes.size());
		
		Token name = checkNextToken(TokenType.IDENTIFIER);
		checkNextToken(Operator.CLOSE_PARENS);
		
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		
		NameTable.Address addr = names.createLocal(name);
		
		add(new Code(OP_VAR_ASSN));
		add(new Code(addr.nest));
		add(new Code(addr.index));
		
		parseBlock();
		names.exitChildScope();
		
		labels.setAddress(endLabel, medcodes.size());
	}
	/**
	*write文を解析します。
	*/
	private void parseWrite() throws ScriptException{
		
		if(!parseExpression()){
			add(new Code(OP_LIT_PUSH));
			add(new Code(""));
		}
		add(new Code(OP_PRINT));
		
		checkNextToken(Operator.SEMICOLON);
	}
	/**
	*writeln文を解析します。
	*/
	private void parseWriteln() throws ScriptException{
		
		if(!parseExpression()){
			add(new Code(OP_LIT_PUSH));
			add(new Code(""));
		}
		add(new Code(OP_PRINTLN));
		
		checkNextToken(Operator.SEMICOLON);
	}
	/**
	 *略式変数宣言文を解析します。
	 *@param name 変数名
	 */
	private void parseShortDeclarative(Token name) throws ScriptException{
		
		checkNextToken(Operator.DECLARE);
		
		if(!parseExpression()){
			throw error("parseShortDeclarative_exception", name);
		}
		NameTable.Address addr = names.createLocal(name);
		
		add(new Code(OP_VAR_ASSN));
		add(new Code(addr.nest));
		add(new Code(addr.index));
		
		Token token = getNextToken();
		
		if(token.getType() != Operator.SEMICOLON){
			add(new Code(OP_VAR_PUSH));
			add(new Code(addr.nest));
			add(new Code(addr.index));
		}
		ungetToken(token);
	}
	/**
	*変数宣言文を解析します。
	*/
	private void parseDeclarative() throws ScriptException{
		
		while(true){
			Token token = getNextToken();
			if(token.isIdentifier()){
				Token next = checkNextToken(
					Operator.ASSIGN,
					Operator.COMMA,
					Operator.SEMICOLON);
				
				if(next.isType(Operator.ASSIGN)){
					parseExpression();
					next = getNextToken();
				}else{
					add(new Code(OP_LIT_PUSH));
					add(new Code(null));
				}
				NameTable.Address addr
					= names.createLocal(token);
				
				add(new Code(OP_VAR_ASSN));
				add(new Code(addr.nest));
				add(new Code(addr.index));
				
				if(next.isType(Operator.SEMICOLON))return;
			}else throw error(token);
		}
	}
	/**
	*変数/引数代入文を解析します。
	*@param name 変数名
	*/
	private void parseVariableAssign(Token name) throws ScriptException{
		
		NameTable.Address addr = names.search(name);
		Token token = getNextToken();
		
		if(!token.isType(Operator.ASSIGN)){
			
			if(addr.isParam){
				add(new Code(OP_ARG_PUSH));
				add(new Code(addr.index));
			}else{
				add(new Code(OP_VAR_PUSH));
				add(new Code(addr.nest));
				add(new Code(addr.index));
			}
			if(!parseExpression()){
				throw error("parseVariableAssign_exception", name);
			}
			add(new Code(token.toCode()));
		}else if(!parseExpression()){
			throw error("parseVariableAssign_exception", name);
		}
		
		if(addr.isParam){
			add(new Code(OP_ARG_ASSN));
			add(new Code(addr.index));
			add(new Code(OP_ARG_PUSH));
			add(new Code(addr.index));
		}else{
			add(new Code(OP_VAR_ASSN));
			add(new Code(addr.nest));
			add(new Code(addr.index));
			add(new Code(OP_VAR_PUSH));
			add(new Code(addr.nest));
			add(new Code(addr.index));
		}
	}
	/**
	*フィールド代入文を解析します。
	*@param name フィールド名
	*/
	private void parseFieldAssign(Token name) throws ScriptException{
		
		Token token = getNextToken();
		
		if(!token.isType(Operator.ASSIGN)){
			add(new Code(OP_DUP));
			add(new Code(OP_FIELD_PUSH));
			add(new Code(name.toString()));
			
			if(!parseExpression()){
				throw error("parseFieldAssign_exception", name);
			}
			add(new Code(token.toCode()));
		}else if(!parseExpression()){
			throw error("parseFieldAssign_exception", name);
		}
		add(new Code(OP_FIELD_ASSN));
		add(new Code(name.toString()));
	}
	/**
	*exit文を解析します。
	*/
	private void parseExit() throws ScriptException{
		
		Token token = getNextToken();
		
		if(token.getType() == Operator.SEMICOLON){
			add(new Code(OP_LIT_PUSH));
			add(new Code(null));
		}else{
			ungetToken(token);
			parseExpression();
			checkNextToken(Operator.SEMICOLON);
		}
		
		add(new Code(OP_EXIT));
	}
	/**
	*大域関数定義文を解析します。
	*/
	private void parseFunction() throws ScriptException{
		
		names.enterChildScope(NameTable.FUNCTION_SCOPE);
		
		Token name = getNextToken();
		if(!name.isIdentifier()){
			throw error("parseFunction_exception");
		}
		int startAddress = medcodes.size();
		int params = parseParameters();
		
		add(new Code(OP_FUNC_FRAME));
		add(new Code(name.toString()+"("+params+")"));
		add(null); //後でローカル変数の個数を代入
		
		int index  = medcodes.size() -1;
		
		functions.define(name, params);
		parseBlock();
		
		add(new Code(OP_LIT_PUSH));
		add(new Code(null));
		add(new Code(OP_RETURN));
		
		int block = labels.searchOrCreateLabel(name, params);
		labels.setAddress(block, startAddress);
		
		medcodes.set(index, new Code(names.getLocalCount()));
		
		functions.endDefine();
		names.exitChildScope();
	}
	/**
	*局所関数定義文を解析します。
	*/
	private void parseClosure() throws ScriptException{
		
		names.enterChildScope(NameTable.FUNCTION_SCOPE);
		
		int endLabel = labels.addLabel();
		
		add(new Code(OP_GOTO));
		add(new Code(endLabel));
		
		int params = parseParameters();
		int start  = medcodes.size();
		
		parseBlock();
		
		add(new Code(OP_LIT_PUSH));
		add(new Code(null));
		add(new Code(OP_RETURN));
		
		labels.setAddress(endLabel, medcodes.size());
		int locals = names.getLocalCount();
		
		add(new Code(OP_CLOS_PUSH));
		add(new Code(start));
		add(new Code(locals));
		
		names.exitChildScope();
	}
	/**
	*引数の宣言文を解析します。
	*@return 引数の個数
	*/
	private int parseParameters() throws ScriptException{
		
		Token token = getNextToken();
		
		if(token.isType(Operator.OPEN_PARENS)){
			while(true){
				token = checkNextToken(
					TokenType.IDENTIFIER,
					Operator.CLOSE_PARENS
				);
				if(token.isType(Operator.CLOSE_PARENS)) break;
				names.createParam(token);
				
				token = checkNextToken(
					Operator.COMMA,
					Operator.CLOSE_PARENS
				);
				if(token.isType(Operator.CLOSE_PARENS)) break;
			}
		}else ungetToken(token);
		
		return names.getParamCount();
	}
	/**
	*大域関数の呼び出し文を解析します。
	*@param name 関数の名前
	*/
	private void parseFunctionCall(Token name) throws ScriptException{
		
		checkNextToken(Operator.OPEN_PARENS);
		int args = parseArguments();
		
		functions.add(name, args);
		int label = labels.searchOrCreateLabel(name, args);
		
		add(new Code(OP_FUNC_CALL));
		add(new Code(args));
		add(new Code(label));
		
		//クロージャが返り値の場合
		parseClosureCall();
	}
	/**
	*変数に格納された局所関数の呼び出し文を解析します。
	*@param name 格納変数名
	*/
	private void parseClosureCall(Token name) throws ScriptException{
		
		checkNextToken(Operator.OPEN_PARENS);
		
		NameTable.Address addr = names.search(name);
		if(addr.isParam){
			add(new Code(OP_ARG_PUSH));
			add(new Code(addr.index));
		}else{
			add(new Code(OP_VAR_PUSH));
			add(new Code(addr.nest));
			add(new Code(addr.index));
		}
		int args = parseArguments();
		add(new Code(OP_CLOS_CALL));
		add(new Code(args));
		
		//クロージャが返り値の場合
		parseClosureCall();
	}
	/**
	*局所関数の呼び出し文を解析します。
	*/
	private void parseClosureCall() throws ScriptException{
		
		while(true){
			Token token = getNextToken();
			if(token.isType(Operator.OPEN_PARENS)){
				int args = parseArguments();
				add(new Code(OP_CLOS_CALL));
				add(new Code(args));
			}else{
				ungetToken(token);
				return;
			}
		}
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
			
			if(!token.isType(Operator.CLOSE_PARENS)){
				ungetToken(token);
				parseExpression();
				token = checkNextToken(
					Operator.COMMA,
					Operator.CLOSE_PARENS
				);
				count++;
			}
			if(token.isType(Operator.CLOSE_PARENS)) return count;
		}
	}
	/**
	*return文を解析します。
	*/
	private void parseReturn() throws ScriptException{
		
		Token token = getNextToken();
		
		if(token.isType(Operator.SEMICOLON)){
			add(new Code(OP_LIT_PUSH));
			add(new Code(null));
		}else{
			ungetToken(token);
			parseExpression();
			checkNextToken(Operator.SEMICOLON);
		}
		
		add(new Code(OP_RETURN));
	}
	/**
	*new文を解析します。
	*/
	private void parseConstruct() throws ScriptException{
		
		//クラス名
		Token token = checkNextToken(TokenType.IDENTIFIER);
		Class cls   = classes.get(token);
		
		checkNextToken(Operator.OPEN_PARENS);
		int count = parseArguments();
		
		add(new Code(OP_OBJ_TOINST));
		add(new Code(cls));
		add(new Code(count));
	}
	/**
	*メソッドコール文を解析します。
	*@param name メソッド名
	*/
	private void parseMethodCall(Token name) throws ScriptException{
		
		int args = parseArguments();
		
		add(new Code(OP_OBJ_METHOD));
		add(new Code(name.toString()));
		add(new Code(args));
		
		//クロージャが返り値の場合
		parseClosureCall();
	}
	/**
	*import文を解析します。
	*/
	private void parseImport() throws ScriptException{
		
		StringBuilder name = new StringBuilder(32);
		loop: while(true){
			Token token = checkNextToken(
				TokenType.IDENTIFIER,
				Operator.MUL
			);
			if(token.isType(Operator.MUL)){
				checkNextToken(Operator.SEMICOLON);
				name.append(token);
				break loop;
			}else name.append(token);
			token = checkNextToken(
				Operator.DOT, Operator.SEMICOLON
			);
			if(token.isType(Operator.DOT)){
				name.append(token);
			}else break loop;
		}
		classes.add(name.toString());
	}
	/**
	*ブロックを解析します。
	*/
	private void parseBlock() throws ScriptException{
		
		Token token = getNextToken();
		
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		
		boolean omis = !token.isType(Operator.OPEN_BRACE);
		if(omis) ungetToken(token);
		
		while(true){
			token = getNextToken();
			
			if(token.isType(Operator.CLOSE_BRACE)) break;
			ungetToken(token);
			parseStatement();
			if(omis) break;
		}
		names.exitChildScope();
	}
	/**
	*生成されたコードにブロックの実アドレスを書き込みます。
	*@throws ScriptException まず発生しない例外
	*/
	private void setLabelAddresses() throws ScriptException{
		
		int length = medcodes.size();
		for(int i=0; i<length; i++){
			int code = medcodes.get(i).toInt();
			//2ワード命令
			switch(code){
			case OP_LIT_PUSH   :
			case OP_ARG_PUSH   :
			case OP_ARG_ASSN   :
			case OP_CLOS_CALL  :
			case OP_FIELD_ASSN :
			case OP_FIELD_PUSH :
				i++;
				break;
			//3ワード命令
			case OP_FUNC_FRAME :
			case OP_VAR_PUSH   :
			case OP_VAR_ASSN   :
			case OP_CLOS_PUSH  :
			case OP_OBJ_TOINST :
			case OP_OBJ_METHOD :
				i += 2;
				break;
			//ラベル使用命令
			case OP_FUNC_CALL  :
				i++;
			case OP_GOTO       :
			case OP_GOTO_FALSE :
			case OP_SHORT_AND  :
			case OP_SHORT_OR   :
			case OP_TRY_PUSH   :
				int addr = labels.getAddress(
					medcodes.get(++i).toInt());
				medcodes.set(i, new Code(addr));
			}
		}
	}
}