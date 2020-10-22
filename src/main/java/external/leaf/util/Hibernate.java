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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * オブジェクトマッピングを永続化するための実装です。
 *
 * @author 東大アマチュア無線クラブ
 * @since 2013/05/04
 */
final class Hibernate implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private Map<String, Object> table;

	public Hibernate(File dir, String name) {
		this.name = name.concat(".cfg");
		try {
			table = load(dir);
		} catch (IOException ex) {
			table = new HashMap<String, Object>();
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> load(File dir) throws IOException {
		File file = new File(dir, name);
		XMLDecoder decoder = null;
		try {
			FileInputStream fin = new FileInputStream(file);
			decoder = new XMLDecoder(new GZIPInputStream(fin));
			return (Map<String, Object>) decoder.readObject();
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new IOException("Illegal XML format: " + file);
		} finally {
			if (decoder != null) decoder.close();
		}
	}

	/**
	 * ハイバーネーションの内容をローカルのファイルに保存します。
	 *
	 * @param dir 保存先のディレクトリ
	 * @throws IOException ファイルが利用できない場合
	 */
	public final void save(File dir) throws IOException {
		File file = new File(dir, name);
		XMLEncoder encoder = null;
		try {
			FileOutputStream fout = new FileOutputStream(file);
			encoder = new XMLEncoder(new GZIPOutputStream(fout));
			encoder.writeObject(table);
		} finally {
			if (encoder != null) encoder.close();
		}
	}

	/**
	 * 指定された{@link Hibernate}と内容が同じであるか確認します。
	 *
	 * @param with 確認する対象
	 * @return 内容が同じ場合true
	 */
	boolean isEqual(Hibernate with) {
		return this.table.equals(with.table);
	}

	/**
	 * 指定された名前に対しオブジェクトをマッピングします。
	 *
	 * @param name オブジェクトの名前
	 * @param obj  マッピングするオブジェクト
	 */
	public void put(String name, Object obj) {
		table.put(name, obj);
	}

	/**
	 * 指定した名前と型に対応するオブジェクトを返します。
	 *
	 * @param <T>  オブジェクトの型
	 * @param name オブジェクトの名前
	 * @param type オブジェクトの型
	 * @return マッピングされたオブジェクト
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
