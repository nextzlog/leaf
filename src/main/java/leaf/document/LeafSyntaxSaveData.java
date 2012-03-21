/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.document;

import java.util.List;

/**
*キーワード強調設定を保存するためのクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月16日
*@see LeafSyntaxManager
*/
public class LeafSyntaxSaveData{

	private List<KeywordSet> data;
	
	/**
	*空の保存データを持つ保存クラスを生成します。
	*/
	public LeafSyntaxSaveData(){
		data = null;
	}
	/**
	*保存データを指定して保存クラスを生成します。
	*@param data 保存するキーワードセットのリスト
	*/
	public LeafSyntaxSaveData(List<KeywordSet> data){
		this.data = data;
	}
	/**
	*保存データを設定します。
	*@param data 保存するキーワードセットのリスト
	*/
	public void setData(List<KeywordSet> data){
		this.data = data;
	}
	/**
	*保存データを返します。
	*@return 保存するキーワードセットのリスト
	*/
	public List<KeywordSet> getData(){
		return data;
	}
}