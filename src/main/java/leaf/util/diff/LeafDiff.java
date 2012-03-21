/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.diff;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;

import static leaf.util.diff.Edit.*;

/**
 *新旧配列間の差分を抽出し、追加・維持・削除の編集操作列に変換します。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年9月20日
 */
public class LeafDiff{
	
	/**
	 *差分抽出コマンドを生成します。
	 */
	public LeafDiff(){}
	
	/**
	 *オブジェクトの比較時に呼び出される委譲メソッドです。
	 *@param oobj 古い配列側のオブジェクト
	 *@param nobj 新しい配列側のオブジェクト
	 *@return 等価である場合には真を返す
	 */
	protected boolean equals(Object oobj, Object nobj){
		try{
			return oobj.equals(nobj);
		}catch(NullPointerException ex){
			return oobj == null && nobj == null;
		}
	}
	/**
	 *新旧の配列の差分を抽出して編集操作列を返します。
	 *@param oldarr 古い配列
	 *@param newarr 新しい配列
	 *@return 編集内容
	 */
	public EditList compare(Object[] oldarr, Object[] newarr){
		LinkedList<Edit> list = new LinkedList<Edit>();
		int[][] table = new int[oldarr.length+1][newarr.length+1];
		for(int n1 = oldarr.length-1; n1 >= 0; n1--){
		for(int n2 = newarr.length-1; n2 >= 0; n2--){
			if(equals(oldarr[n1], newarr[n2])){
				table[n1][n2] = table[n1+1][n2+1] + 1;
			}else{
				table[n1][n2] = Math.max
				(table[n1][n2+1], table[n1+1][n2]);
			}
		}
		}
		int n1 = 0, n2 = 0;
		while(n1 < oldarr.length && n2 < newarr.length){
			if(equals(oldarr[n1], newarr[n2])){
				list.add(new Edit(COMMON, oldarr[n1++]));
				n2++;
			}else if(table[n1+1][n2] >= table[n1][n2+1]){
				list.add(new Edit(DELETE, oldarr[n1++]));
			}else{
				list.add(new Edit(ADD, newarr[n2++]));
			}
		}
		while(n1 < oldarr.length || n2 < newarr.length){
			if(n1 == oldarr.length){
				list.add(new Edit(ADD, newarr[n2++]));
			}else if(n2 == newarr.length){
				list.add(new Edit(DELETE, oldarr[n1++]));
			}
		}
		return new EditList(list.toArray(new Edit[0]));
	}
	/**
	 *新旧のテキストファイルの差分を抽出して編集操作列を返します。
	 *@param of 古いファイル
	 *@param nf 新しいファイル
	 *@param os 古いファイルの文字セット
	 *@param ns 新しいファイルの文字セット
	 *@return 編集内容
	 */
	public EditList compare(File of, File nf, Charset os, Charset ns)
	throws IOException{
		return compare(read(of, os),read(nf, ns));
	}
	/**
	 *比較元の文字列と比較先のテキストファイルの差分を
	 *抽出して編集操作列を返します。
	 *@param ot 比較元の文字列
	 *@param nf 比較先のファイル
	 *@param ns 比較先の文字セット
	 */
	public EditList compare(String ot, File nf, Charset ns)
	throws IOException{
		return compare(ot.split("\r?\n"), read(nf, ns));
	}
	/**
	 *比較元のテキストファイルと比較先の文字列の差分を
	 *抽出して編集操作列を返します。
	 *@param of 比較元のファイル
	 *@param nt 比較先の文字列
	 *@param os 比較元の文字セット
	 */
	public EditList compare(File of, String nt, Charset os)
	throws IOException{
		return compare(read(of, os), nt.split("\r?\n"));
	}
	/**
	 *ファイルから文字列を読み込んで返します。
	 *@param file ファイル
	 *@param chset 文字セット
	 */
	private String[] read(File file, Charset chset) throws IOException{
		FileInputStream stream = new FileInputStream(file);
		FileChannel channel = stream.getChannel();
		MappedByteBuffer buffer = channel.map(
			FileChannel.MapMode.READ_ONLY, 0, channel.size());
		CharsetDecoder decoder = chset.newDecoder();
		CharBuffer cb = decoder.decode(buffer);
		channel.close();
		stream.close();
		return cb.toString().split("\r?\n");
	}
}
