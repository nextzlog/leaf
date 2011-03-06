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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.jar.Attributes;

import leaf.components.shell.*;
import leaf.manager.LeafLangManager;

/**
*シェルのプラグイン拡張のためのローダーモードです。
*シェルにモードを動的に追加する機能を提供します。
*
*追加するモードを格納するJARファイルには、「Shell-Modes」プロパティが
*記述されたマニフェストファイルが格納されている必要があります。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2010年12月12日
*@see LeafShellPane
*/
public class PluginLoaderMode implements Mode{
	
	private final LeafShellPane shell;
	private LeafShellDocument doc;
	private File dir;
	
	/**
	*ローダーモードを生成します。
	*@param shell シェル
	*/
	public PluginLoaderMode(LeafShellPane shell){
		this.shell = shell;
	}
	
	/**
	*モード名を返します。
	*@return モードを表す文字列
	*/
	public String getName(){
		return "load";
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
	public void execute(String cmd){
		doc.append(" => ");
		try{
			load(cmd);
		}catch(IOException ex){}
	}
	
	/**
	*パスをファイルに変換します。
	*@param path パス
	*/
	private File toFile(String path){
		File file = new File(path);
		file = (file.isFile())? file : new File(dir, path);
		return (file.isFile())? file.getAbsoluteFile() : dir;
	}
	
	/**
	*プラグインを読み込みます。
	*@param path プラグインまでのパス
	*@throws IOException 読み込みに失敗した場合
	*/
	private void load(String path) throws IOException{
		load(toFile(path));
	}
	
	/**
	*指定したファイルからプラグインを読み込みます。
	*@param file 読み込み先JARファイル
	*@throws IOException 読み込みに失敗した場合
	*/
	public void load(File file) throws IOException{
		try{
			JarFile jar = new JarFile(file);
			
			URL url = file.toURI().toURL();
			URLClassLoader loader = new URLClassLoader(new URL[]{url});
			
			Attributes attr = jar.getManifest().getMainAttributes();
			String[] names  = attr.getValue("Shell-Modes").split(";");
			
			for(int i=0;i<names.length;i++){
				Object inst = loader.loadClass(names[i]).newInstance();
				
				if(inst instanceof Mode){
					Mode mode = (Mode)inst;
					shell.addMode(mode);
					doc.append(mode.getName() + " ");
				}
			}
			doc.appendln("\n" + names.length + " plugins loaded");
		}catch(IOException ex){
			doc.errorln("\nFailed to load...\n => " + ex);
			throw ex;
		}catch(Exception ex){
			doc.errorln("\nFailed to load...\n => " + ex);
			throw new IOException(ex.getMessage());
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
		doc.appendln(LeafLangManager.get(
			"filename : load plugins",
			"ファイル名 : プラグインをロード"
		));
	}
}
