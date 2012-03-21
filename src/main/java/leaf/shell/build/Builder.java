/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
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

import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.stream.events.XMLEvent.END_ELEMENT;
import static javax.xml.stream.events.XMLEvent.START_ELEMENT;

/**
 *ビルド文書を解析するビルダーの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
final class Builder{
	private final Deque<JComponent> stack;
	private final JComponent target;
	private final LeafShell shell;
	private final EventHandler endElementHandler;
	private final EventHandler startElementHandler;
	
	/**
	 *シェルとビルド対象を指定してビルダを構築します。
	 *
	 *@param shell 関連付けられるシェル
	 *@param target ビルド対象のコンテナ
	 */
	public Builder(LeafShell shell, JComponent target){
		this.shell = shell; // first!
		stack = new ArrayDeque<JComponent>();
		endElementHandler = new EndElementHandler();
		startElementHandler = new StartElementHandler();
		stack.push(this.target = target);
	}
	/**
	 *ビルド文書を読み込むストリームを指定して文書を解析します。
	 *
	 *@param stream ビルド文書を読み込むストリーム
	 *@throws IOException 読み込みに失敗した場合
	 *@throws UnknownNameException 規約違反時
	 */
	public void build(InputStream stream)
	throws IOException, UnknownNameException{
		XMLEventReader reader = null;
		XMLInputFactory fact  = null;
		try{
			fact = XMLInputFactory.newInstance();
			stream = new BufferedInputStream(stream);
			reader = fact.createXMLEventReader(stream);
			while(reader.hasNext()){
				XMLEvent e = reader.nextEvent();
				if(e.getEventType() == START_ELEMENT){
					startElementHandler.handle(e);
				}else if(e.getEventType() == END_ELEMENT){
					endElementHandler.handle(e);
				}
			}
		}catch(UnknownNameException ex){
			throw ex;
		}catch(Exception ex){
			throw new IOException(ex);
		}finally{
			try{
				if(stream != null) stream.close();
				if(reader != null) reader.close();
			}catch(XMLStreamException ex){
				throw new IOException(ex);
			}
		}
	}
	/**
	 *要素の終了イベントを処理します。
	 */
	private class EndElementHandler extends EventHandler{
		@Override public void handle(XMLEvent e){
			stack.pop();
		}
	}
	/**
	 *要素の開始イベントを最初に処理します。
	 */
	private class StartElementHandler extends EventHandler{
		public StartElementHandler(){
			addHandler(new ButtonHandler(shell));
			addHandler(new ItemHandler(shell));
			addHandler(new MenuHandler(shell));
			addHandler(new SeparatorHandler());
		}
		@Override public void handle(XMLEvent e) throws Exception{
			QName qname = ((StartElement)e).getName();
			ElementHandler handler = getHandler(qname);
			if(handler != null){
				JComponent item = handler.handle(e.asStartElement());
 				if(item instanceof JSeparator
				&& stack.peek() instanceof JMenu){
					((JMenu)stack.peek()).addSeparator();
				}else stack.peek().add(item);
				stack.push(item);
			}else if(!qname.toString().equals("build")){
				throw new UnknownNameException(qname);
			}
		}
	}
}