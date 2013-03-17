/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.shell.build;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

import leaf.shell.ButtonProvider;
import leaf.shell.Command;
import leaf.shell.LeafShell;

/**
 * JButtonの記述の開始イベントを処理します。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年12月11日
 */
final class ButtonHandler extends AbstractButtonHandler {
	private LeafShell shell;
	
	/**
	 * シェルを指定してハンドラーを構築します。
	 * 
	 * @param shell 関連付けられるシェル
	 */
	public ButtonHandler(LeafShell shell) {
		this.shell = shell;
		addHandler(new CommandHandler());
	}
	
	/**
	 * このハンドラーが処理する要素の名前を返します。
	 * 
	 * @return menu
	 */
	@Override
	public QName name() {
		return new QName("button");
	}
	
	/**
	 * このハンドラーがデフォルトで生成するコンポーネントを指定します。
	 * 
	 * @return JButtonのインスタンスを返す
	 */
	@Override
	protected AbstractButton createDefaultButton() {
		JButton button = new JButton();
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setRequestFocusEnabled(false);
		return button;
	}
	
	// "command"属性、つまり対応するコマンドを指定する属性です。
	private class CommandHandler extends AttributeHandler{
		@Override
		public QName name() {
			return command;
		}
		
		@Override
		public JComponent handle(Attribute attr)
		throws UnknownNameException, ClassCastException {
			Command cmd = shell.getCommand(attr.getValue());
			if(cmd != null && cmd instanceof ButtonProvider) {
				ButtonProvider bp = (ButtonProvider)cmd;
				return item = bp.createButton((JButton)item);
			}else throw new UnknownNameException(attr.getValue());
		}
	}
}
