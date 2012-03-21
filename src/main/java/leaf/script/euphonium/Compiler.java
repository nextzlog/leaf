/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium;

import leaf.script.common.tree.*;
import leaf.script.common.util.Code;
import leaf.script.euphonium.NameTable.Address;
import leaf.script.euphonium.tree.expression.*;
import leaf.script.euphonium.tree.reflect.*;
import leaf.script.euphonium.tree.statement.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptException;

import leaf.manager.LeafLocalizeManager;

// Parserの実装を変更するための一時的なクラス
// 構文木への対応が完了したらParserに改名する

/**
 *AriCE処理系の構文解析器の実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月28日
 */
public final class Compiler extends CodesAndTokens{
	private static final Code NULL  = new Code(null);
	private static final Code TRUE  = new Code(true);
	private static final Code FALSE = new Code(false);
	
	private final ClassTable    classes;
	private final NameTable     names;
	private final FunctionTable functions;
	
	private final LexicalAnalyzer analyzer;
	private Token nextToken;
	
	private final LeafLocalizeManager localize;
	
	/**
	 *構文解析器を初期化して生成します。
	 */
	public Compiler(){
		analyzer  = new LexicalAnalyzer  ();
		classes   = new ClassTable   (analyzer);
		functions = new FunctionTable(analyzer);
		names     = new NameTable    (analyzer);
		
		localize = LeafLocalizeManager.getInstance(Parser.class);
	}
	/**
	 *構文解析器を初期化します。
	 */
	private void initialize(){
		functions.clear();
		classes.clear();
		names.clear();
	}
	/**
	 *スクリプトを読み込むリーダを指定してコンパイルします。
	 *@param reader リーダー
	 *@return 抽象構文木
	 *@throws ScriptException 構文違反があった場合
	 */
	public Program compile(Reader reader) throws ScriptException{
		List<Function> list = new ArrayList<Function>();
		try{
			analyzer.load(reader);
			
			Token token;
			while((token = analyzer.getNextToken()) != null){
				if(!token.isType(Keyword.IMPORT)){
					ungetToken(token);
					Statement tree = parseStatement();
					if(tree instanceof Function)
						list.add((Function)tree);
				}else parseImport();
			}
			functions.checkAllDefined();
			
			return new Program(list);
		}finally{
			initialize();
		}
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
	 *関数の内部または外部に記述された文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseStatement() throws ScriptException{
		
		Token token = getNextToken();
		
		if(functions.isFunctionDefining()){
			if(token.getType() instanceof Keyword){
				switch((Keyword)token.getType()){
					case BREAK   : return parseBreak();
					case CONTIN  : return parseContinue();
					case EXIT    : return parseExit();
					case IF      : return parseIf();
					case FOR     : return parseFor();
					case RETURN  : return parseReturn();
					case TRY     : return parseTry();
					case VAR     : return parseDeclarative();
					case WRITE   : return parseWrite();
					case WRITELN : return parseWriteln();
				}
			}
			
			if(token.isType(Operator.OPEN_BRACE)) return parseBlock();
			
			ungetToken(token);
			Node exp = parseExpression();
			
			checkNextToken(Operator.SEMICOLON);
			return new Expression(exp);
		}
		
		if(token.isType(Keyword.FUNCT)) return parseFunction();
		throw error(token);
	}
	/**
	 *局所変数及び引数への代入式を解析します。
	 *
	 *@param node 変数木
	 *@return 式の解析木
	 */
	private Node parseAssign(VariableNode node) throws ScriptException{
		
		Node assign;
		Token op = getNextToken();
		
		if(!op.isType(Operator.ASSIGN)){
			Node exp = parseExpression();
			assign = createNode(op.toCode(), node, exp);
		}else assign = parseExpression();
		
		return new AssignNode(node, assign);
	}
	/**
	 *変数式を解析します。
	 *
	 *@param name 変数名
	 *@return 式の解析木
	 */
	private Node parseVariable(Token name) throws ScriptException{
		
		Address addr = names.search(name);
		VariableNode var = new VariableNode(new Code(addr));
		
		Token next = getNextToken();
		
		if(next.isType(Operator.INCREMENT)) return new Increment(var);
		if(next.isType(Operator.DECREMENT)) return new Decrement(var);
		
		ungetToken(next);
		if(next.isAssignOperator()) return parseAssign(var);
		
		return var;
	}
	/**
	 *識別子で始まる式を解析します。
	 *
	 *@param id 識別子
	 *@return 式の解析木
	 */
	private Node parseIdentifier(Token id) throws ScriptException{
		
		if(names.exists(id)) return parseVariable(id);
		
		Token next = getNextToken();
		ungetToken(next);
		
		if(next.isType(Operator.OPEN_PARENS)){ // 大域関数
			List<Node> args = parseArguments();
			functions.add(id, args.size());
			return new CallNode(new LiteralNode(id.toString()), args);
		}
		
		// 未定義の識別子
		return new VariableNode(new Code(names.search(id))); // error!
	}
	/**
	 *式の最小構成単位を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parsePrimary() throws ScriptException{
		
		Token token = getNextToken();
		
		if(token.getType() instanceof TokenType){
			switch((TokenType)token.getType()){
				case INTEGER    : return new LiteralNode(token.toInteger());
				case DOUBLE     : return new LiteralNode(token.toDouble());
				case CHARACTER  : return new LiteralNode(token.toCharacter());
				case STRING     : return new LiteralNode(token.toString());
				case IDENTIFIER : return parseIdentifier(token);
			}
		}else if(token.getType() instanceof Keyword){
			switch((Keyword)token.getType()){
				case FALSE : return new LiteralNode(FALSE);
				case FUNCT : return parseClosure();
				case NULL  : return new LiteralNode(NULL);
				case TRUE  : return new LiteralNode(TRUE);
				default: return parseIdentifier(token);
			}
		}else if(token.isType(Operator.OPEN_PARENS)){ // 括弧の入れ子
			Node child = parseExpression();
			checkNextToken(Operator.CLOSE_PARENS);
			return child;
		}
		return null;
	}
	/**
	 *関数呼出演算子式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseCall() throws ScriptException{
		
		Node  fnode = parsePrimary();
		Token token = getNextToken();
		ungetToken(token);
		
		if(token.isType(Operator.OPEN_PARENS)){
			List<Node> args = parseArguments();
			return new CallNode(fnode, args);
		}else return fnode;
	}
	/**
	 *単項演算子式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseUnary() throws ScriptException{
		
		Token token = getNextToken();
		
		if(token.isType(Operator.ADD, Operator.SUB, Operator.NOT)){
			return createNode(token.toCode(), parsePrimary());
		}else{
			ungetToken(token);
			return parseCall();
		}
	}
	/**
	 *累乗演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parsePower() throws ScriptException{
		
		Node left = parseUnary();
		Token token = getNextToken();
		
		if(token.isType(Operator.POW)){
			return new Power(left, parsePower());
		}else{
			ungetToken(token);
			return left;
		}
	}
	/**
	 *乗除算演算子または剰余演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseMultiplicative() throws ScriptException{
		
		Node left = parsePower();
		
		while(true){
			Token token = getNextToken();
			
			if(token.isType(Operator.MUL, Operator.DIV, Operator.MOD)){
				left = createNode(token.toCode(), left, parsePower());
			}else{
				ungetToken(token);
				return left;
			}
		}
	}
	/**
	 *加減算演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseAdditive() throws ScriptException{
		
		Node left = parseMultiplicative();
		
		while(true){
			Token token = getNextToken();
			
			if(token.isType(Operator.ADD, Operator.SUB)){
				Node right = parseMultiplicative();
				left = createNode(token.toCode(), left, right);
			}else{
				ungetToken(token);
				return left;
			}
		}
	}
	/**
	 *ビットシフト演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseBitShift() throws ScriptException{
		
		Node left = parseAdditive();
		
		while(true){
			Token token = getNextToken();
			
			if(token.isType(Operator.BIT_LEFT, Operator.BIT_RIGHT)){
				Node right = parseAdditive();
				left = createNode(token.toCode(), left, right);
			}else{
				ungetToken(token);
				return left;
			}
		}
	}
	/**
	 *関係演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseRelational() throws ScriptException{
		
		Node left = parseBitShift();
		
		while(true){
			Token token = getNextToken();
			
			if(!token.isRelationalOperator()){
				ungetToken(token);
				return left;
			}else if(token.isType(Keyword.INSTOF)){
				Code cls = new Code (classes.get(getNextToken()));
				left = new InstanceOf(left, new LiteralNode(cls));
			}else{
				Node right = parseBitShift();
				left = createNode(token.toCode(), left, right);
			}
		}
	}
	/**
	 *等価演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseEquality() throws ScriptException{
		
		Node left = parseRelational();
		
		while(true){
			Token token = getNextToken();
			
			if(token.isEqualityOperator()){
				Node right = parseRelational();
				left = createNode(token.toCode(), left, right);
			}else{
				ungetToken(token);
				return left;
			}
		}
	}
	/**
	 *論理積演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseAnd() throws ScriptException{
		
		Node left = parseEquality();
		
		while(true){
			Token token = getNextToken();
			
			if(token.isType(Operator.AND)){
				left = new And(left, parseEquality());
			}else{
				ungetToken(token);
				return left;
			}
		}
	}
	/**
	 *排他的論理和演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseExclusiveOr() throws ScriptException{
		
		Node left = parseAnd();
		
		while(true){
			Token token = getNextToken();
			
			if(token.isType(Operator.XOR)){
				left = new Xor(left, parseAnd());
			}else{
				ungetToken(token);
				return left;
			}
		}
	}
	/**
	 *論理和演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseOr() throws ScriptException{
		
		Node left = parseExclusiveOr();
		
		while(true){
			Token token = getNextToken();
			
			if(token.isType(Operator.OR)){
				left = new Or(left, parseExclusiveOr());
			}else{
				ungetToken(token);
				return left;
			}
		}
	}
	/**
	 *短絡論理積演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseShortCircuitAnd() throws ScriptException{
		
		Node left = parseOr();
		
		while(true){
			Token token = getNextToken();
			
			if(token.isType(Operator.SHORT_AND)){
				left = new ShortAnd(left, parseOr());
			}else{
				ungetToken(token);
				return left;
			}
		}
	}
	/**
	 *短絡論理和演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseShortCircuitOr() throws ScriptException{
		
		Node left = parseShortCircuitAnd();
		
		while(true){
			Token token = getNextToken();
			
			if(token.isType(Operator.SHORT_AND)){
				left = new ShortAnd(left, parseShortCircuitAnd());
			}else{
				ungetToken(token);
				return left;
			}
		}
	}
	/**
	 *三項演算子を主演算子とする式を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseTernary() throws ScriptException{
		
		Node node = parseShortCircuitOr();
		Token token = getNextToken();
			
		if(token.isType(Operator.TERNARY)){
			
			Node left  = parseTernary();
			checkNextToken(Operator.COLON);
			Node right = parseTernary();
			
			return new TernaryNode(node, left, right);
		}else{
			ungetToken(token);
			return node;
		}
	}
	/**
	 *式の全体を解析します。
	 *
	 *@return 式の解析木
	 */
	private Node parseExpression() throws ScriptException{
		return parseTernary();
	}
	/**
	 *条件分岐構文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseIf() throws ScriptException{
		
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		
		checkNextToken(Operator.OPEN_PARENS);
		Node cond = parseExpression();
		if(cond == null) cond = new LiteralNode(true);
		checkNextToken(Operator.CLOSE_PARENS);
		
		Statement tbody = parseBlock(), fbody = null;
		
		Token token = getNextToken();
		if(token.isType(Keyword.ELSE)){
			names.exitChildScope();
			names.enterChildScope(NameTable.BLOCK_SCOPE);
			fbody = parseBlock();
			
		}else ungetToken(token);
		
		names.exitChildScope();
		
		return new If(cond, tbody, fbody);
	}
	/**
	 *繰り返し構文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseFor() throws ScriptException{
		
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		checkNextToken(Operator.OPEN_PARENS);
		
		Node init = null, cond = null, next = null;
		Node exp  = parseExpression();
		
		Token token = checkNextToken(
			Operator.CLOSE_PARENS,
			Operator.SEMICOLON
		);
		
		if(token.isType(Operator.SEMICOLON)){
			init = exp;
			cond = parseExpression();
			checkNextToken(Operator.SEMICOLON);
			next = parseExpression();
			checkNextToken(Operator.CLOSE_PARENS);
		}else cond = exp;
		
		Statement body = parseBlock();
		names.exitChildScope();
		
		return new For(init, cond, next, body);
	}
	/**
	 *例外処理構文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseTry() throws ScriptException{
		
		Statement tbody = parseBlock();
		
		checkNextToken(Keyword.CATCH);
		checkNextToken(Operator.OPEN_PARENS);
		
		Token arg = checkNextToken(TokenType.IDENTIFIER);
		checkNextToken(Operator.CLOSE_PARENS);
		
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		Address addr = names.createLocal(arg);
		
		Statement cbody = parseBlock();
		names.exitChildScope();
		
		return new Try(tbody, new Catch(cbody));
	}
	/**
	 *標準出力文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseWrite() throws ScriptException{
		Node exp = parseExpression();
		checkNextToken(Operator.SEMICOLON);
		
		return new Write(exp);
	}
	/**
	 *改行付き標準出力文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseWriteln() throws ScriptException{
		Node exp = parseExpression();
		checkNextToken(Operator.SEMICOLON);
		
		return new Writeln(exp);
	}
	/**
	 *制御戻し文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseReturn() throws ScriptException{
		
		Token token = getNextToken();
		
		if(!token.isType(Operator.SEMICOLON)){
			ungetToken(token);
			Node exp = parseExpression();
			checkNextToken(Operator.SEMICOLON);
			
			return new Return(exp);
		}
		return new Return(new LiteralNode(NULL));
	}
	/**
	 *強制終了文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseExit() throws ScriptException{
		
		Token token = getNextToken();
		
		if(!token.isType(Operator.SEMICOLON)){
			ungetToken(token);
			Node exp = parseExpression();
			checkNextToken(Operator.SEMICOLON);
			
			return new Exit(exp);
		}
		return new Exit(new LiteralNode(NULL));
	}
	/**
	 *break文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseBreak() throws ScriptException{
		
		checkNextToken(Operator.SEMICOLON);
		return new Break();
	}
	/**
	 *continue文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseContinue() throws ScriptException{
		
		checkNextToken(Operator.SEMICOLON);
		return new Continue();
	}
	/**
	 *変数宣言文を解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseDeclarative() throws ScriptException{
		List<Statement> list = new ArrayList<Statement>();
		
		while(true){
			Token name = getNextToken();
			if(name.isIdentifier()){
				Token next = checkNextToken(
					Operator.ASSIGN,
					Operator.COMMA,
					Operator.SEMICOLON);
				
				names.createLocal(name);
				
				if(next.isType(Operator.ASSIGN)){
					ungetToken(next);
					ungetToken(name);
					
					Node assign = parseExpression();
					list.add(new Expression(assign));
					
					next = checkNextToken(
						Operator.COMMA,
						Operator.SEMICOLON);
				}
				if(next.isType(Operator.SEMICOLON)){
					return new ListNode(list);
				}
			}else throw error(name);
		}
	}
	/**
	 *ブロックを解析します。
	 *
	 *@return 文の解析木
	 */
	private Statement parseBlock() throws ScriptException{
		
		List<Statement> list = new ArrayList<Statement>();
		names.enterChildScope(NameTable.BLOCK_SCOPE);
		
		Token token = getNextToken();
		
		if(token.isType(Operator.SEMICOLON)) return null;
		
		else if(!token.isType(Operator.OPEN_BRACE)){
			ungetToken(token);
			return parseStatement();
		}
		while(true){
			token = getNextToken();
			if(token.isType(Operator.CLOSE_BRACE)) break;
			
			ungetToken(token);
			list.add(parseStatement());
		}
		names.exitChildScope();
		
		return new ListNode(list);
	}
	/**
	 *大域関数の定義文を解析します。
	 *
	 *@return 関数の解析木
	 */
	private Function parseFunction() throws ScriptException{
		
		Token name = getNextToken();
		if(!name.isIdentifier()){
			throw error("parseFunction_exception");
		}
		
		names.enterChildScope(NameTable.FUNCTION_SCOPE);
		int params = parseParameters();
		functions.define(name, params);
		
		Statement body = parseBlock();
		
		int locals = names.getLocalCount();
		Code ncode = new Code(name.toString());
		
		functions.endDefine();
		names.exitChildScope();
		
		return new Function(ncode, params, locals, body);
	}
	/**
	 *局所関数の定義文を解析します。
	 *
	 *@return 関数の解析木
	 */
	private Closure parseClosure() throws ScriptException{
		
		names.enterChildScope(NameTable.FUNCTION_SCOPE);
		int params = parseParameters();
		
		Statement body = parseBlock();
		
		int locals = names.getLocalCount();
		names.exitChildScope();
		
		return new Closure(params, locals, body);
	}
	/**
	 *引数宣言文を解析します。
	 *
	 *@return 仮引数の個数
	 */
	private int parseParameters() throws ScriptException{
		
		Token token = getNextToken();
		
		if(token.isType(Operator.OPEN_PARENS)){
			token = checkNextToken
			(TokenType.IDENTIFIER, Operator.CLOSE_PARENS);
			
			if(token.isType(Operator.CLOSE_PARENS)) return 0;
			else ungetToken(token);
			
			while(true){
				token = checkNextToken(TokenType.IDENTIFIER);
				names.createParam(token);
				
				token = checkNextToken
				(Operator.COMMA, Operator.CLOSE_PARENS);
				
				if(token.isType(Operator.CLOSE_PARENS)) break;
			}
		}else ungetToken(token);
		
		return names.getParamCount();
	}
	/**
	 *実引数式のリストを解析します。
	 *
	 *@return 実引数式のリスト
	 */
	private List<Node> parseArguments() throws ScriptException{
		
		List<Node> args = new ArrayList<Node>();
		
		checkNextToken(Operator.OPEN_PARENS);
		Token token = getNextToken();
		
		if(token.isType(Operator.CLOSE_PARENS)) return args;
		ungetToken(token);
		
		while(true){
			args.add(parseExpression());
			
			token = checkNextToken
			(Operator.COMMA, Operator.CLOSE_PARENS);
			
			if(!token.isType(Operator.COMMA)) return args;
		}
	}
	/**
	 *インポート文を解析します。
	 */
	private void parseImport() throws ScriptException{
		
		StringBuilder name = new StringBuilder(32);
		
		while(true){
			Token token = checkNextToken(
				TokenType.IDENTIFIER, Operator.MUL);
			if(token.isType(Operator.MUL)){
				checkNextToken(Operator.SEMICOLON);
				name.append('*');
				break;
			}else name.append(token);
			
			Token del = checkNextToken(
				Operator.DOT, Operator.SEMICOLON);
			if(del.isType(Operator.SEMICOLON)) break;
			else name.append('.');
		}
		classes.add(name.toString());
	}
}

