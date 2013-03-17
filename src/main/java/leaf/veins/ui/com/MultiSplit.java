/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.veins.ui.com;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 * コンテナの多重分割を実装します。
 * 
 * {@link #createComponent()}を実装することで多重分割機能を提供します。
 * 
 * フォーカスを監視して多重分割を実装するため、
 * フォーカスを取得できないコンポーネントを配置することは推奨されません。
 *
 *
 * @author 東大アマチュア無線クラブ
 *
 * @since 2012/04/30 設計：2011/03/31
 *
 */
public abstract class MultiSplit extends JComponent {
	private static final long serialVersionUID = 1L;
	private final LinkedList<Component> compList;
	private Component focused;
	private final boolean mode;
	
	/**
	 * 自動再描画処理が無効な分割コンテナを生成します。
	 */
	public MultiSplit() {
		this(false);
	}
	
	/**
	 * 自動再描画処理の有無を指定して分割コンテナを生成します。
	 * 
	 * @param mode ディ倍打のドラッグ中の再描画処理の有無
	 */
	public MultiSplit(boolean mode) {
		setLayout(new BorderLayout());
		compList = new LinkedList<Component>();
		add(focused = getNewComponent(), BorderLayout.CENTER);
		this.mode = mode;
	}
	
	/**
	 * 画面分割前の最初に配置されるコンポーネントを指定します。
	 * 
	 * @return 配置するコンポーネント
	 */
	protected abstract Component createFirstComponent();
	
	/**
	 * 画面分割時に新たに配置するコンポーネントを指定します。
	 * 
	 * @return 配置するコンポーネント
	 */
	protected abstract Component createComponent();
	
	/**
	 * createComponentメソッドをラップします。
	 * 
	 * @return 追加するコンポーネント
	 */
	private Component getNewComponent() {
		final Component comp = (compList.size()==0)?
		createFirstComponent() : createComponent();
		FocusListener listener = new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				focused = comp;
			}
		};
		LinkedList<Component> list
		= new LinkedList<Component>();
		list.add(comp);
		while(list.size()>0) {
			Component child = list.get(0);
			list.remove(child);
			if(child.isFocusable()) {
				child.addFocusListener(listener);
			}
			if(child instanceof Container) {
				Component[] comps
				= ((Container)child).getComponents();
				for(int i=0;i<comps.length;i++) {
					list.add(comps[i]);
				}
			}
		}
		compList.add(comp);
		return comp;
	}
	
	/**
	 * 画面分割により追加されたコンポーネントの一覧を返します。
	 * 
	 * @return コンポーネントの配列
	 */
	public Component[] getAddedComponents() {
		return compList.toArray(new Component[0]);
	}
	
	/**
	 *分割対象のコンポーネントを含む親分割コンテナを返します。
	 */
	private JSplitPane getParentSplitPane() {
		Component comp = focused.getParent();
		if(comp instanceof JSplitPane) return (JSplitPane)comp;
		else return null;
	}
	
	/**
	 * 未分割の画面分割コンテナを分割します。
	 * 
	 * @param orient 分割方向
	 */
	private void splitFirst(int orient) {
		Component comp = getNewComponent();
		JSplitPane split = new JSplitPane(orient, mode);
		
		split.setOneTouchExpandable(true);
		split.setLeftComponent(focused);
		split.setRightComponent(comp);
		
		remove(focused);
		add(split, BorderLayout.CENTER);
		
		split.setDividerLocation(
			(((orient == VERTICAL_SPLIT)?
				getHeight() : getWidth())
			- split.getDividerSize()) / 2
		);
	}
	
	/**
	 * 既に分割されている画面分割コンテナを分割します。
	 *
	 * @param orient 分割方向
	 */
	private void splitNext(int orient) {
		Component comp = getNewComponent();
		JSplitPane parent = getParentSplitPane();
		
		int divider = parent.getDividerLocation();
		
		JSplitPane child = new JSplitPane(orient, mode);
		child.setOneTouchExpandable(true);
		
		if(parent.getLeftComponent() == focused) {
			parent.remove(focused);
			parent.setLeftComponent(child);
		} else {
			parent.remove(focused);
			parent.setRightComponent(child);
		}
		parent.setDividerLocation(divider);
		
		child.setLeftComponent(focused);
		child.setRightComponent(comp);
		
		child.setDividerLocation(
			(((orient == VERTICAL_SPLIT)? 
				focused.getHeight() : focused.getWidth())
			- child.getDividerSize()) / 2
		);
	}
	
	/**
	 * コンテナを1段階分割します。
	 * 
	 * @param orient 分割方向
	 */
	public void split(int orient) {
		if(!(focused.getParent() instanceof JSplitPane)) {
			splitFirst(orient);
			validate();
		} else splitNext(orient);
	}
	
	/**
	 * コンテナの分割を1段階解除します。
	 * 
	 * @return 不要になったコンポーネント
	 */
	public Component[] merge() {
		JSplitPane split = getParentSplitPane();
		if(split == null) return new Component[0];
		
		Component maintain = focused, remove;
		if(focused == split.getLeftComponent()) {
			remove =  split.getRightComponent();
		} else{
			remove =  split.getLeftComponent();
		}
		Container cont = split.getParent();
		if(cont == null) return new Component[0];
		
		if(cont instanceof JSplitPane) {
			JSplitPane parent = (JSplitPane)cont;
			int divider = parent.getDividerLocation();
			
			if(split == parent.getLeftComponent()) {
				parent.remove(split);
				parent.setLeftComponent(maintain);
			} else{
				parent.remove(split);
				parent.setRightComponent(maintain);
			}
			parent.setDividerLocation(divider);
		} else{
			setVisible(false);
			remove(split);
			add(maintain, BorderLayout.CENTER);
			setVisible(true);
		}
		return searchComponents(remove);
	}
	
	/**
	* 削除する分割コンテナの子コンポーネントを探索します。
	*
	* @param remove 削除するコンポーネント
	* @return コンポーネントの配列
	*/
	private Component[] searchComponents(Component remove) {
		LinkedList<Component> list
		= new LinkedList<Component>();
		list.add(remove);
		for(int i=0;i<list.size();i++) {
			Component child = list.get(i);
			if(child instanceof JSplitPane) {
				JSplitPane split = (JSplitPane)child;
				list.add(split.getLeftComponent());
				list.add(split.getRightComponent());
				list.remove(split);
				i--;
			} else compList.remove(child);
		}
		return list.toArray(new Component[0]);
	}
	
	/**縦方向の分割です。*/
	public static final int VERTICAL_SPLIT   = JSplitPane.VERTICAL_SPLIT;
	
	/**横方向の分割です。*/
	public static final int HORIZONTAL_SPLIT = JSplitPane.HORIZONTAL_SPLIT;
	
}