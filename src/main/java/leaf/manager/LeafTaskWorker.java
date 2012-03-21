/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.manager;

/**
 *時間的コストの高いタスクを実行する基底クラスです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年9月3日
 */
public class LeafTaskWorker<V>{
	private boolean isCanceled = false;
	/**
	 *タスクを生成します。
	 */
	public LeafTaskWorker(){}
	
	/**
	 *現在の処理の内容を外部に通知します。
	 *@param obj 直後に処理するオブジェクト
	 *@param index 現在のステップ番号
	 *@param step 総ステップ数
	 */
	public void progress(V obj, int index, int step){}
	
	/**
	 *タスクの実行の中断を試みます。
	 */
	public final void cancel(){
		isCanceled = true;
	}
	
	/**
	 *タスクの中断を要求されたか返します。
	 *@return タスクが中断される場合true
	 */
	public final boolean isCancelled(){
		return isCanceled;
	}
}