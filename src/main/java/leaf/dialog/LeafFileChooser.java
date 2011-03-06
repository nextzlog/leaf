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
package leaf.dialog;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

import leaf.manager.LeafCharsetManager;
import leaf.manager.LeafLangManager;

/**
*画像プレビュー機能、文字コード指定用コンボボックスを持ったファイルチューザです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月30日
*@see leaf.manager.LeafFileFilter
*/
public class LeafFileChooser extends JFileChooser{

	/**フィールド*/
	private JPanel mainpanel,subpanel;
	private JScrollPane scroll;
	private JLabel label1,label2,label3;
	private JComboBox comb;

	/**
	*チューザを生成します。
	*/
	public LeafFileChooser(){
		super();
		init();
	}
	/**チューザを初期化します。*/
	public void init(){
		
		setFileHidingEnabled(true);
		setDragEnabled(true);
		
		searchAndClickDetailButton();
		
		/*プレビュー*/
		label1 = new JLabel(LeafLangManager.get("Image","画像"),JLabel.CENTER);
		label1.setPreferredSize(new Dimension(200,20));
		label2 = new JLabel();
		scroll = new JScrollPane(label2);
		
		/*文字コード指定*/
		label3 = new JLabel(
			LeafLangManager.get("Character Code","文字コード："), JLabel.CENTER
		);
		comb   = new JComboBox(LeafCharsetManager.getCharsetNames());
		comb.setEditable(false);
		
		/*アクセサリ*/
		mainpanel = new JPanel();
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
		
		setAccessory(mainpanel);
		
		addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent e) {
				File file = getSelectedFile();
				try{
					ImageIcon icon = new ImageIcon(getSelectedFile().getPath());
					if(icon.getIconWidth()>label2.getWidth()){
						Image img = icon.getImage().getScaledInstance(
							label2.getWidth(),-1,Image.SCALE_FAST
						);
						icon = new ImageIcon(img);
					}
					label2.setIcon(icon);
				}catch(Exception ex){
					label2.setIcon(null);
				}
			}
		});
	}
	/**ダイアログのリサイズを制限する目的でオーバーライドされます。*/
	protected JDialog createDialog(Component parent) throws HeadlessException{
		JDialog dialog = super.createDialog(parent);
		dialog.pack();
		dialog.setResizable(false);
		return dialog;
	}
	/**
	*ユーザーにより選択された文字コードを取得します。
	*@return 文字コードを表す文字列
	*/
	public String getSelectedEncoding(){
		return ((String)comb.getSelectedItem());
	}
	/**
	*詳細ボタンを自動でクリックします。
	*/
	private void searchAndClickDetailButton(){
		searchAndClickButton(this, UIManager.getIcon("FileChooser.detailsViewIcon"));
	}
	/**
	*アイコンを指定してボタンを検索し、自動でクリックします。
	*@param parent 検索元コンテナ
	*@param icon ボタンのアイコン
	*/
	private boolean searchAndClickButton(Container parent, Icon icon){
		for(Component comp : parent.getComponents()){
			if(comp instanceof JToggleButton && ((JToggleButton)comp).getIcon() == icon){
				((AbstractButton)comp).doClick();
				return true;
			}else{
				if(searchAndClickButton((Container)comp, icon)) return true;
			}
		}
		return false;
	}
}
