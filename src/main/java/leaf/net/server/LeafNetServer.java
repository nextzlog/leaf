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

import java.io.*;
import java.util.*;
import java.net.*;

import leaf.net.server.*;
import leaf.manager.*;

/**
*Leafのネットワーク通信機能を提供するサーバーの実装です。
*シングルトン設計により単一インスタンスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月23日
*@see LeafNetMember
*@see LeafNetRoom
*/
public class LeafNetServer{
	
	/**秘匿フィールド*/
	private static LeafNetServer server;
	private ServerSocket socket;
	private int port = 5555;
	private ArrayList<LeafNetRoom> rooms;
	private ArrayList<LeafNetMember> members;
	
	/**秘匿コンストラクタ*/
	private LeafNetServer(){
		rooms = new ArrayList<LeafNetRoom>();
		members = new ArrayList<LeafNetMember>();
	}
	/**
	*LeafNetServerのインスタンスを返します。
	*@return LeafNetServerの単一インスタンス
	*/
	public static LeafNetServer getInstance(){
		if(server == null){
			server = new LeafNetServer();
		}
		return server;
	}
	/**
	*LeafNetServerの通信を開始します。<br>
	*クライアントとの接続は、このメソッドの実行中に受け付けます。
	*このメソッドはサーバーの待機中常時実行され続けます。<br>
	*GUIアプリケーションの場合、別途スレッドを立てる必要があります。
	*@param port 接続を受け付けるポート番号
	*/
	public void start(int port){
		System.out.println("LeafNet Server : PORT "+port);
		try{
			socket = new ServerSocket(this.port = port);
			while(!socket.isClosed()){
				Socket client = socket.accept();
				LeafNetMember member = new LeafNetMember(client);
				addClient(member);
			}
		}catch(Exception ex){ex.printStackTrace();
			System.out.println("Connection error occurred.");
		}
	}
	/**
	*接続を受け付けているポート番号を返します。
	*@return ポート番号
	*/
	public int getPortNumber(){
		return port;
	}
	/**
	*新しいルームをサーバー内に追加します。<br>
	*同時にサーバー上の全てのメンバーにルームの一覧情報が送信されます。
	*@param room 追加する{@link LeafNetRoom}
	*/
	public synchronized void addRoom(LeafNetRoom room){
		if(rooms.contains(room))return;
		rooms.add(room);
		System.out.println("New Room \""+room+"\" is added.");
		/*通知*/
		for(LeafNetMember member:members){
			member.sendMessage("rooms " + LeafArrayManager.getStringFromList(" ",rooms));
		}
	}
	/**
	*指定された名前のルームを返します。<br>
	*返すべきルームが存在しない場合、nullを返します。
	*@param name ルーム名
	*@return 指定した{@link LeafNetRoom}
	*/
	public LeafNetRoom getRoom(String name){
		for(LeafNetRoom room:rooms){
			if(room.getName().equals(name))return room;
		}return null;
	}
	/**
	*サーバー上に存在する全てのルームの配列を返します。
	*@return 全ての{@link LeafNetRoom}の配列
	*/
	public LeafNetRoom[] getRooms(){
		return (LeafNetRoom[]) rooms.toArray(new LeafNetRoom[0]);
	}
	/**
	*指定されたルームを削除します。
	*@param room 削除する{@link LeafNetRoom}
	*/
	public synchronized void removeRoom(LeafNetRoom room){
		rooms.remove(room);
		System.out.println("Room \""+room+"\" is removed.");
		/*通知*/
		for(LeafNetMember member:members){
			member.sendMessage("rooms " + ((rooms.size()>0)?LeafArrayManager.getStringFromList(" ",rooms):""));
		}
	}
	/**
	*このサーバー上に存在する全てのルームを削除します。
	*/
	public synchronized void clearRooms(){
		rooms.clear();
		for(LeafNetMember member:members){//通知
			member.sendMessage("msg  All rooms are removed from the server.");
			member.sendMessage("rooms");
		}
		System.out.println("All Rooms are removed.");
	}
	/**指定されたメンバーをこのサーバー上に追加*/
	private synchronized void addClient(LeafNetMember member){
		if(members.contains(member)){
			member.sendMessage("msg Name " + member + "has already been used.");
			return;
		}
		members.add(member);
		System.out.println("New Client joined.");
	}
	/**
	*指定されたメンバーを返します。<br>
	*メンバーが存在しない場合、nullを返します。
	*@param name メンバー名
	*@return 指定した{@link LeafNetMember}
	*/
	public LeafNetMember getClient(String name){
		for(LeafNetMember member:members){
			if(member.getName().equals(name))return member;
		}return null;
	}
	/**
	*このサーバー上に存在する全てのメンバーの配列を返します。
	*@return 全てのメンバーの配列
	*/
	public LeafNetMember[] getClients(){
		return (LeafNetMember[])members.toArray(new LeafNetMember[0]);
	}
	/**
	*指定されたメンバーをサーバーから削除します。
	*@param member 削除する{@link LeafNetMember}
	*/
	public synchronized void removeClient(LeafNetMember member){
		members.remove(member);
		System.out.println("Client \""+member+"\" is removed");
		for(LeafNetRoom room:rooms){
			if(room.contains(member))room.remove(member);
		}
	}
	/**
	*このサーバー上に存在する全てのメンバーを削除します。<br>
	*サーバーは全てのクライアントに削除を通知した上でメンバーを削除します。
	*/
	public synchronized void clearClients(){
		for(LeafNetMember member:members){
			member.sendMessage("msg  All clients are removed from the server.");
		}
		members.clear();
		System.out.println("All Clients are removed.");
	}
	/**
	*サーバー上の全てのメンバーを削除し、サーバーの通信を終了します。
	**/
	public void disconnect() throws IOException{
		clearClients();
		socket.close();
		System.out.println("Server Disconnected Successfully");
	}
}