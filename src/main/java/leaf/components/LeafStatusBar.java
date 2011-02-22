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
import javax.swing.*;
import javax.swing.border.*;

/**
*ステータスバーの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年4月10日
*/
public class LeafStatusBar extends JPanel{
	/*フィールド*/
	private final JPanel pc,pe;
	private final JLabel le1,le2;
	private final JSeparator sep;
	/**左側のテキストを表します。*/
	public static final int STATUS_LEFT = 0;
	/**右側のテキストを表します。*/
	public static final int STATUS_RIGHT= 1;
	/**
	*ステータスバーを生成します。
	*/
	public LeafStatusBar(){
		super(new BorderLayout());
		setPreferredSize(new Dimension(640,24));
		/*CENTER*/
		pc = new JPanel();
		/*EAST*/
		pe = new JPanel();
		pe.setLayout(new BoxLayout(pe,BoxLayout.X_AXIS));
		le1 = new JLabel();
		le2 = new JLabel();
		sep = new JSeparator(JSeparator.VERTICAL);
		le1.setMaximumSize(new Dimension(120,20));
		le1.setPreferredSize(new Dimension(120,20));
		le2.setMaximumSize(new Dimension(60,20));
		le2.setPreferredSize(new Dimension(60,20));
		pe.add(le1);
		pe.add(sep);
		pe.add(le2);
		/*LAYOUT*/
		add(pc,BorderLayout.CENTER);
		add(pe,BorderLayout.EAST);
	}
	/**
	*ステータスバーの表示テキストを変更します。
	*@param str 新しい文字列
	*@param index 変更するテキストのインデックス
	*/
	public void setText(String str,int index){
		if(index==STATUS_LEFT){
			le1.setText(str);
		}else{
			le2.setText(str);
		}
	}
}