/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.vm;

import java.io.Serializable;

/**
 * 関数呼び出しの情報を保管する環境の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/24
 *
 */
final class Frame implements Serializable {
	static final long serialVersionUID = 1L;
	private Object[] locals;
	private final int rp;
	private final int ab;
	
	/**
	 * 関数フレームを構築します。
	 * 
	 * @param rp 関数から戻る位置
	 * @param ab 実引数の基準位置
	 * @param lc ローカル変数の個数
	 */
	public Frame(int rp, int ab, int lc) {
		locals = new Object[lc];
		this.rp = rp;
		this.ab = ab;
	}
	
	/**
	 * 指定されたローカル変数を返します。
	 * 
	 * @param index 変数の番号
	 * @return 変数の値
	 */
	public Object get(int index) {
		return locals[index];
	}
	
	/**
	 * 指定されたローカル変数に代入します。
	 * 
	 * @param index 変数の番号
	 * @param value 代入する値
	 */
	public void set(int index, Object value) {
		locals[index] = value;
	}
	
	/**
	 * 関数から復帰する時の復帰位置を返します。
	 * 
	 * @return 復帰する位置
	 */
	public int getReturnPosition() {
		return rp;
	}
	
	/**
	 * 実引数を参照するための基準点を返します。
	 * 
	 * @return 実引数のスタック上の位置
	 */
	public int getArgBase() {
		return ab;
	}

}
