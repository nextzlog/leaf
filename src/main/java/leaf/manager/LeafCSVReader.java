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
package leaf.manager;

import java.io.*;
import java.util.ArrayList;

/**
*CSVファイルを読み込んで2次元配列として取り出すクラスです。
*このリーダーは各行での配列要素数の不一致に対し安全です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年6月30日
*@see LeafCSVWriter
*/
public class LeafCSVReader{
	
	private InputStream stream = null;
	private String enc = "JISAutoDetect";
	private ArrayList<String[]> table;
	
	/**
	*CSVファイルを指定してCSVリーダーを生成します。
	*@param file 読み込むCSVファイル
	*@throws FileNotFoundException ファイルが存在しないか、開くことができない場合
	*@throws SecurityException セキュリティマネージャによってアクセスが拒否された場合
	*/
	public LeafCSVReader(File file)
			throws FileNotFoundException,SecurityException{
		try{
			this.stream = new FileInputStream(file);
			table = new ArrayList<String[]>(1000);
		}catch(FileNotFoundException ex){
			throw ex;
		}catch(SecurityException ex){
			throw ex;
		}
	}
	
	/**
	*CSVファイルを指定してCSVリーダーを生成します。
	*@param file 読み込むCSVファイル
	*@param enc 文字セット名
	*@throws FileNotFoundException ファイルが存在しないか、開くことができない場合
	*@throws SecurityException セキュリティマネージャによってアクセスが拒否された場合
	*/
	public LeafCSVReader(File file,String enc)
			throws FileNotFoundException,SecurityException{
		try{
			this.stream = new FileInputStream(file);
			this.enc = enc;
			table = new ArrayList<String[]>(1000);
		}catch(FileNotFoundException ex){
			throw ex;
		}catch(SecurityException ex){
			throw ex;
		}
	}
	
	/**
	*ストリームと文字セットを指定してCSVリーダーを生成します。
	*@param stream 読み込むストリーム
	*@param enc 文字セット名
	*/
	public LeafCSVReader(InputStream stream,String enc){
		this.stream = stream;
		this.enc = enc;
		table = new ArrayList<String[]>(1000);
	}
	
	/**
	*CSVファイルを読み込んで、１行ごとの配列データを並べたArrayListを返します。
	*@return １行ごとの配列を並べたArrayList
	*/
	public ArrayList<String[]> readToList() throws IOException{
		
		try{
			String line;
			
			InputStreamReader isreader = new InputStreamReader(stream,enc);
			BufferedReader breader = new BufferedReader(isreader);
			
			int columns = 0;
			
			while((line = breader.readLine()) != null){
				String[] elems = line.split(",",0);
				columns = Math.max(elems.length,columns);
				table.add(elems);
			}
			
			breader.close();
			isreader.close();
			stream.close();
			
			for(String[] elems : table){
				if(elems.length < columns){
					String[] elements = new String[columns];
					for(int i=0;i<elems.length;i++){
						elements[i] = elems[i];
					}
					table.set(table.indexOf(elems),elements);
				}
			}
			table.trimToSize();
			
			return table;
		}catch(IOException ex){
			throw ex;
		}
	}
	
	/**
	*CSVファイルを読み込んでデータを2次元配列として取り出します。<br>
	*配列のインデックスは、１番目が行を表し、２番目が列を表します。
	*@return 取り出した2次元配列
	*/
	public String[][] read() throws IOException{
		
		try{
			return readToList().toArray(new String[0][0]);
		}catch(IOException ex){
			throw ex;
		}
	}
}
