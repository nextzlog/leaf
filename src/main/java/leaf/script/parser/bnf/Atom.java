/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.bnf;

import javax.script.ScriptException;

import leaf.script.parser.ast.Node;
import leaf.script.parser.ast.NodeList;
import leaf.script.parser.com.SyntaxException;
import leaf.script.parser.lex.Lexer;
import leaf.script.parser.lex.Token;

/**
 * BNF構文規則表現において、アトムを表現する要素です。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public abstract class Atom extends Element {
	
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
		
		if(verifyToken(token)) result.add(createNode(token));
		else throw new SyntaxException(token);
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
		return verifyToken(lexer.getToken(0));
	}
	
	/**
	 * このアトムに対応する、隷下の木を持たない構文木を新規に作成します。
	 * 
	 * @param token アトムに対応する字句
	 * @return tokenに対応するアトムの構文木
	 */
	protected abstract Node createNode(Token token);
	
	/**
	 * 指定された字句がこのアトムに適合する字句であるか確認します。
	 * 
	 * @param token 字句
	 * @return tokenがこのアトムに適合する場合trueを返す
	 */
	protected abstract boolean verifyToken(Token token);
}