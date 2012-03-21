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
 *AriCE構文解析器のラベルテーブルの実装です。
 *ラベルはGOTO命令やFUNC_CALL命令のジャンプ先に用います。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.2 作成：2011年1月28日
 */
final class AriceLabelTable {
	
	private final ArrayList<Label> table;
	private final AriceLexAnalyzer analyzer;
	private Label label;
	private final LeafLocalizeManager localize;
	
	/**
	*字句解析器を指定してテーブルを生成します。
	*@param analyzer 字句解析器
	*/
	public AriceLabelTable(AriceLexAnalyzer analyzer){
		this.analyzer = analyzer;
		table = new ArrayList<Label>();
		localize = LeafLocalizeManager.getInstance(getClass());
	}
	/**
	*匿名ブロックラベルを発行します。
	*@return ラベルの登録番号
	*/
	public int addLabel(){
		return addLabel(null);
	}
	/**
	*名前を指定してブロックラベルを発行します。
	*@param name ラベルの名前
	*@return ラベルの登録番号
	*/
	public int addLabel(Token name){
		table.add(label = new Label(
			label, (name!=null)? name : null, -1));
		return table.size() - 1;
	}
	/**
	*大域関数のラベルを検索し、存在しない場合発行します。
	*@param name 関数の名前
	*@param pars 引数の個数
	*@return ラベルの登録番号
	*/
	public int searchOrCreateLabel(Token name, int pars){
		int size = table.size();
		for(int i=0; i<size; i++){
			if(table.get(i).equals(name, pars)) return i;
		}
		table.add(new Label(null, name, pars));
		return size;
	}
	/**
	*指定した名前のラベルを検索して返します。
	*@param name ラベルの名前
	*@return ラベルの登録番号
	*@throws ScriptException ラベルが未登録の場合
	*/
	public int searchLabel(Token name) throws ScriptException{
		Label target = label.search(name);
		if(target != null) return target.getIndex();
		throw error("searchLabel_exception", name);
	}
	/**
	*現在のラベルに実アドレスを設定します。
	*設定と同時に現在のブロックを脱出します。
	*@param addr 実アドレス
	*/
	public void setAddress(int addr){
		label.setAddress(addr);
		label = label.parent;
	}
	/**
	*関数ラベルに実アドレスを設定します。
	*@param index ラベルの登録番号
	*@param addr 関数の開始位置
	*/
	public void setAddress(int index, int addr){
		table.get(index).setAddress(addr);
	}
	/**
	*指定したラベルの実アドレスを返します。
	*@param index ラベルの登録番号
	*@return 実アドレス 未設定の場合-1
	*/
	public int getAddress(int index){
		return table.get(index).getAddress();
	}
	/**
	*テーブルを初期化します。
	*/
	public void clear(){
		table.clear();
	}
	/**
	*構文違反があった場合に例外を生成します。
	*@param key メッセージの国際化キー
	*@param arg メッセージの引数
	*@return 生成した例外
	*/
	private ScriptException error(String key, Object arg){
		int line = analyzer.getLineNumber();
		int colm = analyzer.getColumnNumber();
		String msg = localize.translate(key, arg);
		return new ScriptException(
			msg + " at line : " + line + "\n => " +
			analyzer.getLine(), null, line, colm
		);
	}
	/**
	*関数やブロックの入れ子関係に対応する
	*入れ子構造を持ったラベルの実装です。
	*/
	private final class Label {
		public final Label parent;
		public final Token name;
		private final int params;
		private int address = -1;
		private final int index;
		
		/**
		*ラベルを生成します。
		*@param parent 親ラベル
		*@param name 名前
		*@param params 引数の個数
		*/
		public Label(Label parent, Token name, int params){
			this.parent = parent;
			this.name   = name;
			this.params = params;
			this.index  = table.size();
		}
		/**
		*指定した関数ラベルと一致するか返します。
		*@param name 関数名
		*@param params 引数の個数
		*/
		public boolean equals(Token name, int params){
			return (this.params == params)
			&& (this.name.equals(name));
		}
		/**
		*指定された名前のラベルを検索して返します。
		*@param name ラベルの名前
		*@return 一致するラベル
		*/
		public Label search(Token name){
			if(params < 0 && name.equals(this.name)) return this;
			return (parent != null)? parent.search(name) : null;
		}
		/**
		*ラベルの登録番号を返します。
		*@return ラベルの登録番号
		*/
		public int getIndex(){
			return index;
		}
		/**
		*ラベルに実アドレスを設定します。
		*@param addr 実アドレス
		*/
		public void setAddress(int addr){
			this.address = addr;
		}
		/**
		*対応する実アドレスを返します。
		*@return 実アドレス
		*/
		public int getAddress(){
			return address;
		}
	}
}
