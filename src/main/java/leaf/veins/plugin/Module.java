/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.plugin;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.swing.JDialog;

/**
 * フレームワーク上で動作するモジュールの基底クラスです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/06/16 
 *
 */
public abstract class Module implements Pluginable {
	/**
	 * {@link ModuleLoader}がインスタンス化に使用します。
	 * 
	 * @return モジュールのインスタンス
	 */
	public static Module newInstance() {
		StackTraceElement ste = null;
		try {
			throw new IllegalAccessException();
		} catch (IllegalAccessException ex) {
			ste = ex.getStackTrace()[0];
			String method = ste.getMethodName();
			System.err.print("static method '");
			System.err.print(method);
			System.err.println("' not implemented");
		}
		return null;
	}
	
	/**
	 * 指定されたモジュールの名前を取得して返します。
	 * 
	 * @param module モジュール
	 * @return モジュールの名前
	 */
	public static final String getName(Module module) {
		Class<?> type = module.getClass();
		if(!Modifier.isAbstract(type.getModifiers())) {
			try {
				Method method = type.getMethod("getName");
				return (String) method.invoke(module);
			} catch (SecurityException ex) {
			} catch (NoSuchMethodException ex) {
			} catch (IllegalArgumentException ex) {
			} catch (IllegalAccessException ex) {
			} catch (InvocationTargetException ex) {
			}
		}
		
		return null;
	}
	
	/**
	 * このメソッドはデフォルトでfalseを返します。
	 * 
	 * @return 設定ダイアログを提供する場合true
	 */
	@Override
	public boolean hasConfigurationDialog() {
		return false;
	}
	
	/**
	 * このメソッドはデフォルトでnullを返します。
	 * 
	 * @param p 所有者となるウィンドウ
	 * @return 設定ダイアログ
	 */
	@Override
	public JDialog createConfigurationDialog(Window p) {
		return null;
	}

}
