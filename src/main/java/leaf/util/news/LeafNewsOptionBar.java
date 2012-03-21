/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.news;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.*;

import leaf.swing.text.LeafTextField;

/**
*ニュースリーダーの設定画面の実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.3 作成：2011年5月4日
*/
final class LeafNewsOptionBar extends JComponent{
	private boolean isExpanded = false;
	private final LeafNewsBar bar;
	private LeafTextField urlfld;
	private JButton bok, bcancel;
	private Timer animator;
	private JLabel urllb;
	private URL url;
	/**
	*設定画面を生成します。
	*@param bar バー
	*/
	public LeafNewsOptionBar(final LeafNewsBar bar){
		super();
		this.bar = bar;
		setLayout(new ExpandLayout());
		JToolBar box = new JToolBar();
		box.setFloatable(false);
		add(box, BorderLayout.CENTER);
		box.add(urllb  = new JLabel(" URL "));
		box.add(urlfld = new LeafTextField());
		box.add(bok =  new ExButton("button_ok"));
		bok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				expand(false);
				try{
					String url = urlfld.getText();
					bar.setURL(url.isEmpty()? null : url);
					bar.update();
				}catch(MalformedURLException ex){}
			}
		});
		box.add(bcancel = new ExButton("button_cancel"));
		bcancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				expand(false);
				bar.setURL(url);
			}
		});
	}
	/**
	*URLを設定します。
	*@param url URL
	*/
	public void setURL(URL url){
		urlfld.setText(url != null? url.toString() : "");
		this.url = url;
	}
	/**
	*専用のレイアウトマネージャ
	*/
	private class ExpandLayout extends BorderLayout{
		private int h = 0, ph = 0;
		public ExpandLayout(){
			super(2, 2);
		}
		/**
		*推奨されるサイズを返します。
		*/
		public Dimension preferredLayoutSize(Container target){
			Dimension ps = super.preferredLayoutSize(target);
			ph = ps.height;
			if(animator != null){
				if( isExpanded && getHeight() > 0 ) h -= 2;
				if(!isExpanded && getHeight() < ph) h += 2;
				if( h <= 0 || h >= ph ){
					animator.stop();
					animator = null;
					if(h <= 0) h = 0;
					else{
						h = ph;
						urlfld.requestFocusInWindow();
					}
					isExpanded = h != 0;
				}
			}
			ps.height = h;
			return ps;
		}
	}
	/**
	*設定画面を展開/収納します。
	*@param exp 展開する場合true
	*/
	public void expand(boolean exp){
		if(isExpanded == exp) return;
		if(animator != null && animator.isRunning()) return;
		isExpanded = getHeight() != 0;
		animator = new Timer(5, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				revalidate();
			}
		});
		animator.start();
	}
}
