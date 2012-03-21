/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.demo;

import java.io.*;

/**
*コピーレフト宣言を取り出して表示
*
*@author 東大アマチュア無線クラブ
*@since  Leaf 1.3 作成：2011年4月4日
*/
final class Copyleft{
	public static String read() throws IOException{
		ClassLoader loader = Copyleft.class.getClassLoader();
		InputStream stream = loader.getResourceAsStream("readme/copyleft.html");
		BufferedReader breader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = breader.readLine())!=null){
			sb.append(line);
		}
		return sb.toString();
	}
}
