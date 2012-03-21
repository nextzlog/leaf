/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.text;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.Document;

/**
*ヒント文字列を表示する機能を持つテキストフィールドです。
*
*@author 東大アマチュア無線クラブ
*@since  Leaf 1.0 作成：2010年5月23日
*/
public class LeafTextField extends JTextField{
	private Color HINT_COLOR = Color.GRAY;
	private Color TEXT_COLOR = getForeground();
	private boolean isShowingHint = false;
	private String hint;
	/**
	*テキストフィールドを生成します。
	*/
	public LeafTextField(){
		super();
		init();
	}
	/**
	*初期文字列を指定してテキストフィールドを生成します。
	*@param text 初期文字列
	*/
	public LeafTextField(String text){
		super(text);
		init();
	}
	/**
	*列数を指定してテキストフィールドを生成します。
	*@param cols 幅を計算するための列数
	*/
	public LeafTextField(int cols){
		super(cols);
		init();
	}
	/**
	*初期文字列と列数を指定してテキストフィールドを生成します。
	*@param text 初期文字列
	*@param cols 幅を計算するための列数
	*/
	public LeafTextField(String text, int cols){
		super(text, cols);
		init();
	}
	/**
	*ドキュメントと初期文字列、列数を指定してテキストフィールドを生成します。
	*@param doc ドキュメント
	*@param text 初期文字列
	*@param cols 幅を計算するための列数
	*/
	public LeafTextField(Document doc, String text, int cols){
		super(doc, text, cols);
		init();
	}
	/**
	*初期化します。
	*/
	private void init(){
		setSelectionColor(getForeground());
		setSelectedTextColor(getBackground());
		isShowingHint = getText().isEmpty();
		addFocusListener(new ExFocusListener());
	}
	/**
	*フォーカスリスナー
	*/
	private class ExFocusListener implements FocusListener{
		public void focusGained(FocusEvent e){
			if(isShowingHint){
				isShowingHint = false;
				setForeground(TEXT_COLOR);
				LeafTextField.super.setText("");
			}
		}
		public void focusLost(FocusEvent e){
			if(getText().isEmpty()){
				isShowingHint = true;
				setForeground(HINT_COLOR);
				LeafTextField.super.setText(hint);
			}
		}
	}
	/**
	*ヒント文字列を設定します。
	*@param hint ヒントとして表示される文字列
	*/
	public void setHintText(String hint){
		this.hint = hint;
		if(hint!=null&&!hint.isEmpty()){
			if(isShowingHint){
				setForeground(HINT_COLOR);
				super.setText(hint);
			}else setForeground(TEXT_COLOR);
		}
	}
	/**
	*ヒント文字列を返します。
	*@return ヒントとして表示される文字列
	*/
	public String getHintText(){
		return hint;
	}
	/**
	*表示される文字列を設定します。
	*@param text 表示する文字列
	*/
	public void setText(String text){
		super.setText(text);
		isShowingHint &= text.isEmpty();
		setHintText(hint);
	}
	/**
	*書き込まれている文字列を返します。
	*@return 書き込まれた文字列
	*/
	public String getText(){
		return isShowingHint? "" : super.getText();
	}
	/**
	*ヒントの表示色を設定します。
	*@param col ヒントを表示する色
	*/
	public void setHintColor(Color col){
		HINT_COLOR = col;
	}
	/**
	*ヒントの表示色を取得します。
	*@return ヒントを表示する色
	*/
	public Color getHintColor(){
		return HINT_COLOR;
	}
	/**
	*ヒントでない文字列の表示色を設定します。
	*@param col 文字色
	*/
	public void setTextColor(Color col){
		TEXT_COLOR = col;
	}
	/**
	*ヒントでない文字列の表示色を返します。
	*@return 文字色
	*/
	public Color getTextColor(){
		return TEXT_COLOR;
	}
}