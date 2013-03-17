/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.bnf;

import javax.script.ScriptException;

import leaf.script.parser.ast.NodeList;
import leaf.script.parser.com.Combinator;
import leaf.script.parser.lex.Lexer;

/**
 * BNF構文規則表現において、非終端記号を規定する要素です。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public class Nonterminal extends Element {
	
	private Combinator combinator;
	
	/**
	 * 隷下のコンビネータを指定して要素を構築します。
	 * 
	 * @param combinator 隷下のコンビネータ
	 */
	public Nonterminal(Combinator combinator) {
		this.combinator = combinator;
	}

	/**
	 * 字句解析器と、解析結果を格納するリストを引数に取り、構文を解析します。
	 * 
	 * @param lexer  字句解析器
	 * @param result 解析結果を格納するリスト
	 * 
	 * @throws ScriptException 構文エラーがあった場合にスローされます
	 */
	@Override
	public void parse(Lexer lexer, NodeList result) throws ScriptException {
		result.add(combinator.parse(lexer));
	}

	/**
	 * 字句解析器の内容が、この要素に適合するか確認します。
	 * 
	 * @param lexer 字句解析器
	 * @return この構文規則に適合する内容である場合trueを返す
	 * 
	 * @throws ScriptException 構文エラーがあった場合にスローされます。
	 */
	@Override
	public boolean match(Lexer lexer) throws ScriptException {
		return combinator.match(lexer);
	}
}
