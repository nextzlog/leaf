/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast.stmt;

import java.io.PrintWriter;

import leaf.script.falcon.error.ImportException;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.type.TypeLoader;
import leaf.script.falcon.vm.CodeList;

/**
 * import文の構文木の実装です。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/02/18
 *
 */
public final class Import extends Stmt {
	private final TypeLoader loader;
	private final String name;
	private Type type = null;
	
	/**
	 * クラス名を指定して構文木を構築します。
	 * 
	 * @param line 行番号
	 * @param name importするクラス名
	 */
	public Import(int line, String name) {
		super(line);
		this.name = name;
		loader = new TypeLoader();
	}
	
	@Override
	public void print(PrintWriter pw) {
		pw.print("import ");
		pw.print(name);
		pw.println(";");
		pw.flush();
	}
	
	@Override
	public Type resolve(Scope scope) {
		return null;
	}

	@Override
	public void gencode(CodeList list) {}
	
	/**
	 * インポートされるクラスの正準名を返します。
	 * 
	 * @return クラスの正準名
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * インポートされるクラスの単純名を返します。
	 * 
	 * @return クラスの単純名
	 */
	public String getSimpleName() {
		int i = name.lastIndexOf('.');
		return name.substring(i + 1);
	}
	
	/**
	 * インポートされるオブジェクト型を返します。
	 * 
	 * @return オブジェクト型
	 * @throws ImportException
	 */
	public Type getType()
	throws ImportException {
		if(type != null) return type;
		return type = loader.load(this);
	}

}
