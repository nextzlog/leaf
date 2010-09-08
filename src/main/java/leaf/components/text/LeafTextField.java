/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components.text;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.Document;

/**
*ヒントを表示する機能を持ったJTextFieldです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1,0 作成：2010年5月23日
*/
public class LeafTextField extends JTextField implements FocusListener{
	
	/**秘匿フィールド*/
	private String hint = null;
	private Color HINT_COLOR = Color.GRAY.brighter();
	private Color TEXT_COLOR = Color.BLACK;
	
	/**
	*このコンポーネントを生成します。
	*/
	public LeafTextField(){
		super();
		init();
	}
	/**
	*初期に表示される文字列を指定してこのコンポーネントを生成します。
	*@param text 初期の文字列
	*/
	public LeafTextField(String text){
		super(text);
		init();
	}
	/**
	*最大文字数を指定してこのコンポーネントを生成します。
	*@param columns 最大文字数
	*/
	public LeafTextField(int columns){
		super(columns);
		init();
	}
	/**
	*初期に表示される文字列と最大文字数を指定してこのコンポーネントを生成します。
	*@param text 初期の文字列
	*@param columns 最大文字数
	*/
	public LeafTextField(String text,int columns){
		super(text,columns);
		init();
	}
	/**
	*ドキュメントモデルと初期に表示される文字列と文字数を指定してこのコンポーネントを生成します。
	*@param doc ドキュメントモデル
	*@param text 初期の文字列
	*@param columns 文字数
	*/
	public LeafTextField(Document doc,String text,int columns){
		super(doc,text,columns);
		init();
	}
	/**初期化*/
	private void init(){
		addFocusListener(this);
		setSelectionColor(Color.BLACK);
		setSelectedTextColor(Color.WHITE);
	}
	/**
	*ヒントの文字列を設定します。
	*@param hint ヒントとして表示される文字列
	*/
	public void setHintText(String hint){
		this.hint = hint;
		if(super.getText().equals("")&&!isFocusOwner()){
			super.setForeground(HINT_COLOR);
			super.setText(hint);
		}else{
			super.setForeground(TEXT_COLOR);
		}
	}
	/**
	*表示される文字列を設定します。<br>
	*このメソッドはJTextFieldのsetText(String)と同じように使用できます。
	*@param text 表示する文字列
	*/
	public void setText(String text){
		super.setText(text);
		if(!isFocusOwner())setHintText(getHintText());
	}
	/**
	*書き込まれている文字列を返します。
	*@return 書き込まれた文字列
	*/
	public String getText(){
		if(super.getForeground().equals(TEXT_COLOR))return super.getText();
		else return "";
	}
	/**
	*ヒントの文字列を返します。
	*@return ヒント
	*/
	public String getHintText(){
		return hint;
	}
	public void focusGained(final FocusEvent e){
		if(super.getText().equals(hint)&&super.getForeground().equals(HINT_COLOR)){
			super.setForeground(TEXT_COLOR);
			super.setText("");
		}
	}
	public void focusLost(final FocusEvent e){
		if(super.getText().equals("")){
			super.setForeground(HINT_COLOR);
			super.setText(hint);
		}
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
	public void setForeground(Color col){
		TEXT_COLOR = col;
	}
	/**
	*ヒントでない文字列の表示色を返します。
	*@return 文字色
	*/
	public Color getForeground(){
		return TEXT_COLOR;
	}
}
