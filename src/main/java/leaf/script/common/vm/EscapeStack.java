/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.common.vm;

/**
 *仮想機械で例外処理用情報を保持するためのスタックです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月10日
 */
public final class EscapeStack{
	private List top;
	
	/**
	*新しい脱出情報を生成して積みます。
	*@param pc 脱出ジャンプ先位置
	*@param sp 脱出先でのスタックポインタ
	*@param frame 脱出先関数のフレーム
	*/
	public void push(int pc, int sp, Frame frame){
		push(new EscapeInfo(pc, sp, frame));
	}
	/**
	*脱出情報をスタックに積みます。
	*@param info 脱出情報
	*/
	public void push(EscapeInfo info){
		top = new List(info, top);
	}
	/**
	*先頭の脱出情報を取り出して削除します。
	*@return 取り出した脱出情報
	*/
	public EscapeInfo pop(){
		if(top != null){
			List ret = top;
			top = top.next;
			return ret.value;
		}
		return null;
	}
	/**
	*先頭の脱出情報を返します。
	*@return 取り出した値
	*/
	public EscapeInfo peek(){
		return top.value;
	}
	/**
	*先頭の脱出情報を削除します。
	*/
	public void delete(){
		top = top.next;
	}
	/**
	*リンクリストの実装です。
	*/
	private final class List{
		public final EscapeInfo value;
		public final List next;
		public List(EscapeInfo value, List next){
			this.value = value;
			this.next  = next;
		}
	}
}