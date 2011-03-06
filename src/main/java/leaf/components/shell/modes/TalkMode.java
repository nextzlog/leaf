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
package leaf.components.shell.modes;

import java.io.File;
import java.io.IOException;

import leaf.components.shell.*;
import leaf.manager.LeafLangManager;
import leaf.media.aquestalk.AquesTalkManager;

/**
*シェル内で実行される合成音声モードの実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年2月11日
*@see LeafShellPane
*/
public class TalkMode implements Mode{
	
	private final AquesTalkManager manager;
	private LeafShellDocument doc;
	
	private File dir;
	
	/**
	*モードを生成します。
	*/
	public TalkMode(){
		this.manager = new AquesTalkManager();
	}
	
	/**
	*モード名を返します。
	*@return モードを表す文字列
	*/
	public String getName(){
		return "talk";
	}
	
	/**
	*このモードに移行します。
	*@param doc 出力先ドキュメント
	*@param dir 作業ディレクトリ
	*/
	public void activate(LeafShellDocument doc, File dir){
		this.doc = doc;
		this.dir = dir;
	}
	
	/**
	*参照先作業ディレクトリを変更します。
	*@param dir 作業ディレクトリ
	*/
	public void setDirectory(File dir){
		this.dir = dir;
	}
	
	/**
	*コマンドを実行します。
	*@param cmd シェルから渡されるコマンド
	*/
	public void execute(String cmd){
		String[] cmds = cmd.split("\\s",2);
		if(cmds.length < 2){
			speak(cmd);
		}else if(cmds[0].equals("phont")){
			loadPhont(cmds[1]);
		}else if(cmds[0].equals("speak")){
			speak(cmds[1]);
		}else if(cmds[0].equals("speed")){
			setSpeed(cmds[1]);
		}else{
			speak(cmd);
		}
	}
	
	/**
	*テキストを読み上げます。
	*@param text 読み上げるテキスト
	*/
	private void speak(String text){
		try{
			manager.speak(text);
		}catch(IOException ex){
			doc.errorln(LeafLangManager.translate(
				" => Failed in talk\n[arg]",
				" => 発声に失敗しました\n[arg]", ex
			));
		}
	}
	
	/**
	*読み上げ速度を設定します。
	*@param cmd 速度
	*/
	private void setSpeed(String cmd){
		try{
			manager.setSpeed(Integer.parseInt(cmd));
		}catch(NumberFormatException ex){
			doc.errorln(LeafLangManager.translate(
				"Illegal Number Format : [arg]",
				"数値が不正です : [arg]", cmd
			));
		}
	}
	
	/**
	*声種データを読み込みます。
	*@param path ファイルパス
	*/
	private void loadPhont(String path){
		File file = new File(dir, path);
		try{
			manager.load(file);
		}catch(IOException ex){
			doc.errorln(LeafLangManager.translate(
				"Failed in loading phont data : [arg]",
				"声種データの読み込みに失敗しました : [arg]", file
			));
			try{
				manager.load(null);
			}catch(Exception e){}
		}
	}
	
	/**
	*モードを終了します。
	*/
	public void exit(){}
	
	/**
	*モードの文字列表現を返します。
	*/
	public String toString(){
		return getName();
	}
	
	/**
	*ヘルプとしてメッセージを表示します。
	*@param cmd ヘルプ対象のコマンド
	*/
	public void showHelp(String cmd){
		if(cmd.equals("phont")){
			doc.appendln(LeafLangManager.get(
				"phont  : load phont data file",
				"phont  : 声種ファイルの読込"
			));
		}else if(cmd.equals("speak")){
			doc.appendln(LeafLangManager.get(
				"speak  : speak the text",
				"speak  : 文章を読み上げ"
			));
		}else if(cmd.equals("speed")){
			doc.appendln(LeafLangManager.get(
				"speed  : set speaking speed",
				"speed  : 読み上げ速度の設定"
			));
		}else{
			doc.appendln(LeafLangManager.get(
				"available commands : phont speak speed",
				"利用できるコマンド : phont speak speed"
			));
		}
	}
}