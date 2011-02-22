/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.1
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ 川勝孝也
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
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
*四則演算・メモリー機能を持った汎用的な電卓ダイアログです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月8日
*/
public final class LeafCalcDialog extends LeafDialog{
	
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
	private final LeafButton b0,b1,b2,b3,b4,b5,b6,b7,b8,b9,badd,bdif,bmul,bdiv,
	beql,bac,bcl,bce,bpm,bdot,bcm,brm,bmm,bmp,bsqrt;
	
	private final ExInputListener listener;
	
	/**
	*親フレームを指定して電卓ダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafCalcDialog(Frame owner){

		super(owner,LeafLangManager.get("Calculator","電卓"),false);
		getContentPane().setPreferredSize(new Dimension(5*width,7*height));
		pack();
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
		
		/*表示部*/
		label = new JLabel("0.0",JLabel.RIGHT);
		label.setPreferredSize(new Dimension(getWidth(),2*height));
		label.setOpaque(true);
		label.setBackground(Color.BLACK);
		label.setForeground(Color.WHITE);
		label.setFont(new Font(Font.SERIF,Font.BOLD,30));
		add(label,BorderLayout.NORTH);
		
		/*入力部*/
		panel = new JPanel(new GridLayout(5,5));
		panel.setBounds(0,2*height,5*width,5*height);
		add(panel,BorderLayout.CENTER);
		
		/*入力リスナー*/
		listener = new ExInputListener();
		addKeyListener(listener);
		
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
			panel.add(this);
			addActionListener(listener);
			setFocusable(false);
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
	*電卓画面を表示します。
	*/
	public void showDialog(){
		super.setVisible(true);
	}
	private class ExInputListener extends KeyAdapter implements ActionListener{
		
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
		public void keyPressed(KeyEvent e){
			char ch  = e.getKeyChar();
			int code = e.getKeyCode();
			if(Character.isDigit(ch)||ch=='.')input(Character.toString(ch));
			else if(ch=='+')calculate(op,ADD);
			else if(ch=='-')calculate(op,DIF);
			else if(ch=='*')calculate(op,MUL);
			else if(ch=='/')calculate(op,DIV);
			else if(code==e.VK_BACK_SPACE)backspace();
			else if(code==e.VK_DELETE)clear();
			else if(code==e.VK_ENTER)equal();
			else if(code==e.VK_ESCAPE)dispose();
			else if(ch=='p')pi();
			else if(ch=='e')e();
			label.setText(String.valueOf(dis));
		}
	}
	/**数字の入力*/
	private void input(String input){
		if(flgdis==DISPLAY_IS_RESULT)num="0";
		if(input=="."&&num.indexOf(".")!=-1)return;
		num += input;
		dis = Double.parseDouble(num);
		flgdis=DISPLAY_IS_INPUT;
	}
	/**１文字削除*/
	private void backspace(){
		if(flgdis==DISPLAY_IS_INPUT&&num.length()>1){
			num = num.substring(0,num.length()-1);
			dis = Double.parseDouble(num);
			flgnum = true;
			flgdis = DISPLAY_IS_INPUT;
		}
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
	/**+/-ボタン時*/
	private void alternate(){
		dis=-dis;
		if(flgdis==DISPLAY_IS_INPUT){
			num=String.valueOf(dis);
			flgnum=true;
		}else nmr=-nmr;
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
	/**円周率入力*/
	private void pi(){
		flgdis = DISPLAY_IS_RESULT;
		input(String.valueOf(Math.PI));
	}
	/**自然対数の底入力*/
	private void e(){
		flgdis = DISPLAY_IS_RESULT;
		input(String.valueOf(Math.E));
	}
}
