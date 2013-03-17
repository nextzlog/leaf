/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.plugin;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.net.URL;
import java.util.TreeMap;

/**
 * モジュールの管理情報を保管し、ローカルに保存するためのマップです。
 * 
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/06/16 
 *
 */
public final class ModuleMap extends TreeMap<String, ModuleInfo> {
	private static final long serialVersionUID = 3797078950621805327L;
	
	/**
	 * この{@link ModuleMap}が設定ファイルに出力しないプロパティを設定します。
	 * 
	 * @param name プロパティ名
	 * @throws IntrospectionException 
	 */
	static void setTransient(String name) {
		try {
			BeanInfo beaninfo = Introspector.getBeanInfo(ModuleInfo.class);
			for(PropertyDescriptor pd : beaninfo.getPropertyDescriptors())
				if(pd.getName().equals(name)) pd.setValue("transient", true);
		} catch (IntrospectionException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * URLで指定されたJARファイル内にあるモジュールを検索してロードします。
	 * 
	 * @param url JARファイルのURL
	 * @throws IOException モジュールのロードに失敗した場合
	 */
	public void findAndLoadModule(URL url) throws IOException {
		url = new URL("jar:" + url + "!/");
		for(ModuleInfo info : new ModuleFinder(url).search()) {
			put(info.getName(), info);
		}
	}
	
	/**
	 * 指定された名前のモジュールをロードします。
	 * 
	 * @param name モジュールの名前
	 * @return モジュールのインスタンス
	 * @throws IOException モジュールのロードに失敗した場合
	 * @throws ModuleException モジュールが登録されていない場合
	 */
	public Module loadModule(String name) throws IOException, ModuleException {
		if(containsKey(name)) {
			ModuleInfo info = new ModuleLoader(get(name).getLocationURL()).load();
			put(name, info);
			return info.getModule();
		}
		throw new ModuleException(String.format("Module '%s' not found", name));
	}
	
	/**
	 * 指定された名前のロード済みモジュールを返します。
	 * 
	 * @param name モジュールの名前
	 * @return モジュールのインスタンス ロード済みでない場合null
	 * @throws ModuleException モジュールが登録されていない場合
	 */
	public Module getModule(String name) throws ModuleException {
		if(containsKey(name)) return get(name).getModule();
		throw new ModuleException(String.format("Module '%s' not found", name));
	}

}
