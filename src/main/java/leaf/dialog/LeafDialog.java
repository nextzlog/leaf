/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
*エスケープキーで閉じる機能を持ったダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*/
public class LeafDialog extends JDialog{
	
	/**OKボタンが選択されたことを示します。*/
	public static final boolean OK_OPTION = true;
	/**キャンセルボタンが選択されたことを示します*/
	public static final boolean CANCEL_OPTION = false;
	private final Frame parent;
	/**
	*親フレームとタイトル、モーダルオプションを指定してダイアログを生成します。
	*@param frame ダイアログの親フレーム
	*@param title ダイアログのタイトル
	*@param modal モーダルダイアログの場合はtrue
	*/
	public LeafDialog(Frame frame,String title,boolean modal){
		super(frame,title,modal);
		parent = frame;
		AbstractAction act = new AbstractAction("CLOSE"){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		};
		InputMap map = getRootPane().getInputMap(
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"CLOSE");
		getRootPane().getActionMap().put("CLOSE",act);
	}
	/**
	*自動的に親フレームの中央に移動してから、ダイアログの表示/非表示を設定します。
	*@param visible 表示する場合はtrue
	*/
	public void setVisible(boolean visible){
		if(visible)this.setLocation(
			parent.getX()+(parent.getWidth()-getWidth())/2,
			parent.getY()+(parent.getHeight()-getHeight())/2
		);
		super.setVisible(visible);
	}
	/**
	*ダイアログをフルスクリーン表示するか設定します。
	*@param isFullScreen フルスクリーンの場合true
	*/
	public void setFullScreen(boolean isFullScreen){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		if(gd.getFullScreenWindow()==null){
			dispose();
			setUndecorated(true);
			setVisible(true);
			gd.setFullScreenWindow(this);
		}else{
			gd.setFullScreenWindow(null);
			dispose();
			setUndecorated(false);
			setVisible(true);
			repaint();
		}
		requestFocusInWindow();
	}
	/**
	*ダイアログがフルスクリーン表示されているか返します。
	*@return フルスクリーンの場合true
	*/
	public boolean isFullScreen(){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		if(gd == null) return false;
		return (this == gd.getFullScreenWindow());
	}
}
