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

import java.util.ArrayList;
import javax.script.ScriptException;

/**
*AriCE言語の仮想マシンの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月29日
*/
public class AriceVirtualMachine{
	
	/**数値をスタックに積みます*/
	public static final int OP_PUSH  = 0;
	/**スタックの上2値を加算して削除後スタックに積みます*/
	public static final int OP_ADD   = 1;
	/**スタックの上2値を減算して削除後スタックに積みます*/
	public static final int OP_SUB   = 2;
	/**スタックの上2値を乗算して削除後スタックに積みます*/
	public static final int OP_MUL   = 3;
	/**スタックの上2値を除算して削除後スタックに積みます*/
	public static final int OP_DIV   = 4;
	/**スタックの上2値でMODを求め削除後スタックに積みます*/
	public static final int OP_MOD   = 5;
	/**スタックの上2値でAND演算して削除後スタックに積みます*/
	public static final int OP_AND   = 6;
	/**スタックの上2値でOR演算して削除後スタックに積みます*/
	public static final int OP_OR    = 7;
	/**スタックの上2値でXOR演算して削除後スタックに積みます*/
	public static final int OP_XOR   = 8;
	/**スタックの上1値の符号を反転します*/
	public static final int OP_MIN   = 9;
	/**スタックの上2値を==演算して削除後スタックに積みます*/
	public static final int OP_EQU   = 10;
	/**スタックの上2値を!=演算して削除後スタックに積みます*/
	public static final int OP_NEQ   = 11;
	/**スタックの上2値を> 演算して削除後スタックに積みます*/
	public static final int OP_GRET  = 12;
	/**スタックの上2値を>=演算して削除後スタックに積みます*/
	public static final int OP_GREQ  = 13;
	/**スタックの上2値を< 演算して削除後スタックに積みます*/
	public static final int OP_LESS  = 14;
	/**スタックの上2値を<=演算して削除後スタックに積みます*/
	public static final int OP_LSEQ  = 15;
	/**オペランドの指定する変数値をスタックに積み増す*/
	public static final int OP_VAR   = 16;
	/**スタックの上1値をオペランドの指定する変数に代入します*/
	public static final int OP_ASSN  = 17;
	/**オペランドの指定する場所にジャンプします*/
	public static final int OP_GOTO  = 18;
	/**スタックの上1値が0の場合オペランドの指定する場所に移動します*/
	public static final int OP_GOTO_ZERO = 19;
	/**プログラムカウンタの値をスタックに積んでオペランドの指定する場所にジャンプします*/
	public static final int OP_GOTO_SUB  = 20;
	/**スタックの指定する場所に移動します*/
	public static final int OP_RETURN    = 21;
	/**スタックの上1値の値を画面に出力します*/
	public static final int OP_PRINT     = 22;
	/**改行文字を画面に出力します*/
	public static final int OP_PRINTLN   = 23;
	/**仮想マシンの実行を停止します*/
	public static final int OP_EXIT      = 24;
	
	private final ArrayList<Code> stack;
	private final ArrayList<Code> vars;
	private final Code[] codes;
	
	private int words;
	
	/**
	*中間言語コードを指定して仮想マシンを生成します。
	*@param codes 中間言語コード
	*/
	public AriceVirtualMachine(Code[] codes){
		stack = new ArrayList<Code>(100);
		vars  = new ArrayList<Code>(10);
		this.codes = codes;
		words = (codes != null)? codes.length : 0;
	}
	/**
	*仮想マシンを実行します。
	*@throws ScriptException 構文エラーがあった場合
	*/
	public void execute() throws ScriptException{
		int pc = 0; //プログラムカウンタ
		int sp = 0; //スタックポインタ
		
		while(pc<words){
			switch(codes[pc].toInteger()){
			case OP_PUSH:
				stack.add(sp++,codes[pc+1]);
				pc += 2;
				break;
			case OP_ADD:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						   + stack.get(sp-1).toInteger()));
				sp--;
				pc++;
				break;
			case OP_SUB:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						   - stack.get(sp-1).toInteger()));
				sp--;
				pc++;
				break;
			case OP_MUL:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						   * stack.get(sp-1).toInteger()));
				sp--;
				pc++;
				break;
			case OP_DIV:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						   / stack.get(sp-1).toInteger()));
				sp--;
				pc++;
				break;
			case OP_MOD:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						   % stack.get(sp-1).toInteger()));
				sp--;
				pc++;
				break;
			case OP_AND:
				stack.set(sp-2,
					new Code(((stack.get(sp-2).toInteger()==1)
						&& (stack.get(sp-1).toInteger()==1))? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_OR:
				stack.set(sp-2,
					new Code(((stack.get(sp-2).toInteger()==1)
						|| (stack.get(sp-1).toInteger()==1))? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_XOR:
				stack.set(sp-2,
					new Code(((stack.get(sp-2).toInteger()==1)
						^ (stack.get(sp-1).toInteger()==1))? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_MIN:
				stack.set(sp-1,
					new Code(stack.get(sp-1).toInteger()*(-1)));
				pc++;
				break;
			case OP_EQU:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  == stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_NEQ:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  != stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_GRET:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  >  stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_GREQ:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  >= stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_LESS:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  <  stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_LSEQ:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  <= stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_VAR:
				stack.add(sp++,vars.get(codes[pc+1].toInteger()));
				pc += 2;
				break;
			case OP_ASSN:
				int index = codes[pc+1].toInteger();
				if(index >= vars.size())vars.add(stack.get(sp-1));
				else vars.set(codes[pc+1].toInteger(),stack.get(sp-1));
				sp--;
				pc += 2;
				break;
			case OP_GOTO:
				pc = codes[pc+1].toInteger();
				break;
			case OP_GOTO_ZERO:
				pc = (stack.get(sp-1).toInteger()==0)?
				codes[pc+1].toInteger() : pc + 2;
				sp--;
				break;
			case OP_GOTO_SUB:
				stack.add(sp++,new Code(pc+2));
				pc = codes[pc+1].toInteger();
				break;
			case OP_RETURN:
				pc = stack.get(sp-1).toInteger();
				sp--;
				break;
			case OP_PRINT:
				System.out.print(stack.get(sp-1));
				sp--;
				pc++;
				break;
			case OP_PRINTLN:
				System.out.println("");
				pc++;
				break;
			case OP_EXIT:
				return;
			}
		}
		
	}
}
