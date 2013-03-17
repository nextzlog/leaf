/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.hibernate;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * アプリケーションの永続オブジェクトを管理します。
 *
 * @since 2011年12月17日
 */
public final class Hibernate implements Serializable{
	private static final long serialVersionUID = -5623672660782062166L;
	
	/**
	 * システムのデフォルトのハイバーネーション名です。
	 */
	public static final String DEFAULT_HIBERNATE_NAME = "DEFAULT_HIBERNATE";
	
	private final String name;
	private Map<String, Object> table;
	private final static WeakHashMap<String, Hibernate> instances
		= new WeakHashMap<String, Hibernate>();
	
	/**
	 * 場所と名前を指定してハイバーネーションオブジェクトを取得します。
	 *
	 * @param dir データの保存場所
	 * @param name ハイバーネーションの名前
	 * @return 対応するオブジェクト
	 */
	public static Hibernate getInstance(File dir, String name) {
		Hibernate instance = instances.get(name);
		if(instance != null) return instance;
		instances.put(name, instance = new Hibernate(dir, name));
		return instance;
	}
	
	/**
	 * ハイバーネーションの名前を指定してオブジェクトを構築します。
	 *
	 * @param dir データの保存場所
	 * @param name このハイバーネーションの名前
	 */
	private Hibernate(File dir, String name) {
		this.name = name.concat(".xml");
		try {
			table = load(dir);
		} catch (IOException ex) {
			table = new HashMap<String, Object>();
		}
	}
	
	/**
	 * ハイバーネーションの名前に対応するファイルからデータを読み込みます。
	 *
	 * @param dir ファイルの存在するディレクトリ
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> load(File dir) throws IOException{
		FileInputStream fstream = null;
		BufferedInputStream bstream = null;
		XMLDecoder decoder = null;
		
		try {
			fstream = new FileInputStream(new File(dir, name));
			bstream = new BufferedInputStream(fstream);
			decoder = new XMLDecoder(bstream);
			
			return (Map<String, Object>)decoder.readObject();
		} catch (RuntimeException ex) {
			throw new IOException("Illegal XML file");
		} finally {
			if(decoder!=null)decoder.close();
		}
	}
	
	/**
	 * ハイバーネーションの名前に対応するファイルにデータを保存します。
	 *
	 * @param dir 保存先のディレクトリ
	 * @throws FileNotFoundException ファイルが利用できない場合
	 */
	public void save(File dir) throws FileNotFoundException{
		FileOutputStream fstream = null;
		BufferedOutputStream bstream = null;
		XMLEncoder encoder = null;
		
		try {
			fstream = new FileOutputStream(new File(dir, name));
			bstream = new BufferedOutputStream(fstream);
			encoder = new XMLEncoder(bstream);
			encoder.writeObject(table);
		} finally {
			if(encoder!=null)encoder.close();
		}
	}
	
	/**
	 * 指定された名前に対しオブジェクトをマッピングします。
	 *
	 * @param name オブジェクトの名前
	 * @param obj マッピングするオブジェクト
	 */
	public void put(String name, Object obj) {
		table.put(name, obj);
	}
	
	/**
	 * 指定した名前と型に対応するオブジェクトを返します。
	 *
	 * @param <T> オブジェクトの型
	 * @param name オブジェクトの名前
	 * @param type オブジェクトの型
	 * @return マッピングされたオブジェクト
	 *
	 * @throws ClassCastException 型が不適合の場合
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String name, Class<T> type) {
		return (T) table.get(name);
	}
	
	/**
	 * 指定した名前に対応するオブジェクトを返します。
	 * 
	 * @param name オブジェクトの名前
	 * @return マッピングされたオブジェクト
	 */
	public Object get(String name) {
		return table.get(name);
	}
	
	/**
	 * 指定した名前に対応するオブジェクトを削除します。
	 * 
	 * @param name オブジェクトの名前
	 * @return 削除されたオブジェクト
	 */
	public Object remove(String name) {
		return table.remove(name);
	}
	
	/**
	 * 指定した名前に対応するオブジェクトが存在するか返します。
	 *
	 * @param name オブジェクトの名前
	 * @return 対応するオブジェクトが存在する場合真
	 */
	public boolean contains(String name) {
		return table.containsKey(name);
	}

}
