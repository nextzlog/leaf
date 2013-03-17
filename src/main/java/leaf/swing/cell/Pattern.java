/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.cell;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *このクラスを利用すればセルオートマータの状態をストリームに保存できます。
 *
 *サポートする状態の種類は1バイトまでであり、
 *これを超えるセルオートマトンについては適切な他の実装が必要です。
 *
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.4 作成：2012年3月17日
 */
public final class Pattern{
	private static final String MAGIC = "cellular automata leafapi";
	private int[][] table;
	
	/**
	 *テーブルデータを指定してパターンを構築します。
	 *
	 *@param table テーブルデータ
	 */
	private Pattern(int[][] table){
		this.table = table;
	}
	
	/**
	 *このパターンをセルオートマータに適用します。
	 *
	 *@param automata セルオートマータ
	 *@return セルオートマータ
	 *
	 *@throws IllegalArgumentException
	 *パターンの大きさとオートマータの大きさが異なる場合
	 */
	public Automata setPattern(Automata automata)
	throws IllegalArgumentException{
		final int aw = automata.getWidth();
		final int ah = automata.getHeight();
		
		final int pw = table.length;
		final int ph = table[0].length;
		
		if(aw == pw && ah == ph){
			for(int y = 0; y < ah; y++){
			for(int x = 0; x < aw; x++){
				automata.setState(x, y, table[x][y]);
			}
			}
			return automata;
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 *セルオートマータの現在のパターンを取得します。
	 *
	 *@param automata セルオートマータ
	 *@return セルオートマータのパターン
	 */
	public static Pattern getPattern(Automata automata){
		final int w = automata.getWidth();
		final int h = automata.getHeight();
		
		int[][] table = new int[w][h];
		
		for(int y = 0; y < h; y++){
		for(int x = 0; x < w; x++){
			table[x][y] = automata.getState(x, y);
		}
		}
		return new Pattern(table);
	}
	
	/**
	 *ストリームを読み込んでパターンを構築します。
	 *
	 *@param input ストリーム
	 *@throws IOException 読み込みに失敗した場合
	 */
	public static Pattern read(InputStream input) throws IOException{
		DataInputStream stream = new DataInputStream(input);
		try{
			for(int i = 0; i < MAGIC.length(); i++){
				int ch = stream.read();
				if(MAGIC.charAt(i) == ch) continue;
				throw new IOException("illegal format");
			}
			
			final int w = stream.readInt();
			final int h = stream.readInt();
			
			if(w >= 0 && h >= 0){
				int[][] table = new int[w][h];
				
				for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					int dat = stream.read();
					if(dat >= 0) table[x][y] = dat;
					else throw new IOException("end of stream");
				}
				}
				return new Pattern(table);
			}
		}finally{
			stream.close();
		}
		throw new IOException("illegal format");
	}
	
	/**
	 *パターンをストリームに書き込みます。
	 *
	 *@param output ストリーム
	 *@throws IOException 書き込みに失敗した場合
	 */
	public void write(OutputStream output) throws IOException{
		DataOutputStream stream = new DataOutputStream(output);
		try{
			for(int i = 0; i < MAGIC.length(); i++){
				stream.write(MAGIC.charAt(i));
			}
			stream.writeInt(table.length);
			stream.writeInt(table[0].length);
			
			final int w = table.length;
			final int h = table[0].length;
			
			for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				stream.write(table[x][y]);
			}
			}
		}finally{
			stream.close();
		}
	}
}