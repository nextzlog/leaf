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

import leaf.util.hibernate.Properties;

/**
 * モジュールの管理を行うマネージャです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/07/13 
 *
 */
public final class ModuleManager {
	private final ModuleMap map;
	private static ModuleManager instance = new ModuleManager();
	
	private ModuleManager() {
		Properties prop = Properties.getInstance(getClass());
		map = prop.get("map", ModuleMap.class, new ModuleMap());
	}
	
	/**
	 * モジュールマネージャのインスタンスを返します。
	 * 
	 * @return マネージャのインスタンス
	 */
	public static ModuleManager getInstance() {
		return instance;
	}
	
	/**
	 * 登録されている全てのモジュールを起動します。
	 */
	public void loadAllModules() {
		for(String name : map.keySet()) {
			try {
				map.loadModule(name);
			} catch (IOException ex) {
			} catch (ModuleException ex) {
			}
		}
	}
	
	/**
	 * 指定された名前のモジュールのインスタンスを返します。
	 * 
	 * @param name モジュールの名前
	 * @return モジュールの本体
	 * @throws ModuleException モジュールが登録されていない場合
	 */
	public Module getModuleByName(String name) throws ModuleException {
		return map.getModule(name);
	}
	
	/**
	 * モジュールの管理情報をマップに追加します。
	 * 
	 * @param info 追加する管理情報
	 */
	void addModuleInfo(ModuleInfo info) {
		map.put(info.getName(), info);
	}
	
	/**
	 * モジュールの管理情報をマップから削除します。
	 * 
	 * @param info 削除する管理情報
	 */
	void removeModuleInfo(ModuleInfo info) {
		map.remove(info.getName());
	}
	
	/**
	 * マップに登録されている全てのモジュールの管理情報を返します。
	 * 
	 * @return 全ての管理情報
	 */
	ModuleInfo[] getAllModuleInfo() {
		ArrayList<ModuleInfo> list = new ArrayList<ModuleInfo>();
		for(String name : map.keySet()) {
			list.add(map.get(name));
		}
		return list.toArray(new ModuleInfo[0]);
	}
	
	/**
	 * URLで指定されたJARファイル内のモジュールを検索・ロードします。
	 * 
	 * @param url JARファイルのURL
	 * @throws IOException モジュールのロードに失敗した場合
	 */
	void findAndLoadModules(URL url) throws IOException {
		map.findAndLoadModule(url);
	}
	
	/**
	 * 全てのモジュールを終了します。
	 * 
	 * @return 全て終了した場合true
	 */
	public boolean shutdownAllModules() {
		for(String name : map.keySet()) {
			ModuleInfo info = map.get(name);
			if(!info.getModule().shutdown()) {
				return false;
			}
		}
		return true;
	}

}
