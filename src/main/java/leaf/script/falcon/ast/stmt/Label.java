/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

/**
 * ジャンプ先を表現するラベルです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class Label {
	private final String name;
	private int jump;
	
	/**
	 * 名前を指定してラベルを構築します。
	 * 
	 * @param name ラベルの名前
	 */
	public Label(String name) {
		this.name = name;
	}
	
	/**
	 * 名前を指定してラベルを構築します。
	 * 
	 * @param name ラベルの名前
	 */
	public Label(LabelName name) {
		this.name = name.name();
	}
	
	/**
	 * ラベルの名前を返します。
	 * 
	 * @return ラベルの名前
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * ラベルのジャンプ先を返します。
	 * 
	 * @return ジャンプ先
	 */
	public int getJump() {
		return jump;
	}
	
	/**
	 * ラベルのジャンプ先を設定します。
	 * 
	 * @param jump ジャンプ先
	 */
	public void setJump(int jump) {
		this.jump = jump;
	}

}
