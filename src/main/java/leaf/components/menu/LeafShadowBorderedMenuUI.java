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
package leaf.components.menu;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
*影付きポップアップメニューを生成するUIクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年8月9日
*/
public class LeafShadowBorderedMenuUI extends BasicPopupMenuUI{
	
	/**影の幅を表します。*/
	public static final int SHADOW_WIDTH = 5;
	/**影の円弧の直径を表します。*/
	public static final int SHADOW_ARC   = 10;
	/**影のアルファ値を表します。*/
	public static final float SHADOW_ALPHA = 0.16f;
	
	private static final Insets insets = new Insets(0,0,SHADOW_WIDTH,SHADOW_WIDTH);
	
	/**
	*このUI表現クラスのインスタンスを生成して返します。
	*@param comp nullで構いません
	*@return UIの基底クラス
	*/
	public static ComponentUI createUI(JComponent comp){
		return new LeafShadowBorderedMenuUI();
	}
	/**
	*このUIをシステムのデフォルトのPopupMenuUIとして設定します。
	*/
	public static void setDefaultPopupMenuUI(){
		UIManager.put("PopupMenuUI","leaf.components.menu.LeafShadowBorderedMenuUI");
	}
	/**
	*対象となるコンポーネントが重量コンポーネントであるか返します。
	*@param comp 対象のコンポーネント
	*@return 重量コンポーネントの場合true
	*/
	private static boolean isHeavyWeight(Component comp){
		for(Container p = comp.getParent();p!=null;p=p.getParent()){
			if((p instanceof Panel) || (p instanceof JWindow)){
				return true;
			}
		}
		return false;
	}
	/**
	*JPopupMenuを表示するPopupを返します。
	*@param popup 表示にPopupを要求するJPopupMenu
	*@param x Popupの水平座標
	*@param y Popupの垂直座標
	*@return JPopupMenuを表示するPopup
	*/
	public Popup getPopup(JPopupMenu popup, int x, int y){
		Popup pop    = super.getPopup(popup,x,y);
		JPanel panel = (JPanel)popup.getParent();
		if(isHeavyWeight(popup)){
			panel.setBorder(new ShadowBorder(popup,new Point(x,y)));
		}else{
			panel.setBorder(new ShadowBorderInPanel());
		}
		panel.setOpaque(false);
		return pop;
	}
	/**
	*影のイメージを生成して返します。
	*@param x イメージの水平座標
	*@param y イメージの垂直座標
	*@param w イメージの幅
	*@param h イメージの高さ
	*@return 影のイメージ
	*/
	private static BufferedImage createShadowImage(int x, int y, int w, int h){
		
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setComposite(AlphaComposite.getInstance
			(AlphaComposite.SRC_OVER,SHADOW_ALPHA));
		g2.setPaint(Color.BLACK);
		g2.translate(x,y);
		
		for(int i=0;i<SHADOW_WIDTH;i++){
			g2.fillRoundRect(SHADOW_WIDTH,SHADOW_WIDTH,
				w-SHADOW_WIDTH*2+i,h-SHADOW_WIDTH*2+i,SHADOW_ARC,SHADOW_ARC);
		}
		
		g2.translate(-x,-y);
		g2.dispose();
		return image;
	}
	
	/**重量コンポーネント時の影付きボーダー*/
	private static class ShadowBorder extends AbstractBorder{
		private BufferedImage screenShot = null;
		
		/**コンストラクタ*/
		public ShadowBorder(JComponent comp,Point p){
			try{
				Dimension dim  = comp.getPreferredSize();
				Rectangle rect = new Rectangle(p.x,p.y,
					dim.width+SHADOW_WIDTH,dim.height+SHADOW_WIDTH);
				screenShot = new Robot().createScreenCapture(rect);
			}catch(AWTException ex){
				ex.printStackTrace();
			}
		}
		public Insets getBorderInsets(Component comp){
			return insets;
		}
		/**影の描画*/
		public void paintBorder(Component comp,Graphics g,int x,int y,int w,int h){
			if(screenShot == null) return;
			Graphics2D g2 = (Graphics2D)g;
			g2.drawImage(screenShot, x, y, comp);
			g2.drawImage(createShadowImage(x, y, w, h), x, y, comp);
		}
	}
	
	/**軽量コンポーネント時の影付きボーダー*/
	private static class ShadowBorderInPanel extends AbstractBorder{
		
		public Insets getBorderInsets(Component comp){
			return insets;
		}
		/**影の描画*/
		public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h){
			((Graphics2D)g).drawImage(createShadowImage(x, y, w, h), x, y, null);
		}
	}
}
