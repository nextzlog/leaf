/**************************************************************************************
ライブラリ「LeafAPI」 開発開始：2010年6月8日
開発言語：Pure Java SE 6
開発者：東大アマチュア無線クラブ
***************************************************************************************
License Documents: See the license.txt (under the folder 'readme')
Author: University of Tokyo Amateur Radio Club / License: GPL
**************************************************************************************/
package leaf.demo;

import javax.swing.UIManager;
import java.util.ArrayList;
/**
*デモアプリケーション
*
*@author 東大アマチュア無線クラブ
*@since  Leaf 1.0 作成：2010年7月10日
*/
final class Demo{
	public static void main(String[] args){
		ArrayList<String> options = new ArrayList<String>();
		String filename = null;
		if(args.length > 0){
			for(int i=0;i<args.length;i++){
				if(args[i].startsWith("-")){
					options.add(args[i].substring(1));
				}else{
					filename = args[i];
				}
			}
		}
		if(args.length > 0 || options.size() > 0){
			RunScript.run(filename, options);
		}else{
			setSystemLookAndFeel();
			new GUIDemo();
		}
	}
	/**
	*システムのルックアンドフィールを適用します。
	*/
	private static void setSystemLookAndFeel(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ex){}
	}
}