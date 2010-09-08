/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.mil;

/**バイトコードの実装*/
class Code{
	
	public static final int INT_TYPE = 0;
	public static final int STRING_TYPE = 1;
	
	private int type;
	private int int_value;
	private String str_value;
	
	/**整数型コンストラクタ*/
	public Code(int value){
		this.type = INT_TYPE;
		this.int_value = value;
	}
	/**文字列型のコンストラクタ*/
	public Code(String value){
		this.type = STRING_TYPE;
		this.str_value = value;
	}
	/**変数型を返す*/
	public int getType(){
		return type;
	}
	/**整数値を返す*/
	public int toInteger(){
		return int_value;
	}
	/**文字列値を返す*/
	public String toString(){
		if(type==INT_TYPE)
			return int_value + "";
		else return str_value;
	}
}