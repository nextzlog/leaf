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

/**仮想マシン*/
class VirtualMachine{

	private final ArrayList<Code> stack;
	private final ArrayList<Code> vars;
	
	private final ArrayList<Code> codes;
	
	private int words;

	/**オペコードの定義*/
	public static final int OP_PUSH_INT     = 0; //整数値をスタックに積む
	public static final int OP_PUSH_STRING  = 1; //文字列をスタックに積む
	public static final int OP_CALC_ADD     = 2; //スタックの上2値を加算して削除後スタックに積む
	public static final int OP_CALC_SUB     = 3; //スタックの上2値を減算して削除後スタックに積む
	public static final int OP_CALC_MUL     = 4; //スタックの上2値を乗算して削除後スタックに積む
	public static final int OP_CALC_DIV     = 5; //スタックの上2値を除算して削除後スタックに積む
	public static final int OP_CALC_MOD     = 6; //スタックの上2値でMODを求め削除後スタックに積む
	public static final int OP_CALC_MINUS   = 7; //スタックの上1値を反転する
	public static final int OP_COMP_EQU     = 8; //スタックの上2値を==比較演算後真偽値をスタックに積む
	public static final int OP_COMP_NEQ     = 9; //スタックの上2値を!=比較演算後真偽値をスタックに積む
	public static final int OP_COMP_GRET    =10; //スタックの上2値を> 比較演算後真偽値をスタックに積む
	public static final int OP_COMP_GREQ    =11; //スタックの上2値を>=比較演算後真偽値をスタックに積む
	public static final int OP_COMP_LESS    =12; //スタックの上2値を< 比較演算後真偽値をスタックに積む
	public static final int OP_COMP_LEEQ    =13; //スタックの上2値を<=比較演算後真偽値をスタックに積む
	public static final int OP_PUSH_VAR     =14; //オペランドの指定する変数値をスタックに積む
	public static final int OP_VARI_ASSIN   =15; //スタックの上1値をオペランドの指定する変数に代入
	public static final int OP_GOTO         =16; //オペランドの指定地にジャンプ
	public static final int OP_GOTO_IF_ZERO =17; //スタックの上1値が0の場合オペランドの指定地にジャンプ
	public static final int OP_GOTO_SUB     =18; //プログラムカウンタの値をスタックに積んでオペランドの指定地にジャンプ
	public static final int OP_RETURN       =19; //プログラムカウンタの値をスタックから取り出してその指定地にジャンプ
	public static final int OP_PRINT        =20; //スタックの上1値の値を画面に出力
	public static final int OP_EXIT         =21; //仮想マシンの実行を停止
	
	/**コンストラクタ*/
	public VirtualMachine(ArrayList<Code> codes){
		stack = new ArrayList<Code>(1024);
		vars  = new ArrayList<Code>(64);
		this.codes = codes;
		words = (codes != null)? codes.size() : 0;
	}
	
	/**実行*/
	public void execute(){
		int pc = 0;//プログラムカウンタ
		int sp = 0;//スタックポインタ
		
		while(pc<words){
			switch(codes.get(pc).toInteger()){
			case OP_PUSH_INT:
				stack.add(sp++,codes.get(pc+1));
				pc += 2;
				break;
			case OP_PUSH_STRING:
				stack.add(sp++,codes.get(pc+1));
				pc += 2;
				break;
			case OP_CALC_ADD:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						   + stack.get(sp-1).toInteger()));
				sp--;
				pc++;
				break;
			case OP_CALC_SUB:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						   - stack.get(sp-1).toInteger()));
				sp--;
				pc++;
				break;
			case OP_CALC_MUL:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						   * stack.get(sp-1).toInteger()));
				sp--;
				pc++;
				break;
			case OP_CALC_DIV:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						   / stack.get(sp-1).toInteger()));
				sp--;
				pc++;
				break;
			case OP_CALC_MOD:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						   % stack.get(sp-1).toInteger()));
				sp--;
				pc++;
				break;
			case OP_CALC_MINUS:
				stack.set(sp-1,
					new Code(stack.get(sp-1).toInteger()*(-1)));
				pc++;
				break;
			case OP_COMP_EQU:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  == stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_COMP_NEQ:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  != stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_COMP_GRET:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  >  stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_COMP_GREQ:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  >= stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_COMP_LESS:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  <  stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_COMP_LEEQ:
				stack.set(sp-2,
					new Code(stack.get(sp-2).toInteger()
						  <= stack.get(sp-1).toInteger()? 1 : 0));
				sp--;
				pc++;
				break;
			case OP_PUSH_VAR:
				stack.add(sp++,vars.get(codes.get(pc+1).toInteger()));
				pc += 2;
				break;
			case OP_VARI_ASSIN:
				int index = codes.get(pc+1).toInteger();
				if(index >= vars.size())vars.add(stack.get(sp-1));
				else vars.set(codes.get(pc+1).toInteger(),stack.get(sp-1));
				sp--;
				pc += 2;
				break;
			case OP_GOTO:
				pc = codes.get(pc+1).toInteger();
				break;
			case OP_GOTO_IF_ZERO:
				pc = (stack.get(sp-1).toInteger()==0)?
				codes.get(pc+1).toInteger() : pc + 2;
				sp--;
				break;
			case OP_GOTO_SUB:
				stack.add(sp++,new Code(pc+2));
				pc = codes.get(pc+1).toInteger();
				break;
			case OP_RETURN:
				pc = stack.get(sp-1).toInteger();
				sp--;
				break;
			case OP_PRINT:
				System.out.println(stack.get(sp-1));
				sp--;
				pc++;
				break;
			case OP_EXIT:
				return;
			}
		}
	}
}