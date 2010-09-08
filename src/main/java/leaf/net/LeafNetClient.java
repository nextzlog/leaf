/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.net.client;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import leaf.net.client.*;
import leaf.manager.*;

/**
*Leafのネットワーク通信機能のクライアント側本体です。<br>
*シングルトン設計により単一インスタンスです。<br>
*通信の仕様上、クライアントはサーバに接続する前にニックネームを設定しておく必要があります。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年6月5日
*/
public class LeafNetClient implements Runnable{
	
	/**秘匿フィールド*/
	private static LeafNetClient client;
	private Socket socket = null;
	private String nickname = "Default";
	private boolean isOnLine = false;
	private ArrayList<String> rooms,members;
	private ArrayList<LeafNetListener> listeners;
	private Thread thread;
	
	/**秘匿コンストラクタ*/
	private LeafNetClient(){
		rooms = new ArrayList<String>(5);
		members= new ArrayList<String>(10);
		listeners = new ArrayList<LeafNetListener>(1);
	}
	/**
	*このクラスのインスタンスを返します。
	*@return LeafNetClientのインスタンス
	*/
	public static LeafNetClient getInstance(){
		if(client == null){
			client = new LeafNetClient();
		}
		return client;
	}
	/**
	*{@link LeafNetListener}を登録します。
	*@param listener 登録するLeafNetListener
	*/
	public void addListener(LeafNetListener listener){
		listeners.add(listener);
	}
	/**
	*指定された{@link LeafNetListener}を削除します。
	*@param listener 削除するLeafNetListener
	*/
	public void removeListener(LeafNetListener listener){
		listeners.remove(listener);
	}
	/**
	*指定されたIPアドレスとポート番号でサーバーとの接続を開始します。
	*@param address サーバーのIPアドレス
	*@param port 通信するポート番号
	*/
	public void connect(InetAddress address,int port){
		this.isOnLine = true;
		thread = new Thread(this);
		try{
			socket = new Socket(address,port);
			LeafNetEvent e = new LeafNetEvent(this,
				"connected","connect to server : "+nickname);
			for(LeafNetListener listener:listeners){
				listener.messageReceived(e);
			}
			send("nick " + nickname);
			thread.start();
		}catch(Exception ex){
			this.isOnLine = false;
			LeafNetEvent e = new LeafNetEvent(this,
				"error","connect error : " + nickname + "\n>>" + ex.toString());
			for(LeafNetListener listener:listeners){
				listener.errorOccurred(e);
			}
		}
	}
	/**
	*指定されたサーバー名とポート番号でサーバーとの接続を開始します。
	*@param host サーバー名
	*@param port 通信するポート番号
	*@throws UnknownHostException サーバーのIPアドレスが判定できなかった場合にスローされます。
	*@throws SecurityException セキュリティ違反があった場合にセキュリティマネージャによってスローされます。
	*/
	public void connect(String host,int port) throws UnknownHostException,SecurityException{
		try{
			connect(InetAddress.getByName(host),port);
		}catch(UnknownHostException ex){
			throw ex;
		}catch(SecurityException ex){
			throw ex;
		}
	}
	/**
	*サーバーとの接続を強制終了します。
	*切断に失敗した場合、全ての{@link LeafNetListener}が呼び出されます。
	*/
	public void disconnect(){
		this.isOnLine = false;
		try{
			send("dis");
			socket.close();
		}catch(Exception ex){
			LeafNetEvent e = new LeafNetEvent(this,
				"error","disconnect error : " + nickname + "\n>>" + ex.toString());
			for(LeafNetListener listener:listeners){
				listener.errorOccurred(e);
			}
		}
	}
	/**
	*接続先のサーバ名を返します。
	*@return サーバ名
	*/
	public String getServerName(){
		return socket.getInetAddress().getHostAddress();
	}
	/**
	*接続先のサーバのIPアドレスを返します。
	*@return IPアドレス
	*/
	public InetAddress getServerAddress(){
		return socket.getInetAddress();
	}
	/**
	*通信に使用するポート番号を返します。
	*@return ポート番号
	*/
	public int getPortNumber(){
		return socket.getPort();
	}
	/**
	*クライアントが使用するニックネームを設定します。<br>
	*@param name ニックネーム
	*/
	public void setNickName(String name){
		this.nickname = name;
		if(isOnLine){
			send("nick " + nickname);
		}
	}
	/**
	*クライアントが使用しているニックネームを返します。
	*@return ニックネーム
	*/
	public String getNickName(){
		return nickname;
	}
	/**
	*サーバとの接続状況を返します。
	*@return サーバに接続している場合true
	*/
	public boolean isOnLine(){
		return isOnLine;
	}
	/**
	*サーバ上に存在するルームの一覧を返します。
	*@return ルームの一覧を表すArrayList<String>
	*/
	public synchronized ArrayList<String> getRooms(){
		return rooms;
	}
	/**
	*同室のメンバーの一覧を返します。
	*@return メンバーの一覧を表すArrayList<STring>
	*/
	public synchronized ArrayList<String> getMembers(){
		return members;
	}
	/**
	*サーバにメッセージを送信します。
	*@param msg 送信するメッセージ
	*/
	public synchronized void send(String msg){
		OutputStream stream= null;
		PrintWriter writer = null;
		try{
			stream = socket.getOutputStream();
			writer = new PrintWriter(stream);
			writer.println(msg);
			writer.flush();
		}catch(Exception ex){
			LeafNetEvent e = new LeafNetEvent(this,
				"error","message transmission error : " + nickname + "\n>>" + ex.toString());
			for(LeafNetListener listener:listeners){
				listener.errorOccurred(e);
			}
		}
	}
	/**メッセージコマンド処理*/
	private void processMessageCommand(LeafNetEvent e){
		
		if(e.getCommand().equals("rooms")){
			this.rooms = LeafArrayManager.getListFromString(" ",e.getMessage());
		}else if(e.getCommand().equals("members")){
			this.members = LeafArrayManager.getListFromString(" ",e.getMessage());
		}
		for(LeafNetListener listener:listeners){
			listener.messageReceived(e);
		}
	}
	/**受信用のスレッドです。*/
	public void run(){
		InputStream stream;
		BufferedReader reader;
		try{
			stream = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(stream));
			while(!socket.isClosed()){
				String line = reader.readLine();
				String[] arr = line.split(" ",2);
				LeafNetEvent e = new LeafNetEvent(this,arr[0],(arr.length>=2)?arr[1]:"");
				processMessageCommand(e);
			}
		}catch(Exception ex){
			LeafNetEvent e = new LeafNetEvent(this,
				"error","Client processing error : " + nickname + "\n>>" + ex.toString());
			for(LeafNetListener listener:listeners){
				listener.errorOccurred(e);
			}
		}finally{
			LeafNetEvent e = new LeafNetEvent(this,
				"lethal","Client receiver loop downed : " + nickname);
			for(LeafNetListener listener:listeners){
				listener.errorOccurred(e);
			}
			isOnLine = false;
		}
	}
}