/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.components;

import java.awt.*;
import javax.swing.*;

/**
*フレームです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年8月9日
*/
public class LeafFrame extends JFrame{

	/**
	*新しいフレームを生成します。
	*/
	public LeafFrame(){
		super();
	}
	/**
	*指定されたGraphicsConfiguration でフレームを生成します。
	*@param gc 新しいフレームを生成するために使用されるGraphicsConfiguration
	*/
	public LeafFrame(GraphicsConfiguration gc){
		super(gc);
	}
	/**
	*指定されたタイトルでフレームを生成します。
	*@param title タイトル
	*/
	public LeafFrame(String title){
		super(title);
	}
	/**
	*指定されたタイトルとGraphicsConfiguration でフレームを生成します。
	*@param title タイトル
	*@param gc 新しいフレームを生成するために使用されるGraphicsConfiguration
	*/
	public LeafFrame(String title, GraphicsConfiguration gc){
		super(title, gc);
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
			setExtendedState(Frame.MAXIMIZED_BOTH);
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
	*フレームがフルスクリーン表示されているか返します。
	*@return フルスクリーンの場合true
	*/
	public boolean isFullScreen(){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		if(gd == null) return false;
		return (this == gd.getFullScreenWindow());
	}
}