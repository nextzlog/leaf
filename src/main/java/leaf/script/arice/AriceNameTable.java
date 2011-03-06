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

import java.util.ArrayList;
import java.util.HashMap;
import javax.script.ScriptException;

/**
*AriCE言語の変数と引数の名前空間管理テーブルの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年2月15日
*/
final class AriceNameTable{
	
	private final ArrayList<String> table;
	private final AriceLexAnalyzer analyzer;
	
	private final ArrayList<String> params;
	
	private Scope scope;
	
	/**
	*アナライザと初期サイズを指定してテーブルを生成します。
	*@param analyzer アナライザ
	*@param vars 変数の初期容量
	*@param args 引数の初期容量
	*/
	public AriceNameTable(AriceLexAnalyzer analyzer, int vars, int args){
		this.analyzer = analyzer;
		table  = new ArrayList<String>(vars);
		params = new ArrayList<String>(args);
	}
	/**
	*現在のスコープを親とする新しいスコープを適用します。
	*/
	public void enterChildScope(){
		scope = new Scope(scope);
	}
	/**
	*現在のスコープを抜けて別の新しい子スコープを適用します。
	*/
	public void enterAnotherChildScope(){
		scope = new Scope(scope.parent);
	}
	/**
	*現在のスコープを抜けて親スコープに復帰します。
	*/
	public void exitChildScope(){
		scope = scope.parent;
	}
	/**
	*ルート以下の全てのスコープと引数をクリアします。
	*/
	public void clear(){
		table.clear();
		scope = new Scope(null);
		
		params.clear();
	}
	/**
	*現在のスコープ内で変数を生成します。
	*@param name 変数名
	*@return 変数の登録番号
	*@throws ScriptException 同じスコープで名前が重複した場合
	*/
	public int createVariable(String name) throws ScriptException{
		if(params.contains(name))
			throw error("Parameter " + name + " is already declared.");
		else if(scope.contains(name))
			throw error ("Variable " + name + " is already declared.");
		else{
			return scope.add(name);
		}
	}
	/**
	*指定された名前を持つ変数や引数がスコープ内に存在するか返します。
	*@param name 名前
	*@return 存在する場合true
	*/
	public boolean exists(String name){
		int index = scope.search(name);
		if(index >= 0) return true;
		else return params.contains(name);
	}
	/**
	*指定された名前を持つ変数を現在のスコープ内で検索します。
	*このメソッドは引数検索の後に実行されることを想定しています。
	*@param name 変数名
	*@return 変数の登録番号
	*@throws ScriptException 変数がスコープ内に存在しない場合
	*/
	public int searchVariable(String name) throws ScriptException{
		int index = scope.search(name);
		if (index >= 0) return index;
		else{
			throw error("Variable " + name + " is not declared.");
		}
	}
	/**
	*指定された名前を持つ引数を検索します。
	*@param name 引数名
	*@return 引数の登録番号 存在しない場合-1
	*/
	public int searchParameter(String name){
		int index = params.indexOf(name);
		return (index>=0)?(params.size()-index-1):index;
	}
	/**
	*引数を新規に生成します。
	*@param name 引数名
	*@throws ScriptException 引数が既に存在する場合
	*/
	public void createParameter(String name) throws ScriptException{
		if(params.indexOf(name) >= 0)
			throw error("Parameter " + name + " is already declared.");
		else{
			params.add(name);
		}
	}
	/**
	*現在の関数で登録されている変数の数を返します。
	*@return 変数の登録数
	*/
	public int getVariableCount(){
		return table.size();
	}
	/**
	*現在の関数で登録されている引数の数を返します。
	*@return 引数の登録数
	*/
	public int getParameterCount(){
		return params.size();
	}
	/**
	*構文違反があった場合に例外を通知します。
	*@param msg メッセージ
	*@return 生成した例外
	*/
	private ScriptException error(String msg){
		int line = analyzer.getLineNumber();
		int colm = analyzer.getColumnNumber();
		return new ScriptException(
			msg + " at line : " + line + "\n => " + analyzer.getLine(), null, line, colm
		);
	}
	/**
	*ローカル変数のアクセススコープの実装
	*/
	private final class Scope{
		public  final Scope parent;
		private final HashMap<String, Integer> locals;
		
		/**
		*親スコープを指定してスコープを生成します。
		*@param parent 親となるスコープ
		*/
		public Scope(Scope parent){
			this.parent = parent;
			this.locals = new HashMap<String, Integer>();
		}
		/**
		*スコープ内に変数を追加します。
		*@param name 変数名
		*@return 変数の登録番号
		*/
		public int add(String name){
			int address = table.size();
			locals.put(name,  address);
			table.add(name);
			return  address;
		}
		/**
		*アクセス可能な変数を検索します。
		*@param name 変数名
		*@return 変数の登録番号 アクセス不能な場合-1
		*/
		public int search(String name){
			if(contains(name)) return locals.get(name);
			return (parent == null)? -1 : parent.search(name);
		}
		/**
		*このスコープ内に変数が定義されているか返します。
		*@param name 変数名
		*@return 変数が定義されている場合true
		*/
		public boolean contains(String name){
			return locals.containsKey(name);
		}
	}
}
