/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

import javax.script.ScriptException;

/**
 * LISPのラムダ式を実装するクラスです。
 * 
 * @since 2012年10月13日
 * @author 東大アマチュア無線クラブ
 */
public class Lambda extends List{
	
	/**
	 * 空のラムダ式を構築します。
	 */
	public Lambda() {
		car(new Symbol("lambda"));
	}
	
	/**
	 * このラムダ式に適用される引数の個数が正しいか確認します。
	 * 
	 * @param argSize 引数の個数
	 * @return 引数が多ければ正 少なければ負 適切であれば0
	 * @throws ScriptException ラムダ式が正しく構成されていない場合
	 */
	public int verifyArgumentSize(int argSize) throws ScriptException{
		final Sexp args = cdr().asList().car();
		if(args == Nil.NIL) return argSize;
		return argSize - args.asList().size();
	}
}