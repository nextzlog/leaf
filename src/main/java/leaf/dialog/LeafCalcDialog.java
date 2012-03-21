/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.math.BigDecimal;

import static java.awt.event.KeyEvent.*;
import static java.math.RoundingMode.HALF_EVEN;

/**
 *32桁四則演算・メモリー機能を持つ汎用の電卓ダイアログです。
 *内部的に10進数で計算するため基数変換の誤差は発生しません。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年5月8日
 */
public final class LeafCalcDialog extends LeafDialog{
	/**フィールド*/
	private static final int BUTTON_WIDTH=80, BUTTON_HEIGHT=25;
	private static final boolean DISPLAY_IS_INPUT  = true;
	private static final boolean DISPLAY_IS_RESULT = false;
	private static final int OP_NON = 0;
	private static final int OP_ADD = 1;
	private static final int OP_SUB = 2;
	private static final int OP_MUL = 3;
	private static final int OP_DIV = 4;
	private BigDecimal memo, dnm, nmr;
	private boolean display;
	private String input;
	private int op;
	/**コンポーネント*/
	private JLabel label;
	private final Font font_button, font_big, font_small;
	private ExInputListener listener;
	
	/**
	*親ダイアログを指定して電卓ダイアログを生成します。
	*@param owner 親ダイアログ
	*/
	public LeafCalcDialog(Dialog owner){
		super(owner, false);
		
		setContentSize(new Dimension(5 * BUTTON_WIDTH, 7 * BUTTON_HEIGHT));
		setResizable(false);
		
		font_button = new Font(Font.MONOSPACED,Font.PLAIN,18);
		font_small = new Font(Font.SERIF,Font.BOLD,24);
		font_big  = new Font(Font.SERIF,Font.BOLD,30);
		
		setLayout(new BorderLayout());
		createContents();
		init();
	}
	/**
	*親フレームを指定して電卓ダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafCalcDialog(Frame owner){
		super(owner, false);
		
		setContentSize(new Dimension(5 * BUTTON_WIDTH, 7 * BUTTON_HEIGHT));
		setResizable(false);
		
		font_button = new Font(Font.MONOSPACED,Font.PLAIN,18);
		font_small = new Font(Font.SERIF,Font.BOLD,24);
		font_big  = new Font(Font.SERIF,Font.BOLD,30);
		
		setLayout(new BorderLayout());
		createContents();
		init();
	}
	/**
	*電卓ダイアログのコンポーネントを配置します。
	*/
	private void createContents(){
		getContentPane().removeAll();
		
		/*表示部*/
		label = new LeafLabel();
		label.setPreferredSize(
			new Dimension(getWidth(),2 * BUTTON_HEIGHT));
		label.setOpaque(true);
		label.setBackground(Color.BLACK);
		label.setForeground(Color.WHITE);
		add(label,BorderLayout.NORTH);
		
		/*入力部*/
		JPanel panel = new JPanel(new GridLayout(5, 5));
		add(panel,BorderLayout.CENTER);
		
		/*入力リスナー*/
		listener = new ExInputListener();
		addKeyListener(listener);
		
		/*ボタン1*/
		panel.add(new LeafButton("CM"));
		panel.add(new LeafButton("RM"));
		panel.add(new LeafButton("M-"));
		panel.add(new LeafButton("M+"));
		panel.add(new LeafButton("√"));
		/*ボタン2*/
		panel.add(new LeafButton("7"));
		panel.add(new LeafButton("8"));
		panel.add(new LeafButton("9"));
		panel.add(new LeafButton("/"));
		panel.add(new LeafButton("AC"));
		/*ボタン3*/
		panel.add(new LeafButton("4"));
		panel.add(new LeafButton("5"));
		panel.add(new LeafButton("6"));
		panel.add(new LeafButton("*"));
		panel.add(new LeafButton("C"));
		/*ボタン4*/
		panel.add(new LeafButton("1"));
		panel.add(new LeafButton("2"));
		panel.add(new LeafButton("3"));
		panel.add(new LeafButton("-"));
		panel.add(new LeafButton("CE"));
		/*ボタン5*/
		panel.add(new LeafButton("0"));
		panel.add(new LeafButton("."));
		panel.add(new LeafButton("+/-"));
		panel.add(new LeafButton("+"));
		panel.add(new LeafButton("="));
		
		clearAll();
	}
	/**専用のラベル*/
	private class LeafLabel extends JLabel{
		public LeafLabel(){
			super("", JLabel.RIGHT);
		}
		public void setText(String text){
			if(text!=null){
				if(text.length() > 24)
					setFont(font_small);
				else
					setFont(font_big);
			}
			super.setText(text);
		}
	}
	/**専用のボタン*/
	private class LeafButton extends JButton{
		public LeafButton(String name){
			super(name);
			setFont(font_button);
			setFocusable(false);
			setActionCommand(name);
			addActionListener(listener);
		}
	}
	/**
	*電卓画面の表示を初期化します。
	*/
	@Override public void init(){
		clearAll();
		setTitle(translate("title"));
	}
	/**
	*電卓画面を表示します。
	*/
	public void showDialog(){
		super.setVisible(true);
	}
	/**
	*キー入力とボタン入力を処理します。
	*/
	private class ExInputListener extends KeyAdapter
	implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String cmd = e.getActionCommand();
			if(cmd.matches("[0-9.]"))input(cmd.charAt(0));
			else if(cmd.equals("+"))calculate(OP_ADD);
			else if(cmd.equals("-"))calculate(OP_SUB);
			else if(cmd.equals("*"))calculate(OP_MUL);
			else if(cmd.equals("/"))calculate(OP_DIV);
			else if(cmd.equals("="))calculate(OP_NON);
			else if(cmd.equals("AC"))clearAll();
			else if(cmd.equals("C") )clear();
			else if(cmd.equals("CE"))clearInput();
			else if(cmd.equals("+/-"))negate();
			else if(cmd.equals("CM"))clearMemory();
			else if(cmd.equals("RM"))displayMemory();
			else if(cmd.equals("M+"))addToMemory();
			else if(cmd.equals("M-"))subtractFromMemory();
			else sqrt();
		}
		public void keyPressed(KeyEvent e){
			char ch  = e.getKeyChar();
			int code = e.getKeyCode();
			if(ch>='0'&&ch<='9'||ch=='.')input(ch);
			else if(ch=='+')calculate(OP_ADD);
			else if(ch=='-')calculate(OP_SUB);
			else if(ch=='*')calculate(OP_MUL);
			else if(ch=='/')calculate(OP_DIV);
			else if(code==VK_BACK_SPACE)backspace();
			else if(code==VK_DELETE)clear();
			else if(code==VK_ENTER )calculate(OP_NON);
			else if(code==VK_ESCAPE)dispose();
			else if(ch=='p')pi();
			else if(ch=='e')e();
		}
	}
	/**
	*数字を1桁入力します。
	*@param ch 入力する数字
	*/
	private void input(char ch){
		if(display==DISPLAY_IS_RESULT) input="";
		if(!(ch=='.' && input.indexOf('.')>0)){
			if(input.isEmpty() && ch=='.')input = "0";
			input = input.concat(Character.toString(ch));
			display = DISPLAY_IS_INPUT;
			label.setText(input);
		}
	}
	/**
	*数字を1桁削除します。
	*/
	private void backspace(){
		if(display==DISPLAY_IS_INPUT&&input.length()>1){
			if(input.charAt(input.length()-1)=='.')
				input = input.substring(0,input.length()-2);
			else
				input = input.substring(0,input.length()-1);
			label.setText(input.isEmpty()? "0" : input);
		}else{
			input = "";
			label.setText("0");
		}
		display = DISPLAY_IS_INPUT;
	}
	/**
	*演算子を入力します。
	*@param op 演算子
	*/
	private void calculate(int op){
		if(!input.isEmpty()){ //通常の演算
			BigDecimal decimal = new BigDecimal(input);
			switch(this.op){
			case OP_ADD:
				nmr = nmr.add(dnm.multiply(decimal));
				break;
			case OP_SUB:
				nmr = nmr.subtract(dnm.multiply(decimal));
				break;
			case OP_MUL:
				nmr = nmr.multiply(decimal);
				break;
			case OP_DIV:
				dnm = dnm.multiply(decimal);
				break;
			default:
				nmr = decimal;
				dnm = BigDecimal.ONE;
			}
		}else{ //機能演算
			switch(this.op){
			case OP_SUB:
				nmr = nmr.negate();
				break;
			case OP_MUL:
				nmr = nmr.pow(2);
				dnm = dnm.pow(2);
				break;
			case OP_DIV:
				BigDecimal tmp = nmr;
				nmr = dnm;
				dnm = tmp;
			}
		}
		if((this.op=op)==OP_NON)
			display = DISPLAY_IS_RESULT;
		input = "";
		display();
	}
	/**
	*演算結果を表示します。
	*/
	private void display(){
		if(dnm.compareTo(BigDecimal.ZERO)!=0){
			BigDecimal val = nmr.divide(dnm,32,HALF_EVEN);
			String text = val.toPlainString();
			if(text.indexOf('.')>0){
				int i=text.length()-1;
				while(text.charAt(--i)=='0');
				if(text.charAt(i)=='.')i--;
				label.setText(text.substring(0, i+1));
			}else label.setText(text);
		}else{
			label.setText("E");
			input = "";
		}
	}
	/**
	*オールクリアします。
	*/
	private void clearAll(){
		clear();
		clearMemory();
	}
	/**
	*計算内容をクリアします。
	*/
	private void clear(){
		nmr = BigDecimal.ZERO;
		dnm = BigDecimal.ONE;
		label.setText(nmr.toString());
		display = DISPLAY_IS_INPUT;
		op = OP_NON;
		input = "";
	}
	/**
	*入力をクリアします。
	*/
	private void clearInput(){
		if(display == DISPLAY_IS_INPUT){
			input = "";
			label.setText("0");
		}
	}
	/**
	*符号を入れ替えます。
	*/
	private void negate(){
		if(display == DISPLAY_IS_INPUT){
			if(input.isEmpty()) return;
			BigDecimal dec = new BigDecimal(input);
			input = dec.negate().toPlainString();
			label.setText(input);
		}else{
			nmr = nmr.negate();
			display();
		}
	}
	/**
	*メモリの内容を消去します。
	*/
	private void clearMemory(){
		memo = BigDecimal.ZERO;
	}
	/**
	*メモリの内容を表示します。
	*/
	private void displayMemory(){
		display = DISPLAY_IS_RESULT;
		input = memo.toPlainString();
		label.setText(input);
	}
	/**
	*メモリに加算します。
	*/
	private void addToMemory(){
		try{
			memo = memo.add(new BigDecimal(label.getText()));
		}catch(Exception ex){}
	}
	/**
	*メモリから減算します。
	*/
	private void subtractFromMemory(){
		try{
			memo = memo.subtract(new BigDecimal(label.getText()));
		}catch(Exception ex){}
	}
	/**
	*平方根を計算します。
	*/
	private void sqrt(){
		try{
			double input = Double.parseDouble(label.getText());
			BigDecimal a = BigDecimal.valueOf(input);
			BigDecimal x = BigDecimal.valueOf(Math.sqrt(input));
			BigDecimal bd_2 = new BigDecimal(2);
			for(int scale=16;scale<=32;scale<<=1){
				x = x.subtract(x.pow(2).subtract(a).divide(
					x.multiply(bd_2), scale, HALF_EVEN
				));
			}
			label.setText(this.input=x.toPlainString());
			display = DISPLAY_IS_INPUT;
		}catch(Exception ex){}
	}
	/**
	*円周率を24桁入力します。
	*/
	private void pi(){
		label.setText(input="3.1415926535897932384626433832795");
		display = DISPLAY_IS_RESULT;
	}
	/**
	*ネイピア数を24桁入力します。
	*/
	private void e(){
		label.setText(input="2.7182818284590452353602874713527");
		display = DISPLAY_IS_RESULT;
	}
}