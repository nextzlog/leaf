/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.media.aquestalk;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 *AquesTalk2をアプリケーションから利用するためのマネージャです。
 *<pre>
 *声種ファイル…声のもととなるデータです
 *読み上げ速度…標準速度に対する100分比率で50以上300以下の整数
 *</pre>
 *AquesTalk2エンジンは環境に合わせて別途ダウンロードしてください。
 *<br><br>
 *必要なDLL : AquesTalk2Da.dll(so) (leaf.jarと同じディレクトリに配置)
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.1 作成：2010年10月1日
 */
public class AquesTalkManager{
	private static AquesTalk aques;
	private final File phont;
	private byte[] data;
	private int speed;
	/**
	 *デフォルト声種を指定してマネージャを生成します。
	 *@throws AquesTalkException エンジンが存在しない場合
	 */
	public AquesTalkManager()
	throws AquesTalkException{
		this(null, 100);
	}
	/**
	 *声種を指定してマネージャを生成します。
	 *ファイルが空の場合デフォルト声種が割り当てられます。
	 *@param phont 声種ファイル
	 *@throws AquesTalkException エンジンが存在しない場合
	 */
	public AquesTalkManager(File phont)
	throws AquesTalkException{
		this(phont, 100);
	}
	/**
	 *声種と読み上げ速度を指定してマネージャを生成します。
	 *ファイルが空の場合デフォルト音声が割り当てられます。
	 *@param phont 声種ファイル
	 *@param speed 読み上げスピード
	 *@throws AquesTalkException エンジンが見つからない場合
	 */
	public AquesTalkManager(File phont, int speed)
	throws AquesTalkException{
		loadLibrary("AquesTalk2Da");
		this.phont = phont;
		this.speed = speed;
		try{
			load(phont);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 *AquesTalk2のエンジンをロードします。
	 *@param name エンジンのファイル名
	 *@throws AquesTalkException エンジンが見つからない場合
	 */
	private static void loadLibrary(String name)
	throws AquesTalkException{
		if(aques == null){
			name = System.mapLibraryName(name);
			if(new File(name).canRead()){
				aques = (AquesTalk)
					Native.loadLibrary(name, AquesTalk.class);
				return;
			}
			throw new AquesTalkException(name + "not found");
		}
	}
	/**
	 *指定されたテキストを読み上げます。
	 *読み上げが完了するまでメソッドはロックされます。
	 *@param text 読み上げるテキスト
	 *@throws AquesTalkException 出力エラーがあった場合
	 */
	public void speak(String text)
	throws AquesTalkException{
		byte[] str = (text + "\0").getBytes();
		if(aques.AquesTalk2Da_PlaySync(str, speed, data)!=0)
			throw new AquesTalkException("Failed to output");
	}
	/**
	 *指定されたテキストを指定された速度で読み上げます。
	 *@param text 読み上げるテキスト
	 *@param speed 読み上げスピード
	 *@throws AquesTalkException 出力エラーがあった場合
	 */
	public void speak(String text, int speed)
	throws AquesTalkException{
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
	 *現在使用されている声種ファイルを返します。
	 *@return 声種ファイル
	 */
	public File getPhontFile(){
		return phont;
	}
	/**
	 *声種ファイルを読み込みます。
	 *@param phont 読みこむファイル
	 *@throws AquesTalkException 読み込みに失敗した場合
	 */
	public void load(File phont) throws AquesTalkException{
		if(phont != null){
			RandomAccessFile file = null;
			FileChannel channel = null;
			try{
				file = new RandomAccessFile(phont, "r");
				channel = file.getChannel();
				byte[] data = new byte[(int)channel.size()];
				int index = 0, readed;
				ByteBuffer buffer = ByteBuffer.allocate(100);
				while((readed = channel.read(buffer))>0){
					buffer.flip();
					for(int i=0; i<readed; i++){
						data[index++] = buffer.get();
					}
					buffer.rewind();
				}
				this.data = data;
			}catch(IOException ex){
				throw new AquesTalkException(ex);
			}finally{
				try{
					if(channel != null) channel.close();
					if(file != null) file.close();
				}catch(IOException ex){
					throw new AquesTalkException(ex);
				}
			}
		}else data = null;
	}
	/**
	 *現在使用されている声種データを取り出します。
	 *@return 声種バイト配列
	 */
	public byte[] getPhontData(){
		return data;
	}
	/**
	 *AquesTalk2のネイティブなDLLのラッパーです。
	 */
	private interface AquesTalk extends Library{
		public int AquesTalk2Da_PlaySync
		(byte[] koe, int iSpeed, byte[] pPhont);
	}
}
