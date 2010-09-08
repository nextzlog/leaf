/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
「LeafCalcDialog」は実装上「月白電卓(2009年3月12日)」の後継に当たります
***************************************************************************************
flgdis:labelの表示値が何を示しているか / flgnum:numに値が入っているか
total:計算の結果数値 / dis:表示値 / memo:メモリ / dnm:分母 / nmr:分子
**************************************************************************************/
package leaf.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import leaf.manager.LeafLangManager;

/**
*四則演算・メモリー機能を持った汎用的な電卓ダイアログです。<br>
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月8日
*/
public class LeafCalcDialog extends LeafDialog implements ActionListener{
	
	/**秘匿フィールド*/
	private final int width=80,height=25;
	private boolean flgdis=DISPLAY_IS_INPUT,flgnum=false;
	private static boolean DISPLAY_IS_INPUT=true,DISPLAY_IS_RESULT=false;
	private double total=0,dis=0,memo=0,dnm=1,nmr=0;
	private String num="0";
	private static final int NON=0,ADD=1,DIF=2,MUL=3,DIV=4;
	private int op= NON;
	/**GUI*/
	private final JLabel label;
	private final JPanel panel;
	private final LeafButton b0,b1,b2,b3,b4,b5,b6,b7,b8,b9,badd,bdif,bmul,bdiv,beql,bac,bcl,bce,bpm,bdot,bcm,brm,bmm,bmp,bsqrt;
	
	/**
	*親フレームを指定して電卓ダイアログを生成します。
	*@param parent 親フレーム
	*/
	public LeafCalcDialog(Frame parent){

		super(parent,LeafLangManager.get("Calculator","電卓"),false);
		this.setSize(408,210);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Container cont = this.getContentPane();
		cont.setLayout(new BorderLayout());
		/*表示部*/
		label = new JLabel("0.0",JLabel.RIGHT);
		label.setPreferredSize(new Dimension(getWidth(),2*height));
		label.setOpaque(true);
		label.setBackground(Color.BLACK);
		label.setForeground(Color.WHITE);
		label.setFont(new Font(Font.MONOSPACED,Font.BOLD,30));
		this.add(label,BorderLayout.NORTH);
		/*入力部*/
		panel = new JPanel(new GridLayout(5,5));
		panel.setBounds(0,2*height+5,5*width,5*height);
		this.add(panel,BorderLayout.CENTER);
		/*ボタン1*/
		bcm = new LeafButton("CM");
		brm = new LeafButton("RM");
		bmm = new LeafButton("M-");
		bmp = new LeafButton("M+");
		bsqrt=new LeafButton("√");
		/*ボタン2*/
		b7  = new LeafButton("7");
		b8  = new LeafButton("8");
		b9  = new LeafButton("9");
		bdiv= new LeafButton("/");
		bac = new LeafButton("AC");
		/*ボタン3*/
		b4  = new LeafButton("4");
		b5  = new LeafButton("5");
		b6  = new LeafButton("6");
		bmul= new LeafButton("*");
		bcl = new LeafButton("C");
		/*ボタン4*/
		b1  = new LeafButton("1");
		b2  = new LeafButton("2");
		b3  = new LeafButton("3");
		bdif= new LeafButton("-");
		bce = new LeafButton("CE");
		/*ボタン5*/
		b0  = new LeafButton("0");
		bdot= new LeafButton(".");
		bpm = new LeafButton("+/-");
		badd= new LeafButton("+");
		beql= new LeafButton("=");
	}
	/**専用のボタン*/
	private class LeafButton extends JButton{
		public LeafButton(String name){
			super(name);
			setFont(new Font(Font.MONOSPACED,Font.PLAIN,20));
			LeafCalcDialog.this.panel.add(this);
			addActionListener(LeafCalcDialog.this);
		}
	}
	/**
	*電卓画面を初期化します。
	*/
	public void init(){
		clearAll();
		setTitle(LeafLangManager.get("Calculator","電卓"));
	}
	/**
	*電卓画面を表示します。<br>
	*他のLeafDialog拡張コンポーネントに倣ったメソッド名になっていますが、
	*電卓ダイアログに限れば、{@link LeafDialog#setVisible(boolean) setVisible(boolean)}でも同様に動作します。
	*/
	public void showDialog(){
		super.setVisible(true);
	}
	public void actionPerformed(ActionEvent e){
		Object source = e.getSource();
		     if(source==b0)input("0");
		else if(source==b1)input("1");
		else if(source==b2)input("2");
		else if(source==b3)input("3");
		else if(source==b4)input("4");
		else if(source==b5)input("5");
		else if(source==b6)input("6");
		else if(source==b7)input("7");
		else if(source==b8)input("8");
		else if(source==b9)input("9");
		else if(source==bdot)input(".");
		else if(source==badd)calculate(op,ADD);
		else if(source==bdif)calculate(op,DIF);
		else if(source==bmul)calculate(op,MUL);
		else if(source==bdiv)calculate(op,DIV);
		else if(source==beql)equal();
		else if(source==bac)clearAll();
		else if(source==bcl)clear();
		else if(source==bce)clearInput();
		else if(source==bpm)alternate();
		else if(source==bcm)clearMemory();
		else if(source==brm)displayMemory();
		else if(source==bmm)minusMemory();
		else if(source==bmp)plusMemory();
		else if(source==bsqrt)sqrt();
		label.setText(String.valueOf(dis));
	}
	/**数字の入力*/
	private void input(String input){
		if(!flgdis)num="0";
		if(input=="."&&num.indexOf(".")!=-1)return;
		num+=input;
		dis=Double.parseDouble(num);
		flgdis=DISPLAY_IS_INPUT;
	}
	/**演算ボタン時*/
	private void calculate(int op,int next){
		if(!flgnum&&num!="0"||op==NON){
			nmr=Double.parseDouble(num);
			dnm=1;
		}else{
			if(op==ADD)nmr+=dnm*Double.parseDouble(num);
			else if(op==DIF){
				if(num=="0"&&next==NON)nmr=-nmr;
				else nmr-=dnm*Double.parseDouble(num);
			}else if(op==MUL){
				if(num=="0"&&next==NON){nmr=Math.pow(nmr,2);dnm=Math.pow(dnm,2);}
				else nmr=nmr*Double.parseDouble(num);
			}else if(op==DIV){
				if(num=="0"&&next==NON){total=nmr;nmr=dnm;dnm=total;}
				else dnm=dnm*Double.parseDouble(num);
			}
		}
		this.op = next;
		total = nmr/dnm;
		dis = total;
		num = "0";
		flgnum=true;
	}
	/**=ボタン時*/
	private void equal(){
		calculate(op,NON);
		num=String.valueOf(total);
		flgdis=DISPLAY_IS_RESULT;
		flgnum=false;
	}
	/**ACボタン時*/
	private void clearAll(){
		clear();
		clearMemory();
	}
	/**Cボタン時*/
	private void clear(){
		num="0";
		flgnum=false;
		nmr=0;
		dnm=1;
		total=0;
		dis=0;
		flgdis=DISPLAY_IS_INPUT;
		op=NON;
	}
	/**CEボタン時*/
	private void clearInput(){
		if(flgdis){
			num="0";
			dis=0;
		}
	}
	/**→ボタン時*/
	private void delete(){
		String str = String.valueOf(dis);
		str=str.substring(0,str.length()-1);
		if(dis==Double.parseDouble(str))dis=(double)((int)(dis/10));
		else dis=Double.parseDouble(str);
		num = String.valueOf(dis);
		flgnum = true;
		flgdis = DISPLAY_IS_INPUT;
	}
	/**+/-ボタン時*/
	private void alternate(){
		dis=-dis;
		if(flgdis)num=String.valueOf(dis);
		else nmr=-nmr;
		flgnum=true;
	}
	/**CMボタン時*/
	private void clearMemory(){
		memo = 0;
	}
	/**RMボタン時*/
	private void displayMemory(){
		num=String.valueOf(dis=memo);
	}
	/**M-ボタン時*/
	private void minusMemory(){
		equal();
		memo-=dis;
	}
	/**M+ボタン時*/
	private void plusMemory(){
		equal();
		memo+=dis;
	}
	/**SQRTボタン時*/
	private void sqrt(){
		if(flgdis)total=Math.sqrt(Double.parseDouble(num));
		else total = Math.sqrt(total);
		flgdis=DISPLAY_IS_RESULT;
		num=String.valueOf(dis=total);
		flgnum=true;
	}
}
