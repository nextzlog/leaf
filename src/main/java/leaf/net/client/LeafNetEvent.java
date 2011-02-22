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
package leaf.net.client;

import java.util.*;

import leaf.net.client.*;

/**
*Leafのネットワーク通信機能のクライアント側のメッセージ受信イベントです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年6月5日
*/
public class LeafNetEvent extends EventObject{
	
	/**秘匿フィールド*/
	private String command;
	private String message;
	
	/**コンストラクタ*/
	/**
	*メッセージ受信イベントを作成します。
	*@param client クライアント本体のインスタンス
	*@param command メッセージコマンド
	*@param message メッセージの本文（引数）
	*/
	public LeafNetEvent(LeafNetClient client,String command,String message){
		super(client);
		this.command = command;
		this.message = message;
	}
	/**
	*メッセージに含まれるコマンドを返します。
	*@return {@link LeafNetListener}の実行すべきコマンド
	*/
	public String getCommand(){
		return command;
	}
	/**
	*メッセージに含まれる本文（引数）を返します。
	*@return [@link LeafNetListener}に渡されるメッセージ本文
	*/
	public String getMessage(){
		return message;
	}
}