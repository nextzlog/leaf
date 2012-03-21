/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.common.vm;

import java.util.Arrays;

/**
 *仮想機械で用いられる固定容量スタックの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年7月31日
 */
public class Stack<E>{
	private Object[] values;
	private int sp = 0;
	/**
	*最大容量を指定してスタックを生成します。
	*@param cap スタックの最大容量
	*/
	public Stack(int cap){
		values = new Object[cap];
	}
	/**
	*スタックの最大容量を設定します。
	*@param cap スタックの最大容量
	*/
	public void setCapacity(int cap){
		values = Arrays.copyOf(values, cap);
	}
	/**
	*スタックの最大容量を返します。
	*@return スタックの最大容量
	*/
	public int getCapaciry(){
		return values.length;
	}
	/**
	*スタックに値を積みます。
	*@param value 積む値
	*/
	public void push(E value){
		values[sp++] = value;
	}
	/**
	*先頭の値を取り出して削除します。
	*@return 取り出した値
	*/
	@SuppressWarnings("unchecked")
	public E pop(){
		Object ret = values[--sp];
		values[sp] = null;
		return (E) ret;
	}
	/**
	*先頭の値を複製して積みます。
	*/
	public void dup(){
		push(peek());
	}
	/**
	*先頭の値を返します。
	*@return 取り出した値
	*/
	@SuppressWarnings("unchecked")
	public E peek(){
		return (E) values[sp-1];
	}
	/**
	*指定した位置の値を返します。
	*@param index 位置
	*@return 取り出した値
	*/
	@SuppressWarnings("unchecked")
	public E get(int index){
		return (E) values[index];
	}
	/**
	*指定した位置に値を代入します。
	*@param index 位置
	*@param value 設定する値
	*/
	public void set(int index, E value){
		values[index] = value;
	}
	/**
	*先頭から指定個数の値を削除します。
	*@param num 削除する個数
	*/
	public void delete(int num){
		for(int i=0; i<num; i++){
			values[--sp] = null;
		}
	}
	/**
	*先頭の値を削除します。
	*/
	public void delete(){
		values[--sp] = null;
	}
	/**
	*スタックのサイズを返します。
	*@return スタックサイズ
	*/
	public int size(){
		return sp;
	}
}