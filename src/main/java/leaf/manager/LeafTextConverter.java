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

/**
*文字列の変換を行います。
*例えば、半角英数の文字列を全角英数に変換します。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月27日
*/
public class LeafTextConverter{
	
	/**
	*半角英数→全角英数の変換を行います。
	*@param str 変換対象の文字列
	*/
	public static String halfToFullsize(String str){
		return halfAlfabetToFullsize(halfNumberToFullsize(str));
	}
	/**
	*全角英数→半角英数の変換を行います。
	*@param str 変換対象の文字列
	*/
	public static String fullToHalfsize(String str){
		return fullAlfabetToHalfsize(fullNumberToHalfsize(str));
	}
	/**
	*半角英字→全角英字の変換を行います。
	*@param str 変換対象の文字列
	*/
	public static String halfAlfabetToFullsize(String str){
		if(str==null)return "";
		StringBuffer buf = new StringBuffer(str);
		for(int i=0;i<buf.length();i++){
			char ch = buf.charAt(i);
			if(ch>='a'&&ch<='z'){
				buf.setCharAt(i,(char)(ch-'a'+'ａ'));
			}else if(ch>='A'&&ch<='Z'){
				buf.setCharAt(i,(char)(ch-'A'+'Ａ'));
			}
		}return buf.toString();
	}
	/**
	*全角英字→半角英字の変換を行います。
	*@param str 変換対象の文字列
	*/
	public static String fullAlfabetToHalfsize(String str){
		if(str==null)return "";
		StringBuffer buf = new StringBuffer(str);
		for(int i=0;i<buf.length();i++){
			char ch = buf.charAt(i);
			if(ch>='ａ'&&ch<='ｚ'){
				buf.setCharAt(i,(char)(ch-'ａ'+'a'));
			}else if(ch>='Ａ'&&ch<='Ｚ'){
				buf.setCharAt(i,(char)(ch-'Ａ'+'A'));
			}
		}return buf.toString();
	}
	/**
	*半角数字→全角数字の変換を行います。
	*@param str 変換対象の文字列
	*/
	public static String halfNumberToFullsize(String str){
		if(str==null)return "";
		StringBuffer buf = new StringBuffer(str);
		for(int i=0;i<buf.length();i++){
			char ch=buf.charAt(i);
			if(ch>='0'&&ch<='9'){
				buf.setCharAt(i,(char)(ch-'0'+'０'));
			}
		}return buf.toString();
	}
	/**
	*全角数字→半角数字の変換を行います。
	*@param str 変換対象の文字列
	*/
	public static String fullNumberToHalfsize(String str){
		if(str==null)return "";
		StringBuffer buf = new StringBuffer(str);
		for(int i=0;i<buf.length();i++){
			char ch=buf.charAt(i);
			if(ch>='０'&&ch<='９'){
				buf.setCharAt(i,(char)(ch-'０'+'0'));
			}
		}return buf.toString();
	}
	/**
	*大文字→小文字字の変換を行います。
	*@param str 変換対象の文字列
	*/
	public static String toLowerCase(String str){
		if(str==null)return "";
		return str.toLowerCase();
	}
	/**
	*小文字→大文字の変換を行います。
	*@param str 変換対象の文字列
	*/
	public static String toUpperCase(String str){
		if(str==null)return "";
		return str.toUpperCase();
	}
	/**
	*全角ひらがな→全角カタカナの変換を行います。
	*@param str 変換対象の文字列
	*/
	public static String hiraganaToKatakana(String str){
		if(str==null)return "";
		StringBuffer buf = new StringBuffer(str);
		for(int i=0;i<buf.length();i++){
			char ch = buf.charAt(i);
			if(ch>='ぁ'&&ch<='ん'){
				buf.setCharAt(i,(char)(ch-'ぁ'+'ァ'));
			}
		}return buf.toString();
	}
	/**
	*全角カタカナ→全角ひらがなの変換を行います。
	*@param str 変換対象の文字列
	*/
	public static String katakanaToHiragana(String str){
		if(str==null)return "";
		StringBuffer buf = new StringBuffer(str);
		for(int i=0;i<buf.length();i++){
			char ch = buf.charAt(i);
			if(ch>='ァ'&&ch<='ン'){
				buf.setCharAt(i,(char)(ch-'ァ'+'ぁ'));
			}else if(ch=='ヵ'){
				buf.setCharAt(i,'か');
			}else if(ch=='ヶ'){
				buf.setCharAt(i,'け');
			}else if(ch=='ヴ'){
				buf.setCharAt(i,'う');
				buf.insert(i+1,'゛');
				i++;
			}
		}return buf.toString();
	}
}
