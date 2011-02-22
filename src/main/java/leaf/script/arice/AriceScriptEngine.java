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
package leaf.script.arice;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import javax.script.ScriptException;

/**
*AriCE言語のスクリプトエンジンです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月29日
*/
public class AriceScriptEngine{
	
	private Code[] codes;
	private AriceVirtualMachine vm;
	
	/**
	*エンジンを生成します。
	*/
	public AriceScriptEngine(){}
	
	/**
	*スクリプトをコンパイルします。
	*@param script コンパイルするスクリプト
	*@throws ScriptException 構文エラーがあった場合
	*/
	public void compile(String script) throws ScriptException{
		codes = new AriceParser(script).parse();
	}
	
	/**
	*{@link Reader}からスクリプトを読み込んでコンパイルします。
	*@param reader スクリプトを読み込むリーダー
	*@throws IOException 入力エラーがあった場合
	*@throws ScriptException 構文エラーがあった場合
	*/
	public void compile(Reader reader) throws IOException, ScriptException{
		
		BufferedReader br;
		if(reader instanceof BufferedReader)
			br = (BufferedReader)reader;
		else
			br = new BufferedReader(reader);
		
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = br.readLine()) != null){
			sb.append(line);
			sb.append("\n");
		}
		codes = new AriceParser(sb.toString()).parse();
	}
	
	/**
	*コンパイルされた中間言語コードを実行します。
	*@throws ScriptException 実行時エラーがあった場合
	*/
	public void execute() throws ScriptException{
		vm = new AriceVirtualMachine(codes);
		vm.execute();
	}
}
	
