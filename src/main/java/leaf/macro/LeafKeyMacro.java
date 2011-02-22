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
package leaf.macro;

import java.util.ArrayList;

/**
*キーマクロを実装するクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月19日
*/
public class LeafKeyMacro{
	
	private ArrayList<Integer> stroke;
	
	/**
	*空のストロークを持つマクロを生成します。
	*/
	public LeafKeyMacro(){
		stroke = new ArrayList<Integer>(0);
	}
	/**
	*ストロークを指定してマクロを生成します。
	*@param stroke 指定するマクロ
	*/
	public LeafKeyMacro(ArrayList<Integer> stroke){
		stroke = stroke;
	}
	/**
	*ストロークを設定します。
	*@param stroke 設定するストローク
	*/
	public void setStroke(ArrayList<Integer> stroke){
		this.stroke = stroke;
	}
	/**
	*キーストロークを返します。
	*@return 記録されているストローク
	*/
	public ArrayList<Integer> getStroke(){
		return stroke;
	}
}