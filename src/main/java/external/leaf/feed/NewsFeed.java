/**********************************************************************************
 * leaf - Java Library for pafelog
 * Copyright(c) 2010 - 2013 by University of Tokyo Amateur Radio Club
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL) as published by the
 * Free Software Foundation, either version 3 of the License, or(at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package leaf.feed;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import leaf.feed.NewsItem;

/**
 * リーダーで受信したフィードを表現します。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2011年5月2日
 * 
 */
public class NewsFeed implements Serializable {
	private static final long serialVersionUID = 1L;
	private URL link;
	private Date date;
	private Locale language;
	private String copyright, description;
	private String generator, title;
	private List<NewsItem> items;
	private Map<String, List<NewsItem>> categories;
	
	/**
	 * 空のフィードを生成します。
	 */
	public NewsFeed() {
		items = new ArrayList<NewsItem>();
		categories = new HashMap<String, List<NewsItem>>();
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
	public void addItem(NewsItem item) {
		if(!items.contains(item))items.add(item);
	}
	
	/**
	 * 指定されたカテゴリでフィードにアイテムを追加します。
	 * 
	 * @param category アイテムのカテゴリ
	 * @param item 追加するアイテム
	 */
	public void addItem(String category, NewsItem item) {
		addItem(item);
		if(!categories.containsKey(category)) {
			categories.put(category, new ArrayList<NewsItem>());
		}
		categories.get(category).add(item);
	}
	
	/**
	 * 全てのカテゴリのアイテムを返します。
	 * 
	 * @return アイテムの一覧
	 */
	public NewsItem[] getItems() {
		return items.toArray(new NewsItem[0]);
	}
	
	/**
	 * 指定したカテゴリのアイテムを返します。
	 * 
	 * @param category アイテムのカテゴリ
	 * @return 該当するアイテムの一覧
	 */
	public NewsItem[] getItems(String category) {
		return categories.get(category).toArray(new NewsItem[0]);
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