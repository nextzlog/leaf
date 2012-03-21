/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.csv;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *CSV形式における「行」の表現です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年9月16日
 */
public class Row implements Serializable{
	private final ArrayList<Cell> list;
	/**
	 *空の行リストを生成します。
	 */
	public Row(){
		list = new ArrayList<Cell>();
	}
	/**
	 *指定したセルを行リストの末尾に追加します。
	 *
	 *@param cell 追加するセル
	 */
	void add(Cell cell){
		list.add(cell);
	}
	/**
	 *指定したセルを行リストに配置します。
	 *
	 *@param column セルの列
	 *@param cell 配置するセル
	 *@return 以前配置されていたセル
	 *@throws IndexOutOfBoundsException 行、列が負の場合
	 */
	public Cell put(int column, Cell cell){
		checkColumn(column);
		if(column < list.size()) return list.set(column, cell);
		else for(int i=list.size(); i<column; i++){
			list.add((i == column)? cell : new Cell(""));
		}
		return null;
	}
	/**
	 *指定した位置のセルを返します。
	 *
	 *@param column セルの列
	 *@return 指定した位置のセル 範囲外の場合null
	 *@throws IndexOutOfBoundsException 列が負の場合
	 */
	public Cell get(int column){
		checkColumn(column);
		return (column<list.size())? list.get(column) : null;
	}
	/**
	 *指定された位置が正当な位置であるか確認します。
	 *@param column セルの列
	 *@throws IndexOutOfBoundsException 負の場合
	 */
	private void checkColumn(int column){
		if(column >= 0) return;
		throw new IndexOutOfBoundsException(
		String.format("column(%d) must be non negative", column));
	}
	/**
	 *行リストの列数を返します。
	 *
	 *@return 行リストの列数
	 */
	public int getColumnCount(){
		return list.size();
	}
	/**
	 *行リストの内容をエスケープ表現を含まないCSV形式で出力します。
	 *
	 *@return CSV形式での文字列表現 行リストが空なら空の文字列
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		final int size = list.size();
		for(int i=0; i<size-1; i++){
			sb.append(list.get(i)).append(',');
		}
		if(size>0) sb.append(list.get(size-1));
		return sb.toString();
	}
	/**
	 *行リストの内容をエスケープされたCSV形式で出力します。
	 *
	 *@return CSV形式での正当な文字列表現 行リストが空なら空の文字列
	 */
	public String canonical(){
		StringBuilder sb = new StringBuilder();
		final int size = list.size();
		for(int i=0; i<size-1; i++){
			sb.append(list.get(i).canonical()).append(',');
		}
		if(size>0) sb.append(list.get(size-1).canonical());
		return sb.toString();
	}
	/**
	 *行リストのデータを全て消去します。
	 */
	public void clear(){
		list.clear();
		list.trimToSize();
	}
}