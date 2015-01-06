/**********************************************************************************
 * leaf - Java Library for pafelog
 * Copyright(c) 2010 - 2013 by University of Tokyo Amateur Radio Club
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL) as published by the
 * Free Software Foundation, either version 3 of the License, or(at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package leaf.feed;

import java.io.IOException;

/**
 * サポートされていない形式のフィードを読み込もうとした場合にスローされます。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2011年9月22日
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