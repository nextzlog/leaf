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

/**
*AriCE言語のトークンの種別と中間言語コードを定義します。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年2月19日
*/
class AriceCodesAndTokens{
	/**
	*演算子の定義です。
	*/
	protected static enum Operators {
		ADD          ("+" ,  OP_ADD ),
		AND          ("&" ,  OP_AND ),
		ASSIGN       ("="),
		ASSIGN_ADD   ("+=",  OP_ADD ),
		ASSIGN_AND   ("&=",  OP_AND ),
		ASSIGN_DIV   ("/=",  OP_DIV ),
		ASSIGN_LEFT  ("<<=", OP_BIT_LEFT ),
		ASSIGN_MOD   ("%=" , OP_MOD ), 
		ASSIGN_MUL   ("*=" , OP_MUL ),
		ASSIGN_OR    ("|=" , OP_OR  ),
		ASSIGN_POW   ("**=", OP_POW ),
		ASSIGN_RIGHT (">>=", OP_BIT_RIGHT),
		ASSIGN_SUB   ("-=" , OP_SUB ),
		ASSIGN_XOR   ("^=" , OP_XOR ),
		BIT_NOT      ("~"  , OP_NOT ),
		BIT_LEFT     ("<<" , OP_BIT_LEFT ),
		BIT_RIGHT    (">>" , OP_BIT_RIGHT),
		CLOSE_BRACE  ("}"),
		CLOSE_PARENS (")"),
		COLON        (":"),
		COMMA        (","),
		DECLARE      (":="),
		DECREMENT    ("--"),
		DIV          ("/"  , OP_DIV  ),
		DOT          ("."),
		EQUAL        ("==" , OP_EQU  ),
		GREATER      (">"  , OP_GRET ),
		GREATER_EQUAL(">=" , OP_GREQ ),
		INCREMENT    ("++"),
		LESS         ("<"  , OP_LESS ),
		LESS_EQUAL   ("<=" , OP_LSEQ ),
		MOD          ("%"  , OP_MOD  ),
		MUL          ("*"  , OP_MUL  ),
		NOT          ("!"  , OP_NOT  ),
		NOT_EQUAL    ("!=" , OP_NEQ  ),
		OPEN_BRACE   ("{"),
		OPEN_PARENS  ("("),
		OR           ("|"  , OP_OR   ),
		POW          ("**" , OP_POW  ),
		SEMICOLON    (";"),
		SHORT_AND    ("&&"),
		SHORT_OR     ("||"),
		SUB          ("-"  , OP_SUB  ),
		TERNARY      ("?"),
		XOR          ("^"  , OP_XOR  ),
		OPEN_SQUARE  ("["),
		CLOSE_SQUARE ("]");
		
		private final String desc;
		private final byte   code;
		/**コンストラクタ*/
		private Operators(String desc){
			this.desc = desc;
			this.code = -1;
		}
		private Operators(String desc, byte code){
			this.desc = desc;
			this.code = code;
		}
		/**文字列化*/
		public String toString(){
			return desc;
		}
		/**対応命令*/
		public byte toCode(){
			return code;
		}
	}
	/**
	*キーワードの定義です。
	*/
	protected static enum Keywords {
		BREAK  (  "break"   ),
		CASE   (  "case"    ),
		DEFAULT(  "default" ),
		ELSE   (  "else"    ),
		EXIT   (  "exit"    ),
		FALSE  (  "false"   ),
		FOR    (  "for"     ),
		FUNCT  (  "function"),
		GET    (  "get"     ),
		IF     (  "if"      ),
		IMPORT (  "import"  ),
		INSTOF ("instanceof"),
		NEW    (  "new"     ),
		NULL   (  "null"    ),
		SET    (  "set"     ),
		SWITCH (  "switch"  ),
		RETURN (  "return"  ),
		TRUE   (  "true"    ),
		VAR    (  "var"     ),
		WRITE  (  "write"   ),
		WRITELN(  "writeln" );
		
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
	*その他のトークンの分類定義です。
	*/
	protected static enum Tokens {
		INTEGER, DOUBLE, CHARACTER, STRING, IDENTIFIER
	}
	//以下中間言語コード
	protected static final byte OP_LIT_PUSH   = 0;
	protected static final byte OP_BIT_LEFT   = 1;
	protected static final byte OP_BIT_RIGHT  = 2;
	protected static final byte OP_DUP        = 3;
	protected static final byte OP_DEL        = 4;
	protected static final byte OP_ADD        = 5;
	protected static final byte OP_SUB        = 6;
	protected static final byte OP_MUL        = 7;
	protected static final byte OP_DIV        = 8;
	protected static final byte OP_MOD        = 9;
	protected static final byte OP_POW        = 10;
	protected static final byte OP_AND        = 11;
	protected static final byte OP_OR         = 12;
	protected static final byte OP_XOR        = 13;
	protected static final byte OP_NOT        = 14;
	protected static final byte OP_SHORT_AND  = 15;
	protected static final byte OP_SHORT_OR   = 16;
	protected static final byte OP_REV        = 17;
	protected static final byte OP_EQU        = 18;
	protected static final byte OP_NEQ        = 19;
	protected static final byte OP_GRET       = 20;
	protected static final byte OP_GREQ       = 21;
	protected static final byte OP_LESS       = 22;
	protected static final byte OP_LSEQ       = 23;
	protected static final byte OP_VAR_INCR   = 24;
	protected static final byte OP_VAR_DECR   = 25;
	protected static final byte OP_ARG_INCR   = 26;
	protected static final byte OP_ARG_DECR   = 27;
	protected static final byte OP_VAR_ASSN   = 28;
	protected static final byte OP_VAR_PUSH   = 29;
	protected static final byte OP_ARG_ASSN   = 30;
	protected static final byte OP_ARG_PUSH   = 31;
	protected static final byte OP_TO_LIST    = 32;
	protected static final byte OP_GOTO       = 33;
	protected static final byte OP_GOTO_FALSE = 34;
	protected static final byte OP_CALL       = 35;
	protected static final byte OP_FRAME      = 36;
	protected static final byte OP_RETURN     = 37;
	protected static final byte OP_CALL_DEL   = 38;
	protected static final byte OP_PRINT      = 39;
	protected static final byte OP_PRINTLN    = 40;
	protected static final byte OP_EXIT       = 41;
	protected static final byte OP_BIND_SET   = 42;
	protected static final byte OP_BIND_GET   = 43;
	protected static final byte OP_INSTANCE   = 44;
	protected static final byte OP_METHOD     = 45;
	protected static final byte OP_FIELD_ASSN = 46;
	protected static final byte OP_FIELD_PUSH = 47;
	protected static final byte OP_CLASS_CAST = 48;
}