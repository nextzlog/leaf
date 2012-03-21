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
 *動的言語で用いられる局所関数ポインタの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年7月30日
 */
public final class Closure{
	private final Frame enclosure;
	private final int jump, locals;
	private final String name;
	/**
	*局所関数を生成します。
	*@param jump 関数の開始位置
	*@param frame エンクロージャーの環境
	*@param locals 局所関数内の変数の個数
	*/
	public Closure(int jump, Frame frame, int locals){
		this.jump = jump;
		this.locals = locals;
		//局所関数名
		int i = 0;
		for(Frame f=enclosure=frame; f!=null; i++){
			f = (frame = f).getParent();
		}
		name = ordinal(i) + " closure(" +
		locals + ") in " + frame.getName();
	}
	/**
	*局所関数の開始位置を返します。
	*@return ジャンプ先
	*/
	public int getJump(){
		return jump;
	}
	/**
	*局所関数自体に宣言されたローカル変数の個数を返します。
	*@return ローカル変数の個数
	*/
	public int getLocalCount(){
		return locals;
	}
	/**
	*局所関数が生成された環境への参照を返します。
	*@return エンクロージャの環境
	*/
	public Frame getEnvironment(){
		return enclosure;
	}
	/**
	*指定されたオブジェクトと等価であるか返します。
	*@param obj 比較するオブジェクト
	*@return 同じ局所関数を示す場合true
	*/
	public boolean equals(Object obj){
		if(obj instanceof Closure){
			return ((Closure)obj).getJump() == jump;
		}
		return false;
	}
	/**
	*局所関数の文字列化表現を返します。
	*@return 文字列による表現
	*/
	public String toString(){
		return name;
	}
	/**
	*階数を表現する文字列を返します。
	*@param number 番号
	*@return 番号に対応する文字列
	*/
	private String ordinal(int number){
		String result = Integer.toString(number);
		int hr = number % 100;
		int tr = number % 10;
		if(hr-tr == 10) return result.concat("th");
		switch(tr){
			case 1 : return result.concat("st");
			case 2 : return result.concat("nd");
			case 3 : return result.concat("rd");
			default: return result.concat("th");
		}
	}
}