/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.lisp;

/**
 * 字句解析器から出力される字句を表現します。
 * 
 * @since 2012年10月11日
 * @author 東大アマチュア無線クラブ
 */
final class Token{
	private TokenType type;
	private final StringBuilder sb;
	
	public Token(){
		type = null;
		sb = new StringBuilder();
	}
	
	public Token setType(TokenType type){
		this.type = type;
		return this;
	}
	
	public TokenType getType(){
		return type;
	}
	
	public boolean isType(TokenType type){
		return this.type == type;
	}
	
	public Token append(char ch){
		sb.append(ch);
		return this;
	}
	
	@Override public String toString(){
		return sb.toString();
	}
}
