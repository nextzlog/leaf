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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

import leaf.feed.NewsFeed;

/**
 * フィードパーサーの基底実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2011年7月4日
 * 
 */
abstract class FeedParser {
	private final Map<Integer, XMLHandler> handlers;
	
	/**
	 * パーサーを生成します。
	 */
	public FeedParser() {
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
	public final NewsFeed parse(URL url) throws IOException{
		return parse(url.openStream());
	}
	
	/**
	 * 読み込み先ストリームを指定してフィードを取得します。
	 * 
	 * @param stream フィードを読み込むストリーム
	 * @return フィード
	 * @throws IOException 読み込みに失敗した場合
	 */
	public NewsFeed parse(InputStream stream) throws IOException{
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