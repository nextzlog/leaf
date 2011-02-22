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

import leaf.icon.*;
import leaf.components.*;
import leaf.components.taskpane.*;
import leaf.components.text.*;
import leaf.dialog.LeafConsoleDialog;
import leaf.document.*;

/**
*デモ用アプリケーション
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年7月10日
*/

public class Demo extends LeafFrame{
	public static void main(String[] args){
		setDefaultLookAndFeel();
		new Demo().setVisible(true);
	}
	public Demo(){
		super("Great Program World !!");
		setSize(400,600);
		setIconImage(new LeafIcons().getIcon("logo").getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		LeafTaskPane taskpane = new LeafTaskPane();
		add(taskpane,BorderLayout.CENTER);
		
		LeafExpandPane panel1 = new LeafExpandPane("LeafTextPane + LeafTextScrollPane"){
			public JComponent setContent(){
				JPanel cont = new JPanel(new BorderLayout());
				LeafTextPane textpane = new LeafTextPane();
				textpane.setLineCursorVisible(true);
				textpane.setEOFVisible(true);
				textpane.setEditorKit(LeafStyledDocument.getEditorKit());
				((LeafStyledDocument)textpane.getDocument()).setAutoIndentEnabled(true);
				textpane.setText("Auto Indent.\n\t");
				textpane.setFont(new Font(Font.MONOSPACED,Font.PLAIN,16));
				LeafTextScrollPane scroll = new LeafTextScrollPane(textpane);
				scroll.getViewport().setOpaque(true);
				scroll.getViewport().setBackground(Color.WHITE);
				cont.add(scroll);
				return cont;
			}
		};
		taskpane.addComp(panel1);
		panel1.setExpanded(true);
		
		LeafExpandPane panel2 = new LeafExpandPane("JTextArea + LeafTextScrollPane"){
			public JComponent setContent(){
				JPanel cont = new JPanel(new BorderLayout());
				JTextArea textarea = new JTextArea();
				textarea.setFont(new Font(Font.MONOSPACED,Font.PLAIN,16));
				LeafTextScrollPane scroll = new LeafTextScrollPane(textarea);
				scroll.getViewport().setBackground(Color.WHITE);
				cont.add(scroll);
				return cont;
			}
		};
		taskpane.addComp(panel2);
		
		LeafExpandPane panel3 = new LeafExpandPane("Welcome"){
			public JComponent setContent(){
				JPanel cont = new JPanel(new BorderLayout());
				JLabel label = new JLabel(
					"<html><center>Under the GPL (GNU General Public License)</center>"
					+"<br>Copyright(C) 2010 by University of Tokyo Amateur Radio Club"
					+"<center>See the 'license.txt' for the license details</center>",
					new LeafIcons().getIcon("welcome"),
					JLabel.CENTER
				);
				label.setHorizontalTextPosition(JLabel.CENTER);
				label.setVerticalTextPosition(JLabel.BOTTOM);
				cont.add(label);
				return cont;
			}
		};
		taskpane.addComp(panel3);
		panel3.setExpanded(true);
		
		final JButton button = new JButton("Full Screen");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				boolean fs = !isFullScreen();
				setFullScreen(fs);
				button.setText(fs?"Normal Screen":"Full Screen");
			}
		});
		add(button,BorderLayout.SOUTH);
		
		new LeafConsoleDialog(this).setSystemOutAndErr();
	}
	private static void setDefaultLookAndFeel(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ex){}
	}
}