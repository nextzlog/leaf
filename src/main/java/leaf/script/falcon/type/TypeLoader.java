/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.type;

import leaf.script.falcon.ast.stmt.Import;
import leaf.script.falcon.error.ImportException;

/**
 * オブジェクト型をロードします。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/02/18
 *
 */
public final class TypeLoader {
	private final ClassLoader loader;
	
	/**
	 * ローダーを構築します。
	 */
	public TypeLoader() {
		loader = getClass().getClassLoader();
	}
	
	/**
	 * 指定されたimport文に対応するオブジェクト型をロードします。
	 * 
	 * @param imp import文
	 * @return 対応する型
	 * @throws ImportException クラスがロードできない場合
	 */
	public Type load(Import imp) throws ImportException {
		try {
			Class<?> c = loader.loadClass(imp.getName());
			return new ObjectType(c);
		} catch (ClassNotFoundException ex) {
			throw new ImportException(imp);
		}
	}

}
