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
import java.beans.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.*;

import leaf.manager.LeafLangManager;

/**
*画像プレビュー機能、文字コード指定用コンボボックスを持ったファイルチューザです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月30日
*@see chooser.LeafActiveFileFilter
*/
public class LeafFileChooser extends JFileChooser implements PropertyChangeListener{

	/**フィールド*/
	private JPanel mainpanel,subpanel;
	private JScrollPane scroll;
	private JLabel label1,label2,label3;
	private JComboBox comb;
	private final String[] encodings = {"SJIS","JIS","EUC-JP","UTF8","UTF-16"};

	/**
	*LeafFileChooserを作成します。
	*/
	public LeafFileChooser(){
		super();
		init();
	}
	/**チューザを初期化します。*/
	public void init(){
		this.setFileHidingEnabled(true);
		this.setDragEnabled(true);
		/*プレビュー*/
		mainpanel = new JPanel();
		label1 = new JLabel("  "+LeafLangManager.get("Image","画像"));
		label1.setPreferredSize(new Dimension(200,20));
		label2 = new JLabel();
		scroll = new JScrollPane(label2);
		label3 = new JLabel(" "+LeafLangManager.get("Character Code","文字コード：")+" ");
		comb   = new JComboBox(encodings);
		comb.setEditable(false);
		subpanel = new JPanel();
		subpanel.setPreferredSize(new Dimension(200,20));
		subpanel.setLayout(new BorderLayout());
		subpanel.add(label3,BorderLayout.WEST);
		subpanel.add(comb,BorderLayout.CENTER);
		mainpanel.setLayout(new BorderLayout());
		mainpanel.add(label1,BorderLayout.NORTH);
		mainpanel.add(scroll,BorderLayout.CENTER);
		mainpanel.add(subpanel,BorderLayout.SOUTH);
		mainpanel.setPreferredSize(new Dimension(200,0));
		this.setAccessory(mainpanel);
		this.addPropertyChangeListener(this);
	}
	/**ダイアログのリサイズを制限する目的でオーバーライドされます。*/
	protected JDialog createDialog(Component parent) throws HeadlessException{
		JDialog dialog = super.createDialog(parent);
		dialog.setResizable(false);
		return dialog;
	}
	/**画像プレビューの表示を更新するために実装されます。*/
	public void propertyChange(PropertyChangeEvent e) {
		File file = getSelectedFile();
		try{
			label2.setIcon(new ImageIcon(file.getPath()));
		}catch(Exception ex){
			label2.setIcon(null);
		}
	}
	/**
	*ユーザーにより選択された文字コードを取得します。
	*@return 文字コードを表す文字列
	*/
	public String getSelectedEncoding(){
		return ((String)comb.getSelectedItem());
	}
}
