/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.border;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;

/**
*写真付きのボーダーです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年6月6日
*/
public class LeafPictureBorder implements Border{
	/**フィールド*/
	private BufferedImage image = null;
	private final File file;
	private final Color bgc;
	private final float alpha;
	private Rectangle rect,trim;
	
	/**
	*ファイルと背景色、アルファ値を指定してボーダーを生成します。
	*<br>fileがnullの場合、画像を表示しません。
	*@param file 画像のファイル
	*@param bgc 背景色
	*@param alpha 画像の表示アルファ値
	*/
	public LeafPictureBorder(File file,Color bgc,float alpha){
		this(file,bgc,alpha,null);
	}
	/**
	*ファイルと背景色、アルファ値及び画像のトリミング座標を指定してボーダーを生成します。
	*<br>fileがnullの場合、画像を表示しません。
	*@param file 画像のファイル
	*@param bgc 背景色
	*@param alpha 画像の表示アルファ値
	*@param trim トリミング座標
	*/
	public LeafPictureBorder(File file,Color bgc,float alpha,Rectangle trim){
		this.file = file;
		this.bgc  = bgc;
		this.alpha = alpha;
		if(file==null||!file.exists())return;
		try{
			this.image = ImageIO.read(file);
			this.rect  = new Rectangle(0,0,image.getWidth(),image.getHeight());
			this.trim  = (trim!=null)?trim:rect;
			this.image = image.getSubimage(trim.x,trim.y,trim.width,trim.height);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**このボーダーを描画します。*/
	public void paintBorder(Component comp,Graphics g,int x,int y,int width,int height){
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(bgc);
		g2.fillRect(x,y,width,height);
		if(image!=null){
			x += (width -rect.width) /2+trim.x;
			y += (height-rect.height)/2+trim.y;
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
			g2.drawRenderedImage(image,AffineTransform.getTranslateInstance(x,y));
		}
	}
	
	/**
	*コンポーネントのボーダーのインセットを返します。
	*/
	public Insets getBorderInsets(Component comp){
		return new Insets(0,0,0,0);
	}
	
	/**
	*ボーダーが不透明かどうか返します。
	*@return trueを返します
	*/
	public boolean isBorderOpaque(){
		return true;
	}
}
