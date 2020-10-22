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
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.jar.JarFile;

/**
 * JARファイル内に格納するリソースを読み込むためのユーティリティです。
 *
 * @author 東大アマチュア無線クラブ
 */
public final class Resources {
	private final URL url;
	private final JarFile jar;
	private final URLClassLoader loader;

	/**
	 * クラスを格納するJARファイルの{@link Resources}を構築します。
	 *
	 * @param c JARファイルに格納されているクラス
	 * @throws IOException クラスがJARファイル内にない場合
	 */
	public Resources(Class<?> c) throws IOException {
		URL curl = c.getResource(c.getSimpleName() + ".class");
		URLConnection uc = curl.openConnection();
		jar = ((JarURLConnection) uc).getJarFile();
		url = ((JarURLConnection) uc).getJarFileURL();
		ClassLoader parent = getClass().getClassLoader();
		loader = new URLClassLoader(new URL[]{url}, parent);
	}

	/**
	 * 指定されたクラスが格納されているJARファイルを返します。
	 *
	 * @param c クラス
	 * @return JARファイルへのパス JARでなければnull
	 */
	public static File getJarFileOf(Class<?> c) {
		try {
			return new File(new Resources(c).getJarFile().getName());
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * 指定されたクラスのJARファイルのあるディレクトリを返します。
	 *
	 * @param c クラス
	 * @return JARファイルが配置されている場所 JARでなければnull
	 */
	public static File getJarFileDir(Class<?> c) {
		File jarfile = getJarFileOf(c);
		return jarfile != null ? jarfile.getParentFile() : null;
	}

	/**
	 * JARファイルへのパスを表すURLを返します。
	 *
	 * @return JARファイルのURL
	 */
	public URL getJarURL() {
		return url;
	}

	/**
	 * {@link JarFile}オブジェクトを返します。
	 *
	 * @return JarFile
	 */
	public JarFile getJarFile() {
		return jar;
	}

	/**
	 * JARファイル内を探索する{@link ClassLoader}を返します。
	 *
	 * @return クラスローダー
	 */
	public ClassLoader getClassLoader() {
		return loader;
	}

	/**
	 * 指定したパスのエントリを読み込む{@link InputStream}を返します。
	 *
	 * @param path JARファイルを起点とするJARファイル内のパス
	 * @return InputStream
	 * @throws IOException パスに対応するエントリが存在しない場合
	 */
	public InputStream getInputStream(String path) throws IOException {
		return jar.getInputStream(jar.getEntry(path));
	}

	/**
	 * 指定したパスのエントリを表すURLを返します。
	 *
	 * @param path JARファイルを起点とするJARファイル内のパス
	 * @return pathに対応するURL
	 */
	public URL getURL(String path) {
		return loader.getResource(path);
	}

}
