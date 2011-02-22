/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.diff;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.*;
import java.util.ArrayList;

/**
*新旧のテキスト配列間の差分を抽出し、編集操作
*(追加・維持・削除)の配列に変換するマネージャです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月20日
*@see LeafPatchManager
*/

public class LeafDiffManager{
	
	private final String[] oldArray, newArray;
	private final ArrayList<Edit> edits;
	
	/**
	*新旧の配列を指定してマネージャを生成します。
	*@param oldArray 古い配列
	*@param newArray 新しい配列
	*/
	public LeafDiffManager(Object[] oldArray, Object[] newArray){
		if(oldArray instanceof String[]){
			this.oldArray = (String[]) oldArray;
		}else{
			this.oldArray = new String[oldArray.length];
			for(int i=0;i<oldArray.length;i++){
				this.oldArray[i] = oldArray[i].toString();
			}
		}
		if(newArray instanceof String[]){
			this.newArray = (String[]) newArray;
		}else{
			this.newArray = new String[newArray.length];
			for(int i=0;i<newArray.length;i++){
				this.newArray[i] = newArray[i].toString();
			}
		}
		edits = new ArrayList<Edit>(oldArray.length/3);
	}
	/**
	*配列を比較し差分を抽出して編集内容の配列を返します。
	*@return 編集内容の配列
	*/
	public Edit[] compare(){
		int[][] next = new int[oldArray.length+1][newArray.length+1];
		for(int i=0;i<oldArray.length;i++){
			for(int j=0;j<newArray.length;j++){
				if(oldArray[i].equals(newArray[j])){
					next[i+1][j+1] = next[i][j] + 1;
				}else{
					next[i+1][j+1] = Math.max(next[i][j+1], next[i+1][j]);
				}
			}
		}
		int i = 0, j = 0;
		while(i < oldArray.length && j < newArray.length){
			if(oldArray[i].equals(newArray[j])){
				edits.add(new Edit(Edit.NO_CHANGE, oldArray[i++]));
				j++;
			}else if(next[i+1][j] >= next[i][j+1]){
				edits.add(new Edit(Edit.DELETE, oldArray[i++]));
			}else{
				edits.add(new Edit(Edit.ADD, newArray[j++]));
			}
		}
		while(i < oldArray.length || j < newArray.length){
			if(i==oldArray.length){
				edits.add(new Edit(Edit.ADD, newArray[j++]));
			}else if(j==newArray.length){
				edits.add(new Edit(Edit.DELETE, oldArray[i++]));
			}
		}
		edits.trimToSize();
		return edits.toArray(new Edit[0]);
	}
	/**
	*比較対象となる新旧のテキストファイルを指定してマネージャを生成します。
	*@param oldFile 古いファイル
	*@param newFile 新しいファイル
	*@param oldset 古いファイルの文字セット
	*@param newset 新しいファイルの文字セット
	*@throws IOException ファイル入力に異常があった場合
	*/
	public static LeafDiffManager createManager
		(File oldFile, File newFile, Charset oldset, Charset newset) throws IOException{
		return new LeafDiffManager(read(oldFile, oldset), read(newFile, newset));
	}
	/**
	*比較対象となる新旧のテキストファイルを指定してマネージャを生成します。
	*@param oldFile 古いファイル
	*@param newFile 新しいファイル
	*@param oldset 古いファイルの文字セット名
	*@param newset 新しいファイルの文字セット名
	*@throws IOException ファイル入力に異常があった場合
	*/
	public static LeafDiffManager createManager
		(File oldFile, File newFile, String oldset, String newset) throws IOException{
		return new LeafDiffManager(
			read(oldFile, Charset.forName(oldset)),
			read(newFile, Charset.forName(newset))
		);
	}
	/**
	*比較元の文字列と比較先のテキストファイルを指定してマネージャを生成します。
	*@param oldText 比較元の古い文字列
	*@param newFile 比較先の新しいファイル
	*@param newset 比較先の文字セット名
	*/
	public static LeafDiffManager createManager
		(String oldText, File newFile, String newset) throws IOException{
		return new LeafDiffManager(
			oldText.split("\r?\n"), read(newFile, Charset.forName(newset))
		);
	}
	/**
	*比較元のテキストファイルとと比較先の文字列を指定してマネージャを生成します。
	*@param oldFile 比較元の古いファイル
	*@param newText 比較先の新しい文字列
	*@param oldset 比較元の文字セット名
	*/
	public static LeafDiffManager createManager
		(File oldFile, String newText, String oldset) throws IOException{
		return new LeafDiffManager(
			read(oldFile, Charset.forName(oldset)), newText.split("\r?\n")
		);
	}
	/**ファイルを読み込む*/
	private static String[] read(File file, Charset charset) throws IOException{
		FileInputStream stream = new FileInputStream(file);
		FileChannel channel = stream.getChannel();
		MappedByteBuffer buffer = channel.map(
			FileChannel.MapMode.READ_ONLY, 0, channel.size()
		);
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer cb = decoder.decode(buffer);
		channel.close();
		stream.close();
		return cb.toString().split("\r?\n");
	}
}
