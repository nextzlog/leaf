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
import java.io.FileReader;

import leaf.components.shell.*;
import leaf.manager.LeafLangManager;
import leaf.script.arice.AriceScriptEngine;

/**
*シェル内で実行されるAriCE対話モードの実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2010年12月5日
*@see LeafShellPane
*/
public class AriceMode implements Mode{
	
	private final AriceScriptEngine engine;
	private LeafShellDocument doc;
	private StringBuilder script;
	
	private File dir;
	
	/**
	*モードを生成します。
	*/
	public AriceMode(){
		engine = new AriceScriptEngine();
	}
	
	/**
	*モード名を返します。
	*@return モードを表す文字列
	*/
	public String getName(){
		return "arice";
	}
	
	/**
	*このモードに移行します。ソースファイル名を指定すると
	*作業ディレクトリ内から自動で読み込んで実行します。
	*@param doc 出力先ドキュメント
	*@param dir 作業ディレクトリ
	*/
	public void activate(LeafShellDocument doc, File dir){
		this.doc   = doc;
		this.dir   = dir;
		this.script = new StringBuilder(200);
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
		if(!cmd.equals("eval")){
			if(cmd.equals("clear")){
				script.delete(0, script.length());
			}else if(cmd.startsWith("include")){
				String[] cmds = cmd.split("\\s",2);
				if(cmds.length>=2)include(cmds[1]);
			}else{
				script.append(cmd);
				script.append("\n");
			}
		}else{
			try{
				doc.appendln(" => " + engine.eval(script.toString()));
			}catch(Exception ex){
				doc.errorln(LeafLangManager.translate(
					"Failed to run\n[arg]",
					"実行に失敗しました\n[arg]", ex
				));
			}
		}
	}
	/**
	*スクリプトファイルをインクルードします。
	*@param path ファイルパス
	*/
	private void include(String path){
		if(path != null){
			try{
				engine.include(new FileReader(new File(dir, path)));
			}catch(Exception ex){
				doc.errorln(LeafLangManager.translate(
					"Failed to include\n[arg]",
					"インクルードに失敗しました\n[arg]", ex
				));
			}
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
		if(cmd.equals("clear")){
			doc.appendln(LeafLangManager.get(
				"clear   : clear source codes",
				"clear   : ソースコードを消去"
			));
		}else if(cmd.equals("eval")){
			doc.appendln(LeafLangManager.get(
				"eval    : evaluate source codes",
				"eval    : ソースコードを実行"
			));
		}else if(cmd.equals("include")){
			doc.appendln(LeafLangManager.get(
				"include : include a source file",
				"include : ソースファイルを読込む"
			));
		}else{
			doc.appendln(LeafLangManager.get(
				"available internal commands : clear eval include",
				"利用できる内部コマンド : clear eval include"
			));
		}
	}
}