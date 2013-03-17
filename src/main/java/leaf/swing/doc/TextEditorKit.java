/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.swing.doc;

import javax.swing.text.*;

/**
 * 水平タブや改行を可視化するエディタキットです。
 * 
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf1.4 2012年12月28日
 */
public class TextEditorKit extends StyledEditorKit {
	private static final long serialVersionUID = 1L;
	
	/**
	 * エディタキットを構築します。
	 */
	public TextEditorKit() {
		super();
	}
	
	/**
	 * ビューを作成するファクトリを返します。
	 * 
	 * @return ビューのファクトリ
	 */
	@Override
	public ViewFactory getViewFactory() {
		return new LeafViewFactory();
	}
	
	private final class LeafViewFactory implements ViewFactory {
		@Override
		public View create(Element elem) {
			String kind = elem.getName();
			if(kind != null) {
				if(kind.equals(AbstractDocument.ContentElementName)) {
					return new TabCharacterView(elem);
				} else if(kind.equals(AbstractDocument.ParagraphElementName)) {
					return new LineSeparatorView(elem);
				} else if(kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem,View.Y_AXIS);
				} else if(kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if(kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}
			return new TabCharacterView(elem);
		}
	}

}
