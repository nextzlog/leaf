/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.vm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 仮想マシンからJavaAPIを操作するためのユニットです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/02/19
 *
 */
final class JavaAPI {
	private final Stack<Object> stack;
	
	/**
	 * 演算対象のスタックを指定してAPIを構築します。
	 * 
	 * @param stack 演算対象のスタック
	 */
	public JavaAPI(Stack<Object> stack) {
		this.stack = stack;
	}
	
	/**
	 * スタックから引数を取得してインスタンス化します。
	 * 
	 * @param c 実行するコンストラクタ
	 * @param argc  コンストラクタの引数
	 * @throws ReflectiveOperationException
	 */
	public void newInstance(Object c, int argc)
		throws ReflectiveOperationException {
		Object[] args = new Object[argc];
		for(int i = 1; i <= argc; i++) {
			args[argc - i] = stack.pop();
		}
		Constructor<?> con = (Constructor<?>) c;
		stack.push(con.newInstance(args));
	}
	
	/**
	 * スタックから引数を取得してメソッドを実行します。
	 * 
	 * @param m 実行するメソッド
	 * @param argc  メソッドの引数
	 * @throws ReflectiveOperationException
	 */
	public void invokeMethod(Object m, int argc)
		throws ReflectiveOperationException {
		Object[] args = new Object[argc];
		for(int i = 1; i <= argc; i++) {
			args[argc - i] = stack.pop();
		}
		Method met = (Method) m;
		stack.push(met.invoke(stack.pop(), args));
	}
	
	/**
	 * スタックトップの値をフィールドに代入します。
	 * 
	 * @param f 代入するフィールド
	 * @throws ReflectiveOperationException
	 */
	public void assignField(Object f)
		throws ReflectiveOperationException {
		Field field = (Field) f;
		Object val = stack.pop();
		Object obj = stack.pop();
		field.set(obj, val);
		stack.push(val);
	}
	
	/**
	 * フィールドの値をスタックにpushします。
	 * 
	 * @param f pushするフィールド
	 * @throws ReflectiveOperationException
	 */
	public void pushField(Object f)
		throws ReflectiveOperationException {
		Field field = (Field) f;
		stack.push(field.get(stack.pop()));
	}

}
