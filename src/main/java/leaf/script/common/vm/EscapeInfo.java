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
 *仮想機械で例外発生時の関数脱出用情報を保持します。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年8月10日
 */
public class EscapeInfo{
	private final int pc, sp;
	private final Frame frame;
	/**
	*脱出先の情報を指定します。
	*@param pc 脱出ジャンプ先位置
	*@param sp 脱出後のスタックポインタ
	*@param frame 脱出先関数のフレーム
	*/
	public EscapeInfo(int pc, int sp, Frame frame){
		this.pc = pc;
		this.sp = sp;
		this.frame = frame;
	}
	/**
	*脱出先位置を返します。
	*@return 脱出ジャンプ先位置
	*/
	public int getJump(){
		return pc;
	}
	/**
	*脱出先でのスタックポインタを返します。
	*@return 脱出後のスタックポインタ
	*/
	public int getStackPointer(){
		return sp;
	}
	/**
	*脱出先関数のフレームを返します。
	*@return 脱出先関数のフレーム
	*/
	public Frame getFrame(){
		return frame;
	}
}