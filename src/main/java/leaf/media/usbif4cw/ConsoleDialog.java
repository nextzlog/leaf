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
package leaf.media.usbif4cw;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import leaf.components.text.*;
import leaf.dialog.LeafDialog;
import leaf.manager.*;

/**
*USBIF4CWをアプリケーションから簡単に操作するためのダイアログです。<br>
*必要なDLL：usbif4cw.dll
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年10月11日
*/

public final class ConsoleDialog extends LeafDialog{
	
	private JLabel speedlb;
	private JSlider speedsld;
	private LeafTextPane textpane;
	
	/**
	*親フレームを指定してモーダレスなダイアログを生成します。
	*@param owner 親フレーム
	*/
	public ConsoleDialog(Frame owner){
		this(owner, 100);
	}
	/**
	*親ダイアログを指定してモーダレスなダイアログを生成します。
	*@param owner 親ダイアログ
	*/
	public ConsoleDialog(Dialog owner){
		this(owner, 100);
	}
	/**
	*親フレームと送信速度を指定してダイアログを生成します。
	*@param owner 親フレーム
	*@param speed 送信速度
	*/
	public ConsoleDialog(Frame owner, int speed){
		super(owner, null, false);
		getContentPane().setPreferredSize(new Dimension(400, 150));
		pack();
		setResizable(false);
		setLayout(null);
		
		init(speed);
	}
	/**
	*親ダイアログと送信速度を指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*@param speed 送信速度
	*/
	public ConsoleDialog(Dialog owner,int speed){
		super(owner, null, false);
		getContentPane().setPreferredSize(new Dimension(400, 150));
		pack();
		setResizable(false);
		setLayout(null);
		
		init(speed);
	}
	/**
	*送信速度を指定してダイアログを初期化します。
	*@param speed 読み上げ速度
	*/
	public void init(final int speed){
		setTitle("USBIF4CW " + LeafLangManager.get("Console","コンソール"));
		
		getContentPane().removeAll();
		
		/*読み上げスピード*/
		speedlb = new JLabel(LeafLangManager.get("Speed","速度"));
		speedlb.setBounds(5,10,60,20);
		add(speedlb);
		
		speedsld = new JSlider(50,300,speed);
		speedsld.setBounds(65,10,330,40);
		speedsld.setLabelTable(speedsld.createStandardLabels(50));
		speedsld.setPaintLabels(true);
		add(speedsld);
		
		/*メッセージ入力*/
		textpane = new LeafTextPane();
		textpane.setOpaque(true);
		LeafTextScrollPane scroll = new LeafTextScrollPane(textpane,true,false);
		scroll.setVerticalScrollBarPolicy(scroll.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(5,55,390,85);
		add(scroll);
	}
}