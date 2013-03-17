/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.util.feed;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * リーダーで受信したフィードを表現します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.3 作成：2011年5月2日
 *
 */
public class LeafNewsFeed implements Serializable {
	private static final long serialVersionUID = 1L;
	private URL link;
	private Date date;
	private Locale language;
	private String copyright, description;
	private String generator, title;
	private List<LeafNewsItem> items;
	private Map<String, List<LeafNewsItem>> categories;
	
	/**
	 * 空のフィードを生成します。
	 */
	public LeafNewsFeed() {
		items = new ArrayList<LeafNewsItem>();
		categories = new HashMap<String, List<LeafNewsItem>>();
	}
	
	/**
	 * フィードにタイトルを設定します。
	 * 
	 * @param title フィードのタイトル
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * フィードのタイトルを返します。
	 * 
	 * @return フィードのタイトル
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * フィードの内容を説明する文字列を設定します。
	 * 
	 * @param description フィードの説明
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * フィードの内容を説明する文字列を返します。
	 * 
	 * @return フィードの説明
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * フィードに参照先リンクを設定します。
	 * 
	 * @param link リンク
	 */
	public void setLink(URL link) {
		this.link = link;
	}
	
	/**
	 * フィードの参照先リンクを返します。
	 * 
	 * @return リンク
	 */
	public URL getLink() {
		return link;
	}
	
	/**
	 * フィードが発行された日時を設定します。
	 * 
	 * @param date 発行日時
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * フィードの発行された日時を返します。
	 * 
	 * @return 発行日時
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * フィードを生成したソフトウェアの名前を設定します。
	 * 
	 * @param generator ジェネレータの名前
	 */
	public void setGenerator(String generator) {
		this.generator = generator;
	}
	
	/**
	 * フィードを生成したソフトウェアの名前を返します。
	 * 
	 * @return ジェネレータの名前
	 */
	public String getGenerator() {
		return generator;
	}
	
	/**
	 * フィードに著作権表示を設定します。
	 * 
	 * @param copyright 著作権表示
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
	/**
	 * フィードの著作権表示を返します。
	 * 
	 * @return 著作権表示
	 */
	public String getCopyright() {
		return copyright;
	}
	
	/**
	 * フィードに表示言語を設定します。
	 * 
	 * @param language フィードの言語
	 */
	public void setLanguage(Locale language) {
		this.language = language;
	}
	
	/**
	 * フィードの表示言語を返します。
	 * 
	 * @return フィードの言語
	 */
	public Locale getLanguage() {
		return language;
	}
	
	/**
	 * カテゴリを指定しないでフィードにアイテムを追加します。
	 * 
	 * @param item 追加するアイテム
	 */
	public void addItem(LeafNewsItem item) {
		if(!items.contains(item))items.add(item);
	}
	
	/**
	 * 指定されたカテゴリでフィードにアイテムを追加します。
	 * 
	 * @param category アイテムのカテゴリ
	 * @param item 追加するアイテム
	 */
	public void addItem(String category, LeafNewsItem item) {
		addItem(item);
		if(!categories.containsKey(category)) {
			categories.put(category, new ArrayList<LeafNewsItem>());
		}
		categories.get(category).add(item);
	}
	
	/**
	 * 全てのカテゴリのアイテムを返します。
	 * 
	 * @return アイテムの一覧
	 */
	public LeafNewsItem[] getItems() {
		return items.toArray(new LeafNewsItem[0]);
	}
	
	/**
	 * 指定したカテゴリのアイテムを返します。
	 * 
	 * @param category アイテムのカテゴリ
	 * @return 該当するアイテムの一覧
	 */
	public LeafNewsItem[] getItems(String category) {
		return categories.get(category).toArray(new LeafNewsItem[0]);
	}
	
	/**
	 * フィードに含まれる全てのカテゴリを返します。
	 * 
	 * @return カテゴリの一覧
	 */
	public String[] getCategories() {
		return categories.keySet().toArray(new String[0]);
	}
	
	/**
	 * フィードの文字列化表現を返します。
	 * 
	 * @return 文字列化表現
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(title);
		for(String category : categories.keySet()) {
			sb.append(" / ").append(category);
		}
		return sb.toString();
	}

}