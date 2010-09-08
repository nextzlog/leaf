/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.beans.*;
import javax.swing.*;
import java.util.*;

import leaf.icon.*;

/**
*写真ビューア機能に最適なGlassPaneです。<br>
*クリックすることで表示が消えます。<br>
*JavaSwingTips てんぷらメモさんの公開するサンプルをベースにしています。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年4月12日
*/
public class LeafPictureGlassPane extends JComponent implements HierarchyListener{
	
	/*秘匿フィールド*/
	private final ImageIcon image;
	private final LeafLoadIndicatorIcon indicator = new LeafLoadIndicatorIcon();
	private float alpha = 0.0f;
	private int width = 0;
	private int height = 0;
	private Rectangle rect = new Rectangle();
	private javax.swing.Timer timer;
	/**
	*画像ファイルを指定してこのコンポーネントを生成します。
	*/
	public LeafPictureGlassPane(java.io.File file){
		setOpaque(false);
		super.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		image = new ImageIcon(file.getPath());
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				setVisible(false);
			}
		});
		addHierarchyListener(this);
	}
	/**
	*このコンポーネントの表示/非表示を設定します。
	*@param visible 表示するときはtrue
	*/
	public void setVisible(boolean visible){
		super.setVisible(visible);
		JRootPane rootPane = SwingUtilities.getRootPane(this);
		if(rootPane!=null&&isVisible()!=visible){
			rootPane.getLayeredPane().setVisible(!visible);
		}if(visible&&(timer==null||!timer.isRunning())){
			/*初期値*/
			width = 40;
			height = 40;
			alpha = 0.0f;
			/*展開表示*/
			timer = new javax.swing.Timer(5,new ActionListener(){
				public void actionPerformed(ActionEvent e){
					indicator.next();
					repaint();
				}
			});timer.start();
		}else{
			if(timer!=null)timer.stop();
		}
		indicator.setRunning(visible);
	}
	/**
	*このガラス領域を描画するのに用いられます。
	*/
	public void paintComponent(Graphics g){
		JRootPane rootPane = SwingUtilities.getRootPane(this);
		if(rootPane!=null){
			rootPane.getLayeredPane().print(g);
		}
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		if(height<image.getIconHeight()+10){
			height += image.getIconHeight()/16;
		}else if(width<image.getIconWidth()+10){
			height = image.getIconHeight()+16;
			width  += image.getIconWidth()/8;
		}else if(alpha<1.0){
			width = image.getIconWidth()+10;
			alpha += 0.1f;
		}else{
			indicator.setRunning(false);
			timer.stop();
		}
		rect.setSize(width,height);
		Rectangle screen = getBounds();
		rect.setLocation(screen.x+screen.width/2-rect.width/2,screen.y+screen.height/2-rect.height/2);
		g2.setColor(new Color(160,160,255,100));
		g2.fill(screen);
		g2.setColor(new Color(255,255,255,200));
		g2.fill(rect);
		if(alpha>0){
			if(alpha>1.0f)alpha=1.0f;
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
			g2.drawImage(image.getImage(),rect.x+5,rect.y+5,image.getIconWidth(),image.getIconHeight(),this);
		}else{
			indicator.paintIcon(this,g2,screen.x+screen.width/2-indicator.getIconWidth()/2,screen.y+screen.height/2-indicator.getIconHeight()/2);
		}
	}
	public void hierarchyChanged(HierarchyEvent e){
		if((e.getChangeFlags()&HierarchyEvent.DISPLAYABILITY_CHANGED)!=0&&timer!=null&&!isDisplayable()){
			timer.stop();
		}
	}
}