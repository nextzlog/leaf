/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
*ダウンローダーの実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.3 作成：2011年5月22日
*/
public class LeafDownloadManager extends java.util.Observable{
	private final URL url;
	private final File file;
	private boolean isLoading = false;
	private int loadedsize = 0, datasize = -1;
	private int timeout = 5000, readout = 120000;
	private static final int BUFFER_SIZE = 64 * 1024;
	/**
	*URLと保存先ファイルを指定してマネージャを生成します。
	*@param url ダウンロード元
	*@param file 保存先ファイル
	*/
	public LeafDownloadManager(URL url, File file){
		this.url  = url;
		this.file = file;
	}
	/**
	*接続のタイムアウト時間をミリ秒で設定します。
	*@param timeout タイムアウト時間 0以下の場合無限
	*/
	public void setConnectionTimeout(int timeout){
		this.timeout = (timeout>0)? timeout : 0;
	}
	/**
	*接続のタイムアウト時間をミリ秒で返します。
	*@return タイムアウト時間
	*/
	public int getConnectionTimeout(){
		return timeout;
	}
	/**
	*読み込みのタイムアウト時間をミリ秒で設定します。
	*@param timeout タイムアウト時間 0以下の場合無限
	*/
	public void setReadTimeout(int timeout){
		this.readout = (timeout>0)? timeout : 0;
	}
	/**
	*読み込みのタイムアウト時間をミリ秒で返します。
	*@return タイムアウト時間
	*/
	public int getReadTimeout(){
		return readout;
	}
	/**
	*接続先のURLを返します。
	*@return 接続先
	*/
	public URL getURL(){
		return url;
	}
	/**
	*保存先のファイルを返します。
	*@return 保存先
	*/
	public File getSaveFile(){
		return file;
	}
	/**
	*読み込むデータのサイズを返します。
	*@return データサイズ 未接続の場合-1
	*/
	public int getDataSize(){
		return datasize;
	}
	/**
	*ダウンロードの進捗率を返します。
	*@return 百分率で表される進捗状況
	*/
	public int getProgress(){
		return (datasize>0)? 
		100 * loadedsize / datasize : 0;
	}
	/**
	*ダウンロードが完了しているか返します。
	*@return 完了している場合true
	*/
	public boolean isCompleted(){
		return (datasize>0)?
		loadedsize == datasize : false;
	}
	/**
	*ダウンロード作業中かどうか返します。
	*@return 作業中の場合true
	*/
	public boolean isDownloading(){
		return isLoading;
	}
	/**
	*オブザーバーに状態を通知します。
	*/
	private void stateChanged(){
		setChanged();
		notifyObservers();
	}
	/**
	*例外を生成します。
	*@param msg メッセージ
	*@return 生成された例外
	*/
	private IOException error(String msg){
		return new IOException(msg);
	}
	/**
	*ダウンロードを実行もしくは再開します。
	*@throws IOException 接続と読み込みに失敗した場合
	*/
	public void download() throws IOException{
		RandomAccessFile file = null;
		InputStream stream = null;
		try{
			HttpURLConnection conn
			= (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(readout);
			conn.setRequestProperty("Range",
				String.format("bytes=%d-", loadedsize));
			conn.connect();
			//レスポンスステータスが200-299でない場合
			int res = conn.getResponseCode();
			if(res / 100 != 2)
				throw error("Connect Error:"+res);
			int length = conn.getContentLength();
			if(length <= 0)
				throw error("Illegal Content Length"+length);
			//ダウンロード初回開始時
			if(datasize == -1){
				datasize = length;
				stateChanged();
			}
			stream = conn.getInputStream();
			file = new RandomAccessFile(this.file, "rw");
			file.seek(loadedsize);
			isLoading = true;
			while(isLoading){
				int remain = datasize - loadedsize;
				byte[] buffer = new byte[
					Math.min(BUFFER_SIZE,remain)];
				int readed = stream.read(buffer);
				if( readed == -1 ) break;
				file.write(buffer, 0, readed);
				loadedsize += readed;
				buffer = null;
				stateChanged();
			}
			isLoading = false;
			stateChanged();
		}finally{
			try{
				if(file != null) file.close();
			}finally{
				if(stream != null) stream.close();
			}
		}
	}
	/**
	*ダウンロードを中断します。
	*/
	public void stop(){
		isLoading = false;
		stateChanged();
	}
}
