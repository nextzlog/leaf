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
package leaf.manager;

import java.lang.reflect.*;

/**
*リフレクト機能を補強するマネージャです。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成；2011年2月25日
*/
public class LeafReflectManager{
	
	/**
	*指定されたクラスに対し条件の一致するコンストラクタを取得します。
	*@param clas 対象クラス
	*@param args 引数の配列
	*@return コンストラクタ 存在しない場合null
	*/
	public static Constructor getConstructor(Class clas, Object... args){
		Constructor[] consts = clas.getConstructors();
		loop : for(Constructor cons : consts){
			Class<?>[] pars = cons.getParameterTypes();
			if(args.length == pars.length){
				for(int i=0;i<args.length;i++){
					if(pars[i].isPrimitive()||args[i]==null) continue;
					if(!pars[i].isInstance(args[i])) continue loop;
				}
				return cons;
			}
		}
		return null;
	}
	/**
	*指定されたクラスに対し条件の一致するコンストラクタを取得します。
	*@param clas 対象クラス
	*@param args 引数のクラスの配列
	*@return コンストラクタ 存在しない場合null
	*/
	public static Constructor getConstructor(Class clas, Class<?>... args){
		Constructor[] consts = clas.getConstructors();
		loop : for(Constructor cons : consts){
			Class<?>[] pars = cons.getParameterTypes();
			if(args.length == pars.length){
				for(int i=0;i<args.length;i++){
					if(pars[i].isPrimitive()||args[i]==null) continue;
					try{
						args[i].asSubclass(pars[i]);
					}catch(ClassCastException ex){continue loop;}
				}
				return cons;
			}
		}
		return null;
	}
	/**
	*指定されたクラスに対し条件の一致するコンストラクタを実行します。
	*@param clas 対象クラス
	*@param args 引数の配列
	*@return クラスのインスタンス
	*@throws NoSuchMethodException コンストラクタが存在しない場合
	*@throws IllegalAccessException コンストラクタにアクセスできない場合
	*@throws IllegalArgumentException 引数の型が誤っている場合
	*@throws InstantiationException クラスが抽象クラスである場合
	*@throws InvocationTargetException メソッド内でスローされた例外
	*/
	public static Object newInstance(Class clas, Object...args)
	throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
	InstantiationException, InvocationTargetException{
		
		Constructor cons = getConstructor(clas, args);
		if(cons != null){
			return cons.newInstance(args);
		}else{
			String fullname = clas.getName();
			throw new NoSuchMethodException(LeafLangManager.translate(
				"Not found [arg].", "[arg] が見つかりません。", fullname
			));
		}
	}
	/**
	*指定されたクラスに対し条件の一致するコンストラクタを実行します。
	*@param clas 対象クラス
	*@param args 引数の配列
	*@param clss 引数のクラスの配列
	*@return クラスのインスタンス
	*@throws NoSuchMethodException コンストラクタが存在しない場合
	*@throws IllegalAccessException コンストラクタにアクセスできない場合
	*@throws IllegalArgumentException 引数の型が誤っている場合
	*@throws InstantiationException クラスが抽象クラスである場合
	*@throws InvocationTargetException メソッド内でスローされた例外
	*/
	public static Object newInstance(Class clas, Object[] args, Class[] clss)
	throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
	InstantiationException, InvocationTargetException{
		
		Constructor cons = getConstructor(clas, clss);
		if(cons != null){
			return cons.newInstance(args);
		}else{
			String fullname = clas.getName();
			throw new NoSuchMethodException(LeafLangManager.translate(
				"Not found [arg].", "[arg] が見つかりません。", fullname
			));
		}
	}
	/**
	*指定されたクラスに対し条件の一致するメソッドを取得します。
	*@param clas 対象オブジェクト
	*@param name メソッド名
	*@param args 引数の配列
	*@return メソッド 存在しない場合null
	*/
	public static Method getMethod(Class clas, String name, Object... args){
		Method[] mets = clas.getMethods();
		loop : for(Method met : mets){
			Class<?>[] pars = met.getParameterTypes();
			if(!met.getName().equals(name)) continue;
			if(args.length == pars.length){
				for(int i=0;i<args.length;i++){
					if(pars[i].isPrimitive()||args[i]==null) continue;
					if(!pars[i].isInstance(args[i])) continue loop;
				}
				return met;
			}
		}
		return null;
	}
	/**
	*指定されたクラスに対し条件の一致するメソッドを取得します。
	*@param clas 対象オブジェクト
	*@param name メソッド名
	*@param args 引数のクラスの配列
	*@return メソッド 存在しない場合null
	*/
	public static Method getMethod(Class clas, String name, Class<?>... args){
		Method[] mets = clas.getMethods();
		loop : for(Method met : mets){
			Class<?>[] pars = met.getParameterTypes();
			if(!met.getName().equals(name)) continue;
			if(args.length == pars.length){
				for(int i=0;i<args.length;i++){
					if(pars[i].isPrimitive()||args[i]==null) continue;
					try{
						args[i].asSubclass(pars[i]);
					}catch(ClassCastException ex){continue loop;}
				}
				return met;
			}
		}
		return null;
	}
	/**
	*クラスを指定してオブジェクトに対し条件の一致するメソッドを実行します。
	*@param obj 対象オブジェクト
	*@param clas オブジェクトのクラス
	*@param name メソッド名
	*@param args 引数の配列
	*@return メソッドの戻り値
	*@throws NoSuchMethodException メソッドが存在しない場合
	*@throws IllegalAccessException メソッドにアクセスできない場合
	*@throws IllegalArgumentException 引数の型が誤っている場合
	*@throws InvocationTargetException メソッド内でスローされた例外
	*/
	public static Object invokeMethod(Object obj, Class clas, String name, Object... args)
	throws NoSuchMethodException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException{
		
		Method method = getMethod(clas, name, args);
		if(method != null){
			return method.invoke(obj, args);
		}else{
			StringBuilder fullname = new StringBuilder(clas.getName()+"."+name+"(");
			for(int i=0;i<args.length;i++){
				fullname.append(args[i].getClass()).append((i<args.length-1)?",":")");
			}
			throw new NoSuchMethodException(LeafLangManager.translate(
				"Not found [arg].", "[arg] が見つかりません。", fullname
			));
		}
	}
	/**
	*クラスを指定してオブジェクトに対し条件の一致するメソッドを実行します。
	*@param obj 対象オブジェクト
	*@param clas オブジェクトのクラス
	*@param name メソッド名
	*@param args 引数の配列
	*@param clss 引数のクラスの配列
	*@return メソッドの戻り値
	*@throws NoSuchMethodException メソッドが存在しない場合
	*@throws IllegalAccessException メソッドにアクセスできない場合
	*@throws IllegalArgumentException 引数の型が誤っている場合
	*@throws InvocationTargetException メソッド内でスローされた例外
	*/
	public static Object invokeMethod
	(Object obj, Class clas, String name, Object[] args, Class[] clss)
	throws NoSuchMethodException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException{
		
		Method method = getMethod(clas, name, clss);
		if(method != null){
			return method.invoke(obj, args);
		}else{
			StringBuilder fullname = new StringBuilder(clas.getName()+"."+name+"(");
			for(int i=0;i<args.length;i++){
				fullname.append(clss[i]).append((i<args.length-1)?",":")");
			}
			throw new NoSuchMethodException(LeafLangManager.translate(
				"Not found [arg].", "[arg] が見つかりません。", fullname
			));
		}
	}
}