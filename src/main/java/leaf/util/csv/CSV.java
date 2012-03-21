/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;

/**
 *CSV形式を扱うためのユーティリティです。
 *
 *CSV形式はアプリケーションと既存のソフトウェアとの連携を容易に
 *しますが、以下の理由によりCSVの積極的な利用は推奨されません。
 *<pre>
 *  ・CSVには統一された標準仕様が存在しない
 *  ・CSVの仕様はソフトウェアによって異なる
 *</pre>
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年9月16日
 */
public class CSV implements Serializable{
	final ArrayList<Row> table;
	
	/**
	 *空のCSVテーブルを生成します。
	 */
	public CSV(){
		table = new ArrayList<Row>();
	}
	/**
	 *指定されたセルをテーブルに配置します。
	 *
	 *@param row セルの行
	 *@param column セルの列
	 *@param cell 配置するセル
	 *@return 以前配置されていたセル
	 *@throws IndexOutOfBoundsException 行、列が負の場合
	 */
	public Cell put(int row, int column, Cell cell){
		checkRow(row);
		if(row < table.size()){
			return table.get(row).put(column, cell);
		}else{
			Row line = new Row();
			line.put(column, cell);
			addRow(row, line);
			return null;
		}
	}
	/**
	 *指定された位置のセルを返します。
	 *
	 *@param row セルの行
	 *@param column セルの列
	 *@return 指定した位置のセル 範囲外の場合null
	 *@throws IndexOutOfBoundsException 行、列が負の場合
	 */
	public Cell get(int row, int column){
		checkRow(row);
		return getRow(row).get(column);
	}
	/**
	 *指定した位置に行を挿入もしくは追加します。
	 *
	 *@param row 行の挿入、追加位置
	 *@param line 挿入する行
	 *@throws IndexOutOfBoundsException 行が負の場合
	 */
	public void addRow(int row, Row line){
		checkRow(row);
		if(row <= table.size()) table.add(row, line);
		else for(int i=table.size(); i<=row; i++){
			if(i == row) table.add(line);
			else table.add(new Row());
		}
	}
	/**
	 *指定した行番号の行を返します。
	 *
	 *@param row 行番号
	 *@return 指定した位置の行 範囲外の場合null
	 *@throws IndexOutOfBoundsException 行が負の場合
	 */
	public Row getRow(int row){
		checkRow(row);
		return (row<table.size())? table.get(row) : null;
	}
	/**
	 *指定した行をテーブルから削除します。
	 *
	 *@param row 削除する行
	 *@return 削除した行
	 *@throws IndexOutOfBoundsException 行が範囲外の場合
	 */
	public Row removeRow(int row){
		checkRow(row);
		if(row < table.size()) return table.remove(row);
		else throw new IndexOutOfBoundsException(
		String.format("row(%d) out of table size", row));
	}
	/**
	 *指定された位置が正当な位置であるか確認します。
	 *@param row セルの行
	 *@throws IndexOutOfBoundsException 負の場合
	 */
	private void checkRow(int row){
		if(row >= 0) return;
		throw new IndexOutOfBoundsException(
		String.format("row(%d) must be non negative", row));
	}
	/**
	 *テーブルの行数を返します。
	 *
	 *@return テーブルの行数
	 */
	public int getRowCount(){
		return table.size();
	}
	/**
	 *テーブルの内容をエスケープ表現を含まないCSV形式で出力します。
	 *
	 *@return CSV形式での文字列表現 テーブルが空なら空の文字列
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		final int size = table.size();
		for(int i=0; i<size-1; i++){
			sb.append(table.get(i)).append('\n');
		}
		if(size>0) sb.append(table.get(size-1));
		return sb.toString();
	}
	/**
	 *テーブルの内容をエスケープされたCSV形式で出力します。
	 *
	 *@return CSV形式での正当な文字列表現 テーブルが空なら空の文字列
	 */
	public String canonical(){
		StringBuilder sb = new StringBuilder();
		final int size = table.size();
		for(int i=0; i<size-1; i++){
			sb.append(table.get(i).canonical()).append('\n');
		}
		if(size>0) sb.append(table.get(size-1).canonical());
		return sb.toString();
	}
	/** 
	 *テーブルの内容を長さの揃った二次元配列で出力します。
	 *
	 *@return CSVの内容をコピーした二次元配列
	 */
	public String[][] toArray(){
		final int rowCount = table.size();
		int columnCount = 0;
		for(int i=0; i<rowCount; i++){
			int col = table.get(i).getColumnCount();
			if(col > columnCount) columnCount = col;
		}
		String[][] data = new String[rowCount][columnCount];
		for(int m=0; m<rowCount; m++){
			final Row row = table.get(m);
			final int col = row.getColumnCount();
			for(int n=0; n<col; n++){
				data[m][n] = row.get(n).toString();
			}
		}
		return data;
	}
	/**
	 *テーブルのデータを全て消去します。
	 */
	public void clear(){
		table.clear();
		table.trimToSize();
	}
	/**
	 *読み込み元を指定してテーブルを生成します。
	 *
	 *@param reader CSV形式を読み込むリーダー
	 *@return 読み込まれたCSVテーブル
	 *@throws IOException 読み込みに失敗した場合
	 */
	public static CSV read(Reader reader) throws IOException{
		final BufferedReader br;
		if(reader instanceof BufferedReader){
			br = (BufferedReader)reader;
		}else br = new BufferedReader(reader);
		try{
			final Parser parser = new Parser();
			final CSV instance = new CSV();
			String line;
			while((line = br.readLine()) != null){
				instance.table.add(parser.parse(line));
			}
			return instance;
		}finally{
			br.close();
		}
	}
	/**
	 *テーブルのデータをCSV形式で出力します。
	 *
	 *@param writer CSV形式を書き込むライター
	 *@throws IOException 書き込みに失敗した場合
	 */
	public void write(Writer writer) throws IOException{
		final BufferedWriter bw;
		if(writer instanceof BufferedWriter){
			bw = (BufferedWriter)writer;
		}else bw = new BufferedWriter(writer);
		try{
			final int rowCount = table.size();
			for(int i=0; i<rowCount; i++){
				bw.write(table.get(i).canonical());
				bw.newLine();
			}
		}finally{
			bw.close();
		}
	}
}