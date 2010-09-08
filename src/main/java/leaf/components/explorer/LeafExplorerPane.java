/**************************************************************************************
月白プロジェクト Java 拡張ライブラリ 開発コードネーム「Leaf」
始動：2010年6月8日
バージョン：Edition 1.0
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ2010年度新入生 川勝孝也
***************************************************************************************
「Leaf」は「月白エディタ」1.2以降及び「Jazlog(ZLOG3.0)」用に開発されたライブラリです
**************************************************************************************/
package leaf.components.explorer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.ArrayList;

/**
*エクスプローラツリーの実装です。
*@author 東大アマチュア無線クラブ
*@since Leaf 1.0 作成：2010年3月25日
*/
public class LeafExplorerPane extends JScrollPane{
	
	private final JTree tree;
	private final DefaultTreeModel model;
	private final ArrayList<FileSelectionListener> listeners;
	/**
	*ルートディレクトリを指定してエクスプローラを生成します。
	*@param folder ルートディレクトリ
	*/
	public LeafExplorerPane(File folder){
		super();
		listeners = new ArrayList<FileSelectionListener>(1);
		model = new DefaultTreeModel(new FolderTreeNode(folder));
		tree = new JTree(model);
		setViewportView(tree);
	}
	
	/**
	*ファイル選択を受け付けるリスナーを登録します。
	*@param listener 登録するFileSelectionListener
	*/
	public void addFileSelectionListener(FileSelectionListener listener){
		listeners.add(listener);
	}
	
	/**
	*登録されたFileSelectionListenerを削除します。
	*@param listener 削除するFileSelectionListener
	*/
	public void removeFileSelectionListener(FileSelectionListener listener){
		listeners.remove(listener);
	}
	
	/**マウスクリックで開く*/
	private class LeafMouseAdapter extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			if(e.getClickCount()==2
			&&SwingUtilities.isLeftMouseButton(e)){
				FolderTreeNode node =
				(FolderTreeNode)tree.getLastSelectedPathComponent();
				if(node==null||!node.isLeaf())return;
				for(FileSelectionListener listener: listeners){
					listener.fileSelected(node.getFile());
				}
			}
		}
	}
	
	/**
	*ルートディレクトリを設定します
	*@param folder ルートディレクトリ
	*/
	public void setRootDirectory(File folder){
		model.setRoot(new FolderTreeNode(folder));
	}
	
	/**フォルダ階層ツリーの実装*/
	private class FolderTreeNode extends DefaultMutableTreeNode{
		
		private final File file;
		private final File[] children;
		
		/**コンストラクタ*/
		public FolderTreeNode(File file){
			this.file = file;
			if(file.isDirectory()){
				children = file.listFiles();
			}else{
				children = null;
			}
		}
		
		/**子要素数を返す*/
		public int getChildCount(){
			return (children==null)?0:children.length;
		}
		
		/**インデックスを指定して子要素を取得*/
		public TreeNode getChildAt(int i){
			return (children==null)?null:new FolderTreeNode(children[i]);
		}
		
		/**要素の文字列表現を返す*/
		public String toString(){
			return file.getName();
		}
		
		/**要素のファイルを返す*/
		public File getFile(){
			return file;
		}
	}
}