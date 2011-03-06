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
package leaf.components.shell;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;

import leaf.components.shell.modes.*;
import leaf.components.text.*;
import leaf.manager.LeafFileManager;
import leaf.manager.LeafLangManager;

/**
*コマンドシェルの実装です。
*
*@author 東大アマチュア無線クラブ
*@since Leaf 1.2 作成：2010年11月28日
*/
public class LeafShellPane extends JPanel{
	
	private Timer animator;
	private boolean isExpanded = true;
	
	private final ArrayList<String> history;
	private int point = 0;
	
	private final ArrayList<Mode> modes;
	private Mode mode;
	
	private final CommandLineMode  cmdline;
	private final PluginLoaderMode loader;
	
	private final LeafShellDocument document;
	
	private LeafTextField input;
	private LeafTextPane  out;
	private LeafTextScrollPane scroll;
	
	private File dir;
	
	/**
	*シェルを生成します。
	*/
	public LeafShellPane(){
		this(null);
	}
	/**
	*作業ディレクトリを指定してシェルを生成します。
	*@param dir 作業ディレクトリ
	*/
	public LeafShellPane(File dir){
		super();
		setLayout(new ExBorderLayout());
		
		history  = new ArrayList<String>();
		modes    = new ArrayList<Mode>();
		
		cmdline  = new CommandLineMode();
		loader   = new PluginLoaderMode(this);
		
		document = new LeafShellDocument();
		document.addDocumentListener(new ExDocumentListener());
		
		setDirectory(dir);
		init();
	}
	/**
	*シェルを初期化します。
	*/
	public void init(){
		
		removeAll();
		
		/*入力部*/
		input = new LeafTextField();
		add(input, BorderLayout.NORTH);
		input.setHintText(LeafLangManager.translate("Shell","シェル"));
		
		input.addActionListener(new ExActionListener());
		input.addKeyListener(new ExKeyAdapter());
		
		/*出力部*/
		out = new LeafTextPane();
		out.setOpaque(true);
		out.setEditable(false);
		out.setEOFVisible(false);
		
		out.setDocument(document);
		
		Font font = out.getFont();
		font = new Font(Font.MONOSPACED, Font.PLAIN, font.getSize());
		out.setFont(font);
		
		scroll = new LeafTextScrollPane(out,true,false);
		scroll.setPreferredSize(new Dimension(200,250));
		add(scroll, BorderLayout.CENTER);
		
		/*モード*/
		addMode(new AriceMode());
		addMode(cmdline);
		addMode(new MorseMode());
		addMode(loader);
		
		/*初期表示*/
		document.append("available modes : ");
		for(Mode m : modes){
			document.append(m.getName() + " ");
		}
		document.append("\n");
		
		setMode(cmdline, null);
	}
	/**
	*作業ディレクトリを返します。
	*@return 作業ディレクトリ
	*/
	public File getDirectory(){
		return dir;
	}
	/**
	*作業ディレクトリを設定します。
	*@param dir 作業ディレクトリ
	*/
	public void setDirectory(File dir){
		if(dir==null || !dir.isDirectory()){
			dir = new File("./");
		}try{
			this.dir = dir.getCanonicalFile();
		}catch(Exception ex){
			this.dir = dir;
		}
	}
	/**
	*シェルにモードを設定します。
	*@param mode 設定するモード
	*@param cmd モードに渡すコマンド
	*/
	public void setMode(Mode mode, String cmd){
		if(this.mode != mode){
			if(this.mode != null){
				this.mode.exit();
			}
			(this.mode = mode).activate(document, dir);
			if(!modes.contains(mode)){
				addMode(mode);
			}
		}
		document.append(mode.getName() + ">");
		execute(cmd);
	}
	/**
	*シェルにモードを追加します。
	*@param mode 追加するモード
	*/
	public void addMode(Mode mode){
		modes.add(mode);
	}
	/**
	*シェルからモードを削除します。
	*@param mode 削除するモード
	*/
	public void removeMode(Mode mode){
		if(this.mode == mode){
			setMode(cmdline, null);
		}
		modes.remove(mode);
	}
	/**
	*指定したファイルからモードをロードします。
	*@param file ロード先JARファイル
	*@throws IOException 読み込みに失敗した場合
	*/
	public void load(File file) throws IOException{
		loader.load(file);
	}
	/**
	*シェルのコマンド入力部のテキストを設定します。
	*@param cmd コマンド
	*/
	protected void setCommand(String cmd){
		input.setText(cmd);
		input.requestFocusInWindow();
	}
	/**
	*コマンドを実行します。
	*@param cmd コマンド
	*/
	protected void execute(final String cmd){
		if(cmd == null)return;
		document.append(cmd + "\n");
		if(process(cmd)){
			document.append(mode.getName() + ">");
			return;
		}
		
		String[] cmds = cmd.split("\\s",2);
		for(Mode mode : modes){
			if(mode.getName().equals(cmds[0])){
				setMode(mode, (cmds.length>=2)?cmds[1]:null);
				return;
			}
		}
		
		new ExSwingWorker(cmd).execute();
	}
	/**
	*内部コマンドを処理します。
	*@param cmd コマンドの文字列
	*@return コマンドが存在して処理された場合true
	*/
	private boolean process(String cmd){
		String[] cmds = cmd.split("\\s",2);
		if(cmds[0].equals("cd")){
			return moveDirectory((cmds.length>=2)?cmds[1]:null);
		}else if(cmds[0].equals("dir")){
			return showDirectory((cmds.length>=2)?cmds[1]:null);
		}else if(cmds[0].equals("cls")){
			return clearScreen();
		}else if(cmds[0].equals("help")){
			mode.showHelp((cmds.length>=2)?cmds[1]:"");
			return true;
		}else{
			return false;
		}
	}
	/**
	*カレントディレクトリ移動コマンドを処理します。
	*@param path ディレクトリ
	*@return コマンドが処理された場合true
	*/
	private boolean moveDirectory(String path){
		if(path==null){
			input.setText("cd " + dir);
			return true;
		}
		dir = toDirectory(path);
		document.append(" => " + dir + "\n");
		
		for(Mode mode : modes){
			mode.setDirectory(dir);
		}
		return true;
	}
	/**
	*ディレクトリ表示コマンドを処理します。
	*@param path ディレクトリ
	*@return コマンドが処理された場合true
	*/
	private boolean showDirectory(String path){
		File file = (path!=null)? toDirectory(path) : dir;
		for(String line : new LeafFileManager().dir(file)){
			document.append(line + "\n");
		}
		return true;
	}
	/**
	*スクリーンを消去します。
	*@return コマンドが処理された場合true
	*/
	private boolean clearScreen(){
		document.clear();
		return true;
	}
	/**
	*パスをディレクトリに変換します。
	*@param path パス
	*/
	private File toDirectory(String path){
		File file = new File(path);
		file = (file.isDirectory())? file : new File(dir, path);
		return (file.isDirectory())? file.getAbsoluteFile() : dir;
	}
	/**
	*SwingWorker : コマンドを専用スレッドで実行します。
	*/
	private class ExSwingWorker extends SwingWorker{
		private final String cmd;
		public ExSwingWorker(String cmd){
			this.cmd  = cmd;
		}
		public String doInBackground(){
			input.setEnabled(false);
			mode.execute(cmd);
			return "Done";
		}
		public void done(){
			input.setEnabled(true);
			input.requestFocusInWindow();
			document.append(mode.getName() + ">");
		}
	}
	/**
	*ActionListener：コマンドを実行します。
	*/
	private class ExActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String cmd = input.getText();
			if(cmd.length()>0){
				history.add(cmd);
				point = history.size();
				input.setText("");
				execute(cmd);
			}
		}
	}
	/**
	*KeyListener：履歴を実装します。
	*/
	private class ExKeyAdapter extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			int code = e.getKeyCode();
			if(code == KeyEvent.VK_UP){
				if(point > 0) input.setText(history.get(point-=1));
			}else if(code == KeyEvent.VK_DOWN){
				if(point < history.size()-1) input.setText(history.get(point+=1));
			}
		}
	}
	/**
	*DocumentListener : 出力を監視します。
	*/
	private class ExDocumentListener implements DocumentListener{
		public void insertUpdate(DocumentEvent e){
			out.setCaretPosition(document.getLength());
		}
		public void removeUpdate(DocumentEvent e){}
		public void changedUpdate(DocumentEvent e){}
	}
	/**
	*専用のレイアウトマネージャ
	*/
	private class ExBorderLayout extends BorderLayout{
		private int height = 0, pheight = 0;
		/**コンストラクタ*/
		public ExBorderLayout(){
			super(2, 2);
		}
		/**
		*推奨されるサイズを返します。
		*/
		public Dimension preferredLayoutSize(Container target){
			Dimension ps = super.preferredLayoutSize(target);
			pheight = ps.height;
			if(animator != null){
				if(isExpanded){
					if(getHeight() > 0) height -= 5;
				}else{
					if(getHeight() < pheight) height += 5;
				}
				if(height <= 0){
					height = 0;
					animator.stop();
				}else if(height >= pheight){
					height = pheight;
					animator.stop();
					animator = null;
					input.requestFocusInWindow();
				}
			}
			ps.height = height;
			return ps;
		}
	}
	/**
	*コマンドシェルを展開/収納します。
	*@param visible 展開する場合true
	*/
	public void expand(boolean visible){
		if(animator != null && animator.isRunning()) return;
		isExpanded = getHeight() != 0;
		animator = new javax.swing.Timer(5, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				revalidate();
			}
		});
		animator.start();
	}
	/**
	*コマンドシェルが展開されているか返します。
	*@return 展開されている場合true
	*/
	public boolean isExpanded(){
		return isExpanded;
	}
	/**
	*フォントを設定します。
	*@param font フォント
	*/
	public void setFont(Font font){
		super.setFont(font);
		if(out != null)out.setFont(font);
	}
	/**
	*フォントを返します。
	*@return フォント
	*/
	public Font getFont(){
		if(out != null)return out.getFont();
		return super.getFont();
	}
}
