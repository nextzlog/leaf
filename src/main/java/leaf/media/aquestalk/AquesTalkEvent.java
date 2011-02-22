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
package leaf.media.aquestalk;

import java.io.File;
import java.util.EventObject;

/**
*{@link AquesTalkManager}の動作を通知するイベントです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年10月3日
*/

public class AquesTalkEvent extends EventObject {
	
	private final File file;
	private final int speed;
	
	/**
	*イベントが発生したAquesTalkManagerとAquesTalkManager
	*の設定Phontファイル、読み上げ速度を指定してイベントを生成します。
	*@param manager イベント発生源
	*@param file Phontファイル
	*@param speed 読み上げ速度
	*/
	public AquesTalkEvent(AquesTalkManager manager, File file, int speed){
		super(manager);
		this.file  = file;
		this.speed = speed;
	}
	/**
	*Phontファイルを返します。
	*@return Phontファイル
	*/
	public File getPhontFile(){
		return file;
	}
	/**
	*読み上げ速度を返します。
	*@return 読み上げ速度
	*/
	public int getSpeed(){
		return speed;
	}
}
