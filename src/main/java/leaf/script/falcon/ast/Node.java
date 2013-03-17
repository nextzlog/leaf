/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.ast;

import java.io.PrintWriter;
import java.io.StringWriter;

import leaf.script.falcon.ast.stmt.Scope;
import leaf.script.falcon.error.ResolutionException;
import leaf.script.falcon.type.Type;
import leaf.script.falcon.vm.CodeList;

/**
 * 全ての構文木の抽象クラスです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/24
 *
 */
public abstract class Node {
	private final int line;
	
	/**
	 * 出現する行を指定して木を構築します。
	 * 
	 * @param line 行番号
	 */
	public Node(int line) {
		this.line = line;
	}
	
	/**
	 * 出現する行を返します。
	 * 
	 * @return 行番号
	 */
	public final int getLine() {
		return line;
	}
	
	/**
	 * 木の表現を{@link PrintWriter}に書き込みます。
	 * 
	 * @param pw 書きこむPrintWriter
	 */
	public abstract void print(PrintWriter pw);
	
	/**
	 * 木の表現を文字列で返します。
	 * 
	 * @return 文字列
	 * @see #print(PrintWriter)
	 */
	@Override
	public final String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		print(pw);
		return sw.toString();
	}
	
	/**
	 * 再帰的に変数や関数を解決し演算の型を確定します。
	 * 
	 * @param scope 構文木に適用されるスコープ
	 * @return 式木である場合その戻り値の型
	 * 
	 * @throws OperationException  演算子の型エラー
	 * @throws ResolutionException 解決に失敗した場合
	 */
	public abstract Type resolve(Scope scope)
		throws ResolutionException;
	
	/**
	 * この構文木に対応する中間言語命令列を生成します。
	 * 
	 * @param list 命令列を追加するリスト
	 */
	public abstract void gencode(CodeList list);

}
