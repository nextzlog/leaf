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

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import leaf.shell.LeafShell;

/**
 *"button"要素の開始イベントを処理します。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
final class ButtonHandler extends ItemHandler{
	private LeafShell shell;
	/**
	 *シェルを指定してハンドラーを構築します。
	 *
	 *@param shell 関連付けられるシェル
	 */
	public ButtonHandler(LeafShell shell){
		super(shell);
		this.shell = shell;
		addHandler(new CommandHandler());
	}
	/**
	 *このハンドラーが処理する要素の名前を返します。
	 *
	 *@return menu
	 */
	@Override public QName name(){
		return new QName("button");
	}
	/**
	 *このハンドラーがデフォルトで生成するコンポーネントを指定します。
	 *
	 *@return JButtonのインスタンスを返す
	 */
	@Override protected AbstractButton createDefaultButton(){
		return new JButton();
	}
	/**
	 *"command"属性、つまり対応するコマンドを指定する属性です。
	 */
	private class CommandHandler extends AttributeHandler{
		@Override public QName name(){
			return new QName("command");
		}
		@Override public JComponent handle(Attribute attr)
		throws UnknownNameException, ClassCastException{
			if(item != null) return item;
			Command cmd = shell.getCommand(attr.getValue());
			if(cmd != null && cmd instanceof ButtonProvider){
				ButtonProvider bp = (ButtonProvider)cmd;
				return item = bp.createButton((JButton)item);
			}else throw new UnknownNameException(attr.getValue());
		}
	}
}
