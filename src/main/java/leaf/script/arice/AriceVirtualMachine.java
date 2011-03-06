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
import javax.script.Bindings;
import javax.script.ScriptException;

/**
*AriCE言語の仮想マシンの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月29日
*/
final class AriceVirtualMachine extends AriceCodesAndTokens{
	
	private Code[] codes, stack;
	
	private final int STACK_SIZE = AriceScriptEngine.STACK_SIZE;
	private final int STEP_MAX   = AriceScriptEngine.STEP_MAX; 
	
	private int words = 0, count = 0;
	private int pc, sp, fp, regist;
	private Code x, y;  //スタックの上２値(演算対象値)
	
	private final AriceCalcUnit unit;
	
	/**
	*仮想マシンを生成します。
	*/
	public AriceVirtualMachine(){
		stack = new Code[STACK_SIZE];
		unit  = new AriceCalcUnit();
	}
	/**
	*プログラムを実行します。
	*@param codes  中間言語コード
	*@param bind バインディング
	*@return 実行結果の値
	*@throws ScriptException 実行エラーがあった場合
	*/
	public Object execute(Code[] codes, Bindings bind) throws ScriptException{
		words = (this.codes = codes).length;
		pc = sp = fp = 0;
		while(pc < words && count < STEP_MAX){
			count ++;
			switch(codes[pc].toInteger()){
			case OP_LIT_PUSH:
				push(codes[pc+1]);
				pc += 2;
				break;
			case OP_BIT_LEFT:
				x = pop();
				y = pop();
				push(unit.bitleft(y,x));
				pc++;
				break;
			case OP_BIT_RIGHT:
				x = pop();
				y = pop();
				push(unit.bitright(y,x));
				pc++;
				break;
			case OP_DUP:
				push(get(sp-1));
				pc++;
				break;
			case OP_DEL:
				delete(operand());
				pc+=2;
				break;
			case OP_ADD:
				x = pop();
				y = pop();
				push(unit.add(y.getValue(), x.getValue()));
				pc++;
				break;
			case OP_SUB:
				x = pop();
				y = pop();
				push(unit.sub(y.getValue(), x.getValue()));
				pc++;
				break;
			case OP_MUL:
				x = pop();
				y = pop();
				push(unit.mul(y.getValue(), x.getValue()));
				pc++;
				break;
			case OP_DIV:
				x = pop();
				y = pop();
				push(unit.div(y.getValue(), x.getValue()));
				pc++;
				break;
			case OP_MOD:
				x = pop();
				y = pop();
				push(unit.mod(y.getValue(), x.getValue()));
				pc++;
				break;
			case OP_POW:
				x = pop();
				y = pop();
				push(unit.pow(y.getValue(), x.getValue()));
				pc++;
				break;
			case OP_AND:
				x = pop();
				y = pop();
				push(unit.and(y.getValue(), x.getValue()));
				pc++;
				break;
			case OP_OR:
				x = pop();
				y = pop();
				push(unit.or(y.getValue(), x.getValue()));
				pc++;
				break;
			case OP_XOR:
				x = pop();
				y = pop();
				push(unit.xor(y.getValue(), x.getValue()));
				pc++;
				break;
			case OP_NOT:
				push(unit.not(pop().getValue()));
				pc++;
				break;
			case OP_SHORT_AND:
				if(pop().isTrue()){
					pc += 2;
					push(true);
				}else{
					pc = operand();
					push(false);
				}
				break;
			case OP_SHORT_OR:
				if(pop().isTrue()){
					pc = operand();
					push(true);
				}else{
					pc += 2;
					push(false);
				}
				break;
			case OP_REV:
				push(unit.mul(pop().getValue(), -1));
				pc++;
				break;
			case OP_EQU:
				x = pop();
				y = pop();
				push(unit.equals(y, x));
				pc++;
				break;
			case OP_NEQ:
				x = pop();
				y = pop();
				push(unit.differs(y, x));
				pc++;
				break;
			case OP_GRET:
				x = pop();
				y = pop();
				push(unit.isBigger(y, x));
				pc++;
				break;
			case OP_GREQ:
				x = pop();
				y = pop();
				push(unit.isBiggerOrEqual(y, x));
				pc++;
				break;
			case OP_LESS:
				x = pop();
				y = pop();
				push(unit.isLesser(y, x));
				pc++;
				break;
			case OP_LSEQ:
				x = pop();
				y = pop();
				push(unit.isLesserOrEqual(y, x));
				pc++;
				break;
			case OP_VAR_INCR:
				x = get(fp + operand());
				set(fp + operand(), unit.increment(x));
				pc += 2;
				break;
			case OP_VAR_DECR:
				x = get(fp + operand());
				set(fp + operand(), unit.decrement(x));
				pc += 2;
				break;
			case OP_ARG_INCR:
				x = get(fp - operand());
				set(fp + operand(), unit.increment(x));
				pc += 2;
				break;
			case OP_ARG_DECR:
				x = get(fp - operand());
				set(fp + operand(), unit.decrement(x));
				pc += 2;
				break;
			case OP_VAR_ASSN:
				set(fp+operand(), pop());
				pc += 2;
				break;
			case OP_VAR_PUSH:
				push(get(fp+operand()));
				pc += 2;
				break;
			case OP_ARG_ASSN:
				set((fp-3)-operand(), pop());
				pc += 2;
				break;
			case OP_ARG_PUSH:
				push(get((fp-3)-operand()));
				pc += 2;
				break;
			case OP_TO_LIST:
				push(toList(operand()));
				pc += 2;
				break;
			case OP_GOTO:
				pc = operand();
				break;
			case OP_GOTO_FALSE:
				pc = (pop().isTrue())? pc + 2 : operand();
				break;
			case OP_CALL:
				push(pc+2);
				pc = operand();
				break;
			case OP_FRAME:
				push(fp);
				fp  = sp;
				sp += operand();
				pc += 2;
				break;
			case OP_RETURN:
				x  = pop();
				sp = fp;
				fp = pop().toInteger();
				pc = pop().toInteger();
				break;
			case OP_CALL_DEL:
				delete(operand());
				push(x);
				pc += 2;
				break;
			case OP_PRINT:
				System.out.print(pop());
				pc++;
				break;
			case OP_PRINTLN:
				System.out.println("");
				pc++;
				break;
			case OP_EXIT:
				return pop().getValue();
			case OP_BIND_SET:
				x = pop();
				y = pop();
				bind.put(y.toString(), x.getValue());
				pc++;
				break;
			case OP_BIND_GET:
				push(new Code(bind.get(pop().toString())));
				pc++;
				break;
			case OP_INSTANCE:
				x = operand(0);
				y = operand(1);
				push(getInstance(x, y.toInteger()));
				pc += 3;
				break;
			case OP_METHOD:
				x = operand(0);
				y = operand(1);
				push(invoke(x, y.toInteger()));
				pc += 3;
				break;
			case OP_FIELD_ASSN:
				push(setField(operand(0)));
				pc += 2;
				break;
			case OP_FIELD_PUSH:
				push(getField(operand(0)));
				pc += 2;
				break;
			case OP_CLASS_CAST:
				push(cast(operand(0)));
				pc += 2;
				break;
			default:
				System.out.println("Fatal Error at AriCE VM : " + codes[pc]);
				return null;
			}
		}
		return get(0);
	}
	/**
	*スタックの先頭から値を取り出して削除します。
	*@return 取り出した値
	*/
	private Code pop(){
		Code ret = stack[sp-1];
		stack[(sp--)-1] = null;
		return ret;
	}
	/**
	*スタック上の指定した位置から値を取り出します。
	*@param i 取り出す位置
	*@return 取り出した値
	*/
	private Code get(int i){
		return stack[i];
	}
	/**
	*スタックの先頭に値を積みます。
	*@param code 積む値
	*/
	private void push(Code code) throws ScriptException{
		try{
			stack[sp++] = code;
		}catch(ArrayIndexOutOfBoundsException ex){
			throw error("Stack Level Too Deep : " + sp);
		}
	}
	/**
	*スタックの先頭に真偽値を積みます。
	*@param bool 真偽値
	*/
	private void push(boolean bool) throws ScriptException{
		try{
			stack[sp++] = new Code(bool);
		}catch(ArrayIndexOutOfBoundsException ex){
			throw error("Stack Level Too Deep : " + sp);
		}
	}
	/**
	*スタックの先頭に整数値を積みます。
	*@param intg 整数値
	*/
	private void push(int intg) throws ScriptException{
		try{
			stack[sp++] = new Code(intg);
		}catch(ArrayIndexOutOfBoundsException ex){
			throw error("Stack Level Too Deep : " + sp);
		}
	}
	/**
	*スタックの指定したインデックスに値を設定します。
	*@param index 位置
	*@param code 値
	*/
	private void set(int index, Code code){
		stack[index] = code;
	}
	/**
	*スタックの先頭から指定された個数の値を削除します。
	*@param num 個数
	*/
	private void delete(int num){
		for(int i=0;i<num;i++){
			stack[--sp] = null;
		}
	}
	/**
	*命令の第一オペランドを返します。
	*@return オペランドの示す値
	*/
	private int operand() throws ScriptException{
		return codes[pc+1].toInteger();
	}
	/**
	*命令のオペランドを返します。
	*@param num オペランドの番号
	*@return オペランドコード
	*/
	private Code operand(int num){
		return codes[pc+1+num];
	}
	/**
	*スタックの先頭から指定された個数の値をリストに纏めます。
	*@param num 個数
	*@return リストコード
	*/
	private Code toList(int num){
		ArrayList<Object> list = new ArrayList<Object>(num);
		for(int i=num;i>0;i--){
			list.add(stack[sp-i].getValue());
		}
		sp -= num;
		return new Code(list);
	}
	/**
	*指定した名前のクラスのインスタンスを生成します。
	*@param name クラス名
	*@param pars 引数の個数
	*@return 生成したインスタンスを格納したコード
	*/
	private Code getInstance(Code name, int pars) throws ScriptException{
		Code[] args = new Code[pars];
		for(int i=pars-1;i>=0;i--){
			args[i] = pop();
		}
		return unit.getInstanceCode(name, args);
	}
	/**
	*スタックの先頭について、指定したメソッドを実行します。
	*@param name メソッド名
	*@param pars 引数の個数
	*@return メソッドの戻り値
	*/
	private Code invoke(Code name, int pars) throws ScriptException{
		Code[] args = new Code[pars];
		for(int i=pars-1;i>=0;i--){
			args[i] = pop();
		}
		return unit.invokeMethod(pop(), name, args);
	}
	/**
	*スタックの先頭について、指定したフィールドの値を返します。
	*@param name フィールド名
	*@return フィールドの値
	*/
	private Code getField(Code name) throws ScriptException{
		return unit.getField(pop(), name);
	}
	/**
	*スタックの先頭について、指定したフィールドに値を設定します。
	*@param name  フィールド名
	*@return フィールドの値
	*/
	private Code setField(Code name) throws ScriptException{
		Code value = pop(), obj = pop();
		unit.setField(obj, name, value);
		return value;
	}
	/**
	*スタックの先頭について、指定した型に変換します。
	*@param cast 型
	*@return 型変換したコード
	*/
	private Code cast(Code cast) throws ScriptException{
		return unit.cast(pop(), cast);
	}
	/**例外を生成します*/
	private ScriptException error(Object msg){
		return new ScriptException(msg.toString());
	}
}
