/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.parser.bnf;

import java.util.Arrays;

import javax.script.ScriptException;

import leaf.script.parser.ast.NodeList;
import leaf.script.parser.com.Combinator;
import leaf.script.parser.com.SyntaxException;
import leaf.script.parser.lex.Lexer;

/**
 * BNF構文規則表現において、規則の選択を示す「|」に相当する要素です。
 *
 *
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.4 作成：2012年7月28日
 *
 */
public class Alternative extends Element {
	
	private Combinator[] combins;
	
	/**
	 * 隷下のコンビネータを指定して要素を構築します。
	 * 
	 * @param combinators 隷下のコンビネータ
	 */
	public Alternative(Combinator... combinators) {
		this.combins = combinators;
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
		Combinator combin = select(lexer);
		
		if(combin != null) result.add(combin.parse(lexer));
		else throw new SyntaxException(lexer.getToken(0));
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
		return select(lexer) != null;
	}
	
	private Combinator select(Lexer lexer) throws ScriptException {
		for(Combinator combin : combins) {
			if(combin.match(lexer)) return combin;
		}
		
		return null;
	}
	
	/**
	 * この要素の隷下に新しい構文規則を実装したコンビネータを追加します。
	 * 
	 * @param combinator 追加するコンビネータ
	 */
	public void addAlternative(Combinator combinator) {
		Combinator[] arr = Arrays.copyOf(combins, combins.length + 1);
		arr[combins.length] = combinator;
		this.combins = arr;
	}

}