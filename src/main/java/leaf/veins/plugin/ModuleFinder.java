/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.plugin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * URLで指定された場所に存在するモジュールを検索します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/06/16 
 *
 */
final class ModuleFinder extends ModuleLoader {

	/**
	 * 検索するJARファイルへのURLを指定します。
	 * 
	 * @param url JARファイルの位置
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public ModuleFinder(URL url) throws IOException {
		super(url);
	}
		
	/**
	 * コンストラクタで指定された位置からモジュールを検索します。
	 * 
	 * @return 検出されたモジュールの管理情報のリスト
	 */
	public List<ModuleInfo> search() {
		List<ModuleInfo> list = new ArrayList<ModuleInfo>();
		Enumeration<JarEntry> e = super.jarfile.entries();
		
		while(e.hasMoreElements()){
			JarEntry entry = e.nextElement();
			if(!entry.isDirectory()) {
				ModuleInfo info = loadModule(entry.getName());
				if(info != null) list.add(info);
			}
		}
		
		return list;
	}

}
