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
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.*;
import javax.swing.undo.*;

/**
*元に戻す・やり直しをサポートするテーブルモデルです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年8月17日
*/
public class LeafUndoableTableModel extends DefaultTableModel{
	
	private UndoableEditSupport undoSupport = new UndoableEditSupport(this);

	/**
	*列数0、行数0を初期値とするテーブルモデルを生成します。
	*/
	public LeafUndoableTableModel(){
		super();
	}
	/**
	*デフォルトの行数、列数を指定してテーブルモデルを生成します。
	*@param rowCount 行数
	*@param columnCount 列数
	*/
	public LeafUndoableTableModel(int rowCount,int columnCount){
		super(rowCount,columnCount);
	}
	/**
	*デフォルトの２次元データ配列と列の名前を指定してテーブルモデルを生成します。
	*@param data 2次元データ配列
	*@param columnNames 列の名前
	*/
	public LeafUndoableTableModel(Object[][] data,Object[] columnNames){
		super(data,columnNames);
	}
	/**
	*列の名前と行数を指定してテーブルモデルを生成します。
	*@param columnNames 列の名前
	*@param rowCount 行数
	*/
	public LeafUndoableTableModel(Object[] columnNames,int rowCount){
		super(columnNames,rowCount);
	}
	/**
	*列の名前と行数を指定してテーブルモデルを生成します。
	*@param columnNames 列の名前
	*@param rowCount 行数
	*/
	public LeafUndoableTableModel(Vector columnNames,int rowCount){
		super(columnNames,rowCount);
	}
	/**
	*デフォルトの２次元データ配列と列の名前を指定してテーブルモデルを生成します。
	*@param data 2次元データ配列
	*@param columnNames 列の名前
	*/
	public LeafUndoableTableModel(Vector data,Vector columnNames){
		super(data,columnNames);
	}
	/**
	*指定されたセルに値を設定します。
	*@param value 設定する値
	*@param row 行
	*@param column 列
	*@throws ArrayIndexOutOfBoundsException 行番号もしくは列番号が範囲外の時
	*/
	public void setValueAt(Object value, int row, int column)
		throws ArrayIndexOutOfBoundsException{
		try{
			if(undoSupport != null){
				UndoableEdit ue = new CellEdit(value,row,column);
				undoSupport.postEdit(ue);
			}
			super.setValueAt(value,row,column);
		}catch(ArrayIndexOutOfBoundsException ex){
			throw ex;
		}
	}
	/**
	*指定された行に行データを挿入します。
	*@param row 挿入する行
	*@param rowData 挿入する行データ
	*@throws ArrayIndexOutOfBOundsException 行番号が範囲外の時
	*/
	public void insertRow(int row,Vector rowData)throws ArrayIndexOutOfBoundsException{
		try{
			if(undoSupport != null){
				UndoableEdit ue = new InsertEdit(rowData,row);
				undoSupport.postEdit(ue);
			}
			super.insertRow(row,rowData);
		}catch(ArrayIndexOutOfBoundsException ex){
			throw ex;
		}
	}
	/**
	*指定された行を削除してデータを上に詰めます。
	*@param row 削除する行
	*@throws ArrayIndexOutOfBOundsException 行番号が範囲外の時
	*/
	public void removeRow(int row) throws ArrayIndexOutOfBoundsException{
		try{
			if(undoSupport != null){
				UndoableEdit ue = new RemoveEdit(getRowData(row),row);
				undoSupport.postEdit(ue);
			}
			super.removeRow(row);
		}catch(ArrayIndexOutOfBoundsException ex){
			throw ex;
		}
	}
	/**
	*行番号がstartからendまでの行をtoで指定された行番号まで移動します。
	*@param start 移動する先頭行
	*@param end 移動する最後の行
	*@param to 先頭の移動先行番号
	*@throws ArrayIndexOutOfBOundsException 範囲外の行番号が含まれる時
	*/
	public void moveRow(int start,int end,int to) throws ArrayIndexOutOfBoundsException{
		try{
			if(undoSupport != null){
				UndoableEdit ue = new MoveEdit(start,end,to);;
				undoSupport.postEdit(ue);
			}
			super.moveRow(start,end,to);
		}catch(ArrayIndexOutOfBoundsException ex){
			throw ex;
		}
	}
	/**
	*指定された行の１次元配列データを返します。
	*@param row 取得する行
	*@return その行のデータ配列
	*@throws ArrayIndexOutOfBoundsException 行番号が範囲外の時
	*/public Vector getRowData(int row) throws ArrayIndexOutOfBoundsException{
		try{
			return ((Vector)getDataVector().elementAt(row));
		}catch(ArrayIndexOutOfBoundsException ex){
			throw ex;
		}
	}
	
	/**
	*このモデルのUndoableEditSupportを取得します。
	*@return UndoableEditSupport
	*/
	public UndoableEditSupport getUndoableEditSupport(){
		return undoSupport;
	}
	/**
	*UndoManagerを設定して「元に戻す」「やり直し」をサポートします。
	*@param manager 登録するUndoManager
	*/
	public void setUndoManager(UndoManager manager){
		undoSupport.addUndoableEditListener(manager);
	}
	/**
	*このモデルにUndoableEditListenerを追加します。
	*@param listener 登録するUndoableEditListener
	*/
	public void addUndoableEditListener(UndoableEditListener listener){
		undoSupport.addUndoableEditListener(listener);
	}
	/**
	*このモデルからUndoableEditListenerを削除します。
	*@param listener 削除するUndoableEditListener
	*/
	public void removeUndoableEditListener(UndoableEditListener listener){
		undoSupport.removeUndoableEditListener(listener);
	}
	/**
	*セル編集時専用のUndoableEdit拡張クラスです。
	*/
	private class CellEdit extends AbstractUndoableEdit{
		protected Object oldValue;
		protected Object newValue;
		protected int row, column;
		
		/**コンストラクタ*/
		protected CellEdit(Object value, int row, int column){
			oldValue = getValueAt(row, column);
			newValue = value;
			this.row = row;
			this.column = column;
		}
		/**元に戻す*/
		public void undo(){
			super.undo();
			LeafUndoableTableModel.super.setValueAt(oldValue,row,column);
		}
		/**やり直し*/
		public void redo(){
			super.redo();
			LeafUndoableTableModel.super.setValueAt(newValue,row,column);
		}
		/**破棄*/
		public void die(){
			super.die();
			oldValue = null;
			newValue = null;
		}
	}
	/**
	*行挿入時専用のUndoableEdit拡張クラスです。
	*/
	private class InsertEdit extends AbstractUndoableEdit{
		protected Vector<?> rowData;
		protected int rowIndex;
		
		/**コンストラクタ*/
		protected InsertEdit(Vector<?> rowData,int rowIndex){
			if(rowData != null){
				this.rowData  = (Vector<?>)rowData.clone();
			}
			this.rowIndex = rowIndex;
		}
		/**元に戻す*/
		public void undo(){
			super.undo();
			LeafUndoableTableModel.super.removeRow(rowIndex);
		}
		/**やり直し*/
		public void redo(){
			super.redo();
			Vector<?> data = (rowData!=null)?(Vector<?>)rowData.clone():null;
			LeafUndoableTableModel.super.insertRow(rowIndex,data);
		}
		/**破棄*/
		public void die(){
			super.die();
			rowData = null;
		}
	}
	/**
	*行削除時専用のUndoableEdit拡張クラスです。
	*/
	private class RemoveEdit extends AbstractUndoableEdit{
		protected Vector<?> rowData;
		protected int rowIndex;
		
		/**コンストラクタ*/
		protected RemoveEdit(Vector<?> rowData,int rowIndex){
			if(rowData != null){
				this.rowData  = (Vector<?>)rowData.clone();
			}
			this.rowIndex = rowIndex;
		}
		/**元に戻す*/
		public void undo(){
			super.undo();
			Vector<?> data = (rowData!=null)?(Vector<?>)rowData.clone():null;
			LeafUndoableTableModel.super.insertRow(rowIndex,data);
		}
		/**やり直し*/
		public void redo(){
			super.redo();
			LeafUndoableTableModel.super.removeRow(rowIndex);
		}
		/**破棄*/
		public void die(){
			super.die();
			rowData = null;
		}
	}
	/**
	*行移動時専用のUndoableEdit拡張クラスです。
	*/
	private class MoveEdit extends AbstractUndoableEdit{
		protected int start, end, to;
		
		/**コンストラクタ*/
		protected MoveEdit(int start, int end, int to){
			this.start = start;
			this.end = end;
			this.to = to;
		}
		/**元に戻す*/
		public void undo(){
			super.undo();
			LeafUndoableTableModel.super.moveRow(to,to+end-start,start);
		}
		/**やり直し*/
		public void redo(){
			super.redo();
			LeafUndoableTableModel.super.moveRow(start,end,to);
		}
		/**破棄*/
		public void die(){
			super.die();
		}
	}
}
