/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.dialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import leaf.util.lang.LocalizeManager;

/**
 * LeafAPIが提供する全てのダイアログの基底クラスです。
 *
 * {@link LocalizeManager}の誤動作を防止するため、
 * このクラスの継承クラスはfinalクラスであるべきです。
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.0 作成：2010年5月22日
 */
public abstract class LeafDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private LocalizeManager localize = null;
	
	/**
	 * モーダレスダイアログを生成します。
	 */
	public LeafDialog() {
		setEscapeKeyAction();
	}
	
	/**
	 * 親フレームを指定してモーダレスダイアログを生成します。
	 *
	 * @param owner 親
	 */
	public LeafDialog(Frame owner) {
		super(owner);
		setEscapeKeyAction();
	}
	
	/**
	 * 親ダイアログを指定してモーダレスダイアログを生成します。
	 *
	 * @param owner 親
	 */
	public LeafDialog(Dialog owner) {
		super(owner);
		setEscapeKeyAction();
	}
	
	/**
	 * 親ウィンドウを指定してモーダレスダイアログを生成します。
	 *
	 * @param owner 親
	 */
	public LeafDialog(Window owner) {
		super(owner);
		setEscapeKeyAction();
	}
	
	/**
	 * 親フレームとモーダルを指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param modal モーダルの場合true
	 */
	public LeafDialog(Frame owner, boolean modal) {
		super(owner, modal);
		setEscapeKeyAction();
	}
	
	/**
	 * 親フレームとタイトルを指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param title タイトル
	 */
	public LeafDialog(Frame owner, String title) {
		super(owner, title);
		setEscapeKeyAction();
	}
	
	/**
	 * 親ダイアログとモーダルを指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param modal モーダルの場合true
	 */
	public LeafDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		setEscapeKeyAction();
	}
	
	/**
	 * 親ダイアログとタイトルを指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param title タイトル
	 */
	public LeafDialog(Dialog owner, String title) {
		super(owner, title);
		setEscapeKeyAction();
	}
	
	/**
	 * 親ウィンドウとモーダルを指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param modal モーダル
	 */
	public LeafDialog(Window owner, ModalityType modal) {
		super(owner, modal);
		setEscapeKeyAction();
	}
	
	/**
	 * 親ウィンドウとタイトルを指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param title タイトル
	 */
	public LeafDialog(Window owner, String title) {
		super(owner, title);
		setEscapeKeyAction();
	}
	
	/**
	 * 親とタイトル、モーダルを指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param title タイトル
	 * @param modal モーダルの場合true
	 */
	public LeafDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setEscapeKeyAction();
	}
	
	/**
	 * 親とタイトル、モーダルを指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param title タイトル
	 * @param modal モーダルの場合true
	 */
	public LeafDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setEscapeKeyAction();
	}
	
	/**
	 * 親とタイトル、モーダルを指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param title タイトル
	 * @param modal モーダルの場合true
	 */
	public LeafDialog(Window owner, String title, ModalityType modal) {
		super(owner, title, modal);
		setEscapeKeyAction();
	}
	
	/**
	 * 親とタイトル、モーダル及びグラフィックス設定を指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param title タイトル
	 * @param modal モーダルの場合true
	 * @param config グラフィックス設定
	 */
	public LeafDialog
	(Frame owner, String title, boolean modal, GraphicsConfiguration config) {
		super(owner, title, modal, config);
		setEscapeKeyAction();
	}
	
	/**
	 * 親とタイトル、モーダル及びグラフィックス設定を指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param title タイトル
	 * @param modal モーダルの場合true
	 * @param config グラフィックス設定
	 */
	public LeafDialog
	(Dialog owner, String title, boolean modal, GraphicsConfiguration config) {
		super(owner, title, modal, config);
		setEscapeKeyAction();
	}
	
	/**
	 * 親とタイトル、モーダル及びグラフィックス設定を指定してダイアログを生成します。
	 *
	 * @param owner 親
	 * @param title タイトル
	 * @param modal モーダル
	 * @param config グラフィックス設定
	 */
	public LeafDialog
	(Window owner, String title, ModalityType modal, GraphicsConfiguration config) {
		super(owner, title, modal, config);
		setEscapeKeyAction();
	}
	
	/**
	 * ダイアログの表示と配置を初期化します。
	 */
	public abstract void initialize();
	
	/**
	 * contentPaneの推奨されるサイズを設定します。
	 *
	 * @param size 推奨されるサイズ
	 */
	public void setContentSize(Dimension size) {
		getContentPane().setPreferredSize(size);
		pack(); // connect dialog to resource
		
		Insets insets = getInsets();
		Dimension dsize = new Dimension(size);
		dsize.width  += insets.left + insets.right;
		dsize.height += insets.top + insets.bottom;
		
		setSize(dsize);
	}
	
	/**
	 * {@link LocalizeManager}を利用して現地語に翻訳します。
	 *
	 * @param key キーとなる文字列
	 * @return 翻訳された文字列
	 */
	public String translate(String key) {
		if(localize == null) {
			localize = LocalizeManager.get(getClass());
		}
		return localize.translate(key);
	}
	
	/**
	 * {@link LocalizeManager}を利用して現地語に翻訳します。
	 *
	 * @param key キーとなる文字列
	 * @param args 書式指示子により参照される引数
	 * @return 翻訳された文字列
	 * @see java.util.Formatter
	 */
	public String translate(String key, Object... args) {
		if(localize == null) {
			localize = LocalizeManager.get(getClass());
		}
		return localize.translate(key, args);
	}
	
	/**
	 * ダイアログのロケールを返します。
	 *
	 * @return ダイアログのロケール
	 * @see LocalizeManager#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return LocalizeManager.getLocale();
	}
	
	private class EscapeKeyAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public EscapeKeyAction() {
			super("CLOSE");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
	
	private void setEscapeKeyAction() {
		InputMap map = getRootPane().getInputMap
		(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CLOSE");
		getRootPane().getActionMap().put("CLOSE", new EscapeKeyAction());
	}
	
	/**
	 * 自動的に親の中央に移動してから、ダイアログの表示/非表示を設定します。
	 *
	 * @param visible 表示する場合はtrue
	 */
	@Override
	public void setVisible(boolean visible) {
		if(!isVisible() && visible) {
			setLocationRelativeTo(getOwner());
		}
		super.setVisible(visible);
	}
	
	/**
	 * 指定されたテキストでメッセージボックスを表示します。
	 *
	 * @param message メッセージ
	 */
	protected void showMessage(String message) {
		JOptionPane.showMessageDialog
		(this, message, getTitle(), JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**OKボタンが選択されたことを示します*/
	public static final boolean OK_OPTION = true;
	
	/**キャンセルボタンが選択されたことを示します*/
	public static final boolean CANCEL_OPTION = false;

}
