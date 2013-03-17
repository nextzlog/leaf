/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * 配色を設定するために使用するモーダルダイアログです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.2 作成：2011年1月5日
 *
 */
@SuppressWarnings("serial")
public final class LeafColorDialog extends LeafDialog{
	private final ArrayList<ColorPanel> panels;
	private Box box;
	private JScrollPane scroll;
	private JButton bok, bcancel;
	private Map<String, Color>map;
	private boolean isChanged = CANCEL_OPTION;
	
	/**
	 * 親フレームを指定してダイアログを生成します。
	 *
	 * @param owner 親フレーム
	 */
	public LeafColorDialog(Frame owner){
		this(owner, null);
	}
	
	/**
	 * 親ダイアログを指定してダイアログを生成します。
	 *
	 * @param owner 親ダイアログ
	 */
	public LeafColorDialog(Dialog owner){
		this(owner, null);
	}
	
	/**
	 * 親フレームとマップを指定してダイアログを生成します。
	 *
	 * @param owner 親フレーム
	 * @param map マップ
	 */
	public LeafColorDialog(Frame owner, Map<String, Color> map){
		super(owner, true);
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
			}
		});
		
		panels  = new ArrayList<ColorPanel>(5);
		this.map = map;
		
		setLayout(null);
		initialize();
	}
	
	/**
	 * 親ダイアログとマップを指定してダイアログを生成します。
	 *
	 * @param owner 親ダイアログ
	 * @param map マップ
	 */
	public LeafColorDialog(Dialog owner, Map<String, Color> map){
		super(owner, true);
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
			}
		});
		
		panels  = new ArrayList<ColorPanel>(5);
		this.map = map;
		
		setLayout(null);
		initialize();
	}
	
	/**
	 * 色の設定を並べたマップを設定します。
	 *
	 * @param map 配色マップ
	 */
	public void setMap(Map<String, Color> map){
		this.map = map;
		loadSettings();
	}
	
	/**
	 * ユーザーにより設定された配色を返します。
	 *
	 * @return 配色マップ
	 */
	public Map<String, Color> getResult(){
		for(ColorPanel panel : panels){
			map.put(panel.key, panel.color);
		}
		return map;
	}
	
	/**
	 * ダイアログの表示を初期化します。
	 */
	@Override public void initialize(){
		getContentPane().removeAll();
		setTitle(translate("title"));
		
		box = Box.createVerticalBox();
		scroll = new JScrollPane(box);
		add(scroll);
		
		bok = new JButton(translate("button_ok"));
		bcancel = new JButton(translate("button_cancel"));
		
		add(bok);
		add(bcancel);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = OK_OPTION;
				dispose();
			}
		});
		
		bcancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = CANCEL_OPTION;
				dispose();
			}
		});
		
		layoutComponents();
		setMap(map);
	}
	
	private void layoutComponents(){
		scroll.setBounds(5, 5, 230, 120);
		
		int pref = bok.getPreferredSize().height;
		bok.setBounds(20, 130, 100, pref);
		bcancel.setBounds(130, 130, 100, pref);
		
		setContentSize(new Dimension(240, 140 + pref));
	}
	
	/**
	 * ダイアログを表示します。
	 *
	 * @return OKボタンで閉じられた場合true
	 */
	public boolean showDialog(){
		loadSettings();
		setVisible(true);
		return isChanged;
	}
	
	private void loadSettings(){
		box.removeAll();
		for(String key : map.keySet()){
			ColorPanel pane = new ColorPanel(key, map.get(key));
			panels.add(pane);
			box.add(pane);
		}
	}
	
	static final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	
	private class ColorPanel extends JPanel{
		public final String key;
		public Color color;
		private final JTextField field;
		private final JLabel button;
		
		public ColorPanel(String key, Color col){
			super(new BorderLayout());
			this.key = key;
			
			field = new JTextField();
			field.setEditable(false);
			field.setFont(font);
			add(field, BorderLayout.CENTER);
			
			button = new JLabel();
			add(button, BorderLayout.EAST);
			
			button.setOpaque(true);
			button.setPreferredSize(new Dimension(32, 0));
			button.addMouseListener(new ButtonListener());
			
			setColor(col);
		}
		
		private class ButtonListener extends MouseAdapter{
			public void mousePressed(MouseEvent e){
				setColor(JColorChooser.showDialog(
					LeafColorDialog.this,
					translate("chooser_title"), color));
			}
		}
		
		private void setColor(Color col){
			this.color = col != null? col : Color.WHITE;
			button.setBackground(color);
			field.setText(String.format("%#x : %s", color.getRGB(), key));
		}
	}

}