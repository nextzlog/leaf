/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.shell;

import java.util.EventObject;
import java.util.Locale;

/**
 * シェルのロケールが変更された場合に通知されるイベントです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.3 作成：2011年12月11日
 *
 */
public class LocaleEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private final Locale locale;
	
	/**
	 * イベントの発生源とロケールを指定してイベントを構築します。
	 * 
	 * @param source イベントの発生源、つまりシェル本体
	 * @param locale 新しく適用されるロケール
	 */
	public LocaleEvent(Object source, Locale locale){
		super(source);
		this.locale = locale;
	}
	
	/**
	 * イベントが通知するロケールを返します。
	 * 
	 * @return 新しく適用されるロケール
	 */
	public Locale getLocale(){
		return locale;
	}

}
