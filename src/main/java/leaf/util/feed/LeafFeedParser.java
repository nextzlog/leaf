/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.feed;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

/**
 * フィードパーサーの基底実装です。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年7月4日
 */
abstract class LeafFeedParser {
	private final Map<Integer, XMLHandler> handlers;
	
	/**
	 * パーサーを生成します。
	 */
	public LeafFeedParser() {
		handlers = new HashMap<Integer, XMLHandler>();
	}
	
	/**
	 * このパーサーにイベントハンドラーを登録します。
	 * 
	 * @param type 関連付けるイベントの種類
	 * @param handler イベントハンドラー
	 */
	protected final void addHandler(int type, XMLHandler handler) {
		handlers.put(type, handler);
	}
	
	/**
	 * このパーサーに関連付けられるMIMEtypeのリストを返します。
	 * 
	 * @return MIMEtypeのリスト
	 */
	public abstract List<String> getMimeTypes();
	
	/**
	 * 読み込み先URLを指定してフィードを取得します。
	 * 
	 * @param url フィードを読み込むURL
	 * @return フィード
	 * @throws IOException 読み込みに失敗した場合
	 */
	public final LeafNewsFeed parse(URL url) throws IOException{
		return parse(url.openStream());
	}
	
	/**
	 * 読み込み先ストリームを指定してフィードを取得します。
	 * 
	 * @param stream フィードを読み込むストリーム
	 * @return フィード
	 * @throws IOException 読み込みに失敗した場合
	 */
	public LeafNewsFeed parse(InputStream stream) throws IOException{
		XMLEventReader reader = null;
		XMLInputFactory fact  = null;
		try {
			fact = XMLInputFactory.newInstance();
			stream = new BufferedInputStream(stream);
			reader = fact.createXMLEventReader(stream);
			while(reader.hasNext()) {
				XMLEvent e = reader.nextEvent();
				final int type = e.getEventType();
				XMLHandler handler = handlers.get(type);
				if(handler != null) handler.handle(e);
			}
			return null;
		} catch(FactoryConfigurationError err) {
			throw new IOException(err);
		} catch(XMLStreamException ex) {
			throw new IOException(ex);
		} catch(Exception ex) {
			throw new IOException(ex);
		} finally {
			try{
				if(stream != null) stream.close();
				if(reader != null) reader.close();
			}catch(XMLStreamException ex) {
				throw new IOException(ex);
			}
		}
	}
	
	/**
	 * 各種イベント毎に処理を実行します。
	 */
	public abstract class XMLHandler {
		public abstract void handle(XMLEvent e) throws Exception;
		private Map<String, TagHandler> handlers = null;
		public final void addHandler(TagHandler handler) {
			if(handlers == null) {
				handlers = new HashMap<String, TagHandler>();
			}
			handlers.put(handler.tag().toLowerCase(), handler);
		}
		public final TagHandler getHandler(String tag) {
			if(handlers == null) return null;
			return handlers.get(tag.toLowerCase());
		}
		public final boolean isSupported(String tag) {
			if(handlers == null) return false;
			return handlers.get(tag.toLowerCase()) != null;
		}
	}
	
	/**
	 * 各種タグ要素に対応した処理を実行します。
	 */
	public interface TagHandler {
		public void handle(XMLEvent e) throws Exception;
		public String tag();
	}
}