/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.arice;

import leaf.script.common.util.Code;

import javax.script.ScriptException;

/**
 *AriCE処理系のテスト用ディスアセンブラの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.2 作成：2011年2月19日
 */
final class AriceDisassembler extends AriceCodesAndTokens {
	
	private StringBuilder builder;
	private Code[] medcodes;
	private int index;
	
	/**
	*中間言語コードをディスアセンブルします。
	*@param medcodes 中間言語コード
	*@return ディスアセンブルされたコード
	*@throws ScriptException 中間言語にバグがある場合
	*/
	public String disassemble(Code[] medcodes) throws ScriptException{
		builder = new StringBuilder(65536);
		this.medcodes = medcodes;
		
		builder.append("=========DISASSEMBLE=========\n");
		
		for(index=0; index<medcodes.length;index++){
			switch(medcodes[index].toInt()){
			case OP_LIT_PUSH   : append("LIT_PUSH",  1);break;
			case OP_BIT_LEFT   : append("BIT_LEFT",  0);break;
			case OP_BIT_RIGHT  : append("BIT_RIGHT", 0);break;
			case OP_DUP        : append("DUP",       0);break;
			case OP_DEL        : append("DEL",       0);break;
			case OP_ADD        : append("ADD",       0);break;
			case OP_SUB        : append("SUB",       0);break;
			case OP_MUL        : append("MUL",       0);break;
			case OP_DIV        : append("DIV",       0);break;
			case OP_MOD        : append("MOD",       0);break;
			case OP_POW        : append("POW",       0);break;
			case OP_NEG        : append("NEG",       0);break;
			case OP_AND        : append("AND",       0);break;
			case OP_OR         : append("OR",        0);break;
			case OP_XOR        : append("XOR",       0);break;
			case OP_NOT        : append("NOT",       0);break;
			case OP_SHORT_AND  : append("SHORT_AND", 1);break;
			case OP_SHORT_OR   : append("SHORT_OR",  1);break;
			case OP_IS         : append("IS",        0);break;
			case OP_EQU        : append("EQU",       0);break;
			case OP_NEQ        : append("NEQ",       0);break;
			case OP_GRET       : append("GRET",      0);break;
			case OP_GREQ       : append("GREQ",      0);break;
			case OP_LESS       : append("LESS",      0);break;
			case OP_LSEQ       : append("LSEQ",      0);break;
			case OP_COMP       : append("COMP",      0);break;
			case OP_VAR_INCR   : append("VAR_INCR",  2);break;
			case OP_VAR_DECR   : append("VAR_DECR",  2);break;
			case OP_ARG_INCR   : append("ARG_INCR",  1);break;
			case OP_ARG_DECR   : append("ARG_DECR",  1);break;
			case OP_VAR_ASSN   : append("VAR_ASSN",  2);break;
			case OP_VAR_PUSH   : append("VAR_PUSH",  2);break;
			case OP_ARG_ASSN   : append("ARG_ASSN",  1);break;
			case OP_ARG_PUSH   : append("ARG_PUSH",  1);break;
			case OP_GOTO       : append("GOTO",      1);break;
			case OP_GOTO_FALSE : append("GOTO_FALSE",1);break;
			case OP_FUNC_CALL  : append("FUNC_CALL", 2);break;
			case OP_FUNC_FRAME : append("FUNC_FRAME",2);break;
			case OP_CLOS_CALL  : append("CLOS_CALL", 1);break;
			case OP_CLOS_PUSH  : append("CLOS_PUSH", 2);break;
			case OP_RETURN     : append("RETURN",    0);break;
			case OP_TRY_PUSH   : append("TRY_PUSH",  1);break;
			case OP_TRY_DEL    : append("TRY_DEL",   0);break;
			case OP_PRINT      : append("PRINT",     0);break;
			case OP_PRINTLN    : append("PRINTLN",   0);break;
			case OP_OBJ_TOINST : append("OBJ_TOINST",2);break;
			case OP_OBJ_METHOD : append("OBJ_METHOD",2);break;
			case OP_FIELD_ASSN : append("FIELD_ASSN",1);break;
			case OP_FIELD_PUSH : append("FIELD_PUSH",1);break;
			case OP_EXIT       : append("EXIT",      0);break;
			}
		}
		builder.append("=========DISASSEMBLE=========\n");
		String result = new String(builder);
		builder.delete(0, builder.length());
		this.medcodes = null;
		return result;
	}
	/**
	*カウンタ、命令文字列、オペランドの個数を指定してバッファに追加します。
	*@param opecode  命令文字列
	*@param operands オペランドの個数
	*/
	private void append(String opecode, int operands){
		builder.append(String.format("%4d       ",  index));
		builder.append(String.format("%-12s     ", opecode));
		for(int i=1;i<=operands;i++){
			builder.append(String.format("%-8s  ", medcodes[index+i]));
		}
		index += operands;
		builder.append("\n");
	}
}