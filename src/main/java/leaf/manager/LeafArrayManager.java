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

import java.io.*;
import java.util.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

/**
*配列を表現する文字列と{@link ArrayList}との相互の変換を行うクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月23日
*/
public class LeafArrayManager{
	
	/**
	*文字列を区切り文字で分解して配列を得ます。
	*変換される文字列は指定された区切り文字で配列を表し、順番通りに格納されます。
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
	*変換される文字列は指定された区切り文字で配列を表し、順番通りに格納されます。
	*@param separator 区切り文字
	*@param str ArrayListに置き換える文字列
	*@return 文字列型のArrayListを返します。引数の文字列がnullの場合、nullを返します。
	*/
	public static ArrayList<String> getListFromString(String separator,String str){
		if(str==null)return null;
		ArrayList<String> list = new ArrayList<String>();
		for(String split : str.split(separator)){
			if(split.length()>0)list.add(split);
		}
		return list;
	}
	/**
	*配列を表現する文字列を得ます。
	*変換される配列の各要素は、この順番通りに文字列に追加されます。
	*@param separator 区切り文字
	*@param arr 文字列に置き換える配列
	*@return 区切り文字separatorで分けられた文字列
	*/
	public static String getStringFromArray(String separator, Object[] arr){
		if(arr==null||arr.length==0)return null;
		StringBuilder sb = new StringBuilder();
		for(Object obj : arr){
			sb.append(obj + separator);
		}
		return sb.substring(0, Math.max(0, sb.length()-1));
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
		StringBuilder sb = new StringBuilder();
		for(Object obj : list){
			sb.append(obj + separator);
		}
		return sb.substring(0, Math.max(0, sb.length()-1));
	}
	/**
	*指定された配列の部分配列を返します。
	*部分配列の型は自動で元の配列と同じ型が適用されます。
	*@param array 配列
	*@param start コピー開始位置
	*@param end   コピー終了位置
	*@return 部分配列
	*@since 2011年2月24日
	*/
	public static Object[] subArray(Object array, int start, int end){
		Object[] src  = (Object[])array;
		Class<?> comp = array.getClass().getComponentType();
		Object[] ret = (Object[])Array.newInstance(comp, end-start+1);
		for(int i=start; i<=end; i++){
			ret[i-start] = src[i];
		}
		return ret;
	}
}