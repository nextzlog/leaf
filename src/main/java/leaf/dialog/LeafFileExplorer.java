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
package leaf.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import leaf.dialog.explorer.*;
import leaf.manager.*;

/**
*ファイルエクスプローラ機能を実現するモーダレスなダイアログです。
*表示ファイルを正規表現でフィルタリングする機能を搭載しています。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.1 作成：2010年9月18日
*/

public final class LeafFileExplorer extends LeafDialog{
	
	/**GUI*/
	private JTextField rootfld, extfld;
	private JTree  tree;
	private JList  list;
	private JLabel label;
	private JButton broot, bupdt;
	private JLabel extlb;
	
	private final DefaultTreeModel dirmodel;
	
	private final JFileChooser chooser;
	
	private final ArrayList<FileSelectionListener> listeners;
	private FilenameFilter filter;
	
	private final DateFormat format;
	private String lastmodtext;
	
	private File root, current;
	
	/**
	*親フレームを指定してダイアログを生成します。
	*@param owner 親フレーム
	*/
	public LeafFileExplorer(Frame owner){
		this(owner, null);
	}
	/**
	*親フレームとルートディレクトリを指定してダイアログを生成します。
	*@param owner 親フレーム
	*@param root ルートディレクトリ
	*/
	public LeafFileExplorer(Frame owner, File root){
		super(owner, null, false);
		setLayout(null);
		getContentPane().setPreferredSize(new Dimension(640,310));
		pack();
		setResizable(false);
		
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if(root!=null&&root.exists()){
			this.root = (root.isDirectory())?root:root.getParentFile();
			chooser.setCurrentDirectory(this.root);
		}
		else this.root = chooser.getCurrentDirectory();
		
		dirmodel = new DefaultTreeModel(new DirNode(current = this.root));
		
		format = DateFormat.getDateTimeInstance();
		
		init();
		
		listeners = new ArrayList<FileSelectionListener>(1);
	}
	/**
	*親フレームとデフォルトの拡張子、ルートディレクトリを指定してダイアログを生成します。
	*@param owner 親フレーム
	*@param ext 拡張子の正規表現
	*@param root ルートディレクトリ
	*/
	public LeafFileExplorer(Frame owner, String ext, File root){
		this(owner, root);
		extfld.setText(ext);
	}
	/**
	*FileSelectionListenerを追加します。
	*@param listener 追加するリスナー
	*/
	public void addFileSelectionListener(FileSelectionListener listener){
		listeners.add(listener);
	}
	/**
	*FileSelectionListenerを削除します。
	*@param listener 削除するリスナー
	*/
	public void removeFileSelecitonListener(FileSelectionListener listener){
		listeners.remove(listener);
	}
	/**
	*ダイアログの表示内容を最新の情報に更新します。
	*/
	public void update(){
		if(root!=null&&root.isDirectory()){
			dirmodel.setRoot(new DirNode(root));
			rootfld.setText(root.getPath());
			updateCurrentDirectory();
		}
	}
	/**
	*カレントディレクトリ内の表示を最新の情報に更新します。
	*/
	public void updateCurrentDirectory(){
		filter = new ExFilenameFilter();
		String[] names = current.list(filter);
		if(names==null)names = new String[0];
		list.setListData(names);
		label.setText(lastmodtext+format.format(current.lastModified()));
	}
	/**
	*ルートディレクトリを選択します。
	*/
	private void setRootDirectory(){
		if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
			root = current = chooser.getSelectedFile();
			update();
		}
	}
	/**
	*ダイアログを初期化します。
	*/
	public void init(){
		setTitle(LeafLangManager.get("File Explorer","ファイルエクスプローラ"));
		getContentPane().removeAll();
		
		/*フォルダツリー*/
		tree = new JTree(dirmodel);
		JScrollPane scroll = new JScrollPane(tree);
		scroll.setBounds(5,10,240,300);
		add(scroll);
		tree.addTreeExpansionListener(new ExTreeExpansionListener());
		
		/*ルートディレクトリ*/
		rootfld = new JTextField(current.getAbsolutePath());
		rootfld.setBounds(260,10,330,22);
		add(rootfld);
		
		rootfld.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root = new File(rootfld.getText());
				update();
			}
		});
		
		/*ディレクトリボタン*/
		broot = new JButton("...");
		broot.setBounds(600,10,40,22);
		add(broot);
		
		broot.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setRootDirectory();
			}
		});
		
		/*ファイル情報*/
		lastmodtext = LeafLangManager.get("Last Modified : ","最終更新日時：");
		label = new JLabel((String)null,JLabel.RIGHT);
		label.setBounds(260,36,380,24);
		add(label);
		
		/*ファイルリスト*/
		list = new JList();
		scroll = new JScrollPane(list);
		scroll.setBounds(260,60,380,210);
		add(scroll);
		
		list.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				String value = (String)list.getSelectedValue();
				if(value==null) return;
				File file = new File(current,value);
				label.setText(lastmodtext+format.format(new Date(file.lastModified())));
				if(e.getClickCount() == 2){
					FileSelectionEvent event = new FileSelectionEvent(this,file);
					for(FileSelectionListener listener : listeners){
						listener.fileSelected(event);
					}
				}
			}
		});
		
		/*拡張子*/
		extlb = new JLabel(LeafLangManager.get("Ext.","拡張子"));
		extlb.setBounds(260,280,50,20);
		add(extlb);
		extfld = new JTextField("(txt|text|htm|html|java|c|cpp|h)");
		extfld.setBounds(310,280,220,20);
		add(extfld);
		
		extfld.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateCurrentDirectory();
			}
		});
		
		/*更新ボタン*/
		bupdt = new JButton(LeafLangManager.get("Update","更新(U)"));
		bupdt.setMnemonic(KeyEvent.VK_U);
		bupdt.setBounds(540,278,100,22);
		getRootPane().setDefaultButton(bupdt);
		add(bupdt);
		
		bupdt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateCurrentDirectory();
			}
		});
		
		updateCurrentDirectory();
	}
	/**
	*別のディレクトリへの移動
	*/
	private class ExTreeExpansionListener implements TreeExpansionListener{
		public void treeCollapsed(TreeExpansionEvent e){
			update();
		}
		public void treeExpanded(TreeExpansionEvent e){
			update();
		}
		private void update(){
			TreePath path = tree.getSelectionPath();
			DirNode node = (DirNode)path.getLastPathComponent();
			current = node.getFile();
			updateCurrentDirectory();
		}
	}
	/**
	*カレントディレクトリ内のファイル一覧用のフィルタ
	*/
	private class ExFilenameFilter implements FilenameFilter{
		private final String ext = extfld.getText();
		public boolean accept(File dir, String name){
			File file = new File(dir, name);
			if(file.isDirectory()||file.isHidden()) return false;
			String suf = LeafFileManager.getSuffix(file);
			return (suf!=null&&suf.matches(ext));
		}
	}
	/**
	*フォルダツリー用のフィルタ
	*/
	private final FileFilter DIR_FILTER = new FileFilter(){
		public boolean accept(File file){
			if(file.isHidden()) return false;
			return file.isDirectory();
		}
	};
	/**
	*ツリーに表示されるファイルの実装
	*/
	private class DirNode extends DefaultMutableTreeNode{
		private final File file;
		private final File[] children;
		public DirNode(File file){
			this.file = file;
			children  = file.listFiles(DIR_FILTER);
		}
		/**ファイルを返す*/
		public File getFile(){
			return file;
		}
		/**子ファイル数を返す*/
		public int getChildCount(){
			return (children==null)?0:children.length;
		}
		/**指定された位置の子ファイルを返す*/
		public TreeNode getChildAt(int i){
			return (children==null)?null:new DirNode(children[i]);
		}
		/**文字列表現を返す*/
		public String toString(){
			return file.getName();
		}
		/**子要素を持つかどうか返す*/
		public boolean isLeaf(){
			return !file.isDirectory();
		}
	}
}