/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.shell;

import java.util.EventObject;
import java.util.Locale;

/**
 *シェルのロケール環境が変更される場合に各コマンドに通知されるイベントです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
public class LocaleEvent extends EventObject {
	private final Locale locale;
	
	/**
	 *イベントの発生源とロケールを指定してイベントを生成します。
	 *
	 *@param source イベントの発生源、つまりシェル本体
	 *@param locale 新しく適用されるロケール
	 */
	public LocaleEvent(Object source, Locale locale){
		super(source);
		this.locale = locale;
	}
	/**
	 *イベントが通知するロケールを返します。
	 *
	 *@return 新しく適用されるロケール
	 */
	public Locale getLocale(){
		return locale;
	}
}
