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
import java.util.Date;

/**
 * フィードに含まれるヘッドラインを表現します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.3 作成：2011年5月2日
 *
 */
public class LeafNewsItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title, description;
	private URL link;
	private Date date;
	
	/**
	 * 空のアイテムを生成します。
	 */
	public LeafNewsItem() {}
	
	/**
	 * アイテムのタイトルを設定します。
	 * 
	 * @param title タイトル
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * アイテムのタイトルを返します。
	 * 
	 * @return タイトル
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * アイテムの説明を設定します。
	 * 
	 * @param desc 説明
	 */
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	/**
	 * アイテムの説明を返します。
	 * 
	 * @return 説明
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * アイテムのリンクを設定します。
	 * 
	 * @param link リンク
	 */
	public void setLink(URL link) {
		this.link = link;
	}
	
	/**
	 * アイテムのリンクを返します。
	 * 
	 * @return リンク
	 */
	public URL getLink() {
		return link;
	}
	
	/**
	 * アイテムの発行日付を設定します。
	 * 
	 * @param date 日付
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * アイテムの発行日付を返します。
	 * 
	 * @return 日付
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * アイテムの文字列化表現を返します。
	 * 
	 * @return アイテムを表す文字列
	 */
	public String toString() {
		return title;
	}

}