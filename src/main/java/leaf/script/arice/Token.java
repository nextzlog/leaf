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
final class Token{
	
	private Enum type;
	private final StringBuilder builder;
	
	/**
	*空のトークンを生成します。
	*/
	public Token(){
		type = null;
		builder = new StringBuilder();
	}
	/**
	*指定した名前を持つトークンを生成します。
	*@param name 名前
	*/
	public Token(String name){
		this();
		builder.append(name);
	}
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
	*このトークンの値に文字を追加します。
	*@param ch 追加する文字
	
	*/
	public void append(char ch){
		builder.append(ch);
	}
	/**
	*このトークンの値を返します。
	*@return トークンの値
	*/
	public String getValue(){
		return builder.toString();
	}
	/**
	*トークンの値を整数型として返します。
	*@return 整数で表される値
	*/
	public int toInteger(){
		return Integer.parseInt(builder.toString());
	}
	/**
	*トークンの値を倍精度浮動小数点数型として返します。
	*@return 小数で表される値
	*/
	public double toDouble(){
		return Double.parseDouble(builder.toString());
	}
	/**
	*トークンの値を文字型として返します。
	*@return 文字コードで表される値
	*/
	public char toCharacter(){
		return builder.charAt(0);
	}
	/**
	*このトークンに対応する基本演算命令を返します。
	*用途が限定されているため例外は返しません。
	*@return 命令
	*/
	public byte toCode(){
		return ((AriceParser.Operators)type).toCode();
	}
	/**
	*トークンを文字列で表します。
	*@return {#getValue()}と同じ
	*/
	public String toString(){
		return builder.toString();
	}
	/**
	*指定されたキーワードと同一かどうか返します。
	*@param key キーワード
	*/
	public boolean equals(Object key){
		if(key instanceof String){ //字句解析器用
			return builder.toString().equals(key);
		}if(key instanceof Token){ //関数登録時用
			return builder.toString().equals(key.toString());
		}
		return super.equals(key);
	}
	/**
	*指定された種別とトークンが一致するか返します。
	*@param types 種別
	*@return 一致する場合true
	*/
	public boolean isType(Enum... types){
		for(int i=0;i<types.length;i++){
			if(type == types[i]) return true;
		}
		return false;
	}
	/**
	*このトークンが識別子として利用可能かどうか返します。
	*文脈によっては予約語も識別子となることに注意が必要です。
	*@return 利用可能な場合true
	*/
	public boolean isIdentifier(){
		if(type == AriceParser.Tokens.IDENTIFIER) return true;
		else return (type instanceof AriceParser.Keywords);
	}
}
