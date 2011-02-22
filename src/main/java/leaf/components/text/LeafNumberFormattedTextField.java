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
package leaf.components.text;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.*;

/**
*総称型により入力が数値に限定されたJFormattedTextFieldです。
*指定できる型はLong、Integer、Short、Byte、Double、Floatがあります。
*@author 東大アマチュア無線クラブ
*@since Leaf 1,0 作成：2010年8月17日
*/
public class LeafNumberFormattedTextField<T extends Number> extends JFormattedTextField{

	/**秘匿フィールド*/
	private final NumberFormatter nf = new NumberFormatter();
	private T value;
	
	/**
	*このコンポーネントを生成します。
	*/
	public LeafNumberFormattedTextField(){
		super();
		init();
	}
	/**
	*初期に表示される文字列を指定してこのコンポーネントを生成します。
	*@param value 初期の整数値
	*/
	public LeafNumberFormattedTextField(T value){
		super();
		init();
		setValue(this.value = value);
	}
	/**初期化*/
	private void init(){
		
		setSelectionColor(Color.BLACK);
		setSelectedTextColor(Color.WHITE);
		
		nf.setValueClass(Number.class);
		((NumberFormat)nf.getFormat()).setGroupingUsed(true);
		
		setFormatterFactory(new NumberFormatterFactory());
	}
	/**
	*表示する数値を設定します。
	*@param value 数値
	*/
	public void setValue(T value){
		super.setValue(this.value = value);
	}
	/**
	*表示されている数値を返します。
	*@return 数値
	*/
	public T getValue(){
		return value;
	}
	/**フォーマット*/
	private class NumberFormatterFactory extends DefaultFormatterFactory{
	
		public NumberFormatterFactory(){
			super(nf,nf,nf);
		}
	}
}
