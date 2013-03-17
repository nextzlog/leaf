/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.shell.build;

import javax.xml.namespace.QName;

/**
 * ビルド文書の要素や属性がこのパッケージで未定義である場合にスローされます。
 * 
 * @author 東大アマチュア無線クラブ
 * @since  Leaf 1.3 作成：2011年12月11日
 */
public class UnknownNameException extends Exception{
	private static final long serialVersionUID = 1L;
	private final String name;
	
	/**
	 * 指定された詳細メッセージを持つ例外を生成します。
	 * 
	 * @param name 例外の原因となった名前
	 */
	public UnknownNameException(String name){
		super(name);
		this.name = name;
	}
	
	/**
	 * 指定された原因を詳細メッセージに持つ例外を生成します。
	 * 
	 * @param qname 例外の原因となった名前
	 */
	public UnknownNameException(QName qname){
		this(qname.toString());
	}
	
	/**
	 * この例外の原因となった名前を返します。
	 * 
	 * @return 例外の原因となった名前
	 */
	public String getName(){
		return name;
	}

}