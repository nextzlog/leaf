/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon.lex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

/**
 * リーダーを文字列に読み込みます。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/22
 *
 */
public class ReaderString {
	private String string;
	private int index = 0;
	private int mark = -1;
	
	private ReaderString(String string) {
		this.string = string;
	}
	
	/**
	 * 指定された位置の文字を返します。
	 * 
	 * @param n 位置
	 * @return n番目の文字 範囲外の場合NULL文字
	 */
	public char charAt(int n) {
		try {
			return string.charAt(n);
		} catch(IndexOutOfBoundsException ex) {
			return '\0';
		}
	}
	
	/**
	 * 次の文字を返してカーソルを進めます。
	 * 
	 * @return 次の文字 範囲外の場合NULL文字
	 */
	public char nextChar() {
		return charAt(index++);
	}
	
	/**
	 * カーソルを1文字戻します。
	 */
	public void goBack() {
		index--;
	}
	
	/**
	 * 現在カーソルがある位置をマークします。
	 * 
	 * @see #getStringFromMark()
	 */
	public void mark() {
		this.mark = index;
	}
	
	/**
	 * マークした位置からカーソル直前までの文字列を切り取ります。
	 * 
	 * @return 切り取られた文字列
	 * 
	 * @see #mark()
	 */
	public String getStringFromMark() {
		String sub = string.substring(mark, index);
		mark = -1;
		return sub;
	}
	
	/**
	 * 指定されたリーダーを最後まで読み込んでインスタンスを構築します。
	 * 
	 * @param reader 読み込むリーダー
	 * @return ReaderStringのインスタンス
	 * 
	 * @throws IOException 読み込みに失敗した場合
	 */
	public static ReaderString read(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		String line;
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		while((line = br.readLine()) != null) {
			pw.println(line);
		}
		br.close();
		
		return new ReaderString(sw.toString());
	}

}
