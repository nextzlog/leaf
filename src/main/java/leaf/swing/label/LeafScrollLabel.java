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
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.Timer;

import static java.awt.event.HierarchyEvent.DISPLAYABILITY_CHANGED;

/**
 * 長大な文字列をスクロール表示するユーザーインターフェースです。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年4月29日
 */
public class LeafScrollLabel extends JComponent {
	private GlyphVector vector;
	private float xx, yy, text_x;
	private String text;
	private final Timer timer;
	private int cnt = 0, max = 0, arrow;
	
	/**
	 *文字列を左端から右端の方向へスクロール表示することを示します。
	 *
	 */
	public static final int RIGHT_SCROLL = 1;
	
	/**
	 *文字列を右端から左端の方向へスクロール表示することを示します。
	 *
	 */
	public static final int LEFT_SCROLL  = 0;
	
	/**
	 *左方向へスクロール表示するラベルを作成します。
	 *
	 */
	public LeafScrollLabel(){
		this("", LEFT_SCROLL);
	}
	
	/**
	 *指定した文字列を左方向へスクロール表示するラベルを作成します。
	 *
	 *@param text スクロール表示する文字列
	 */
	public LeafScrollLabel(String text){
		this(text, LEFT_SCROLL);
	}
	
	/**
	 *スクロール表示する文字列とスクロール方向を指定してラベルを作成します。
	 *
	 *@param text  スクロール表示する文字列
	 *@param arrow 文字列が移動する方向
	 */
	public LeafScrollLabel(String text, final int arrow){
		super();
		this.arrow = arrow;
		setFont(new Font(Font.DIALOG, Font.BOLD, 12));
		setForeground(Color.BLACK);
		this.setText(text);
		this.timer = new Timer(25, new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				final int cw = getWidth();
				if(vector != null){
					Rectangle2D visual = vector.getVisualBounds();
					if(cw + visual.getWidth() <= xx){
						if(cnt++ < max) stop();
						xx = 0f;
					}else xx += 1f;
					text_x = (arrow == LEFT_SCROLL)?
					cw - xx : xx - (float) visual.getWidth();
				}
				repaint();
			}
		});
		addHierarchyListener(new HierarchyListener(){
			@Override public void hierarchyChanged(HierarchyEvent e){
				if((e.getChangeFlags() & DISPLAYABILITY_CHANGED)!=0){
					if(isDisplayable()) timer.start();
					else timer.stop();
				}
			}
		});
	}
	
	/**
	 *このラベルでスクロール表示する文字列を指定して表示を更新します。
	 *
	 *@param text スクロール表示する文字列
	 */
	public void setText(String text){
		this.text = String.valueOf(text);
		cnt = 0; xx = 0f;
		vector = null; // must!
	}
	
	/**
	 *このラベルがスクロール表示する文字列を返します。
	 *
	 *@return スクロール表示される文字列
	 */
	public String getText(){
		return text;
	}
	
	/**
	 *このラベルが文字列を連続でスクロール表示する回数を設定します。
	 *
	 *@param max 0以下を指定すると回数無制限
	 */
	public void setScrollCount(int max){
		this.max = max;
	}
	
	/**
	 *このラベルが文字列を連続でスクロール表示する回数を返します。
	 *
	 *@return 自然数であれば回数は有限 回数無制限の場合-1を返す
	 */
	public int getScrollCount(){
		return max >= 0 ? max : -1;
	}
	
	/**
	 *自動再描画の時間間隔を指定することでスクロール速度を設定します。
	 *
	 *@param interval 再描画をトリガーする時間間隔(遅延時間)
	 */
	public void setRepaintInterval(int interval){
		timer.setDelay(interval);
	}
	
	/**
	 *自動再描画の時間間隔を返します。このパラメータはスクロール速度を決定します。
	 *
	 *@return 再描画をトリガーする時間間隔(遅延時間)
	 */
	public int getRepaintInterval(){
		return timer.getDelay();
	}
	
	/**
	 *文字列のスクロール表示を開始します。
	 *
	 */
	public void start(){
		stop();
		timer.start();
	}
	
	/**
	 *文字列のスクロール表示を停止します。
	 *
	 */
	public void stop(){
		timer.stop();
		cnt = 0;
	}
	
	/**
	 *グリフベクタを初期化します。
	 *
	 *@param g2 このラベルを描画するのに用いられるグラフィックス
	 */
	private void initialize(Graphics2D g2){
		FontRenderContext frc = g2.getFontRenderContext();
		LineMetrics met = getFont().getLineMetrics(text, frc);
		final float ascent = met.getAscent();
		vector = getFont().createGlyphVector(frc, text);
		yy = ascent / 2f + (float) vector.getVisualBounds().getY();
	}
	
	/**
	 *このラベルを再描画します。
	 *
	 *@param g このラベルを描画するのに用いられるグラフィックス
	 */
	@Override protected void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		if(vector == null) initialize(g2);
		g2.setPaint(getForeground());
		g2.drawGlyphVector(vector, text_x, getHeight() / 2f - yy);
	}
}