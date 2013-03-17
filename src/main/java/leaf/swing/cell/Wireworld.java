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
 *「Wireworld」のセルオートマータの実装です。
 *
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.4 作成：2012年3月17日
 */
public final class Wireworld extends Automata{
	
	private static final int NULL = 0;
	private static final int WIRE = 2;
	private static final int HEAD = 3;
	private static final int TAIL = 4;
	
	/**
	 *指定した縦横セル数でセルオートマータを構築します。
	 *
	 *@param w 横セル数
	 *@param h 縦セル数
	 */
	public Wireworld(int w, int h){
		super(w, h);
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
		switch(getState(x, y)){
			default   : return NULL;
			case HEAD : return TAIL;
			case TAIL : return WIRE;
			case WIRE : break; // needed
		}
		int heads = 0;
		
		if(getState(x-1, y-1) == HEAD) heads++;
		if(getState(x  , y-1) == HEAD) heads++;
		if(getState(x+1, y-1) == HEAD) heads++;
		if(getState(x-1, y  ) == HEAD) heads++;
		if(getState(x+1, y  ) == HEAD) heads++;
		if(getState(x-1, y+1) == HEAD) heads++;
		if(getState(x  , y+1) == HEAD) heads++;
		if(getState(x+1, y+1) == HEAD) heads++;
		
		return (heads == 1 || heads == 2)? HEAD : WIRE;
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
		switch(getState(x, y)){
			case NULL : return Color.BLACK;
			case WIRE : return Color.YELLOW;
			case HEAD : return Color.BLUE;
			case TAIL : return Color.RED;
		}
		return Color.WHITE;
	}
	
	/**
	 *ユーザーからの入力により、指定されたセルの状態を切り替えます。
	 *
	 *@param x 横方向の座標
	 *@param y 縦方向の座標
	 */
	@Override public void cellPressed(int x, int y){
		switch(getState(x, y)){
			case NULL : setState(x, y, WIRE); break;
			case WIRE : setState(x, y, HEAD); break;
			case HEAD : setState(x, y, TAIL); break;
			case TAIL : setState(x, y, NULL); break;
		}
	}
}