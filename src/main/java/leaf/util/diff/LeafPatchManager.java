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

import java.util.ArrayList;

/**
*新旧のテキスト配列間の差分を利用して新しいテキスト配列を生成するマネージャです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月20日
*@see LeafDiffManager
*/
public class LeafPatchManager{
	
	private final String[] oldArray;
	
	/**
	*新しい配列を生成する基となる配列を指定してマネージャを生成します。
	*@param oldArray 古い配列
	*/
	public LeafPatchManager(Object[] oldArray){
		if(oldArray instanceof String[]){
			this.oldArray = (String[]) oldArray;
		}else{
			this.oldArray = new String[oldArray.length];
			for(int i=0;i<oldArray.length;i++){
				this.oldArray[i] = oldArray[i].toString();
			}
		}
	}
	/**
	*差分となる編集内容の配列を指定してテキスト配列を生成します。
	*@param edits 差分の編集内容
	*@return 新しい文字列配列
	*/
	public String[] patch(Edit[] edits){
		ArrayList<String> newList = new ArrayList<String>(oldArray.length);
		for(int i=0;i<edits.length;i++){
			if(edits[i].getType() == Edit.DELETE){
				newList.remove(edits[i].getContent());
			}else{
				newList.add(edits[i].getContent());
			}
		}
		newList.trimToSize();
		return newList.toArray(new String[0]);
	}
}
