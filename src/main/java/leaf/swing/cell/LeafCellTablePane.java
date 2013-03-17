/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.cell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import leaf.util.lang.LocalizeManager;

/**
 * {@link LeafCellTable}によるシミュレーションを容易にするパネルです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.4 作成：2012年3月17日
 *
 */
public class LeafCellTablePane extends JComponent {
	private static final long serialVersionUID = 1L;
	private Box console;
	private JButton bopen, bsave, bstart, breset;
	private LeafCellTable table;
	
	private JFileChooser chooser;
	private LocalizeManager localize;
	
	/**
	 * パネルを構築します。
	 */
	public LeafCellTablePane() {
		setLayout(new BorderLayout(5, 5));
		
		localize = LocalizeManager.get(LeafCellTablePane.class);
		
		String filterName = localize.translate("filechooser_filter_name");
		FileFilter filter = new FileNameExtensionFilter(filterName, "bin");
		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(filter);
		
		table = new LeafCellTable();
		add(table, BorderLayout.CENTER);
		
		bopen  = new JButton(localize.translate("button_open"));
		bsave  = new JButton(localize.translate("button_save"));
		bstart = new JButton(localize.translate("button_start"));
		breset = new JButton(localize.translate("button_reset"));
		
		Dimension bsize = bopen.getPreferredSize();
		bopen.setPreferredSize(new Dimension(100, bsize.height));
		bsave.setPreferredSize(new Dimension(100, bsize.height));
		bstart.setPreferredSize(new Dimension(100, bsize.height));
		breset.setPreferredSize(new Dimension(100, bsize.height));
		
		console = Box.createHorizontalBox();
		console.add(Box.createHorizontalStrut(5));
		console.add(bopen);
		console.add(Box.createHorizontalStrut(5));
		console.add(bsave);
		console.add(Box.createHorizontalGlue());
		console.add(bstart);
		console.add(Box.createHorizontalStrut(5));
		console.add(breset);
		console.add(Box.createHorizontalStrut(5));
		console.add(Box.createRigidArea(new Dimension(0, bsize.height + 10)));
		
		add(console, BorderLayout.SOUTH);
		
		bopen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(getCellAutomata() != null) openPattern();
			}
		});
		
		bsave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(getCellAutomata() != null) savePattern();
			}
		});
		
		bstart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setAutoUpdateEnabled(!isAutoUpdateEnabled());
			}
		});
		
		breset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setAutoUpdateEnabled(false);
				table.init();
			}
		});
	}
	
	/**
	 * このパネルが内蔵するセルテーブルを返します。
	 * 
	 * @return セルテーブル
	 */
	public LeafCellTable getInternalCellTable() {
		return table;
	}
	
	/**
	 * このパネルが内蔵するセルテーブルにセルオートマータを設定します。
	 * 
	 * @param automata オートマータ
	 */
	public void setCellAutomata(Automata automata) {
		table.setCellAutomata(automata);
	}
	
	/**
	 * このパネルが内蔵するセルテーブルのセルオートマータを返します。
	 * 
	 * @return セルオートマータ
	 */
	public Automata getCellAutomata() {
		return table.getCellAutomata();
	}
	
	/**
	 * セルテーブルの世代更新周期をミリ秒単位で設定します。
	 * 
	 * @param ms 世代更新周期
	 * @throws IllegalArgumentException 正数でない周期を指定した場合
	 */
	public void setAutoUpdateInterval(int ms) throws IllegalArgumentException{
		table.setAutoUpdateInterval(ms);
	}
	
	/**
	 * セルテーブルの世代更新周期の設定値をミリ秒単位で返します。
	 * 
	 * @return 世代更新周期
	 */
	public int getAutoUpdateInterval() {
		return table.getAutoUpdateInterval();
	}
	
	/**
	 * セルテーブルの自動的な世代更新を開始または停止します。
	 * 
	 * @param b 開始する場合は真 停止する場合は偽
	 */
	public synchronized void setAutoUpdateEnabled(boolean b) {
		table.setAutoUpdateEnabled(b);
		if(b) {
			bstart.setText(localize.translate("button_pause"));
		} else {
			bstart.setText(localize.translate("button_start"));
		}
	}
	
	/**
	 * セルテーブルの自動的な世代更新動作が稼働中であるか返します。
	 * 
	 * @return 稼働中である場合は真 停止中である場合は偽
	 */
	public boolean isAutoUpdateEnabled() {
		return table.isAutoUpdateEnabled();
	}
	
	/**
	 * ファイル選択画面を表示して、パターンをファイルから読み込みます。
	 */
	private void openPattern() {
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				Pattern pattern = Pattern.read(new FileInputStream(file));
				pattern.setPattern(getCellAutomata());
			} catch(IOException ex) {
				String msg = localize.translate("failed_read_pattern");
				JOptionPane.showMessageDialog(this, msg);
			} catch(IllegalArgumentException ex) {
				String msg = localize.translate("illegal_size_pattern");
				JOptionPane.showMessageDialog(this, msg);
			}
			table.repaint();
		}
	}
	
	/**
	 * ファイル選択画面を表示して、パターンをファイルに保存します。
	 */
	private void savePattern() {
		if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				Pattern pattern = Pattern.getPattern(getCellAutomata());
				pattern.write(new FileOutputStream(file));
			} catch(IOException ex) {
				String msg = localize.translate("failed_save_pattern");
				JOptionPane.showMessageDialog(this, msg);
			}
		}
	}

}