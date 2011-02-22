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
package leaf.components.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;

import leaf.manager.*;

/**
*「最近使ったファイル」メニューを簡単に実現するための{@link JMenu}です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月6日
*@see HistoryMenuListener
*/
public class LeafHistoryMenu extends JMenu{
	/**
	*表示される履歴の最大数です。10に固定されています。
	*/
	public static final int HISTORY_MAX = 10;
	/**秘匿フィールド*/
	private ArrayList<String> files;
	private final HistoryMenuListener listener;
	/**
	*{@link HistoryMenuListener HistoryMenuListener}と初期に表示される履歴を指定して履歴メニューを生成します。<br>
	*使用する言語は、{@link LeafLangManager LeafLangManager}によって自動で選択されます。
	*@param lis {@link HistoryMenuListener HistoryMenuListener}
	*@param list ファイル履歴を表すArrayList<String>
	*/
	public LeafHistoryMenu(HistoryMenuListener lis,ArrayList<String> list){
		super(LeafLangManager.get("History","最近使ったファイル(H)"));
		this.setMnemonic(KeyEvent.VK_H);
		this.files = list;
		this.listener = lis;
		if(files==null||files.size()<=0){
			this.setEnabled(false);
			return;
		}
		for(int i=files.size()-1;i>=0;i--){
			this.update(files.get(i));
		}
	}
	/**
	*新しいファイルを指定して履歴メニューを更新します。
	*@param newpath 新しいファイルのパス
	*/
	public void update(String newpath){
		this.setEnabled(true);
		this.removeAll();
		files.remove(newpath);
		files.add(0,newpath);
		if(files.size()>HISTORY_MAX)files.remove(files.size()-1);
		for(int i=0;i<files.size();i++){
			String index = String.valueOf(i);
			/*KeyStrokeへの変換*/
			byte[] bt = index.getBytes();
			final String path = files.get(i);
			JMenuItem item = new JMenuItem(index+" "+path);
			item.setMnemonic((int)bt[0]);
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					listener.historyClicked(path);
				}
			});
			this.add(item,i);
		}
	}
	/**
	*表示されているファイル履歴を返します。
	*@return ファイル履歴を表すArrayList<String>
	*/
	public ArrayList<String> getHistFilePaths(){
		return files;
	}
}
