/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.main;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import leaf.icon.LeafIcons;
import leaf.veins.app.Application;

/**
 * LeafVeinsアプリケーションのメインウィンドウです。
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/03/27 
 *
 */
@SuppressWarnings("serial")
public final class MainFrame extends JFrame {
	private static MainFrame instance;
	
	private final JPanel centerpane;
	private final MainStatusBar statusbar;
	private JToolBar toolbar;
	
	/**
	 * メインウィンドウのインスタンスを返します。
	 * 
	 * {@link Application}からの利用は推奨されません。
	 * 
	 * @return メインウィンドウ
	 */
	public static MainFrame getInstance() {
		if(instance == null) instance = new MainFrame();
		return instance;
	}
	
	protected MainFrame() {
		setIconImage(LeafIcons.getImage("LUNA"));
		setMinimumSize(new Dimension(500, 250));
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		centerpane = new JPanel(new BorderLayout());
		statusbar = new MainStatusBar();
		
		add(centerpane, BorderLayout.CENTER);
		add(statusbar, BorderLayout.SOUTH);
	}
	
	/**
	 * ウィンドウ中央のパネルを返します。
	 * 
	 * @return パネル
	 */
	public JPanel getCenterPane(){
		return centerpane;
	}
	
	/**
	 * ウィンドウ下部のステータスバーを返します。
	 * 
	 * @return ステータスバー
	 */
	public MainStatusBar getStatusBar(){
		return statusbar;
	}

	/**
	 * ウィンドウ上部のツールバーを設定します。
	 * 
	 * @param toolbar ツールバー
	 */
	public void setJToolBar(JToolBar toolbar) {
		final JToolBar old = this.toolbar;
		
		if(this.toolbar != null) remove(this.toolbar);
		add(toolbar, BorderLayout.NORTH);
		this.toolbar = toolbar;
		
		firePropertyChange("jToolBar", old, toolbar);
	}
	
	/**
	 * ウィンドウ上部のツールバーを返します。
	 * 
	 * @return ツールバー
	 */
	public JToolBar getJToolBar(){
		return toolbar;
	}
	
	/**
	 * ウィンドウの表示を初期化します。
	 * 
	 */
	public void initialize() {
		statusbar.initialize();
	}

}
