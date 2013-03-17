/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.startup.os;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * 起動時にOS毎のネイティブ設定を適用するための基底クラスです。
 *
 * @since 2011年12月12日
 */
public abstract class OS {
	
	/**
	 * 初期設定動作を実行します。
	 *
	 * @throws Exception 何らかの例外
	 */
	protected abstract void startup() throws Exception;
	
	/**
	 * 終了時動作を実行します。
	 * 
	 * @throws Exception
	 */
	protected abstract void exit() throws Exception;
	
	/**
	 * 指定されたOS向けの実装を呼び出して初期設定動作を実行します。
	 *
	 * @param name OS名
	 */
	public static void startup(String name){
		final String pack = OS.class.getPackage().getName();
		name = pack.concat(".").concat(name.replaceAll(" ", ""));
		
		try {
			((OS)Class.forName(name).newInstance()).startup();
		} catch (Exception e) {
		}
	}
	
	/**
	 * 指定されたOS向けの実装を呼び出して終了時動作を実行します。
	 *
	 * @param name OS名
	 */
	public static void exit(String name){
		final String pack = OS.class.getPackage().getName();
		name = pack.concat(".").concat(name.replaceAll(" ", ""));
		
		try {
			((OS)Class.forName(name).newInstance()).exit();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Nimbusルックアンドフィールのクラス名を返します。
	 * 
	 * @return Nimbusがインストールされていない場合null
	 */
	public static String getNimbusLookAndFeelClassName() {
		for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if(info.getName().equals("Nimbus")) return info.getClassName();
		}
		return null;
	}

}
