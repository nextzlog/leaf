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
package leaf.components.shell.modes;

import java.io.*;
import java.util.ArrayList;

import leaf.components.shell.*;
import leaf.manager.LeafLangManager;

import leaf.media.LeafMorseToneGenerator;

/**
*シェル内部で実行されるモールス符号モードの実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2010年12月5日
*@see LeafShellPane
*@see LeafMorseToneGenerator
*/
public class MorseMode implements Mode{
	
	private LeafShellDocument doc;
	private LeafMorseToneGenerator generator;
	
	private int freq = 1000, wpm = 5, ratio = 3;
	
	/**
	*モード名を返します。
	*@return モードを表す文字列
	*/
	public String getName(){
		return "morse";
	}
	/**
	*このモードに移行します。
	*@param doc 出力先ドキュメント
	*@param dir 作業ディレクトリ
	*/
	public void activate(LeafShellDocument doc, File dir){
		this.doc = doc;
		try{
			generator = new LeafMorseToneGenerator();
			generator.setToneFrequency(freq);
			generator.setSpeed(wpm);
		}catch(Exception ex){
			generator = null;
			doc.errorln(LeafLangManager.get(
				"Failed in initializing Tone Generator",
				"トーンジェネレータの初期化に失敗しました。"
			));
		}
	}
	/**
	*実装されていません。
	*@param dir 作業ディレクトリ
	*/
	public void setDirectory(File dir){}
	
	/**
	*コマンドを実行します。
	*@param cmd シェルから渡されるコマンド
	*/
	public void execute(String cmd){
		String[] cmds = cmd.split("\\s",2);
		if(cmds.length < 2){
			encode(cmd.toLowerCase());
			return;
		}
		
		if(cmds[0].equals("decode")){
			decode(cmds[1]);
		}else if(cmds[0].equals("encode")){
			encode(cmds[1].toLowerCase());
		}else if(cmds[0].equals("freq")){
			freq(cmds[1]);
		}else if(cmds[0].equals("random")){
			random(cmds[1]);
		}else if(cmds[0].equals("wpm")){
			wpm(cmds[1]);
		}else{
			encode(cmd.toLowerCase());
		}
	}
	/**
	*モールス符号にエンコードします。
	*@param text 元の文
	*/
	private void encode(String text){
		char ch;
		StringBuilder sb = new StringBuilder();
		int length = text.length();
		boolean isCode = false;
		loop:
		for(int i=0;i<length;i++){
			switch(ch = text.charAt(i)){
			case ' ':
			case '　':
				sb.append("   ");
				break;
			case '[':
				isCode = true;
				break;
			case ']':
				isCode = false;
				break;
			default:
				for(int j=0;j<codes.length;j++){
					if(ch==chars[j]){
						sb.append(codes[j] +(isCode?"":" "));
						continue loop;
					}
				}
				doc.errorln(LeafLangManager.translate(
					" => \"[arg]\"is an invalid character",
					" =>「[arg]」は無効な文字です", ch
				));
			}
		}
		doc.appendln(" => " + sb.toString());
		play(sb);
	}
	/**
	*モールス符号をデコードします。
	*@param text モールス文
	*/
	private void decode(String text){
		String[] split = text.split("\\s",0);
		StringBuilder sb = new StringBuilder(split.length);
		loop:
		for(int i=0;i<split.length;i++){
			if(split[i].length()==0) continue;
			for(int j=0;j<codes.length;j++){
				if(split[i].equals(codes[j])){
					sb.append(Character.toString(chars[j]));
					continue loop;
				}
			}
			doc.errorln(LeafLangManager.translate(
				"\"[arg]\" is an invalid code",
				"\"[arg]\"は無効な符号です", split[i]
			));
		}
		doc.appendln(sb.toString());
	}
	/**
	*「freq」コマンドを処理します。
	*@param cmd 周波数
	*/
	private void freq(String cmd){
		if(generator == null)return;
		try{
			this.freq = Integer.parseInt(cmd);
			generator.setToneFrequency(freq);
		}catch(NumberFormatException ex){
			doc.errorln(LeafLangManager.translate(
				"Illegal Number Format : [arg]",
				"数値が不正です : [arg]", cmd
			));
		}
	}
	/**
	*「random」コマンドを処理します。
	*@param cmd テキストの長さ
	*/
	private void random(String cmd){
		String text = createRandomText(cmd, ratio);
		doc.appendln(text);
		encode(text);
	}
	/**
	*「wpm」コマンドを処理します。
	*@param cmd PARIS速度
	*/
	private void wpm(String cmd){
		if(generator == null)return;
		try{
			this.wpm = Integer.parseInt(cmd);
			generator.setSpeed(wpm);
		}catch(NumberFormatException ex){
			doc.errorln(LeafLangManager.translate(
				"Illegal Number Format : [arg]",
				"数値が不正です : [arg]", cmd
			));
		}
	}
	/**
	*モールス符号のトーンを再生します。
	*@param text モールス符号文字列
	*/
	private void play(StringBuilder text){
		if(generator == null)return;
		int length = text.length();
		ArrayList<Byte> list = new ArrayList<Byte>(length * 3);
		list.add(LeafMorseToneGenerator.WHITESPACE);
		for(int i=0;i<length;i++){
			switch(text.charAt(i)){
			case '-':
				list.add(LeafMorseToneGenerator.LONG_DASH);
				list.add(LeafMorseToneGenerator.WHITESPACE);
				break;
			case '.':
				list.add(LeafMorseToneGenerator.SHORT_DOT);
				list.add(LeafMorseToneGenerator.WHITESPACE);
				break;
			default:
				list.add(LeafMorseToneGenerator.WHITESPACE);
				list.add(LeafMorseToneGenerator.WHITESPACE);
				list.add(LeafMorseToneGenerator.WHITESPACE);
			}
		}
		byte[] codes = new byte[list.size()];
		for(int i=0;i<codes.length;i++){
			codes[i] = list.get(i);
		}
		generator.play(codes);
	}
	/**
	*ランダムな文章を生成します。
	*@param cmd 文の長さ
	*@param ratio アルファベットの重率
	*@return 文
	*/
	private String createRandomText(String cmd, int ratio){
		try{
			int size  = Integer.parseInt(cmd) * 4 / 3;
			int alpha = 26 * ratio, total = alpha + 24;
			char[] text = new char[size];
			for(int i=0;i<size;i++){
				if(i%4==3)text[i] = ' ';
				else{
					int num = (int)(Math.random() * total);
					num = (num < alpha)? (num / ratio) : (num - alpha + 26);
					text[i] = chars[num];
				}
			}
			return new String(text);
		}catch(NumberFormatException ex){
			doc.errorln(LeafLangManager.translate(
				"Illegal Number Format : [arg]",
				"数値が不正です : [arg]", cmd
			));
		}
		return "";
	}
	/**
	*モードを終了します。
	*/
	public void exit(){
		if(generator != null){
			generator.close();
			generator = null;
		}
	}
	
	/**
	*モードの文字列表現を返します。
	*/
	public String toString(){
		return getName();
	}
	
	/**
	*欧文モールス符号のテーブル
	*/
	private static final String[] codes = {
		".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..",
		".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.",
		"...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..",
		".-.-.-", "--..--", "---...", "..--..", ".----.", "-....-",
		"-.--.", "-.--.-", "-..-.", "-...-", ".-.-.", ".-..-.", "-..-",
		".--.-.", ".----", "..---", "...--", "....-", ".....",
		"-....", "--...", "---..", "----.", "-----"
	};
	
	/**
	*対応する文字のテーブル
	*/
	private static final char[] chars = {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '.', ',',
		':', '?', '\'', '-', '(', ')', '/', '=', '+', '\"', '*', '@', '1', '2',
		'3', '4', '5', '6', '7', '8', '9', '0'
	};
	
	/**
	*ヘルプとしてメッセージを表示します。
	*@param cmd ヘルプ対象のコマンド
	*/
	public void showHelp(String cmd){
		if(cmd.equals("decode")){
			doc.appendln(LeafLangManager.get(
				"decode : translate morse into alphabets",
				"decode : モールス符号をアルファベットに変換"
			));
		}else if(cmd.equals("encode")){
			doc.appendln(LeafLangManager.get(
				"encode : translate morse into alphabets",
				"encode : アルファベットをモールスに変換"
			));
		}else if(cmd.equals("freq")){
			doc.appendln(LeafLangManager.get(
				"freq   : set tone frequency",
				"freq   : トーンの周波数を設定"
			));
		}else if(cmd.equals("random")){
			doc.appendln(LeafLangManager.get(
				"random : create random morse text",
				"random : ランダムなモールス文を生成"
			));
		}else if(cmd.equals("wpm")){
			doc.appendln(LeafLangManager.get(
				"wpm    : set tone speed",
				"wpm    : トーンの速度を設定"
			));
		}else{
			doc.appendln(LeafLangManager.get(
				"available commands : decode encode freq random wpm",
				"利用できるコマンド : decode encode freq random wpm"
			));
		}
	}
}