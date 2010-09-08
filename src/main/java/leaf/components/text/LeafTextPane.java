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
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.font.TextAttribute;

import leaf.document.*;

/**
*改行記号や水平タブ、[EOF]、行カーソルの表示機能を持つJTextPaneです。<br>
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*/

public class LeafTextPane extends JTextPane{
	
	/**フィールド*/
	private DefaultCaret caret;
	private int tabsize = 8;
	private boolean eofvisible = false,linecursorvisible = false;
	private Color cursorcolor = Color.BLUE;
	
	/**
	*テキスト領域を生成します。
	*/
	public LeafTextPane(){
		super();
		init();
	}
	/**
	*ドキュメントモデルを指定してテキスト領域を生成します。
	*@param doc デフォルトのドキュメント
	*/
	public LeafTextPane(StyledDocument doc){
		super(doc);
		init();
	}
	/**初期化*/
	private void init(){
		setOpaque(false);
		setEditorKit(new LeafEditorKit());
		caret = new LeafCaret();
		caret.setBlinkRate(getCaret().getBlinkRate());
		setCaret(caret);
		setSelectionColor(Color.BLACK);
		setSelectedTextColor(Color.WHITE);
		setTabSize(tabsize);
	}
	/**
	*カーソル行強調と[EOF]表示のために実装されます。
	*/
	protected void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		if(isLineCursorVisible()){
			Insets insets = getInsets();
			int cy = caret.y+caret.height-1;
			g2.setPaint(cursorcolor);
			g2.drawLine(insets.left,cy,getSize().width-insets.left-insets.right,cy);
		}
		super.paintComponent(g);
		if(isEOFVisible()){
			try{
				g2.setPaint(Color.BLACK);
				Rectangle rect = modelToView(getDocument().getLength());
				FontMetrics met = g2.getFontMetrics();
				g2.fillRect(rect.x+2,rect.y,met.stringWidth("[EOF]")+1,met.getHeight());
				g2.setPaint(Color.WHITE);
				g2.drawString("[EOF]",rect.x+3,rect.y+met.getHeight()-2);
			}catch(Exception ex){}
		}
	}
	/**独自のキャレット*/
	private class LeafCaret extends DefaultCaret{
		public void paint(Graphics g){
			if(isVisible()){
				try{
					javax.swing.plaf.TextUI ui = getUI();
					Rectangle r = ui.modelToView(LeafTextPane.this,getDot());
					g.setColor(getCaretColor());
					g.fillRect(r.x,r.y,2,r.height);
				}catch(Exception ex){}
			}
		}
		protected synchronized void damage(Rectangle r){
			if(r!=null){
				x = 0;
				y = r.y;
				width = LeafTextPane.this.getSize().width;
				height= r.height;
				LeafTextPane.this.repaint();
			}
		}
	}
	/**行を折り返さないためにオーバーライドされます。*/
	public boolean getScrollableTracksViewportWidth(){
		Component p = getParent();
		if(p==null)return true;
		int ewidth = getUI().getPreferredSize(this).width;
		return ewidth<=p.getSize().width;
	}
	/**
	*タブサイズ分の半角空白文字列を返します。
	*@return タブサイズ分の空白文字列
	*/
	public String getTabSizeWhiteSpace(){
		String space="";
		for(int i=0;i<tabsize;i++){
			space+=" ";
		}
		return space;
	}
	/**
	*タブサイズを返します。
	*@return タブの展開文字数
	*/
	public int getTabSize(){
		return tabsize;
	}
	/**
	*タブサイズを設定します。
	*@param len タブの展開文字数
	*/
	public void setTabSize(int len){
		tabsize = len;
		FontMetrics fm = getFontMetrics(getFont());
		int tabsize = fm.charWidth('m')*len;
		TabStop[] tabs = new TabStop[25];
		for(int i=0;i<tabs.length;i++){
			tabs[i] = new TabStop((i+1)*tabsize);
		}
		TabSet tabset = new TabSet(tabs);
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setTabSet(attr,tabset);
		getStyledDocument().setParagraphAttributes(0,getDocument().getLength(),attr,false);
	}
	/**
	*このテキスト領域の末尾に文字列を追加します。
	*@param str 追加する文字列
	*/
	public void append(String str){
		setText(getText()+str);
		setCaretPosition(getText().length());
	}
	/**
	*[EOF]の可視を設定します。
	*@param visible [EOF]表示の場合true
	*/
	public void setEOFVisible(boolean visible){
		eofvisible = visible;
	}
	/**
	*[EOF]が可視かどうか返します。
	*@return [EOF]表示の場合true
	*/
	public boolean isEOFVisible(){
		return eofvisible;
	}
	/**
	*行カーソルの可視を設定します。
	*@param visible 行カーソル表示の場合true
	*/
	public void setLineCursorVisible(boolean visible){
		linecursorvisible = visible;
	}
	/**
	*行カーソルが可視かどうか返します。
	*@return 行カーソル表示の場合true
	*/
	public boolean isLineCursorVisible(){
		return linecursorvisible;
	}
	/**
	*行カーソルの表示色を設定します。
	*@param color 行カーソルの色
	*/
	public void setLineCursorColor(Color color){
		cursorcolor = color;
	}
	/**
	*行カーソルの表示色を返します。
	*@return 行カーソルの色
	*/
	public Color getLineCursorColor(){
		return cursorcolor;
	}
	/**改行コードの差を吸収するためにオーバーライドされます。*/
	public String getText(){
		return super.getText().replaceAll("\r\n","\n").replaceAll("\r","\n");
	}
	/**ドキュメントの変更時にタブサイズの変更を防止するためにオーバーライドされます。*/
	public void setDocument(Document doc){
		super.setDocument(doc);
		setTabSize(getTabSize());
	}
}
