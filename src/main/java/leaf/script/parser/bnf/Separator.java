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
import leaf.script.parser.com.SyntaxException;
import leaf.script.parser.lex.Lexer;
import leaf.script.parser.lex.Token;

/**
 * BNF構文規則表現において、構文解析木には直接含まれない終端記号を規定します。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public class Separator extends Element {
	
	private String pattern;
	
	/**
	 * 非終端記号の表記を指定して要素を構築します。
	 * 
	 * @param pattern 非終端記号の表記
	 */
	public Separator(String pattern) {
		this.pattern = pattern;
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
		Token token = lexer.getNextToken();
		if(!token.isIdentifier() || !pattern.equals(token.toString())) {
			throw new SyntaxException(token, token + " is not terminal symbol");
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
		Token token = lexer.getNextToken();
		return token.isIdentifier() && pattern.equals(token.toString());
	}

}
