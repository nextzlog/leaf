/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.shell.build;

import leaf.shell.LeafShell;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import static javax.xml.stream.events.XMLEvent.END_ELEMENT;
import static javax.xml.stream.events.XMLEvent.START_ELEMENT;

/**
 * ビルド文書を解析するビルダーの実装です。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年12月11日
 */
final class Builder {
	private final Deque<JComponent> stack;
	private final LeafShell shell;
	private final EventHandler endElementHandler;
	private final EventHandler startElementHandler;
	
	/**
	 * シェルとビルド対象を指定してビルダを構築します。
	 * 
	 * @param shell 関連付けられるシェル
	 * @param target ビルド対象のコンテナ
	 */
	public Builder(LeafShell shell, JComponent target) {
		this.shell = shell; // first!
		stack = new ArrayDeque<JComponent>();
		endElementHandler = new EndElementHandler();
		startElementHandler = new StartElementHandler();
		stack.push(target);
	}
	
	/**
	 * ビルド文書を読み込むストリームを指定して文書を解析します。
	 * 
	 * @param stream ビルド文書を読み込むストリーム
	 * @throws IOException 読み込みに失敗した場合
	 * @throws UnknownNameException 規約違反時
	 */
	public void build(InputStream stream)
	throws IOException, UnknownNameException {
		XMLEventReader reader = null;
		XMLInputFactory fact  = null;
		try {
			fact = XMLInputFactory.newInstance();
			stream = new BufferedInputStream(stream);
			reader = fact.createXMLEventReader(stream);
			while(reader.hasNext()) {
				XMLEvent e = reader.nextEvent();
				if(e.getEventType() == START_ELEMENT) {
					startElementHandler.handle(e);
				} else if(e.getEventType() == END_ELEMENT) {
					endElementHandler.handle(e);
				}
			}
		} catch(UnknownNameException ex) {
			throw ex;
		} catch(Exception ex) {
			throw new IOException(ex);
		} finally {
			try {
				if(stream != null) stream.close();
				if(reader != null) reader.close();
			} catch(XMLStreamException ex) {
				throw new IOException(ex);
			}
		}
	}
	
	private class EndElementHandler extends EventHandler {
		@Override
		public void handle(XMLEvent e) {
			stack.pop();
		}
	}
	
	private class StartElementHandler extends EventHandler {
		public StartElementHandler() {
			addHandler(new ButtonHandler(shell));
			addHandler(new ItemHandler(shell));
			addHandler(new MenuHandler(shell));
			addHandler(new SeparatorHandler());
		}
		
		@Override
		public void handle(XMLEvent e) throws Exception {
			QName qname = ((StartElement)e).getName();
			ElementHandler handler = getHandler(qname);
			if(handler != null) {
				JComponent item = handler.handle(e.asStartElement());
				if(handler instanceof SeparatorHandler) {
					handler.addSeparator(stack.peek());
				}
				else stack.peek().add(item);
				
				assert item != null;
				stack.push(item);
			} else if(!qname.toString().equals("build")) {
				throw new UnknownNameException(qname);
			}
		}
	}
	
	/**
	 * ビルド文書解析時の各種イベント毎の処理を最初に実行します。
	 * 
	 * @author 東大アマチュア無線クラブ
	 * @since  Leaf 1.3 作成：2011年12月11日
	 */
	private abstract class EventHandler {
		private Map<QName, ElementHandler> handlers = null;
		
		/**
		 * このハンドラーに各要素に対応する隷下ハンドラーを追加します。
		 * 
		 * @param handler 追加するハンドラー
		 */
		protected final void addHandler(ElementHandler handler) {
			if(handlers == null) {
				handlers = new HashMap<QName, ElementHandler>();
			}
			handlers.put(handler.name(), handler);
		}
		
		/**
		 * 指定した要素名に対応する隷下属性ハンドラーを返します。
		 * 
		 * @param name 解析する属性
		 * @return 対応する要素ハンドラー
		 */
		protected final ElementHandler getHandler(QName name) {
			return (handlers == null)? null : handlers.get(name);
		}
		
		/**
		 * イベントオブジェクトを受け取って処理を実行します。
		 * 
		 * @param e 受け取るイベント
		 * @throws Exception この処理が発生しうる例外
		 */
		public abstract void handle(XMLEvent e) throws Exception;
	}

}