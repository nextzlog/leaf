/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
*16進ダンプ機能をアプリケーション向けに提供します。
*
*@author 東大アマチュア無線クラブ
*@since  Leaf 1.3 作成：2011年3月20日
*/
public class LeafDumpManager{
	
	//秘匿コンストラクタ
	private LeafDumpManager(){}
	
	/**
	*読み込み元ストリームを指定してダンプします。
	*読み込みが終了するとストリームは自動で閉じられます。
	*@param stream 読み込み元
	*@return ダンプの内容
	*@throws IOException 読み込み例外が発生した場合
	*/
	public static String dump(InputStream stream) throws IOException{
		StringBuilder sb = new StringBuilder(1024);
		byte[] buffer = new byte[16];
		int len;
		while((len = stream.read(buffer)) > 0){
			for(int i=0;i<len;i++){
				int b = buffer[i] & 0xFF;
				sb.append(Integer.toHexString(b>>4).toUpperCase());
				sb.append(Integer.toHexString(b%16).toUpperCase());
				sb.append(" ");
			}
			for(int i=0;i<16-len;i++) sb.append("   ");
			sb.append(" ");
			for(int i=0;i<len;i++){
				if(buffer[i] >= 0x20 && buffer[i] <= 0x7E){
					sb.append((char)buffer[i]);
				}else{
					sb.append(".");
				}
			}
			sb.append("\n");
		}
		stream.close();
		return sb.toString();
	}
}