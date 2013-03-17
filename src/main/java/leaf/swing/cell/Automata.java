/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.cell;

import java.awt.Color;

/**
 * セルの状態を管理し、次状態を導出するセルオートマータです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.4 作成：2012年3月16日
 *
 */
public abstract class Automata {
	private boolean isUpdating = false;
	private int width, height;
	private int[][] states;
	
	/**
	 * 指定した縦横セル数でセルオートマータを構築します。
	 * 
	 * @param w 横セル数
	 * @param h 縦セル数
	 */
	public Automata(int w, int h) {
		states = new int[width = w][height = h];
	}
	
	/**
	 * セルオートマータの横方向のセル数を返します。
	 * 
	 * @return 横方向のセル数
	 */
	public final int getWidth() {
		return width;
	}
	
	/**
	 * セルオートマータの縦方向のセル数を返します。
	 * 
	 * @return 縦方向のセル数
	 */
	public final int getHeight() {
		return height;
	}
	
	/**
	 * セルオートマータの状態を初期化します。
	 */
	public final void init() {
		states = new int[width][height];
	}
	
	/**
	 * 指定された位置のセルの現世代での状態を設定します。
	 * 
	 * @param x 横方向の座標
	 * @param y 縦方向の座標
	 * @param state 現世代での状態
	 * @throws IllegalStateException
	 * {@link #updateNext()}の内部で呼び出された場合
	 */
	public final void setState(int x, int y, int state)
	throws IllegalStateException {
		int xmod = x % width;
		int ymod = y % height;
		
		if(xmod < 0) xmod += width;
		if(ymod < 0) ymod += height;
		
		if(!isUpdating) states[xmod][ymod] = state;
		else throw new IllegalStateException();
	}
	
	/**
	 * 指定された位置のセルの現世代での状態を返します。
	 * 
	 * @param x 横方向の座標
	 * @param y 縦方向の座標
	 * @return 現世代での状態
	 */
	public final int getState(int x, int y) {
		int xmod = x % width;
		int ymod = y % height;
		
		if(xmod < 0) xmod += width;
		if(ymod < 0) ymod += height;
		
		return states[xmod][ymod];
	}
	
	/**
	 * 指定された位置のセルの次世代での状態を計算します。
	 * 
	 * @param x 横方向の座標
	 * @param y 縦方向の座標
	 * @return 次世代での状態
	 */
	protected abstract int getNextState(int x, int y);
	
	/**
	 * セルオートマータ全体の次世代での状態を計算します。
	 */
	public void updateNext() {
		int[][] next = new int[width][height];
		
		isUpdating = true;
		for(int x = 0; x < width ; x++) {
		for(int y = 0; y < height; y++) {
			next[x][y] = getNextState(x, y);
		}
		}
		for(int x = 0; x < width ; x++) {
		for(int y = 0; y < height; y++) {
			states[x][y] = next[x][y];
		}
		}
		isUpdating = false;
	}
	
	/**
	 * セルオートマータが次世代の計算中であるか確認します。
	 * 
	 * @return 次世代の計算途中である場合true
	 */
	public boolean isUpdating() {
		return isUpdating;
	}
	
	/**
	 * セルオートマータが次世代の計算中であるか設定します。
	 * 
	 * @param 次世代の計算途中でない場合false
	 */
	final void setUpdating(boolean isUpdating) {
		this.isUpdating = isUpdating;
	}
	
	/**
	 * 指定された位置のセルの表示色を返します。
	 * 
	 * @param x 横方向の座標
	 * @param y 縦方向の座標
	 * 
	 * @return セルの表示色
	 */
	public abstract Color getCellColor(int x, int y);
	
	/**
	 * ユーザーからの入力により、指定されたセルの状態を切り替えます。
	 * 
	 * @param x 横方向の座標
	 * @param y 縦方向の座標
	 */
	public abstract void cellPressed(int x, int y);

}