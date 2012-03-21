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

/**
 *CSV形式における「セル」の表現です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年9月16日
 */
public class Cell implements Serializable{
	private final String value;
	/**
	 *値を指定してセルを生成します。
	 *
	 *@param value セルに設定する文字列値
	 */
	public Cell(Object value){
		this.value = String.valueOf(value);
	}
	/**
	 *セルの値をエスケープ表現を含まないCSV形式で出力します。
	 *
	 *@return CSV形式での文字列表現
	 */
	public String toString(){
		return value;
	}
	/**
	 *セルの値をエスケープされたCSV形式で出力します。
	 *
	 *@return CSV形式での正当な文字列表現
	 */
	public String canonical(){
		StringBuilder sb = new StringBuilder("\"");
		boolean hasEscapeSequence = false;
		final int length = value.length();
		for(int i=0; i<length; i++){
			char ch = value.charAt(i);
			sb.append(ch);
			switch(ch){
				case '"': sb.append('"'); //fallthrough
				case ',': hasEscapeSequence = true;
			}
		}
		if(hasEscapeSequence){
			return sb.append('"').toString();
		}else return sb.substring(1);
	}
}