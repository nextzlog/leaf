/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.demo;

import java.io.FileReader;
import java.util.List;
import javax.script.CompiledScript;
import leaf.script.arice.AriceScriptEngine;

/**
*AriCE言語のデモ実行環境です。
*
*@author 東大アマチュア無線クラブ
*@since  Leaf 1.3 作成：2011年4月4日
*/
final class RunScript{
	/**
	*ソースファイルを指定して実行します。
	*@param src ソースファイルへのパス
	*@param options 起動オプション
	*/
	public static void run(String src, List<String> options){
		AriceScriptEngine engine = new AriceScriptEngine();
		if(options.contains("help")){
			System.out.println("Type as follows if you want to run arice script");
			System.out.println("java -jar leaf.jar <source file> <options>");
			System.out.println("[options]");
			System.out.println("-trace : print stack trace when error occurred");
			System.out.println("-asm   : dis assemble arice code");
		}else if(src != null){
			try{
				System.out.println("===========EVALUATE==========");
				final long start = System.nanoTime();
				CompiledScript cs = engine.compile(new FileReader(src));
				if(options.contains("asm")){
					System.out.println(
						((AriceScriptEngine.AriceCompiledScript)cs).disassemble());
				}else{
					System.out.println("=>" + cs.eval());
					System.out.println("=>" + (System.nanoTime() - start) + " ns");
				}
			}catch(Exception ex){
				if(options.contains("trace")){
					System.out.println("============ERROR============");
					ex.printStackTrace();
				}else{
					System.out.println("============ERROR============");
					System.out.println(ex);
				}
			}
		}
	}
}