/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.util.news;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.TimerTask;
import javax.swing.*;

import leaf.swing.label.LeafBusyLabel;
import leaf.swing.label.LeafScrollLabel;
import leaf.swing.list.LeafScrollList;
import leaf.util.feed.*;

/**
*ニュースバーの実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.3 作成：2011年5月4日
*/
public class LeafNewsBar extends JPanel{
	private LeafBusyLabel indicator;
	private boolean isLoading = false;
	private LeafNewsOptionBar optbar;
	private ExButton bsets, bnews;
	private LeafScrollLabel label;
	private java.util.Timer timer;
	private LeafNewsReader reader;
	private FeedWorker worker;
	private JScrollPane scroll;
	private LeafScrollList list;
	private JToolBar box;
	/**
	*ニュースバーを生成します。
	*/
	public LeafNewsBar(){
		this(null);
	}
	/**
	*URLを指定してニュースバーを生成します。
	*@param url 接続先URL
	*/
	public LeafNewsBar(URL url){
		super();
		setLayout(new BorderLayout());
		init(url);
	}
	/**
	*ニュースバーを初期化します。
	*@param url 接続先URL
	*/
	public void init(URL url){
		removeAll();
		box = new JToolBar();
		box.setFloatable(false);
		add(box, BorderLayout.CENTER);
		bsets = new ExButton("button_settings");
		bsets.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				bsets.setEnabled(false);
				optbar.expand(true);
			}
		});
		box.add(bsets);
		bnews = new ExButton("button_news");
		bnews.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				alternate();
			}
		});
		box.add(bnews);
		box.add(label = new LeafScrollLabel());
		label.setScrollCount(1);
		box.add(indicator = new LeafBusyLabel());
		optbar = new LeafNewsOptionBar(this);
		add(optbar, BorderLayout.NORTH);
		setURL(url);
	}
	/**
	*接続先のURLを設定します。
	*@param url 接続先URL
	*/
	public void setURL(URL url){
		if(url == null) reader = null;
		else reader = new LeafNewsReader(url);
		bnews.setEnabled(url != null);
		bsets.setEnabled(true);
		optbar.setURL(url);
	}
	/**
	*接続先のURLを設定します。
	*@param url 接続先URLの文字列
	*@throws MalformedURLException URLが無効な場合
	*/
	public void setURL(String url)
	throws MalformedURLException{
		try{
			bsets.setEnabled(true);
			setURL(url != null? new URL(url) : null);
		}catch(MalformedURLException ex){
			label.setText(ex.getMessage());
			label.start();
			throw ex;
		}
	}
	/**
	*接続先のURLを返します。
	*@return 接続先URL
	*/
	public URL getURL(){
		if(reader!=null) return reader.getURL();
		else return null;
	}
	/**
	*接続を開始し、最新のフィードを取得します。
	*/
	public void update(){
		stopScroll();
		startScroll();
		isLoading = true;
	}
	/**
	*フィードの表示/非表示を切り替えます。
	*/
	private void alternate(){
		if(isLoading ^= true) startScroll();
		else stopScroll();
	}
	/**
	*フィードをスクロール表示します。
	*/
	private void startScroll(){
		if(reader != null){
			bnews.setText("button_stop");
			bsets.setEnabled(false);
			indicator.start();
			indicator.setVisible(true);
			worker = new FeedWorker(reader);
			worker.execute();
		}
	}
	/**
	*フィードのスクロール表示を停止します。
	*/
	private void stopScroll(){
		if(worker!=null){
			worker.cancel(true);
			worker = null;
		}
		label.setText("");
		label.stop();
		label.repaint();
	}
	/**
	*通信/フィード取得用バックグラウンドタスク
	*/
	private class FeedWorker extends SwingWorker{
		private final LeafNewsReader reader;
		public FeedWorker(LeafNewsReader reader){
			this.reader = reader;
		}
		@Override protected String doInBackground(){
			try{
				LeafNewsFeed feed = reader.read();
				LeafNewsItem[] items = feed.getItems();
				StringBuilder sb = new StringBuilder();
				sb.append(feed.getTitle());
				for(LeafNewsItem item : items){
					sb.append("  /  ");
					sb.append(item.getTitle());
				}
				label.setText(sb.toString());
				if(list != null)
					list.setListData(feed.getItems());
			}catch(Exception ex){
				label.setText(ex.toString());
			}finally{
				label.start();
				worker = null;
				return "Done";
			}
		}
		public void done(){
			indicator.setVisible(false);
			bnews.setText("button_news");
			bsets.setEnabled(true);
			isLoading = false;
			indicator.stop();
		}
	}
	/**
	*自動更新を有効化/無効化するための簡易的なメソッドです。
	*時刻を詳細に指定する場合は、別途{@link #update()}メソッドと
	*{@link Timer java.util.Timer}を利用して外部から制御します。
	*
	*@param able 自動更新する場合true デフォルトはfalse
	*@see #isAutoUpdateEnabled()
	*/
	public void setAutoUpdateEnabled(boolean able){
		setAutoUpdate(10);
	}
	/**
	*自動更新が有効であるか返します。
	*@return 自動更新する場合true
	*@see #setAutoUpdateEnabled(boolean)
	*/
	public boolean isAutoUpdateEnabled(){
		return timer!=null;
	}
	/**
	*毎正時を基準に自動更新の間隔時間を指定します。
	*@param min 分間隔 0以下の場合自動更新無効化
	*/
	private void setAutoUpdate(int min){
		if(min > 0){
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			TimerTask task = new TimerTask(){
				public void run(){
					if(isShowing()) update();
				}
			};
			timer = new java.util.Timer();
			timer.scheduleAtFixedRate(
				task, cal.getTime(), 60000*min
			);
		}else{
			if(timer!=null)timer.cancel();
			timer = null;
		}
	}
	/**
	*フィードのニュース一覧リストを表示するか設定します。
	*実行環境で{@link Desktop Desktop}がサポートされる場合、
	*リストのダブルクリックでブラウザを起動する機能を持ちます。
	*@param s 表示する場合true
	*/
	public void setShowList(boolean s){
		if(list != null && !s){
			remove(scroll);
			scroll = null;
			list = null;
			add(box, BorderLayout.CENTER);
		}else if(list == null && s){
			list = new LeafScrollList();
			scroll = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
			);
			remove(box);
			add(box, BorderLayout.SOUTH);
			add(scroll, BorderLayout.CENTER);
			list.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					if(e.getClickCount() < 2) return;
					if(e.getButton() == MouseEvent.BUTTON1)
					browse((LeafNewsItem)list.getSelectedValue());
				}
			});
		}
	}
	/**
	*フィードのニュース一覧リストを表示するか返します。
	*@return 表示する場合true
	*/
	public boolean isShowList(){
		return list != null;
	}
	/**
	*デスクトップ操作がサポートされる場合に、
	*ニュースのリンクをブラウザで開きます。
	*@param item アイテム
	*/
	private void browse(LeafNewsItem item){
		try{
			URI uri = item.getLink().toURI();
			Desktop.getDesktop().browse(uri); 
		}catch(Exception ex){}
	}
}