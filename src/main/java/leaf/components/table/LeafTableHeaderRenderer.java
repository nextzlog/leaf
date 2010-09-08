/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components.table;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
*描画用コンポーネントを追加できるテーブルヘッダです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年8月1日
*/
public class LeafTableHeaderRenderer implements TableCellRenderer{
	
	private final TableCellRenderer tcr;
	private final JComponent comp;
	
	/**
	*セルレンダラーとヘッダ描画用コンポーネントを指定してヘッダを生成します。
	*@param tcr セルレンダラー
	*@param comp 追加するコンポーネント
	*/
	public LeafTableHeaderRenderer(TableCellRenderer tcr,JComponent comp){
		this.tcr = tcr;
		this.comp= comp;
	}
	/**
	*ヘッダを描画するのに用いるテーブルセルレンダラーを返します。
	*@param table テーブル
	*@param value セルに割り当てる値
	*@param isSelected セル選択時はtrue
	*@param hasFocus フォーカスがある場合true
	*@param row セルの行番号
	*@param column セルの列番号
	*/
	public Component getTableCellRendererComponent(JTable table,
	Object value, boolean isSelected, boolean hasFocus, int row, int column){
		return comp;
	}
}