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

import java.util.EventListener;

/**
*{@link AquesTalkManager}からイベントを受信するためのリスナーです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年10月3日
*/

public interface AquesTalkListener extends EventListener{
	
	/**
	*AquesTalkManagerが発声を開始するときに呼び出されます。
	*@param e 発声開始イベント
	*/
	public void startingToSpeak(AquesTalkEvent e);
	
	/**
	*AquesTalkManagerが発声を終了したときに呼び出されます。
	*@param e 発声終了イベント
	*/
	public void speakEnded(AquesTalkEvent e);
	
	/**
	*AquesTalkManagerがPhontデータをロードしたときに呼び出されます。
	*@param e ロード完了イベント
	*/
	public void phontDataLoaded(AquesTalkEvent e);
}