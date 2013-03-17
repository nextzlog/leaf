/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.hibernate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import leaf.util.hibernate.Hibernate;

/**
 * アプリケーションの永続オブジェクトをユーザー毎に一括管理します。
 * .resource/config/users以下にユーザー毎のファイルが配置されます。
 *
 * @since 2011年12月17日
 */
public final class Properties {
	private static File dir;
	private static final Hibernate hibernate;
	private final static Map<Class<?>, Properties> instances;
	
	static {
		instances = new HashMap<Class<?>, Properties>();
		dir = new File("resource");
		dir = new File(dir, "config");
		dir = new File(dir, "users");
		String user = System.getProperty("user.name");
		hibernate = Hibernate.getInstance (dir, user);
	}
	
	private final String prefix;
	
	private Properties(Class<?> type) {
		prefix = type.getName().concat(".");
	}
	
	/**
	 * 対応するクラスを指定してプロパティオブジェクトを取得します。
	 *
	 * @param type プロパティを委託するクラス
	 * @return 対応するオブジェクト
	 */
	public static Properties getInstance(Class<?> type) {
		Properties instance = instances.get(type);
		if(instance != null) return instance;
		instances.put(type, instance = new Properties(type));
		return instance;
	}
	
	/**
	 * ハイバーネーションファイルにデータを保存します。
	 */
	public static void save() {
		try {
			if(!dir.isDirectory()) dir.mkdirs();
			hibernate.save(dir);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 指定された名前に対しオブジェクトをマッピングします。
	 *
	 * @param name オブジェクトの名前
	 * @param obj マッピングするオブジェクト
	 */
	public void put(String name, Object obj) {
		hibernate.put(prefix.concat(name), obj);
	}
	
	/**
	 * 指定した名前と型に対応するオブジェクトを返します。
	 *
	 * @param <T> オブジェクトの型
	 * @param name オブジェクトの名前
	 * @param type オブジェクトの型
	 * @param defo デフォルトの代用値
	 *
	 * @return マッピングされたオブジェクト
	 *
	 * @throws ClassCastException 型が不適合の場合
	 */
	public <T> T get(String name, Class<T> type, T defo) {
		name = prefix.concat(name);
		if(!hibernate.contains(name)) {
			hibernate.put(name, defo);
			return defo;
		} else return hibernate.get(name, type);
	}
	
	/**
	 * 指定した名前と初期値に対応するオブジェクトを返します。
	 * 
	 * @param name オブジェクトの名前
	 * @param defo デフォルトの代用値
	 * 
	 * @return マッピングされたオブジェクト
	 * 
	 * @throws ClassCastException 型が不適合の場合
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String name, T defo) {
		name = prefix.concat(name);
		if(!hibernate.contains(name)) {
			hibernate.put(name, defo);
			return defo;
		} else {
			return (T) hibernate.get(name);
		}
	}
	
	/**
	 * 指定した名前に対応するオブジェクトを削除します。
	 * 
	 * @param name オブジェクトの名前
	 * @return 削除されたオブジェクト
	 */
	public Object remove(String name) {
		name = prefix.concat(name);
		return hibernate.remove(name);
	}

}