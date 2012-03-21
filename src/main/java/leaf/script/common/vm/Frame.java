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

import java.util.Arrays;

/**
 *仮想機械の関数フレームの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年7月30日
 */
public class Frame{
	private final String name;
	private final Frame enclosure;
	private final Code[] locals;
	private Code[] args = null;
	private final int pc;
	/**
	*関数フレームを構築します。
	*@param name 関数名
	*@param ef エンクロージャの関数フレーム
	*@param pc 呼び出し元のプログラムカウンタ
	*@param lc ローカル変数の個数
	*/
	public Frame(String name, Frame ef, int pc, int lc){
		this.name = name;
		locals = new Code[lc];
		this.pc = pc;
		enclosure = ef;
	}
	/**
	*フレームに対応する関数名を返します。
	*@return 関数名
	*/
	public String getName(){
		return name;
	}
	/**
	*エンクロージャの関数フレームを返します。
	*@return 親フレーム
	*/
	public Frame getParent(){
		return enclosure;
	}
	/**
	*指定されたローカル変数を返します。
	*@param nest クロージャのネスト階数
	*@param index 変数の登録番号
	*@return 変数の値
	*/
	public Code getLocal(int nest, int index){
		if(nest == 0) return locals[index];
		else return enclosure.getLocal(nest-1, index);
	}
	/**
	*指定されたローカル変数に代入します。
	*@param nest クロージャのネスト階数
	*@param index 変数の登録番号
	*@param value 変数の値
	*/
	public void setLocal(int nest, int index, Code value){
		if(nest == 0) locals[index] = value;
		else enclosure.setLocal(nest-1, index, value);
	}
	/**
	*指定された実引数を返します。
	*@param index 引数の登録番号
	*@return 変数の値
	*/
	public Code getArgument(int index){
		try{
			return args[index];
		}catch(NullPointerException ex){
			return new Code(null);
		}catch(IndexOutOfBoundsException ex){
			return new Code(null);
		}
	}
	/**
	*指定された実引数に代入します。
	*@param index 引数の登録番号
	*@param value 引数の値
	*/
	public void setArgument(int index, Code value){
		try{
			args[index] = value;
		}catch(NullPointerException ex){
			args = new Code[index+1];
			args[index] = value;
		}catch(IndexOutOfBoundsException ex){
			args = Arrays.copyOf(args, index+1);
			args[index] = value;
		}
	}
	/**
	*配列を用いて実引数の値を設定します。
	*@param args 引数の値の配列
	*/
	public void setArguments(Code[] args){
		this.args = args;
	}
	/**
	*関数から復帰する際に引数への参照を削除します。
	*/
	public void clearArguments(){
		args = null;
	}
	/**
	*関数から復帰する際の復帰位置を返します。
	*@return 復帰位置
	*/
	public int getJump(){
		return pc;
	}
}
