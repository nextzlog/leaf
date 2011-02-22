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
package leaf.components;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import leaf.manager.*;

/**
*AbstractButtonの子クラスの定形的な設定をインスタンス化時にまとめて実行します。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*/
public class LeafButtons{
	
	/**
	*英語、日本語での表示テキストと、ActionListener、ニーモニックを指定して
	*新規にJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param lis ActionListener
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenuItem
	*/
	public static JMenuItem createMenuItem(
		String eng,String jpn,ActionListener lis,int mnemo){
		return createMenuItem(eng,jpn,null,lis,null,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、アクセラレータ、ニーモニック
	*を指定して新規にJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param lis ActionListener
	*@param key キーアクセラレータ
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenuItem
	*/
	public static JMenuItem createMenuItem(
		String eng,String jpn,ActionListener lis,String key,int mnemo){
		return createMenuItem(eng,jpn,null,lis,key,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、表示アイコン、ActionListener、アクセラレータ、
	*ニーモニックを指定して新規にJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param icon 表示するアイコン
	*@param lis ActionListener
	*@param key キーアクセラレータ
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenuItem
	*/
	public static JMenuItem createMenuItem(
		String eng,String jpn,Icon icon,ActionListener lis,String key,int mnemo){
		return new LeafMenuItem(eng,jpn,icon,lis,key,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ニーモニックを指定してJMenuを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenu
	*/
	public static JMenu createMenu(String eng,String jpn,int mnemo){
		return new LeafMenu(eng,jpn,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、ニーモニックを指定して
	*新規にJCheckBoxMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param lis ActionListener
	*@param mnemo キーボードニーモニック
	*@return 作成されたJCheckBoxMenuItem
	*/
	public static JCheckBoxMenuItem createCheckBoxMenuItem(
		String eng,String jpn,ActionListener lis,int mnemo){
		return createCheckBoxMenuItem(eng,jpn,null,lis,null,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、アクセラレータ、ニーモニック
	*を指定して新規にJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param lis ActionListener
	*@param key キーアクセラレータ
	*@param mnemo キーボードニーモニック
	*@return 作成されたJCheckBoxMenuItem
	*/
	public static JCheckBoxMenuItem createCheckBoxMenuItem(
		String eng,String jpn,ActionListener lis,String key,int mnemo){
		return createCheckBoxMenuItem(eng,jpn,null,lis,key,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、アクセラレータ、ニーモニックを
	*指定して新規にJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param icon 表示するアイコン
	*@param lis ActionListener
	*@param key キーアクセラレータ
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenuItem
	*/
	public static JCheckBoxMenuItem createCheckBoxMenuItem(
		String eng,String jpn,Icon icon,ActionListener lis,String key,int mnemo){
		return new LeafCheckBoxMenuItem(eng,jpn,icon,lis,key,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、ニーモニックを指定して
	*新規にJRadioButtonMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param lis ActionListener
	*@param mnemo キーボードニーモニック
	*@return 作成されたJRadioButtonMenuItem
	*/
	public static JRadioButtonMenuItem createRadioButtonMenuItem(
		String eng,String jpn,ActionListener lis,int mnemo){
		return createRadioButtonMenuItem(eng,jpn,null,lis,null,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、アクセラレータ、ニーモニックを
	*指定して新規にJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param lis ActionListener
	*@param key キーアクセラレータ
	*@param mnemo キーボードニーモニック
	*@return 作成されたJRadioButtonMenuItem
	*/
	public static JRadioButtonMenuItem createRadioButtonMenuItem(
		String eng,String jpn,ActionListener lis,String key,int mnemo){
		return createRadioButtonMenuItem(eng,jpn,null,lis,key,mnemo);
	}
	/**
	*英語、日本語での表示テキストと、ActionListener、アクセラレータ、ニーモニックを
	*指定して新規にJMenuItemを作成します。
	*@param eng 英語での表示テキスト
	*@param jpn 日本語での表示テキスト
	*@param icon 表示するアイコン
	*@param lis ActionListener
	*@param key キーアクセラレータ
	*@param mnemo キーボードニーモニック
	*@return 作成されたJMenuItem
	*/
	public static JRadioButtonMenuItem createRadioButtonMenuItem(
		String eng,String jpn,Icon icon,ActionListener lis,String key,int mnemo){
		return new LeafRadioButtonMenuItem(eng,jpn,icon,lis,key,mnemo);
	}
	/**
	*英語、日本語でのツールチップと、表示アイコン、ActionListenerを指定して
	*新規にJButtonを作成します。
	*@param eng 英語での説明文
	*@param jpn 日本語での説明文
	*@param lis ActionListener
	*@param icon 表示するアイコン
	*@return 作成されたJButton
	*/
	public static JButton createButton(
		String eng,String jpn,Icon icon,ActionListener lis){
		return new LeafButton(eng,jpn,icon,lis);
	}
	/**
	*英語、日本語でのツールチップと、表示アイコン、ActionListenerを指定して
	*新規にJToggleButtonを作成します。
	*@param eng 英語での説明文
	*@param jpn 日本語での説明文
	*@param lis ActionListener
	*@param icon 表示するアイコン
	*@return 作成されたJButton
	*/
	public static JToggleButton createToggleButton(
		String eng,String jpn,Icon icon,ActionListener lis){
		return new LeafToggleButton(eng,jpn,icon,lis);
	}
	/**独自のJMenuItem*/
	private static class LeafMenuItem extends JMenuItem{
		public LeafMenuItem(
			String eng,String jpn,Icon icon,ActionListener lis,String key,int mnemo){
			super(icon);
			if(key!=null)setAccelerator(KeyStroke.getKeyStroke(key));
			if(mnemo!=-1){
				setText(LeafLangManager.get(eng,jpn)+
					"("+KeyEvent.getKeyText(mnemo).toUpperCase()+")　　　　");
				setMnemonic(mnemo);
			}else{
				setText(LeafLangManager.get(eng,jpn));
			}
			addActionListener(lis);
			setActionCommand(eng);
		}
	}
	/**独自のJMenu*/
	private static class LeafMenu extends JMenu{
		public LeafMenu(String eng,String jpn,int mnemo){
			super();
			if(mnemo!=-1){
				setText(LeafLangManager.get(eng,jpn)+
					"("+KeyEvent.getKeyText(mnemo).toUpperCase()+")");
				setMnemonic(mnemo);
			}else{
				setText(LeafLangManager.get(eng,jpn));
			}
		}
	}
	/**独自のJCheckBoxMenuItem*/
	private static class LeafCheckBoxMenuItem extends JCheckBoxMenuItem{
		public LeafCheckBoxMenuItem(
			String eng,String jpn,Icon icon,ActionListener lis,String key,int mnemo){
			super(icon);
			if(key!=null)setAccelerator(KeyStroke.getKeyStroke(key));
			if(mnemo!=-1){
				setText(LeafLangManager.get(eng,jpn)+
					"("+KeyEvent.getKeyText(mnemo).toUpperCase()+")　　　　　");
				setMnemonic(mnemo);
			}else{
				setText(LeafLangManager.get(eng,jpn));
			}
			addActionListener(lis);
			setActionCommand(eng);
		}
	}
	/**独自のJRadioButtonMenuItem*/
	private static class LeafRadioButtonMenuItem extends JRadioButtonMenuItem{
		public LeafRadioButtonMenuItem(
			String eng,String jpn,Icon icon,ActionListener lis,String key,int mnemo){
			super(icon);
			if(key!=null)setAccelerator(KeyStroke.getKeyStroke(key));
			if(mnemo!=-1){
				setText(LeafLangManager.get(eng,jpn)+
					"("+KeyEvent.getKeyText(mnemo).toUpperCase()+")　　　　");
				setMnemonic(mnemo);
			}else{
				setText(LeafLangManager.get(eng,jpn));
			}
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