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
import javax.swing.*;

/**
 *セル・オートマトンの一種「ライフ」のテーブルの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年4月1日
 */
public class LeafLifePane extends JComponent{
	private boolean[][] table;
	private Image screen;
	private Graphics graph;
	private int width, height, pixel, gen;
	private float hue = 0.3f;
	private final float HUE_DIV = 0.01f;
	
	/**
	 *縦横25セル、1セルあたり25ピクセルのライフ盤を生成します。
	 */
	public LeafLifePane(){
		this(25, 25, 25);
	}
	/**
	 *縦横のセル数と1セルのピクセル数を指定してライフ盤を生成します。
	 *@param w 横のセル数
	 *@param h 縦のセル数
	 *@param pixel ピクセル数
	 */
	public LeafLifePane(final int w, final int h, final int pixel){
		setPreferredSize(new Dimension(w * pixel, h * pixel));
		init(width = w, height = h);
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				int x = e.getX() / pixel;
				int y = e.getY() / pixel;
				if(x <= w && y <= h){
					table[x][y] = !table[x][y];
					repaint();
				}
			}
		});
		setForeground(Color.getHSBColor(hue, 1f, 1f));
		setBackground(Color.BLACK);
		this.pixel = pixel;
	}
	/**
	 *セルテーブルを初期化して再描画します。
	 */
	public void init(){
		init(width, height);
		repaint();
	}
	/**
	 *セルテーブルを初期化します。
	 *@param w 横のセル数
	 *@param h 縦のセル数
	 */
	private void init(int w, int h){
		table = new boolean[w][h];
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				table[x][y] = false;
			}
		}
		gen = 0;
	}
	/**
	 *縦方向のセル数を返します。
	 *@return 縦のセル数
	 */
	public int getHeightCellCount(){
		return height;
	}
	/**
	*横方向のセル数を返します。
	*@return 横のセル数
	*/
	public int getWidthCellCount(){
		return width;
	}
	/**
	 *セルのテーブルを返します。
	 *@return セルの2次元配列
	 */
	public boolean[][] getTable(){
		return table;
	}
	/**
	 *セルのテーブルを描画します。
	 *@param g グラフィックス
	 */
	public void paintComponent(Graphics g){
		Dimension dim = getPreferredSize();
		if(screen == null){
			screen = createImage(dim.width, dim.height);
			graph = screen.getGraphics();
		}
		graph.setColor(getBackground());
		graph.fillRect(0, 0, dim.width, dim.height);
		graph.setColor(getForeground());
		for(int x=0;x<width;x++){
			int xpixel = x * pixel;
			for(int y=0;y<height;y++){
				if(table[x][y]){
					graph.fillRect(
						xpixel, y*pixel,
						pixel-1, pixel-1
					);
				}
			}
		}
		g.clearRect(0, 0, dim.width, dim.height);
		g.drawImage(screen, 0, 0, this);
	}
	/**
	 *セルの色循環を実装します。
	 */
	private void nextColor(){
		if((hue += HUE_DIV) > 1f) hue = 0f;
		setForeground(Color.getHSBColor(hue, 1f, 1f));
	}
	/**
	 *セルのテーブルの状態を更新します。
	 *@return 現在の世代数
	 */
	public int updateNext(){
		//副作用排除
		boolean next[][] = new boolean[width][height];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				next[x][y] = checkAlive(x, y);
			}
		}
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				table[x][y] = next[x][y];
			}
		}
		nextColor();
		repaint();
		return ++gen;
	}
	/**
	 *指定された座標の次の世代の状態を返します。
	 *@param x 横方向座標
	 *@param y 縦方向座標
	 *@return 次の世代の状態
	 */
	private boolean checkAlive(int x, int y){
		int life = 0, w = width, h = height;
		life += table[(x+w-1)%w][(y+h-1)%h]? 1 : 0;
		life += table[(x + w)%w][(y+h-1)%h]? 1 : 0;
		life += table[(x+w+1)%w][(y+h-1)%h]? 1 : 0;
		life += table[(x+w-1)%w][(y + h)%h]? 1 : 0;
		life += table[(x+w+1)%w][(y + h)%h]? 1 : 0;
		life += table[(x+w-1)%w][(y+h+1)%h]? 1 : 0;
		life += table[(x + w)%w][(y+h+1)%h]? 1 : 0;
		life += table[(x+w+1)%w][(y+h+1)%h]? 1 : 0;
		return (life==2 && table[x][y]) || life==3;
	}
}