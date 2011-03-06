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
*AriCE言語のブロックテーブルの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2011年1月28日
*/
final class AriceBlockTable{
	
	private final ArrayList<Block> table;
	private final AriceLexAnalyzer analyzer;
	
	private Block block = null;
	
	/**
	*字句解析器と初期サイズを指定してテーブルを生成します。
	*@param analyzer 字句解析器
	*@param size 初期容量
	*/
	public AriceBlockTable(AriceLexAnalyzer analyzer, int size){
		this.analyzer = analyzer;
		table  = new ArrayList<Block>(size);
	}
	/**
	*匿名ブロックを発行します。
	*@return ブロックの登録番号
	*/
	public int addBlock(){
		return addBlock(null);
	}
	/**
	*指定した名前の構文ブロックを発行します。
	*@param name ブロックの名前
	*@return ブロックの登録番号
	*/
	public int addBlock(Token name){
		table.add(block = new Block(
			block, (name != null)? name.toString() : null, -1
		));
		return table.size() -1;
	}
	/**
	*関数ブロックを検索し、存在しない場合発行します。
	*@param name 関数の名前
	*@param args 引数の個数
	*@return ブロックの登録番号
	*/
	public int searchOrCreateBlock(String name, int args){
		int size = table.size();
		for(int i=0;i<size;i++){
			if(table.get(i).equals(name,args)){
				return i;
			}
		}
		Block block = new Block(null, name, args);
		table.add(block);
		return size;
	}
	/**
	*指定した名前の構文ブロックを検索して返します。
	*@param name ブロックの名前
	*@return ブロックの登録番号
	*@throws ScriptException ブロックが存在しない場合
	*/
	public int searchBlock(String name) throws ScriptException{
		Block target = block.search(name);
		if(target != null)
			return table.lastIndexOf(target);
		else{
			throw error("Block " + name + " is not declared.");
		}
	}
	/**
	*指定した構文ブロックに対し実アドレスを設定します。
	*設定と同時に現在のブロックを脱出します。
	*@param num ブロックの番号
	*@param address 実アドレス
	*/
	public void setAddress(int address){
		block.setAddress(address);
		block = block.parent;
	}
	/**
	*指定した関数ブロックに対し実アドレスを設定します。
	*@param num ブロックの番号
	*@param address 実アドレス
	*/
	public void setAddress(int num, int address){
		table.get(num).setAddress(address);
	}
	/**
	*指定したブロックの参照する実アドレスを返します。
	*@param num ブロックの番号
	*@return 実アドレス 設定されていない場合-1
	*/
	public int getAddress(int num){
		return table.get(num).getAddress();
	}
	/**
	*テーブルを初期化します。
	*/
	public void clear(){
		table.clear();
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
	*位置参照ブロックの実装
	*/
	private class Block{
		
		public final Block parent;
		public final String  name;
		private final int args;
		private int address = -1;
		
		/**
		*ブロックを生成します。
		*@param parent 親ブロック
		*@param name 名前
		*@param args 引数の数
		*/
		public Block(Block parent, String name, int args){
			this.parent = parent;
			this.name   = name;
			this.args   = args;
		}
		/**
		*指定した関数ブロックと一致するか返します。
		*@param name 関数名
		*@param args 引数の個数
		*/
		public boolean equals(String name, int args){
			return (this.args == args)&&(this.name.equals(name));
		}
		/**
		*指定された名前の構文ブロックをルートまで検索して返します。
		*@param name ブロック名
		*@return 一致するブロック
		*/
		public Block search(String name){
			if(name.equals(this.name) && args<0) return this;
			else return (parent != null)?parent.search(name) : null;
		}
		/**
		*ブロックにアドレスを設定します。
		*@param address アドレス
		*/
		public void setAddress(int address){
			this.address = address;
		}
		/**
		*ブロックのアドレスを返します。
		*@return アドレス
		*/
		public int getAddress(){
			return address;
		}
	}
}
