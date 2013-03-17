/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.cell;

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

/**
 *電光掲示板のセルオートマータによる実装です。
 *
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.4 作成：2012年3月18日
 */
public final class LeftScroll extends Automata{
	
	private final List<Color> colorList;
	
	/**
	 *指定した縦横セル数でセルオートマータを構築します。
	 *
	 *@param w 横セル数
	 *@param h 縦セル数
	 */
	public LeftScroll(int w, int h){
		super(w, h);
		colorList = new ArrayList<Color>();
		colorList.add(Color.BLACK);
	}
	/**
	 *電光掲示板に色を追加します。
	 *
	 *@param color 追加する色
	 */
	public void addColor(Color color){
		colorList.add(color);
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
		return getState(x + 1, y);
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
		final int state = getState(x, y);
		final int size = colorList.size();
		return colorList.get(state % size);
	}
	
	/**
	 *ユーザーからの入力により、指定されたセルの状態を切り替えます。
	 *
	 *@param x 横方向の座標
	 *@param y 縦方向の座標
	 */
	@Override public void cellPressed(int x, int y){
		final int state = getState(x, y) + 1;
		final int size = colorList.size();
		setState(x, y, (state < size)? state : 0);
	}
}