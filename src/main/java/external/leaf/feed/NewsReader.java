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
package leaf.feed;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 小型軽量なRSS/Atomフィードリーダーの実装です。
 *
 * @author 東大アマチュア無線クラブ
 * @since 2011年5月2日
 */
public class NewsReader {
	private static final Map<String, FeedParser> mimes;

	static {
		mimes = new HashMap<String, FeedParser>();
		addFeedParser(new AtomParser());
		addFeedParser(new RssParser());
	}

	private final URL url;
	private FeedParser parser;

	/**
	 * URLを指定してフィードリーダーを生成します。
	 *
	 * @param url 読み込み先のURL
	 */
	public NewsReader(URL url) {
		this.url = url;
	}

	/**
	 * フィードパーサーを登録します。
	 *
	 * @param parser 追加するパーサー
	 */
	private static void addFeedParser(FeedParser parser) {
		for (String mime : parser.getMimeTypes()) {
			mimes.put(mime, parser);
		}
	}

	/**
	 * フィードの読み込み先となるURLを返します。
	 *
	 * @return フィードを読み込むURL
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * フィードを取得して返します。
	 *
	 * @return フィード
	 * @throws IOException            読み込みに失敗した場合
	 * @throws UnknownFormatException 未対応の形式の場合
	 */
	public NewsFeed read()
			throws IOException, UnknownFormatException {
		if (parser != null) return parser.parse(url);
		URLConnection conn = url.openConnection();
		String mime = conn.getContentType();
		if (mime != null) {
			int range = mime.indexOf(';');
			if (range >= 0) mime = mime.substring(0, range);
		}
		parser = getFeedParser(mime);
		return parser.parse(conn.getInputStream());
	}

	/**
	 * 指定したMIME type でフィードを取得して返します。
	 *
	 * @param mime フィード形式を指定するMIME type
	 * @return フィード
	 * @throws IOException            読み込みに失敗した場合
	 * @throws UnknownFormatException 未対応の形式の場合
	 */
	public NewsFeed read(final String mime)
			throws IOException, UnknownFormatException {
		return getFeedParser(mime).parse(url);
	}

	/**
	 * 指定したMIME type でフィードパーサーを検索します。
	 *
	 * @param mime フィード形式を指定するMIME type
	 * @return 対応するフィードパーサー
	 * @throws IOException            自動識別に失敗した場合
	 * @throws UnknownFormatException 未対応の形式の場合
	 */
	private FeedParser getFeedParser(String mime)
			throws IOException, UnknownFormatException {
		FeedParser parser = mimes.get(mime);
		if (parser != null) return parser;
		parser = mimes.get(new Detector().detect());
		if (parser != null) return parser;
		throw new UnknownFormatException(mime);
	}

	/**
	 * 標準化されていないMIMEtypeに対する最後の砦です。
	 */
	private class Detector extends DefaultHandler {
		private String mime;

		/**
		 * フィードを解析してMIMEタイプを返します。
		 *
		 * @return 書式を指定する登録済みMIMEタイプ
		 * @throws IOException 読み込みに失敗した場合
		 */
		public String detect() throws IOException {
			InputStream stream = null;
			SAXParserFactory fact = null;
			try {
				stream = url.openStream();
				fact = SAXParserFactory.newInstance();
				fact.newSAXParser().parse(stream, this);
				return mime;
			} catch (ParserConfigurationException ex) {
				throw new IOException(ex);
			} catch (SAXException ex) {
				return mime;
			} finally {
				if (stream != null) stream.close();
			}
		}

		@Override
		public void startElement
				(String uri, String local, String qname, Attributes attr)
				throws SAXException {
			if (qname.equalsIgnoreCase("feed")) {
				mime = "application/atom+xml";
			} else if (qname.equalsIgnoreCase("rss")) {
				mime = "application/rss+xml";
			}
			throw new SAXException(); // jump to catch
		}
	}
}