/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.icon;

import java.awt.*;
import javax.swing.*;

/**
*閉じるアイコンの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*/
public class LeafCloseIcon implements Icon{
	private final int width = 16;
	private final int height= 16;
	public void paintIcon(Component comp,Graphics g,int x,int y){
		g.translate(x,y);//原点指定
		g.setColor(Color.BLACK);
		g.drawLine(4,4,11,11);
		g.drawLine(4,5,10,11);
		g.drawLine(5,4,11,10);
		g.drawLine(11,4,4,11);
		g.drawLine(11,5,5,11);
		g.drawLine(10,4,4,10);
		g.translate(-x,-y);
	}
	public int getIconWidth(){
		return width;
	}
	public int getIconHeight(){
		return height;
	}
}