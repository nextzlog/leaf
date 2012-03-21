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
import java.util.HashMap;
import javax.script.ScriptException;

import leaf.manager.LeafLocalizeManager;

/**
 *AriCE構文解析器で変数と引数の名前を管理するテーブルです。
 *関数スコープとその内部のブロックスコープが実装されます。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.2 作成：2011年2月15日
 */
final class AriceNameTable{
	
	private final AriceLexAnalyzer analyzer;
	private FunctionScope scope = null;
	
	public static final boolean BLOCK_SCOPE = false;
	public static final boolean FUNCTION_SCOPE = true;
	
	private final LeafLocalizeManager localize;
	
	/**
	*字句解析器を指定してテーブルを構築します。
	*@param analyzer 字句解析器
	*/
	public AriceNameTable(AriceLexAnalyzer analyzer){
		this.analyzer = analyzer;
		localize = LeafLocalizeManager.getInstance(getClass());
	}
	/**
	*テーブルを初期化します。
	*/
	public void clear(){
		scope = null;
	}
	/**
	*現在のスコープ内に新しいスコープを構築します。
	*@param kind 種別  BLOCK_SCOPE / FUNCTION_SCOPE
	*/
	public void enterChildScope(boolean kind){
		if(kind == FUNCTION_SCOPE){
			scope = new FunctionScope(scope);
		}else  scope.createChildBlockScope();
	}
	/**
	*現在のスコープを脱出して親スコープに復帰します。
	*/
	public void exitChildScope(){
		if(scope.block.parent == null){
			scope = scope.parent;
			//大域関数の外部ではnull
		}else{
			scope.block = scope.block.parent;
		}
	}
	/**
	*スコープ内に変数を追加します。
	*@param name 変数名
	*@return 変数のアドレス
	*@throws ScriptException 名前の衝突時
	*/
	public Address createLocal(Token name) throws ScriptException{
		if(scope.isDuplicate(name.toString())){
			throw error("createLocal_exception", name);
		}
		return scope.addLocal(name.toString());
	}
	/**
	*スコープ内に引数を追加します。
	*@param name 引数名
	*@throws ScriptExceotion 引数名が衝突する場合
	*/
	public void createParam(Token name) throws ScriptException{
		if(scope.isDuplicate(name.toString())){
			throw error("createParam_exception", name);
		}
		scope.addParam(name.toString());
	}
	/**
	*指定された名前の変数または引数が存在するか返します。
	*@param name 変数名/引数名
	*@return 存在する場合true
	*/
	public boolean exists(Token name) throws ScriptException{
		if(scope.searchParam(name.toString())!=null) return true;
		return (scope.searchLocal(name.toString())!=null);
	}
	/**
	*指定された名前の変数または引数を返します。
	*@param name 変数名/引数名
	*@return アドレス
	*@throws ScriptException 変数/引数が存在しない場合
	*/
	public Address search(Token name) throws ScriptException{
		Address addr = scope.searchParam(name.toString());
		if(addr != null) return addr;
		addr = scope.searchLocal(name.toString());
		if(addr != null) return addr;
		throw error("search_exception", name);
	}
	/**
	*現在の関数スコープ内に登録された変数の個数を返します。
	*@return 宣言された変数の個数
	*/
	public int getLocalCount(){
		return scope.getLocalCount();
	}
	/**
	*現在の関数スコープ内に登録された引数の個数を返します。
	*@return 宣言された引数の個数
	*/
	public int getParamCount(){
		return scope.getParamCount();
	}
	/**
	*構文違反があった場合に例外を通知します。
	*@param key メッセージの国際化キー
	*@param args メッセージの引数
	*@return 生成した例外
	*/
	private ScriptException error(String key, Object... args){
		int line = analyzer.getLineNumber();
		int colm = analyzer.getColumnNumber();
		String msg = localize.translate(key, args);
		return new ScriptException(
			msg + " at line : " + line + "\n => " +
			analyzer.getLine(), null, line, colm
		);
	}
	/**
	*変数や引数のアドレスの実装
	*/
	public final class Address{
		public final int nest, index;
		public final boolean isParam;
		/**
		*返り値用にローカル変数のアドレスを生成します。
		*@param nest 局所関数のネスト階数
		*@param index 関数フレーム内の位置
		*@param isParam 引数/変数の指定
		*/
		Address(int nest, int index, boolean isParam){
			this.nest  = nest;
			this.index = index;
			this.isParam = isParam;
		}
	}
	/**
	*局所関数のアクセススコープの実装
	*/
	private final class FunctionScope{
		private final ArrayList<String> params;
		public  final FunctionScope parent;
		public  BlockScope block;
		private final int nest;
		private int localCount = 0;
		
		/**
		*親スコープを指定してスコープを生成します。
		*@param parent 親となるスコープ
		*/
		public FunctionScope(FunctionScope parent){
			this.parent = parent;
			params = new ArrayList<String>();
			block = new BlockScope(null);
			nest = (parent != null)? parent.nest+1 : 0;
		}
		/**
		*スコープ内に変数を追加します。
		*@param name 変数名
		*@return 変数の登録番号
		*/
		public Address addLocal(String name){
			return new Address(0, block.add(name), false);
		}
		/**
		*スコープ内に引数を追加します。
		*@param name 引数名
		*@return 引数の登録番号
		*/
		public Address addParam(String name){
			params.add(name);
			return new Address(0, params.size()-1, true);
		}
		/**
		*アクセス可能な変数を検索します。
		*@param name 変数名
		*@return 変数の登録番号 アクセス不能な場合null
		*/
		public Address searchLocal(String name){
			return searchLocal(name, 0);
		}
		private Address searchLocal(String name, int nest){
			int index = block.search(name);
			if(index >= 0) return new Address(nest, index, false);
			//エンクロージャ内検索
			if(parent == null) return null;
			return parent.searchLocal(name, nest+1);
		}
		/**
		*アクセス可能な引数を検索します。
		*@param name 引数名
		*@return 引数のアドレス アクセス不能な場合null
		*/
		public Address searchParam(String name){
			int index = params.indexOf(name);
			if(index >= 0){
				return new Address(0, index, true);
			}
			return null; //親関数の引数は参照禁止
		}
		/**
		*指定した名前が既に使用されているか返します。
		*@param name 変数名/引数名
		*@return 既に定義済みの場合true
		*/
		public boolean isDuplicate(String name){
			if(params.contains(name)) return true;
			if( block.contains(name)) return true;
			//エンクロージャ内検索
			if(parent == null) return false;
			return parent.isDuplicate(name);
		}
		/**
		*変数の個数を返します。
		*@return 登録済みの変数の個数
		*/
		public int getLocalCount(){
			return localCount;
		}
		/**
		*引数の個数を返します。
		*@return 登録済みの引数の個数
		*/
		public int getParamCount(){
			return params.size();
		}
		/**
		*現在のスコープの内部にブロックスコープを構築します。
		*@return 構築したスコープ
		*/
		public BlockScope createChildBlockScope(){
			return (block = new BlockScope(block));
		}
		/**
		*ブロック文のアクセススコープの実装
		*/
		public final class BlockScope{
			public  final BlockScope parent;
			private final HashMap<String, Integer> locals;
			
			/**
			*親スコープを指定してスコープを生成します。
			*@param parent 親となるスコープ
			*/
			public BlockScope(BlockScope parent){
				this.parent = parent;
				locals = new HashMap<String, Integer>();
			}
			/**
			*スコープ内に変数を追加します。
			*@param name 変数名
			*@return 変数の登録番号
			*/
			public int add(String name){
				int address = localCount++;
				locals.put(name,  address);
				return  address;
			}
			/**
			*アクセス可能な変数を検索します。
			*@param name 変数名
			*@return 変数の登録番号 アクセス不能な場合-1
			*/
			public int search(String name){
				if(contains(name)) return locals.get(name);
				//親ブロック内検索
				if(parent == null) return -1;
				return parent.search(name);
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
}
