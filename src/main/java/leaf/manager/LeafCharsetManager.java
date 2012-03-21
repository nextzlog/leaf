/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.manager;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *文字エンコード方式間での文字列変換を行うマネージャです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年10月1日
 */
public class LeafCharsetManager{
	private static String newline;
	private static Charset[] chsets;
	private static final HashMap<String, String> newlines;
	static{
		chsets = new Charset[2];
		chsets[0] = Charset.forName("UTF-8");
		chsets[1] = Charset.forName("UTF-16");
		newlines = new HashMap<String, String>();
		newlines.put("\r", "CR");
		newlines.put("\n", "LF");
		newlines.put("\r\n", "CR LF");
		newline = System.getProperty("line.separator");
	}
	/**
	*マネージャを生成します。
	*/
	public LeafCharsetManager(){}
	/**
	*変換元と変換先の文字セットを指定して変換を行います。
	*@param text 変換元の文字列
	*@param from 変換元の文字セット
	*@param to   変換先の文字セット
	*@return 変換した文字列
	*/
	public String convert(String text, Charset from, Charset to){
		return new String(text.getBytes(from), to);
	}
	/**
	*変換先の文字セットを指定してUTF16文字列を変換します。
	*@param text 変換元の文字列
	*@param to   変換先の文字セット
	*@return 変換した文字列
	*/
	public String convert(String text, Charset to){
		return convert(text, Charset.forName("UTF-16"), to);
	}
	/**
	*実行環境でサポートされている文字セットの一覧を返します。
	*@return 使用できる文字セットの配列
	*/
	public static Charset[] availableCharsets(){
		ArrayList<Charset> list = new ArrayList<Charset>();
		Map<String, Charset> chsets = Charset.availableCharsets();
		Iterator i = chsets.keySet().iterator();
		while(i.hasNext()) list.add(chsets.get((String)i.next()));
		return list.toArray(new Charset[0]);
	}
	/**
	*Leafで使用する文字セット一覧を設定します。
	*@param chsets 文字セットの配列
	*/
	public static void setCharsets(Charset[] chsets){
		LeafCharsetManager.chsets = chsets;
	}
	/**
	*Leafで使用する文字セット一覧を返します。
	*@return 文字セットの配列
	*/
	public static Charset[] getCharsets(){
		return chsets;
	}
	/**
	*改行コード名の配列を返します。
	*@return CR / CR LF / LF
	*/
	public static String[] getLineSeparatorNames(){
		return newlines.entrySet().toArray(new String[0]);
	}
	/**
	*Leafで使用する改行コード名を取り出します。
	*@return 改行コード名 CR / CR LF / LF
	*/
	public static String getLineSeparatorName(){
		return newlines.get(newline);
	}
	/**
	*Leafで使用する改行コードを返します。
	*@return 改行コード
	*/
	public static String getLineSeparator(){
		return newline;
	}
	/**
	*Leafで使用する改行コードを設定します。
	*nullの場合デフォルトの改行コードを指定します。
	*@param ls 改行コードまたは改行コード名
	*@throws IllegalArgumentException 不正な改行コードの場合
	*/
	public static void setLineSeparator(String ls)
	throws IllegalArgumentException{
		if(newlines.containsKey(ls)) newline = ls;
		else if(ls!=null) throw new IllegalArgumentException();
		else newline = System.getProperty("line.separator");
	}
}