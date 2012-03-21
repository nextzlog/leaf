/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.arice;

import java.util.ArrayList;
import javax.script.ScriptException;

import leaf.manager.LeafLocalizeManager;

/**
 *ArkCE構文解析器で大域関数を管理するテーブルです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.1 作成：2011年1月28日
 */
final class AriceFunctionTable {
	
	private final ArrayList<Function> table;
	private final AriceLexAnalyzer analyzer;
	private Function current; //現在定義中の関数
	private final LeafLocalizeManager localize;
	
	/**
	*字句解析器を指定してテーブルを構築します。
	*@param analyzer 字句解析器
	*/
	public AriceFunctionTable(AriceLexAnalyzer analyzer){
		this.analyzer = analyzer;
		table = new ArrayList<Function>();
		localize = LeafLocalizeManager.getInstance(getClass());
	}
	/**
	*関数をテーブルに登録します。
	*@param name 関数名
	*@param pars 引数の個数
	*/
	public void add(Token name, int pars){
		for(Function func : table){
			if(func.name.equals(name)
			&& func.params == pars){
				return;
			}
		}
		table.add(new Function(name, pars));
	}
	/**
	*定義を開始する関数をテーブルに登録します。
	*@param name 関数名
	*@param pars 引数の個数
	*@throws ScriptException 既に登録されている場合
	*/
	public void define(Token name, int pars) throws ScriptException{
		for(Function func : table){
			if(!func.name.equals(name)
			|| func.params != pars) continue;
			if(!func.isDefined()){
				(current = func).setDefined();
				return;
			}
			throw error("define_exception", func);
		}
		current = new Function(name, pars);
		current.setDefined();
		table.add(current);
	}
	/**
	*現在定義動作中の関数の定義を終了します。
	*/
	public void endDefine(){
		current = null;
	}
	/**
	*現在関数の定義動作中であるか返します。
	*@return 定義動作中ならtrue
	*/
	public boolean isFunctionDefining(){
		return (current != null);
	}
	/**
	*全ての登録済み関数が定義済みであるか確認します。
	*@throws ScriptException 定義済みでない関数がある場合
	*/
	public void checkAllDefined() throws ScriptException{
		ArrayList<Function> errors = new ArrayList<Function>();
		for(Function func : table){
			if(!func.isDefined()) errors.add(func);
		}
		if(errors.size() == 0) return;
		Function[] funcs = errors.toArray(new Function[0]);
		throw error("checkAllDefined_exception", funcs);
	}
	/**
	*関数テーブルを初期化します。
	*/
	public void clear(){
		table.clear();
		current = null;
	}
	/**
	*構文違反があった場合に例外を生成します。
	*@param key メッセージの国際化キー
	*@param errs 問題のある関数の列挙
	*@return 生成した例外
	*/
	private ScriptException error(String key, Function... errs){
		int line = errs[0].line;
		int colm = errs[0].column;
		StringBuilder en = new StringBuilder();
		for(Function func : errs){
			en.append(func).append(" ");
		}
		StringBuilder desc = new StringBuilder();
		desc.append(localize.translate(key, en.toString()));
		desc.append(" at line : ").append(line);
		desc.append("\n => ").append(errs[0].description);
		return new ScriptException(desc.toString(), null, line, colm);
	}
	/**
	*テーブル上の関数登録情報の実装
	*/
	public class Function{
		public final Token name;
		public final int params, line, column;
		public String description;
		
		private boolean isDefined = false;
		
		/**
		*関数名と引数の個数を指定して関数情報を生成します。
		*@param name 関数名のトークン
		*@param pars 引数の個数
		*/
		public Function(Token name, int pars){
			this.name   = name;
			this.params = pars;
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
		/**
		*文字列化表現を返します。
		*/
		public String toString(){
			return name + "(" + params + ")";
		}
	}
}
