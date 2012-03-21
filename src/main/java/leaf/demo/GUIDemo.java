/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import leaf.document.KeywordSet;
import leaf.document.LeafSyntaxDocument;
import leaf.icon.LeafIcons;
import leaf.swing.label.LeafScrollLabel;
import leaf.swing.tabbedpane.LeafTabbedPane;
import leaf.swing.taskpane.LeafExpandPane;
import leaf.swing.taskpane.LeafTaskPane;
import leaf.swing.text.LeafTextPane;
import leaf.swing.text.LeafTextScrollPane;

/**
 * LeafSwingAPIのデモンストレーションプログラム
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年7月10日
 */

@SuppressWarnings("serial") final class GUIDemo extends JFrame {
	private String text = "public void leaf(){\n\t//AutoIndent.\n}";
	private LeafTextPane textpane;
	private String copyleft = "";
	
	public GUIDemo(){
		super("Leaf Demo");
		setSize(400, 600);
		setLocationRelativeTo(null);
		setIconImage(LeafIcons.getImage("LUNA"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		LeafTabbedPane tabpane = new LeafTabbedPane();
		add(tabpane, BorderLayout.CENTER);
		
		try{
			copyleft = Copyleft.read();
		}catch(Exception ex){ex.printStackTrace();}
		
		tabpane.add("LeafTabbedPane", createTab());
		
		setVisible(true);
		textpane.requestFocusInWindow();
	}
	
	private Component createTab(){
		LeafTaskPane taskpane = new LeafTaskPane();
		
		LeafExpandPane panel1 = new LeafExpandPane
		("LeafTextPane + LeafTextScrollPane + LeafSyntaxDocument"){
			@Override protected JComponent createContent() {
				LeafSyntaxDocument doc = new LeafSyntaxDocument();
				KeywordSet set = new KeywordSet();
				set.setCommentLineStart("//");
				set.setKeywords(Arrays.asList("public", "void"));
				doc.setKeywordSet(set);
				
				textpane = new LeafTextPane(doc);
				doc.setAutoIndentEnabled(true);
				
				textpane.setCaretMode(LeafTextPane.CARET_REPLACE_MODE);
				textpane.setText(text);
				
				textpane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
				return new LeafTextScrollPane(textpane);
			}
		};
		taskpane.addComp(panel1);
		panel1.setExpanded(true);
		
		LeafExpandPane panel2 = new LeafExpandPane("LeafTextPane + LeafExpandPane"){
			@Override protected JComponent createContent() {
				JLabel label = new JLabel(copyleft,
					new ImageIcon(LeafIcons.getImage("LEAF")), JLabel.CENTER);
				label.setHorizontalTextPosition(JLabel.CENTER);
				label.setVerticalTextPosition(JLabel.BOTTOM);
				
				label.setBackground(Color.WHITE);
				label.setOpaque(true);
				return label;
			}
		};
		taskpane.addComp(panel2);
		panel2.setExpanded(true);
		
		LeafScrollLabel label = new LeafScrollLabel("Hello, great program world!");
		label.setRepaintInterval(30);
		taskpane.addComp(label);
		label.start();
		
		return taskpane;
	}
}