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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
*AquesTalk2をアプリケーションから簡単に利用するためのマネージャです。<br>
*AquesTalk2を制御するには以下のパラメータが必要です。<br>
*<ul>
*<li>Phontファイル…声のもととなるデータです
*<li>読み上げ速度…標準速度に対する100分比率で50以上300以下の整数
*</ul><br>
*<b>AquesTalk2は環境に合わせて別途ダウンロードしてください。</b><br>
*必要なDLL : AquesTalk2Da
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年10月1日
*/
public class AquesTalkManager{
	
	private final File file;
	private int speed;
	private byte[] data;
	
	private static final AquesTalk aques = AquesTalk.INSTANCE;
	
	private final ArrayList<AquesTalkListener> listeners;
	
	/**
	*デフォルトのPhontファイルを指定して(デフォ子ではない)マネージャを生成します。
	*/
	public AquesTalkManager(){
		this(null, 100);
	}
	/**
	*デフォルトで使用するPhontデータファイルを指定してマネージャを生成します。
	*ファイルがnullか読み取り不可能な場合自動でデフォルト音声が割り当てられます。
	*@param file Phontファイル
	*/
	public AquesTalkManager(File file){
		this(file, 100);
	}
	/**
	*デフォルトで使用するPhontファイルと読み上げ速度を指定してマネージャを生成します。
	*ファイルがnullか読み取り不可能な場合自動でデフォルト音声が割り当てられます。
	*@param file Phontファイル
	*@param speed 読み上げスピード
	*/
	public AquesTalkManager(File file, int speed){
		this.file = file;
		this.speed = speed;
		try{
			load(file);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		listeners = new ArrayList<AquesTalkListener>(5);
	}
	/**
	*指定されたテキストを読み上げます。読み上げが完了するまでこのメソッドはロックされます。
	*@param text 読み上げるテキスト
	*@throws IOException AquesTalk2側で出力エラーがあった場合
	*/
	public void speak(String text) throws IOException {
		byte[] str = (text + "\0").getBytes("SJIS");
		AquesTalkEvent e = new AquesTalkEvent(this,file,speed);
		for(AquesTalkListener lis : listeners){
			lis.startingToSpeak(e);
		}
		if(aques.AquesTalk2Da_PlaySync(str, speed, data) != 0){
			throw new IOException("Failed to output");
		}
		for(AquesTalkListener lis : listeners){
			lis.startingToSpeak(e);
		}
	}
	/**
	*指定されたテキストを指定された速度で読み上げます。
	*@param text 読み上げるテキスト
	*@param speed 読み上げスピード
	*@throws IOException AquesTalk2側で出力エラーがあった場合
	*/
	public void speak(String text, int speed) throws IOException {
		this.speed = speed;
		speak(text);
	}
	/**
	*テキストを読み上げる速度を設定します。
	*@param speed 読み上げスピード
	*/
	public void setSpeed(int speed){
		this.speed = speed;
	}
	/**
	*テキストを読み上げるスピードを返します。
	*@return 読み上げスピード
	*/
	public int getSpeed(){
		return speed;
	}
	/**
	*現在使用されているPhontファイルを返します。
	*@return Phontファイル
	*/
	public File getPhontFile(){
		return file;
	}
	/**
	*新しいPhontファイルを読み込みます。
	*@param file 読みこむファイル
	*@throws IOException 読み込みに失敗した場合
	*/
	public void load(File file) throws IOException{
		if(file==null){
			data = null;
			return;
		}
		ArrayList<Byte> list = new ArrayList<Byte>(10000);
		BufferedInputStream stream = null;
		try{
			stream = new BufferedInputStream(
				new FileInputStream(file)
			);
			int count;
			
			while((count = stream.available()) > 0){
				byte[] arr = new byte[count];
				stream.read(arr);
				for(int i=0;i<arr.length;i++){
					list.add(arr[i]);
				}
			}
		}finally{
			if(stream != null) stream.close();
		}
		list.trimToSize();
		byte[] arr = new byte[list.size()];
		for(int i=0;i<arr.length;i++){
			arr[i] = list.get(i);
		}
		list = null;
		data = arr;
		AquesTalkEvent e = new AquesTalkEvent(this,file,speed);
		for(AquesTalkListener lis : listeners){
			lis.startingToSpeak(e);
		}
	}
	/**
	*現在使用されているPhontデータを取り出します。
	*@return Phontバイト配列
	*/
	public byte[] getPhontData(){
		return data;
	}
	/**
	*AquesTalk2のネイティブなDLLのラッパーです。
	*/
	private interface AquesTalk extends Library{
		AquesTalk INSTANCE = (AquesTalk)Native.loadLibrary("AquesTalk2Da",AquesTalk.class);
		public int AquesTalk2Da_PlaySync(byte[] koe, int iSpeed, byte[] pPhont);
	}
}
