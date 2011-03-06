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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import javax.script.*;

/**
*AriCE言語のスクリプトエンジンです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月29日
*/
public class AriceScriptEngine{
	
	protected static final int STACK_SIZE = 4096;
	protected static final int STEP_MAX   = Integer.MAX_VALUE >> 3;
	
	private Bindings bind;
	
	private final AriceParser parser;
	private final AriceVirtualMachine vm;
	
	private final StringBuilder script;
	private Code[] medcodes;
	
	/**
	*エンジンを生成します。
	*/
	public AriceScriptEngine(){
		bind   = new SimpleBindings();
		parser = new AriceParser();
		vm     = new AriceVirtualMachine();
		script = new StringBuilder(STACK_SIZE*2);
	}
	/**
	*Bindingsを指定してエンジンを生成します。
	*@param bind 使用するBindings
	*/
	public AriceScriptEngine(Bindings bind){
		this.bind = bind;
		parser = new AriceParser();
		vm = new AriceVirtualMachine();
		script = new StringBuilder(STACK_SIZE*2);
	}
	/**
	*スクリプトをコンパイルします。
	*@param script スクリプト
	*@throws ScriptException コンパイルエラー
	*/
	public void compile(String script) throws ScriptException{
		medcodes = parser.compile(this.script + script);
	}
	/**
	*スクリプトをコンパイルします。
	*@param reader スクリプトを読み込むリーダ
	*@throws IOException 読込エラーがあった場合
	*@throws ScriptException コンパイルエラー
	*/
	public void compile(Reader reader) throws IOException, ScriptException{
		BufferedReader breader;
		if(reader instanceof BufferedReader)
			breader = (BufferedReader)reader;
		else
			breader = new BufferedReader(reader);
		
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = breader.readLine()) != null){
			sb.append(line);
			sb.append("\n");
		}
		breader.close();
		compile(sb.toString());
	}
	/**
	*コンパイル済みのスクリプトを実行します。
	*@throws ScriptException 実行エラーがあった場合
	*/
	public Object eval() throws ScriptException{
		return vm.execute(medcodes, bind);
	}
	/**
	*スクリプトをコンパイルして実行します。
	*@param script スクリプト
	*@throws ScriptException 実行エラーがあった場合
	*/
	public Object eval(String script) throws ScriptException{
		return eval(script, bind);
	}
	/**
	*スクリプトをコンパイルして実行します。
	*@param script スクリプト
	*@param bind エンジンに渡されるバインディング
	*@return スクリプトの戻り値
	*@throws ScriptException 実行エラーがあった場合
	*/
	public Object eval(String script, Bindings bind) throws ScriptException{
		compile(script);
		return vm.execute(medcodes, (this.bind = bind));
	}
	/**
	*スクリプトをコンパイルして実行します。
	*@param reader スクリプトを読み込むリーダ
	*@throws IOException 読込エラーがあった場合
	*@throws ScriptException 実行エラーがあった場合
	*/
	public Object eval(Reader reader) throws IOException, ScriptException{
		return eval(reader, bind);
	}
	/**
	*スクリプトをコンパイルして実行します。
	*@param reader スクリプトを読み込むリーダ
	*@param bind エンジンに渡されるバインディング
	*@return スクリプトの戻り値
	*@throws IOException 読込エラーがあった場合
	*@throws ScriptException 実行エラーがあった場合
	*/
	public Object eval(Reader reader, Bindings bind) throws IOException, ScriptException{
		compile(reader);
		return vm.execute(medcodes, (this.bind = bind));
	}
	/**
	*スクリプトファイルをインクルードします。
	*@param reader スクリプトを読み込むリーダ
	*@throws IOException 読込エラーがあった場合
	*/
	public void include(Reader reader) throws IOException{
		BufferedReader breader;
		if(reader instanceof BufferedReader)
			breader = (BufferedReader)reader;
		else
			breader = new BufferedReader(reader);
		
		String line;
		while((line = breader.readLine()) != null){
			script.append(line);
			script.append("\n");
		}
		breader.close();
	}
	/**
	*コンパイル済みの中間言語コードをディスアセンブルして返します。
	*@return ディスアセンブルされたコード
	*/
	public String disassemble(){
		try{
			return new AriceDisassembler().disassemble(medcodes);
		}catch(ScriptException ex){
			return ex.getMessage();
		}
	}
	/**
	*オブジェクトと値のバインディングを返します。
	*@return バインディング
	*/
	public Bindings getBindings(){
		return bind;
	}
	/**
	*オブジェクトと値のバインディングを設定します。
	*@param bind バインディング
	*/
	public void setBindings(Bindings bind){
		this.bind = bind;
	}
}
