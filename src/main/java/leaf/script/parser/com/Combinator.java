/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.com;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import leaf.script.parser.ast.Node;
import leaf.script.parser.ast.NodeList;
import leaf.script.parser.bnf.Element;
import leaf.script.parser.lex.Lexer;

/**
 * 構文解析器を動的に構築するパーサーコンビネータの実装です。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public final class Combinator {

	private List<Element> elements;
	
	/**
	 * 隷下の要素を持たない空のコンビネータを構築します。
	 * 
	 */
	public Combinator() {
		this.elements = new ArrayList<Element>();
	}
	
	/**
	 * 字句解析器を引数に構文を解析して、構文解析木を生成します。
	 * 
	 * @param lexer  字句解析器
	 * @return 構文を解析して生成された構文解析器
	 * 
	 * @throws ScriptException 構文エラーがあった場合にスローされます
	 */
	public Node parse(Lexer lexer) throws ScriptException {
		final NodeList results = new NodeList();
		
		for(Element elem : elements) {
			elem.parse(lexer, results);
		}
		
		return results.toNode();
	}
	
	/**
	 * 字句解析器の内容が、この要素に適合するか確認します。
	 * 
	 * @param lexer 字句解析器
	 * @return この構文規則に適合する内容である場合trueを返す
	 * 
	 * @throws ScriptException 構文エラーがあった場合にスローされます。
	 */
	public boolean match(Lexer lexer) throws ScriptException {
		if(elements.isEmpty()) return true;
		return elements.get(0).match(lexer);
	}
	
	/**
	 * このコンビネータの隷下に、構文規則を規定する要素を追加します。
	 * 
	 * @param element 追加する要素
	 */
	public void addElement(Element element) {
		elements.add(element);
	}

}
