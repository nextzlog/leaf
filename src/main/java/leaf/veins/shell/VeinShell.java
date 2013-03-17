/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.shell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import leaf.shell.LeafShell;

/**
 * コマンドを管理・処理するシェルです。
 *
 */
public final class VeinShell extends LeafShell implements ActionListener {
	private static final VeinShell instance = new VeinShell();
	
	/**
	 * シェルを構築します。
	 */
	private VeinShell(){
		super();
	}
	
	/**
	 * シェルのインスタンスを返します。
	 *
	 * @return このクラスのインスタンス
	 */
	public static VeinShell getInstance(){
		return instance;
	}
	
	/**
	 *ボタンやメニューアイテムが選択された時にコマンドを呼び出します。
	 *
	 *@param e メソッドが受け取るイベント
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		call(e.getActionCommand());
	}

}
