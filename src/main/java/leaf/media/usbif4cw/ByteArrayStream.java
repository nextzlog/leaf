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
package leaf.media.usbif4cw;

import java.io.*;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
*USBIF4CWによるモールス符字列出力を行うための
*ネイティブ依存なバイト出力ストリームです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年8月16日
*/
public class ByteArrayStream extends OutputStream{
	
	private final int ID;
	private final int VERSION;
	private byte[] buffer = new byte[100];
	private int index = 0;
	private int max   = 0;
	
	private static final USBIF4CW usbif = USBIF4CW.INSTANCE;
	/**
	*USBIF4CWの内部バッファのサイズです*/
	public static final int HARDWARE_BUFFER_MAX = 8;
	
	/**
	*USBIF4CWを検出してストリームを初期化します。
	*@throws IOException USBIF4CWが検出されなかった場合
	*/
	public ByteArrayStream() throws IOException{
		super();
		ID = (int)usbif.usbif4cwOpen(0);
		if(ID < 0)
			throw new IOException("Failed to initialize USBIF4CW");
		VERSION = (int)usbif.usbif4cwGetVersion(ID);
	}
	
	/**
	*指定されたバイト配列をストリームに書き込みます。
	*@param arr ASCII文字を表現するバイトデータ配列
	*@throws IOException バッファに余裕がない場合
	*/
	public void write(byte[] arr) throws IOException{
		for(int i=0;i<arr.length;i++){
			if(max < buffer.length){
				buffer[max++] = arr[i];
			}else{
				throw new IOException("Buffer overflow : " + i);
			}
		}
	}
	
	/**
	*指定されたバイト配列のうちoff以降長さlenのデータをストリームに書き込みます。
	*@param arr ASCII文字を表現するバイトデータ配列
	*@param off 書き込み開始位置
	*@param len 書き込む長さ
	*@throws IOException バッファに余裕がないか、オフセット値が不正の場合
	*/
	public void write(byte[] arr, int off, int len) throws IOException{
		try{
			for(int i=off;i<off+len;i++){
				if(max < buffer.length){
					buffer[max++] = arr[i];
				}else{
					throw new IOException("Buffer overflow : " + i);
				}
			}
		}catch(ArrayIndexOutOfBoundsException ex){
			throw new IOException(ex.getMessage());
		}
	}
	
	/**
	*指定された整数の下位8ビットをストリームに書き込みます。
	*@param ch ASCII文字を表現するバイトデータ
	*@throws IOException バッファに余裕がない場合
	*/
	public void write(int ch) throws IOException{
		if(max < buffer.length){
			buffer[max++] = (byte)ch;
		}else{
			throw new IOException("Buffer overflow : " + max);
		}
	}
	
	/**
	*ストリームをフラッシュしてバイトデータを強制的に出力します。
	*@throws IOException 出力に失敗した場合
	*/
	public void flush() throws IOException {
		long length = 0;
		if(usbif.usbif4cwTxCancel(ID) != 0){
			throw new IOException("Failed to clear hardware buffer");
		}
		while(index <= max){
			if((length = usbif.usbif4cwIsTxBusy(ID)) < HARDWARE_BUFFER_MAX && length >= 0){
				if(usbif.usbif4cwPutChar(ID, buffer[index++]) != 0){
					throw new IOException("Failed to flush stream : Index["+(index-1)+"]");
				}
			}else{
				try{
					Thread.sleep(500);
				}catch(Exception ex){
					throw new IOException(ex.getMessage());
				}
			}
		}
		index = max = 0;
	}
	
	/**
	*ストリームを閉じてリソースを解放します。
	*/
	public void close(){
		buffer = null;
		usbif.usbif4cwClose(ID);
	}
	
	/**
	*USBIF4CWのPTT遅延時間を設定します。
	*@param bef PTT前の遅延時間
	*@param aft PTT後の遅延時間
	*@throws IOException 設定に失敗したとき
	*/
	public void setDelay(int bef, int aft) throws IOException{
		if(usbif.usbif4cwSetPTTParam(ID, (byte)bef, (byte)aft) != 0)
			throw new IOException("Failed to set delay : [" +bef + "," + aft + "]");
	}
	
	/**
	*USBIF4CWの送信速度を設定します。
	*@param wpm 分あたりの単語数
	*@throws IOException 設定に失敗したとき
	*/
	public void setWPM(int wpm) throws IOException{
		if(usbif.usbif4cwSetWPM(ID, wpm) != 0)
			throw new IOException("Failed to set WPM : " + wpm);
	}
	
	/**
	*USBIF4CWの送信速度を返します。
	*@return 分あたりの単語数
	*/
	public int getWPM() throws IOException{
		int wpm = (int)usbif.usbif4cwGetWPM(ID);
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
	private interface USBIF4CW extends Library{
		USBIF4CW INSTANCE = (USBIF4CW)Native.loadLibrary("usbif4cw",USBIF4CW.class);
		
		/**
		*USBIF4CWをポートから検出して初期化します。
		*@param param 使用されていません
		*/
		public long usbif4cwOpen(long param);
		
		/**
		*指定されたUSBIF4CWへのストリームを閉じリソースを解放します。
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
		*@param nID ID番号
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
