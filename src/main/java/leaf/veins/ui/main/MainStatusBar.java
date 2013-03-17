/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.main;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.*;

import leaf.util.hibernate.Properties;
import leaf.veins.util.GoogleUtils;
import leaf.swing.news.LeafNewsBar;

/**
 * メインウィンドウ最下部に表示されるステータスバーです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 */
@SuppressWarnings("serial")
public final class MainStatusBar extends StatusBar {
	private final Properties properties;
	
	private final JLabel[] labels;
	private final LeafNewsBar bar;
	
	public static final int STATUS_LEFT = 0;
	public static final int STATUS_RIGHT= 1;
	
	private static final String NewsBarURL = "newsBarURL";
	private static final String defaultURL = GoogleUtils.getNewsURL();
	
	/**
	 * ステータスバーを構築します。
	 */
	public MainStatusBar(){
		properties = Properties.getInstance(getClass());
		
		setGlue(bar = new LeafNewsBar());
		bar.setAutoUpdateEnabled(true);
		bar.setURL(loadNewsBarURL());
		
		labels = new JLabel[2];
		addComp(labels[0] = new JLabel("", JLabel.CENTER));
		addComp(labels[1] = new JLabel("", JLabel.CENTER));
		
		Dimension dim0 = new Dimension(120, 20);
		Dimension dim1 = new Dimension( 80, 20);
		
		labels[0].setMaximumSize(dim0);
		labels[1].setMaximumSize(dim1);
		labels[0].setPreferredSize(dim0);
		labels[1].setPreferredSize(dim1);
	}
	
	/**
	 * ステータスバーの言語表示を初期化します。
	 */
	public void initialize(){
		if(bar != null) bar.init(bar.getURL());
	}
	
	/**
	 * ステータスバーに表示される文字列を変更します。
	 *
	 * @param text 表示文字列
	 * @param index ラベルの指定
	 */
	public void setText(String text, int index){
		labels[index].setText(text);
	}
	
	/**
	 * ステータスバーに表示される文字列を返します。
	 *
	 * @param index ラベルの指定
	 * @return ラベルのテキスト
	 */
	public String getText(int index){
		return labels[index].getText();
	}
	
	/**
	 * ニュースバーの接続先URLを設定します。
	 * @param url 接続先URL
	 */
	public void setNewsBarURL(URL url){
		bar.setURL(url);
		properties.put(NewsBarURL, url);
	}
	
	/**
	 * ニュースバーの接続先URLを返します。
	 * @return 接続先URL
	 */
	public URL getNewsBarURL(){
		return bar.getURL();
	}
	
	/**
	 * ニュースバーの接続先URLを取得します。
	 * @return 接続先URL
	 */
	private URL loadNewsBarURL(){
		try {
			return new URL(properties.get(NewsBarURL, defaultURL));
		} catch (MalformedURLException ex) {
			return null;
		}
	}
	
	/**
	 * ニュースバーの接続先URLを保存します。
	 */
	public void saveNewsBarURL(){
		properties.put(NewsBarURL, String.valueOf(getNewsBarURL()));
	}

}
