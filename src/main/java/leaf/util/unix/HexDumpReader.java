/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.unix;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * ストリームの内容を16進数ダンプに変換して出力します。
 *
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.4 作成：2012年3月19日
 */
public class HexDumpReader extends Reader {
	private final InputStream stream;
	private StringBuilder sb;
	private byte[] buffer;
	
	/**
	 * 入力を指定してリーダーを構築します。
	 * 
	 * @param stream 入力
	 */
	public HexDumpReader(InputStream stream){
		this.stream = stream;
		sb = new StringBuilder();
		buffer = new byte[16];
	}
	
	/**
	 * 指定された文字を文字として表示するか指定します。
	 * 
	 * @param ch 1バイト分の文字
	 * @return 文字として表示しない場合には「.」
	 */
	private char toChar(byte ch){
		return (ch >= 0x20 && ch <= 0x7E)? (char)ch : '.';
	}
	
	/**
	 * ストリームを閉じて、関連する全てのリソースを解放します。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException{
		sb = null;
		buffer = null;
		stream.close();
	}
	
	/**
	 * 文字型配列の一部にダンプを読み込みます。
	 * 
	 * @param cbuf 文字型配列
	 * @param off  格納開始位置
	 * @param len  格納する長さ
	 * 
	 * @return 読み込まれた長さ 終端に達した場合-1
	 * @throws IOException 入出力エラーが発生した場合
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException{
		int returnVal = 0;
		
		if(sb.length() > 0){
			final int buflen = Math.min(len, sb.length());
			for(int i = 0; i < buflen; i++){
				cbuf[off + i] = sb.charAt(i);
			}
			sb.delete(0, buflen);
			len -= buflen;
			off += buflen;
			returnVal += buflen;
		}
		
		int size;
		while((size = stream.read(buffer)) > 0){
			for(int i = 0; i < size; i++){
				int b = buffer[i] & 0xFF;
				sb.append(Integer.toHexString(b>>4).toUpperCase());
				sb.append(Integer.toHexString(b%16).toUpperCase());
				sb.append(' ');
			}
			for(int i = 0; i < 3 * (16-size); i++) sb.append(' ');
			sb.append(' ');
			for(int i = 0; i < size; i++) sb.append(toChar(buffer[i]));
			sb.append('\n');
			
			if(sb.length() > len) break;
		}
		
		final int lastlen = Math.min(len, sb.length());
		for(int i = 0; i < lastlen; i++){
			cbuf[off + i] = sb.charAt(i);
		}
		sb.delete(0, lastlen);
		returnVal += lastlen;
		
		return (returnVal > 0)? returnVal : -1;
	}
}
