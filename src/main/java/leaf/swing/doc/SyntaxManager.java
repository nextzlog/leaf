/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.doc;

import java.awt.Color;
import java.io.*;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * キーワード強調設定を管理します。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf1.1 作成：2010年9月15日
 * @see KeywordSet
 */
public class SyntaxManager {
	private List<KeywordSet> keywordsets = null;
	private static Map<String, Color> colors;
	
	static{
		colors = new HashMap<String, Color>(5);
		colors.put("normal",  Color.BLACK);
		colors.put("keyword", Color.BLUE);
		colors.put("quote",   Color.RED);
		colors.put("comment", new Color(0,150,0));
	}
	
	/**
	 * 指定された設定ファイルからキーワード強調設定を取り込みます。
	 * このメソッドの実行によりそれまでに設定されたキーワード設定はクリアされます。
	 * 
	 * @param file XMLファイル
	 * @throws FileNotFoundException ファイルが見つからない場合
	 * @throws ClassCastException ファイルの内容が{@link SyntaxSaveData}でない場合
	 */
	public void load(File file) throws FileNotFoundException, ClassCastException{
		FileInputStream fstream = null;
		BufferedInputStream bstream = null;
		XMLDecoder decoder = null;
		
		try {
			fstream = new FileInputStream(file);
			bstream = new BufferedInputStream(fstream);
			decoder = new XMLDecoder(bstream);
			Object obj = decoder.readObject();
			keywordsets = ((SyntaxSaveData) obj).getData();
		} finally {
			if(decoder != null) decoder.close();
		}
	}
	/**
	 * 指定された設定ファイルにキーワード強調設定を保存します。
	 * 
	 * @param file 保存先のXMLファイル
	 * @throws FileNotFoundException ファイルに書き込めない場合
	 */
	public void save(File file) throws FileNotFoundException{
		FileOutputStream fstream = null;
		BufferedOutputStream bstream = null;
		XMLEncoder encoder = null;
		try {
			fstream = new FileOutputStream(file);
			bstream = new BufferedOutputStream(fstream);
			encoder = new XMLEncoder(bstream);
			encoder.writeObject(new SyntaxSaveData(keywordsets));
		} finally {
			if(encoder!=null) encoder.close();
		}
	}
	/**
	 * キーワードセットのリストを返します。
	 * @return 空の場合null
	 */
	public List<KeywordSet> getKeywordSets() {
		return keywordsets;
	}
	/**
	 * キーワードセットのリストを設定します。
	 * @param sets キーワードセットのリスト
	 */
	public void setKeywordSets(List<KeywordSet> sets) {
		this.keywordsets = sets;
	}
	/**
	 * 登録されているキーワードセットの数を返します。
	 * @return リストが空の場合0
	 */
	public int getKeywordSetCount() {
		if(keywordsets!=null) {
			return keywordsets.size();
		}
		return 0;
	}
	/**
	 * 指定されたキーワードセットを追加します。
	 * @param set 追加するセット
	 */
	public void addKeywordSet(KeywordSet set) {
		keywordsets.add(set);
	}
	/**
	 * 指定されたキーワードセットを削除します。
	 * @param set 削除するセット
	 */
	public void removeKeywordSet(KeywordSet set) {
		keywordsets.remove(set);
	}
	/**
	 * 指定された名前のキーワードセットを検索して返します。
	 * @param name セットの名前
	 * @return 見つからなかった場合null
	 */
	public KeywordSet getKeywordSetByName(String name) {
		if(keywordsets!=null) {
			for(KeywordSet set : keywordsets) {
				if(set.getName().equals(name)) return set;
			}
		}
		return null;
	}
	/**
	 * 指定された拡張子に対応するキーワードセットを検索して返します。
	 * @param ext 拡張子
	 * @return 見つからなかった場合null
	 */
	public KeywordSet getKeywordSetByExtension(String ext) {
		if(keywordsets!=null) {
			for(KeywordSet set : keywordsets) {
				if(set.getExtensions().contains(ext)) return set;
			}
		}
		return null;
	}
	/**
	 * 指定された属性に対応する表示色を返します。
	 * @param key 属性の名前
	 */
	public static Color getColor(String key) {
		return colors.get(key);
	}
	/**
	 * 指定された属性に対応する表示色を設定します。
	 * @param key 属性の名前
	 * @param color 表示色
	 */
	public static void putColor(String key, Color color) {
		colors.put(key, color);
	}
	/**
	 * 配色を並べたマップを返します。
	 * @return マップ
	 */
	public static Map<String, Color> getColorMap() {
		return colors;
	}
	/**
	 * 配色を並べたマップを設定します。
	 * @param map マップ
	 */
	public static void setColorMap(Map<String, Color> map) {
		SyntaxManager.colors = map;
	}
}
