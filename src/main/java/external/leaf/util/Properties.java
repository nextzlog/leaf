/**********************************************************************************
 * leaf - Java Library for pafelog
 * Copyright(c) 2010 - 2013 by University of Tokyo Amateur Radio Club
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL) as published by the
 * Free Software Foundation, either version 3 of the License, or(at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package leaf.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.WeakHashMap;

/**
 * オブジェクトを永続化しユーザー毎に一括管理します。
 * usersディレクトリ直下にcfgファイルが作成されます。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2011年12月17日
 * 
 */
public final class Properties implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final File dir;
	private static final String user;
	private static final Cache cache;
	private static final Hibernate hibernate;
	
	static {
		cache = new Cache();
		dir = new File("users");
		user = System.getProperty("user.name");
		hibernate = new Hibernate(dir, user);
	}
	
	private final String prefix;
	
	Properties(Class<?> type) {
		prefix = type.getName().concat(".");
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
	 * @param <T> マッピングされたオブジェクトの総称型
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
	
	/**
	 * 指定したクラスのプロパティオブジェクトを取得します。
	 *
	 * @param type プロパティを委託するクラス
	 * @return 対応するオブジェクト
	 */
	public static Properties getInstance(Class<?> type) {
		return cache.forClass(type);
	}
	
	private static class Cache extends WeakHashMap
	<Class<?>, SoftReference<Properties>> {
		public Properties forClass(Class<?> type) {
			if(!containsKey(type)) return create(type);
			Properties p = get(type).get();
			return p != null ? p : create(type);
		}
		
		private Properties create(Class<?> type) {
			Properties p = new Properties(type);
			put(type, new SoftReference<>(p));
			return p;
		}
	}
	
	/**
	 * 過去にローカルに保存された回数を返します。
	 * 
	 * @return 保存された回数
	 */
	public static int getSaveCount() {
		Properties p = Properties.getInstance(Properties.class);
		return p.get("saveCount", 0);
	}
	
	/**
	 * ローカルに最新版が保存されているか確認します。
	 * 
	 * @return 最新版が保存されている場合true
	 */
	public static boolean isSaved() {
		return hibernate.isEqual(new Hibernate(dir, user));
	}
	
	/**
	 * ローカルに最新版を保存します。
	 * 
	 * @return 保存に成功した場合true
	 */
	public static boolean save() {
		Properties p = Properties.getInstance(Properties.class);
		p.put("saveCount", getSaveCount() + 1);
		try {
			if(!dir.isDirectory()) dir.mkdirs();
			hibernate.save(dir);
			return true;
		} catch (SecurityException ex) {
			ex.printStackTrace();
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
