/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components.system;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
*仮想マシン上で発生した全ての標準出力とエラーメッセージを表示するテキスト領域です。<br>
*{@link #setSystemOut()}メソッドと{@link #setSystemErr()}メソッドで出力先として設定できます。<br>
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年5月22日
*/
public class LeafSystemOutArea extends JTextArea{
	private LeafSystemOutStream out;
	private final ArrayList<SystemOutListener> listeners;
	private boolean type = SystemOutEvent.OUT;
	/**
	*このコンポーネントを生成します。
	*@throws HeaflessException ディスプレイがサポートされていない環境で実行した場合スローされます。 
	*/
	public LeafSystemOutArea() throws HeadlessException{
		super();
		setEditable(false);
		setRows(500);
		setLineWrap(true);
		setWrapStyleWord(true);
		setSelectionColor(Color.BLACK);
		setSelectedTextColor(Color.WHITE);
		out = new LeafSystemOutStream(this);
		listeners = new ArrayList<SystemOutListener>(1);
	}
	/**
	*全ての例外の出力先がこのコンポーネントになるように設定します。<br>
	*このメソッドの実行によって、他のいかなる出力先も無効になります。
	*/
	public void setSystemOut(){
		type = SystemOutEvent.OUT;
		System.setOut(new PrintStream(getOut()));
	}
	/**
	*全てのコンソール出力先がこのコンポーネントになるように設定します。<br>
	*このメソッドの実行によって、他のいかなる出力先も無効になります。
	*/
	public void setSystemErr(){
		type = SystemOutEvent.OUT;
		System.setErr(new PrintStream(getOut()));
	}
	/**
	*標準出力ストリームを返します。
	*@return 出力ストリーム
	*/
	public ByteArrayOutputStream getOut(){
		return out;
	}
	/**
	*出力ストリームに書き出された内容を強制的に表示します。
	*/
	public void flush(){
		append(out.toString());
		out.reset();
		setCaretPosition(getText().length());
	}
	/**
	*SystemOutListenerを登録します。
	*@param lis 登録するリスナー
	*/
	public void addSystemOutListener(SystemOutListener lis){
		listeners.add(lis);
	}
	/**
	*SystemOutListenerを削除します。
	*@param lis 削除するリスナー
	*/
	public void removeSystemOutListener(SystemOutListener lis){
		listeners.remove(lis);
	}
	/**ストリーム*/
	private class LeafSystemOutStream extends ByteArrayOutputStream{
		private final LeafSystemOutArea outarea;
		public LeafSystemOutStream(LeafSystemOutArea outarea){
			super();
			this.outarea = outarea;
		}
		public synchronized void write(byte[] b,int offset,int length){
			for(SystemOutListener listener:listeners){
				listener.printed(new SystemOutEvent(LeafSystemOutArea.this,type));
			}
			super.write(b,offset,length);
			outarea.flush();
		}
		public synchronized void write(int b){
			for(SystemOutListener listener:listeners){
				listener.printed(new SystemOutEvent(LeafSystemOutArea.this,type));	
			}
			super.write(b);
			outarea.flush();
		}
		public void write(byte[] b)throws IOException{
			for(SystemOutListener listener:listeners){
				listener.printed(new SystemOutEvent(LeafSystemOutArea.this,type));
			}
			super.write(b);
			outarea.flush();
		}
	}
}
