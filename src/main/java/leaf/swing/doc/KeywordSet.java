/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.doc;

import java.util.ArrayList;
import java.util.List;

/**
 * 強調表示を行うキーワードの集合となるオブジェクトです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.1 作成；2010年9月15日
 */
public class KeywordSet {
	private String name = null;
	private ArrayList<String> exts;
	private ArrayList<String> keywords;
	private String commentBlockStart = null;
	private String commentBlockEnd   = null;
	private String commentLineStart  = null;
	
	/**
	 * 匿名の空のキーワードセットを生成します。
	 */
	public KeywordSet(){
		this.name = null;
		this.exts = new ArrayList<String>(0);
		this.keywords = new ArrayList<String>(0);
	}
	
	/**
	 * 名前を指定して空のキーワードセットを生成します。
	 * 
	 * @param name セットの名前
	 */
	public KeywordSet(String name){
		this.name = name;
		this.exts = new ArrayList<String>(0);
		this.keywords = new ArrayList<String>(0);
	}
	
	/**
	 * キーワードセットの名前を返します。
	 * 
	 * @return キーワードセットの名前
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * キーワードセットの名前を設定します。
	 * 
	 * @param name キーワードセットの名前
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * この強調設定が適用される拡張子のリストを返します。
	 * 
	 * @return このキーワードセットを用いる拡張子のリスト
	 */
	public List<String> getExtensions(){
		return exts;
	}
	
	/**
	 * この強調設定が適用される拡張子のリストを設定します。
	 * 
	 * @param exts このキーワードセットを用いる拡張子のリスト
	 */
	public void setExtensions(List<String> exts){
		if(exts instanceof ArrayList)
			this.exts = (ArrayList<String>)exts;
		else
			this.exts = new ArrayList<String>(exts);
	}
	
	/**
	 * キーワードセットに含まれるキーワードのリストを返します。
	 * 
	 * @return キーワードのリスト
	 */
	public List<String> getKeywords(){
		return keywords;
	}
	
	/**
	 * キーワードのリストを設定します。
	 * 
	 * @param list キーワードのリスト
	 */
	public void setKeywords(List<String> list) {
		this.keywords = new ArrayList<String>(list);
	}
	
	/**
	 * コメントブロックの開始記号を返します。
	 */
	public String getCommentBlockStart(){
		return commentBlockStart;
	}
	
	/**
	 * コメントブロックの開始記号を設定します。
	 */
	public void setCommentBlockStart(String mark){
		commentBlockStart = mark;
	}
	
	/**
	 * コメントブロックの終了記号を返します。
	 */
	public String getCommentBlockEnd(){
		return commentBlockEnd;
	}
	
	/**
	 * コメントブロックの終了記号を設定します。
	 */
	public void setCommentBlockEnd(String mark){
		commentBlockEnd = mark;
	}
	
	/**
	 * 行コメントの開始記号を返します。
	 */
	public String getCommentLineStart(){
		return commentLineStart;
	}
	
	/**
	 * 行コメントの開始記号を設定します。
	 */
	public void setCommentLineStart(String mark){
		commentLineStart = mark;
	}
	
	/**
	 * このセットの文字列による表現を返します。
	 * 
	 * @return 文字列による表現
	 */
	public String toString(){
		return name;
	}
}