/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.label;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.Timer;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

/**
 *タスクが進行中であることを視覚的に表示するコンポーネントです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.0 作成：2010年5月23日 再編：2011年7月9日
 */
public class LeafBusyLabel extends JLabel{
	private final IndicatorIcon icon;
	private final Timer timer;
	
	/**
	*インジケータを生成します。
	*/
	public LeafBusyLabel(){
		super();
		icon  = new IndicatorIcon();
		addComponentListener(icon);
		timer = new Timer(100, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				icon.next();
				repaint();
			}
		});
		setForeground(Color.DARK_GRAY);
		icon.update(2d);
		setIcon(icon);
	}
	/**
	*アイコンの実装です。
	*/
	private class IndicatorIcon
	extends ComponentAdapter implements Icon{
		private java.util.List<Shape> list;
		private double sx = 0d, sy = 0d;
		private boolean running;
		private Dimension dim;
		/**
		*アイコンの描画リストを更新します。
		*@param r 半径
		*/
		protected void update(double r){
			Dimension dim = new Dimension((int)(r*8+sx*2),(int)(r*8+sy*2));
			if(r>0 && !dim.equals(this.dim)){
				this.dim = dim;
				list = new LinkedList<Shape>(Arrays.asList(
					new Ellipse2D.Double(sx+3*r, sy+0*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+5*r, sy+1*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+6*r, sy+3*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+5*r, sy+5*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+3*r, sy+6*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+1*r, sy+5*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+0*r, sy+3*r, 2*r, 2*r),
					new Ellipse2D.Double(sx+1*r, sy+1*r, 2*r, 2*r)
				));
			}
		}
		/**
		*コンポーネントのサイズ変更時に実行されます。
		*@param e イベント
		*/
		public void componentResized(ComponentEvent e){
			if(isShowing()){
				int r = Math.max(getHeight(), getWidth()) >> 3;
				update((double)r);
			}
		}
		/**
		*コンポーネントの可視化時に実行されます。
		*@param e イベント
		*/
		public void componentShown(ComponentEvent e){
			int r = Math.max(getHeight(), getWidth()) >> 3;
			update((double)r);
		}
		/**
		*アイコンの回転アニメーションを１コマ先に進めます。
		*/
		public void next(){
			if(running) list.add(list.remove(0));
		}
		/**
		*アイコンを描画します。
		*@param g グラフィックス
		*@param x 描画位置
		*@param y 描画位置
		*/
		public void paintIcon(Component comp,Graphics g,int x,int y){
			if(running){
				Graphics2D g2 = (Graphics2D)g;
				g2.setPaint(new Color(0,0,0,0));
				g2.fillRect(x,y,getIconWidth(),getIconHeight());
				g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
				g2.setColor(getForeground());
				g2.translate(x,y);
				float alpha = 0.0f;
				for(Shape s:list){
					g2.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, alpha));
					alpha += 0.125f;
					g2.fill(s);
				}
				g2.translate(-x,-y);
			}
		}
		/**
		*アイコンの幅を返します。
		*@return アイコンの幅
		*/
		public int getIconWidth(){
			return dim.width;
		}
		/**
		*アイコンの高さを返します。
		*@return アイコンの高さ
		*/
		public int getIconHeight(){
			return dim.height;
		}
	}
	/**
	*インジケータの状態を返します。
	*@return インジケータが回転中の場合、trueを返します。
	*/
	public boolean isRunning(){
		return icon.running;
	}
	/**
	*インジケータの回転を開始します。
	*/
	public void start(){
		icon.running = true;
		timer.start();
	}
	/**
	*インジケータの回転を終了します。
	*/
	public void stop(){
		icon.running = false;
		timer.stop();
	}
}