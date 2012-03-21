/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.manager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *特定の区切り文字でリストを表現する文字列とリストとの変換を行います。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年5月23日
 */
public class LeafArrayManager{
	private LeafArrayManager(){}
	/**
	*文字列を区切り文字で分解して配列を返します。
	*文字列がnullである場合nullを返します。また、
	*文字列が空である場合空の配列を返します。
	*@param separator 区切り文字
	*@param str 配列に置き換える文字列
	*@return 文字列型配列
	*/
	public static String[] toArray(String separator, String str){
		List<String> list = toList(separator, str);
		if(list != null) return list.toArray(new String[0]);
		else return null;
	}
	/**
	*文字列を区切り文字で分解してリストを返します。
	*文字列がnullである場合nullを返します。また、
	*文字列が空である場合空のリストを返します。
	*@param separator 区切り文字
	*@param str リストに置き換える文字列
	*@return 文字列のリスト
	*/
	public static List<String> toList(String separator, String str){
		if(str == null) return null;
		ArrayList<String> list = new ArrayList<String>();
		for(String split : str.split(separator)){
			if(!split.isEmpty()) list.add(split);
		}
		return list;
	}
	/**
	*配列を表現する文字列を返します。
	*@param separator 区切り文字
	*@param arr 文字列に置き換える配列
	*@return 区切り文字で連結された文字列
	*/
	public static String toString(String separator, Object... arr){
		return toString(separator, Arrays.asList(arr));
	}
	/**
	*リストを表現する文字列を返します。
	*@param separator 区切り文字
	*@param list 文字列に置き換えるリスト
	*@return 区切り文字で連結された文字列
	*/
	public static String toString(String separator, List list){
		if(list == null) return null;
		StringBuilder sb = new StringBuilder();
		for(Object obj : list) sb.append(obj).append(separator);
		return sb.toString();
	}
}