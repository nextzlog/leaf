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

import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

/**
*大手検索エンジン用の検索クエリを含んだURLを生成するクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月5日
*/
public class LeafURLQueryManager{

	/**Googleの検索エンジンを指定します。*/
	public static final int GOOGLE = 0;
	/**Yahoo!の検索エンジンを指定します。*/
	public static final int YAHOO  = 1;
	/**Wikipediaの検索エンジンを指定します。*/
	public static final int WIKIPEDIA = 2;
	/**YouTubeの検索エンジンを指定します。*/
	public static final int YOUTUBE = 3;
	/**ニコニコ動画の検索エンジンを指定します。*/
	public static final int NICOVIDEO = 4;
	/**Bingの検索エンジンを指定します。*/
	public static final int BING = 5;
	/**Twitterの検索エンジンを指定します。*/
	public static final int TWITTER = 6;
	
	/**
	*検索対象文字列と検索エンジン、ロケールを指定して検索用URLを作成します。
	*@param query クエリ
	*@param locale ロケール
	*@param engine 検索エンジン
	*/
	public static URL encode(String query, Locale locale, int engine){
		
		try{
			return new URL(template(locale,engine) + URLEncoder.encode(query,"UTF8"));
		}catch(Exception ex){
			return null;
		}
	}
	/**
	*検索対象文字列と検索エンジンを指定して検索用URLを作成します。
	*@param query クエリ
	*@param engine 検索エンジン
	*/
	public static URL encode(String query,int engine){
		
		return encode(query, Locale.getDefault(), engine);
	}
	/**
	*各検索エンジンのURLを空のクエリパラメータ付きで返します。
	*@param locale ロケール
	*@param engine 対象の検索エンジン
	*@return URLの文字列
	*/
	public static String template(Locale locale,int engine){
		String cnt = locale.getCountry();
		switch(engine){
			case GOOGLE:
				return "http://www.google.co." + cnt + "/search?q=";
			case YAHOO :
				return "http://search.yahoo.co." + cnt + "/search?p=";
			case WIKIPEDIA :
				return "http://" + cnt + ".wikipedia.org/wiki/";
			case YOUTUBE :
				return "http://www.youtube.com/results?search_query=";
			case NICOVIDEO :
				return "http://www.nicovideo.jp/search/";
			case BING :
				return "http://www.bing.com/search?q=";
			case TWITTER:
				return "http://search.twitter.com/search.atom?q=";
			default: return null;
		}
	}
}