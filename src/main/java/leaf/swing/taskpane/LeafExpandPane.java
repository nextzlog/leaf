/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.swing.taskpane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.Timer;

import leaf.icon.LeafIcons;

/**
 *タスク領域に表示される展開/収納可能なコンテナです。
 *
 *@author 東大アマチュア無線クラブ
 *@since  Leaf 1.0 作成：2010年7月10日
 *@see LeafTaskPane
 */
public abstract class LeafExpandPane extends JComponent {
	private Dimension contentSize;
	private final JComponent content;
	private final JLabel iconLabel, titleLabel, button;
	private final Icon expandicon, foldicon, titleicon;
	private boolean isExpanded = false;
	private final ExpandLayout layout;
	
	private static final int TITLE_HEIGHT = 30;
	
	/**
	 *コンテナが下方向に展開することを示します。
	 *
	 */
	public static final int DROP_DOWN_EXPAND = 0;
	
	/**
	 *コンテナが上方向に展開することを示します。
	 *
	 */
	public static final int DROP_UP_EXPAND   = 1;
	
	/**
	 *タイトル文字列を指定して下方向に展開するコンテナを作成します。
	 *
	 *@param title 表示するタイトル
	 */
	public LeafExpandPane(String title){
		this(title, null, DROP_DOWN_EXPAND);
	}
	
	/**
	 *タイトル文字列とアイコンを指定して下方向に展開するコンテナを作成します。
	 *
	 *@param title 表示するタイトル
	 *@param icon  表示するアイコン
	 */
	public LeafExpandPane(String title, Icon icon){
		this(title, icon, DROP_DOWN_EXPAND);
	}
	
	/**
	 *タイトル文字列とアイコン及び展開方向を指定してコンテナを作成します。
	 *
	 *@param title 表示するタイトル
	 *@param icon  表示するアイコン
	 *@param arrow 展開する方向 {@link #DROP_DOWN_EXPAND} {@link #DROP_UP_EXPAND}
	 */
	public LeafExpandPane(String title, Icon icon, final int arrow){
		super();
		
		if(arrow == DROP_DOWN_EXPAND){
			expandicon = LeafIcons.getIcon("DROP_DOWN");
			foldicon   = LeafIcons.getIcon("DROP_UP");
		}else if(arrow == DROP_UP_EXPAND){
			expandicon = LeafIcons.getIcon("DROP_UP");
			foldicon   = LeafIcons.getIcon("DROP_DOWN");
		}else throw new IllegalArgumentException();
		
		setLayout(layout = new ExpandLayout());
		
		JPanel titlePane = new JPanel(new BorderLayout());
		add(titlePane, BorderLayout.NORTH);
		
		iconLabel  = new GradientLabel(titleicon = icon);
		titleLabel = new GradientLabel(title);
		
		button = new GradientLabel(expandicon);
		button.setPreferredSize(new Dimension(25, 25));
		
		titlePane.add(iconLabel,  BorderLayout.WEST);
		titlePane.add(titleLabel, BorderLayout.CENTER);
		titlePane.add(button,     BorderLayout.EAST);
		titlePane.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
		
		titlePane.addMouseListener(new MouseAdapter(){
			@Override public void mousePressed(MouseEvent e){
				setExpanded(!isExpanded());
			}
		});
		
		this.content = createContent();
		setContentSize(new Dimension(200, 180));
		
		Border outer = BorderFactory.createMatteBorder(0, 2, 2, 2, Color.WHITE);
		Border inner = BorderFactory.createEmptyBorder(2, 0, 2, 0);
		Border inout = BorderFactory.createCompoundBorder(outer, inner);
		
		content.setBorder(inout);
		
		final int height = titlePane.getPreferredSize().height;
		setMaximumSize(new Dimension(Short.MAX_VALUE, height));
	}
	
	/**
	 *グラデーション効果を持ったラベルの共通実装です。
	 *
	 */
	private final class GradientLabel extends JLabel{
		public GradientLabel(String text){
			super(text);
		}
		public GradientLabel(Icon icon){
			super(icon);
		}
		@Override public void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D)g;
			Color bgc = getBackground();
			g2.setPaint(new GradientPaint(
				0, 0, bgc.brighter(),
				0, getHeight(), bgc.darker(), true));
			g2.fillRect(0, 0, getWidth(), getHeight());
			super.paintComponent(g);
		}
	}
	
	/**
	 *コンテナに配置するコンポーネントを生成します。
	 *
	 *@return コンテナに配置するコンポーネント
	 */
	protected abstract JComponent createContent();
	
	/**
	 *コンテナに表示される1行のテキストを設定します。
	 *
	 *@param text コンテナに表示されるテキスト
	 */
	public void setText(String text){
		titleLabel.setText(text);
	}
	
	/**
	 *コンテナに表示されている1行のテキストを返します。
	 *
	 *@return コンテナに表示されているテキスト
	 */
	public String getText(){
		return titleLabel.getText();
	}
	
	/**
	 *コンテナに表示されるグラフィックアイコンを設定します。
	 *
	 *@param icon コンテナに表示されるアイコン
	 */
	public void setIcon(Icon icon){
		iconLabel.setIcon(icon);
	}
	
	/**
	 *コンテナに表示されているグラフィックアイコンを返します。
	 *
	 *@return コンテナに表示されているアイコン
	 */
	public Icon getIcon(){
		return iconLabel.getIcon();
	}
	
	/**
	 *コンテナに配置されるコンポーネントの推奨されるサイズを設定します。
	 *
	 *@param size レイアウトの評価時に用いられる推奨サイズ
	 */
	public void setContentSize(Dimension size){
		final Dimension old = contentSize;
		content.setPreferredSize(contentSize = size);
		firePropertyChange("contentSize", old, size);
	}
	
	/**
	 *コンテナに配置されるコンポーネントの推奨されるサイズを返します。
	 *
	 *@return レイアウトの評価時に用いられる推奨サイズ
	 */
	public Dimension getContentSize(){
		return contentSize;
	}
	
	/**
	 *コンテナが展開され、内部コンポーネントが可視状態であるか返します。
	 *展開収納状態が遷移中である場合は、遷移後の状態を返します。
	 *
	 *@return 展開されている場合true 収納されている場合false
	 */
	public boolean isExpanded(){
		return layout.isExpandingOrFolding()? !isExpanded : isExpanded;
	}
	
	/**
	 *コンテナの展開/収納状態の切り替え動作を開始します。
	 *
	 *@param b 展開する場合true 収納する場合false
	 */
	public void setExpanded(boolean b){
		button.setIcon(b? foldicon : expandicon);
		if(b) add(content, BorderLayout.CENTER);
		layout.setExpanded(isExpanded = b);
	}
	
	/**
	 *展開アニメーションを実現するレイアウトマネージャ
	 *
	 *@since 2011年11月12日
	 */
	private final class ExpandLayout extends BorderLayout {
		private int height = TITLE_HEIGHT, pheight = TITLE_HEIGHT;
		private Timer animator = null;
		private boolean isExpanded;
		private static final int STEP = 30;
		
		public ExpandLayout(){
			super(2, 2);
			this.isExpanded = LeafExpandPane.this.isExpanded;
		}
		
		@Override
		public Dimension preferredLayoutSize(Container target){
			Dimension ps = super.preferredLayoutSize(target);
			pheight = ps.height;
			
			if(animator != null){
				if(isExpanded){
					if(getHeight() > TITLE_HEIGHT) height -= pheight / STEP;
				}else if(getHeight() < pheight)    height += pheight / STEP;
				if(height <= TITLE_HEIGHT || height >= pheight){
					animator.stop();
					animator = null;
					height = (height <= TITLE_HEIGHT) ? TITLE_HEIGHT : pheight;
					if(!(isExpanded = height != TITLE_HEIGHT)) remove(content);
					
					SwingUtilities.invokeLater(new Runnable(){
						@Override public void run(){
							Rectangle rect = content.getBounds();
							content.scrollRectToVisible(rect);
						}
					});
					
					firePropertyChange("expanded", !isExpanded, isExpanded);
					LeafExpandPane.this.fireExpandListeners();
				}
			}
			ps.height = height;
			return ps;
		}
		public void setExpanded(boolean expanded){
			if(isExpanded != expanded && animator == null){
				animator = new Timer(5, new ActionListener(){
					public void actionPerformed(ActionEvent e){
						revalidate();
					}
				});
				animator.start();
			}
		}
		public boolean isExpandingOrFolding(){
			return animator != null;
		}
	}
	
	private void fireExpandListeners(){
		final ExpandEvent e = new ExpandEvent(this);
		for(ExpandListener l : listenerList.getListeners(ExpandListener.class)){
			l.stateChanged(e);
		}
	}
	
	/**
	 *このコンテナにExpandListenerを追加します。
	 *
	 *@param listener 追加するリスナー
	 */
	public void addExpandListener(ExpandListener listener){
		listenerList.add(ExpandListener.class, listener);
	}
	
	/**
	 *このコンテナからExpandListenerを削除します。
	 *
	 *@param listener 削除するリスナー
	 */
	public void removeExpandListener(ExpandListener listener){
		listenerList.remove(ExpandListener.class, listener);
	}
}