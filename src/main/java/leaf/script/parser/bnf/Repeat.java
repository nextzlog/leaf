/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.bnf;

import javax.script.ScriptException;

import leaf.script.parser.ast.ListNode;
import leaf.script.parser.ast.Node;
import leaf.script.parser.ast.NodeList;
import leaf.script.parser.com.Combinator;
import leaf.script.parser.lex.Lexer;

/**
 * BNF構文規則表現において、要素の任意回数もしくは1度限りの繰り返しを規定します。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public class Repeat extends Element {
	
	private Combinator combinator;
	private boolean isOnlyOnce = false;
	
	/**
	 * 隷下のコンビネータと、繰り返し回数の制限を指定して要素を構築します。
	 * 
	 * @param combinator 隷下のコンビネータ
	 * @param once この繰り返しが1度限りである場合にtrueを指定する
	 */
	public Repeat(Combinator combinator, boolean once) {
		this.combinator = combinator;
		this.isOnlyOnce = once;
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
		while(combinator.match(lexer)) {
			Node node = combinator.parse(lexer);
			if(!(node instanceof ListNode)
					|| node.hasChildren()) {
				result.add(node);
			}
			
			if(isOnlyOnce) break;
		}
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