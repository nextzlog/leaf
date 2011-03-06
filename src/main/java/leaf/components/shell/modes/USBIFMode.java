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
package leaf.components.shell.modes;

import java.io.File;
import java.io.IOException;

import leaf.components.shell.*;
import leaf.manager.LeafLangManager;
import leaf.media.usbif4cw.ByteArrayStream;

/**
*USBIF4CWを制御するモードです
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2010年12月27日
*@see LeafShellPane
*/
public class USBIFMode implements Mode{
	
	private LeafShellDocument doc;
	private ByteArrayStream stream;
	
	/**
	*モードを生成します。
	*/
	public USBIFMode(){
		try{
			stream = new ByteArrayStream();
		}catch(IOException ex){}
	}
	
	/**
	*モード名を返します。
	*@return モードを表す文字列
	*/
	public String getName(){
		return "usbif";
	}
	
	/**
	*このモードに移行します。
	*@param doc 出力先ドキュメント
	*@param dir 作業ディレクトリ
	*/
	public void activate(LeafShellDocument doc, File dir){
		this.doc = doc;
	}
	
	/**
	*実装されていません。
	*@param dir 作業ディレクトリ
	*/
	public void setDirectory(File dir){}
	
	/**
	*コマンドを実行します。
	*@param cmd シェルから渡されるコマンド
	*/
	public void execute(String cmd){
		
		String[] cmds = cmd.split("\\s",2);
		if(cmds.length >= 2){
			if(cmds[0].equals("send")){
				send(cmds[1]);
			}else if(cmds[0].equals("wpm")){
				wpm(cmds[1]);
			}
		}else{
			if(cmd.equals("id")){
				id();
			}else if(cmd.equals("version")){
				version();
			}else if(cmd.equals("close")){
				close();
			}else if(cmd.equals("connect")){
				connect();
			}else if(cmd.equals("wpm")){
				wpm(null);
			}else{
				send(cmd);
			}
		}
	}
	
	/**
	*USBIF4CWを検索して接続します。
	*/
	private void connect(){
		try{
			close();
			stream = new ByteArrayStream();
			doc.appendln(" => " + stream.getID());
		}catch(IOException ex){
			doc.errorln(LeafLangManager.get(
				" => Failed to connect...", " => 接続に失敗しました"
			));
		}
	}
	
	/**
	*USBIF4CWのIDを表示します。
	*/
	private void id(){
		if(stream != null){
			doc.appendln(" => " + stream.getID());
		}else{
			doc.errorln(LeafLangManager.get(
				" => No Connection", " => 接続されていません"
			));
		}
	}
	
	/**
	*USBIF4CWのバージョンを表示します。
	*/
	private void version() throws NullPointerException{
		if(stream != null){
			doc.appendln(" => "+ stream.getVersion());
		}else{
			doc.errorln(LeafLangManager.get(
				" => No Connection", " => 接続されていません"
			));
		}
	}
	
	/**
	*メッセージを送信します。
	*@param msg メッセージ
	*/
	private void send(String msg){
		try{
			stream.write(msg.getBytes());
			stream.flush();
			doc.appendln(" => " + msg);
		}catch(NullPointerException ex){
			doc.errorln(LeafLangManager.get(
				" => No Connection", " => 接続されていません"
			));
		}catch(IOException ex){
			doc.errorln(LeafLangManager.get(
				" => Failed to send...", " => 送信に失敗しました"
			));
		}
	}
	
	/**
	*USBIF4CWとの通信を終了します。
	*/
	private void close(){
		if(stream != null){
			stream.close();
			stream = null;
		}
	}
	
	/**
	*USBIF4CWの打鍵速度を表示または設定します。
	*@param wpm 設定する値
	*/
	private void wpm(String wpm){
		if(stream == null){
			doc.errorln(LeafLangManager.get(
				" => No Connection", " => 接続されていません"
			));
		}else{
			try{
				if(wpm != null){
					stream.setWPM(Integer.parseInt(wpm));
					doc.appendln(" => " + wpm + "\n");
				}else{
					doc.appendln(" => " + stream.getWPM());
				}
			}catch(NumberFormatException ex){
				doc.errorln(LeafLangManager.translate(
					" => Illegal Number Format : [arg]",
					" => 数字が不正です : [arg]", wpm
				));
			}catch(IOException ex){
				doc.errorln(LeafLangManager.get(
					" => Failed in settings...", " => 設定に失敗しました"
				));
			}
		}
	}
	
	/**
	*モードを終了します。
	*/
	public void exit(){}
	
	/**
	*モードの文字列表現を返します。
	*/
	public String toString(){
		return getName();
	}
	
	/**
	*ヘルプとしてメッセージを表示します。
	*@param cmd ヘルプ対象のコマンド
	*/
	public void showHelp(String cmd){
		if(cmd.equals("close")){
			doc.appendln(LeafLangManager.get(
				"close   : close the port",
				"close   : ポートを閉じます"
			));
		}else if(cmd.equals("connect")){
			doc.appendln(LeafLangManager.get(
				"connect : connect to USBIF",
				"connect : USBIFに接続します"
			));
		}else if(cmd.equals("id")){
			doc.appendln(LeafLangManager.get(
				"id      : show the ID of USBIF",
				"id      : USBIFのIDを表示します"
			));
		}else if(cmd.equals("send")){
			doc.appendln(LeafLangManager.get(
				"send    : send message by USBIF",
				"send    : USBIFでメッセージを送信します"
			));
		}else if(cmd.equals("version")){
			doc.appendln(LeafLangManager.get(
				"version : show version of USBIF",
				"version : USBIFのバージョンを表示します"
			));
		}else if(cmd.equals("wpm")){
			doc.appendln(LeafLangManager.get(
				"wpm     : set WPM of USBIF",
				"wpm     : USBIFの打鍵速度を設定します"
			));
		}else{
			doc.appendln(LeafLangManager.get(
				"available commands : close connect id send version wpm",
				"利用できるコマンド : close connect id send version wpm"
			));
		}
	}
}