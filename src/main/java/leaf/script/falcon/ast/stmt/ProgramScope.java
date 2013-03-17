/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import java.util.ArrayList;
import java.util.List;

/**
 * プログラム全体のスコープです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class ProgramScope extends Scope {
	protected final List<Import> imports;
	
	public ProgramScope() {
		imports = new ArrayList<Import>();
	}
	
	/**
	 * import文をスコープに追加します。
	 * 
	 * @param imp 追加するimport文
	 */
	public void addImport(Import imp) {
		imports.add(imp);
	}
	
	@Override
	public Import searchImport(String name) {
		for(Import imp : imports) {
			String sn = imp.getSimpleName();
			if(sn.equals(name)) return imp;
		}
		return super.searchImport(name);
	}

}
