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
package leaf.script.arice;

/**
*AriCE言語のラベルの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月29日
*/
class Label{
	
	private final String name;
	private int address = -1;
	
	/**
	*ラベル名を指定してラベルを生成します。
	*@param name 名前
	*/
	public Label(String name){
		this.name = name;
	}
	/**
	*ラベルの名前を返します。
	*@return ラベル名
	*/
	public String getName(){
		return name;
	}
	/**
	*ラベルにアドレスを設定します。
	*@param address アドレス
	*/
	public void setAddress(int address){
		this.address = address;
	}
	/**
	*ラベルのアドレスを返します。
	*@return アドレス
	*/
	public int getAddress(){
		return address;
	}
}
