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
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import javax.swing.undo.*;

import leaf.components.text.*;

/**
*２次元配列を表示するテーブルです。<br>
*偶数行と奇数行とで背景色が異なります。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年8月1日
*/
public class LeafTable extends JTable{
	
	/**秘匿フィールド*/
	private Color color_even = new Color(220,220,255);
	private JTableHeader header;
	
	/**
	*テーブルを生成します。
	*/
	public LeafTable(){
		super();
		init();
	}
	/**
	*行数と列数を指定してテーブルを生成します。
	*@param numRows 行数
	*@param numColumns 列数
	*/
	public LeafTable(int numRows, int numColumns){
		super(numRows,numColumns);
		init();
	}
	/**
	*デフォルトの2次元データ配列と列の名前を指定してテーブルを生成します。
	*@param rowData デフォルトの2次元データ配列
	*@param columnNames 列の名前の配列
	*/
	public LeafTable(Object[][] rowData, Object[] columnNames){
		super(rowData,columnNames);
		init();
	}
	/**
	*データモデルを指定してテーブルを生成します。
	*@param dm データモデル
	*/
	public LeafTable(TableModel dm){
		super(dm);
		init();
	}
	/**
	*データモデルと列モデルを指定してテーブルを生成します。
	*@param dm データモデル
	*@param cm 列モデル
	*/
	public LeafTable(TableModel dm, TableColumnModel cm){
		super(dm,cm);
		init();
	}
	/**
	*データモデルと列モデル、選択モデルを指定してテーブルを生成します。
	*@param dm データモデル
	*@param cm 列モデル
	*@param sm 選択モデル
	*/
	public LeafTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm){
		super(dm,cm,sm);
		init();
	}
	/**
	*行データと列の名前をVectorで指定してテーブルを生成します。
	*@param rowData 行データ
	*@param columnNames 列の名前の配列
	*/
	public LeafTable(Vector rowData, Vector columnNames){
		super(rowData,columnNames);
		init();
	}
	
	/**テーブルの初期設定*/
	private void init(){
		
		setFillsViewportHeight(true);
		setSelectionForeground(Color.WHITE);
		setSelectionBackground(Color.BLACK);
		
		setShowHorizontalLines(false);
		
		Border border = BorderFactory.createLineBorder(Color.ORANGE,2);
		
		LeafTextField tf = new LeafTextField();
		tf.setBorder(border);
		setDefaultEditor(String.class,new DefaultCellEditor(tf));
		
		LeafNumberFormattedTextField<Integer> itf
		= new LeafNumberFormattedTextField<Integer>();
		itf.setBorder(border);
		setDefaultEditor(Integer.class,new DefaultCellEditor(itf));
		
		LeafNumberFormattedTextField<Long> ltf
		= new LeafNumberFormattedTextField<Long>();
		ltf.setBorder(border);
		setDefaultEditor(Long.class,new DefaultCellEditor(ltf));
		
		LeafNumberFormattedTextField<Double> dtf
		= new LeafNumberFormattedTextField<Double>();
		dtf.setBorder(border);
		setDefaultEditor(Double.class,new DefaultCellEditor(dtf));
		
		LeafNumberFormattedTextField<Float> ftf
		= new LeafNumberFormattedTextField<Float>();
		ftf.setBorder(border);
		setDefaultEditor(Double.class,new DefaultCellEditor(ftf));
	}
	/**
	*レンダラーを生成準備します。
	*@param tcr セルのレンダラー
	*@param row セルを指定する行番号
	*@param column セルを指定する列番号
	*/
	public Component prepareRenderer(TableCellRenderer tcr, int row, int column){
		Component comp = super.prepareRenderer(tcr,row,column);
		
		if(isRowSelected(row)){
			comp.setForeground(getSelectionForeground());
			comp.setBackground(getSelectionBackground());
		}else{
			comp.setForeground(getForeground());
			comp.setBackground((row%2==0)?color_even:getBackground());
		}
		return comp;
	}
	
	/**
	*奇数行の背景色を設定します。
	*@param color 奇数行の背景色
	*/
	public void setOddRowBackground(Color color){
		setBackground(color);
	}
	/**
	*偶数行の背景色を設定します。
	*@param color 偶数行の背景色
	*/
	public void setEvenRowBackground(Color color){
		color_even = color;
	}
	/**
	*奇数行の背景色を返します。
	*@return 奇数行の背景色
	*/
	public Color getOddBackground(){
		return getBackground();
	}
	/**
	*偶数行の背景色を返します。
	*@return 偶数行の背景色
	*/
	public Color getEvenBackground(){
		return color_even;
	}
	
	/**
	*ページ分割操作用のコンポーネントを生成して返します。
	*@param items 1ページあたりの最大アイテム数
	*@return ページ分割操作パネル
	*/
	public LeafTablePaginatePane getTablePaginatePane(int items){
		LeafTablePaginatePane pane = new LeafTablePaginatePane(this);
		pane.init(items, 0);
		return pane;
	}
}