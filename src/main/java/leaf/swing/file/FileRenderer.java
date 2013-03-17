/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.file;

import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * {@link LeafFileTree}のファイル表示に用いるレンダラーです。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2013/02/17
 *
 */
final class FileRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	private final TreeCellRenderer renderer;
	private final FileSystemView view;
	
	/**
	 * LeafFileTreeとセルレンダラーを指定してレンダラーを構築します。
	 * 
	 * @param tree ファイルツリー
	 * @param r セルレンダラー
	 */
	public FileRenderer(LeafFileTree tree, TreeCellRenderer r) {
		this.renderer = r;
		this.view = tree.getFileSystemView();
	}
	
	@Override
	public Component getTreeCellRendererComponent
	(JTree tree, Object value, boolean isSelected, boolean isExpanded,
	boolean isLeaf, int row, boolean hasFocus) {
		JLabel comp = (JLabel) renderer.getTreeCellRendererComponent
		(tree, value, isSelected, isExpanded, isLeaf, row, hasFocus);
		if(isSelected) {
			comp.setOpaque(false);
			comp.setForeground(getTextSelectionColor());
		} else{
			comp.setOpaque(true);
			comp.setForeground(getTextNonSelectionColor());
			comp.setBackground(getBackgroundNonSelectionColor());
		}
		
		if(value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();
			if(obj instanceof File) {
				File file = (File) obj;
				comp.setIcon(view.getSystemIcon(file));
				comp.setText(view.getSystemDisplayName(file));
				comp.setToolTipText(file.getPath());
			}
		}
		return comp;
	}

}