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

/**トークンの実装*/
class Token{
	
	private Enum type = null;
	private String value = "";
	
	/**型を返す*/
	public Enum getType(){
		return type;
	}
	/**型を設定*/
	public void setType(Enum type){
		this.type = type;
	}
	/**文字を追加*/
	public Token add(char ch){
		value += Character.toString(ch);
		return this;
	}
	/**整数値を返す*/
	public int toInteger(){
		return Integer.parseInt(value);
	}
	/**文字列値を返す*/
	public String toString(){
		return value;
	}
}