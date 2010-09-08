/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import leaf.manager.*;

/**
*AbstractButtonの子クラスの定形的な設定をインスタンス化の段階でまとめて実行します。<br>
*インスタンスは、staticなメソッドの返り値として得られます。<br>
*多言語化に対応するため、{@link LeafLangManager}を参照して使用する言語を自動選択します。<br>
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*/
public class LeafButtons{
	
	/**
	*英語、日本語での表示テキストと、ActionListener、キーボードニーモニックを指定してJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param lis ActionListener
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenuItem
	*/
	public static JMenuItem getMenuItem(String eng,String jpn,ActionListener lis,int mnemo){
		return getMenuItem(eng,jpn,null,lis,null,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、キーアクセラレータ、キーボードニーモニックを指定してJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param lis ActionListener
	*@param key キーアクセラレータ
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenuItem
	*/
	public static JMenuItem getMenuItem(String eng,String jpn,ActionListener lis,String key,int mnemo){
		return getMenuItem(eng,jpn,null,lis,key,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、表示アイコン、ActionListener、キーアクセラレータ、キーボードニーモニックを指定してJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param icon 表示するアイコン
	*@param lis ActionListener
	*@param key キーアクセラレータ
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenuItem
	*/
	public static JMenuItem getMenuItem(String eng,String jpn,Icon icon,ActionListener lis,String key,int mnemo){
		return new LeafMenuItem(eng,jpn,icon,lis,key,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、キーボードニーモニックを指定してJMenuを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenu
	*/
	public static JMenu getMenu(String eng,String jpn,int mnemo){
		return new LeafMenu(eng,jpn,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、キーボードニーモニックを指定してJCheckBoxMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param lis ActionListener
	*@param mnemo キーボードニーモニック
	*@return 作成されたJCheckBoxMenuItem
	*/
	public static JCheckBoxMenuItem getCheckBoxMenuItem(String eng,String jpn,ActionListener lis,int mnemo){
		return getCheckBoxMenuItem(eng,jpn,null,lis,null,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、キーアクセラレータ、キーボードニーモニックを指定してJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param lis ActionListener
	*@param key キーアクセラレータ
	*@param mnemo キーボードニーモニック
	*@return 作成されたJCheckBoxMenuItem
	*/
	public static JCheckBoxMenuItem getCheckBoxMenuItem(String eng,String jpn,ActionListener lis,String key,int mnemo){
		return getCheckBoxMenuItem(eng,jpn,null,lis,key,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、キーアクセラレータ、キーボードニーモニックを指定してJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param icon 表示するアイコン
	*@param lis ActionListener
	*@param key キーアクセラレータ
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenuItem
	*/
	public static JCheckBoxMenuItem getCheckBoxMenuItem(String eng,String jpn,Icon icon,ActionListener lis,String key,int mnemo){
		return new LeafCheckBoxMenuItem(eng,jpn,icon,lis,key,mnemo);
	}
	/**
	*英語、日本語でのツールチップと、表示アイコン、ActionListenerを指定してJButtonを作成します。
	*@param eng 英語での説明文
	*@param jpn 日本語での説明文
	*@param lis ActionListener
	*@param icon 表示するアイコン
	*@return 作成されたJButton
	*/
	public static JButton getButton(String eng,String jpn,Icon icon,ActionListener lis){
		return new LeafButton(eng,jpn,icon,lis);
	}
	/**
	*英語、日本語でのツールチップと、表示アイコン、ActionListenerを指定してJToggleButtonを作成します。
	*@param eng 英語での説明文
	*@param jpn 日本語での説明文
	*@param lis ActionListener
	*@param icon 表示するアイコン
	*@return 作成されたJButton
	*/
	public static JToggleButton getToggleButton(String eng,String jpn,Icon icon,ActionListener lis){
		return new LeafToggleButton(eng,jpn,icon,lis);
	}
	/**独自のJMenuItem*/
	private static class LeafMenuItem extends JMenuItem{
		public LeafMenuItem(String eng,String jpn,Icon icon,ActionListener lis,String key,int mnemo){
			super(LeafLangManager.get(eng,jpn)+"("+KeyEvent.getKeyText(mnemo).toUpperCase()+")　　　　",icon);
			if(key!=null)setAccelerator(KeyStroke.getKeyStroke(key));
			if(mnemo!=-1)setMnemonic(mnemo);
			addActionListener(lis);
			setActionCommand(eng);
		}
	}
	/**独自のJMenu*/
	private static class LeafMenu extends JMenu{
		public LeafMenu(String eng,String jpn,int mnemo){
			super(LeafLangManager.get(eng,jpn)+"("+KeyEvent.getKeyText(mnemo).toUpperCase()+")");
			if(mnemo!=-1)setMnemonic(mnemo);
		}
	}
	/**独自のJCheckBoxMenuItem*/
	private static class LeafCheckBoxMenuItem extends JCheckBoxMenuItem{
		public LeafCheckBoxMenuItem(String eng,String jpn,Icon icon,ActionListener lis,String key,int mnemo){
			super(LeafLangManager.get(eng,jpn)+"("+KeyEvent.getKeyText(mnemo).toUpperCase()+")",icon);
			if(key!=null)setAccelerator(KeyStroke.getKeyStroke(key));
			if(mnemo!=-1)setMnemonic(mnemo);
			addActionListener(lis);
			setActionCommand(eng);
		}
	}
	/**独自のJButton*/
	private static class LeafButton extends JButton{
		public LeafButton(String eng,String jpn,Icon icon,ActionListener lis){
			super(icon);
			setBorderPainted(false);
			setFocusPainted(false);
			setFocusable(false);
			setRequestFocusEnabled(false);
			setToolTipText(LeafLangManager.get(eng,jpn));
			addActionListener(lis);
			setActionCommand(eng);
		}
	}
	/**独自のJToggleButton*/
	private static class LeafToggleButton extends JToggleButton{
		public LeafToggleButton(String eng,String jpn,Icon icon,ActionListener lis){
			super(icon);
			setBorderPainted(false);
			setFocusPainted(false);
			setFocusable(false);
			setRequestFocusEnabled(false);
			setToolTipText(LeafLangManager.get(eng,jpn));
			addActionListener(lis);
			setActionCommand(eng);
		}
	}
}