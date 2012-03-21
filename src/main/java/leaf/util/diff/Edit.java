/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.diff;

/**
 *新旧の配列間の編集内容を表すコンテナクラスです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年9月20日
 */

public class Edit{
	
	/**要素の追加挿入操作です。*/
	public static final byte ADD    = 0;
	
	/**要素の削除操作です。*/
	public static final byte DELETE = 1;
	
	/**要素の維持操作です。*/
	public static final byte COMMON = 2;
	
	private byte type;
	private Object content;
	
	/**
	 *空の編集を生成します。
	 */
	public Edit(){
		type = COMMON;
	}
	/**
	 *編集の操作と対象の内容を指定して編集を生成します。
	 *@param type 編集の操作
	 *@param cont 対象の内容
	 */
	public Edit(byte type, Object cont){
		if(type >= ADD && type <= COMMON){
			this.type = type;
			this.content = cont;
		}else throw new IllegalArgumentException();
	}
	/**
	 *編集の操作を指定します。
	 *@param type 操作
	 */
	public void setType(byte type){
		this.type = type;
	}
	/**
	 *編集の操作を返します。
	 *@return 操作
	 */
	public byte getType(){
		return type;
	}
	/**
	 *操作対象の内容を指定します。
	 *@param cont 対象の内容
	 */
	public void setContent(Object cont){
		this.content = cont;
	}
	/**
	 *操作対象の内容を返します。
	 *@return 対象の内容
	 */
	public Object getContent(){
		return content;
	}
	/**
	 *編集内容を表す文字列表現を返します。
	 *@return 文字列表現
	 */
	public String toString(){
		String value = String.valueOf(content);
		switch(type){
			case ADD   : return "> ".concat(value);
			case DELETE: return "< ".concat(value);
			case COMMON: return "  ".concat(value);
		}
		return "";
	}
}
