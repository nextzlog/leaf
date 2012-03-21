/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.media.usbif4cw;

import java.io.*;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
*CWインターフェース「USBIF4CW」へのモールス符字列出力
*を実装するネイティブ依存なバイト出力ストリームです。
*
*@author 東大アマチュア無線クラブ
*@since  Leaf 1.0 作成：2010年8月16日
*/
public class ByteStream extends OutputStream{
	private byte[] buffer = new byte[256];
	private static Driver driver;
	private final int VERSION;
	private final int ID;
	private int index, max;
	/**
	*USBIF4CWを検出してストリームを初期化します。
	*@throws IOException
	*USBIF4CWが検出されないかドライバが見つからない場合
	*/
	public ByteStream() throws IOException{
		super();
		loadLibrary("usbif4cw");
		if((ID = (int) driver.usbif4cwOpen(0)) < 0){
			throw new IOException("USBIF4CW not found");
		}
		VERSION = (int) driver.usbif4cwGetVersion(ID);
	}
	/**
	*USBIF4CWのドライバをロードします。
	*@param name ドライバの名前
	*@throws IOException 見つからない場合
	*/
	private static void loadLibrary(String name) throws IOException{
		if(driver == null){
			name = System.mapLibraryName(name);
			if(new File(name).canRead()){
				driver = (Driver)
					Native.loadLibrary(name, Driver.class);
				return;
			}
			throw new IOException(name + "not found");
		}
	}
	/**
	*整数の下位8ビットをストリームに書き込みます。
	*@param ch ASCII文字を表現するバイトデータ
	*@throws IOException バッファオーバーフロー時
	*/
	@Override
	public void write(int ch) throws IOException{
		try{
			buffer[max++] = (byte) ch;
		}catch(IndexOutOfBoundsException ex){
			throw new IOException(ex);
		}
	}
	/**
	*USBIF4CWにバイトデータを出力します。
	*@throws IOException 通信に失敗した場合
	*/
	public void flush() throws IOException{
		while(index < max){
			if((int)driver.usbif4cwIsTxBusy(ID)==0){
				if((int)driver.usbif4cwPutChar(
					ID, buffer[index++]) != 0){
					throw new IOException("Failed to flush");
				}
			}
			try{
				Thread.sleep(10);
			}catch(Exception ex){
				throw new IOException(ex);
			}
		}
		index = max = 0;
	}
	/**
	*ストリームを閉じてリソースを解放します。
	*/
	public void close(){
		buffer = null;
		driver.usbif4cwClose(ID);
	}
	/**
	*USBIF4CWの送信速度を設定します。
	*@param wpm 分あたりの単語数
	*@throws IOException 設定に失敗したとき
	*/
	public void setWPM(int wpm) throws IOException{
		if((int) driver.usbif4cwSetWPM(ID, wpm) != 0)
			throw new IOException("Failed to set WPM : " + wpm);
	}
	/**
	*USBIF4CWの送信速度を返します。
	*@return 分あたりの単語数
	*/
	public int getWPM() throws IOException{
		int wpm = (int)driver.usbif4cwGetWPM(ID);
		if(wpm < 0)
			throw new IOException("Failed to get WPM");
		return wpm;
	}
	/**
	*USBIF4CWのID番号を返します。
	*@return ID番号
	*/
	public int getID(){
		return ID;
	}
	/**
	*USBIF4CWのバージョンを返します。
	*@return バージョン
	*/
	public float getVersion(){
		return ((float)VERSION) / 10;
	}
	/**
	*USBIF4CWのネイティブなDLLのラッパーです。
	*/
	private interface Driver extends Library{
		
		/**
		*USBIF4CWをポートから検出して初期化します。
		*@param param 使用されていません
		*/
		public long usbif4cwOpen(long param);
		
		/**
		*指定されたUSBIF4CWへのストリームを閉じます。
		*@param nId ID番号
		*/
		public void usbif4cwClose(int nId);
		
		/**
		*USBIF4CWにバイトデータを送ります。
		*@param nId ID番号
		*@param port USBIF4CW側のポート番号
		*@param data バイトデータ
		*/
		public long usbif4cwWriteData(int nId, int port, byte data);
		
		/**
		*UDBIF4CWに文字を送信します。
		*@param nId ID番号
		*@param data 文字データ
		*/
		public long usbif4cwPutChar(int nId, byte data);
		
		/**
		*USBIF4CWの打鍵速度を設定します。
		*@param nId ID番号
		*@param nWPM 一分間の打鍵ワード数
		*/
		public long usbif4cwSetWPM(int nId, int nWPM);
		
		/**
		*USBIF4CWの打鍵速度を取得します。
		*@param nId ID番号
		*@return 一分間の打鍵ワード数
		*/
		public long usbif4cwGetWPM(int nId);
		
		/**
		*USBIF4CWの内部バッファの待機文字数を取得します。
		*@param nId ID番号
		*@return 待機文字数
		*/
		public long usbif4cwIsTxBusy(int nId);
		
		/**
		*USBIF4CWの内部バッファをクリアします。
		*@param nId ID番号
		*/
		public long usbif4cwTxCancel(int nId);
		
		/**
		*USBIF4CWにPTTを設定します。
		*@param nId ID番号
		*@param tx 送信時は下位ビット1、受信時は下位ビット0
		*/
		public long usbif4cwSetPTT(int nId, byte tx);
		
		/**
		*USBIF4CWのPTTの切り替え遅延時間を設定します。
		*@param nId ID番号
		*@param nLen1 PTT前ディレイ
		*@param nLen2 PTT後ディレイ
		*/
		public long usbif4cwSetPTTParam(int nId, byte nLen1, byte nLen2);
		
		/**
		*USBIF4CWのバージョン番号を返します。
		*@param nId ID番号
		*@return バージョン番号を10倍した整数
		*/
		public long usbif4cwGetVersion(int nId);
		
	}
}