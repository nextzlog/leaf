/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.script.falcon;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import leaf.script.falcon.ast.stmt.Program;
import leaf.script.falcon.lex.ReaderString;
import leaf.script.falcon.lex.Tokenizer;
import leaf.script.falcon.parser.StatementParser;
import leaf.script.falcon.vm.CodeList;
import leaf.script.falcon.vm.VirtualMachine;

/**
 * Falcon処理系の{@link ScriptEngine}の実装です。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * 
 * @since 2012/12/24
 *
 */
public final class FalconScriptEngine
extends AbstractScriptEngine implements Compilable {
	
	/**
	 * エンジンを構築します。
	 */
	public FalconScriptEngine() {}

	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	@Override
	public Object eval(String source, ScriptContext sc) throws ScriptException {
		return compile(source).eval(context);
	}

	@Override
	public Object eval(Reader reader, ScriptContext sc) throws ScriptException {
		return compile(reader).eval(context);
	}

	@Override
	public ScriptEngineFactory getFactory() {
		return null;
	}

	@Override
	public CompiledScript compile(String source) throws ScriptException {
		return compile(new StringReader(source));
	}

	@Override
	public CompiledScript compile(Reader reader) throws ScriptException {
		try {
			ReaderString rs = ReaderString.read(reader);
			Tokenizer lex = new Tokenizer(rs);
			StatementParser parser = new StatementParser(lex);
			Program program = parser.parseProgram();
			program.resolve(null);
			CodeList list = new CodeList();
			program.gencode(list);
			System.out.println(program);
			return new FalconCompiledScript(list.toCode());
		} catch(IOException ex) {
			throw new ScriptException(ex);
		}
	}
	
	/**
	 * コンパイル済みの中間言語命令列を格納します。
	 * 
	 * 
	 * @author 東大アマチュア無線クラブ
	 * 
	 * @since 2012/12/24
	 *
	 */
	public class FalconCompiledScript extends CompiledScript {
		private final VirtualMachine vm;
		
		public FalconCompiledScript(Object[] codes) {
			vm = new VirtualMachine(codes);
		}	
		
		@Override
		public Object eval(ScriptContext sc) throws ScriptException {
			Bindings b = sc.getBindings(ScriptContext.ENGINE_SCOPE);
			return vm.execute(b);
		}
		
		@Override
		public ScriptEngine getEngine() {
			return FalconScriptEngine.this;
		}
	
	}

}
