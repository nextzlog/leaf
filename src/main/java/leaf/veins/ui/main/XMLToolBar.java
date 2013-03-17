/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JToolBar;

import leaf.shell.build.LeafMenuBuilder;
import leaf.shell.build.UnknownNameException;
import leaf.util.lang.LocalizeManager;
import leaf.veins.shell.VeinShell;

/**
 * 構造をXMLファイルに記述させるメニューバーです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/01/02 
 *
 */
@SuppressWarnings("serial")
public class XMLToolBar extends JToolBar {
	private final File dir;
	
	/**
	 * XMLを保管するディレクトリを指定してメニューバーを構築します。
	 * 
	 * @param dir XMLファイルを保管するディレクトリ
	 */
	public XMLToolBar(File dir) {
		this.dir = dir;
	}
	
	/**
	 * ツールバーをリソースファイルから読み込んで構築します。
	 *
	 * @throws UnknownNameException リソースの記述に問題がある場合
	 * @throws IOException リソースからの初期化に失敗した場合
	 */
	public void initialize() throws IOException, UnknownNameException {
		removeAll();
		
		String iso3 = LocalizeManager.getLocale().getISO3Language();
		File file = new File(dir, iso3 + ".xml");
		
		FileInputStream stream = new FileInputStream(file);
		new LeafMenuBuilder(VeinShell.getInstance()).build(this, stream);
	}
}
