/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.vm;

import java.util.ArrayList;

/**
 * 中間言語命令列を格納するリストです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/24
 *
 */
public class CodeList extends ArrayList<Object> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * リストを構築します。
	 */
	public CodeList() {
		super();
	}
	
	/**
	 * 中間言語命令列を配列に変換して返します。
	 * 
	 * @return 中間言語命令列
	 */
	public Object[] toCode() {
		return toArray();
	}

}
