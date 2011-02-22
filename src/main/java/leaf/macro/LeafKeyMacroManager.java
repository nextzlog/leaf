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
package leaf.macro;

import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
/**
*キーボードマクロを手軽に実装するためのユーティリティです。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月12日
*/
public class LeafKeyMacroManager extends KeyAdapter{

	private final Robot robot;
	private ArrayList<Integer> stroke;
	private boolean isTypingStroke = false;
	
	/**
	*マネージャを生成します。
	*/
	public LeafKeyMacroManager() throws AWTException{
		robot = new Robot();
		stroke = new ArrayList<Integer>();
	}
	/**
	*ストロークを読み込む先のXMLファイルを指定してマネージャを生成します。
	*@param file XMLファイル
	*/
	public LeafKeyMacroManager(File file)
		throws AWTException, FileNotFoundException, ArrayIndexOutOfBoundsException{
		robot = new Robot();
		load(file);
	}
	/**
	*指定されたキーを打鍵します。
	*@param keyCode キーに割り当てられたコード
	*@see KeyEvent
	*/
	public void keyPress(int keyCode){
		robot.keyPress(keyCode);
	}
	/**
	*記録されたキーを全消去します。
	*/
	public void clear(){
		stroke.clear();
	}
	/**
	*キーが押されているときに呼び出され、キーストロークを追加記録します。
	*@param event キーのイベント
	*/
	public void keyPressed(KeyEvent event){
		if(!isTypingStroke)
			stroke.add(event.getKeyCode());
	}
	/**
	*記録したストロークを順次打鍵します。
	*/
	public void typeStroke(){
		isTypingStroke = true;
		for(int i=0;i<stroke.size();i++){
			keyPress(stroke.get(i));
		}
		isTypingStroke = false;
	}
	/**
	*記録したストロークを返します。
	*@return ストロークの配列
	*/
	public int[] getStroke(){
		int[] ret = new int[stroke.size()];
		for(int i=0;i<ret.length;i++){
			ret[i] = stroke.get(i);
		}
		return ret;
	}
	/**
	*ストロークを設定します。
	*@param stroke ストロークの配列
	*/
	public void setStroke(int[] stroke){
		this.stroke.clear();
		for(int i=0;i<stroke.length;i++){
			this.stroke.add(stroke[i]);
		}
	}
	/**
	*記録したストロークをファイルに保存します。
	*@param file 保存先のファイル
	*@throws FileNotFoundException ファイルに書き込めない場合
	*/
	public void save(File file) throws FileNotFoundException{
	
		FileOutputStream fstream = null;
		BufferedOutputStream bstream = null;
		XMLEncoder encoder = null;
		
		try{
			fstream = new FileOutputStream(file);
			bstream = new BufferedOutputStream(fstream);
			encoder = new XMLEncoder(bstream);
			encoder.writeObject(new LeafKeyMacro(stroke));
			
		}catch(FileNotFoundException ex){
			throw ex;
		}finally{
			if(encoder!=null)encoder.close();
		}
	}
	/**
	*ファイルからストロークを読み込みます。
	*@param file 読み込む先のファイル
	*@throws FileNotFoundException ファイルが見つからない場合
	*@throws ArrayIndexOutOfBoundsException ストリームにオブジェクトがなかった場合
	*@throws ClassCastException 読み込んだオブジェクトが適切なインスタンスでない場合
	*/
	public void load(File file) 
		throws FileNotFoundException, ArrayIndexOutOfBoundsException, ClassCastException{
		
		FileInputStream fstream = null;
		BufferedInputStream bstream = null;
		XMLDecoder decoder = null;
		
		try{
			fstream = new FileInputStream(file);
			bstream = new BufferedInputStream(fstream);
			decoder = new XMLDecoder(bstream);
			
			try{
				Object obj = decoder.readObject();
				if(obj instanceof LeafKeyMacro){
					stroke = ((LeafKeyMacro)obj).getStroke();
				}else{
					throw new ClassCastException();
				}
			}catch(ArrayIndexOutOfBoundsException ex){
				throw ex;
			}
			
		}catch(FileNotFoundException ex){
			throw ex;
		}finally{
			if(decoder!=null)decoder.close();
		}
	}
}
