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
import javax.script.ScriptException;

/**
*AriCE言語の関数テーブルの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2011年1月28日
*/
final class AriceFunctionTable{
	
	private final ArrayList<Function> table;
	private final AriceLexAnalyzer analyzer;
	
	private Function current; //現在定義中の関数
	
	/**
	*アナライザと初期サイズを指定してテーブルを生成します。
	*@param analyzer アナライザ
	*@param size 初期容量
	*/
	public AriceFunctionTable(AriceLexAnalyzer analyzer, int size){
		this.analyzer = analyzer;
		table  = new ArrayList<Function>(size);
	}
	/**
	*関数をテーブルに登録します。
	*@param name 登録する関数名
	*@param params 引数の個数
	*/
	public void add(Token name, int params){
		for(Function func : table){
			if(func.name.equals(name) && func.params == params){
				return;
			}
		}
		table.add(new Function(name, params));
	}
	/**
	*定義を開始する関数をテーブルに登録します。
	*@param name 登録する関数名
	*@param params 引数の個数
	*@throws ScriptException 既に登録されている場合
	*/
	public void define(Token name, int params) throws ScriptException{
		for(Function func : table){
			if(func.name.equals(name) && func.params == params){
				if(func.isDefined()){
					throw error(func.name+"("+func.params+") is already defined.",func);
				}else{
					(current = func).setDefined();
					return;
				}
			}
		}
		current = new Function(name, params);
		current.setDefined();
		table.add(current);
	}
	/**
	*現在定義動作中の関数の定義を完了します。
	*/
	public void endDefine(){
		current = null;
	}
	/**
	*現在定義動作中の関数名を返します。
	*@return 関数名
	*/
	public String getFunctionName(){
		return current.name.toString();
	}
	/**
	*現在関数の定義動作中かどうか返します。
	*@return 定義動作中はtrue
	*/
	public boolean isFunctionDefining(){
		return (current != null);
	}
	/**
	*関数の最終確認を行い、関数が全て定義済みかどうか確認します。
	*@throws ScriptException 定義済みでない関数がある場合
	*/
	public void checkAllDefined() throws ScriptException{
		for(Function func : table){
			if(!func.isDefined()){
				throw error(func.name+"("+func.params+") is not defined.",func);
			}
		}
	}
	/**
	*関数テーブルをクリアします。
	*/
	public void clear(){
		table.clear();
		current = null;
	}
	/**
	*構文違反があった場合に例外を通知します。
	*@param msg メッセージ
	*@param func 問題のある関数
	*@return 生成した例外
	*/
	private ScriptException error(String msg, Function func){
		int line = func.line, colm = func.column;
		return new ScriptException(
			msg + " at line : " + line + "\n => " + func.description, null, line, colm
		);
	}
	/**
	*テーブル上の関数データの実装
	*/
	public class Function{
		public final Token name;
		public final int params, line, column;
		public String description;
		
		private boolean isDefined = false;
		
		/**
		*関数名と引数の個数を指定して関数データを生成します。
		*@param name 関数名のトークン
		*@param params 引数の個数
		*/
		public Function(Token name, int params){
			this.name   = name;
			this.params = params;
			this.line   = analyzer.getLineNumber();
			this.column = analyzer.getColumnNumber();
			description = analyzer.getLine();
		}
		/**
		*関数の定義が行われたときに実行します。
		*/
		public void setDefined(){
			this.isDefined = true;
		}
		/**
		*関数が定義済みかどうか返します。
		*@return 定義済みの場合true
		*/
		public boolean isDefined(){
			return isDefined;
		}
	}
}