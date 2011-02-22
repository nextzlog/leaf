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
package leaf.net.server;

import java.util.*;

import leaf.net.server.*;

/**
*Leafのネットワーク通信機能において、サーバー上のメンバーの
*間でやり取りされるコマンド付きメッセージのイベントです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月23日
*@see LeafNetMessageListener
*/
public class LeafNetMessageEvent extends EventObject{

	/**秘匿フィールド*/
	private LeafNetMember source;
	private String command;
	private String value;
	
	/**
	*メッセージイベントを生成します。
	*@param source このメッセージの送信元メンバー
	*@param command メッセージのコマンド
	*@param value メッセージの本文（引数）
	*/
	public LeafNetMessageEvent(LeafNetMember source,String command,String value){
		super(source);
		this.source = source;
		this.command = command;
		this.value = value;
	}
	/**
	*メッセージイベントの送信者を返します。
	*@return 送信者
	*/
	public LeafNetMember getSource(){
		return source;
	}
	/**
	*メッセージに付与されたコマンドを返します。
	*@return コマンドを表す文字列
	*/
	public String getCommand(){
		return command;
	}
	/**
	*このメッセージの本文（引数）を返します。
	*@return 本文
	*/
	public String getValue(){
		return value;
	}
}
