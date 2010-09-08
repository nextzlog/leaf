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
import java.util.*;

/**
*配列を表現する文字列とArrayListとの相互の変換を行うクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月23日
*/
public class LeafArrayManager{
	
	/**
	*文字列を区切り文字で分解して配列を得ます。
	*変換される文字列は指定された区切り文字で配列を表し、配列にこの順番通りに格納されます。
	*@param separator 区切り文字
	*@param str 配列に置き換える文字列
	*@return 文字列型配列を返します。引数の文字列がnullの場合、nullを返します。
	*/
	public static String[] getArrayFromString(String separator,String str){
		ArrayList<String> list = getListFromString(separator,str);
		if(list!=null)return list.toArray(new String[0]);
		else return null;
	}
	/**
	*文字列を区切り文字で分解してArrayListを得ます。
	*変換される文字列は指定された区切り文字で配列を表し、ArrayListにこの順番通りに格納されます。
	*@param separator 区切り文字
	*@param str ArrayListに置き換える文字列
	*@return 文字列型のArrayListを返します。引数の文字列がnullの場合、nullを返します。
	*/
	public static ArrayList<String> getListFromString(String separator,String str){
		if(str==null)return null;
		ArrayList<String> array = new ArrayList<String>();
		String[] sarr = str.split(separator);
		for(int i=0;i<sarr.length;i++){
			if(sarr[i].length()>0)array.add(sarr[i]);
		}
		return array;
	}
	/**
	*配列を表現する文字列を得ます。
	*変換される配列の各要素は、この順番通りに文字列に追加されます。
	*@param separator 区切り文字
	*@param arr 文字列に置き換える配列
	*@return 区切り文字separatorで分けられた文字列
	*/
	public static String getStringFromArray(String separator,Object[] arr){
		if(arr==null||arr.length==0)return null;
		String ret = "";
		for(int i=0;i<arr.length;i++){
			ret += arr[i] + separator;
		}return ret.substring(0,Math.max(0,ret.length()-1));
	}
	/**
	*ArrayListを表現する文字列を得ます。
	*変換されるArrayListの各要素は、この順番通りに文字列に追加されます。
	*@param separator 区切り文字
	*@param list 文字列に置き換えるArrayList
	*@return 区切り文字separatorで分けられた文字列
	*/
	public static String getStringFromList(String separator,ArrayList list){
		if(list==null)return null;
		String ret = "";
		for(Object obj: list){
			ret += obj.toString() + separator;
		}
		return ret.substring(0,Math.max(0,ret.length()-1));
	}
}