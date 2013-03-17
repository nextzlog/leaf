/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.plugin;

/**
 * モジュールのロードに失敗した場合にスローされます。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/06/16 
 *
 */
public class ModuleException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * 指定されたメッセージを持つ例外を構築します。
	 * 
	 * @param msg 例外の内容を説明する文字列
	 */
	public ModuleException(String msg){
		super(msg);
	}

}
