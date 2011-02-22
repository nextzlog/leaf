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

import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

/**
*文字エンコード方式の変換を行うマネージャです。
*Leafが使用する文字セットの管理も行います。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年10月1日
*/

public class LeafCharsetManager{
	
	private static String[] charsets = {"SJIS","JIS","EUC-JP","UTF-8","UTF-16"};
	private static final String[] lsnames = {"CR","CR LF","LF"};
	private static final String[] lss = {"\r","\r\n","\n"};
	
	private static String lineSeparator = System.getProperty("line.separator");

	/**
	*変換元と変換先の文字セットを指定して変換を行います。
	*@param source 変換元の文字列
	*@param bef 変換元の文字セット
	*@param aft 変換先の文字セット
	*@return 変換した文字列
	*@throws UnsupportedEncodingException サポートされない文字エンコード方式の場合
	*/
	public String convert(String source, Charset bef, Charset aft)
		throws UnsupportedEncodingException {
			
		return new String(source.getBytes(bef), aft);
	}
	/**
	*変換元と変換先の文字セット名を指定して変換を行います。
	*@param source 変換元の文字列
	*@param bef 変換元の文字セット名
	*@param aft 変換先の文字セット名
	*@return 変換した文字列
	*@throws IllegalArgumentException 文字セット名が不正かサポートされていない場合
	*@throws UnsupportedEncodingException サポートされていない文字エンコード方式の場合
	*/
	public String convert(String source, String bef, String aft)
		throws IllegalArgumentException, UnsupportedEncodingException {
		
		return convert(source, Charset.forName(bef), Charset.forName(aft));
	}
	/**
	*変換先の文字セットを指定してUTF16文字列を変換します。
	*@param source 変換元の文字列
	*@param aft 変換先の文字セット
	*@return 変換した文字列
	*@throws IllegalArgumentException 文字セット名が不正かサポートされていない場合
	*@throws UnsupportedEncodingException サポートされていない文字エンコード方式の場合
	*/
	public String convert(String source, Charset aft)
		throws IllegalArgumentException , UnsupportedEncodingException{
		
		return convert(source, Charset.forName("UTF-16"), aft);
	}
	/**
	*変換先の文字セット名を指定してUTF16文字列を変換します。
	*@param source 変換元の文字列
	*@param aft 変換先の文字セット名
	*@return 変換した文字列
	*@throws IllegalArgumentException 文字セット名が不正かサポートされていない場合
	*@throws UnsupportedEncodingException サポートされていない文字エンコード方式の場合
	*/
	public String convert(String source, String aft)
		throws IllegalArgumentException, UnsupportedEncodingException {
		
		return convert(source, "UTF-16", aft);
	}
	/**
	*システムで利用できる文字セット名を取得します。
	*@return 使用できる文字セット名の配列
	*/
	public static String[] availableCharsets(){
		ArrayList<String> list = new ArrayList<String>(100);
		Iterator i = Charset.availableCharsets().keySet().iterator();
		while(i.hasNext()){
			list.add((String)i.next());
		}
		list.trimToSize();
		return list.toArray(new String[0]);
	}
	/**
	*Leafで使用する文字セットを設定します。
	*@param names 文字セット名の配列
	*/
	public static void setCharsets(String[] names){
		charsets = names;
	}
	/**
	*Leafで使用する文字セットを設定します。
	*@param names 文字セット名のリスト
	*/
	public static void setCharsets(ArrayList<String> names){
		setCharsets(names.toArray(new String[0]));
	}
	/**
	*Leafで使用する文字セット名を返します。
	*@return 文字セット名の配列
	*/
	public static String[] getCharsetNames(){
		return charsets;
	}
	/**
	*改行コード名の配列を返します。
	*@return CR / CR LF / LF
	*/
	public static String[] getLineSeparatorNames(){
		return lsnames;
	}
	/**
	*Leafで使用する改行コード名を取り出します。
	*@return 改行コード名 CR / CR LF / LF
	*/
	public static String getLineSeparatorName(){
		for(int i=0;i<lss.length;i++){
			if(lineSeparator.equals(lss[i])) return lsnames[i];
		}
		return null;
	}
	/**
	*Leafで使用する改行コードを返します。
	*@return 改行コード
	*/
	public static String getLineSeparator(){
		return lineSeparator;
	}
	/**
	*Leafで使用する改行コードを設定します。
	*nullの場合デフォルトの改行コードを指定します。
	*@param ls 改行コードまたは改行コード名
	*/
	public static void setLineSeparator(String ls){
		for(int i=0;i<lsnames.length;i++){
			if(ls.equals(lsnames[i])){
				lineSeparator = lss[i];
				return;
			}
		}
		lineSeparator = (ls!=null)?ls:System.getProperty("line.separator");
	}
}