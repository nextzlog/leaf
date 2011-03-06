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
import java.util.ArrayList;
import java.util.HashMap;

import leaf.components.text.LeafTextField;
import leaf.manager.LeafLangManager;

/**
*配色設定のためのダイアログです。
*配色は属性を表すキーとそれに対する色とをハッシュマップで指定します。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年1月5日
*/
public final class LeafColorOptionDialog extends LeafDialog{
	
	private final JColorChooser chooser;
	private final ArrayList<ColorPanel> panels;
	
	private Box box;
	private JButton bok, bcancel;
	
	private HashMap<String, Color>map;
	
	private boolean isChanged = CANCEL_OPTION;
	
	/**
	*親フレームを指定してダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafColorOptionDialog(Frame owner){
		this(owner, null);
	}
	/**
	*親ダイアログを指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*/
	public LeafColorOptionDialog(Dialog owner){
		this(owner, null);
	}
	/**
	*親フレームとマップを指定してダイアログを生成します。
	*@param owner 親フレーム
	*@param map ハッシュマップ
	*/
	public LeafColorOptionDialog(Frame owner, HashMap<String, Color> map){
		super(owner, null, true);
		
		getContentPane().setPreferredSize(new Dimension(240,155));
		pack();
		setResizable(false);
		setLayout(null);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
				dispose();
			}
		});
		chooser  = new JColorChooser();
		panels   = new ArrayList<ColorPanel>(5);
		init(this.map = map);
	}
	/**
	*親ダイアログとマップを指定してダイアログを生成します。
	*@param owner 親ダイアログ
	*@param map ハッシュマップ
	*/
	public LeafColorOptionDialog(Dialog owner, HashMap<String, Color> map){
		super(owner, null, true);
		
		getContentPane().setPreferredSize(new Dimension(240,155));
		pack();
		setResizable(false);
		setLayout(null);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				isChanged = CANCEL_OPTION;
				dispose();
			}
		});
		chooser  = new JColorChooser();
		panels   = new ArrayList<ColorPanel>(5);
		init(this.map = map);
	}
	/**
	*色の設定を並べたハッシュマップを設定します。
	*@param map ハッシュマップ
	*/
	public void set(HashMap<String, Color> map){
		this.map = new HashMap<String, Color>(map);
		loadSettings();
	}
	/**
	*ユーザーにより設定された配色を返します。
	*@return ハッシュマップ
	*/
	public HashMap<String, Color> getResult(){
		for(ColorPanel panel : panels){
			map.put(panel.getKey(), panel.getColor());
		}
		return map;
	}
	/**
	*マップを指定してダイアログの表示を更新します。
	*@param map ハッシュマップ
	*/
	public void init(HashMap<String, Color> map){
		
		getContentPane().removeAll();
		setTitle(LeafLangManager.get("Color Settings","配色の設定"));
		
		/*配色一覧*/
		box = Box.createVerticalBox();
		
		JScrollPane scroll = new JScrollPane(box);
		scroll.setBounds(5, 5, 230, 110);
		add(scroll);
		
		/*OKボタン*/
		bok = new JButton("OK");
		bok.setBounds(20, 130, 100, 22);
		add(bok);
		
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = OK_OPTION;
				dispose();
			}
		});
		
		/*閉じるボタン*/
		bcancel = new JButton(LeafLangManager.get("Cancel","キャンセル"));
		bcancel.setBounds(130, 130, 100, 22);
		add(bcancel);
		
		bcancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isChanged = CANCEL_OPTION;
				dispose();
			}
		});
		set(map);
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
		String[] keys = map.keySet().toArray(new String[0]);
		for(String key : keys){
			ColorPanel panel = new ColorPanel(key, map.get(key));
			panels.add(panel);
			box.add(panel);
		}
	}
	/**
	*キーと配色を表示するコンポーネントです。
	*/
	private class ColorPanel extends JPanel{
		private final String key;
		private Color color;
		private final JLabel button;
		/**
		*キーと色を指定してパネルを生成します。
		*@param key キー
		*@param color 色
		*/
		public ColorPanel(String key, Color color){
			super(new BorderLayout());
			this.key = key;
			LeafTextField field = new LeafTextField(
				"0x" + Integer.toHexString(color.getRGB()).toUpperCase() + " : " + key
			);
			field.setEditable(false);
			add(field, BorderLayout.CENTER);
			
			button = new JLabel();
			add(button, BorderLayout.EAST);
			
			button.setOpaque(true);
			button.setBackground(this.color = color);
			button.setPreferredSize(new Dimension(32, 0));
			
			button.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					selectColor();
				}
			});
		}
		/**
		*色を選択します。
		*/
		private void selectColor(){
			Color col = chooser.showDialog(LeafColorOptionDialog.this,
				LeafLangManager.get("Background Color","背景色"), color
			);
			if(col!=null)button.setBackground(color = col);
		}
		/**
		*キーを返します。
		*@return キー
		*/
		public String getKey(){
			return key;
		}
		/**
		*設定された色を返します。
		*@return 色
		*/
		public Color getColor(){
			return color;
		}
	}
}