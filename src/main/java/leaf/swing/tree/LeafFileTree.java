/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.tree;

import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;

/**
 *ディレクトリ構造をツリー表示するコンポーネントです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.3 作成：2011年7月9日
 */
public class LeafFileTree extends JComponent{
	private final LinkedList<FileSelectionListener> listeners;
	private final FileSystemView view;
	private JTree tree;
	/**
	 *ファイルツリーを構築します。
	 */
	public LeafFileTree(){
		super();
		setLayout(new BorderLayout());
		view = FileSystemView.getFileSystemView();
		listeners = new LinkedList<FileSelectionListener>();
		init();
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
	 *コンポーネントを初期化します。
	 */
	public void init(){
		removeAll();
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		DefaultTreeModel model = new DefaultTreeModel(root);
		for(File rootdir : view.getRoots()){
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootdir);
			root.add(node);
			for(File file : view.getFiles(rootdir, true)){
				if(file.isDirectory()){
					node.add(new DefaultMutableTreeNode(file));
				}
			}
		}
		tree = new JTree(model);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(new DirListener());
		tree.setCellRenderer(new FileRenderer(tree.getCellRenderer()));
		
		JScrollPane scroll = new JScrollPane(tree);
		scroll.setBounds(5, 10, 240, 300);
		add(scroll, BorderLayout.CENTER);
	}
	/**
	 *フォルダを展開するタスクを実行するリスナー
	 */
	private class DirListener implements TreeSelectionListener{
		private DefaultTreeModel model;
		private DefaultMutableTreeNode node;
		/**選択時*/
		public void valueChanged(TreeSelectionEvent e){
			node = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
			model = (DefaultTreeModel)tree.getModel();
			final File file = (File)node.getUserObject();
			if(node.isLeaf() && file.isDirectory()){
				new ExSwingWorker(file).execute();
			}
			FileSelectionEvent event = new FileSelectionEvent(this, file);
			for(FileSelectionListener listener : listeners){
				listener.fileSelected(event);
			}
		}
		/**展開タスクを実行するワーカー*/
		private class ExSwingWorker extends SwingWorker<String, File>{
			private final File parent;
			public ExSwingWorker(File parent){
				this.parent = parent;
			}
			public String doInBackground(){
				File[] children = view.getFiles(parent, true);
				for(File child : children){
					if(child.isDirectory() && !child.isHidden()){
						publish(child);
					}
				}
				return "Done";
			}
			protected void process(java.util.List<File> chunks){
				for(File file : chunks){
					node.add(new DefaultMutableTreeNode(file));
				}
				model.nodeStructureChanged(node);
			}
		}
	}
	/**
	 *FileSystemViewからアイコンを取得するセルレンダラー
	 */
	private class FileRenderer extends DefaultTreeCellRenderer{
		private final TreeCellRenderer renderer;
		public FileRenderer(TreeCellRenderer renderer){
			this.renderer = renderer;
		}
		/**セルのコンポーネントを返す*/
		public Component getTreeCellRendererComponent
		(JTree tree, Object value, boolean isSelected, boolean isExpanded,
		boolean isLeaf, int row, boolean hasFocus){
			JLabel comp = (JLabel)renderer.getTreeCellRendererComponent
			(tree, value, isSelected, isExpanded, isLeaf, row, hasFocus);
			if(isSelected){
				comp.setOpaque(false);
				comp.setForeground(getTextSelectionColor());
			}else{
				comp.setOpaque(true);
				comp.setForeground(getTextNonSelectionColor());
				comp.setBackground(getBackgroundNonSelectionColor());
			}
			if(value instanceof DefaultMutableTreeNode){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
				Object obj = node.getUserObject();
				if(obj instanceof File){
					File file = (File)obj;
					comp.setIcon(view.getSystemIcon(file));
					comp.setText(view.getSystemDisplayName(file));
					comp.setToolTipText(file.getPath());
				}
			}
			return comp;
		}
	}
}