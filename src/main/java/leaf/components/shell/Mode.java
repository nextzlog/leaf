/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.components.shell;

import java.io.File;

/**
*シェル内部で実行される各「モード」の基底となるインターフェースです。
*シェルはコマンドを読み取る際、まず内部コマンドを参照し、該当するもの
*がない場合、次にモード名を検索して該当するモードに移行します。このため、
*全てのモード名は必ずモードを呼び出すコマンド文字列でなければなりません。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2010年12月5日
*@see LeafShellPane
*/
public interface Mode{
	
	/**
	*モード名を返します。
	*@return モードを表す文字列
	*/
	public String getName();
	
	/**
	*このモードに移行します。
	*@param doc 出力先ドキュメント
	*@param dir 作業ディレクトリ
	*/
	public void activate(LeafShellDocument doc, File dir);
	
	/**
	*作業ディレクトリを設定します。
	*@param dir 作業ディレクトリ
	*/
	public void setDirectory(File dir);
	
	/**
	*コマンドを実行します。
	*@param cmd シェルから渡されるコマンド
	*/
	public void execute(String cmd);
	
	/**
	*モードを終了します。
	*/
	public void exit();
	
	/**
	*モードの文字列表現を返します。
	*/
	public String toString();
	
	/**
	*ヘルプとしてメッセージを表示します。
	*@param cmd ヘルプ対象のコマンド
	*/
	public void showHelp(String cmd);
}