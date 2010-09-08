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

/**ラベルの実装*/
class Label{
	private final String name;
	private int address = -1;
	/**コンストラクタ*/
	public Label(String name){
		this.name    = name;
	}
	/**名前を返す*/
	public String name(){
		return name;
	}
	/**アドレスを設定*/
	public void setAddress(int address){
		this.address = address;
	}
	/**アドレスを返す*/
	public int address(){
		return address;
	}
}