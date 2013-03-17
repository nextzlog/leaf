/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.bnf;

import java.util.Map;

import javax.script.ScriptException;

import leaf.script.parser.ast.Node;
import leaf.script.parser.ast.NodeList;
import leaf.script.parser.com.Combinator;
import leaf.script.parser.com.Operator;
import leaf.script.parser.com.Operator.Associative;
import leaf.script.parser.lex.Lexer;
import leaf.script.parser.lex.Token;

/**
 * BNF構文規則表現において、演算子準位法による式のボトムアップ解析を規定します。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public abstract class Expression extends Element {
	
	private Map<String, Operator> operators;
	private Combinator combinator;
	
	/**
	 * 隷下のコンビネータと、演算子のマップを指定して要素を構築します。
	 * 
	 * @param combinator 隷下のコンビネータ
	 * @param operators  演算子の表記と演算子とのマッピングオブジェクト
	 */
	public Expression(Combinator combinator, Map<String, Operator> operators) {
		this.combinator = combinator;
		this.operators = operators;
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
		Node right = combinator.parse(lexer);
		Operator op;
		
		while((op = getNext(lexer)) != null) {
			right = parseBottomUp(lexer, right, op);
		}
		
		result.add(right);
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

	private Operator getNext(Lexer lexer) {
		Token token = lexer.getToken(0);
		return token.isIdentifier()? operators.get(token.toString()) : null;
	}

	private Node parseBottomUp(Lexer lexer, Node left, Operator parent)
	throws ScriptException {
		final Operator op = operators.get(lexer.getNextToken().toString());
		
		NodeList list = new NodeList();
		list.add(left);
		list.add(op.createNode());
		
		Node right = combinator.parse(lexer);
		Operator next;
		
		while((next = getNext(lexer)) != null && hasNext(parent, next)) {
			right = parseBottomUp(lexer, right, next);
		}
		
		list.add(right);
		return list.toNode();
	}

	private boolean hasNext(Operator parent, Operator child) {
		if(child.getAssociative() == Associative.LEFT) {
			return parent.getOrder() < child.getOrder();
		}
		
		return parent.getOrder() <= child.getOrder();
	}

}