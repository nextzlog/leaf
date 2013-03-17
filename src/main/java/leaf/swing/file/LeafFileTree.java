/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.file;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;

/**
 * ディレクトリ構造をツリー表示するコンポーネントです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since Leaf 1.3 作成：2011年7月9日
 *
 */
public class LeafFileTree extends JComponent {
	private static final long serialVersionUID = 1L;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel model;
	private final FileSystemView view;
	private final JTree tree;
	private DirectoryHandler handler;
	private TreeCellRenderer renderer;
	
	/**
	 * ファイルツリーを構築します。
	 */
	public LeafFileTree() {
		setLayout(new BorderLayout());
		view  = FileSystemView.getFileSystemView();
		root  = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(root);
		tree = new JTree(model);
		
		handler = new DirectoryHandler(this, view,  model);
		renderer = tree.getCellRenderer();
		renderer = new FileRenderer(this, renderer);
		
		tree.setRootVisible(false);
		tree.setCellRenderer(renderer);
		tree.addTreeSelectionListener(handler);
		
		add(tree, BorderLayout.CENTER);
		update();
	}
	
	/**
	 * FileSelectionListenerを追加します。
	 * 
	 * @param l 追加するリスナー
	 */
	public void addFileSelectionListener(FileSelectionListener l) {
		listenerList.add(FileSelectionListener.class, l);
	}
	
	/**
	 * FileSelectionListenerを削除します。
	 * 
	 * @param l 削除するリスナー
	 */
	public void removeFileSelecitonListener(FileSelectionListener l) {
		listenerList.remove(FileSelectionListener.class, l);
	}
	
	/**
	 * 指定されたファイルが選択されたことをリスナーに通知します。
	 * 
	 * @param file 選択されたファイル
	 */
	protected final void fireFileSelectionEvent(File file) {
		FileSelectionEvent e = new FileSelectionEvent(this, file);
		for(FileSelectionListener l :
		listenerList.getListeners(FileSelectionListener.class)) {
			l.fileSelected(e);
		}
	}
	
	/**
	 * ファイル一覧を取得するために使用するFileSystemViewを返します。
	 * 
	 * @return FileSystemView
	 */
	public FileSystemView getFileSystemView() {
		return view;
	}
	
	/**
	 * ディレクトリの表示を最新のものに更新します。
	 */
	public void update() {
		root.removeAllChildren();
		for(File rootdir : view.getRoots()) {
			addRootDirectory(rootdir);
		}
	}
	
	private void addRootDirectory(File rootdir) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootdir);
		root.add(node);
		for(File file : view.getFiles(rootdir, true)) {
			if(file.isDirectory()) {
				node.add(new DefaultMutableTreeNode(file));
			}
		}
	}

}