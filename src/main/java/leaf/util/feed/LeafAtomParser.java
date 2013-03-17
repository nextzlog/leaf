/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.feed;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.stream.events.*;

import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.stream.events.XMLEvent.CHARACTERS;
import static javax.xml.stream.events.XMLEvent.END_ELEMENT;
import static javax.xml.stream.events.XMLEvent.START_ELEMENT;

/**
 * 簡易的なAtomパーサーの実装です。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.3 作成：2011年7月4日
 *
 */
final class LeafAtomParser extends LeafFeedParser {
	private final DateFormat format1, format2;
	private LeafNewsFeed feed;
	private LeafNewsItem item;
	private String value;
	private int nest = 0;
	private final StartElementHandler startElementHandler;
	private final EndElementHandler endElementHandler;
	
	/**
	 * フィード解析器を生成します。
	 */
	public LeafAtomParser() {
		format1 = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		format2 = new SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ssZ",   Locale.US);
		startElementHandler = new StartElementHandler();
		endElementHandler = new EndElementHandler();
		addHandler(END_ELEMENT, endElementHandler);
		addHandler(START_ELEMENT,  startElementHandler);
		addHandler(CHARACTERS, new CharactersHandler());
	}
	
	/**
	 * このパーサーに関連付けられるMIME typeのリストを返します。
	 * 
	 * @return MIME type のリスト
	 */
	@Override
	public List<String> getMimeTypes() {
		return Arrays.asList("application/atom+xml");
	}
	
	/**
	 * 読み込み先ストリームを指定してフィードを取得します。
	 * 
	 * @param stream フィードを読み込むストリーム
	 * @return フィード
	 * @throws IOException 読み込みに失敗した場合
	 */
	@Override
	public LeafNewsFeed parse(InputStream stream) throws IOException {
		try {
			super.parse(stream);
			return feed;
		}finally{
			feed = null;
		}
	}
	
	private class CharactersHandler extends XMLHandler {
		private StringBuilder sb = new StringBuilder();
		@Override
		public void handle(XMLEvent e) {
			if(nest != 0) return;
			if(value == null) sb.setLength(0);
			String data = ((Characters)e).getData();
			final int length = data.length();
			for(int i=0; i<length; i++) {
				char ch = data.charAt(i);
				if(Character.getType(ch)
				!= Character.LINE_SEPARATOR) {
					sb.append(ch);
				}
			}
			value = sb.toString();
		}
	}
	
	private class StartElementHandler extends XMLHandler {
		public StartElementHandler() {
			addHandler(new FeedHandler());
			addHandler(new EntryHandler());
			addHandler(new CategoryHandler());
			addHandler(new LinkHandler());
		}
		@Override
		public void handle(XMLEvent e) throws Exception {
			value = null;
			QName qname = ((StartElement)e).getName();
			String local = qname.getLocalPart();
			String prefix = qname.getPrefix();
			if(prefix.equals(DEFAULT_NS_PREFIX)) {
				TagHandler handler = getHandler(local);
				if(handler != null) handler.handle(e);
				else if(!endElementHandler.isSupported(local)) {
					if(feed != null) nest++;
				}
			} else if(feed != null) nest++;
		}
		// <feed>
		private class FeedHandler implements TagHandler {
			public void handle(XMLEvent e) {
				feed = new LeafNewsFeed();
			}
			public String tag() {
				return "feed";
			}
		}
		// <entry>
		private class EntryHandler implements TagHandler {
			public void handle(XMLEvent e) {
				if(feed != null) {
					feed.addItem(item = new LeafNewsItem());
				}
			}
			public String tag() {
				return "entry";
			}
		}
		// <category>
		private class CategoryHandler implements TagHandler {
			public void handle(XMLEvent e) {
				QName qterm = new QName("term");
				StartElement se = (StartElement)e;
				Attribute term = se.getAttributeByName(qterm);
				feed.addItem(term.getValue(), item);
			}
			public String tag() {
				return "category";
			}
		}
		// <link>
		private class LinkHandler implements TagHandler {
			public void handle(XMLEvent e) throws MalformedURLException {
				StartElement se = (StartElement)e;
				QName qrel  = new QName("rel");
				QName qhref = new QName("href");
				Attribute rel  = se.getAttributeByName(qrel);
				Attribute href = se.getAttributeByName(qhref);
				if("alternate".equals(rel.getValue())) {
					URL link = new URL(href.getValue());
					if(item != null) item.setLink(link);
					else feed.setLink(link);
				}
			}
			public String tag() {
				return "link";
			}
		}
	}
	
	private class EndElementHandler extends XMLHandler {
		public EndElementHandler() {
			addHandler(new EntryHandler());
			addHandler(new TitleHandler());
			addHandler(new ContentHandler());
			addHandler(new UpdatedHandler());
			addHandler(new GeneratorHandler());
			addHandler(new RightsHandler());
		}
		@Override
		public void handle(XMLEvent e) throws Exception {
			if(feed == null) return;
			QName qname = ((EndElement)e).getName();
			String local = qname.getLocalPart();
			if(nest != 0) nest--;
			else if(nest == 0) {
				TagHandler handler = getHandler(local);
				if(handler != null) handler.handle(e);
			}
			value = null;
		}
		// </entry>
		private class EntryHandler implements TagHandler {
			public void handle(XMLEvent e) {
				item = null;
			}
			public String tag() {
				return "entry";
			}
		}
		// </title>
		private class TitleHandler implements TagHandler {
			public void handle(XMLEvent e) {
				if(item != null) item.setTitle(value);
				else feed.setTitle(value);
			}
			public String tag() {
				return "title";
			}
		}
		// </content>
		private class ContentHandler implements TagHandler {
			public void handle(XMLEvent e) {
				if(item != null) item.setDescription(value);
				else feed.setDescription(value);
			}
			public String tag() {
				return "content";
			}
		}
		// </updated>
		private class UpdatedHandler implements TagHandler {
			public void handle(XMLEvent e) throws ParseException {
				Date date = parseDate(value);
				if(item != null) item.setDate(date);
				else feed.setDate(date);
			}
			public String tag() {
				return "updated";
			}
		}
		// </generator>
		private class GeneratorHandler implements TagHandler {
			public void handle(XMLEvent e) {
				feed.setGenerator(value);
			}
			public String tag() {
				return "generator";
			}
		}
		// </rights>
		private class RightsHandler implements TagHandler {
			public void handle(XMLEvent e) {
				feed.setCopyright(value);
			}
			public String tag() {
				return "rights";
			}
		}
		
		/**
		 * RFC3339形式に従って日時を解析して返します。
		 * 
		 * @param text 日時の文字列
		 * @return 解析された日時
		 * @throws ParseException 日時の書式が不正の場合
		 */
		private Date parseDate(String text) throws ParseException {
			try {
				return format1.parse(text);
			} catch(ParseException ex) {
				return format2.parse(text);
			}
		}
	}
}
