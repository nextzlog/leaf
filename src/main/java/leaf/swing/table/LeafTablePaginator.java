/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.table;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.View;

/**
 *巨大な{@link JTable}をページ分割して表示する操作を提供します。
 *
 *指定した範囲のアイテムのみを同時に表示し、
 *範囲外の行はページ切り替えによって表示します。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年8月6日
 */
public class LeafTablePaginator extends JComponent {
	private final PageButtonUI ui = new PageButtonUI();
	private final TableRowSorter<TableModel> sorter;
	private final JTable table;
	
	private static final int WING_PAGE_SIZE = 6;
	private int visibleRowCount = 100;
	private int currentPage = 0;
	private int lowest, uppest;
	private Color linkColor = Color.BLUE;
	
	/**
	 *テーブルを指定してページ分割コンポーネントを構築します。
	 *
	 *@param table 操作対象のテーブル
	 */
	public LeafTablePaginator(JTable table){
		super();
		this.table = table;
		sorter = new TableRowSorter<TableModel>(table.getModel()){
			@Override public boolean isSortable(int column){
				return false;
			}
		};
		table.setRowSorter(sorter);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setPreferredSize(new Dimension(200, 30));
		
		update(0);
	}
	
	/**
	 *ページ分割コンポーネントを初期化します。
	 *
	 *@param page 表示するページ番号
	 *
	 */
	private void update(int page){
		this.currentPage = page;
		
		sorter.setRowFilter(new RowFilter<TableModel, Integer>(){
			@Override public boolean include
			(Entry<? extends TableModel, ? extends Integer> entry){
				int id = entry.getIdentifier();
				int min = currentPage * visibleRowCount;
				return min <= id && id < min + visibleRowCount;
			}
		});
		
		final int rowCount = table.getModel().getRowCount();
		this.lowest = Math.max(page - WING_PAGE_SIZE, 0);
		this.uppest = Math.min(page + WING_PAGE_SIZE,
			(rowCount - 1) / visibleRowCount);
		
		final ButtonGroup group = new ButtonGroup();
		removeAll();
		add(Box.createHorizontalGlue());
		
		for(int i = lowest; i <= uppest; i++){
			JRadioButton link = createLinkButton(i);
			group.add(link);
			add(link);
		}
		add(Box.createHorizontalGlue());
		revalidate();
		repaint();
	}
	
	/**
	 *指定したページへのリンクボタンを生成します。
	 *
	 *@param to リンクするページ
	 *@return 生成したリンクボタン
	 */
	private JRadioButton createLinkButton(final int to){
		JRadioButton button = new JRadioButton(String.valueOf(to + 1));
		button.setForeground(to == currentPage? getForeground() : linkColor);
		button.setSelected(to == currentPage);
		button.setUI(ui);
		
		button.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				update(to);
			}
		});
		return button;
	}
	
	/**
	 *表示するページを設定します。
	 *
	 *@param page 表示するページの番号
	 */
	public void setCurrentPage(int page){
		final int old = currentPage;
		update(currentPage = page);
		firePropertyChange("currentPage", old, page);
	}
	
	/**
	 *現在表示されているページの番号を返します。
	 *
	 *@return 現在のページ番号
	 */
	public int getCurrentPage(){
		return currentPage;
	}
	
	/**
	 *1ページあたりに表示する最大アイテム数を設定します。
	 *
	 *@param count 1ページに同時に表示するアイテム数
	 */
	public void setVisibleRowCount(int count){
		final int old = visibleRowCount;
		if(visibleRowCount != count){
			visibleRowCount = count;
			update(currentPage);
		}
		firePropertyChange("visibleRowCount", old, count);
	}
	
	/**
	 *1ページあたりに表示する最大アイテム数を返します。
	 *
	 *@return 1ページに同時に表示するアイテム数
	 */
	public int getVisibleRowCount(){
		return visibleRowCount;
	}
	
	/**
	 *リンクボタンを表示するのに用いる色を設定します。
	 *
	 *@param color リンクボタンの色
	 */
	public void setLinkColor(Color color){
		final Color old = linkColor;
		if(!linkColor.equals(color)){
			linkColor = (color != null? color: Color.WHITE);
			update(currentPage);
		}
		firePropertyChange("linkColor", old, linkColor);
	}
	
	/**
	 *リンクボタンを表示するのに用いる色を返します。
	 *
	 *@return リンクボタンの色
	 */
	public Color getLinkColor(){
		return linkColor;
	}
	
	/**
	 *ページリンクボタン用のUI
	 */
	private class PageButtonUI extends BasicRadioButtonUI{
		
		private Dimension size = new Dimension();
		private Rectangle viewRect = new Rectangle();
		private Rectangle iconRect = new Rectangle();
		private Rectangle textRect = new Rectangle();
		
		@Override public Icon getDefaultIcon(){
			return null;
		}
		
		@Override public synchronized void paint(Graphics g, JComponent c){
			
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
				button.getVerticalAlignment(),
				button.getHorizontalAlignment(),
				button.getVerticalTextPosition(),
				button.getHorizontalTextPosition(),
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
				g.drawLine(viewRect.x,
					viewRect.y + viewRect.height,
					viewRect.x + viewRect.width,
					viewRect.y + viewRect.height);
			}
			View view = (View)c.getClientProperty(BasicHTML.propertyKey);
			
			if(view!=null) view.paint(g, textRect);
			else paintText(g, button, textRect, text);
		}
	}
}
