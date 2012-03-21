/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.media.aquestalk;

/**
 *AquesTalk2呼び出しに起因する例外をラップします。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年5月21日
 */
public class AquesTalkException extends Exception{
	/**
	 *メッセージを指定して例外を生成します。
	 *@param msg 例外メッセージ
	 */
	public AquesTalkException(String msg){
		super(msg);
	}
	/**
	 *例外をラップする例外を生成します。
	 *@param ex ラップする例外
	 */
	public AquesTalkException(Exception ex){
		super(ex);
	}
}