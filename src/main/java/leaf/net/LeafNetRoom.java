/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.net.server;

import java.io.*;
import java.util.*;
import java.net.*;

import leaf.net.server.*;
import leaf.manager.*;

/**
*Leafのネットワーク通信機能におけるルームを実装します。<br>
*プロトコル上、全てのメンバーは必ず最大１つのルームに所属します。<br>
*ルームの作成者は自動的にこのルームのホストとなります。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月23日
*@see LeafNetServer
*@see LeafNetMember
*/
public class LeafNetRoom implements LeafNetMessageListener{
	
	/**秘匿フィールド*/
	private String roomname;
	private LeafNetMember hostclient;
	private ArrayList<LeafNetMember> members;
	
	/**
	*ルーム名とホストを指定してルームを生成します。
	*@param roomname ルーム名
	*@param hostclient ホスト
	*/
	public LeafNetRoom(String roomname,LeafNetMember hostclient){
		this.roomname = roomname;
		members = new ArrayList<LeafNetMember>();
		setHost(hostclient);
	}
	/**
	*このルームの名前を返します。
	*@return ルーム名
	*/
	public String getName(){
		return roomname;
	}
	/**
	*このルームの文字列化表現を返します。
	*返される文字列は、{@link #getName()}と同様に定義されます。
	*@return ルームの文字列による表現
	*/
	public String toString(){
		return roomname;
	}
	/**
	*このルームのホストを設定します。
	*@param host 新しいホスト
	*/
	public synchronized void setHost(LeafNetMember host){
		this.hostclient = host;
		host.addMessageListener(this);
		members.add(host);
		host.sendMessage("members " + host);
		host.sendMessage("msg " + host + " joined " + getName() + ".");
	}
	/**
	*このルームのホストを返します。
	*@return ホスト
	*/
	public LeafNetMember getHost(){
		return hostclient;
	}
	/**
	*このルームにメンバーを追加します。
	*すでに登録されているメンバーの場合、エラーメッセージがクライアントに送信されます。
	*@param member 追加するメンバー
	*/
	public synchronized void add(LeafNetMember member){
		if(members.contains(member)){
			member.sendMessage("msg " + member + " has already joined " + getName() + ".");
			return;
		}
		member.addMessageListener(this);
		members.add(member);
		for(LeafNetMember mb:members){
			mb.sendMessage("members " + LeafArrayManager.getStringFromList(" ",getClientList()));
			mb.sendMessage("msg " + member + " joined " + getName() + ".");
		}
	}
	/**
	*指定されたメンバーをこのルーム内で検索します。
	*@param member 検索するメンバー
	*@return 存在する場合true
	*/
	public boolean contains(LeafNetMember member){
		return members.contains(member);
	}
	/**
	*このルームに存在するメンバーの配列を返します。
	*return メンバーの配列を表すArrayList
	*/
	public ArrayList<LeafNetMember> getClientList(){
		return members;
	}
	/**
	*このルームに存在するメンバーの配列を返します。
	*return メンバーの配列
	*/
	public LeafNetMember[] getClients(){
		return (LeafNetMember[]) members.toArray(new LeafNetMember[0]);
	}
	/**
	*指定されたメンバーをこのルームから削除します。
	*@param member 削除するメンバー
	*/
	public synchronized void remove(LeafNetMember member){
		member.removeMessageListener(this);
		members.remove(member);
		for(LeafNetMember mb:members){
			mb.sendMessage("members");
			mb.sendMessage("msg " + member.getName() + " out.");
		}
		/**空のチャットルームを削除*/
		if(members.size()==0)
			LeafNetServer.getInstance().removeRoom(this);
	}
	/**
	*メンバーから受信したコマンド付きメッセージを処理します。
	*@param e 受信したメッセージイベント
	*/
	public void processMessage(LeafNetMessageEvent e){
		LeafNetMember source = e.getSource();
		/*発言*/
		if(e.getCommand().equals("msg")){
			for(LeafNetMember member:members){
				member.sendMessage("msg (" + source.getName() + ") " + e.getValue());
			}
		}
		/*ニックネームの変更*/
		else if(e.getCommand().equals("nick")){
			for(LeafNetMember member:members){
				member.sendMessage("members");
			}
		}
		/**その他の通信*/
		else{
			for(LeafNetMember member: members){
				member.sendMessage(e.getCommand() + " " + e.getValue());
			}
		}
	}
}
