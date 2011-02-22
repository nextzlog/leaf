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

/**
*画面分割用のコンポーネントです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年3月29日
*/
public class LeafSplitPane extends JPanel{
	
	/**縦方向の分割を表します。*/
	public static final int VERTICAL_SPLIT = JSplitPane.VERTICAL_SPLIT;
	/**横方向の分割を表します。*/
	public static final int HORIZONTAL_SPLIT = JSplitPane.HORIZONTAL_SPLIT;
	/**左（上）の内部コンポーネントがメインコンポーネントであることを表します。*/
	public static final boolean FIRST_IS_MAIN_COMPONENT = true;
	/**右（下）の内部コンポーネントがメインコンポーネントであることを表します。*/
	public static final boolean SECOND_IS_MAIN_COMPONENT=false;
	/**この分割コンポーネントが分割状態にあることを表します。*/
	public static final boolean SPLITTED = true;
	/**この分割コンポーネントが非分割状態にあることを表します。*/
	public static final boolean NOT_SPLITTED = false;
	
	/**秘匿フィールド*/
	private final boolean main;
	private boolean splitted = NOT_SPLITTED;
	private final int orient;
	
	/**GUI*/
	private final JSplitPane split;
	private JComponent comp1,comp2;
	
	/**
	*メインとなる内部コンポーネントの位置と分割方向を指定して分割パネルを生成します。
	*@param main 
	*/
	public LeafSplitPane(boolean main,int orient){
		super();
		this.comp1 = comp1;
		this.comp2 = comp2;
		this.main = main;
		split = new JSplitPane(this.orient = orient);
		split.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
	}
	/**
	*左（上）のコンポーネントと右（下）のコンポーネントを指定して追加します。
	*@param comp1 左（上）の内部コンポーネント
	*@param comp2 右（下）の内部コンポーネント
	*/
	public void add(JComponent comp1,JComponent comp2){
		this.comp1 = comp1;
		this.comp2 = comp2;
		add(getMainComponent(), BorderLayout.CENTER);
	}
	/**
	*メインとなる内部コンポーネントを返します。
	*@return 常に表示されるコンポーネント
	*/
	public JComponent getMainComponent(){
		return (main==FIRST_IS_MAIN_COMPONENT)?comp1:comp2;
	}
	/**
	*メインでない内部コンポーネントを返します。
	*@return 分割時のみ表示されるコンポーネント
	*/
	public JComponent getSubComponent(){
		return (main!=FIRST_IS_MAIN_COMPONENT)?comp1:comp2;
	}
	/**
	*この分割コンポーネントの分割状態を設定します。
	*@param opt 分割するときはtrue
	*/
	public void setSplitted(boolean opt){
		if(opt&&splitted==NOT_SPLITTED){
			remove(comp1);
			split.setTopComponent(comp1);
			split.setBottomComponent(comp2);
			add(split,BorderLayout.CENTER);
			split.setDividerLocation((getHeight()-split.getDividerSize())/2);
		}else if(!opt&&splitted==SPLITTED){
			remove(split);
			split.remove(comp1);
			split.remove(comp2);
			add(getMainComponent(),BorderLayout.CENTER);
		}
		repaint();
		splitted = !splitted;
	}
	/**
	*この分割コンポーネントが分割されているか返します。
	*@return 分割時はtrue
	*/
	public boolean isSplitted(){
		return splitted;
	}
}
