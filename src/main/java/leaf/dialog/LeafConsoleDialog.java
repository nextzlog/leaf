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
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.text.JTextComponent;

import leaf.icon.LeafIcons;
import leaf.components.LeafButtons;
import leaf.components.system.*;
import leaf.components.taskpane.*;
import leaf.manager.LeafLangManager;

/**
*仮想マシン上で発生した全ての標準出力とエラーメッセージを自動で表示するダイアログです。
*<br>{@link #setSystemOutAndErr()}メソッドでこのダイアログを出力先に設定できます。
*このメソッドの実行後は、出力に応じてダイアログが自動で表示されます。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*@see LeafSystemOutArea
*/
public final class LeafConsoleDialog 
	extends LeafDialog implements ActionListener,SystemOutListener{
	
	private final LeafTaskPane taskpane;
	private final LeafSystemOutArea out,err;
	private LeafExpandPane outpane,errpane;
	private LeafSearchDialog searchdialog;
	
	private final JMenuBar menubar;
		private final LeafIcons icons = new LeafIcons();
	private LeafSystemOutArea textpane;
	/**
	*親フレームを指定してダイアログを生成します。
	*@param frame 親フレーム
	*/
	public LeafConsoleDialog(Frame frame){
		super(frame,LeafLangManager.get("Console Out","コンソール出力"),false);
		getContentPane().setPreferredSize(new Dimension(620,310));
		pack();
		setResizable(false);
		
		taskpane = new LeafTaskPane();
		add(taskpane,BorderLayout.CENTER);
		
		out = new LeafSystemOutArea();
		out.addSystemOutListener(this);
		
		out.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e){
				textpane = out;
			}
		});
		textpane = out;
		
		err = new LeafSystemOutArea();
		err.addSystemOutListener(this);
		
		err.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e){
				textpane = err;
			}
		});
		
		menubar = new JMenuBar();
		setJMenuBar(menubar);
		menubar.setBorderPainted(false);
		
		init();
		
		searchdialog = new LeafSearchDialog(this);
	}
	/**
	*このダイアログを初期化します。
	*/
	public void init(){
		
		setTitle(LeafLangManager.get("Console Out","コンソール出力"));
		if(searchdialog!=null) searchdialog.init();
		
		outpane = new LeafExpandPane
		(LeafLangManager.get("Standard Out","標準出力")){
			public JComponent setContent(){
				return new JScrollPane(out);
			}
		};
		
		errpane = new LeafExpandPane
		(LeafLangManager.get("Error Out","例外出力")){
			public JComponent setContent(){
				return new JScrollPane(err);
			}
		};
		
		taskpane.removeAll();
		
		taskpane.addComp(outpane);
		taskpane.addComp(errpane);
		
		menubar.removeAll();
		menubar.add(makeFileMenu());
		menubar.add(makeEditMenu());
		
		repaint();
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
		if(!isVisible()){
			out.setText("");
			err.setText("");
		}
		super.setVisible(opt);
		outpane.setExpanded(true);
		errpane.setExpanded(false);
	}
	/**
	*出力があった際に{@link LeafSystemOutArea}によって呼び出されます。
	*/
	public synchronized void printed(SystemOutEvent e){
		if(!isVisible())setVisible(true);
	}
	/**
	*ファイルメニューを生成して返します。
	*@return ファイルメニュー
	*/
	private JMenu makeFileMenu(){
		JMenu menu = LeafButtons.createMenu("File","ファイル",KeyEvent.VK_F);
		menu.add(createMenuItem(
			"Save Standard Out","標準出力を保存",icons.SAVE,"ctrl S",KeyEvent.VK_S));
		menu.add(createMenuItem(
			"Save Standard Err","例外出力を保存",icons.SAVE,"ctrl S",KeyEvent.VK_A));
		menu.addSeparator();
		menu.add(createMenuItem(
			"Print Standard Out","標準出力を印刷",icons.PRINT,"ctrl P",KeyEvent.VK_O));
		menu.add(createMenuItem(
			"Print Standard Err","例外出力を印刷",icons.PRINT,"ctrl shift P",KeyEvent.VK_E));
		menu.addSeparator();
		menu.add(createMenuItem(
			"Close","閉じる",icons.EXIT,"ctrl F4",KeyEvent.VK_X));
		return menu;
	}
	/**
	*編集メニューを生成して返します。
	*@return 編集メニュー
	*/
	private JMenu makeEditMenu(){
		JMenu menu = LeafButtons.createMenu("Edit","編集",KeyEvent.VK_E);
		menu.add(createMenuItem(
			"Copy","コピー",icons.COPY,"ctrl C",KeyEvent.VK_C));
		menu.add(createMenuItem(
			"Select All","全て選択",icons.SELECT_ALL,"ctrl A",KeyEvent.VK_A));
		menu.addSeparator();
		menu.add(createMenuItem(
			"Delete Standard Out","標準出力を消去",icons.DELETE,null,KeyEvent.VK_O));
		menu.add(createMenuItem(
			"Delete Standard Err","例外出力を消去",icons.DELETE,null,KeyEvent.VK_E));
		menu.addSeparator();
		menu.add(createMenuItem(
			"Search","検索",icons.SEARCH,"ctrl F",KeyEvent.VK_F));
		return menu;
	}
	/**
	*メニューアイテムを生成して返します。
	*@return メニューアイテム
	*/
	private JMenuItem createMenuItem(
		String eng,String jpn,String icon,String key,int mnemo){
		return LeafButtons.createMenuItem(eng,jpn,icons.getIcon(icon),this,key,mnemo);
	}
	public void actionPerformed(ActionEvent e){
		String cmd = ((AbstractButton)e.getSource()).getActionCommand();
		if(cmd.equals("Save Standard Out")){
			saveOut(out);
		}else if(cmd.equals("Save Standard Err")){
			saveOut(err);
		}else if(cmd.equals("Print Standard Out")){
			printOut(out);
		}else if(cmd.equals("Print Standard Err")){
			printOut(err);
		}else if(cmd.equals("Close")){
			dispose();
		}else if(cmd.equals("Copy")){
			copy();
		}else if(cmd.equals("Select All")){
			selectAll();
		}else if(cmd.equals("Delete Standard Out")){
			out.setText("");
		}else if(cmd.equals("Delete Standard Err")){
			err.setText("");
		}else if(cmd.equals("Search")){
			search();
		}
	}
	/**出力を保存*/
	private void saveOut(JTextComponent area){
		LeafFileChooser chooser = new LeafFileChooser();
		
		if(chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
		
		FileOutputStream stream = null;
		OutputStreamWriter oswriter = null;

		try{
			stream = new FileOutputStream(chooser.getSelectedFile());
			oswriter = new OutputStreamWriter(stream,chooser.getSelectedEncoding());
			area.write(oswriter);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				oswriter.close();
				stream.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	/**出力を印刷*/
	private void printOut(JTextComponent area){
		try{
			area.print();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**コピー*/
	private void copy(){
		textpane.copy();
		textpane.requestFocusInWindow();
	}
	/**全て選択*/
	private void selectAll(){
		textpane.selectAll();
		textpane.requestFocusInWindow();
	}
	/**検索*/
	private void search(){
		searchdialog.showDialog(textpane);
	}
}
