/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium;

import leaf.script.common.util.Code;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.script.*;

import static javax.script.ScriptContext.*;

/**
 *AriCE処理系本体の実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.1 作成：2010年9月29日
 */
public final class AriceScriptEngine
 extends AbstractScriptEngine implements Compilable{
	
 	protected static final int FRAME_NEST_MAX = 1024;
 	protected static final int STACK_SIZE_MAX = 4096;
 	protected static final int EXECUTE_STEP_MAX = Integer.MAX_VALUE >> 3;
	
	private final Parser parser;
	private final VirtualMachine vm;
	
	/**
	*エンジンを構築します。
	*/
	public AriceScriptEngine(){
		super();
		parser = new Parser();
		vm     = new VirtualMachine();
	}
	/**
	*エンジンスコープのBindingsを指定してエンジンを構築します。
	*@param bind 関連付けるBindings
	*/
	public AriceScriptEngine(Bindings bind){
		super(bind);
		parser = new Parser();
		vm     = new VirtualMachine();
	}
 	/**
 	*新しいBindingsを生成して返します。
 	*@return 新規のBindings
 	*/
 	@Override
 	public Bindings createBindings(){
 		return new SimpleBindings();
 	}
	/**
	*スクリプトをコンパイルします。
 	*@param reader スクリプトのソース
	*@return コンパイルされたスクリプト
 	*@throws ScriptException コンパイルに失敗した場合
	*/
 	@Override
 	public CompiledScript compile(Reader reader)
 	throws ScriptException{
 		try{
 			return new AriceCompiledScript(parser.compile(reader));
 		}finally{
 			try{
 				if(reader != null) reader.close();
 			}catch(IOException ex){
 				throw new ScriptException(ex);
 			}
 		}
 	}
 	/**
 	*スクリプトをコンパイルします。
 	*@param script スクリプト
 	*@return コンパイルされたスクリプト
 	*@throws ScriptException コンパイルに失敗した場合
 	*/
 	@Override
 	public CompiledScript compile(String script)
 	throws ScriptException{
 		return compile(new StringReader(script));
 	}
 	/**
 	*スクリプトをコンパイルして実行します。
 	*@param reader スクリプトのソース
 	*@param context 関連付けるコンテキスト
 	*@return スクリプトの戻り値
 	*@throws ScriptException 実行時例外があった場合
 	*/
 	@Override
 	public Object eval(Reader reader, ScriptContext context)
 	throws ScriptException{
 		return compile(reader).eval(context);
 	}
 	/**
 	*スクリプトをコンパイルして実行します。
 	*@param script スクリプト
 	*@param context 関連付けるコンテキスト
 	*@return スクリプトの戻り値
 	*@throws ScriptException 実行時例外があった場合
 	*/
 	@Override
 	public Object eval(String script, ScriptContext context)
 	throws ScriptException{
 		return compile(script).eval(context);
 	}
 	/**
	*AriCE言語のコンパイル結果を格納します。
 	*
 	*@author 東大アマチュア無線クラブ
 	*@since  Leaf 1.3 作成：2011年7月8日
	*/
	public final class AriceCompiledScript extends CompiledScript{
		private final Code[] medcodes;
		AriceCompiledScript(Code[] codes){
			this.medcodes = codes;
		}
		/**
		*コンパイル済みのスクリプトを実行します。
		*@param context 関連付けられるコンテキスト
		*@return スクリプトの返り値
		*@throws ScriptException 実行時例外があった場合
		*/
		@Override
		public Object eval(ScriptContext context) throws ScriptException{
			Bindings bind = context.getBindings(ENGINE_SCOPE);
			return vm.execute(medcodes, bind);
		}
		/**
		*スクリプトに関連付けられたエンジンを返します。
		*@return エンジン本体
		*/
		@Override
		public ScriptEngine getEngine(){
			return AriceScriptEngine.this;
		}
		/**
		*スクリプトを逆コンパイルして返します。
		*@return 逆コンパイルされたソース
		*/
		public String disassemble(){
			try{
				return new Disassembler().disassemble(medcodes);
			}catch(ScriptException ex){
				return ex.getMessage();
			}
		}
	}
 	/**
 	*このエンジンに関連付けられたファクトリを返します。
 	*@return ファクトリ
 	*/
 	@Override
 	public ScriptEngineFactory getFactory(){
 		return AriceScriptEngineFactory.getInstance();
 	}
}