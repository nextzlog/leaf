/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.feed;

import java.io.IOException;

/**
 * サポートされていない形式のフィードを読み込もうとした場合にスローされます。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年9月22日
 */
public class UnknownFormatException extends IOException {
	private static final long serialVersionUID = 1L;

	/**
	 * 指定された詳細メッセージを持つ例外を生成します。
	 * 
	 * @param msg 例外の内容を説明するメッセージ
	 */
	public UnknownFormatException(String msg) {
		super(msg);
	}

}