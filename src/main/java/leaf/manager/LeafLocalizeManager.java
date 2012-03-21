/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.manager;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;
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
 *LeafAPIの全クラスの多言語化を支援する専用マネージャです。
 *言語セットは以下のXMLフォーマットに従います。
 *<br><br>
 *&lt;?xml version="1.0" encoding="文字コード名" ?&gt;<br>
 *&lt;entry key="Hello" value="Hello, World"/&gt;
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年6月18日
 */
public final class LeafLocalizeManager{
	private HashMap<String, String> table;
	private Locale mylocale;
	private final Class clazz;
	private final Logger logger;
	private final String name;
	private static Locale locale = Locale.ENGLISH;
	private static WeakHashMap<Class, LeafLocalizeManager> map;
	private static final HashMap<String, File> roots;
	
	static{
		roots = new HashMap<String, File>();
		map = new WeakHashMap<Class, LeafLocalizeManager>();
	}
	
	/**
	 *指定されたクラスに対応するマネージャを返します。
	 *
	 *@param clazz 国際化するクラス
	 *@return マネージャのインスタンス
	 */
	public static LeafLocalizeManager getInstance(Class clazz){
		LeafLocalizeManager instance = map.get(clazz);
		if(instance == null){
			instance = new LeafLocalizeManager(clazz);
			instance.loadAndLog(LeafLocalizeManager.locale);
			map.put(clazz, instance);
		}
		return instance;
	}
	/**
	 *クラスを指定してマネージャのインスタンスを生成します。
	 *
	 *@param clazz 契約するクラス
	 */
	private LeafLocalizeManager(Class clazz){
		name = getClass().getName()+" for "+clazz.getName();
		ConsoleHandler cons = new ConsoleHandler();
		cons.setFormatter(new LogFormatter());
		logger = Logger.getLogger(name);
		logger.setUseParentHandlers(false);
		logger.addHandler(cons);
		this.clazz = clazz;
	}
	/**
	 *マネージャが使用する言語を指定します。
	 *
	 *@param locale 設定する言語ロケール
	 */
	public static void setLocale(Locale locale){
		if(locale == null){
			LeafLocalizeManager.locale = Locale.ENGLISH;
		}
		LeafLocalizeManager.locale = locale;
	}
	/**
	 *マネージャが使用する言語を返します。
	 *
	 *@return 設定されている言語ロケール
	 */
	public static Locale getLocale(){
		return LeafLocalizeManager.locale;
	}
	/**
	 *指定したロケールがサポートされているか調べます。
	 *
	 *@param locale 調べるロケール
	 *@return 対応ファイルが用意されている場合true
	 */
	public boolean available(Locale locale){
		final String iso3 = locale.getISO3Language();
		File root = roots.get(clazz.getPackage().getName());
		if(root == null) try{
			String name = clazz.getSimpleName();
			URL curl = clazz.getResource(name + ".class");
			URLConnection conn = curl.openConnection();
			JarFile jar = ((JarURLConnection)conn).getJarFile();
			JarEntry dir = jar.getJarEntry("localize/" + iso3);
			return (dir != null && dir.isDirectory());
		}catch(Exception ex){
			return false;
		}
		String path = "localize" + File.separator + iso3;
		return new File(root, path).canRead();
	}
	/**
	 *指定したキーに対応するローカライズされた文字列を返します。
	 *
	 *@param key 検索用のキー
	 *@return ロケールに対応した文字列
	 */
	public String translate(String key){
		if(!mylocale.equals(locale)){
			if(!loadAndLog(locale)) loadAndLog(Locale.ENGLISH);
		}
		String localized = table.get(key);
		if(localized != null) return localized;
		logger.warning("key not found:" + key);
		return "";
	}
	/**
	 *指定したキーに対応するフォーマット済み文字列を返します。
	 *
	 *@param key 検索用のキー
	 *@param args 書式指示子により参照される引数
	 *@return ロケールに対応したフォーマット済み文字列
	 *@see java.util.Formatter
	 */
	public String translate(String key, Object... args){
		try{
			return String.format(translate(key), args);
		}catch(IllegalFormatException ex){
			logger.warning("illegal format : " + key);
			return "";
		}
	}
	/**
	 *エラーログ付で言語セットを読み込みます。
	 *
	 *@param locale 読み込む言語に対応するロケール
	 *@return 読み込みに成功した場合true
	 */
	private boolean loadAndLog(Locale locale){
		try{
			load(locale);
			logger.fine("loaded successfully.");
			return true;
		}catch(ClassCastException ex){
			logger.severe("not jar file:" + getDir(locale, "/"));
		}catch(Exception ex){
			logger.warning("failed to load:" + ex);
		}
		return false;
	}
	/**
	 *必要とされる時点で言語セットを読み込みます。
	 *
	 *@param locale 読み込む言語に対応するロケール
	 *@throws SAXException
	 *@throws IOException 読み込みに失敗した場合
	 *@throws ClassCastException 読み込み先がJarでない場合
	 */
	private void load(Locale locale) throws Exception{
		this.mylocale = locale;
		String name = clazz.getSimpleName() + ".xml";
		table = new DataParser().parse(getResource(name));
	}
	/**
	 *指定されたリソースの読み込みストリームを返します。
	 *@param name リソース名
	 *@return リソースを読み込むストリーム
	 *@throws IOException 取得に失敗した場合
	 *@throws ClassCastException 読み込み先がJarでない場合
	 */
	public InputStream getResource(String name) throws IOException{
		this.mylocale = locale;
		File dir = roots.get(clazz.getPackage().getName());
		if(dir == null){
			String classfile = clazz.getSimpleName() + ".class";
			URL curl = clazz.getResource(classfile);
			URLConnection conn = curl.openConnection();
			URL root = ((JarURLConnection)conn).getJarFileURL();
			
			return URLClassLoader.newInstance(new URL[]{root})
			.getResourceAsStream(getDir(locale, "/") + name);
		}else{
			String path = getDir(locale, File.separator);
			File file = new File(dir, path + name);
			return new FileInputStream(file);
		}
	}
	/**
	 *指定されたロケールでのリソースディレクトリへのパスを返します。
	 *@param locale ロケール
	 *@param separator パスの区切り文字
	 *@return 言語セットの存在するディレクトリへのパス
	 */
	private String getDir(Locale locale, String separator){
		String pack = clazz.getPackage().getName();
		StringBuilder path = new StringBuilder();
		path.append("localize");
		path.append(separator);
		path.append(locale.getISO3Language());
		path.append(separator);
		path.append(pack.replace(".", separator));
		return path.append('/').toString();
	}
	/**
	 *パッケージの言語設定のルートディレクトリを設定ます。
	 *
	 *@param pack パッケージ名
	 *@param root ルートディレクトリ
	 */
	public static void setRoot(String pack, File root){
		roots.put(pack, root);
	}
	/**
	 *パッケージの言語設定のルートディレクトリを設定ます。
	 *
	 *@param pack パッケージ
	 *@param root ルートディレクトリ
	 */
	public static void setRoot(Package pack, File root){
		roots.put(pack.getName(), root);
	}
	/**
	 *XML形式の言語セットをストリームから読み込みます。
	 */
	private final class DataParser extends DefaultHandler{
		private HashMap<String, String> table;
		/**
		*XMLを解析して対応するマップを返します。
		*@param stream ソースとなるストリーム
		*@return 読み込まれた言語セット
		*/
		public HashMap<String, String> parse(InputStream stream)
		throws Exception{
			this.table = new HashMap<String, String>();
			SAXParserFactory fact = null;
			try{
				fact = SAXParserFactory.newInstance();
				if(fact != null){
					SAXParser parser = fact.newSAXParser();
					parser.parse(stream, this);
				}
				return this.table;
			}finally{
				if(stream != null) stream.close();
			}
		}
		@Override public void startElement
		(String uri, String local, String name, Attributes attr)
		throws SAXException{
			if(name!=null && name.equals("entry")){
				String key   = attr.getValue("key");
				String value = attr.getValue("value");
				this.table.put(key, value);
			}
		}
	}
	/**
	 *マネージャのエラーログの書式を指定します。
	 */
	private final class LogFormatter extends Formatter{
		private final Date date = new Date();
		private final DateFormat format;
		public LogFormatter(){
			format = DateFormat.getDateTimeInstance();
		}
		@Override
		public synchronized String format(LogRecord record){
			StringBuilder sb = new StringBuilder();
			date.setTime(record.getMillis());
			sb.append(format.format(date));
			sb.append(' ');
			sb.append(record.getLevel().getLocalizedName());
			sb.append("\nat LeafLocalizeManager for ");
			sb.append(clazz.getCanonicalName());
			sb.append(' ');
			sb.append(mylocale.getDisplayLanguage(Locale.ENGLISH));
			sb.append('\n');
			sb.append(super.formatMessage(record));
			return sb.append('\n').toString();
		}
	}
}
