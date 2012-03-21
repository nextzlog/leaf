/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

/**
 * JSR223に基づいて、AriCEエンジンの説明及び
 *インスタンス化に用いられるファクトリです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年7月6日
 */
public final class AriceScriptEngineFactory
implements ScriptEngineFactory{
	
	private static AriceScriptEngineFactory instance;
	private static final HashMap<String, Object> props;
	static{
		instance = new AriceScriptEngineFactory();
		props    = new HashMap<String, Object>();
		props.put("ScriptEngine.ENGINE",           instance.getEngineName()     );
		props.put("ScriptEngine.ENGINE_VERSION",   instance.getEngineVersion()  );
		props.put("ScriptEngine.EXTENSONS",        instance.getExtensions()     );
		props.put("ScriptEngine.NAME",             instance.getNames()          );
		props.put("ScriptEngine.LANGUAGE",         instance.getLanguageName()   );
		props.put("ScriptEngine.LANGUAGE_VERSION", instance.getLanguageVersion());
		props.put("ScriptEngine.MIME_TYPES",       instance.getMimeTypes()      );
	}
	/**
	*ファクトリを新規に生成します。
	*/
	public AriceScriptEngineFactory(){
		instance = this;
	}
	/**
	*ファクトリのインスタンスを返します。
	*@return ファクトリ
	*/
	static AriceScriptEngineFactory getInstance(){
		return instance;
	}
	/**
	*エンジンの完全な名前を返します。
	*@return "Arice Script Engine"
	*/
	@Override public String getEngineName(){
		return "Arice Script Engine";
	}
	/**
	*エンジンのバージョンを返します。
	*@return エンジンの実装のバージョン
	*/
	@Override public String getEngineVersion(){
		return "1.4 euphonium";
	}
	/**
	*エンジンに関連付けられる拡張子のリストを返します。
	*@return ["arice"]
	*/
	@Override public List<String> getExtensions(){
		return Arrays.asList("arice");
	}
	/**
	*言語に関連付けられるMIME typeのリストを返します。
	*@return ["application/arice", "text/arice"]
	*/
	@Override public List<String> getMimeTypes(){
		return Arrays.asList("application/arice", "text/arice");
	}
	/**
	*エンジンを識別するために用いる名前のリストです。
	*@return ["arice", "AriCE"]
	*/
	@Override public List<String> getNames(){
		return Arrays.asList("arice", "AriCE");
	}
	/**
	*エンジンがサポートする言語の名前を返します。
	*@return "AriCE"
	*/
	@Override public String getLanguageName(){
		return "AriCE";
	}
	/**
	*エンジンがサポートする言語のバージョン名を返します。
	*@return AriCEのバージョン名
	*/
	@Override public String getLanguageVersion(){
		return "1.4 euphonium";
	}
	/**
	*指定された属性に対応する値を返します。
	*@param key 属性の名前
	*@return 属性値
	*/
	@Override public Object getParameter(String key){
		return props.get(key);
	}
	/**
	*Javaオブジェクトのメソッドを呼び出すためのAriCE言語の構文を返します。
	*@param obj オブジェクト名
	*@param mth メソッド名
	*@param args メソッドの引数の名前
	*@return AriCE言語のステートメント
	*/
	@Override public String getMethodCallSyntax
	(String obj, String mth, String... args){
		StringBuilder sb = new StringBuilder(obj);
		sb.append(".").append(mth).append("(");
		for(int i=0; i<args.length; i++){
			sb.append(args[i]);
			sb.append((i==args.length-1)? ")" : ",");
		}
		return sb.toString();
	}
	/**
	*標準出力に文字列を表示するためのAriCE言語の構文を返します。
	*@param display 表示する文字列
	*@return AriCE言語のステートメント
	*/
	@Override public String getOutputStatement(String display){
		return "write \"" + display + '\"';
	}
	/**
	*指定された文を含むAriCE言語の完全なプログラムを返します。
	*@param statements AriCE言語のステートメント
	*@return 完全で実行可能なプログラム
	*/
	@Override public String getProgram(String... statements){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<statements.length; i++){
			sb.append(statements[i]).append(";\n");
		}
		return sb.toString();
	}
	/**
	*このファクトリに関連付けられたエンジンのインスタンスを返します。
	*@return エンジン
	*/
	@Override public ScriptEngine getScriptEngine(){
		return new AriceScriptEngine();
	}
}