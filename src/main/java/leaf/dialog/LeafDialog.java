/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.dialog;

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

import leaf.manager.LeafLocalizeManager;

/**
 *LeafAPIが提供する全てのダイアログの基底クラスです。
 *
 *{@link LeafLocalizeManager}の誤動作を防止するため、
 *このクラスの継承クラスはfinalクラスであるべきです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年5月22日
 */
public abstract class LeafDialog extends JDialog{
	private LeafLocalizeManager localize = null;
	
	/**
	 *タイトルと親を指定しないでモーダレスダイアログを生成します。
	 */
	public LeafDialog() {
		super();
		setEscapeAction();
	}
	/**
	 *親フレームを指定してモーダレスダイアログを生成します。
	 *@param owner 親
	 */
	public LeafDialog(Frame owner) {
		super(owner);
		setEscapeAction();
	}
	/**
	 *親ダイアログを指定してモーダレスダイアログを生成します。
	 *@param owner 親
	 */
	public LeafDialog(Dialog owner) {
		super(owner);
		setEscapeAction();
	}
	/**
	 *親ウィンドウを指定してモーダレスダイアログを生成します。
	 *@param owner 親
	 */
	public LeafDialog(Window owner) {
		super(owner);
		setEscapeAction();
		
	}
	/**
	 *親フレームとモーダル設定を指定してダイアログを生成します。
	 *@param owner 親
	 *@param modal モーダルの場合true
	 */
	public LeafDialog(Frame owner, boolean modal) {
		super(owner, modal);
		setEscapeAction();
	}
	/**
	 *親フレームとタイトルを指定してダイアログを生成します。
	 *@param owner 親
	 *@param title タイトル
	 */
	public LeafDialog(Frame owner, String title) {
		super(owner, title);
		setEscapeAction();
	}
	/**
	 *親ダイアログとモーダル設定を指定してダイアログを生成します。
	 *@param owner 親ダイアログ
	 *@param modal モーダルの場合true
	 */
	public LeafDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		setEscapeAction();
	}
	/**
	 *親ダイアログとタイトルを指定してダイアログを生成します。
	 *@param owner 親
	 *@param title タイトル
	 */
	public LeafDialog(Dialog owner, String title) {
		super(owner, title);
		setEscapeAction();
	}
	/**
	 *親ウィンドウとモーダル設定を指定してダイアログを生成します。
	 *@param owner 親
	 *@param modal モーダル設定
	 */
	public LeafDialog(Window owner, ModalityType modal) {
		super(owner, modal);
		setEscapeAction();
	}
	/**
	 *親ウィンドウとタイトルを指定してダイアログを生成します・
	 *@param owner 親
	 *@param title タイトル
	 */
	public LeafDialog(Window owner, String title) {
		super(owner, title);
		setEscapeAction();
	}
	/**
	 *親フレームとタイトル、
	 *モーダル設定を指定してダイアログを生成します。
	 *@param owner 親
	 *@param title タイトル
	 *@param modal モーダルの場合true
	 */
	public LeafDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setEscapeAction();
	}
	/**
	 *親ダイアログとタイトル、モーダル設定を指定してダイアログを生成します。
	 *@param owner 親
	 *@param title タイトル
	 *@param modal モーダルの場合true
	 */
	public LeafDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setEscapeAction();
	}
	/**
	 *親ダイアログとタイトル、モーダル設定を指定してダイアログを生成します。
	 *@param owner 親
	 *@param title タイトル
	 *@param modal モーダルの場合true
	 */
	public LeafDialog(Window owner, String title, ModalityType modal) {
		super(owner, title, modal);
		setEscapeAction();
	}
	/**
	 *親フレームとタイトル、モーダル設定及び
	 *グラフィックス設定を指定してダイアログを生成します。
	 *@param owner 親
	 *@param title タイトル
	 *@param modal モーダルの場合true
	 *@param config グラフィックス設定
	 */
	public LeafDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration config) {
		super(owner, title, modal, config);
		setEscapeAction();
	}
	/**
	 *親ダイアログ	とタイトル、モーダル設定及び
	 *グラフィックス設定を指定してダイアログを生成します。
	 *@param owner 親
	 *@param title タイトル
	 *@param modal モーダルの場合true
	 *@param config グラフィックス設定
	 */
	public LeafDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration config) {
		super(owner, title, modal, config);
		setEscapeAction();
	}
	/**
	 *親ウィンドウとタイトル、モーダル設定及び
	 *グラフィックス設定を指定してダイアログを生成します。
	 *@param owner 親
	 *@param title タイトル
	 *@param modal モーダル設定
	 *@param config グラフィックス設定
	 */
	public LeafDialog(Window owner, String title, ModalityType modal,
			GraphicsConfiguration config) {
		super(owner, title, modal, config);
		setEscapeAction();
	}
	/**
	 *ダイアログの表示と配置を初期化します。
	 */
	public abstract void init();
	
	/**
	 *contentPaneの推奨されるサイズを設定します。
	 *@param dim 推奨されるサイズ
	 */
	public void setContentSize(Dimension dim){
		getContentPane().setPreferredSize(dim);
		pack();
		Insets insets  = getInsets();
		Dimension size = getSize();
		size.width = dim.width + insets.left + insets.right;
		size.height = dim.height + insets.top + insets.bottom;
		setSize(size);
	}
	/**
	 *{@link LeafLocalizeManager}を利用して現地語に翻訳します。
	 *@param key キーとなる文字列
	 *@return 翻訳された文字列
	 */
	public String translate(String key){
		if(localize == null){
			localize = LeafLocalizeManager.getInstance(getClass());
		}
		return localize.translate(key);
	}
	/**
	 *{@link LeafLocalizeManager}を利用して現地語に翻訳します。
	 *@param key キーとなる文字列
	 *@param args 書式指示子により参照される引数
	 *@return 翻訳された文字列
	 *@see java.util.Formatter
	 */
	public String translate(String key, Object... args){
		if(localize == null){
			localize = LeafLocalizeManager.getInstance(getClass());
		}
		return localize.translate(key, args);
	}
	/**
	 *ダイアログのロケールを返します。
	 *@return ダイアログのロケール
	 *@see LeafLocalizeManager#getLocale()
	 */
	@Override public Locale getLocale(){
		return LeafLocalizeManager.getLocale();
	}
	/**
	 *ESCAPEキーの動作を設定します。
	 */
	private void setEscapeAction(){
		AbstractAction action = new AbstractAction("CLOSE"){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		};
		InputMap map = getRootPane().getInputMap(
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		map.put(KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0), "CLOSE");
		getRootPane().getActionMap().put("CLOSE", action);
	}
	/**
	 *自動的に親の中央に移動してから、ダイアログの表示/非表示を設定します。
	 *@param visible 表示する場合はtrue
	 */
	public void setVisible(boolean visible){
		if(!isVisible() && visible){
			setLocationRelativeTo(getOwner());
		}
		super.setVisible(visible);
	}
	/**
	 *指定されたテキストでメッセージボックスを表示します。
	 *@param message メッセージ
	 */
	public void showMessage(String message){
		JOptionPane.showMessageDialog(
			this, message, getTitle(), JOptionPane.INFORMATION_MESSAGE
		);
	}
	/**OKボタンが選択されたことを示します。*/
	public static final boolean OK_OPTION = true;
	/**キャンセルボタンが選択されたことを示します*/
	public static final boolean CANCEL_OPTION = false;
}
