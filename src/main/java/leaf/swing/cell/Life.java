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
 *「Game of Life」のセルオートマータの実装です。
 *
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.4 作成：2012年3月16日
 */
public final class Life extends Automata{
	private static final float HUE_DIV = 0.01f;
	private float hue = 0.3f;
	private Color color;
	
	private static final int DEAD  = 0;
	private static final int ALIVE = 1;
	
	/**
	 *指定した縦横セル数でセルオートマータを構築します。
	 *
	 *@param w 横セル数
	 *@param h 縦セル数
	 */
	public Life(int w, int h){
		super(w, h);
		color = Color.getHSBColor(hue, 1f, 1f);
	}
	/**
	 *指定された位置のセルの次世代での状態を計算します。
	 *
	 *@param x 横方向の座標
	 *@param y 縦方向の座標
	 *
	 *@return 次世代での状態
	 */
	@Override protected int getNextState(int x, int y){
		int life = 0;
		life += getState(x-1, y-1);
		life += getState(x  , y-1);
		life += getState(x+1, y-1);
		life += getState(x-1, y  );
		life += getState(x+1, y  );
		life += getState(x-1, y+1);
		life += getState(x  , y+1);
		life += getState(x+1, y+1);
		
		if(life == 2) return getState(x, y);
		return (life == 3)? ALIVE : DEAD;
	}
	/**
	 *セルオートマータの次世代でのテーブルを計算します。
	 */
	@Override public void updateNext(){
		if((hue += HUE_DIV) > 1f) hue = 0f;
		color = Color.getHSBColor(hue, 1f, 1f);
		super.updateNext();
	}
	/**
	 *指定された位置のセルの表示色を返します。
	 *
	 *@param x 横方向の座標
	 *@param y 縦方向の座標
	 *
	 *@return セルの表示色
	 */
	@Override public Color getCellColor(int x, int y){
		return (getState(x, y) == ALIVE)? color : Color.BLACK;
	}
	
	/**
	 *ユーザーからの入力により、指定されたセルの状態を切り替えます。
	 *
	 *@param x 横方向の座標
	 *@param y 縦方向の座標
	 */
	@Override public void cellPressed(int x, int y){
		setState(x, y, (getState(x, y) == DEAD)? ALIVE : DEAD);
	}
}