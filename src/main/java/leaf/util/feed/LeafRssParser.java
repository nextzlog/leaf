/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
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
 *簡易的なRSSパーサーの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年5月2日
 */
final class LeafRssParser extends LeafFeedParser{
	private final DateFormat format;
	private LeafNewsFeed feed;
	private LeafNewsItem item;
	private String value, unsupported;
	private int nest = 0;
	private final StartElementHandler startElementHandler;
	private final EndElementHandler endElementHandler;
	
	/**
	 *フィード解析器を生成します。
	 *
	 */
	public LeafRssParser(){
		format = new SimpleDateFormat(
		"EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
		startElementHandler = new StartElementHandler();
		endElementHandler = new EndElementHandler();
		addHandler(END_ELEMENT, endElementHandler);
		addHandler(START_ELEMENT,  startElementHandler);
		addHandler(CHARACTERS, new CharactersHandler());
	}
	/**
	 *このパーサーに関連付けられるMIME typeのリストを返します。
	 *
	 *@return MIME type のリスト
	 */
	@Override
	public List<String> getMimeTypes(){
		return Arrays.asList("application/rss+xml");
	}
	/**
	 *読み込み先ストリームを指定してフィードを取得します。
	 *
	 *@param stream フィードを読み込むストリーム
	 *@return フィード
	 *@throws IOException 読み込みに失敗した場合
	 */
	@Override
	public LeafNewsFeed parse(InputStream stream) throws IOException{
		try{
			super.parse(stream);
			return feed;
		}finally{
			feed = null;
		}
	}
	/**
	 *文字列を読み込みます。
	 *
	 */
	private class CharactersHandler extends XMLHandler{
		private StringBuilder sb = new StringBuilder();
		@Override
		public void handle(XMLEvent e){
			if(nest != 0) return;
			if(value == null) sb.setLength(0);
			String data = ((Characters)e).getData();
			final int length = data.length();
			for(int i=0; i<length; i++){
				char ch = data.charAt(i);
				if(Character.getType(ch)
				!= Character.LINE_SEPARATOR){
					sb.append(ch);
				}
			}
			value = sb.toString();
		}
	}
	/**
	 *要素の開始通知を処理します。
	 *
	 */
	private class StartElementHandler extends XMLHandler{
		public StartElementHandler(){
			addHandler(new ChannelHandler());
			addHandler(new ItemHandler());
		}
		@Override
		public void handle(XMLEvent e) throws Exception{
			value = null;
			QName qname = ((StartElement)e).getName();
			String local = qname.getLocalPart();
			String prefix = qname.getPrefix();
			if(prefix.equals(DEFAULT_NS_PREFIX)){
				TagHandler handler = getHandler(local);
				if(handler != null) handler.handle(e);
				else if(!endElementHandler.isSupported(local)){
					if(feed != null && nest++ == 0){
						unsupported = local;
					}
				}
			}else if(feed != null && nest++ == 0){
				unsupported = prefix + ':' + local;
			}
		}
		// <channel>
		private class ChannelHandler implements TagHandler{
			public void handle(XMLEvent e){
				feed = new LeafNewsFeed();
			}
			public String tag(){
				return "channel";
			}
		}
		// <item>
		private class ItemHandler implements TagHandler{
			public void handle(XMLEvent e){
				if(feed != null){
					feed.addItem(item = new LeafNewsItem());
				}
			}
			public String tag(){
				return "item";
			}
		}
	}
	/**
	 *要素の終了通知を処理します。
	 *
	 */
	private class EndElementHandler extends XMLHandler{
		public EndElementHandler(){
			addHandler(new ItemHandler());
			addHandler(new LinkHandler());
			addHandler(new TitleHandler());
			addHandler(new PubDateHandler());
			addHandler(new GeneratorHandler());
			addHandler(new CopyrightHandler());
			addHandler(new LanguageHandler());
			addHandler(new CategoryHandler());
			addHandler(new DescriptionHandler());
		}
		@Override
		public void handle(XMLEvent e) throws Exception{
			if(feed == null) return;
			QName qname = ((EndElement)e).getName();
			String local = qname.getLocalPart();
			if(nest != 0 && --nest == 0){
				unsupported = null;
			}else if(nest == 0){
				TagHandler handler = getHandler(local);
				if(handler != null) handler.handle(e);
			}
			value = null;
		}
		// </item>
		private class ItemHandler implements TagHandler{
			public void handle(XMLEvent e){
				item = null;
			}
			public String tag(){
				return "item";
			}
		}
		// </link>
		private class LinkHandler implements TagHandler{
			public void handle(XMLEvent e)
			throws MalformedURLException{
				URL link = new URL(value);
				if(item != null) item.setLink(link);
				else feed.setLink(link);
			}
			public String tag(){
				return "link";
			}
		}
		// </title>
		private class TitleHandler implements TagHandler{
			public void handle(XMLEvent e){
				if(item != null) item.setTitle(value);
				else feed.setTitle(value);
			}
			public String tag(){
				return "title";
			}
		}
		// </pubdate>
		private class PubDateHandler implements TagHandler{
			public void handle(XMLEvent e)
			throws ParseException{
				Date date = format.parse(value);
				if(item != null) item.setDate(date);
				else feed.setDate(date);
			}
			public String tag(){
				return "pubdate";
			}
		}
		// </generator>
		private class GeneratorHandler implements TagHandler{
			public void handle(XMLEvent e){
				feed.setGenerator(value);
			}
			public String tag(){
				return "generator";
			}
		}
		// </copyright>
		private class CopyrightHandler implements TagHandler{
			public void handle(XMLEvent e){
				feed.setCopyright(value);
			}
			public String tag(){
				return "copyright";
			}
		}
		// </language>
		private class LanguageHandler implements TagHandler{
			public void handle(XMLEvent e){
				feed.setLanguage(new Locale(value));
			}
			public String tag(){
				return "language";
			}
		}
		// </category>
		private class CategoryHandler implements TagHandler{
			public void handle(XMLEvent e){
				feed.addItem(value, item);
			}
			public String tag(){
				return "category";
			}
		}
		// </description>
		private class DescriptionHandler implements TagHandler{
			public void handle(XMLEvent e){
				if(item != null) item.setDescription(value);
				else feed.setDescription(value);
			}
			public String tag(){
				return "description";
			}
		}
	}
}
