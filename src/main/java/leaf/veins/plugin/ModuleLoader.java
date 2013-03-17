/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.plugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.jar.JarFile;

/**
 * URLで指定された場所に存在するモジュールを取得します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/06/16 
 *
 */
class ModuleLoader {
	private final String entry;
	private final URL url, jpath;
	private final URLClassLoader loader;
	
	protected final JarFile jarfile;
	
	/**
	 * 検索するJARファイルへのURLを指定します。
	 * 
	 * @param url JARファイルの位置またはそのエントリ
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public ModuleLoader(URL url) throws IOException {
		URLConnection conn = (this.url = url).openConnection();
		
		if(conn instanceof JarURLConnection){
			JarURLConnection jarconn = (JarURLConnection) conn;
			
			jpath = jarconn.getJarFileURL();
			entry   = jarconn.getEntryName();
			jarfile = jarconn.getJarFile();
			
			ClassLoader cl = Module.class.getClassLoader();
			loader  = new URLClassLoader(new URL[]{jpath}, cl);
		}
		
		else throw new IOException("not jar:" + url);
	}
	
	/**
	 * 指定した名前のエントリからモジュールを取得します。
	 * 
	 * @param entry エントリの名前
	 * @return エントリの指すモジュールを含む管理情報
	 */
	protected ModuleInfo loadModule(String entry){
		String cname = getClassName(entry);
		if(cname == null) return null;
		
		ModuleInfo info = null;
		try {
			Class<?> type = loader.loadClass(cname);
			if(!isModule(type)) return null;
			
			Method method = type.getMethod("newInstance");
			Module module = (Module) method.invoke(null);
			String dname = Module.getName(module);
			info = new ModuleInfo(getURL(entry), dname);
			info.setModule(module);
			module.start();
		} catch (IllegalAccessException ex) {
		} catch (ClassNotFoundException ex) {
		} catch (NoSuchMethodException ex) {
		} catch (ClassCastException ex) {
		} catch (SecurityException  ex) {
		} catch (IllegalArgumentException  ex) {
		} catch (InvocationTargetException ex) {
		}
		return info;
	}
	
	private boolean isModule(Class<?> type) {
		int mod = type.getModifiers();
		if(Modifier.isAbstract(mod)) return false;
		return Module.class.isAssignableFrom(type);
	}
	
	private String getClassName(String entry) {
		if(entry == null) return null;
		entry = entry.replace('/', '.');
		
		int dot = entry.lastIndexOf('.');
		if(dot < 0) return null;
		
		String suffix = entry.substring(dot + 1);
		if(suffix.equals("class")) {
			return entry.substring(0, dot);
		}
		return null;
	}
	
	private String getURL(String entry) {
		return "jar:" + jpath + "!/" + entry;
	}

	/**
	 * コンストラクタで指定したモジュールをロードします。
	 * 
	 * @return モジュールのインスタンスを含む管理情報
	 * 
	 * @throws IOException モジュールが見つからなかった場合
	 */
	public ModuleInfo load() throws IOException {
		ModuleInfo info = loadModule(entry);
		if(info != null) return info;
		
		throw new IOException("not found:" + url);
	}

}
