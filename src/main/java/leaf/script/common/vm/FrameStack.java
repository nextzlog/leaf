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
 *仮想機械で関数フレームを保持するためのスタックです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年7月31日
 */
public final class FrameStack extends Stack<Frame>{
	/**
	*最大容量を指定してフレームスタックを生成します。
	*@param cap スタックの最大容量
	*/
	public FrameStack(int cap){
		super(cap);
	}
	/**
	*新しいフレームを生成して積みます。
	*@param ef エンクロージャの関数フレーム
	*@param pc 呼び出し元のプログラムカウンタ
	*@param lc ローカル変数の個数
	*/
	public void push(String name, Frame ef, int pc, int lc){
		super.push(new Frame(name, ef, pc, lc));
	}
	/**
	*局所関数のフレームを生成して積みます。
	*@param closure 局所関数
	*@param pc 呼び出し元のプログラムカウンタ
	*/
	public void push(Closure closure, int pc){
		Frame ef = closure.getEnvironment();
		int lc = closure.getLocalCount();
		String name = closure.toString();
		this.push(name, ef, pc, lc);
	}
}