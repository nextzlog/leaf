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

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * LeafAPIの全クラスの地域化を実装する{@link ResourceBundle}です。
 *
 * <pre>
 * 各ロケールの定義ファイルは以下のXMLフォーマットに従います。
 * &lt;?xml version="1.0" encoding="utf-8" ?&gt;
 * &lt;entry key="Hello" value="こんにちは"/&gt;
 * 定義ファイルはjar:file!/localize以下に配置します。
 * </pre>
 *
 * @author 東大アマチュア無線クラブ
 * @since 2011/06/18
 */
public final class Localizer extends java.util.ResourceBundle {
	private static final Cache cache = new Cache();
	private static Locale defaultLocale = Locale.ENGLISH;
	private final Class<?> type;
	private final Logger logger;
	private HashMap<String, String> table;
	private Locale locale;

	/**
	 * クラスを指定して{@link Localizer}のインスタンスを生成します。
	 *
	 * @param type 契約するクラス
	 */
	private Localizer(Class<?> type) {
		this.type = type;
		ConsoleHandler cons = new ConsoleHandler();
		cons.setFormatter(new LocalizerLogFormatter());
		logger = Logger.getLogger(getLocalizerName());
		logger.setUseParentHandlers(false);
		logger.addHandler(cons);
	}

	/**
	 * 指定されたクラスに対応する{@link Localizer}を返します。
	 *
	 * @param type 国際化するクラス
	 * @return {@link Localizer}のインスタンス
	 */
	public static Localizer getInstance(Class<?> type) {
		return cache.forClass(type);
	}

	/**
	 * {@link Localizer}が共通で使用する言語を返します。
	 *
	 * @return 設定されているロケール
	 */
	public static Locale getDefaultLocale() {
		return Localizer.defaultLocale;
	}

	/**
	 * {@link Localizer}が使用する言語を指定します。
	 *
	 * @param locale 設定するロケール
	 */
	public static void setDefaultLocale(Locale locale) {
		if (locale == null) {
			Localizer.defaultLocale = Locale.ENGLISH;
		}
		Localizer.defaultLocale = locale;
	}

	/**
	 * この{@link Localizer}の名前を返します。
	 *
	 * @return 名前
	 */
	private String getLocalizerName() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getCanonicalName());
		sb.append(" for ");
		sb.append(type.getCanonicalName());
		return sb.toString();
	}

	/**
	 * この{@link Localizer}が使用する言語を返します。
	 *
	 * @return 設定されているロケール
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

	/**
	 * 指定した{@link Locale}が利用可能か調べます。
	 *
	 * @param locale 調べるロケール
	 * @return 対応ファイルが用意されている場合true
	 */
	public boolean available(Locale locale) {
		try {
			return loadable(locale);
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * キーのリストを返します。
	 *
	 * @return キーのリスト
	 */
	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(table.keySet());
	}

	/**
	 * この{@link Localizer}に含まれるキーの{@link Set}を返します。
	 *
	 * @return キーのセット
	 */
	protected Set<String> handleKeySet() {
		return table.keySet();
	}

	/**
	 * このメソッドは{@link #translate(String)}と同等です。
	 *
	 * @param key キーとなる文字列
	 * @return 地域化された文字列
	 */
	@Override
	protected Object handleGetObject(String key) {
		return translate(key);
	}

	/**
	 * 指定したキーに対応する地域化された文字列を返します。
	 *
	 * @param key 検索用のキー
	 * @return ロケールに対応した文字列
	 */
	public String translate(String key) {
		if (!locale.equals(defaultLocale)
				&& !loadAndLog(defaultLocale)) {
			loadAndLog(Locale.ENGLISH);
		}
		String localized = table.get(key);
		if (localized != null) return localized;
		logger.warning("key not found:" + key);
		return "";
	}

	/**
	 * 指定したキーに対応するフォーマット済み文字列を返します。
	 *
	 * @param key  検索用のキー
	 * @param args 書式指示子により参照される引数
	 * @return ロケールに対応したフォーマット済み文字列
	 * @see java.util.Formatter
	 */
	public String translate(String key, Object... args) {
		try {
			return String.format(translate(key), args);
		} catch (IllegalFormatException ex) {
			logger.warning("illegal format : " + key);
			return "";
		}
	}

	private boolean loadAndLog(Locale locale) {
		try {
			load(locale);
			logger.fine("loaded successfully.");
			return true;
		} catch (ClassCastException ex) {
			logger.severe("not jar file:" + getPath(locale, "/"));
		} catch (Exception ex) {
			logger.warning("failed to load:" + ex);
		}
		table = new HashMap<>();
		return false;
	}

	private void load(Locale locale) throws Exception {
		this.locale = locale;
		String name = type.getSimpleName() + ".xml";
		table = new Parser().parse(getResourceAsStream(name));
	}

	private boolean loadable(Locale locale) throws IOException {
		String name = type.getSimpleName();
		URL curl = type.getResource(name + ".class");
		URLConnection conn = curl.openConnection();
		JarFile jar = ((JarURLConnection) conn).getJarFile();
		JarEntry dir = jar.getJarEntry(getPath(locale, "/"));
		return (dir != null && dir.isDirectory());
	}

	/**
	 * 指定されたリソースを読み込む{@link  InputStream}を返します。
	 * 地域化定義ファイルのあるディレクトリがルートに設定されます。
	 *
	 * @param name リソース名
	 * @return リソースを読み込むストリーム
	 * @throws IOException        取得に失敗した場合
	 * @throws ClassCastException 読み込み先がJarでない場合
	 */
	public InputStream getResourceAsStream(String name) throws IOException {
		this.locale = defaultLocale;
		String classfile = type.getSimpleName() + ".class";
		URL curl = type.getResource(classfile);
		URLConnection conn = curl.openConnection();
		URL root = ((JarURLConnection) conn).getJarFileURL();
		return URLClassLoader.newInstance(new URL[]{root})
				.getResourceAsStream(getPath(defaultLocale, "/") + name);
	}

	/**
	 * 指定されたリソースを読み込む{@link URL}を返します。
	 * 地域化定義ファイルのあるディレクトリがルートに設定されます。
	 *
	 * @param name リソース名
	 * @return リソースのURL
	 * @throws IOException        取得に失敗した場合
	 * @throws ClassCastException 読み込み先がJarでない場合
	 */
	public URL getResource(String name) throws IOException {
		this.locale = defaultLocale;
		String classfile = type.getSimpleName() + ".class";
		URL curl = type.getResource(classfile);
		URLConnection conn = curl.openConnection();
		URL root = ((JarURLConnection) conn).getJarFileURL();
		return URLClassLoader.newInstance(new URL[]{root})
				.getResource(getPath(defaultLocale, "/") + name);
	}

	/**
	 * 地域化定義ファイルのあるディレクトリへのパスを返します。
	 *
	 * @param locale    ロケール
	 * @param separator パスの区切り文字
	 * @return 言語セットの存在するディレクトリへのパス
	 */
	private String getPath(Locale locale, String separator) {
		String pack = type.getPackage().getName();
		StringBuilder path = new StringBuilder();
		path.append("localize");
		path.append(separator);
		path.append(locale.getISO3Language());
		path.append(separator);
		path.append(pack.replace(".", separator));
		return path.append('/').toString();
	}

	private static class Cache extends WeakHashMap
			<Class<?>, SoftReference<Localizer>> {
		public Localizer forClass(Class<?> type) {
			if (!containsKey(type)) return newInstance(type);
			Localizer loc = get(type).get();
			return loc != null ? loc : newInstance(type);
		}

		private Localizer newInstance(Class<?> type) {
			Localizer loc = new Localizer(type);
			loc.loadAndLog(Localizer.defaultLocale);
			put(type, new SoftReference<>(loc));
			return loc;
		}
	}

	private final class Parser extends DefaultHandler {
		private HashMap<String, String> table;

		public HashMap<String, String> parse(InputStream stream)
				throws Exception {
			this.table = new HashMap<String, String>();
			SAXParserFactory fact = null;
			try {
				fact = SAXParserFactory.newInstance();
				if (fact != null) {
					SAXParser parser = fact.newSAXParser();
					parser.parse(stream, this);
				}
				return this.table;
			} finally {
				if (stream != null) stream.close();
			}
		}

		@Override
		public void startElement
				(String uri, String local, String name, Attributes attr)
				throws SAXException {
			if (name != null && name.equals("entry")) {
				String key = attr.getValue("key");
				String value = attr.getValue("value");
				this.table.put(key, value);
			}
		}
	}

	private final class LocalizerLogFormatter extends Formatter {
		private final Date date = new Date();
		private final DateFormat format;

		public LocalizerLogFormatter() {
			format = DateFormat.getDateTimeInstance();
		}

		@Override
		public synchronized String format(LogRecord record) {
			StringBuilder sb = new StringBuilder();
			date.setTime(record.getMillis());
			sb.append(format.format(date));
			sb.append(' ');
			sb.append(record.getLevel().getLocalizedName());
			sb.append("\nat ");
			sb.append(getLocalizerName());
			sb.append(' ');
			sb.append(locale.getDisplayLanguage(Locale.ENGLISH));
			sb.append('\n');
			sb.append(super.formatMessage(record));
			return sb.append('\n').toString();
		}
	}

}
