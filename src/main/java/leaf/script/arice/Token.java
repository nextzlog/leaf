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
*AriCEコンパイラで用いられるトークンの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月28日
*/
class Token{
	
	private Enum type = null;
	private String value = "";
	
	/**
	*このトークンに型を設定します。
	*@param type トークンの種別
	*/
	public void setType(Enum type){
		this.type = type;
	}
	/**
	*このトークンの型を返します。
	*@return トークンの種別
	*/
	public Enum getType(){
		return type;
	}
	/**
	*このトークンが指定されたキーワードと同一であるかどうか返します。
	*@param key キーワード
	*/
	public boolean equals(Object key){
		if(key instanceof String){
			return value.equals(key);
		}else{
			return super.equals(key);
		}
	}
	/**
	*このトークンに値を設定します。
	*@param value 設定する値
	*/
	public void setValue(String value){
		this.value = value;
	}
	/**
	*このトークンの値に文字を追加します。
	*@param ch 追加する文字
	
	*/
	public void append(char ch){
		value += Character.toString(ch);
	}
	/**
	*このトークンの値を返します。
	*@return トークンの値
	*/
	public String getValue(){
		return value;
	}
	/**
	*トークンの値を整数型として返します。
	*@return 整数であらわされる値
	*/
	public int toInteger(){
		return Integer.parseInt(value);
	}
	/**
	*トークンの値を倍精度浮動小数点数型として返します。
	*@return 小数であらわされる値
	*/
	public double toDouble(){
		return Integer.parseInt(value);
	}
	/**
	*トークンを文字列で表します。
	*@return {#getValue()}と同じ
	*/
	public String toString(){
		return value;
	}
}
