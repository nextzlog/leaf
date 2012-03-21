/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.shell.build;

import leaf.shell.LeafShell;

import java.io.IOException;
import java.io.InputStream;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/**
 *ビルド文書を解析するビルダーのフロントエンドです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年12月11日
 */
public final class LeafMenuBuilder{
	private final LeafShell shell;
	/**
	 *シェルを指定してビルダを構築します。
	 *
	 *@param shell 関連付けられるシェル
	 */
	public LeafMenuBuilder(LeafShell shell){
		this.shell = shell;
	}
	/**
	 *ビルド対象とビルド文書を読み込むストリームを指定して文書を解析します。
	 *
	 *@param menubar ビルド対象のメニューバー
	 *@param stream ビルド文書を読み込むストリーム
	 *@throws IOException 読み込みに失敗した場合
	 *@throws UnknownNameException 規約違反時
	 */
	public void build(JMenuBar menubar, InputStream stream)
	throws IOException, UnknownNameException{
		new Builder(shell, menubar).build(stream);
	}
	/**
	 *ビルド対象とビルド文書を読み込むストリームを指定して文書を解析します。
	 *
	 *@param popup ビルド対象のポップアップメニュー
	 *@param stream ビルド文書を読み込むストリーム
	 *@throws IOException 読み込みに失敗した場合
	 *@throws UnknownNameException 規約違反時
	 */
	public void build(JPopupMenu popup, InputStream stream)
	throws IOException, UnknownNameException{
		new Builder(shell, popup).build(stream);
	}
	/**
	 *ビルド対象とビルド文書を読み込むストリームを指定して文書を解析します。
	 *
	 *@param toolbar ビルド対象のツールバー
	 *@param stream ビルド文書を読み込むストリーム
	 *@throws IOException 読み込みに失敗した場合
	 *@throws UnknownNameException 規約違反時
	 */
	public void build(JToolBar toolbar, InputStream stream)
	throws IOException, UnknownNameException{
		new Builder(shell, toolbar).build(stream);
	}
}