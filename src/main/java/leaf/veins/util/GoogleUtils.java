/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Googleによる文字列検索を行うためのURIを生成します。また、
 * Google News英語版のURLを返します。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/04/30 
 *
 */
public final class GoogleUtils {
	private static final String template = "http://www.google.com/search?q=";

	public static URI createSearchURI(String query) {
		try {
			return new URI(template + URLEncoder.encode(query, "UTF8"));
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			return null;
		} catch (URISyntaxException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String getNewsURL() {
		return "http://news.google.com/news?cf=all&ned=us&output=atom";
	}
}
