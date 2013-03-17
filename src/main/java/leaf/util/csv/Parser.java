/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.csv;

/**
 * CSV形式の簡易的なパーサーです。
 * 
 * 基本的なエスケープ表現「""」には対応しています。
 * 改行文字を含み多段にわたる「セル」や「行」はサポートされません。
 * 
 * セル先頭の空白文字は、セルが「"」で囲まれていない場合削除されます。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年9月16日
 */
final class Parser {
	private static final byte STATE_INITIAL = 0;
	private static final byte STATE_ESCAPED = 1;
	private static final byte STATE_QUOTED  = 2;
	private static final byte STATE_NOT_QUOTED = 3;
	
	private int state;
	private Row row;
	private StringBuilder cell = new StringBuilder();
	
	/**
	 * CSV形式を1行だけ構文解析します。
	 * 
	 * @param line CSV形式の1行
	 * @return 初期化された「行」
	 */
	public Row parse(String line){
		this.row = new Row();
		this.state = STATE_INITIAL;
		final int length = line.length();
		for(int i=0; i<length;){
			if(next(line.charAt(i))) i++;
		}
		if(cell.length() > 0){
			row.add(new Cell(cell));
			cell.setLength(0);
		}
		return row;
	}
	
	/**
	 * 次の文字を読み込んで解析します。
	 * 
	 * @param ch 次の文字
	 * @return 文字の参照位置をインクリメントするか指定
	 */
	private boolean next(char ch){
		switch(state){
			case STATE_INITIAL : return next_initial(ch);
			case STATE_ESCAPED : return next_escaped(ch);
			case STATE_QUOTED : return next_quoted(ch);
			default : return next_not_quoted(ch);
		}
	}
	
	/**
	 * 初期状態
	 * 
	 * @param ch 次の文字
	 * @return 文字の参照位置をインクリメントするか指定
	 */
	private boolean next_initial(char ch){
		if(ch == ',') row.add(new Cell(""));
		else if(ch == '"') state = STATE_QUOTED;
		else if(!Character.isWhitespace(ch)){
			state = STATE_NOT_QUOTED;
			cell.append(ch);
		}
		return true;
	}
	
	/**
	 * エスケープ文字列を読み込んでいる状態
	 * 
	 * @param ch 次の文字
	 * @return 文字の参照位置をインクリメントするか指定
	 */
	private boolean next_escaped(char ch){
		if(ch == '"'){
			state = STATE_QUOTED;
			cell.append('"');
			return true;
		}else{
			state = STATE_INITIAL;
			row.add(new Cell(cell));
			cell.setLength(0);
			return false;
		}
	}
	
	/**
	 * 「"」で囲まれたセルを読み込んでいる状態
	 * 
	 * @param ch 次の文字
	 * @return 文字の参照位置をインクリメントするか指定
	 */
	private boolean next_quoted(char ch){
		if(ch == '"') state = STATE_ESCAPED;
		else cell.append(ch);
		return true;
	}
	
	/**
	 * 「"」で囲まれていないセルを読み込んでいる状態
	 * 
	 * @param ch 次の文字
	 * @return 文字の参照位置をインクリメントするか指定
	 */
	private boolean next_not_quoted(char ch){
		if(ch == ','){
			state = STATE_INITIAL;
			row.add(new Cell(cell));
			cell.setLength(0);
		}else cell.append(ch);
		return true;
	}
}