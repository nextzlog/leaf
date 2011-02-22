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
import java.net.*;
import java.util.*;

import leaf.net.server.*;
import leaf.manager.*;

/**
*このクラスは、Leafのネットワーク通信機能のメンバーを実装します。
*対応するクライアントとの接続開始により自動生成され、
*{@link LeafNetServer}上で管理されます。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*/
public class LeafNetMember implements Runnable,LeafNetMessageListener{
	
	/**秘匿フィールド*/
	private Socket socket;
	private String name = "Guest";
	private String roomname;
	private LeafNetServer server = LeafNetServer.getInstance();
	private ArrayList<LeafNetMessageListener> listeners;
	
	/**
	*メンバーを生成します。
	*@param socket クライアントの通信ソケット
	*/
	public LeafNetMember(Socket socket){
		this.socket = socket;
		listeners = new ArrayList<LeafNetMessageListener>();
		this.addMessageListener(this);
		Thread thread = new Thread(this);
		thread.start();
	}
	/**
	*メンバーのニックネームを設定します。
	*@param name メンバーのニックネーム
	*/
	public synchronized void setName(String name){
		this.name = name;
	}
	/**
	*メンバーのニックネームを返します。
	*@return このメンバーのニックネーム
	*/
	public String getName(){
		return name;
	}
	/**文字列表現を得る*/
	/**
	*このメンバーの文字列化表現を返します。
	*返される文字列は、{@link #getName()}と同様に定義されます。
	*@return メンバーの文字列による表現
	*/
	public String toString(){
		return name;
	}
	/**
	*メンバーをサーバーから削除し、対応するクライアントとの通信を終了します。<br>
	*このメソッドは失敗することがありますが、エラーメッセージをクライアントに
	*送付するだけで、例外をスローしません。
	*/
	public void disconnect(){
		exitRoom();
		try{
			server.removeClient(this);
			listeners.clear();
			socket.close();
		}catch(Exception ex){
			sendMessage("msg failed to disconnect : " + getName() + "\n" + ex + "\n");
		}
	}
	/**
	*対応するクライアントにメッセージを送信します。<br>
	*メッセージはLeafのネットワーク通信プロトコルに従い、
	*「コマンド+メッセージ本文」の形式でなければなりません。
	*@param msg 送信するコマンド付きメッセージ
	*/
	public synchronized void sendMessage(String msg){
		OutputStream stream;
		PrintWriter writer;
		try{
			stream = socket.getOutputStream();
			writer = new PrintWriter(stream);
			writer.println(msg);
			writer.flush();
		}catch(Exception ex){ex.printStackTrace();}
	}
	/**
	*このメンバーにメッセージを送信します。<br>
	*受信したメッセージは登録された全ての
	*{@link LeafNetMessageListener#processMessage(LeafNetMessageEvent)}
	*に送付され、コマンド処理されます。
	*/
	public void messageReceived(String cmd,String value){
		LeafNetMessageEvent e = new LeafNetMessageEvent(this,cmd,value);
		for(int i=0;i<listeners.size();i++){
			listeners.get(i).processMessage(e);
		}
	}
	/**
	*LeafNetMessageListenerを登録します。
	*@param lis 登録するリスナー
	*/
	public synchronized void addMessageListener(LeafNetMessageListener lis){
		listeners.add(lis);
	}
	/**
	*LeafNetMessageListenerを削除します。
	*@param lis 削除するリスナー
	*/
	public synchronized void removeMessageListener(LeafNetMessageListener lis){
		listeners.remove(lis);
	}
	public LeafNetMessageListener[] getMessageListeners(){
		return (LeafNetMessageListener[])listeners.toArray(new LeafNetMessageListener[0]);
	}
	/**
	*このメンバーが受信した[@link LeafNetMessageEvent コマンド付きメッセージ}
	*を処理します。<br>このメソッドは、他のLeafNetMemberによって
	*インタフェース{@link LeafNetMessageListener}を媒介して実行されます。
	*@param e 受信したコマンド付きメッセージのイベント
	*/
	public void processMessage(LeafNetMessageEvent e){
		String cmd   = e.getCommand();
		String value = e.getValue();
		if(cmd.equals("dis"))disconnect();
		else if(cmd.equals("nick"))rename(value);
		else if(cmd.equals("mroom"))addRoom(value);
		else if(cmd.equals("rooms"))returnRooms();
		else if(cmd.equals("ent"))enterRoom(value);
		else if(cmd.equals("out"))exitRoom();
		else if(cmd.equals("members"))returnMembers();
	}
	/**
	*メンバーのニックネームを変更します。<br>
	*{@link #setName(String)}とは異なり、通信を介しての動的なニックネーム
	*変更を受け付ける際に用いられます。一般に、サーバープログラムの誤作動
	*を防ぐには、このメソッドを使用してニックネームを変更すべきです。これ
	*は、プロトコル上、メンバーの配列を半角空白を区切り文字として表現するためです。
	*@param name 新しいニックネーム
	*/
	public void rename(String name){
		if(name.indexOf(" ")>=0){
			sendMessage("msg  halfsize whitespaces cannot be used : " + name);
		}else{
			String old = getName();
			setName(name);
			messageReceived("msg", "renamed " + old + " to " + name);
		}
	}
	/**
	*指定したルームにメンバーを入室させます。
	*@param roomname ルーム名
	*/
	public void enterRoom(String roomname){
		LeafNetRoom room = server.getRoom(roomname);
		if(room!=null){
			if(roomname.equals(this.roomname)){
				sendMessage("msg " + getName() + " has already joined " + roomname);
				return;
			}
			exitRoom();
			room.add(this);
			this.roomname = roomname;
		}else{
			sendMessage("msg not found room " + roomname + ".");
		}
	}
	/**
	*現在入室中のルームからメンバーを退室させます。
	*/
	public void exitRoom(){
		LeafNetRoom room = server.getRoom(roomname);
		if(room!=null){
			room.remove(this);
		}
	}
	/**
	*このメンバーをホストとする新しいルームを追加します。<br>
	*プロトコル上、一人のメンバーは必ず最大１つのルームに入室しなければなりません。
	*従って、このメソッドの実行と同時にメンバーは入室中のルームから強制的に削除されます。
	*/
	public void addRoom(String roomname){
		if(roomname.indexOf(" ")>=0){
			sendMessage("msg a half size whitespace cannot be used : " + roomname);
		}else{
			exitRoom();
			this.roomname = roomname;
			sendMessage("msg " + getName() + " made a room " + roomname + ".");
			LeafNetRoom room = new LeafNetRoom(roomname,this);
			server.addRoom(room);
		}
	}
	/**
	*同室のメンバーの一覧をクライアントに送信します。
	*/
	public void returnMembers(){
		LeafNetRoom room = server.getRoom(roomname);
		if(room!=null){
			sendMessage("members " + LeafArrayManager.getStringFromArray(" ",room.getClients()));
		}
	}
	/**
	*{@link LeafNetServer}上に存在する全てのルームの一覧をクライアントに送信します。
	*/
	public void returnRooms(){
		sendMessage("rooms " + LeafArrayManager.getStringFromArray(" ",server.getRooms()));
	}
	/**
	*対応するクライアントからのメッセージを受信するスレッドです。
	*/
	public void run(){
		InputStream stream;
		BufferedReader reader;
		try{
			stream = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(stream));
			while(!socket.isClosed()){
				String line = reader.readLine();
				String[] arr = line.split(" ",2);
				try{
					messageReceived(arr[0],(arr.length>=2)?arr[1]:"");
				}catch(Exception ex){ex.printStackTrace();}
			}
		}catch(Exception ex){ex.printStackTrace();}
	}
}
