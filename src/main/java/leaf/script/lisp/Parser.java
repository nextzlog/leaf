/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

import java.io.Reader;
import javax.script.ScriptException;

/**
 * LISP処理系のS式リーダー部の構文解析器の実装です。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
public final class Parser{
	private final Lex lex;
	private Bindings bindings;
	
	/**
	 * 環境を指定して構文解析器を構築します。
	 * 
	 * @param bindings 環境
	 */
	public Parser(Bindings bindings){
		this.bindings = bindings;
		lex = new Lex();
	}
	
	/**
	 * S式を読み込むリーダーを関連付けます。
	 * 
	 * @param reader リーダー
	 */
	public void load(Reader reader) throws ScriptException{
		lex.load(reader);
	}
	
	/**
	 * S式を読み込んでS式オブジェクトに変換します。
	 * 
	 * @return S式オブジェクト
	 */
	public Sexp read() throws ScriptException{
		Token token = lex.getNext();
		if(token == null) return null;
		switch(token.getType()){
			case L_BRACE: return readList();
			case QUOTE  : return readQuote();
			default : return readAtom(token);
		}
	}
	
	private Sexp readList() throws ScriptException{
		List top = new List(), list = top;
		while(true){
			Token token = lex.getNext();
			if(token == null) throw error("expected ')'");
			if(token.isType(TokenType.R_BRACE)) {
				return top.isEmpty()? Nil.NIL : top;
			}else if(token.isType(TokenType.DOT)){
				list.cdr(read());
				lex.checkNext(TokenType.R_BRACE);
				return top;
			}else if(!top.isEmpty()){
				list = (List) list.cdr(new List());
			}
			lex.unget(token);
			list.car(read());
		}
	}
	
	private Sexp readQuote() throws ScriptException{
		List list = new List(), quot = new List();
		list.car(bindings.get("quote"));
		list.cdr(quot);
		quot.car(read());
		return list;
	}
	
	private Sexp readAtom(Token token) throws ScriptException{
		final String disp = token.toString();
		switch(token.getType()){
			case REAL  : return Real.parse(disp);
			case STRING: return new LispString(disp);
		}
		if(disp.equals(T.T.toString())) return T.T;
		if(disp.equals(F.F.toString())) return F.F;
		if(token.isType(TokenType.SYMBOL)) {
			Symbol symbol = bindings.get(disp);
			if(symbol != null) return symbol;
			bindings.put(symbol = new Symbol(disp));
			return symbol;
		}else throw error("'" + token + "' is not symbol");
	}
	
	private ScriptException error(String msg){
		return new ScriptException(msg);
	}
}
