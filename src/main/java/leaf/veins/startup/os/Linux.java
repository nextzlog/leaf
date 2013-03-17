/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.startup.os;

import javax.swing.UIManager;

import leaf.util.hibernate.Properties;

final class Linux extends OS {
	@Override
	protected void startup() throws Exception{
		Properties prop = Properties.getInstance(getClass());
		String lnf = prop.get("lnf", OS.getNimbusLookAndFeelClassName());
		UIManager.setLookAndFeel(lnf);
	}

	@Override
	protected void exit() throws Exception {
		Properties prop = Properties.getInstance(getClass());
		prop.put("lnf", UIManager.getLookAndFeel().getClass().getCanonicalName());
	}
}
