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
package leaf.document;

import java.awt.Color;
import java.awt.Frame;
import java.io.*;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
*キーワード強調設定を管理するためのマネージャークラスです。<br>
*
*@author 東大アマチュア無線クラブ
*@since Leaf1.1 作成：2010年9月15日
*@see KeywordSet
*@see LeafSyntaxOptionDialog
*/

public class LeafSyntaxManager{
	
	private ArrayList<KeywordSet> keywordsets = null;
	private static HashMap<String, Color> colors = new HashMap<String, Color>(5);
	
	static{
		colors.put("normal",  Color.BLACK);
		colors.put("keyword", Color.BLUE);
		colors.put("quote",   Color.RED);
		colors.put("comment", new Color(0,150,0));
	}
	
	/**
	*指定された設定ファイルからキーワード強調設定を取り込みます。
	*このメソッドの実行によりそれまでに設定されたキーワード設定はクリアされます。
	*@param file XMLファイル
	*@throws FileNotFoundException ファイルが見つからない場合
	*@throws ArrayIndexOutOfBoundsException ストリームにオブジェクトがなかった場合
	*@throws ClassCastException 読み込んだオブジェクトが適切なインスタンスでない場合
	*/
	public void load(File file)
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
				if(obj instanceof LeafSyntaxSaveData){
					keywordsets = ((LeafSyntaxSaveData)obj).getData();
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
	/**
	*指定された設定ファイルにキーワード強調設定を保存します。
	*@param file 保存先のXMLファイル
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
			encoder.writeObject(new LeafSyntaxSaveData(keywordsets));
			
		}catch(FileNotFoundException ex){
			throw ex;
		}finally{
			if(encoder!=null)encoder.close();
		}
	}
	/**
	*キーワードセットのリストを返します。
	*@return 空の場合null
	*/
	public ArrayList<KeywordSet> getKeywordSets(){
		if(keywordsets!=null){
			return new ArrayList<KeywordSet>(keywordsets);
		}else{
			return null;
		}
	}
	/**
	*キーワードセットのリストを設定します。
	*@param sets キーワードセットのリスト
	*/
	public void setKeywordSets(ArrayList<KeywordSet> sets){
		keywordsets = sets;
	}
	/**
	*登録されているキーワードセットの数を返します。
	*@return リストが空の場合0
	*/
	public int getKeywordSetCount(){
		if(keywordsets!=null){
			return keywordsets.size();
		}
		return 0;
	}
	/**
	*指定されたキーワードセットを追加します。
	*@param set 追加するセット
	*/
	public void addKeywordSet(KeywordSet set){
		keywordsets.add(set);
	}
	/**
	*指定されたキーワードセットを削除します。
	*@param set 削除するセット
	*/
	public void removeKeywordSet(KeywordSet set){
		keywordsets.remove(set);
	}
	/**
	*指定された名前のキーワードセットを検索して返します。
	*@param name セットの名前
	*@return 見つからなかった場合null
	*/
	public KeywordSet getKeywordSetByName(String name){
		if(keywordsets!=null){
			for(KeywordSet set : keywordsets){
				if(set.getName().equals(name))
					return set;
			}
		}
		return null;
	}
	/**
	*指定された拡張子に対応するキーワードセットを検索して返します。
	*@param ext 拡張子
	*@return 見つからなかった場合null
	*/
	public KeywordSet getKeywordSetByExtension(String ext){
		if(keywordsets!=null){
			for(KeywordSet set : keywordsets){
				if(set.getExtensions().contains(ext))
					return set;
			}
		}
		return null;
	}
	/**
	*親フレームを指定してキーワード設定画面を開きます。
	*@param parent 親フレーム
	*@return 設定が変更された場合true
	*/
	public boolean showOptionDialog(Frame parent){
		LeafSyntaxOptionDialog dialog = new LeafSyntaxOptionDialog(parent,this);
		return (dialog.showDialog()==dialog.OK_OPTION);
	}
	/**
	*指定された属性に対応する表示色を返します。
	*@param key 属性の名前
	*/
	public static Color getColor(String key){
		return colors.get(key);
	}
	/**
	*指定された属性に対応する表示色を設定します。
	*@param key 属性の名前
	*@param color 表示色
	*/
	public static void putColor(String key, Color color){
		colors.put(key, color);
	}
	/**
	*配色を並べたハッシュマップを返します。
	*@return ハッシュマップ
	*/
	protected static HashMap<String, Color> getColorMap(){
		return colors;
	}
	/**
	*配色を並べたハッシュマップを設定します。
	*@param map ハッシュマップ
	*/
	protected static void setColorMap(HashMap<String, Color> map){
		colors = map;
	}
}
