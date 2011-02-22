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
package leaf.icon;

import javax.swing.*;

/**
*Leafで用意されたアイコンを簡単に取り出すためのクラスです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年6月12日
*/
public class LeafIcons{
	/**
	*指定した名前のアイコンを取り出します。
	*@param name アイコンのファイル名から拡張子を除いた文字列
	*@return とりだしたImageIcon
	*/
	public ImageIcon getIcon(String name){
		return new ImageIcon(getClass().getResource("images/"+name+".png"));
	}
	/**バックスペース*/
	public static final String BACKSPACE = "backspace";
	/**電卓*/
	public static final String CALC = "calc";
	/**閉じる*/
	public static final String CLOSE = "close";
	/**閉じて開く*/
	public static final String CLOSE_OPEN = "closeopen";
	/**タブを閉じる*/
	public static final String CLOSE_TAB = "closetab";
	/**タブを保存して閉じる*/
	public static final String SAVE_CLOSE_TAB = "closetabsave";
	/**コンソール*/
	public static final String CONSOLE = "console";
	/**コピー*/
	public static final String COPY = "copy";
	/**切り取り*/
	public static final String CUT = "cut";
	/**削除*/
	public static final String DELETE = "delete";
	/**1行削除*/
	public static final String DELETE_LINE = "deleteline";
	/**実行*/
	public static final String EVAL = "eval";
	/**終了*/
	public static final String EXIT = "exit";
	/**展開*/
	public static final String EXPAND = "expand";
	/**検索*/
	public static final String SEARCH = "find";
	/**次を検索*/
	public static final String SEARCH_DOWNWARD = "finddownward";
	/**前を検索*/
	public static final String SEARCH_UPWARD = "findupward";
	/**収納*/
	public static final String FOLD = "fold";
	/**ヘルプ*/
	public static final String HELP = "help";
	/**GREP*/
	public static final String GREP = "grep";
	/**ループ*/
	public static final String LOOP = "loop";
	/**新規作成*/
	public static final String NEW = "new";
	/**開く*/
	public static final String OPEN = "open";
	/**貼り付け*/
	public static final String PASTE = "paste";
	/**再生/一時停止*/
	public static final String PLAY = "play";
	/**印刷*/
	public static final String PRINT = "print";
	/**ファイルから読み込む*/
	public static final String READIN = "readin";
	/**リードミー*/
	public static final String README = "readme";
	/**やり直し*/
	public static final String REDO = "redo";
	/**開きなおす*/
	public static final String REOPEN = "reopen";
	/**置換*/
	public static final String REPLACE = "replace";
	/**保存*/
	public static final String SAVE = "save";
	/**全て保存*/
	public static final String SAVE_ALL = "saveall";
	/**名前を付けて保存*/
	public static final String SAVE_AS = "saveas";
	/**全て選択*/
	public static final String SELECT_ALL = "selectall";
	/**1行選択*/
	public static final String SELECT_LINE = "selectline";
	/**画面分割*/
	public static final String SEPARATE = "separate";
	/**停止*/
	public static final String STOP = "stop";
	/**元に戻す*/
	public static final String UNDO = "undo";
	/**バージョン*/
	public static final String VERSION = "version";
	/**Leafのロゴ*/
	public static final String LOGO = "welcome";
	/**ファイルに書き出す*/
	public static final String WRITEOUT = "writeout";
}
