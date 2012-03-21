/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium;

import leaf.script.common.tree.Node;
import leaf.script.euphonium.tree.expression.*;
import leaf.script.euphonium.tree.statement.*;


/**
 * AriCE言語のトークンの種別と中間言語コードを定義します。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.2 作成：2011年2月19日
 */
class CodesAndTokens{
	/**
	*演算子の定義です。
	*/
	protected static enum Operator {
		ADD          ("+" ,  OP_ADD ),
		AND          ("&" ,  OP_AND ),
		ASSIGN       ("=" ,  true   ),
		ASSIGN_ADD   ("+=",  true, OP_ADD ),
		ASSIGN_AND   ("&=",  true, OP_AND ),
		ASSIGN_DIV   ("/=",  true, OP_DIV ),
		ASSIGN_LEFT  ("<<=", true, OP_BIT_LEFT ),
		ASSIGN_MOD   ("%=" , true, OP_MOD ), 
		ASSIGN_MUL   ("*=" , true, OP_MUL ),
		ASSIGN_OR    ("|=" , true, OP_OR  ),
		ASSIGN_POW   ("**=", true, OP_POW ),
		ASSIGN_RIGHT (">>=", true, OP_BIT_RIGHT),
		ASSIGN_SUB   ("-=" , true, OP_SUB ),
		ASSIGN_XOR   ("^=" , true, OP_XOR ),
		BIT_NOT      ("~"  , OP_NOT ),
		BIT_LEFT     ("<<" , OP_BIT_LEFT ),
		BIT_RIGHT    (">>" , OP_BIT_RIGHT),
		CLOSE_BRACE  ("}"),
		CLOSE_PARENS (")"),
		CLOSE_SQUARE ("]"),
		COLON        (":"),
		COMMA        (","),
		COMPARE      ("<=>", OP_COMP ),
		DECLARE      (":="),
		DECREMENT    ("--"),
		DIV          ("/"  , OP_DIV  ),
		DOT          ("."),
		EQUAL        ("==" , OP_EQU  ),
		GREATER      (">"  , OP_GRET ),
		GREATER_EQUAL(">=" , OP_GREQ ),
		INCREMENT    ("++"),
		IS           ("===", OP_IS   ),
		LESS         ("<"  , OP_LESS ),
		LESS_EQUAL   ("<=" , OP_LSEQ ),
		MOD          ("%"  , OP_MOD  ),
		MUL          ("*"  , OP_MUL  ),
		NOT          ("!"  , OP_NOT  ),
		NOT_EQUAL    ("!=" , OP_NEQ  ),
		OPEN_BRACE   ("{"),
		OPEN_PARENS  ("("),
		OPEN_SQUARE  ("["),
		OR           ("|"  , OP_OR   ),
		POW          ("**" , OP_POW  ),
		SEMICOLON    (";"),
		SHORT_AND    ("&&"),
		SHORT_OR     ("||"),
		SUB          ("-"  , OP_SUB  ),
		TERNARY      ("?"),
		XOR          ("^"  , OP_XOR  );
		
		private final String desc;
		private final byte   code;
		private final boolean isAssign;
		/**コンストラクタ*/
		private Operator(String desc){
			this(desc, false, (byte)-1);
		}
		private Operator(String desc, boolean b){
			this(desc, b, (byte)-1);
		}
		private Operator(String desc, byte code){
			this(desc, false, code);
		}
		private Operator(String desc,
			boolean isAssign, byte code){
			this.desc = desc;
			this.code = code;
			this.isAssign = isAssign;
		}
		/**文字列化*/
		public String toString(){
			return desc;
		}
		/**対応命令*/
		public byte toCode(){
			return code;
		}
		/**代入演算子か*/
		public boolean isAssign(){
			return isAssign;
		}
	}
	/**
	*キーワードの定義です。
	*/
	protected static enum Keyword {
		BREAK  (  "break"   ),
		CASE   (  "case"    ),
		CATCH  (  "catch"   ),
		CONTIN (  "continue"),
		DEFAULT(  "default" ),
		ELSE   (  "else"    ),
		EXIT   (  "exit"    ),
		FALSE  (  "false"   ),
		FOR    (  "for"     ),
		FUNCT  (  "function"),
		IF     (  "if"      ),
		IMPORT (  "import"  ),
		INSTOF ("instanceof"),
		NEW    (  "new"     ),
		NULL   (  "null"    ),
		RETURN (  "return"  ),
		SWITCH (  "switch"  ),
		TRUE   (  "true"    ),
		TRY    (  "try"     ),
		VAR    (  "var"     ),
		WRITE  (  "write"   ),
		WRITELN(  "writeln" );
		
		private String desc;
		/**コンストラクタ*/
		private Keyword(String desc){
			this.desc = desc;
		}
		/**文字列化*/
		public String toString(){
			return desc;
		}
	}
	/**
	*演算子以外のトークンの分類定義です。
	*/
	protected static enum TokenType {
		INTEGER, DOUBLE, CHARACTER, STRING, IDENTIFIER
	}
	/**
	 *指定された中間言語命令に対応する単項演算子ノードを生成します。
	 *
	 *@param code 中間言語命令
	 *@param child 子
	 *
	 *@return 生成された単項演算子ノード
	 */
	protected Node createNode(byte code, Node child){
		switch(code){
			case OP_ADD : return new Plus (child);
			case OP_SUB : return new Minus(child);
			case OP_NOT : return new Not  (child);
		}
		return null;
	}
	/**
	 *指定された中間言語命令に対応する二項演算子ノードを生成します。
	 *
	 *@param code 中間言語命令
	 *@param left 左の子
	 *@param right 右の子
	 *
	 *@return 生成された二項演算子ノード
	 */
	protected Node createNode(byte code, Node left, Node right){
		switch(code){
			case OP_BIT_LEFT  : return new LeftShift (left, right);
			case OP_BIT_RIGHT : return new RightShift(left, right);
			case OP_ADD : return new Add     (left, right);
			case OP_SUB : return new Subtract(left, right);
			case OP_MUL : return new Multiply(left, right);
			case OP_DIV : return new Divide  (left, right);
			case OP_MOD : return new Mod     (left, right);
			case OP_POW : return new Power   (left, right);
			case OP_AND : return new And     (left, right);
			case OP_OR  : return new Or      (left, right);
			case OP_XOR : return new Xor     (left, right);
			case OP_GRET: return new Greater (left, right);
			case OP_LESS: return new Less    (left, right);
			case OP_COMP: return new Compare (left, right);
			case OP_IS  : return new ShallowEqual(left, right);
			case OP_EQU : return new DeepEqual   (left, right);
			case OP_NEQ : return new NotEqual    (left, right);
			case OP_GREQ: return new GreaterEqual(left, right);
			case OP_LSEQ: return new LessEqual   (left, right);
		}
		return null;
	}
	
	//以下中間言語コードと説明
	protected static final byte OP_LIT_PUSH   = 0;  //定数をスタックに置く
	protected static final byte OP_BIT_LEFT   = 1;  // <<
	protected static final byte OP_BIT_RIGHT  = 2;  // >>
	protected static final byte OP_DUP        = 3;  //先頭値をコピー
	protected static final byte OP_DEL        = 4;  //先頭値を削除
	protected static final byte OP_ADD        = 5;  // +
	protected static final byte OP_SUB        = 6;  // -
	protected static final byte OP_MUL        = 7;  // *
	protected static final byte OP_DIV        = 8;  // /
	protected static final byte OP_MOD        = 9;  // %
	protected static final byte OP_POW        = 10; // **
	protected static final byte OP_NEG        = 11; // -
	protected static final byte OP_AND        = 12; // &
	protected static final byte OP_OR         = 13; // |
	protected static final byte OP_XOR        = 14; // ^
	protected static final byte OP_NOT        = 15; // ! ~
	protected static final byte OP_SHORT_AND  = 16; // &&
	protected static final byte OP_SHORT_OR   = 17; // ||
	protected static final byte OP_IS         = 18; // ===
	protected static final byte OP_EQU        = 19; // ==
	protected static final byte OP_NEQ        = 20; // !=
	protected static final byte OP_GRET       = 21; // >
	protected static final byte OP_GREQ       = 22; // >=
	protected static final byte OP_LESS       = 23; // <
	protected static final byte OP_LSEQ       = 24; // <=
	protected static final byte OP_COMP       = 25; // <=>
	protected static final byte OP_VAR_INCR   = 26; // 変数++
	protected static final byte OP_VAR_DECR   = 27; // 変数--
	protected static final byte OP_ARG_INCR   = 28; // 引数++
	protected static final byte OP_ARG_DECR   = 29; // 引数--
	protected static final byte OP_VAR_ASSN   = 30; // 変数代入
	protected static final byte OP_VAR_PUSH   = 31; // 変数参照
	protected static final byte OP_ARG_ASSN   = 32; // 引数代入
	protected static final byte OP_ARG_PUSH   = 33; // 引数参照
	protected static final byte OP_GOTO       = 34; // GOTO
	protected static final byte OP_GOTO_FALSE = 35; // 偽ならGOTO
	protected static final byte OP_FUNC_CALL  = 36; // 大域関数呼出
	protected static final byte OP_FUNC_FRAME = 37; // 大域関数開始
	protected static final byte OP_CLOS_CALL  = 38; // 局所関数呼出
	protected static final byte OP_CLOS_PUSH  = 39; // 局所関数参照
	protected static final byte OP_RETURN     = 40; // 関数リターン
	protected static final byte OP_TRY_PUSH   = 41; // try節設定
	protected static final byte OP_TRY_DEL    = 42; // try節脱出
	protected static final byte OP_PRINT      = 43; // 出力
	protected static final byte OP_PRINTLN    = 44; // 改行付出力
	protected static final byte OP_OBJ_TOINST = 45; // new
	protected static final byte OP_OBJ_METHOD = 46; // メソッド呼出
	protected static final byte OP_FIELD_ASSN = 47; // フィールド代入
	protected static final byte OP_FIELD_PUSH = 48; // フィールド参照
	protected static final byte OP_EXIT       = 49; // 緊急脱出
}