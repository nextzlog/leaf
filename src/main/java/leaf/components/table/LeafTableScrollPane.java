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
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
*JTable用の行ヘッダ付きスクロール領域です。
*<br>JListを用いて行ヘッダを実現しています。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月23日
*/
public class LeafTableScrollPane extends JScrollPane{
	
	/**秘匿フィールド*/
	private JTable table;
	private ListModel model;
	
	/**
	*JTableを指定してスクロール領域を生成します。
	*@param table 内部に表示するJTable
	*/
	public LeafTableScrollPane(JTable table){
		super(table);
		this.table = table;
		/**自身の表示設定*/
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		/**四隅の不透明化*/
		setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER,new JPanel());
		setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER,new JPanel());
		setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,new JPanel());
		setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,new JPanel());
	}
	/**
	*このスクロール領域を初期化します。このメソッドの実行により行ヘッダが描画されます。
	*<br>デフォルトでは行ヘッダが描画されないので必ず実行してください。
	*@param model 行ヘッダ部分のListModel
	*/
	public void init(ListModel model){
		this.model = model;
		this.setRowHeaderView(new LeafRowHeaderView(table,model));
		getRowHeader().addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				JViewport viewport = (JViewport)e.getSource();
				getVerticalScrollBar().setValue(viewport.getViewPosition().y);
			}
		});
	}
	/**
	*行ヘッダのリストモデルを返します。
	*@return 行ヘッダ部分のListModel
	*/
	public ListModel getListModel(){
		return model;
	}
	/**行ヘッダー*/
	private class LeafRowHeaderView extends JList{
		/**秘匿フィールド*/
		private final JTable table;
		private final ListSelectionModel tablemodel;
		private final ListSelectionModel rowmodel;
		/**変数*/
		private int rollOveredRowIndex = -1;
		private int pressedRowIndex = -1;
		/**コンストラクタ*/
		public LeafRowHeaderView(JTable table,ListModel model){
			super(model);
			this.table = table;
			setFixedCellHeight(table.getRowHeight());
			setFixedCellWidth(50);
			setCellRenderer(new LeafRowHeaderRenderer(table.getTableHeader()));
			LeafMouseAdapter listener = new LeafMouseAdapter();
			addMouseListener(listener);
			addMouseMotionListener(listener);
			tablemodel = table.getSelectionModel();
			rowmodel  = this.getSelectionModel();
		}
		/**このコンポーネントのレンダラー*/
		private class LeafRowHeaderRenderer extends JLabel implements ListCellRenderer{
			private final JTableHeader header;
			/**コンストラクタ*/
			public LeafRowHeaderRenderer(JTableHeader header){
				this.header = header;
				this.setOpaque(true);
				this.setBackground(header.getBackground());
				this.setForeground(header.getForeground());
				this.setBorder(BorderFactory.createMatteBorder(0,0,1,1,Color.BLACK));
				this.setHorizontalAlignment(CENTER);
			}
			/**ListCellRenderer実装*/
			public Component getListCellRendererComponent(JList list,Object value,
				int index,boolean isSelected,boolean cellHasFocus){
				if(index==rollOveredRowIndex)
					setBackground(header.getBackground().brighter());
				else{
					setForeground(header.getForeground());
					setBackground(header.getBackground());
				}this.setText((value!=null)?value.toString():"");
				return this;
			}
		}
		/**MouseListener実装*/
		private class LeafMouseAdapter extends MouseAdapter{
			public void mouseExited(MouseEvent e){
				if(pressedRowIndex<0){
					rollOveredRowIndex = -1;
					repaint();
				}
			}
			public void mouseMoved(MouseEvent e){
				int row = LeafRowHeaderView.this.locationToIndex(e.getPoint());
				if(row!=rollOveredRowIndex){
					rollOveredRowIndex = row;
					repaint();
				}
			}
			public void mouseDragged(MouseEvent e){
				if(pressedRowIndex>=0){
					int row = LeafRowHeaderView.this.locationToIndex(e.getPoint());
					int start = Math.min(row,pressedRowIndex);
					int end   = Math.max(row,pressedRowIndex);
					tablemodel.clearSelection();
					tablemodel.addSelectionInterval(start,end);
					rowmodel.clearSelection();
					rowmodel.addSelectionInterval(start,end);
					repaint();
				}
			}
			public void mousePressed(MouseEvent e){
				int row = LeafRowHeaderView.this.locationToIndex(e.getPoint());
				if(row==pressedRowIndex)return;
				rowmodel.clearSelection();
				table.changeSelection(row,0,false,false);
				table.changeSelection(row,table.getColumnModel().getColumnCount()-1,false,true);
				pressedRowIndex = row;
			}
			public void mouseReleased(MouseEvent e){
				rowmodel.clearSelection();
				pressedRowIndex   = -1;
				rollOveredRowIndex= -1;
				repaint();
			}
		}
	}
}
