/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components.table;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import javax.swing.text.View;

import leaf.manager.LeafLangManager;

/**
*JTable用のページ分割操作用コンポーネントです。<br>
*指定した最大数のアイテムだけを同時に表示し、<br>
*それ以外のアイテムはページを切り替えることで表示します。<br>
*このクラスを利用する場合、RowSorterは使用してはなりません。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年8月6日
*/
public class LeafTablePaginatePane extends JPanel{
	
	private final int WING_PAGE_SIZE = 11;
	private final TableRowSorter<TableModel> sorter;
	private final JTable table;
	private final PageLinkButtonUI ui = new PageLinkButtonUI();
	
	private int items = 0, page = 0, lowest = 0, uppest = 0;
	
	/**
	*JTableを指定して分割操作コンポーネントを生成します。
	*<br>インスタンスは{@link #init(int,int)}で必ず初期化してください。
	*@param table 操作対象のJTable
	*/
	public LeafTablePaginatePane(JTable table){
		super();
		this.table  = table;
		this.sorter = new TableRowSorter<TableModel>(table.getModel()){
			public boolean isSortable(int column){
				return false;
			}
		};
		table.setRowSorter(sorter);
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		setPreferredSize(new Dimension(200,30));
	}
	/**
	*リンク一覧を初期化します。
	*@param items 1ページあたりの最大アイテム数
	*@param page 表示するページ
	*/
	public void init(int items, int page){
		
		sorter.setRowFilter(getRowFilter(items,page));
		this.items = items;
		this.page  = page;
		
		ArrayList<JRadioButton> buttons = new ArrayList<JRadioButton>();
		
		lowest = Math.max(0, page - WING_PAGE_SIZE);
		uppest = Math.min(
			(table.getModel().getRowCount()/items), page + WING_PAGE_SIZE);
		
		JRadioButton rprev = getPrevLinkButton(items,page);
		rprev.setEnabled(page>0);
		for(int i=lowest;i<=uppest;i++)
			buttons.add(getPageLinkButton(items,page,i));
		JRadioButton rnext = getNextLinkButton(items,page);
		rnext.setEnabled(page<uppest);
		
		removeAll();
		ButtonGroup group = new ButtonGroup();
		add(rprev);
		add(Box.createHorizontalGlue());
		for(JRadioButton button : buttons){
			add(button);
			group.add(button);
		}
		add(Box.createHorizontalGlue());
		add(rnext);
		revalidate();
		repaint();
		buttons.clear();
	}
	/**
	*表示するページを設定します。
	*@param page 表示するページの番号
	*/
	public void setPage(int page){
		if(page != this.page){
			init(items,page);
		}
	}
	/**
	*現在表示中のページ番号を返します。
	*@return 現在のページ
	*/
	public int getCurrentPage(){
		return page;
	}
	/**
	*最も左側に表示されるボタンのページ番号を返します。
	*@return 最も左のページ番号
	*/
	public int getLowestPage(){
		return lowest;
	}
	/**
	*最も右側に表示されるボタンのページ番号を返します。
	*@return 最も右のページ番号
	*/
	public int getUppestPage(){
		return uppest;
	}
	/**
	*最後のページ番号を返します。
	*@return 最後のページ番号
	*/
	public int getLastPage(){
		return (table.getModel().getRowCount()/items);
	}
	/**
	*1ページあたりの表示アイテム数を返します。
	*/
	public int getItemCountPerPage(){
		return items;
	}
	/**
	*ページリンクのボタンを生成して返す
	*@param items 1ページあたり表示アイテム数
	*@param current 現在表示中のページ
	*@param linkto リンクするページ
	*@return リンクボタン
	*/
	private JRadioButton getPageLinkButton
			(final int items, final int current, final int linkto){
		JRadioButton button = new JRadioButton(String.valueOf(linkto+1));
		button.setForeground(Color.BLUE);
		button.setUI(ui);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				init(items,linkto);
			}
		});
		if(linkto==current){
			button.setSelected(true);
			button.setForeground(Color.BLACK);
		}
		return button;
	}
	/**
	*「前のページ」ボタンを生成して返す
	*@param items 1ページあたりの表示アイテム数
	*@param current 現在表示中のページ
	*@return リンクボタン
	*/
	private JRadioButton getPrevLinkButton(final int items, final int current){
		JRadioButton button = new JRadioButton(LeafLangManager.get("Prev","前"));
		button.setForeground(Color.BLUE);
		button.setUI(ui);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				init(items, current-1);
			}
		});
		return button;
	}
	/**
	*「次のページ」ボタンを生成して返す
	*@param items 1ページあたりの表示アイテム数
	*@param current 現在表示中のページ
	*@return リンクボタン
	*/
	private JRadioButton getNextLinkButton(final int items, final int current){
		JRadioButton button = new JRadioButton(LeafLangManager.get("Next","次"));
		button.setForeground(Color.BLUE);
		button.setUI(ui);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				init(items, current+1);
			}
		});
		return button;
	}
	/**
	*行フィルタを生成して返す
	*@param items 1ページあたりの表示アイテム数
	*@param current 現在表示中のページ
	*/
	private RowFilter<TableModel,Integer> getRowFilter
			(final int items, final int current){
		return new RowFilter<TableModel,Integer>(){
			public boolean include
				(Entry<? extends TableModel,? extends Integer> entry){
				int id = entry.getIdentifier();
				return (current*items <= id && id < current*items + items);
			}
		};
	}
	/**
	*ページリンクボタン用のUI
	*/
	private class PageLinkButtonUI extends BasicRadioButtonUI{
		
		private Dimension size = new Dimension();
		private Rectangle viewRect = new Rectangle();
		private Rectangle iconRect = new Rectangle();
		private Rectangle textRect = new Rectangle();
		
		public Icon getDefaultIcon(){
			return null;
		}
		
		public synchronized void paint(Graphics g,JComponent c){
			
			AbstractButton button = (AbstractButton)c;
			ButtonModel model = button.getModel();
			g.setFont(button.getFont());
			FontMetrics met = button.getFontMetrics(button.getFont());
			
			Insets ins = button.getInsets();
			size = button.getSize(size);
			viewRect.x = ins.left;
			viewRect.y = ins.top;
			viewRect.width = size.width - (ins.right + viewRect.x);
			viewRect.height= size.height- (ins.bottom+ viewRect.y);
			iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
			textRect.x = textRect.y = textRect.width = textRect.height = 0;
			
			String text = SwingUtilities.layoutCompoundLabel(
				button, met, button.getText(), null,
				button.getVerticalAlignment(), button.getHorizontalAlignment(),
				button.getVerticalTextPosition(), button.getHorizontalTextPosition(),
				viewRect, iconRect, textRect, 0
			);
			
			if(button.isOpaque()){
				g.setColor(button.getBackground());
				g.fillRect(0, 0, size.width, size.height);
			}
			if(text==null) return;
			
			g.setColor(button.getForeground());
			if(!model.isSelected() && !model.isPressed() && !model.isArmed()
					&& button.isRolloverEnabled() && model.isRollover()){
				g.drawLine(
					viewRect.x,
					viewRect.y + viewRect.height,
					viewRect.x + viewRect.width,
					viewRect.y + viewRect.height
				);
			}
			View view = (View)c.getClientProperty(BasicHTML.propertyKey);
			if(view!=null){
				view.paint(g, textRect);
			}else{
				paintText(g, button, textRect, text);
			}
		}
	}
}
