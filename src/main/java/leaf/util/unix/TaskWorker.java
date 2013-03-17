/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.unix;

/**
 * 時間的コストの高いタスクを実行する基底クラスです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年9月3日
 */
public class TaskWorker<V>{
	private boolean isCanceled = false;
	/**
	 * タスクを生成します。
	 */
	public TaskWorker(){}
	
	/**
	 * 現在の処理の内容の通知を受け取ります。
	 * 
	 * @param obj 直後に処理するオブジェクト
	 * @param index 現在のステップ番号
	 * @param step 総ステップ数
	 */
	public void progress(V obj, int index, int step){}
	
	/**
	 * タスクの実行の中断を試みます。
	 */
	public final void cancel(){
		isCanceled = true;
	}
	
	/**
	 * タスクの中断を要求されたか返します。
	 * 
	 * @return タスクが中断される場合true
	 */
	public final boolean isCancelled(){
		return isCanceled;
	}
}