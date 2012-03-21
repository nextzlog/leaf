/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_ROUND;

/**
 *ドラッグにより画像をトリミングするための簡易的なコンポーネントです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年3月30日
 */
public class LeafTrimPane extends JComponent{
	private BufferedImage fullsize, trimmed;
	private Image view;
	private Rectangle drag, trim; //点線用/切り抜き用
	private final BasicStroke stroke;
	private final MediaTracker tracker;
	private final float[] pattern = {3f, 3f, 3f, 3f};
	
	/**
	 *デフォルトの何も表示しないコンポーネントを生成します。
	 */
	public LeafTrimPane(){
		this(null);
	}
	/**
	 *デフォルトで表示する画像を指定してコンポーネントを生成します。
	 *
	 *@param image 表示する画像
	 */
	public LeafTrimPane(BufferedImage image){
		tracker = new MediaTracker(this);
		setImage(image);
		addComponentListener(new ResizeListener());
		DragListener listener = new DragListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		stroke = new BasicStroke(1f, CAP_SQUARE, JOIN_ROUND, 1f, pattern, 0f);
		trim = new Rectangle(0, 0, 0, 0);
	}
	/**
	 *表示画像を設定します。このメソッドは画像を完全にロードするまで復帰しません。
	 *
	 *@param image 表示する画像
	 */
	public void setImage(BufferedImage image){
		final Image old = fullsize;
		fullsize = trimmed = image;
		if(image != null) try{
			tracker.addImage(image, 0);
			tracker.waitForID(0);
			trim.x = trim.y = 0;
			trim.width  = image.getWidth();
			trim.height = image.getHeight();
			resizeViewImage();
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
		firePropertyChange("image", old, image);
	}
	/**
	 *表示画像を返します。
	 *
	 *@return 表示画像
	 */
	public BufferedImage getImage(){
		return fullsize;
	}
	/**
	 *描画用のイメージをコンポーネントのサイズに合わせます。
	 */
	private void resizeViewImage(){
		if(trimmed != null && isShowing()){
			Rectangle bounds = getVisibleRect();
			view = trimmed.getScaledInstance(
			bounds.width, bounds.height, Image.SCALE_SMOOTH);
		}
		repaint();
	}
	/**
	 *コンポーネントを描画します。
	 *
	 *@param g グラフィックス
	 */
	@Override protected void paintComponent(Graphics g){
		super.paintComponent(g);
		if(trimmed != null){
			g.drawImage(view, 0, 0, this);
			if(drag != null){
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(getBackground());
				g2.setXORMode(getForeground());
				g2.setStroke(stroke);
				g2.draw(drag);
			}
		}
	}
	/**
	 *指定した座標で表示画像を切り抜きます。
	 *
	 *@param trim 画像を切り抜く座標
	 */
	public void setTrimRect(Rectangle trim){
		final Rectangle old = this.trim;
		try{
			this.trim = trim = (Rectangle) trim.clone();
			trimmed = fullsize.getSubimage(
			trim.x, trim.y, trim.width, trim.height);
			firePropertyChange("trimRect", old, trim);
			resizeViewImage();
		}catch(Exception ex){
			setImage(fullsize);
		}
		repaint();
	}
	/**
	 *画像を切り抜く座標を返します。
	 *
	 *@return 画像を切り抜く座標
	 */
	public Rectangle getTrimRect(){
		return trim;
	}
	/**
	 *コンポーネントのリサイズを監視します。
	 */
	private class ResizeListener extends ComponentAdapter{
		@Override public void componentResized(ComponentEvent e){
			resizeViewImage();
		}
	}
	/**
	 *マウスのドラッグ・ドロップを監視します。
	 */
	private class DragListener extends MouseAdapter{
		private Point start = new Point();
		private double wratio = 1, hratio = 1;
		@Override public void mousePressed(MouseEvent e){
			if(trimmed != null){
				start = e.getPoint();
				wratio = (double)trimmed.getWidth() /getWidth();
				hratio = (double)trimmed.getHeight()/getHeight();
			}
		}
		@Override public void mouseDragged(MouseEvent e){
			if(trimmed != null){
				drag = new Rectangle(
					start.x, start.y,
					e.getX()-start.x,
					e.getY()-start.y
				);
				repaint();
			}
		}
		@Override public void mouseReleased(MouseEvent e){
			if(trimmed != null){
				drag = null;
				trim.x += (int)(start.x * wratio);
				trim.y += (int)(start.y * hratio);
				trim.width  = (int)((e.getX()-start.x)*wratio);
				trim.height = (int)((e.getY()-start.y)*hratio);
				setTrimRect(trim);
				fireActionPerformed();
			}
		}
	}
	/**
	 *ActionListenerを追加登録します。
	 *
	 *@param lis 登録するリスナー
	 */
	public void addActionListener(ActionListener lis){
		listenerList.add(ActionListener.class, lis);
	}
	/**
	 *ActionListenerを削除します。
	 *
	 *@param lis 削除するリスナー
	 */
	public void removeActionListener(ActionListener lis){
		listenerList.remove(ActionListener.class, lis);
	}
	/**
	 *ドラッグ・ドロップに起因するイベントを通知します。
	 */
	private void fireActionPerformed(){
		ActionEvent e = new ActionEvent(this,
		ActionEvent.ACTION_PERFORMED, "trim");
		ActionListener[] listeners =
		listenerList.getListeners(ActionListener.class);
		for(int i=0;i<listeners.length;i++){
			listeners[i].actionPerformed(e);
		}
	}
}
