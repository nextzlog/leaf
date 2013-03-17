/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.vm;

/**
 * 仮想マシンで使用されるスタックの実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/23
 * 
 * @param <E>
 */
public final class Stack<E> {
	private final Object[] values;
	private int sp = 0;
	
	/**
	 * 最大容量を指定してスタックを構築します。
	 * 
	 * @param cap スタックの最大容量
	 */
	public Stack(int cap) {
		values = new Object[cap];
	}
	
	/**
	 * スタックの最大容量を返します。
	 * 
	 * @return 最大容量
	 */
	public int getCapaciry() {
		return values.length;
	}
	
	/**
	 * スタックに値を追加します。
	 * 
	 * @param value 追加する値
	 */
	public void push(E value) {
		values[sp++] = value;
	}
	
	/**
	 * 先頭の値を取り出して削除します。
	 * 
	 * @return 取りだした値
	 */
	@SuppressWarnings("unchecked")
	public E pop() {
		Object ret = values[--sp];
		values[sp] = null;
		return (E) ret;
	}
	
	/**
	 * 先頭の値を返します。
	 * 
	 * @return 先頭の値
	 */
	@SuppressWarnings("unchecked")
	public E peek() {
		return (E) values[sp-1];
	}
	
	/**
	 * 指定した位置の値を返します。
	 * 
	 * @param index 位置
	 * @return indexの位置にある値
	 */
	@SuppressWarnings("unchecked")
	public E get(int index) {
		return (E) values[index];
	}
	
	/**
	 * 指定した位置に値を格納します。
	 * 
	 * @param index 位置
	 * @param value 格納する値
	 */
	public void set(int index, E value) {
		values[index] = value;
	}
	
	/**
	 * 指定された個数の値を削除します。
	 * 
	 * @param num 削除する個数
	 */
	public void delete(int num) {
		for(int i = 0; i < num; i++) {
			values[--sp] = null;
		}
	}
	
	/**
	 * スタックのサイズを返します。
	 * 
	 * @return スタックの大きさ
	 */
	public int size() {
		return sp;
	}

}