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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.FileReader;
import java.util.ArrayList;

import leaf.components.LeafFrame;
import leaf.components.shell.LeafShellPane;
import leaf.components.tabbedpane.LeafTabbedPane;
import leaf.components.taskpane.LeafExpandPane;
import leaf.components.taskpane.LeafTaskPane;
import leaf.components.text.LeafTextPane;
import leaf.components.text.LeafTextScrollPane;
import leaf.document.LeafStyledDocument;
import leaf.document.KeywordSet;
import leaf.icon.LeafIcons;
import leaf.script.arice.AriceScriptEngine;

/**
*デモ用アプリケーション
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年7月10日
*/

public class Demo extends LeafFrame{
	
	private LeafTextPane textpane;
	private String text = "public void leaf(){\n\t//AutoIndent.\n}";
	
	private String copyleft 
	= "<html><center>Under the GPL (GNU General Public License)</center>"
	+"<br>Copyright(C) 2010 by University of Tokyo Amateur Radio Club"
	+"<center>See the 'license.txt' for the license details</center>";
	
	public static void main(String[] args){
		if(args.length > 0)runScript(args[0]);
		else{
			setDefaultLookAndFeel();
			new Demo();
		}
	}
	public Demo(){
		super("Great Program World !!");
		setSize(400,600);
		setIconImage(new LeafIcons().getIcon("logo").getImage());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		LeafTabbedPane tabpane = new LeafTabbedPane();
		add(tabpane, BorderLayout.CENTER);
		
		tabpane.add("<html><b>LeafTabbedPane", createComponentDemo());
		
		final JButton button = new JButton("Full Screen");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				boolean fs = !isFullScreen();
				setFullScreen(fs);
				button.setText(fs?"Normal Screen":"Full Screen");
			}
		});
		add(button,BorderLayout.SOUTH);
		
		setVisible(true);
		textpane.requestFocusInWindow();
	}
	private static void setDefaultLookAndFeel(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ex){}
	}
	/**
	*AriCE実行環境
	*/
	private static void runScript(String src){
		AriceScriptEngine engine = new AriceScriptEngine();
		try{
			System.out.println("===========EVALUATE==========");
			final long start = System.nanoTime();
			System.out.println("=>" + engine.eval(new FileReader(src)));
			System.out.println("=>" + (System.nanoTime() - start) + "NS");
			System.out.println(engine.disassemble());
		}catch(Exception ex){
			System.out.println("============ERROR============");
			System.out.println(ex.getMessage());
		}
	}
	/**
	*コンポーネント関連のデモ
	*/
	private LeafTaskPane createComponentDemo(){
		LeafTaskPane taskpane = new LeafTaskPane();
		LeafExpandPane panel1 = new LeafExpandPane(
			"LeafTextPane + LeafTextScrollPane + LeafStyledDocument"){
			public JComponent setContent(){
				JPanel border = new JPanel(new BorderLayout());
				LeafStyledDocument doc = createStyledDocument();
				textpane  = new LeafTextPane(doc);
				doc.setAutoIndentEnabled(true);
				textpane.setInsertMode(false);
				textpane.setText(text);
				textpane.setFont(new Font(Font.MONOSPACED,Font.PLAIN,16));
				LeafTextScrollPane scroll = new LeafTextScrollPane(textpane);
				scroll.getViewport().setOpaque(true);
				scroll.getViewport().setBackground(Color.WHITE);
				border.add(scroll, BorderLayout.CENTER);
				return border;
			}
		};
		taskpane.addComp(panel1);
		panel1.setExpanded(true);
		
		LeafExpandPane panel2 = new LeafExpandPane("LeafTaskPane + LeafExpandPane"){
			public JComponent setContent(){
				JLabel label = new JLabel(
					copyleft, new LeafIcons().getIcon("welcome"), JLabel.CENTER);
				label.setHorizontalTextPosition(JLabel.CENTER);
				label.setVerticalTextPosition(JLabel.BOTTOM);
				return label;
			}
		};
		taskpane.addComp(panel2);
		panel2.setExpanded(true);
		return taskpane;
	}
	/**
	*ドキュメント
	*/
	private LeafStyledDocument createStyledDocument(){
		LeafStyledDocument doc = new LeafStyledDocument();
		KeywordSet set = new KeywordSet();
		ArrayList<String> list = set.getKeywords();
		list.add("public");
		list.add("void");
		set.setCommentLineStart("//");
		doc.setKeywordSet(set);
		return doc;
	}
}