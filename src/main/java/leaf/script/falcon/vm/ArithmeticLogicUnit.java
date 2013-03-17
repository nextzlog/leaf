/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.vm;

/**
 * 仮想マシンでの演算を行うユニットです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 *
 */
final class ArithmeticLogicUnit {
	private final Stack<Object> stack;
	
	/**
	 * 演算対象のスタックを指定してALUを構築します。
	 * 
	 * @param stack 演算対象のスタック
	 */
	ArithmeticLogicUnit(Stack<Object> stack) {
		this.stack = stack;
	}
	
	public void castIntToDouble() {
		Integer val = (Integer) stack.pop();
		stack.push(val.doubleValue());
	}
	
	public void castDoubleToInt() {
		Double val = (Double) stack.pop();
		stack.push(val.intValue());
	}
	
	public void calcLogNeg() {
		stack.push(!(Boolean) stack.pop());
	}
	
	public void calcLogAnd() {
		Boolean b1 = (Boolean) stack.pop();
		Boolean b2 = (Boolean) stack.pop();
		stack.push(b2 && b1);
	}
	
	public void calcLogOr() {
		Boolean b1 = (Boolean) stack.pop();
		Boolean b2 = (Boolean) stack.pop();
		stack.push(b2 || b1);
	}
	
	public void calcLogXor() {
		Boolean b1 = (Boolean) stack.pop();
		Boolean b2 = (Boolean) stack.pop();
		stack.push(b2 ^ b1);
	}
	
	public void calcEq() {
		Object o1 = stack.pop();
		Object o2 = stack.pop();
		if(o2 != null) {
			stack.push(o2.equals(o1));
		} else {
			stack.push(o1 == null);
		}
	}
	
	public void calcNeq() {
		Object o1 = stack.pop();
		Object o2 = stack.pop();
		if(o2 != null) {
			stack.push(!o2.equals(o1));
		} else {
			stack.push(o1 != null);
		}
	}
	
	public void calcIntAdd() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 + i1);
	}
	
	public void calcIntSub() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 - i1);
	}
	
	public void calcIntMul() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 * i1);
	}
	
	public void calcIntDiv() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 / i1);
	}
	
	public void calcIntRem() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 % i1);
	}
	
	public void calcIntLt() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 < i1);
	}
	
	public void calcIntGt() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 > i1);
	}
	
	public void calcIntLe() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 <= i1);
	}
	
	public void calcIntGe() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 >= i1);
	}
	
	public void calcIntNeg() {
		stack.push(-(Integer) stack.pop());
	}
	
	public void calcIntAnd() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 & i1);
	}
	
	public void calcIntOr() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 | i1);
	}
	
	public void calcIntXor() {
		Integer i1 = (Integer) stack.pop();
		Integer i2 = (Integer) stack.pop();
		stack.push(i2 ^ i1);
	}
	
	public void calcDoubleAdd() {
		Double d1 = (Double) stack.pop();
		Double d2 = (Double) stack.pop();
		stack.push(d2 + d1);
	}
	
	public void calcDoubleSub() {
		Double d1 = (Double) stack.pop();
		Double d2 = (Double) stack.pop();
		stack.push(d2 - d1);
	}
	
	public void calcDoubleMul() {
		Double d1 = (Double) stack.pop();
		Double d2 = (Double) stack.pop();
		stack.push(d2 * d1);
	}
	
	public void calcDoubleDiv() {
		Double d1 = (Double) stack.pop();
		Double d2 = (Double) stack.pop();
		stack.push(d2 / d1);
	}
	
	public void calcDoubleRem() {
		Double d1 = (Double) stack.pop();
		Double d2 = (Double) stack.pop();
		stack.push(d2 % d1);
	}
	
	public void calcDoubleLt() {
		Double d1 = (Double) stack.pop();
		Double d2 = (Double) stack.pop();
		stack.push(d2 < d1);
	}
	
	public void calcDoubleGt() {
		Double d1 = (Double) stack.pop();
		Double d2 = (Double) stack.pop();
		stack.push(d2 > d1);
	}
	
	public void calcDoubleLe() {
		Double d1 = (Double) stack.pop();
		Double d2 = (Double) stack.pop();
		stack.push(d2 <= d1);
	}
	
	public void calcDoubleGe() {
		Double d1 = (Double) stack.pop();
		Double d2 = (Double) stack.pop();
		stack.push(d2 >= d1);
	}
	
	public void calcDoubleNeg() {
		stack.push(-(Double) stack.pop());
	}

}
