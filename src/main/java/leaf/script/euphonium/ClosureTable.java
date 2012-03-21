/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium;

import leaf.script.common.vm.Closure;
import leaf.script.common.vm.Frame;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import javax.script.ScriptException;

import leaf.manager.LeafLocalizeManager;

/**
 *不正な局所関数ポインタを排除するテーブルです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.3 作成：2011年8月17日
 */
final class ClosureTable{
	private final LinkedList<WeakReference> list;
	private final ReferenceQueue<Closure> queue;
	private final LeafLocalizeManager localize;
	/**
	*テーブルを生成します。
	*/
	public ClosureTable(){
		list = new LinkedList<WeakReference>();
		queue = new ReferenceQueue<Closure>();
		localize = LeafLocalizeManager.getInstance(getClass());
	}
	/**
	*管理された局所関数を生成します。
	*@param jump 局所関数の開始位置
	*@param frame 局所関数の参照する環境
	*@param locals 局所関数の持つ変数の個数
	*@return 生成された局所関数
	*/
	public Closure createClosure(int jump, Frame frame, int locals){
		Closure closure = new Closure(jump, frame, locals);
		list.add(new WeakReference<Closure>(closure, queue));
		sweep();
		return closure;
	}
	/**
	*指定した局所関数の発行元を検査します。
	*@param closure 検査対象の局所関数
	*@throws ScriptException 不正な局所関数を摘発した場合
	*/
	public void checkClosure(Closure closure) throws ScriptException{
		sweep();
		for(WeakReference ref : list){
			if(ref.get() == closure) return;
		}
		throw new ScriptException(localize.translate("checkClosure_exception"));
	}
	/**
	*解放済みの局所関数への参照を削除します。
	*/
	private void sweep(){
		Reference ref;
		while((ref = queue.poll())!=null) list.remove(ref);
	}
}
