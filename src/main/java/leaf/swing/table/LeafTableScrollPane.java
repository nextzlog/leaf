/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

/**
 *{@link JTable}に行番号を表示するスクロールコンテナです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成；2010年5月23日
 */
public class LeafTableScrollPane extends JScrollPane {
	private JTable table;
	private DefaultListModel listmodel = new DefaultListModel();
	
	/**
	 *テーブルを指定してスクロールコンテナを構築します。
	 *
	 *@param table 内部に配置するテーブル
	 */
	public LeafTableScrollPane(JTable table){
		super(table);
		this.table = table;
		
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		// set corner opaque
		setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER,  new JPanel());
		setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, new JPanel());
		setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,  new JPanel());
		setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new JPanel());
		
		// row numbers
		setRowHeaderView(new RowNumberView(table, listmodel));
		getRowHeader().addChangeListener(new ChangeListener(){
			@Override public void stateChanged(ChangeEvent e){
				JViewport viewport = (JViewport)e.getSource();
				getVerticalScrollBar().setValue(viewport.getViewPosition().y);
			}
		});
		
		final TableModel tablemodel = table.getModel();
		final int last = tablemodel.getRowCount();
		final int now  = listmodel.getSize();
		
		for(int i = now; i < last; i++){
			listmodel.addElement(String.valueOf(i + 1));
		}
		
		// auto update
		tablemodel.addTableModelListener(new TableModelListener(){
			@Override public void tableChanged(TableModelEvent e){
				final int last = tablemodel.getRowCount();
				final int now  = listmodel.getSize();
				for(int i = now; i<last; i++){
					listmodel.addElement(String.valueOf(i + 1));
				}
			}
		});
	}
	
	/**
	 *行番号を表示するヘッダーです。
	 *
	 */
	private class RowNumberView extends JList {
		private final JTable table;
		private final ListSelectionModel tablemodel;
		private final ListSelectionModel rowmodel;
		
		private int rollOveredRowIndex = -1;
		private int pressedRowIndex = -1;
		
		public RowNumberView(JTable table, ListModel model){
			super(model);
			setFixedCellHeight((this.table = table).getRowHeight());
			setFixedCellWidth(getFontMetrics(getFont()).stringWidth("000000"));
			setCellRenderer(new HeaderRenderer(table.getTableHeader()));
			RollOverListener listener = new RollOverListener();
			addMouseListener(listener);
			addMouseMotionListener(listener);
			tablemodel = table.getSelectionModel();
			rowmodel = getSelectionModel();
		}
		private class HeaderRenderer extends JLabel implements ListCellRenderer{
			private final JTableHeader header;
			
			public HeaderRenderer(JTableHeader header){
				this.header = header;
				setOpaque(true);
				setBackground(header.getBackground());
				setForeground(header.getForeground());
				setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, getForeground()));
				setHorizontalAlignment(CENTER);
			}
			@Override public Component getListCellRendererComponent
			(JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus){
				if(index == rollOveredRowIndex){
					setBackground(header.getBackground().brighter());
				}else{
					setBackground(header.getBackground());
				}
				setText((value != null)? value.toString() : "");
				return this;
			}
		}
		private class RollOverListener extends MouseAdapter {
			@Override public void mouseExited(MouseEvent e){
				if(pressedRowIndex < 0){
					rollOveredRowIndex = -1;
					repaint();
				}
			}
			@Override public void mouseMoved(MouseEvent e){
				int row = RowNumberView.this.locationToIndex(e.getPoint());
				if(row != rollOveredRowIndex){
					rollOveredRowIndex = row;
					repaint();
				}
			}
			@Override public void mouseDragged(MouseEvent e){
				if(pressedRowIndex >= 0){
					int row = RowNumberView.this.locationToIndex(e.getPoint());
					final int start = Math.min(row, pressedRowIndex);
					final int end   = Math.max(row, pressedRowIndex);
					tablemodel.clearSelection();
					tablemodel.addSelectionInterval(start, end);
					rowmodel.clearSelection();
					rowmodel.addSelectionInterval(start, end);
					repaint();
				}
			}
			@Override public void mousePressed(MouseEvent e){
				int row = RowNumberView.this.locationToIndex(e.getPoint());
				if(row != pressedRowIndex){
					rowmodel.clearSelection();
					table.changeSelection(row, 0, false, false);
					table.changeSelection(row,
						table.getColumnModel().getColumnCount() - 1,
						false, true);
					pressedRowIndex = row;
				}
			}
			@Override public void mouseReleased(MouseEvent e){
				rowmodel.clearSelection();
				pressedRowIndex =    -1;
				rollOveredRowIndex = -1;
				repaint();
			}
		}
	}
}