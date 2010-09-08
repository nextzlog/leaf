/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components.taskpane;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

import leaf.icon.*;

/**
*LeafTaskPaneで表示される、展開/折りたたみ自在なコンポーネントです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年7月10日
*@see LeafTaskPane
*/
public abstract class LeafExpandPane extends JPanel{

	private final String title;
	private final JPanel titlepane;
	private final JLabel label;
	private final JLabel button;
	private final JComponent cont;
	private boolean isExpanded = false;
	
	private final ImageIcon expand, fold;
	
	protected ArrayList<ExpandListener> listeners;
	
	/**
	*コンテンツとなるJComponentはこの中で生成されます。
	*@return コンテンツ
	*/
	public abstract JComponent setContent();
	
	/**
	*タイトルを指定してこのコンポーネントを生成します。
	*@param title タイトル
	*/
	public LeafExpandPane(String title){
		super(new BorderLayout());
		
		LeafIcons icons = new LeafIcons();
		expand = icons.getIcon("expand");
		fold   = icons.getIcon("fold");
		
		titlepane = new JPanel(new BorderLayout());
		add(titlepane,BorderLayout.NORTH);
		
		label = new JLabel(this.title = title){
			protected void paintComponent(Graphics g){
				Graphics2D g2 = (Graphics2D)g;
				g2.setPaint(new GradientPaint(0,0,getBackground().brighter(),
					0,getHeight()/3,getBackground().darker(),true));
				g2.fillRect(0,0,getWidth(),getHeight());
				super.paintComponent(g);
			}
		};
		
		button = new JLabel(expand){
			protected void paintComponent(Graphics g){
				Graphics2D g2 = (Graphics2D)g;
				g2.setPaint(new GradientPaint(0,0,getBackground().brighter(),
					0,getHeight(),getBackground().darker()));
				g2.fillRect(0,0,getWidth(),getHeight());
				super.paintComponent(g);
			}
		};
		button.setPreferredSize(new Dimension(25,25));
		
		titlepane.add(label,BorderLayout.CENTER);
		titlepane.add(button,BorderLayout.EAST);
		
		titlepane.setBorder(BorderFactory.createEmptyBorder(2,0,2,0));
		
		titlepane.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				setExpanded(!isExpanded);
				init();
			}
		});
		
		cont = setContent();
		cont.setOpaque(true);
		Border outer = BorderFactory.createMatteBorder(0,2,2,2,Color.WHITE);
		Border inner = BorderFactory.createEmptyBorder(2,0,2,0);
		Border border= BorderFactory.createCompoundBorder(outer,inner);
		
		cont.setBorder(border);
		cont.setBackground(getBackground().brighter());
		
		setMaximumSize(new Dimension(Short.MAX_VALUE,titlepane.getPreferredSize().height));
		
		listeners = new ArrayList<ExpandListener>(1);
	}
	
	/**
	*このコンポーネントが展開中かどうか返します。
	*@return 展開されている場合true
	*/
	public boolean isExpanded(){
		return isExpanded;
	}
	
	/**
	*このコンポーネントの展開状態を設定します。
	*@param isExpanded 展開する場合true
	*/
	public void setExpanded(boolean isExpanded){
		this.isExpanded = isExpanded;
		init();
	}
	
	/**
	*展開状態の変更時にこのコンポーネントを初期化します。
	*/
	protected void init(){
		int height;
		if(isExpanded()){
			button.setIcon(fold);
			add(cont,BorderLayout.CENTER);
			if(cont.getSize().height==0){
				height = titlepane.getSize().height + 200;
			}else{
				height = titlepane.getSize().height + cont.getSize().height;
			}
		}else{
			button.setIcon(expand);
			remove(cont);
			height = titlepane.getSize().height;
		}
		setPreferredSize(new Dimension(0,height));
		revalidate();
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				cont.scrollRectToVisible(cont.getBounds());
			}
		});
		
		for(ExpandListener listener: listeners){
			listener.stateChanged(new ExpandEvent(this));
		}
	}
	
	/**
	*ExpandListenerを登録します。
	*@param listener 展開イベントの受信クラス
	*/
	public void addExpandListener(ExpandListener listener){
		listeners.add(listener);
	}
	
	/**
	*ExpandListenerを削除します。
	*@param listener 削除対象のリスナー
	*/
	public void removeExpandListener(ExpandListener listener){
		listeners.remove(listener);
	}
}