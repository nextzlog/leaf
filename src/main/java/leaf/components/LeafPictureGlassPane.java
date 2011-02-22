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
package leaf.components;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.beans.*;
import javax.swing.*;
import java.io.File;
import java.io.FileFilter;

import leaf.icon.LeafLoadIndicatorIcon;
import leaf.icon.LeafIcons;
import leaf.manager.LeafFileManager;

/**
*本格的なピクチャービューアをガラス領域で実装するコンポーネントです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年4月12日 大幅拡張：2010年9月16日
*/

public class LeafPictureGlassPane extends JComponent{
	
	private ImageIcon image;
	private final LeafLoadIndicatorIcon indicator;
	private final Color COLOR_SCREEN = new Color(70,70,70,140);
	private final Color COLOR_WINDOW = Color.WHITE;
	private final Rectangle rect = new Rectangle();
	private int width = 0, height = 0;
	private float alpha = 0f;
	private final FileFilter filter;
	private final File[] files;
	private int index = 0;
	private String name;
	private Timer timer;
	
	/**
	*デフォルトで表示される画像ファイルを指定してビューアを生成します。
	*@param file 画像ファイル
	*/
	public LeafPictureGlassPane(File file){
		
		setOpaque(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		indicator = new LeafLoadIndicatorIcon();
		
		filter = new FileFilter(){
			public boolean accept(File file){
				String suf = LeafFileManager.getSuffix(file);
				return(suf!=null&& suf.matches("(gif|jpg|jpeg|png)"));
			}
		};
		files = file.getParentFile().listFiles(filter);
		init(file);
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				setVisible(false);
			}
		});
		addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e){
				showNext((e.getWheelRotation()>0)?LightButton.RIGHT:LightButton.LEFT);
			}
		});
		addHierarchyListener(new HierarchyListener(){
			public void hierarchyChanged(HierarchyEvent e){
				if((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0
				&& timer != null && !isDisplayable()){
					timer.stop();
				}
			}
		});
		setLayout(new BorderLayout());
		add(new LightButton(LightButton.LEFT), BorderLayout.WEST);
		add(new LightButton(LightButton.RIGHT),BorderLayout.EAST);
	}
	/**
	*ビューアを初期化します。
	*@param file 表示するファイル
	*/
	public void init(File file){
		
		image = new ImageIcon(file.getPath());
		name  = file.getName();
		for(int i=0;i<files.length;i++){
			if(file.equals(files[i]))index = i;
		}
	}
	/**
	*ビューアの表示/非表示を設定します。
	*@param visible 表示する場合true
	*/
	public void setVisible(boolean visible){
		
		super.setVisible(visible);
		JRootPane root = SwingUtilities.getRootPane(this);
		if(root != null && isVisible() != visible){
			root.getLayeredPane().setVisible(!visible);
		}
		if(visible && (timer==null || !timer.isRunning())){
			
			width = height = 40;
			alpha = 0f;
			timer = new Timer(10, new ActionListener(){
				public void actionPerformed(ActionEvent e){
					indicator.next();
					repaint();
				}
			});
			timer.start();
		}else{
			if(timer!=null) timer.stop();
		}
		indicator.setRunning(visible);
	}
	/**
	*表示されている画像を返します。
	*@return 表示されているImageオブジェクト
	*/
	public Image getImage(){
		return image.getImage();
	}
	/**
	*ビューアを描画します。
	*@param g グラフィックス
	*/
	public void paintComponent(Graphics g){
		
		JRootPane root = SwingUtilities.getRootPane(this);
		if(root!=null){
			root.getLayeredPane().print(g);
		}
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		FontMetrics met = g2.getFontMetrics();
		int inset = met.getHeight();
		
		if(height < image.getIconHeight() + 20 + inset){
			height += image.getIconHeight() / 16;
		}else if(width < image.getIconWidth() + 20){
			height = image.getIconHeight() + 20 + inset;
			width += image.getIconWidth() / 8;
		}else if(alpha < 1f){
			width =  image.getIconWidth() + 20;
			alpha += 0.05f;
		}else{
			indicator.setRunning(false);
			timer.stop();
		}
		
		rect.setSize(width, height);
		Rectangle screen = getBounds();
		rect.setLocation(
			screen.x + (screen.width  - rect.width ) /2,
			screen.y + (screen.height - rect.height) /2
		);
		g2.setPaint(COLOR_SCREEN);
		g2.fill(screen);
		g2.setPaint(COLOR_WINDOW);
		g2.fill(rect);
		
		if(alpha>0){
			if(alpha > 1f) alpha = 1f;
			g2.setComposite(
				AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
			);
			g2.drawImage(image.getImage(),
				rect.x + 10, rect.y + 10,
				image.getIconWidth(), image.getIconHeight(),
				this
			);
			g2.setPaint(Color.BLACK);
			g2.drawString(name,
				rect.x + (rect.width - met.stringWidth(name)) / 2,
				rect.y + rect.height - inset + 5
			);
		}else{
			indicator.paintIcon(this, g2,
				screen.x + (screen.width  - indicator.getIconWidth() ) / 2,
				screen.y + (screen.height - indicator.getIconHeight()) / 2
			);
		}
		g2.setComposite(
			AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
		);
	}
	/**
	*左右に表示される光るボタンの実装です。
	*/
	private class LightButton extends JLabel{
		public static final int LEFT = -1, RIGHT = 1;
		private int side;
		public LightButton(final int side){
			if(side==LEFT){
				setIcon(new LeafIcons().getIcon("aqualeft"));
			}else{
				setIcon(new LeafIcons().getIcon("aquaright"));
			}
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					showNext(side);
				}
			});
		}
	}
	/**
	*同一フォルダ内の次の画像ファイルを表示します。
	*@param side 左側か右側かを指定
	*/
	private void showNext(int side){
		if(timer!=null&&timer.isRunning()) return;
		if(side==LightButton.LEFT)
			index = (index<=0)?(files.length-1):(index-1);
		else
			index = (index>=files.length-1)?0:(index+1);
		init(files[index]);
		setVisible(true);
	}
}
