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
package leaf.util.diff;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

/**
*一連の編集操作を順序どおりに管理するリストです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月20日
*/
public class EditList{
	
	private Edit[] edits;
	
	/**
	*空の編集内容を持つリストを生成します。
	*/
	public EditList(){
		edits = new Edit[0];
	}
	/**
	*編集内容を指定してリストを生成します。
	*@param edits 編集内容
	*/
	public EditList(Edit[] edits){
		this.edits = edits;
	}
	/**
	*編集内容を返します。
	*@return 編集の手順
	*/
	public Edit[] getEdits(){
		return edits;
	}
	/**
	*編集内容を設定します。
	*@param edits 編集の手順
	*/
	public void setEdits(Edit[] edits){
		this.edits = edits;
	}
	/**
	*指定されたXMLファイルにリストを保存します。
	*@param file XMLファイル
	*@throws FileNotFoundException ファイルに書き込めない場合
	*/
	public void save(File file) throws FileNotFoundException{
		
		FileOutputStream fstream = null;
		BufferedOutputStream bstream = null;
		XMLEncoder encoder = null;
		
		try{
			fstream = new FileOutputStream(file);
			bstream = new BufferedOutputStream(fstream);
			encoder = new XMLEncoder(bstream);
			encoder.writeObject(this);
			
		}catch(FileNotFoundException ex){
			throw ex;
		}finally{
			if(encoder!=null)encoder.close();
		}
	}
	/**
	*指定されたXMLファイルからリストを読み込んで生成します。
	*@param file XMLファイル
	*@return リスト
	*@throws FileNotFoundException ファイルが見つからない場合
	*@throws ArrayIndexOutOfBoundsException ストリームにオブジェクトがなかった場合
	*@throws ClassCastException 読み込んだオブジェクトがこのクラスのインスタンスでない場合
	*/
	public static EditList load(File file)
		throws FileNotFoundException, ArrayIndexOutOfBoundsException, ClassCastException{
		
		FileInputStream fstream = null;
		BufferedInputStream bstream = null;
		XMLDecoder decoder = null;
		
		try{
			fstream = new FileInputStream(file);
			bstream = new BufferedInputStream(fstream);
			decoder = new XMLDecoder(bstream);
			
			try{
				Object obj = decoder.readObject();
				if(obj instanceof EditList){
					return (EditList)obj;
				}else{
					throw new ClassCastException();
				}
			}catch(ArrayIndexOutOfBoundsException ex){
				throw ex;
			}
		}catch(FileNotFoundException ex){
			throw ex;
		}finally{
			if(decoder!=null)decoder.close();
		}
	}
}