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
import java.util.Date;

/**
 * フィードに含まれるヘッドラインを表現します。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2011年5月2日
 * 
 */
public class NewsItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title, description;
	private URL link;
	private Date date;
	
	/**
	 * 空のアイテムを生成します。
	 */
	public NewsItem() {}
	
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