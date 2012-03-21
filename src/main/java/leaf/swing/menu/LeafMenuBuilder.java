/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.menu;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.MissingResourceException;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import leaf.icon.LeafIcons;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *XMLを利用して{@link JMenuItem}を動的に構築する機構を提供します。
 *XMLの書式例に以下に示します。<br><br>
 *
 *&lt;?xml version="1.0" encoding="utf-8" ?&gt;<br>
 *&lt;menu&gt;<br>
 *&lt;item type="javax.swing.JMenu" text="File"&gt;<br>
 *&lt;item type="javax.swing.JMenuItem" text="Open"
 *name="open" cmd="open" accel="ctrl O" mnemonic="O"/&gt;
 *<br>&lt;separator/&gt;<br>&lt/item&gt;<br>&lt;/menu&gt;
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年8月19日
 */
public class LeafMenuBuilder{
	private final JComponent component;
	private final HashMap<String, Class> classes;
	private final HashMap<String, JMenuItem> items;
	private final ArrayList<ActionListener> listeners;
	/**
	*構築対象のコンテナを指定してビルダを生成します。
	*@param comp 構築対象のコンテナ
	*/
	public LeafMenuBuilder(JComponent comp){
		this.component = comp;
		classes = new HashMap<String, Class>();
		items = new HashMap<String, JMenuItem>();
		listeners = new ArrayList<ActionListener>();
		addMenuItemClass(JCheckBoxMenuItem.class);
		addMenuItemClass(JMenu.class);
		addMenuItemClass(JMenuItem.class);
		addMenuItemClass(JRadioButtonMenuItem.class);
		addMenuItemClass(LeafHistoryMenu.class);
	}
	/**
	*ファイルを指定してコンテナを構築します。
	*@param file XMLを読み込むストリーム
	*@return 構築されたコンテナ
	*@throws IOException ストリーム入力例外
	*@throws Exception XML解析時に生じた何らかの例外
	*/
	public JComponent build(File file)
	throws IOException, Exception{
		return build(new FileInputStream(file));
	}
	/**
	*ストリームを指定してコンテナを構築します。
	*@param stream XMLを読み込むストリーム
	*@return 構築されたコンテナ
	*@throws IOException ストリーム入力例外
	*@throws Exception XML解析時に生じた何らかの例外
	*/
	public JComponent build(InputStream stream)
	throws IOException, Exception{
		try{
			new DataParser().parse(stream);
			return component;
		}catch(SAXException ex){
			Exception src = ex.getException();
			if(src != null) throw src;
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
	}
	/**
	*{@link JMenu}以外に適用するActionListenerを登録します。
	*@param listener 登録するリスナー
	*@throws IllegalArgumentException リスナーがnullの場合
	*/
	public void addActionListener(ActionListener listener){
		if(listener == null) throw new IllegalArgumentException();
		listeners.add(listener);
	}
	/**
	*name属性値を指定してJMenuItemを返します。
	*@param name JMenuItemの名前
	*@return 取得したJMenuItem
	*@throws MissingResourceException JMenuItemが存在しない場合
	*/
	public JMenuItem getMenuItem(String name)
	throws MissingResourceException{
		JMenuItem item = items.get(name);
		if(item != null) return item;
		throw new MissingResourceException(
			"Cannot find " + name + " at "
			+ getClass().getCanonicalName(),
			getClass().getCanonicalName(), name);
	}
	/**
	*XML解析中に呼び出されコンポーネントを追加します。
	*@param comp JMenuItemが配置されるコンテナ
	*@param item タグが表現するJMenuItem
	*@param attr 属性
	*/
	protected void addMenuItem
	(JComponent comp, JMenuItem item, Attributes attr){
		comp.add(item);
		
		String name = attr.getValue("name");
		if(name != null) items.put(name, item);
		
		String text = attr.getValue("text");
		if(text != null) item.setText(text);
		
		String cmd = attr.getValue("cmd");
		if(cmd != null) item.setActionCommand(cmd);
		
		String icons = attr.getValue("leaficons");
		if(icons != null) item.setIcon(LeafIcons.getIcon(icons));
		
		String accel = attr.getValue("accel");
		KeyStroke as = KeyStroke.getKeyStroke(accel);
		if(as != null) item.setAccelerator(as);
		
		String mnemo = attr.getValue("mnemonic");
		KeyStroke ms = KeyStroke.getKeyStroke(mnemo);
		if(ms != null) item.setMnemonic(ms.getKeyCode());
		
		if(!(item instanceof JMenu)){
			for(ActionListener lis : listeners){
				item.addActionListener(lis);
			}
		}
	}
	/**
	*短縮名を指定するだけで呼び出せるクラスを登録します。
	*@param clazz 登録するクラス
	*/
	public void addMenuItemClass(Class<? extends JComponent> clazz){
		classes.put(clazz.getSimpleName(), clazz);
	}
	/**
	*XML解析中に呼び出されコンポーネントのクラスを返します。
	*@param name クラス名
	*@return 対応するクラス
	*@throws ClassNotFoundException クラスが存在しない場合
	*/
	private Class searchClass(String name)
	throws ClassNotFoundException{
		Class clazz = classes.get(name);
		if(clazz != null)  return clazz;
		return Class.forName(name);
	}
	/**
	*XMLパーサーの実装です。各タグ/要素の説明を以下に示します。
	*
	*item : JMenuItemの宣言開始タグ
	*separator : セパレータを表すタグ
	*group : ButtonGroupを表すタグ
	*type : JMenuItemの型
	*name : JMenuItemの名前
	*text : setText(String)の引数
	*cmd  : setActionCommand(String)の引数
	*accel: setAccelerator(KeyStroke)の引数
	*mnemonic  : setMnemonic(int)の引数
	*leaficons : (非公開)LeafIcons.getIcon(String)の引数
	*/
	private final class DataParser extends DefaultHandler{
		private LinkedList<JComponent> stack;
		private ButtonGroup group = null;
		public void parse(InputStream stream)
		throws Exception{
			stack = new LinkedList<JComponent>();
			SAXParserFactory fact = null;
			try{
				fact = SAXParserFactory.newInstance();
				if(fact != null){
					SAXParser parser = fact.newSAXParser();
					stack.push(component);
					parser.parse(stream, this);
				}
			}finally{
				if(stream != null) stream.close();
			}
		}
		@Override
		public void startElement
		(String uri, String local, String qname, Attributes attr)
		throws SAXException{
			try{
				if(qname.equals("item")){
					Class type = searchClass(attr.getValue("type"));
					JMenuItem item = (JMenuItem)type.newInstance();
					addMenuItem((JComponent)stack.peek(), item, attr);
					if(group != null) group.add(item);
					stack.push(item);
				}else if(qname.equals("separator")){
					((JMenu)stack.peek()).addSeparator();
				}else if(qname.equals("group")){
					group = new ButtonGroup();
				}
			}catch(Exception ex){
				throw new SAXException(ex);
			}
		}
		@Override
		public void endElement
		(String uti, String local, String qname)
		throws SAXException{
			if(qname.equals("item")){
				stack.pop();
			}else if(qname.equals("group")){
				group = null;
			}
		}
	}
}
