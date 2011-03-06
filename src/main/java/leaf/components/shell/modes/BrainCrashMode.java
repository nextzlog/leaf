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

import leaf.components.shell.*;
import leaf.manager.LeafLangManager;

/**
*シェル内で実行されるBrainCrash対話モードの実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年2月12日
*@see LeafShellPane
*/
public class BrainCrashMode implements Mode{
	private String code;
	private char[] memory;
	private int mp=0, pc = 0;
	private boolean forRead = false;
	private LeafShellDocument doc;
	private final StringBuilder lines;
	/**
	*BrainCrashインタプリタの生成
	*/
	public BrainCrashMode(){
		lines  = new StringBuilder(100);
		init();
	}
	/**
	*インタプリタを初期化します。
	*/
	private void init(){
		memory = new char[1024];
		lines.delete(0, lines.length());
		mp = pc = 0;
		String hello = "Hello, world!";
		final int length = hello.length();
		for(int i=0;i<memory.length;i++){
			memory[i] = (i < length)?hello.charAt(i) : 0;
		}
	}
	/**
	*ソースコードを指定して実行します。
	*@param code 実行するコード
	*@param input ユーザによって入力された値
	*/
	private void eval(String code, String input){
		if(input!=null)read(input.charAt(0));
		this.code = code;
		int size  = code.length();
		for(;pc<size;pc++){
			switch(code.charAt(pc)){
				case '+':memory[mp]++;break;
				case '-':memory[mp]--;break;
				case '>':mp++;break;
				case '<':mp--;break;
				case '.':write();break;
				case ',':read();pc++;return;
				case '[':goLoopEnd();break;
				case ']':goLoopStart();break;
				case 'l':or();break;
				case '&':and();break;
				case '~':not();break;
				case '^':xor();break;
			}
			if(mp < 0 || mp >= memory.length){
				doc.errorln("Buffer Over Run : " + mp);
				init();
				return;
			}
		}
		show();
	}
	/**
	*ポインタの指す値を出力します。
	*/
	private void write(){
		doc.append(Character.toString(memory[mp]));
	}
	/**
	*read(char)メソッドにつながる入力操作をガイドします。
	*/
	private void read(){
		doc.appendln("please input a value");
		forRead = true;
	}
	/**
	*ポインタの指す位置に値を読み込みます。
	*@param val 読み込む値
	*/
	private void read(char val){
		if(forRead){
			memory[mp] = val;
			forRead = false;
		}
	}
	/**
	*ループの終了までジャンプします。
	*/
	private void goLoopEnd(){
		if(memory[mp]!=0)return;
		int p = pc;
		for(int nest=1;nest>0;){
			switch(code.charAt(++p)){
				case '[': nest++;break;
				case ']': nest--;break;
			}
		}
		pc = p;
    }
	/**
	*ループの開始位置までジャンプします。
	*/
	private void goLoopStart(){
		int p = pc;
		for(int nest=1;nest>0;){
			switch(code.charAt(--p)){
				case '[': nest--;break;
				case ']': nest++;break;
			}
		}
		pc = p-1;
	}
	/**
	*OR演算します。
	*/
	private void or(){
		memory[mp+1] = (char)(((int)memory[mp])|((int)memory[++mp]));
	}
	/**
	*AND演算します。
	*/
	private void and(){
		memory[mp+1] = (char)(((int)memory[mp])&((int)memory[++mp]));
	}
	/**
	*NOT演算します。
	*/
	private void not(){
		memory[mp] = (char)(~(int)memory[mp]);
	}
	/**
	*XOR演算します。
	*/
	private void xor(){
		memory[mp+1] = (char)(((int)memory[mp])^((int)memory[++mp]));
	}
	/**
	*終了時にメモリの中身を出力します。
	*/
	private void show(){
		for(int i=mp;memory[i]!=0;i++){
			doc.append(Character.toString(memory[i]));
		}
		doc.appendln("\nBrainCrash Finished");
	}
	/**
	*モード名を返します。
	*@return モードを表す文字列
	*/
	public String getName(){
		return "braincrash";
	}
	
	/**
	*このモードに移行します。
	*@param doc 出力先ドキュメント
	*@param dir 作業ディレクトリ
	*/
	public void activate(LeafShellDocument doc, File dir){
		this.doc = doc;
	}
	
	/**
	*作業ディレクトリを設定します。
	*@param dir 作業ディレクトリ
	*/
	public void setDirectory(File dir){}
	
	/**
	*コマンドを実行します。
	*@param cmd シェルから渡されるコマンド
	*/
	public void execute(String cmd){
		if(cmd.equals("clear")){
			init();
		}else if(cmd.equals("eval")){
			pc = mp = 0;
			eval(lines.toString(), null);
		}else if(forRead){
			eval(lines.toString(), cmd);
		}else{
			lines.append(cmd);
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
		}else{
			doc.appendln(LeafLangManager.get(
				"available internal commands : clear eval",
				"利用できる内部コマンド : clear eval"
			));
		}
	}
}
