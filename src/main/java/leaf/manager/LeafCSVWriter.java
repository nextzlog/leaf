/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.manager;

import java.io.*;
import java.util.ArrayList;

/**
*Stringの2次元配列をCSVファイルに保存するクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年7月31日
*/
public class LeafCSVWriter{
	
	private OutputStream stream;
	private String enc = "SJIS";
	
	/**
	*CSVファイルを指定してCSVライターを生成します。
	*@param file 書き込むCSVファイル
	*@throws FileNotFoundException ファイルに書き込むことができない場合
	*@throws SecurityException セキュリティマネージャによってアクセスが拒否された場合
	*/
	public LeafCSVWriter(File file)
			throws FileNotFoundException,SecurityException{
		try{
			this.stream = new FileOutputStream(file);
		}catch(FileNotFoundException ex){
			throw ex;
		}catch(SecurityException ex){
			throw ex;
		}
	}

	/**
	*CSVファイルを指定してCSVライターを生成します。
	*@param file 書き込むCSVファイル
	*@param enc 文字セット名
	*@throws FileNotFoundException ファイルに書き込むことができない場合
	*@throws SecurityException セキュリティマネージャによってアクセスが拒否された場合
	*/
	public LeafCSVWriter(File file,String enc)
			throws FileNotFoundException,SecurityException{
		try{
			this.stream = new FileOutputStream(file);
			this.enc = enc;
		}catch(FileNotFoundException ex){
			throw ex;
		}catch(SecurityException ex){
			throw ex;
		}
	}
	
	/**
	*ストリームを指定してCSVライターを生成します。
	*@param stream 書き込むストリーム
	*@param enc 文字セット名
	*/
	public LeafCSVWriter(OutputStream stream,String enc){
		this.stream = stream;
		this.enc = enc;
	}
	
	/**
	*2次元配列データをCSVファイルに保存します。<br>
	*配列のインデックスは、１番目が行を表し、２番目が列を表します。
	*@param table 2次元配列データ
	*/
	public void write(String[][] table)
		throws IOException,ArrayIndexOutOfBoundsException{
		
		try{
			String line;
			
			OutputStreamWriter oswriter = new OutputStreamWriter(stream,enc);
			BufferedWriter bwriter = new BufferedWriter(oswriter);
			
			for(int i=0;i<table.length;i++){
				line = "";
				for(int j=0;j<table[i].length-1;j++){
					line += table[i][j] + ",";
				}
				line += table[i][table[i].length-1];
				
				bwriter.write(line);
				bwriter.newLine();
				bwriter.flush();
			}
			
			bwriter.close();
			oswriter.close();
			stream.close();
			
		}catch(IOException ex){
			throw ex;
		}catch(ArrayIndexOutOfBoundsException ex){
			throw ex;
		}
	}
}
