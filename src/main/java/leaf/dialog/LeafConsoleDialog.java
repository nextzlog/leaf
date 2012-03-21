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
import java.io.*;
import javax.swing.*;

import leaf.manager.LeafCharsetManager;
import leaf.manager.LeafLocalizeManager;
import leaf.swing.LeafGraphMonitor;
import leaf.swing.menu.LeafMenuBuilder;
import leaf.swing.taskpane.LeafExpandPane;
import leaf.swing.taskpane.LeafTaskPane;
import leaf.swing.text.LeafTextArea;
import leaf.swing.text.LeafTextField;
import leaf.swing.text.LeafTextScrollPane;

import static java.awt.Font.MONOSPACED;
import static java.awt.Font.PLAIN;
import static java.awt.event.KeyEvent.*;
import static leaf.icon.LeafIcons.*;

/**
 *実行環境の全てのコンソール入出力を扱うダイアログです。
 *
 *@author 東大アマチュア無線クラブ
 *@since Leaf 1.0 作成：2010年5月22日
 */
public final class LeafConsoleDialog extends LeafDialog {
	
	private ConsoleArea outarea;
	private LeafTextScrollPane outscroll;
	private LeafExpandPane outexpand, memexpand;
	private LeafGraphMonitor monitor;
	private LeafSearchDialog srchdialog;
	private LeafTextField infield;
	private final ExActionListener listener;
	private final JMenuBar menubar;
	private final LeafTaskPane taskpane;
	private boolean isAssociatedWithConsole = false;
	
	private static String MENUBAR_FILE = "LeafConsoleDialog_menu.xml";
	
	/**
	 *親フレームを指定してコンソールダイアログを構築します。
	 *@param owner ダイアログの親
	 */
	public LeafConsoleDialog(Frame owner) {
		super(owner, false);
		setContentSize(new Dimension(600, 320));
		setResizable(false);
		
		taskpane = new LeafTaskPane();
		add(taskpane, BorderLayout.CENTER);
		
		listener = new ExActionListener();
		setJMenuBar(menubar = new JMenuBar());
		menubar.setBorderPainted(false);
		init();
	}
	/**
	 *親ダイアログを指定してコンソールダイアログを構築します。
	 *@param owner ダイアログの親
	 */
	public LeafConsoleDialog(Dialog owner) {
		super(owner, false);
		setContentSize(new Dimension(600, 320));
		setResizable(false);
		
		taskpane = new LeafTaskPane();
		add(taskpane, BorderLayout.CENTER);
		
		listener = new ExActionListener();
		setJMenuBar(menubar = new JMenuBar());
		menubar.setBorderPainted(false);
		init();
	}
	/**
	 *ダイアログを初期化します。
	 */
	@Override public void init(){
		setTitle(translate("title"));
		taskpane.removeAll();
		
		/*standard out / err / in*/
		outarea = new ConsoleArea();
		infield = new LeafTextField();
		outscroll = new LeafTextScrollPane(outarea);
		outexpand = new LeafExpandPane(
			translate("panel_console")){
			protected JComponent createContent(){
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(outscroll, BorderLayout.CENTER);
				panel.add(infield,   BorderLayout.SOUTH);
				return panel;
			}
		};
		infield.setBackground(Color.BLACK);
		infield.setTextColor(Color.WHITE);
		infield.setHintColor(Color.WHITE);
		infield.setSelectedTextColor(Color.BLACK);
		infield.setSelectionColor(Color.WHITE);
		infield.setHintText("Console Input");
		outexpand.setContentSize(new Dimension(100, 220));
		outexpand.setExpanded(true);
		taskpane.addComp(outexpand);
		
		/*resource monitor*/
		monitor   = new LeafGraphMonitor(500){
			@Override public int sample(){
				Runtime runtime = Runtime.getRuntime();
				long free  = runtime.freeMemory();
				long total = runtime.totalMemory();
				return (int)(500 - 500 * free / total);
			}
		};
		memexpand = new LeafExpandPane(
			translate("panel_resource")){
			protected JComponent createContent(){
				return monitor;
			}
		};
		memexpand.setContentSize(new Dimension(100, 220));
		monitor.setAutoSamplingEnabled(true);
		taskpane.addComp(memexpand);
		
		/*menubar*/
		menubar.removeAll();
		LeafMenuBuilder builder = new LeafMenuBuilder(menubar);
		builder.addActionListener(listener);
		try{
			LeafLocalizeManager localize
			= LeafLocalizeManager.getInstance(getClass());
			builder.build(localize.getResource(MENUBAR_FILE));
		}catch(Exception ex){}
		
		if(isAssociatedWithConsole) setSystemConsole();
		repaint();
		
		srchdialog = new LeafSearchDialog(this);
		srchdialog.setTextComponent(outarea);
	}
	/**
	 *コンソール入出力をこのダイアログに関連付けます。
	 */
	public void setSystemConsole(){
		PrintStream ps = new PrintStream(outarea.stream, true);
		System.setOut(ps);
		System.setErr(ps);
		setSystemIn(infield);
		isAssociatedWithConsole = true;
	}
	/**
	 *テキストフィールドに標準入力を関連付けます。
	 *@param infield 関連付けるテキストフィールド
	 */
	private void setSystemIn(final LeafTextField infield){
		try{
			PipedOutputStream pos = new PipedOutputStream();
			OutputStreamWriter sw = new OutputStreamWriter(pos);
			final BufferedWriter bw = new BufferedWriter(sw);
			System.setIn(new PipedInputStream(pos));
			infield.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try{
						String text = infield.getText();
						bw.write(text, 0, text.length());
						bw.newLine();
						bw.flush();
						infield.setText("");
					}catch(IOException ex){
						ex.printStackTrace();
					}
				}
			});
		}catch(IOException ex){
			ex.printStackTrace();
			infield.setEnabled(false);
		}
	}
	/**
	 *標準出力/エラー出力を表示するコンポーネントです。
	 */
	private class ConsoleArea extends LeafTextArea{
		public final ConsoleOutputStream stream;
		public ConsoleArea(){
			super(100, 100);
			setEditable(false);
			setLineCursorVisible(false);
			stream = new ConsoleOutputStream(this);
			int size = getFont().getSize();
			setFont(new Font(MONOSPACED, PLAIN, size));
		}
		public void moveToEnd(){
			setCaretPosition(getText().length());
		}
	}
	/**
	 *標準出力/エラー出力先ストリームです。
	 */
	private class ConsoleOutputStream extends OutputStream{
		private final ConsoleArea area;
		private final ByteArrayOutputStream stream;
		public ConsoleOutputStream(ConsoleArea area){
			super();
			this.area = area;
			this.stream = new ByteArrayOutputStream();
		}
		@Override
		public synchronized void write(int b){
			stream.write(b);
		}
		@Override
		public synchronized void flush(){
			area.append(stream.toString());
			area.moveToEnd();
			stream.reset();
		}
	}
	/**
	 *メニュー項目が選択されたときに呼び出されます。
	 */
	private class ExActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String cmd = e.getActionCommand();
			if(cmd.equals("file_close")){
				dispose();
			}else if(cmd.equals("edit_copy")){
				outarea.copy();
				outarea.requestFocusInWindow();
			}else if(cmd.equals("edit_select_all")){
				outarea.selectAll();
				outarea.requestFocusInWindow();
			}else if(cmd.equals("edit_search")){
				srchdialog.setVisible(true);
			}
		}
	}
	/**
	 *出力内容をライターに書き込みます。
	 *@param writer 書き込み先ライター
	 *@throws IOException 書き込み例外があった場合
	 */
	private void write(Writer writer) throws IOException{
		BufferedWriter bwriter = null;
		try{
			bwriter  = new BufferedWriter(writer);
			int start = 0, end = 0;
			String text = outarea.getText();
			String ls   = LeafCharsetManager.getLineSeparator();
			while((end = text.indexOf("\n", start)) >= 0){
				bwriter.write(text.substring(start, end) + ls);
				bwriter.flush();
				start = end + 1;
			}
			if(start < text.length())
				bwriter.write(text.substring(start, text.length()));
		}finally{
			if(bwriter != null) bwriter.close();
			if( writer != null) writer.close();
		}
	}
}
