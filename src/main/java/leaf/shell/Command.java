/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.shell;

/**
 * シェルに統合される各種コマンドの基底クラスです。
 *
 * @author 東大アマチュア無線クラブ
 * @since 2011年8月31日
 */
public abstract class Command {
	/**
	 * このオブジェクトが担当するコマンドの名前を返します。
	 * デフォルトでは、クラスの単純名をコマンド名とします。
	 *
	 * @return コマンド名(クラスの単純名)
	 */
	public String getName() {
		return getClass().getSimpleName();
	}
	/**
	 * このオブジェクトが担当するコマンドを処理します。
	 *
	 * @param args コマンドへの引数
	 * @throws Exception この処理が発生しうる例外
	 */
	public abstract void process(Object... args) throws Exception;
}
