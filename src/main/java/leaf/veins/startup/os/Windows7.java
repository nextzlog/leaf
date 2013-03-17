/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.startup.os;

import javax.swing.UIManager;

import leaf.swing.menu.ShadowedMenuUI;
import leaf.util.hibernate.Properties;

final class Windows7 extends OS {
	@Override
	public void startup() throws Exception{
		Properties prop = Properties.getInstance(getClass());
		String lnf = prop.get("lnf", OS.getNimbusLookAndFeelClassName());
		
		UIManager.setLookAndFeel(lnf);
		UIManager.put("PopupMenuUI", ShadowedMenuUI.class.getName());
	}
	
	@Override
	protected void exit() throws Exception {
		Properties prop = Properties.getInstance(getClass());
		prop.put("lnf", UIManager.getLookAndFeel().getClass().getCanonicalName());
	}
}
