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
import java.util.ArrayList;

/**再帰下降型構文解析部*/
class Parser{
	
	private final ArrayList<Code>   bytecodes;
	private final ArrayList<String> varnames;
	private final ArrayList<Label>  labels;
	
	/**演算子の定義*/
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
	/**制御子の定義*/
	public static enum Keywords {
		IF  ("if"),
		ELSE("else"),
		WHILE("while"),
		GOTO("goto"),
		GOSUB("gosub"),
		RETURN("return"),
		PRINT("print");
		
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
	/**トークンの定義*/
	public static enum OtherTokens {
		INT,IDENTIFIER,STRING
	}
	
	private Token next_token = null;
	private final Analyzer analyzer;
	
	/**コンストラクタ*/
	public Parser(String script){
		analyzer    = new Analyzer(script);
		bytecodes   = new ArrayList<Code>(16384);
		varnames    = new ArrayList<String>(16);
		labels      = new ArrayList<Label> (16);
	}
	
	/**トークンを取得*/
	private Token getNextToken(){
		if(next_token!=null){
			Token ret = next_token;
			next_token= null;
			return ret;
		}else{
			return analyzer.getNext();
		}
	}
	
	/**トークンを押し戻す*/
	private void ungetToken(Token token){
		next_token = token;
	}
	
	/**バイトコード生成追加*/
	private void addCode(Code code){
		bytecodes.add(code);
	}
	
	/**エラー通知*/
	private void error(String msg){
		System.out.println("Parse Error: "
			+ msg + " at line." + analyzer.getLineNumber());
	}
	
	/**次のトークンを確認*/
	private boolean checkNext(Enum expected){
		Token next = getNextToken();
		if(next != null && next.getType() == expected){
			return true;
		}else{
			error("Token \"" + expected + 
				"\" is expected here instead of \"" + next + "\"");
			return false;
		}
	}
	
	/**変数を検索する*/
	private int searchVariable(String name){
		return varnames.indexOf(name);
	}
	
	/**ラベルを追加*/
	private void addLabel(Label label){
		label.setAddress(bytecodes.size());
		labels.add(label);
	}
	
	/**ラベルを検索し、ない場合追加*/
	private Label getLabel(String name){
		for(Label label : labels){
			if(label.name().equals(name))return label;
		}
		return new Label(name);
	}
	
	/**解析開始*/
	public ArrayList<Code> parse(){
		
		Token token;
		
		while((token = getNextToken()) != null){
			ungetToken(token);
			if(!parseStatement()) return null;
		}
		fixLabels();
		return bytecodes;
	}
	
	/**構文解析(お品書き)*/
	private boolean parseStatement(){
		
		Token token = getNextToken();
		if(token == null) return true;
		
		if(token.getType() == Keywords.IF){
			return parseIf();
		}else if(token.getType() == Keywords.WHILE){
			return parseWhile();
		}else if(token.getType() == Keywords.PRINT){
			return parsePrint();
		}else if(token.getType() == Keywords.GOTO){
			return parseGoto();
		}else if(token.getType() == Keywords.GOSUB){
			return parseGosub();
		}else if(token.getType() == Keywords.RETURN){
			return parseReturn();
		}else if(token.getType() == Operators.MUL){//特殊記号*
			return parseLabel();
		}else if(token.getType() == OtherTokens.IDENTIFIER){
			return parseAssign(token.toString());
		}else{
			error(" Bad statement here : \"" + token + "\"");
			return false;
		}
	}
	
	/**基本式解析*/
	private boolean parsePrimary(){
		
		Token token = getNextToken();
		if(token == null) return true;
		
		if(token.getType() == OtherTokens.INT){
			addCode(new Code(
				VirtualMachine.OP_PUSH_INT));
			addCode(new Code(
				token.toInteger()));
		}else if(token.getType() == OtherTokens.STRING){
			addCode(new Code(
				VirtualMachine.OP_PUSH_STRING));
			addCode(new Code(
				token.toString()));
		}else if(token.getType() == Operators.OPEN_PARENS){
			if(!parseExpression()) return false;
			if(!checkNext(Operators.CLOSE_PARENS)) return false;
		}else if(token.getType() == OtherTokens.IDENTIFIER){
			addCode(new Code(
				VirtualMachine.OP_PUSH_VAR));
			int index = searchVariable(token.toString());
			if(index < 0){
				error("The Variable \"" +
					token + "\" is not defined");
				return false;
			}
			addCode(new Code(index));
		}
		return true;
	}
	/**負の数のある式を解析*/
	private boolean parseUnary(){
		
		Token token = getNextToken();
		if(token == null) return true;
		
		if(token.getType()==Operators.SUB){
			if(!parsePrimary()) return false;
			addCode(new Code(
				VirtualMachine.OP_CALC_MINUS));
		}else{
			ungetToken(token);
			return parsePrimary();
		}
		return true;
	}
	
	/** * または / からなる式を解析*/
	private boolean parseMultiplicative(){
		
		if(!parseUnary()) return false;
		
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return true;
			
			if(token.getType() != Operators.MUL
			&& token.getType() != Operators.DIV){
				ungetToken(token);
				break;
			}
			if(!parseUnary()) return false;
			
			if(token.getType() == Operators.MUL){
				addCode(new Code(
					VirtualMachine.OP_CALC_MUL));
			}else{
				addCode(new Code(
					VirtualMachine.OP_CALC_DIV));
			}
		}
		return true;
	}
	
	/** + または - からなる式を解析*/
	private boolean parseAdditive(){
		
		if(!parseMultiplicative()) return false;
		
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return true;
			
			if(token.getType() != Operators.ADD
			&& token.getType() != Operators.SUB){
				ungetToken(token);
				break;
			}
			if(!parseMultiplicative()) return false;
			
			if(token.getType() == Operators.ADD){
				addCode(new Code(
					VirtualMachine.OP_CALC_ADD));
			}else{
				addCode(new Code(
					VirtualMachine.OP_CALC_SUB));
			}
		}
		return true;
	}
	
	/**比較演算子からなる式を解析*/
	private boolean parseComparative(){
		
		if(!parseAdditive()) return false;
		
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return true;
			
			if(token.getType() != Operators.EQUAL
			&& token.getType() != Operators.NOT_EQUAL
			&& token.getType() != Operators.GREATER
			&& token.getType() != Operators.GREATER_EQUAL
			&& token.getType() != Operators.LESS
			&& token.getType() != Operators.LESS_EQUAL){
				ungetToken(token);
				break;
			}
			if(!parseAdditive()) return false;
			
			if(token.getType() == Operators.EQUAL){
				addCode(new Code(
					VirtualMachine.OP_COMP_EQU));
			}else if(token.getType() == Operators.NOT_EQUAL){
				addCode(new Code(
					VirtualMachine.OP_COMP_NEQ));
			}else if(token.getType() == Operators.GREATER){
				addCode(new Code(
					VirtualMachine.OP_COMP_GRET));
			}else if(token.getType() == Operators.GREATER_EQUAL){
				addCode(new Code(
					VirtualMachine.OP_COMP_GREQ));
			}else if(token.getType() == Operators.LESS){
				addCode(new Code(
					VirtualMachine.OP_COMP_LESS));
			}else if(token.getType() == Operators.LESS_EQUAL){
				addCode(new Code(
					VirtualMachine.OP_COMP_LEEQ));
			}
		}
		return true;
	}
	/**式解析*/
	private boolean parseExpression(){
		return parseComparative();
	}
	
	/**if文解析*/
	private boolean parseIf(){
		
		if(!checkNext(Operators.OPEN_PARENS)) return false;
		if(!parseExpression()) return false;
		if(!checkNext(Operators.CLOSE_PARENS))return false;
		
		Label else_label = new Label("else");
		addCode(new Code(
			VirtualMachine.OP_GOTO_IF_ZERO));
		addCode(new Code(labels.size()));
		
		if(!parseBlock()) return false;
		
		Token token = getNextToken();
		if(token == null) return true;
		
		if(token.getType() == Keywords.ELSE){
			Label end_if_label = new Label("end");
			addCode(new Code(
				VirtualMachine.OP_GOTO));
			addCode(new Code(labels.size()));
			addLabel(else_label);
			
			if(!parseBlock()) return false;
			
			addLabel(end_if_label);
		}else{
			ungetToken(token);
			addLabel(else_label);
		}
		return true;
	}
	
	/**while文解析*/
	private boolean parseWhile(){
		
		if(!checkNext(Operators.OPEN_PARENS)) return false;
		if(!parseExpression()) return false;
		if(!checkNext(Operators.CLOSE_PARENS))return false;
		
		Label loop_label = new Label("loop");
		addLabel(loop_label);
		int index = labels.size()-1;
		
		addCode(new Code(
			VirtualMachine.OP_GOTO_IF_ZERO));
		addCode(new Code(labels.size()));
		
		if(!parseBlock()) return false;
		
		Label loop_end_label = new Label("end");
		addLabel(loop_end_label);
		
		addCode(new Code(
			VirtualMachine.OP_GOTO));
		addCode(new Code(index));
		
		return true;
	}
	
	/**print文解析*/
	private boolean parsePrint(){
		
		if(!checkNext(Operators.OPEN_PARENS)) return false;
		if(!parseExpression()) return false;
		if(!checkNext(Operators.CLOSE_PARENS))return false;
		
		addCode(new Code(
			VirtualMachine.OP_PRINT));
		return checkNext(Operators.SEMICOLON);
	}
	
	/**代入文解析*/
	private boolean parseAssign(String name){
		
		int index = searchVariable(name);
		
		if(!checkNext(Operators.ASSIGN)) return false;
		if(!parseExpression()) return false;
		
		addCode(new Code(
			VirtualMachine.OP_VARI_ASSIN));
		if(index < 0){
			addCode(new Code(varnames.size()));
		}else{
			addCode(new Code(index));
		}
		
		if(index < 0) varnames.add(name);
		
		return checkNext(Operators.SEMICOLON);
	}
	
	/**goto文解析*/
	private boolean parseGoto(){
		
		if(!checkNext(Operators.MUL)) return false;
		Token token = getNextToken();
		if(token == null) return true;
		
		if(token.getType() != OtherTokens.IDENTIFIER)
			error("A label is expected here");
		
		Label label = getLabel(token.toString());
		
		addCode(new Code(
			VirtualMachine.OP_GOTO));
		addCode(new Code(labels.size()));
		addLabel(label);
		
		return checkNext(Operators.SEMICOLON);
	}
	
	/**gosub(サブルーチン)文解析*/
	private boolean parseGosub(){
		
		if(!checkNext(Operators.MUL)) return false;
		Token token = getNextToken();
		if(token == null) return true;
		
		if(token.getType() != OtherTokens.IDENTIFIER){
			error("A label is expected here");
			return false;
		}
		
		Label label = getLabel(token.toString());
		
		addCode(new Code(
			VirtualMachine.OP_GOTO_SUB));
		addCode(new Code(labels.size()));
		addLabel(label);
		
		return checkNext(Operators.SEMICOLON);
	}
	
	/**ラベル文解析*/
	private boolean parseLabel(){
		
		Token token = getNextToken();
		if(token == null) return true;
		
		if(token.getType() != OtherTokens.IDENTIFIER){
			error("A label is expected here");
			return false;
		}
		
		Label label = getLabel(token.toString());
		addLabel(label);
		
		return true;
	}
	
	/**return文解析*/
	private boolean parseReturn(){
		addCode(new Code(
			VirtualMachine.OP_RETURN));
		return checkNext(Operators.SEMICOLON);
	}
	
	/**ブロック解析*/
	private boolean parseBlock(){
		
		if(!checkNext(Operators.OPEN_BRACE))return false;
		Token token;
		
		while(true){
			token = getNextToken();
			if(token == null) return true;
			
			if(token.getType() == Operators.CLOSE_BRACE)
				break;
			ungetToken(token);
			if(!parseStatement()) return false;
		}
		return true;
	}
	/**コードにラベルの実アドレスを書き込み*/
	private void fixLabels(){
		int length = bytecodes.size();
		for(int i=0; i<length; i++){
			int code = bytecodes.get(i).toInteger();
			//2ワード命令
			if(code == VirtualMachine.OP_PUSH_INT
			|| code == VirtualMachine.OP_PUSH_STRING
			|| code == VirtualMachine.OP_PUSH_VAR
			|| code == VirtualMachine.OP_VARI_ASSIN)
			{
				i++;
			}//うちラベルを使用する命令
			else if(code == VirtualMachine.OP_GOTO
				||  code == VirtualMachine.OP_GOTO_IF_ZERO
				||  code == VirtualMachine.OP_GOTO_SUB)
			{
				bytecodes.set(i+1, new Code(labels.get
					(bytecodes.get(i+1).toInteger()).address()));
				i++;
			}
		}
	}
}