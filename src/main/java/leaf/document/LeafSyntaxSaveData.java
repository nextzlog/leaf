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

import java.util.ArrayList;

/**
*キーワード強調設定を保存するためのBeanクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月16日
*@see LeafSyntaxManager
*/
public class LeafSyntaxSaveData{

	private ArrayList<KeywordSet> data;
	
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
	public LeafSyntaxSaveData(ArrayList<KeywordSet> data){
		this.data = data;
	}
	/**
	*保存データを設定します。
	*@param data 保存するキーワードセットのリスト
	*/
	public void setData(ArrayList<KeywordSet> data){
		this.data = data;
	}
	/**
	*保存データを返します。
	*@return 保存するキーワードセットのリスト
	*/
	public ArrayList<KeywordSet> getData(){
		return data;
	}
}