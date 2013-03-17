/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.news;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import leaf.swing.com.LeafBusyLabel;
import leaf.swing.com.LeafScrollLabel;
import leaf.util.feed.*;

/**
 * ニュースバーの実装です。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年5月4日
 */
@SuppressWarnings("serial")
public class LeafNewsBar extends JPanel {
	private LeafBusyLabel indicator;
	private boolean isLoading = false;
	private ConfigBar cfgbar;
	private NotBorderedButton bsets, bnews;
	private LeafScrollLabel label;
	private java.util.Timer timer;
	private LeafNewsReader reader;
	private FeedWorker worker;
	private JToolBar box;
	
	/**
	 * ニュースバーを構築します。
	 */
	public LeafNewsBar() {
		this(null);
	}
	
	/**
	 * URLを指定してニュースバーを構築します。
	 * 
	 * @param url 接続先URL
	 */
	public LeafNewsBar(URL url) {
		super();
		setLayout(new BorderLayout());
		init(url);
	}
	
	/**
	 * ニュースバーを初期化します。
	 * 
	 * @param url 接続先URL
	 */
	public void init(URL url) {
		removeAll();
		
		box = new JToolBar();
		box.setFloatable(false);
		box.setLayout(new ToolBarLayout());
		add(box, BorderLayout.CENTER);
		
		bsets = new NotBorderedButton("button_settings");
		bsets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bsets.setEnabled(false);
				cfgbar.expand(true);
			}
		});
		box.add(bsets);
		bnews = new NotBorderedButton("button_news");
		bnews.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				alternate();
			}
		});
		box.add(bnews);
		box.add(label = new LeafScrollLabel());
		label.setScrollCount(1);
		box.add(indicator = new LeafBusyLabel());
		
		cfgbar = new ConfigBar(this);
		add(cfgbar, BorderLayout.NORTH);
		setURL(url);
	}
	
	/**
	 * 接続先のURLを設定します。
	 * 
	 * @param url 接続先URL
	 */
	public void setURL(URL url) {
		if(url == null) reader = null;
		else reader = new LeafNewsReader(url);
		bnews.setEnabled(url != null);
		bsets.setEnabled(true);
		cfgbar.setURL(url);
	}
	
	/**
	 * 接続先のURLを設定します。
	 * 
	 * @param url 接続先URLの文字列
	 * @throws MalformedURLException URLが無効な場合
	 */
	public void setURL(String url) throws MalformedURLException{
		try {
			bsets.setEnabled(true);
			setURL(url != null? new URL(url) : null);
		} catch(MalformedURLException ex) {
			label.setText(ex.getMessage());
			label.start();
			throw ex;
		}
	}
	
	/**
	 * 接続先のURLを返します。
	 * 
	 * @return 接続先URL
	 */
	public URL getURL() {
		if(reader!=null) return reader.getURL();
		else return null;
	}
	
	/**
	 * 接続を開始し、最新のフィードを取得します。
	 */
	public void update() {
		stopScroll();
		startScroll();
		isLoading = true;
	}
	
	private void alternate() {
		if(isLoading ^= true) startScroll();
		else stopScroll();
	}
	
	private void startScroll() {
		if(reader != null) {
			bnews.setText("button_stop");
			bsets.setEnabled(false);
			indicator.start();
			indicator.setVisible(true);
			worker = new FeedWorker(reader);
			worker.execute();
		}
	}
	
	private void stopScroll() {
		if(worker!=null) {
			worker.cancel(true);
			worker = null;
		}
		label.setText("");
		label.stop();
		label.repaint();
	}
	
	class FeedWorker extends SwingWorker<String, String> {
		private final LeafNewsReader reader;
		public FeedWorker(LeafNewsReader reader) {
			this.reader = reader;
		}
		
		@Override
		protected String doInBackground() throws IOException {
			LeafNewsFeed feed = reader.read();
			LeafNewsItem[] items = feed.getItems();
			StringBuilder sb = new StringBuilder();
			sb.append(feed.getTitle());
			for(LeafNewsItem item : items) {
				sb.append("  /  ");
				sb.append(item.getTitle());
			}
			return sb.toString();
		}
		
		@Override
		public void done() {
			indicator.setVisible(false);
			bnews.setText("button_news");
			bsets.setEnabled(true);
			isLoading = false;
			indicator.stop();
			try {
				label.setText(get());
			} catch (InterruptedException ex) {
			} catch (ExecutionException ex) {
				label.setText(ex.getMessage());
			}
			label.start();
			worker = null;
		}
	}
	
	/**
	 * 自動更新を有効化/無効化するための簡易的なメソッドです。
	 * 時刻を詳細に指定する場合は、別途{@link #update()}メソッドと
	 * {@link Timer java.util.Timer}を利用して外部から制御します。
	 * 
	 * @param able 自動更新する場合true デフォルトはfalse
	 * @see #isAutoUpdateEnabled()
	 */
	public void setAutoUpdateEnabled(boolean able) {
		setAutoUpdate(10);
	}
	
	/**
	 * 自動更新が有効であるか返します。
	 * 
	 * @return 自動更新する場合true
	 * @see #setAutoUpdateEnabled(boolean)
	 */
	public boolean isAutoUpdateEnabled() {
		return timer!=null;
	}
	
	private void setAutoUpdate(int min) {
		if(min > 0) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			TimerTask task = new TimerTask() {
				public void run() {
					if(isShowing()) update();
				}
			};
			timer = new java.util.Timer();
			timer.scheduleAtFixedRate(
				task, cal.getTime(), 60000 * min
			);
		} else{
			if(timer != null) timer.cancel();
			timer = null;
		}
	}
	
	private class ToolBarLayout extends BoxLayout {
		public ToolBarLayout() {
			super(box, BoxLayout.X_AXIS);
		}
		
		@Override
		public Dimension maximumLayoutSize(Container target) {
			return target.getMaximumSize();
		}
	}

}