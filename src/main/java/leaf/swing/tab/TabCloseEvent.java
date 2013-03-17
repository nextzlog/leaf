/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.tab;

import java.awt.Component;
import java.util.EventObject;

public class TabCloseEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private final Component component;

	/**
	 * イベントの発生源と閉じられるタブを指定してイベントを構築します。
	 * 
	 * @param source イベントの発生源
	 * @param comp このイベントの後に閉じられるコンポーネント
	 */
	public TabCloseEvent(Object source, Component comp) {
		super(source);
		this.component = comp;
	}
	
	/**
	 * このイベントの後に閉じられるコンポーネントを返します。
	 * 
	 * @return コンポーネント
	 */
	public Component getComponent() {
		return component;
	}

}