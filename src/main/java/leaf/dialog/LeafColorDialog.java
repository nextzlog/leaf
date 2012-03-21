/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;

import leaf.swing.text.LeafTextField;

/**
 *配色設定のためのモーダルダイアログです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.2 作成：2011年1月5日
 */
public final class LeafColorDialog extends LeafDialog{
	private final JColorChooser chooser;
	private final ArrayList<ColorPanel> panels;
	private Box box;
	private JButton bok, bcancel;
	private Map<String, Color>map;
	private boolean isChanged = CANCEL_OPTION;
	
	/**
	*親フレームを指定してダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafColorDialog(Frame owner){
		this(owner, null);
	}
	/**
	*親ダイアログを指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*/
	public LeafColorDialog(Dialog owner){
		this(owner, null);
	}
	/**
	*親フレームとマップを指定してダイアログを生成します。
	*@param owner 親フレーム
	*@param map マップ
	*/
	public LeafColorDialog(Frame owner, Map<String, Color> map){
		super(owner, true);
		setContentSize(new Dimension(240, 155));
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
			}
		});
		
		chooser = new JColorChooser();
		panels  = new ArrayList<ColorPanel>(5);
		this.map = map;
		
		setLayout(null);
		init();
	}
	/**
	*親ダイアログとマップを指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*@param map マップ
	*/
	public LeafColorDialog(Dialog owner, Map<String, Color> map){
		super(owner, true);
		setContentSize(new Dimension(240, 155));
		setResizable(false);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
			}
		});
		
		chooser = new JColorChooser();
		panels  = new ArrayList<ColorPanel>(5);
		this.map = map;
		
		setLayout(null);
		init();
	}
	/**
	*色の設定を並べたマップを設定します。
	*@param map 配色マップ
	*/
	public void setMap(Map<String, Color> map){
		this.map = map;
		loadSettings();
	}
	/**
	*ユーザーにより設定された配色を返します。
	*@return 配色マップ
	*/
	public Map<String, Color> getResult(){
		for(ColorPanel panel : panels){
			map.put(panel.key, panel.color);
		}
		return map;
	}
	/**
	*ダイアログの表示を初期化します。
	*/
	@Override public void init(){
		getContentPane().removeAll();
		setTitle(translate("title"));
		
		/*配色一覧*/
		box = Box.createVerticalBox();
		JScrollPane scroll = new JScrollPane(box);
		scroll.setBounds(5, 5, 230, 110);
		add(scroll);
		
		/*OKボタン*/
		bok = new JButton(translate("button_ok"));
		bok.setBounds(20, 130, 100, 22);
		add(bok);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = OK_OPTION;
				dispose();
			}
		});
		
		/*閉じるボタン*/
		bcancel = new JButton(translate("button_cancel"));
		bcancel.setBounds(130, 130, 100, 22);
		add(bcancel);
		
		bcancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = CANCEL_OPTION;
				dispose();
			}
		});
		setMap(map);
	}
	/**
	*ダイアログを表示します。
	*@return OKボタンで閉じられた場合true
	*/
	public boolean showDialog(){
		loadSettings();
		setVisible(true);
		return isChanged;
	}
	/**
	*ハッシュマップから設定を読み込みます。
	*/
	private void loadSettings(){
		box.removeAll();
		for(String key : map.keySet()){
			ColorPanel pane = new ColorPanel(key, map.get(key));
			panels.add(pane);
			box.add(pane);
		}
	}
	private static final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	/**
	*キーと配色を表示するコンポーネントです。
	*/
	private class ColorPanel extends JPanel{
		public final String key;
		public Color color;
		private final LeafTextField field;
		private final JLabel button;
		/**
		*キーと色を指定してパネルを生成します。
		*@param key キー
		*@param col 色
		*/
		public ColorPanel(String key, Color col){
			super(new BorderLayout());
			this.key = key;
			
			field = new LeafTextField();
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
		/**
		*色選択ボタンのリスナーです。
		*/
		private class ButtonListener extends MouseAdapter{
			public void mousePressed(MouseEvent e){
				setColor(chooser.showDialog(LeafColorDialog.this,
				translate("chooser_title"), color));
			}
		}
		/**
		*パネルに色を設定します。
		*@param col 設定する色
		*/
		private void setColor(Color col){
			if(col != null){
				button.setBackground(color = col);
				field.setText(String.format("%#x : %s", color.getRGB(), key));
			}
		}
	}
}