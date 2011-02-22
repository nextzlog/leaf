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

import java.io.*;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.util.*;

/**
*強調キーワードセットの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成；2010年9月15日
*@see LeafSyntaxManager
*/
public class KeywordSet{
	
	private String name = null;
	private ArrayList<String> exts = new ArrayList<String>();
	private ArrayList<String> keywords = new ArrayList<String>();
	private String commentBlockStart = null;
	private String commentBlockEnd   = null;
	private String commentLineStart  = null;
	
	/**
	*セットを生成します。
	*/
	public KeywordSet(){
		this.name = null;
	}
	/**
	*名前を指定してセットを生成します。
	*@param name セットの名前
	*/
	public KeywordSet(String name){
		this.name = name;
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
	public ArrayList<String> getExtensions(){
		return exts;
	}
	/**
	*拡張子のリストを設定します。
	*/
	public void setExtensions(ArrayList<String> exts){
		this.exts = exts;
	}
	/**
	*キーワードのリストを返します。
	*/
	public ArrayList<String> getKeywords(){
		return keywords;
	}
	/**
	*キーワードのリストを設定します。
	*/
	public void setKeywords(ArrayList<String> list){
		this.keywords = keywords;
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