/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.manager;

import java.lang.reflect.*;
import java.util.HashMap;

/**
 *JavaAPIを呼び出す能力を持つ動的言語を支援するためのマネージャです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.2 作成：2011年2月25日
 */
public class LeafReflectManager{
	
	private LeafReflectManager(){}
	
	/**
	*指定されたクラスに対し条件の一致するコンストラクタを取得します。
	*@param type 対象クラス
	*@param args 引数の配列
	*@return コンストラクタ 存在しない場合null
	*/
	public static Constructor getConstructor(Class type, Object... args){
		Constructor[] consts = type.getConstructors();
		loop : for(Constructor cons : consts){
			Class<?>[] pars = cons.getParameterTypes();
			if(args.length == pars.length){
				for(int i=0;i<args.length;i++){
					if(!isCastable(args[i], pars[i])) continue loop;
				}
				return cons;
			}
		}
		return null;
	}
	/**
	*指定されたクラスに対し条件の一致するコンストラクタを実行します。
	*@param type 対象クラス
	*@param args 引数の配列
	*@return クラスのインスタンス
	*@throws NoSuchMethodException コンストラクタが存在しない場合
	*@throws IllegalAccessException コンストラクタにアクセスできない場合
	*@throws IllegalArgumentException 引数の型が誤っている場合
	*@throws InstantiationException クラスが抽象クラスである場合
	*@throws InvocationTargetException メソッド内でスローされた例外
	*/
	public static Object newInstance(Class type, Object...args)
	throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
	InstantiationException, InvocationTargetException{
		
		Constructor cons = getConstructor(type, args);
		if(cons != null) return cons.newInstance(args);
		else{
			StringBuilder fullname = new StringBuilder(type.getName()+"(");
			for(int i=0;i<args.length;i++){
				fullname.append((args[i]!=null)?args[i].getClass().getName():"null");
				if(i<args.length-1) fullname.append(",");
			}
			fullname.append(")");
			throw new NoSuchMethodException(String.format("Not found %s.", fullname));
		}
	}
	/**
	*指定されたクラスに対し条件の一致するメソッドを取得します。
	*@param type 対象オブジェクト
	*@param name メソッド名
	*@param args 引数の配列
	*@return メソッド 存在しない場合null
	*/
	public static Method getMethod(Class type, String name, Object... args){
		Method[] mets = type.getMethods();
		loop : for(Method met : mets){
			Class<?>[] pars = met.getParameterTypes();
			if(!met.getName().equals(name)) continue;
			if(args.length == pars.length){
				for(int i=0;i<args.length;i++){
					if(!isCastable(args[i], pars[i])) continue loop;
				}
				return met;
			}
		}
		return null;
	}
	/**
	*クラスを指定して条件の一致する静的メソッドを実行します。
	*@param type クラス
	*@param name メソッド名
	*@param args 引数の配列
	*@return メソッドの戻り値
	*@throws NoSuchMethodException メソッドが存在しない場合
	*@throws IllegalAccessException メソッドにアクセスできない場合
	*@throws IllegalArgumentException 引数の型が誤っている場合
	*@throws InvocationTargetException メソッド内でスローされた例外
	*@since 2011年7月30日
	*/
	public static Object invokeMethod(Class type, String name, Object... args)
	throws NoSuchMethodException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException{
		
		Method method = getMethod(type, name, args);
		if(method != null) return method.invoke(null, args);
		else{
			StringBuilder fullname = new StringBuilder(type.getName()+"."+name+"(");
			for(int i=0;i<args.length;i++){
				fullname.append((args[i]!=null)?args[i].getClass().getName():"null");
				if(i<args.length-1) fullname.append(",");
			}
			fullname.append(")");
			throw new NoSuchMethodException(String.format("Not found %s.", fullname));
		}
	}
	/**
	*オブジェクトに対し条件の一致するメソッドを実行します。
	*@param obj 対象オブジェクト
	*@param name メソッド名
	*@param args 引数の配列
	*@return メソッドの戻り値
	*@throws NoSuchMethodException メソッドが存在しない場合
	*@throws IllegalAccessException メソッドにアクセスできない場合
	*@throws IllegalArgumentException 引数の型が誤っている場合
	*@throws InvocationTargetException メソッド内でスローされた例外
	*@since 2011年7月30日
	*/
	public static Object invokeMethod(Object obj, String name, Object... args)
	throws NoSuchMethodException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException{
		
		Class type = obj.getClass();
		Method method = getMethod(type, name, args);
		if(method != null) return method.invoke(obj, args);
		else{
			StringBuilder fullname = new StringBuilder(type.getName()+"."+name+"(");
			for(int i=0;i<args.length;i++){
				fullname.append((args[i]!=null)?args[i].getClass().getName():"null");
				if(i<args.length-1) fullname.append(",");
			}
			fullname.append(")");
			throw new NoSuchMethodException(String.format("Not found %s.", fullname));
		}
	}
	/**
	*オブジェクトが指定された型で型変換可能であるか返します。
	*オートボクシングによって可換である場合真値を返しますが、
	*型が基本数値型である場合、精度を損ねることなく型変換可能
	*な場合に限り真値を返します。
	*@param obj オブジェクト
	*@param type 型
	*@return 型変換可能な場合真値を返す
	*@since 2011年3月21日
	*/
	public static boolean isCastable(Object obj, Class type){
		if(type.isInstance(obj)) return true;
		if(!type.isPrimitive()) return obj == null;
		if(obj instanceof Number)
		return TypeUnit.isUpperCompatible((Number)obj, type);
		return TypeUnit.wrap(type).isInstance(obj);
	}
	/**
	*算術基本型変換規則を表現するクラスです。
	*/
	private static final class TypeUnit{
		private static final HashMap<Class, Integer> map;
		private static int strength = 0;
		static{
			map = new HashMap<Class, Integer>();
			add(Byte.class);
			add(Short.class);
			add(Integer.class);
			add(Long.class);
			add(Float.class);
			add(Double.class);
		}
		/**
		*現在の最強型よりも強い数値型を追加します。
		*@param numcls より強力な型
		*/
		public static void add(Class numcls){
			map.put(numcls, strength++);
		}
		/**
		*ある基本型に対しラッパークラスを返します。
		*@param type 基本型
		*@return ラッパークラス
		*/
		public static Class wrap(Class type){
			assert type.isPrimitive() : "not primitive !";
			Object arr  = Array.newInstance(type, 1);
			return Array.get(arr, 0).getClass();
		}
		/**
		*指定した基本型で数値が精度を維持できるか返します。
		*@param a 数値
		*@param b 型変換する基本数値型
		*@return 精度が損なわれる場合false
		*/
		public static boolean isUpperCompatible(Number a, Class b){
			Integer anum = map.get(a.getClass());
			Integer bnum = map.get(wrap(b));
			try{
				return anum <= bnum;
			}catch(NullPointerException ex){
				return false;
			}
		}
	}
}