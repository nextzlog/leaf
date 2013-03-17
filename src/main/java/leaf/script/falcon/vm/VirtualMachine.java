/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.vm;

import javax.script.Bindings;
import javax.script.ScriptException;

import leaf.script.falcon.ast.stmt.Label;
import leaf.script.falcon.error.StackException;

/**
 * 仮想マシンの実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
public final class VirtualMachine {
	private final Stack<Frame> frames;
	private final Stack<Object> stack;
	private final ArithmeticLogicUnit alu;
	private final JavaAPI api;
	private final Object[] codes;
	
	/**
	 * 中間言語命令列を指定して仮想マシンを構築します。
	 * 
	 * @param codes 中間言語命令列
	 */
	public VirtualMachine(Object[] codes) {
		frames = new Stack<Frame>(1 << 20);
		stack = new Stack<Object>(1 << 20);
		alu = new ArithmeticLogicUnit(stack);
		api = new JavaAPI(stack);
		this.codes = codes;
	}
	
	/**
	 * [@link Bindings}を指定してプログラムを実行します。
	 * 
	 * @param bind  グローバル変数を保持するBindings
	 * @return main関数の返り値
	 * @throws ScriptException 実行時例外が発生した場合
	 */
	public Object execute(Bindings bind) throws ScriptException {
		int pc = 0;
		stack.push(bind);
		
		while(true) try {
			switch((InstructionSet) codes[pc++]) {
			case EXIT:
				return stack.pop();
			case PUSH:
				stack.push(codes[pc++]);
				break;
			case ITOD:
				alu.castIntToDouble();
				break;
			case DTOI:
				alu.castDoubleToInt();
				break;
			case NEG:
				alu.calcLogNeg();
				break;
			case AND:
				alu.calcLogAnd();
				break;
			case OR:
				alu.calcLogOr();
				break;
			case XOR:
				alu.calcLogXor();
				break;
			case EQ:
				alu.calcEq();
				break;
			case NEQ:
				alu.calcNeq();
				break;
			case IADD:
				alu.calcIntAdd();
				break;
			case ISUB:
				alu.calcIntSub();
				break;
			case IMUL:
				alu.calcIntMul();
				break;
			case IDIV:
				alu.calcIntDiv();
				break;
			case IREM:
				alu.calcIntRem();
				break;
			case ILT:
				alu.calcIntLt();
				break;
			case IGT:
				alu.calcIntGt();
				break;
			case ILE:
				alu.calcIntLe();
				break;
			case IGE:
				alu.calcIntGe();
				break;
			case INEG:
				alu.calcIntNeg();
				break;
			case IAND:
				alu.calcIntAnd();
				break;
			case IOR:
				alu.calcIntOr();
				break;
			case IXOR:
				alu.calcIntXor();
				break;
			case DADD:
				alu.calcDoubleAdd();
				break;
			case DSUB:
				alu.calcDoubleSub();
				break;
			case DMUL:
				alu.calcDoubleMul();
				break;
			case DDIV:
				alu.calcDoubleDiv();
				break;
			case DREM:
				alu.calcDoubleRem();
				break;
			case DLT:
				alu.calcDoubleLt();
				break;
			case DGT:
				alu.calcDoubleGt();
				break;
			case DLE:
				alu.calcDoubleLe();
				break;
			case DGE:
				alu.calcDoubleGe();
				break;
			case DNEG:
				alu.calcDoubleNeg();
				break;
			case LASSN:
				assignLocal(operand(pc++, 0));
				break;
			case LPUSH:
				pushLocal(operand(pc++, 0));
				break;
			case AASSN:
				assignArg(operand(pc++, 0));
				break;
			case APUSH:
				pushArg(operand(pc++, 0));
				break;
			case JUMP:
				pc = operand(pc, 0);
				break;
			case JUMPF:
				if(Boolean.TRUE == stack.pop()) pc++;
				else pc = operand(pc, 0);
				break;
			case JUMPT:
				if(Boolean.TRUE != stack.pop()) pc++;
				else pc = operand(pc, 0);
				break;
			case CALL:
				stack.push(pc + 1);
				pc = operand(pc, 0);
				break;
			case FRAME:
				createFrame(
				operand(pc, 0), operand(pc, 1));
				pc += 2;
				break;
			case RET:
				pc = frames.pop().getReturnPosition();
				break;
			case ADEL:
				deleteArgs(operand(pc++, 0));
				break;
			case DEL:
				stack.pop();
				break;
			case PRINT:
				System.out.println(stack.pop());
				break;
			case JNEW:
				api.newInstance(
				codes[pc++], operand(pc++, 0));
				break;
			case JINV:
				api.invokeMethod(
				codes[pc++], operand(pc++, 0));
				break;
			case JASSN:
				api.assignField(codes[pc++]);
				break;
			case JPUSH:
				api.pushField(codes[pc++]);
				break;
			}
		} catch(IndexOutOfBoundsException ex) {
			throw new StackException(ex);
		} catch(Exception ex) {
			throw new ScriptException(ex);
		}
	}
	
	/**
	 * 現在の命令のオペランド部を返します。
	 * 
	 * @param pc プログラムカウンタ
	 * @param n オペランドの番号
	 * 
	 * @return オペランドの値
	 */
	private int operand(int pc, int n) {
		Object rand = codes[pc + n];
		if(rand instanceof Label) {
			return ((Label) rand).getJump();
		}
		return (Integer) rand;
	}
	
	/**
	 * 指定された番号の変数に値を代入します。
	 * 
	 * @param index 変数の番号
	 */
	private void assignLocal(int index) {
		frames.peek().set(index, stack.peek());
	}
	
	/**
	 * 指定された番号の引数に値を代入します。
	 * 
	 * @param index 引数の番号
	 */
	private void assignArg(int index) {
		int fp = frames.peek().getArgBase();
		stack.set(fp + index, stack.peek());
	}
	
	/**
	 * 指定された番号の変数の値をpushします。
	 * 
	 * @param index 変数の番号
	 */
	private void pushLocal(int index) {
		stack.push(frames.peek().get(index));
	}
	
	/**
	 * 指定された番号の引数の値をpushします。
	 * 
	 * @param index 引数の番号
	 */
	private void pushArg(int index) {
		int fp = frames.peek().getArgBase();
		stack.push(stack.get(fp + index));
	}
	
	/**
	 * 引数と変数の個数を指定して関数を構築します。
	 * 
	 * @param ac 引数の個数
	 * @param lc 変数の個数
	 */
	private void createFrame(int ac, int lc) {
		int rp = (Integer) stack.pop();
		int ab = stack.size() - ac;
		frames.push(new Frame(rp, ab, lc));
	}
	
	/**
	 * 関数呼び出しからの復帰直後に引数を削除します。
	 * 
	 * @param count 引数の個数
	 */
	private void deleteArgs(int count) {
		Object retval = stack.pop();
		stack.delete(count);
		stack.push(retval);
	}

}
