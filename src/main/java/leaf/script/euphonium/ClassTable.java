/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.script.euphonium;

import java.util.HashMap;
import java.util.HashSet;
import javax.script.ScriptException;

import leaf.manager.LeafLocalizeManager;

/**
 *インポート済みクラスを管理するテーブルの実装です。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.2 作成：2011年2月17日
 */
final class ClassTable {
	
	private final HashMap<String, Class> table;
	private final HashSet<String> packages;
	private final LexicalAnalyzer analyzer;
	private final ClassLoader loader;
	private final LeafLocalizeManager localize;
	
	/**
	*字句解析器を指定してテーブルを生成します。
	*@param analyzer 字句解析器
	*/
	public ClassTable(LexicalAnalyzer analyzer){
		loader = ClassTable.class.getClassLoader();
		localize = LeafLocalizeManager.getInstance(getClass());
		this.analyzer = analyzer;
		packages  = new HashSet<String>();
		table = new HashMap<String, Class>();
		addPackage("java.lang");
		addPackage("java.util");
		addPackage("java.net" );
		addPackage("java.math");
		addPackage("leaf.manager");
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
			throw error(localize.translate("add_exception", old));
		}
	}
	/**
	*指定された正準名のクラスを追加登録します。
	*末尾がワイルドカードである場合、パッケージを登録します。
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
	*/
	public void addPackage(String name){
		packages.add(name);
	}
	/**
	*指定した短縮名のクラスが登録済みであるか返します。
	*@param name クラス名
	*@return 登録済みの場合true
	*/
	public boolean exists(Token name){
		if(table.containsKey(name.toString())) return true;
		return (search(name.toString()) != null);
	}
	/**
	*指定した名前の登録済みクラスを返します。
	*正準名の場合登録済みである必要はありません。
	*@param name クラス名
	*@return 登録済みクラス
	*@throws ScriptException クラスが登録されていない場合
	*/
	public Class get(Token name) throws ScriptException{
		return get(name.toString());
	}
	/**
	*指定した名前の登録済みクラスを返します。
	*正準名の場合登録済みである必要はありません。
	*@param name クラス名
	*@return 登録済みクラス
	*@throws ScriptException クラスが登録されていない場合
	*/
	public Class get(String name) throws ScriptException{
		if(isFormalName(name)) return loadClass(name);
		else if(table.containsKey(getSimpleName(name)))
			return table.get(getSimpleName(name));
		else{
			Class cls = search(name);
			if(cls != null) return cls;
			throw error(localize.translate("get_exception", name));
		}
	}
	/**
	*指定された正準名のクラスを返します。
	*@param name 正準クラス名
	*@return クラス
	*@throws ScriptException クラスが存在しない場合
	*/
	private Class loadClass(String name) throws ScriptException{
		try{
			return (getClass().getClassLoader().loadClass(name));
		}catch(ClassNotFoundException ex){
			throw error(localize.translate("loadClass_exception", name));
		}
	}
	/**
	*指定された短縮名のクラスを登録済みパッケージ内で検索します。
	*@param name 短縮クラス名
	*@return クラス 存在しない場合null
	*/
	private Class search(String name){
		Class cls = null;
		for(String pack : packages){
			try{
				cls =loader.loadClass(pack + "." + name);
				break;
			}catch(ClassNotFoundException ex){}
		}
		if(cls != null) table.put(name, cls);
		return cls;
	}
	/**
	*指定されたクラス名が正準名であるか返します。
	*@param name クラス名
	*@return 正準名の場合true 短縮名の場合false
	*/
	private boolean isFormalName(String name){
		return (name.indexOf(".") > 0);
	}
	/**
	*指定された正準クラス名の短縮名を返します。
	*既に短縮クラス名の場合はそのまま返します。
	*@param name 正準クラス名
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
		//Do not init packages!
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