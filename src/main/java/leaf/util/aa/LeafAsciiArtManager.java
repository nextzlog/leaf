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
package leaf.util.aa;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.image.PixelGrabber;
import javax.swing.ImageIcon;

/**
*イメージからアスキーアートを簡単に生成するマネージャです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月29日
*/
public class LeafAsciiArtManager{

	private final FontMetrics met;
	private final int fh;
	
	/**
	*使用する{@link FontMetrics}を指定してマネージャを生成します。
	*@param met 対象となるコンポーネントでのFontMetrics
	*/
	public LeafAsciiArtManager(FontMetrics met){
		this.met = met;
		fh = met.getHeight();
	}
	/**
	*指定されたImageIconをアスキーアートに変換します。
	*@param img 変換元のイメージ
	*@param elems アスキーアートを構成する文字要素の文字列
	*@return アスキーアート文字列
	*@throws InterruptedException 他のスレッドにより中止された場合
	*/
	public String convert(ImageIcon img, String elems) throws InterruptedException{
		
		int w = img.getIconWidth();
		int h = img.getIconHeight();
		
		PixelGrabber pg = new PixelGrabber(img.getImage(),0,0,w,h,true);
		
		pg.grabPixels();
		int[] pixels = (int[])pg.getPixels();
		
		StringBuilder sb = new StringBuilder();
		String ls = System.getProperty("line.separator");
		
		int len = elems.length()-1;
		char ch;
		
		for(int y=0; y<h; y+=fh){
			for(int x=0; x<w; x+= met.charWidth(ch)){
				Color color = new Color(pixels[y*w+x]);
				int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
				sb.append(Character.toString(ch = elems.charAt(gray*len/256)));
			}
			sb.append(ls);
		}
		return sb.toString();
	}
}