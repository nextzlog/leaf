/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

/**
 * 関数定義全体のスコープです。引数を管理します。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class FunctionScope extends Scope {
	private final Function owner;
	private int paramCount = 0;
	private int localCount = 0;
	
	/**
	 * 関数を指定してスコープを構築します。
	 * 
	 * @param owner スコープを持つ関数
	 */
	public FunctionScope(Function owner) {
		this.owner = owner;
	}
	
	/**
	 * このスコープを持つ関数を返します。
	 * 
	 * @return スコープを持つ関数
	 */
	public Function getOwner() {
		return owner;
	}
	
	/**
	 * このスコープに宣言を追加します。
	 * 
	 * @param param 追加する宣言
	 */
	public void addParam(Param param) {
		Function f = getEnclosureFunction();
		f.getScope().allocate(param);
		super.decls.add(param);
	}
	
	/**
	 * この関数の引数の個数を返します。
	 * 
	 * @return 引数の個数
	 */
	public int getParamCount() {
		return paramCount;
	}
	
	/**
	 * この関数の変数の個数を返します。
	 * 
	 * @return 変数の個数
	 */
	public int getLocalCount() {
		return localCount;
	}
	
	/**
	 * 引数宣言に番号を割り当てます。
	 * 
	 * @param param 引数の宣言
	 */
	public void allocate(Param param) {
		param.allocate(paramCount++);
	}
	
	/**
	 * 変数宣言に番号を割り当てます。
	 * 
	 * @param local 変数の宣言
	 */
	public void allocate(Local local) {
		local.allocate(localCount++);
	}

}
