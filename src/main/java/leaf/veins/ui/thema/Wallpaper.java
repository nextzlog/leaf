/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.thema;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.border.Border;

/**
 * 壁紙を表示する{@link Border}の実装です。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2011年12月20日 
 *
 */
public final class Wallpaper implements Border {
	private final WallpaperSettings settings;
	private BufferedImage image;
	private Rectangle size, trim;

	/**
	 * 壁紙設定を指定して壁紙を生成します。
	 * 
	 * @param ws 壁紙の設定
	 * @throws IOException 壁紙の読み込みに失敗した場合
	 */
	public Wallpaper(WallpaperSettings ws) throws IOException {
		this.settings = ws;
		if(ws.getPhotoFile() != null) loadImage();
	}
	
	private void loadImage() throws IOException {
		image = ImageIO.read(settings.getPhotoFile());
		int iw = image.getWidth();
		int ih = image.getHeight();
		size = new Rectangle(0, 0, iw, ih);
		
		trim = settings.getPhotoTrimRect();
		if(trim == null) trim = size;
		int tx = trim.x;
		int ty = trim.y;
		int tw = trim.width;
		int th = trim.height;
		
		image = image.getSubimage(tx, ty, tw, th);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, 0);
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(settings.getBackgroundColor());
		g2.fillRect(x, y, width, height);
		if(image != null){
			
			x += (width  - size.width ) / 2 + trim.x;
			y += (height - size.height) / 2 + trim.y;
			
			float alpha = settings.getPhotoAlpha();
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2.drawRenderedImage(image, AffineTransform.getTranslateInstance(x,y));
		}
	}

}
