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
import javax.swing.*;
import java.io.*;

import leaf.components.system.*;
import leaf.components.taskpane.*;
import leaf.manager.LeafLangManager;

/**
*仮想マシン上で発生した全ての標準出力とエラーメッセージを自動で表示するダイアログです。<br>
*{@link #setSystemOutAndErr()}メソッドでこのダイアログを出力先に設定できます。<br>
*このメソッドを実行すると、以後、このダイアログを閉じていても出力があれば自動で表示されます。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*@see LeafSystemOutArea
*/
public class LeafSystemOutDialog extends LeafDialog implements SystemOutListener{
	private final LeafTaskPane taskpane;
	private final LeafSystemOutArea out,err;
	private final LeafExpandPane outpane,errpane;
	/**
	*親フレームを指定してダイアログを生成します。
	*@param frame 親フレーム
	*/
	public LeafSystemOutDialog(Frame frame){
		super(frame,LeafLangManager.get("Console Out","コンソール出力"),false);
		setSize(640,360);
		
		taskpane = new LeafTaskPane();
		add(taskpane,BorderLayout.CENTER);
		
		out = new LeafSystemOutArea();
		out.addSystemOutListener(this);
		
		outpane = new LeafExpandPane
		(LeafLangManager.get("Standard Out","標準出力")){
			public JComponent setContent(){
				return new JScrollPane(out);
			}
		};
		
		err = new LeafSystemOutArea();
		err.addSystemOutListener(this);
		
		errpane = new LeafExpandPane
		(LeafLangManager.get("Error Out","エラー出力")){
			public JComponent setContent(){
				return new JScrollPane(err);
			}
		};
		
		taskpane.addComp(outpane);
		taskpane.addComp(errpane);
	}
	/**
	*このダイアログを初期化します。
	*/
	public void init(){
		this.setTitle(LeafLangManager.get("Console Out","コンソール出力"));
		out.setText("");
		err.setText("");
	}
	/**
	*このダイアログに全ての標準出力とエラーメッセージを表示するように設定します。<br>
	*以後、出力があるたびにこのダイアログが自動で再表示されます。
	*/
	public void setSystemOutAndErr(){
		out.setSystemOut();
		err.setSystemErr();
	}
	/**
	*このダイアログを表示します。
	*/
	public void setVisible(boolean opt){
		out.setText("");
		err.setText("");
		super.setVisible(opt);
	}
	/**
	*出力があった際に{@link LeafSystemOutArea}によって呼び出されます。
	*/
	public synchronized void printed(SystemOutEvent e){
		if(!isVisible())setVisible(true);
		outpane.setExpanded(e.getType()==SystemOutEvent.OUT);
		errpane.setExpanded(e.getType()==SystemOutEvent.ERR);
	}
}
