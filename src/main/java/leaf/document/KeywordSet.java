/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.document;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
*強調キーワードセットの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成；2010年9月15日
*@see LeafSyntaxManager
*/
public class KeywordSet{
	
	private String name = null;
	private ArrayList<String> exts;
	private ArrayList<String> keywords;
	private String commentBlockStart = null;
	private String commentBlockEnd   = null;
	private String commentLineStart  = null;
	
	/**
	*セットを生成します。
	*/
	public KeywordSet(){
		this.name = null;
		//必須
		this.exts = new ArrayList<String>(0);
		this.keywords = new ArrayList<String>(0);
	}
	/**
	*名前を指定してセットを生成します。
	*@param name セットの名前
	*/
	public KeywordSet(String name){
		this.name = name;
		//必須
		this.exts = new ArrayList<String>(0);
		this.keywords = new ArrayList<String>(0);
	}
	/**
	*セットの名前を返します。
	*/
	public String getName(){
		return name;
	}
	/**
	*セットの名前を設定します。
	*/
	public void setName(String name){
		this.name = name;
	}
	/**
	*拡張子のリストを返します。
	*/
	public List<String> getExtensions(){
		return exts;
	}
	/**
	*拡張子のリストを設定します。
	*/
	public void setExtensions(List<String> exts){
		if(exts instanceof ArrayList)
			this.exts = (ArrayList<String>)exts;
		else
			this.exts = new ArrayList<String>(exts);
	}
	/**
	*キーワードのリストを返します。
	*/
	public List<String> getKeywords(){
		return keywords;
	}
	/**
	*キーワードのリストを設定します。
	*/
	public void setKeywords(List<String> list){
		if(list instanceof ArrayList)
			this.keywords = (ArrayList<String>)list;
		else
			this.keywords = new ArrayList<String>(list);
	}
	/**
	*コメントブロックの開始記号を返します。
	*/
	public String getCommentBlockStart(){
		return commentBlockStart;
	}
	/**
	*コメントブロックの開始記号を設定します。
	*/
	public void setCommentBlockStart(String mark){
		commentBlockStart = mark;
	}
	/**
	*コメントブロックの終了記号を返します。
	*/
	public String getCommentBlockEnd(){
		return commentBlockEnd;
	}
	/**
	*コメントブロックの終了記号を設定します。
	*/
	public void setCommentBlockEnd(String mark){
		commentBlockEnd = mark;
	}
	/**
	*行コメントの開始記号を返します。
	*/
	public String getCommentLineStart(){
		return commentLineStart;
	}
	/**
	*行コメントの開始記号を設定します。
	*/
	public void setCommentLineStart(String mark){
		commentLineStart = mark;
	}
	/**
	*このセットの文字列化表現を返します。
	*/
	public String toString(){
		return name;
	}
}