/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.shell;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import leaf.shell.ButtonProvider;
import leaf.shell.MenuItemProvider;


/**
 * アプリケーションの全てのコマンドの基底実装です。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since 2011年8月31日
 */
public abstract class Command extends leaf.shell.Command
implements MenuItemProvider, ButtonProvider{
	
	private boolean isEnabled = true;
	private List<AbstractButton> buttons;
	
	/**
	 * コマンドの利用可能/不可能を切り替えます。
	 *
	 * @param enabled 利用可能な場合真 利用不可能な場合偽
	 */
	public void setEnabled(boolean enabled){
		if(buttons != null){
			for(int i=0; i<buttons.size(); i++){
				buttons.get(i).setEnabled(enabled);
			}
		}
		this.isEnabled = enabled;
	}
	
	/**
	 * このコマンドが利用可能であるか返します。
	 * 
	 * @return 利用可能な場合真 利用不可能な場合偽
	 */
	public boolean isEnabled() {
		return isEnabled;
	}
	
	/**
	 * このコマンドに対応するメニューアイテムを構築します。
	 *
	 * @param item メニューアイテム
	 * @return メニューアイテム
	 */
	@Override
	public JMenuItem createMenuItem(JMenuItem item) {
		if(buttons == null){
			buttons = new ArrayList<AbstractButton>();
		}
		buttons.add(item);
		item.setEnabled(isEnabled);
		
		item.addActionListener(VeinShell.getInstance());
		item.setActionCommand(getClass().getSimpleName());
		
		return item;
	}
	
	/**
	 * このコマンドに対応するボタンを構築します。
	 *
	 * @param button ボタン
	 * @return ボタン
	 */
	@Override
	public JButton createButton(JButton button) {
		if(buttons == null){
			buttons = new ArrayList<AbstractButton>();
		}
		buttons.add(button);
		button.setEnabled(isEnabled);
		
		button.addActionListener(VeinShell.getInstance());
		button.setActionCommand(getClass().getSimpleName());
		return button;
	}
}
