/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium;

import leaf.script.common.util.*;
import leaf.script.common.vm.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.script.Bindings;
import javax.script.ScriptException;

import leaf.manager.LeafLocalizeManager;

import static leaf.script.euphonium.AriceScriptEngine.FRAME_NEST_MAX;
import static leaf.script.euphonium.AriceScriptEngine.STACK_SIZE_MAX;
import static leaf.script.euphonium.AriceScriptEngine.EXECUTE_STEP_MAX;

/**
 *AriCE処理系の仮想機械の実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月29日
 */
final class VirtualMachine extends CodesAndTokens{
	
	private int words, step, pc;
	private final FrameStack frames;
	private final CallStack  stack;
	private final EscapeStack escapes;
	private Code[] codes;
	private Code x, y; //temp
	
	private final ClosureTable closures;
	private final LeafLocalizeManager localize;
	
	/**
	*仮想マシンを生成します。
	*/
	public VirtualMachine(){
		stack  = new CallStack (STACK_SIZE_MAX);
		frames = new FrameStack(FRAME_NEST_MAX);
		escapes = new EscapeStack();
		closures = new ClosureTable();
		localize = LeafLocalizeManager.getInstance(getClass());
	}
	/**
	*プログラムを実行します。
	*@param codes  中間言語コード
	*@param bind バインディング
	*@return 実行結果の値
	*@throws ScriptException 実行エラーがあった場合
	*/
	public Object execute(Code[] codes, Bindings bind)
	throws ScriptException{
		pc = 0;
		words = (this.codes = codes).length;
		stack.push(new Code(bind));
		while(step++ < EXECUTE_STEP_MAX){
			try{
				switch(codes[pc].toInt()){
				case OP_LIT_PUSH:
					stack.push(operandCode(0));
					pc += 2;
					break;
				case OP_BIT_LEFT:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.bitleft(y,x));
					pc++;
					break;
				case OP_BIT_RIGHT:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.bitright(y,x));
					pc++;
					break;
				case OP_DUP:
					stack.dup();
					pc++;
					break;
				case OP_DEL:
					stack.delete(1);
					pc++;
					break;
				case OP_ADD:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.add(y, x));
					pc++;
					break;
				case OP_SUB:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.subtract(y, x));
					pc++;
					break;
				case OP_MUL:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.multiply(y, x));
					pc++;
					break;
				case OP_DIV:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.divide(y, x));
					pc++;
					break;
				case OP_MOD:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.mod(y, x));
					pc++;
					break;
				case OP_POW:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.power(y, x));
					pc++;
					break;
				case OP_NEG:
					x = stack.pop();
					stack.push(LeafCalcUnit.negate(x));
					pc++;
					break;
				case OP_AND:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.and(y, x));
					pc++;
					break;
				case OP_OR:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.or(y, x));
					pc++;
					break;
				case OP_XOR:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.xor(y, x));
					pc++;
					break;
				case OP_NOT:
					x = stack.pop();
					stack.push(LeafCalcUnit.not(x));
					pc++;
					break;
				case OP_SHORT_AND:
					if(stack.pop().toBoolean()){
						pc += 2;
						stack.push(true);
					}else{
						pc = operand(0);
						stack.push(false);
					}
					break;
				case OP_SHORT_OR:
					if(stack.pop().toBoolean()){
						pc = operand(0);
						stack.push(true);
					}else{
						pc += 2;
						stack.push(false);
					}
					break;
				case OP_IS:
					x = stack.pop();
					y = stack.pop();
					stack.push(x == y);
					pc++;
					break;
				case OP_EQU:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.deepequals(y, x));
					pc++;
					break;
				case OP_NEQ:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.notequal(y, x));
					pc++;
					break;
				case OP_GRET:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.isGreater(y, x));
					pc++;
					break;
				case OP_GREQ:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.isGreaterOrEqual(y, x));
					pc++;
					break;
				case OP_LESS:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.isLess(y, x));
					pc++;
					break;
				case OP_LSEQ:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.isLessOrEqual(y, x));
					pc++;
					break;
				case OP_COMP:
					x = stack.pop();
					y = stack.pop();
					stack.push(LeafCalcUnit.compareTo(y, x));
					pc++;
					break;
				case OP_VAR_INCR:{
					int nest  = operand(0);
					int index = operand(1);
					Frame frame = frames.peek();
					Code code = frame.getLocal(nest, index);
					frame.setLocal(nest, index,
						LeafCalcUnit.increment(code));
					pc += 3;
					break;
				}
				case OP_VAR_DECR:{
					int nest  = operand(0);
					int index = operand(1);
					Frame frame = frames.peek();
					Code code = frame.getLocal(nest, index);
					frame.setLocal(nest, index,
						LeafCalcUnit.decrement(code));
					pc += 3;
					break;
				}
				case OP_ARG_INCR:{
					int index = operand(0);
					Frame frame = frames.peek();
					Code code = frame.getArgument(index);
					frame.setArgument(index,
						LeafCalcUnit.increment(code));
					pc += 2;
					break;
				}
				case OP_ARG_DECR:{
					int index = operand(0);
					Frame frame = frames.peek();
					Code code = frame.getArgument(index);
					frame.setArgument(index,
						LeafCalcUnit.decrement(code));
					pc += 2;
					break;
				}
				case OP_VAR_ASSN:{
					int nest  = operand(0);
					int index = operand(1);
					Frame frame = frames.peek();
					frame.setLocal(nest, index, stack.pop());
					pc += 3;
					break;
				}
				case OP_VAR_PUSH:{
					int nest  = operand(0);
					int index = operand(1);
					Frame frame = frames.peek();
					stack.push(frame.getLocal(nest, index));
					pc += 3;
					break;
				}
				case OP_ARG_ASSN:{
					int index = operand(0);
					Frame frame = frames.peek();
					frame.setArgument(index, stack.pop());
					pc += 2;
					break;
				}
				case OP_ARG_PUSH:{
					int index = operand(0);
					Frame frame = frames.peek();
					stack.push(frame.getArgument(index));
					pc += 2;
					break;
				}
				case OP_GOTO:
					pc = operand(0);
					break;
				case OP_GOTO_FALSE:
					x  = stack.pop();
					pc = (x.toBoolean())? pc + 2 : operand(0);
					break;
				case OP_FUNC_CALL:
					x  = new Code(pc+3);
					y  = operandCode(0);
					pc = operand(1);
					break;
				case OP_FUNC_FRAME:
					pushFrame();
					pc += 3;
					break;
				case OP_CLOS_CALL:
					callClosure();
					break;
				case OP_CLOS_PUSH:
					pushClosure();
					pc += 3;
					break;
				case OP_RETURN:{
					Frame frame = frames.pop();
					pc = frame.getJump();
					frame.clearArguments();
					break;
				}
				case OP_TRY_PUSH:
					escapes.push(operand(0),
						stack.size(), frames.peek());
					pc += 2;
					break;
				case OP_TRY_DEL:
					escapes.delete();
					pc++;
					break;
				case OP_PRINT:
					System.out.print(stack.pop());
					pc++;
					break;
				case OP_PRINTLN:
					System.out.println(stack.pop());
					pc++;
					break;
				case OP_OBJ_TOINST:
					x = operandCode(0);
					y = operandCode(1);
					stack.push(newInstance(x, y.toInt()));
					pc += 3;
					break;
				case OP_OBJ_METHOD:
					x = operandCode(0);
					y = operandCode(1);
					stack.push(invoke(x, y.toInt()));
					pc += 3;
					break;
				case OP_FIELD_ASSN:
					stack.push(setField(operandCode(0)));
					pc += 2;
					break;
				case OP_FIELD_PUSH:
					stack.push(getField(operandCode(0)));
					pc += 2;
					break;
				case OP_EXIT:
					return stack.pop().getValue();
				default: throw error("execute_exception_switch");
				}
			}catch(IndexOutOfBoundsException ex){
				throw error("execute_exception_overflow");
			}catch(ScriptException ex){ //実行時例外補足
				EscapeInfo info = escapes.pop();
				if(info == null) throw ex;
				pc = info.getJump();
				int sp = info.getStackPointer();
				stack.delete(stack.size() - sp);
				stack.push(new Code(ex));
				Frame frame = info.getFrame();
				while(frames.peek()!=frame)frames.delete();
				ex.setStackTrace(new StackTraceElement[0]);
			}
		}
		return null;
	}
	/**命令のオペランドを返します*/
	private int operand(int num) throws ScriptException{
		return codes[pc+1+num].toInt();
	}
	/**命令のオペランドを返します*/
	private Code operandCode(int num){
		return codes[pc+1+num];
	}
	/**関数フレームを生成してプッシュします*/
	private void pushFrame() throws ScriptException{
		int old_pc = x.toInt();
		int ac = y.toInt();
		int lc = operand(1);
		String name = operandCode(0).toString();
		frames.push(name, null, old_pc, lc);
		Frame frame = frames.peek();
		Code[] args = new Code[ac];
		for(int i=ac-1;i>=0;i--){
			args[i] = stack.pop();
		}
		frame.setArguments(args);
	}
	/**クロージャを実行します*/
	private void callClosure() throws ScriptException{
		int ac = operand(0);
		Code[] args = new Code[ac];
		for(int i=ac-1;i>=0;i--){
			args[i] = stack.pop();
		}
		Closure clos = stack.pop().toClosure();
		closures.checkClosure(clos);
		frames.push(clos, pc+2);
		frames.peek().setArguments(args);
		pc = clos.getJump();
	}
	/**クロージャをプッシュします*/
	private void pushClosure() throws ScriptException{
		int start = operand(0);
		int lc = operand(1);
		Frame frame = frames.peek();
		stack.push(new Code(
			closures.createClosure(start, frame, lc)));
	}
	/**指定したクラスのインスタンスを生成します*/
	private Code newInstance(Code clas, int pars)
	throws ScriptException{
		Code[] args = new Code[pars];
		for(int i=pars-1;i>=0;i--){
			args[i] = stack.pop();
		}
		return LeafReflectUnit.newInstance(clas.toClass(), args);
	}
	/**メソッドを実行します*/
	private Code invoke(Code name, int pars)
	throws ScriptException{
		Code[] args = new Code[pars];
		for(int i=pars-1;i>=0;i--){
			args[i] = stack.pop();
		}
		Code obj = stack.pop();
		return LeafReflectUnit.invokeMethod(name.toString(), obj, args);
	}
	/**フィールドの値を取得します*/
	private Code getField(Code name)
	throws ScriptException{
		return LeafReflectUnit.getField(name.toString(), stack.pop());
	}
	/**フィールドに値を代入します*/
	private Code setField(Code name)
	throws ScriptException{
		Code value  = stack.pop();
		Code object = stack.pop();
		LeafReflectUnit.setField(name.toString(), object, value);
		return value;
	}
	/**
	*実行時違反が発生した際に例外を生成します*/
	private ScriptException error(String key, Object... args){
		return new ScriptException(localize.translate(key, args));
	}
}
