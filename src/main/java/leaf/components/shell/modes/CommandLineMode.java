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

import java.io.*;

import leaf.components.shell.*;
import leaf.manager.LeafFileManager;
import leaf.manager.LeafLangManager;

/**
*シェル内部で実行されるコマンドラインモードの実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2010年12月5日
*@see LeafShellPane
*/
public class CommandLineMode implements Mode{
	
	private LeafShellDocument doc;
	private File dir;
	
	/**
	*モード名を返します。
	*@return モードを表す文字列
	*/
	public String getName(){
		return "cmd";
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
	*作業ディレクトリを設定します。
	*@param dir 作業ディレクトリ
	*/
	public void setDirectory(File dir){
		this.dir = dir;
	}
	
	/**
	*コマンドを実行します。
	*@param cmd シェルから渡されるコマンド
	*/
	public void execute(final String cmd){
		String[] cmds = cmd.split("\\s",2);
		if(cmds.length < 2){
			process(cmd);
		}else{
			if(cmds[0].equals("find")){
				search(cmds[1]);
			}else{
				process(cmd);
			}
		}
	}
	/**
	*プロセスを起動します。
	*@param cmd コマンドの文字列
	*/
	private void process(String cmd){
		try{
			String result;
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(cmd, null, dir);
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while((result=br.readLine())!=null){
				doc.appendln(result);
			}
		}catch(Exception ex){
			doc.append(ex + "\n");
		}
	}
	/**
	*ファイル検索コマンドを処理します。
	*@param cmd 検索する中間一致条件
	*/
	private void search(String cmd){
		File[] result = new LeafFileManager().search(dir, cmd);
		doc.appendln(LeafLangManager.translate(
			" => [arg] files hit", " => [arg] 件見つかりました", result.length
		));
		for(File file : result){
			doc.append(file + "\n");
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
		if(cmd.equals("find")){
			doc.appendln(LeafLangManager.get(
				"find : search files by partial name",
				"find : ファイル名を検索します"
			));
		}else{
			doc.appendln(LeafLangManager.get(
				"available internal commands : find",
				"利用できる内部コマンド : find"
			));
		}
	}
}