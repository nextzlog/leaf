/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.shell.build;

import leaf.shell.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.xml.namespace.QName;
import javax.xml.stream.events.*;
import leaf.icon.LeafIcons;

/**
 *"item"要素の開始イベントを処理します。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
class ItemHandler extends ElementHandler{
	private LeafShell shell;
	private Map<String, ButtonGroup> groups;
	protected AbstractButton item;
	private final QName command = new QName("command");
	
	/**
	 *シェルを指定してハンドラーを構築します。
	 *
	 *@param shell 関連付けられるシェル
	 */
	public ItemHandler(LeafShell shell){
		this.shell = shell;
		addHandler(new CommandHandler());
		addHandler(new GroupHandler());
		addHandler(new TextHandler());
		addHandler(new IconHandler());
		addHandler(new LeafIconHandler());
		addHandler(new AccelHandler());
		addHandler(new MnemonicHandler());
		groups = new HashMap<String, ButtonGroup>();
	}
	/**
	 *このハンドラーが処理する要素の名前を返します。
	 *
	 *@return item
	 */
	@Override public QName name(){
		return new QName("item");
	}
	/**
	 *このハンドラーがデフォルトで生成するコンポーネントを指定します。
	 *
	 *@return JMenuItemのインスタンスを返す
	 */
	protected AbstractButton createDefaultButton(){
		return new JMenuItem();
	}
	/**
	 *イベントオブジェクトを受け取って処理を実行します。
	 *
	 *@param e 受け取るイベント
	 *@return 処理の結果生成されたコンポーネント
	 *@throws Exception この処理が発生しうる例外
	 */
	@Override public JComponent handle(StartElement e) throws Exception{
		item = createDefaultButton();
		handle(e, command);
		final Iterator iterator = e.getAttributes();
		while(iterator.hasNext()){
			QName name = ((Attribute) iterator.next()).getName();
			if(!name.equals(command)) handle(e, name);
		}
		JComponent ntem = item;
		item = null;
		return ntem;
	}
	/**
	 *指定した名前の属性に対して処理を実行します。
	 *
	 *@param e イベント
	 *@param name 属性名
	 *@throws Exception この処理が発生しうる例外
	 */
	private void handle(StartElement e, QName name) throws Exception{
		Attribute attr = e.getAttributeByName(name);
		if(attr != null){
			AttributeHandler handler = getHandler(attr.getName());
			if(handler != null) handler.handle(attr);
			else throw new UnknownNameException(attr.getName());
		}
	}
	/**
	 *"command"属性、つまり対応するコマンドを指定する属性です。
	 */
	private class CommandHandler extends AttributeHandler{
		@Override public QName name(){
			return command;
		}
		@Override public JComponent handle(Attribute attr)
		throws UnknownNameException, ClassCastException{
			Command cmd = shell.getCommand(attr.getValue());
			if(cmd != null && cmd instanceof MenuItemProvider){
				MenuItemProvider mip = (MenuItemProvider)cmd;
				return item = mip.createMenuItem((JMenuItem)item);
			}else throw new UnknownNameException(attr.getValue());
		}
	}
	/**
	 *"group"属性、つまりアイテムの排他的選択動作を指定する属性です。
	 */
	private class GroupHandler extends AttributeHandler{
		@Override public QName name(){
			return new QName("group");
		}
		@Override public JComponent handle(Attribute attr){
			ButtonGroup group = groups.get(attr.getValue());
			if(group == null){
				groups.put(attr.getValue(), group = new ButtonGroup());
			}
			group.add(item);
			return item;
		}
	}
	/**
	 *"text"属性、つまりアイテムの表示テキストを指定する属性です。
	 */
	private class TextHandler extends AttributeHandler{
		@Override public QName name(){
			return new QName("text");
		}
		@Override public JComponent handle(Attribute attr){
			item.setText(attr.getValue());
			return item;
		}
	}
	/**
	 *"icon"属性、つまりアイテムの表示アイコンを指定する属性です。
	 */
	private class IconHandler extends AttributeHandler{
		@Override public QName name(){
			return new QName("icon");
		}
		@Override public JComponent handle(Attribute attr){
			item.setIcon(new ImageIcon(attr.getValue()));
			return item;
		}
	}
	/**
	 *"leaficon"属性、つまりアイテムの表示アイコンを指定する属性です。
	 */
	private class LeafIconHandler extends AttributeHandler{
		@Override public QName name(){
			return new QName("leaficon");
		}
		@Override public JComponent handle(Attribute attr){
			item.setIcon(LeafIcons.getIcon(attr.getValue()));
			return item;
		}
	}
	/**
	 *"accel"属性、つまりアイテムのキーアクセラレータを指定する属性です。
	 */
	private class AccelHandler extends AttributeHandler{
		@Override public QName name(){
			return new QName("accel");
		}
		@Override public JComponent handle(Attribute attr)
		throws ClassCastException {
			KeyStroke as = KeyStroke.getKeyStroke(attr.getValue());
			((JMenuItem)item).setAccelerator(as);
			return item;
		}
	}
	/**
	 *"mnemonic"属性、つまりアイテムのニーモニックキーを指定する属性です。
	 */
	private class MnemonicHandler extends AttributeHandler{
		@Override public QName name(){
			return new QName("mnemonic");
		}
		@Override public JComponent handle(Attribute attr){
			String mnemo = attr.getValue();
			KeyStroke ms = KeyStroke.getKeyStroke(mnemo);
			item.setMnemonic(ms.getKeyCode());
			return item;
		}
	}
}
