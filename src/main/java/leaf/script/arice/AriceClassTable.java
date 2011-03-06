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

import java.util.HashMap;
import java.util.ArrayList;
import javax.script.ScriptException;

/**
*AriCE言語でインポートされたクラスを管理するテーブルの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2011年2月17日
*/
final class AriceClassTable{
	
	private final HashMap<String, Class> table;
	private final ArrayList<String>   packs;
	private final AriceLexAnalyzer analyzer;
	
	/**
	*字句解析器と初期サイズを指定してテーブルを生成します。
	*@param analyzer 字句解析器
	*@param size 初期容量
	*/
	public AriceClassTable(AriceLexAnalyzer analyzer, int size){
		this.analyzer = analyzer;
		packs = new ArrayList<String>();
		table = new HashMap<String, Class>(size);
		AriceDefaultClasses.importDefaultClasses(this);
	}
	/**
	*クラスを追加登録します。
	*@param cls 登録するクラス
	*@throws ScriptException クラス名が重複している場合
	*/
	public void add(Class cls) throws ScriptException{
		String name = cls.getSimpleName();
		Class old = table.put(name, cls);
		if(old != null && !old.getName().equals(cls.getName())){
			throw error(old + " is already imported.");
		}
	}
	/**
	*指定された正規名のクラスを追加登録します。
	*クラス名の末尾がワイルドカードである場合、パッケージを登録します。
	*@param name 登録するクラス
	*@throws ScriptException クラスが存在しないか、重複している場合
	*/
	public void add(String name) throws ScriptException{
		if(name.endsWith(".*")){
			addPackage(name.substring(0, name.length() - 2));
		}else add(loadClass(name));
	}
	/**
	*指定された名前のパッケージを追加登録します。
	*@param name 登録するパッケージ
	*@throws ScriptException パッケージが存在しない場合
	*/
	public void addPackage(String name) throws ScriptException{
		Package pack = Package.getPackage(name);
		if(pack != null) packs.add(pack.getName());
		else{
			throw error("Package " + name + " does not exist.");
		}
	}
	/**
	*指定した短縮名のクラスが登録済みかどうか返します。
	*@param name クラス名
	*@return 登録済みの場合true
	*/
	public boolean exists(String name){
		if(table.containsKey(name)) return true;
		else return (search(name) != null);
	}
	/**
	*指定した名前の登録済みクラスを返します。
	*正規名の場合登録済みである必要はありません。
	*@param name クラス名
	*@return 登録済みクラス
	*@throws ScriptException クラスが登録されていない場合
	*/
	public Class get(Token name) throws ScriptException{
		return get(name.toString());
	}
	/**
	*指定した名前の登録済みクラスを返します。
	*正規名の場合登録済みである必要はありません。
	*@param name クラス名
	*@return 登録済みクラス
	*@throws ScriptException クラスが登録されていない場合
	*/
	public Class get(String name) throws ScriptException{
		if(isFormalName(name))
			return loadClass(name);
		else if(table.containsKey(getSimpleName(name)))
			return table.get(name);
		else{
			Class cls = search(name);
			if(cls != null) return cls;
			else throw error(name + " is not imported.");
		}
	}
	/**
	*指定された正規名のクラスを返します。
	*@param name 正規クラス名
	*@return クラス
	*@throws ScriptException クラスが存在しない場合
	*/
	private Class loadClass(String name) throws ScriptException{
		try{
			return (getClass().getClassLoader().loadClass(name));
		}catch(ClassNotFoundException ex){
			throw error(name + " is not found.");
		}
	}
	/**
	*指定された短縮名のクラスを登録済みパッケージ内で検索します。
	*@param name 短縮クラス名
	*@return クラス 存在しない場合null
	*/
	private Class search(String name){
		Class cls = null;
		for(String pack : packs){
			try{
				cls = Class.forName(pack + "." + name);break;
			}catch(Exception ex){}
		}
		if(cls != null) table.put(name, cls);
		return cls;
	}
	/**
	*指定されたクラス名が正規名かどうか返します。
	*@param name クラス名
	*@return 正規名の場合true 短縮名の場合false
	*/
	private boolean isFormalName(String name){
		return (name.indexOf(".") > 0);
	}
	/**
	*指定された正規クラス名の短縮名を返します。
	*名前が短縮クラス名の場合はそのまま返します。
	*@param name 正規クラス名
	*@return 短縮クラス名
	*/
	private String getSimpleName(String name){
		return name.substring(name.lastIndexOf(".")+1);
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
			msg + " at line : " + line + "\n => " 
			+ analyzer.getLine(), null, line, colm
		);
	}
}
