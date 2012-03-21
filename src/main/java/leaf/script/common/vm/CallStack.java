/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.common.vm;

import leaf.script.common.util.Code;

/**
 *仮想機械で用いられるコールスタックの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年7月31日
 */
public final class CallStack extends Stack<Code>{
	/**
	*最大容量を指定してコールスタックを生成します。
	*@param cap スタックの最大容量
	*/
	public CallStack(int cap){
		super(cap);
	}
	/**
	*スタックに真偽値を積みます。
	*@param value 積む値
	*/
	public void push(boolean value){
		super.push(new Code(value));
	}
	/**
	*スタックに文字値を積みます。
	*@param value 積む値
	*/
	public void push(char value){
		super.push(new Code(value));
	}
	/**
	*スタックにバイト値を積みます。
	*@param value 積む値
	*/
	public void push(byte value){
		super.push(new Code(value));
	}
	/**
	*スタックに整数値を積みます。
	*@param value 積む値
	*/
	public void push(short value){
		super.push(new Code(value));
	}
	/**
	*スタックに整数値を積みます。
	*@param value 積む値
	*/
	public void push(int value){
		super.push(new Code(value));
	}
	/**
	*スタックに倍精度整数値を積みます。
	*@param value 積む値
	*/
	public void push(long value){
		super.push(new Code(value));
	}
	/**
	*スタックに浮動小数点数を積みます。
	*@param value 積む値
	*/
	public void push(float value){
		super.push(new Code(value));
	}
	/**
	*スタックに倍精度浮動小数点数を積みます。
	*@param value 積む値
	*/
	public void push(double value){
		super.push(new Code(value));
	}
}