/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.feed;

import java.io.IOException;

/**
 *サポートされていない形式のフィードを読み込もうとした場合にスローされます。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年9月22日
 */
public class UnknownFormatException extends IOException{
	/**
	 *指定された詳細メッセージを持つ例外を生成します。
	 *
	 *@param msg 例外の内容を説明するメッセージ
	 */
	public UnknownFormatException(String msg){
		super(msg);
	}
}