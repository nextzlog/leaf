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
package leaf.util.diff;

/**
*新旧の配列間の編集内容を表すラッパークラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月20日
*/

public class Edit{
	
	/**要素の追加挿入操作です。*/
	public static final int ADD = 0;
	/**要素の削除操作です。*/
	public static final int DELETE = 1;
	/**要素の維持操作です。*/
	public static final int NO_CHANGE = 2;
	
	private final int type;
	private final String content;
	
	/**
	*操作の種類と対象の要素の内容を指定して編集を生成します。
	*@param type 操作の種類 ({@link #ADD} か {@link #DELETE})
	*@param content 要素の内容
	*/
	public Edit(int type, String content){
		this.type = type;
		this.content = content;
	}
	/**
	*操作の種類を返します。
	*@return 操作の種類 ({@link #ADD} か {@link #DELETE})
	*/
	public int getType(){
		return type;
	}
	/**
	*要素の内容を返します。
	*@return 要素の内容
	*/
	public String getContent(){
		return content;
	}
	/**
	*編集内容を表す文字列表現を返します。
	*@return 文字列表現
	*/
	public String toString(){
		switch(type){
			case ADD:
				return "+ " + content;
			case DELETE:
				return "- " + content;
			default:
				return "  " + content;
		}
	}
}
