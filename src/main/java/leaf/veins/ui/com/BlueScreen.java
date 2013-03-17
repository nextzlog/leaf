/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.com;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

import leaf.swing.com.LeafWrappedLabel;
import leaf.veins.util.WindowUtils;

/**
 * エラーが発生した場合に全画面表示される青画面です。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/03/30 
 *
 */
public class BlueScreen extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JLabel titleLabel;
	private final LeafWrappedLabel bodyLabel;

	/**
	 * タイトルと警告文字列を指定して青画面を構築します。
	 * 
	 * @param title タイトル
	 * @param text  警告文字列
	 */
	public BlueScreen(String title, String text) {
		super(title);
		titleLabel = new JLabel(title, JLabel.CENTER);
		bodyLabel  = new LeafWrappedLabel(text);
		addKeyListener(new EscapeHandler());
		createContentPane();
		WindowUtils.setFullScreen(this, true);
	}
	
	private void createContentPane() {
		Container pane = getContentPane();
		pane.setBackground(new Color(45, 45, 250));
		
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 120));
		pane.add(titleLabel, BorderLayout.NORTH);
		
		bodyLabel.setForeground(Color.WHITE);
		bodyLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 48));
		pane.add(bodyLabel, BorderLayout.CENTER);
	}
	
	private class EscapeHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				dispose();
			}
		}
	}

}
