/*****************************************************************************
 * Java Class Library 'LeafAPI' since 2010 June 8th
 * Language: Java Standard Edition 7
 *****************************************************************************
 * License : GNU General Public License v3 (see LICENSE.txt)
 * Author: University of Tokyo Amateur Radio Club (JA1ZLO)
*****************************************************************************/
package leaf.shell.build;

import javax.xml.stream.events.Attribute;

/**
 * ビルド文書解析時に各種属性毎の処理を実行します。
 * 
 * @author 東大アマチュア無線クラブ
 * @since Leaf 1.3 作成：2011年12月11日
 */
abstract class AttributeHandler extends Handler<Attribute>{}
