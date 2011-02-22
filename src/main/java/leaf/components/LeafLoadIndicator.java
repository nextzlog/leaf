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
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.JLabel;

import leaf.icon.*;

/**
*作業が進行していることを視覚的に通知するコンポーネントです。
*バックグラウンドスレッド上でインジケータを回転させます。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月23日
*@see LeafLoadIndicatorIcon
*/
public class LeafLoadIndicator extends JLabel implements ActionListener{
	
	/**秘匿フィールド*/
	private final Timer timer;
	private final LeafLoadIndicatorIcon icon = new LeafLoadIndicatorIcon();
	
	/**
	*インジケータを生成します。
	*/
	public LeafLoadIndicator(){
		super();
		timer = new Timer(100,this);
		this.setIcon(icon);
	}
	public void actionPerformed(ActionEvent e){
		icon.next();
		this.repaint();
	}
	/**
	*インジケータの状態を返します。
	*@return インジケータが回転中の場合、trueを返します。
	*/
	public boolean isRunning(){
		return icon.isRunning();
	}
	/**
	*インジケータの回転を開始します。
	*/
	public void start(){
		icon.setRunning(true);
		timer.start();
	}
	/**
	*インジケータの回転を終了します。
	*/
	public void stop(){
		icon.setRunning(false);
		timer.stop();
	}
}
