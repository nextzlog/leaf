/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.mil;
import java.util.ArrayList;

/**
*MIL言語のインタープリタの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年6月24日
*/
public class MILScriptEngine{
	
	private ArrayList<Code> bytecodes;
	private Parser parser;
	private VirtualMachine machine;
	
	/**MIL言語のインタープリタを生成します。*/
	public MILScriptEngine(){}
	
	/**
	スクリプトの文字列を指定してスクリプトを実行します。
	*@param script 実行するスクリプト
	*/
	public void eval(String script){
		parser    = new Parser(script);
		bytecodes = parser.parse();
		machine   = new VirtualMachine(bytecodes);
		machine.execute();
	}
}