/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium;

import javax.script.ScriptException;
import static leaf.script.euphonium.CodesAndTokens.*;

/**
 *AriCE字句解析器/構文解析で用いられる字句の実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年6月27日 搭載：2010年9月28日
 */
final class Token{
	
	private Enum type;
	private final StringBuilder builder;
	
	/**
	*空の字句を生成します。
	*/
	public Token(){
		type = null;
		builder = new StringBuilder();
	}
	/**
	*指定した値を持つ字句を生成します。
	*@param value 値
	*/
	public Token(Object value){
		this();
		builder.append(value);
	}
	/**
	*この字句に型を設定します。
	*@param type 字句の種別
	*@return この字句への参照
	*/
	public Token setType(Enum type){
		this.type = type;
		return this;
	}
	/**
	*この字句の型を返します。
	*@return 字句の種別
	*/
	public Enum getType(){
		return type;
	}
	/**
	*この字句に文字を追加します。
	*@param ch 追加する文字
	*@return この字句への参照
	*/
	public Token append(char ch){
		builder.append(ch);
		return this;
	}
	/**
	*この字句の値を返します。
	*@return 字句の値
	*/
	public String getValue(){
		return builder.toString();
	}
	/**
	*字句の値を整数型として返します。
	*@param radix 基数
	*@return 整数で表される値
	*@throws NumberFormatException
	*/
	public int toInteger(int radix)
	throws NumberFormatException{
		return Integer.parseInt(builder.toString(), radix);
	}
	/**
	*字句の値を10進整数型として返します。
	*@return 整数で表される値
	*@throws ScriptException
	*/
	public int toInteger() throws ScriptException{
		try{
			return toInteger(10);
		}catch(NumberFormatException ex){
			throw new ScriptException(ex);
		}
	}
	/**
	*字句の値を倍精度浮動小数点数型として返します。
	*@return 小数で表される値
	*/
	public double toDouble(){
		return Double.parseDouble(builder.toString());
	}
	/**
	*字句の値を文字型として返します。
	*@return 文字コードで表される値
	*/
	public char toCharacter(){
		return builder.charAt(0);
	}
	/**
	*この字句に対応する基本演算命令を返します。
	*用途が限定されているため例外は返しません。
	*@return 字句に対応する命令
	*/
	public byte toCode(){
		return ((Operator)type).toCode();
	}
	/**
	*字句を文字列で表します。
	*@return {#getValue()}と同じ
	*/
	public String toString(){
		return builder.toString();
	}
	/**
	*字句が指定されたキーワードと等値かどうか返します。
	*@param key キーワード
	*/
	public boolean equals(Object key){
		if(key instanceof String) //字句解析器用
			return builder.toString().equals(key);
		if(key instanceof Token)  //関数登録時用
			return builder.toString().equals(key.toString());
		return super.equals(key);
	}
	/**
	*指定された種別と字句が一致するか返します。
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
	*指定された種別と字句が一致するか返します。
	*@param type 種別
	*@return 一致する場合true
	*/
	public boolean isType(Enum type){
		return (this.type == type);
	}
	/**
	*字句が代入演算子を表すか返します。
	*@return 代入演算子の場合true
	*/
	public boolean isAssignOperator(){
		if(type instanceof Operator){
			return ((Operator)type).isAssign();
		}
		return false;
	}
	/**
	*字句が関係演算子であるか返します。
	*@return 関係演算子の場合true
	*/
	public boolean isRelationalOperator(){
		if(type instanceof Operator){
			switch((Operator) type){
				case GREATER      : return true;
				case GREATER_EQUAL: return true;
				case LESS         : return true;
				case LESS_EQUAL   : return true;
			}
		}
		return type == Keyword.INSTOF;
	}
	/**
	*字句が等価演算子であるか返します。
	*@return 等価演算子の場合true
	*/
	public boolean isEqualityOperator(){
		if(type instanceof Operator){
			switch((Operator) type){
				case COMPARE   : return true;
				case EQUAL     : return true;
				case IS        : return true;
				case NOT_EQUAL : return true;
			}
		}
		return false;
	}
	/**
	*字句が識別子として有効であるか返します。
	*文脈によって予約語も識別子として有効です。
	*@return 利用可能な場合true
	*/
	public boolean isIdentifier(){
		if(type == TokenType.IDENTIFIER) return true;
		else return (type instanceof Keyword);
	}
}
